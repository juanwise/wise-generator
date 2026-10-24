#!/usr/bin/env python3
"""Imports Rayman's Smash Flash 2 costumes from images cut out by hand (one per costume).

Rayman's sprite is made of floating parts (head, gloves, shoes, a potion), so his costume sheet cannot be
split automatically like the other characters. Cut each costume out of the sheet into its own image
(1.png = first costume, 2.png, ...), with the sheet's plain light-grey background, and run:

    python3 build/importRaymanCostumes.py <folder with 1.png .. N.png>      (from the repo root)

The background is made transparent and pieces of the neighbouring costume left at the edges are removed.
Writes the same files as importSmashFlash2Renders.py for the `rayman` fighter.
"""
import json, re, sys
from pathlib import Path

import numpy as np
from PIL import Image
from scipy import ndimage as ndi

sys.path.insert(0, str(Path(__file__).parent))
import importSmashFlash2Renders as ssf2

BACKGROUND = np.array([242, 242, 242])
MIN_EDGE_PIECE = 400   # a piece touching the image edge smaller than this is a sliver of the neighbouring costume


def cutout(path):
    a = np.array(Image.open(path).convert("RGBA"))
    diff = np.abs(a[:, :, :3].astype(int) - BACKGROUND).max(axis=2)
    bg_lab, _ = ndi.label(diff <= 4)
    border = set(np.unique(np.concatenate([bg_lab[0], bg_lab[-1], bg_lab[:, 0], bg_lab[:, -1]]))) - {0}
    fg = ~np.isin(bg_lab, list(border))
    pocket_lab, n = ndi.label((diff <= 1) & fg)           # background trapped between fingers etc.
    for k in range(1, n + 1):
        if (pocket_lab == k).sum() >= 6:
            fg[pocket_lab == k] = False
    lab, n = ndi.label(fg, structure=np.ones((3, 3)))
    h, w = fg.shape
    keep = np.zeros_like(fg)
    for k in range(1, n + 1):
        ys, xs = np.where(lab == k)
        touches_edge = ys.min() == 0 or xs.min() == 0 or ys.max() == h - 1 or xs.max() == w - 1
        if len(ys) < 12 or (touches_edge and len(ys) < MIN_EDGE_PIECE):
            continue
        keep |= lab == k
    # remove the light fringe left by anti-aliasing against the grey background
    for _ in range(2):
        edge = keep & ~ndi.binary_erosion(keep, structure=ndi.generate_binary_structure(2, 1))
        keep &= ~(edge & (diff <= 40))
    a[:, :, 3] = np.where(keep, 255, 0)
    lab, n = ndi.label(keep, structure=np.ones((3, 3)))
    head = int(np.argmax(ndi.sum(keep, lab, range(1, n + 1)))) + 1      # largest piece = head and body
    hy, hx = np.where(lab == head)
    return Image.fromarray(a), (hx.min(), hy.min())


def main(folder):
    files = sorted(Path(folder).glob("*.png"), key=lambda f: int(re.sub(r"\D", "", f.stem) or 0))
    cut = [cutout(f) for f in files]
    # Each costume was cut with slightly different margins (and some without the small potion at the bottom),
    # so line them all up by their head before sharing one canvas.
    ax, ay = max(h[0] for _, h in cut), max(h[1] for _, h in cut)
    width = max(ax - h[0] + im.width for im, h in cut)
    height = max(ay - h[1] + im.height for im, h in cut)
    crops = []
    for im, (hx, hy) in cut:
        canvas = Image.new("RGBA", (width, height), (0, 0, 0, 0))
        canvas.alpha_composite(im, (ax - hx, ay - hy))
        crops.append(canvas)
    box = Image.new("L", (width, height))
    for c in crops:
        box.paste(255, mask=c.getchannel("A"))
    crops = [c.crop(box.getbbox()) for c in crops]
    placement = ssf2.write_character("rayman", crops, aligned=True)
    settings = json.loads(ssf2.SETTINGS.read_text(encoding="utf-8"))
    for fighter in settings["fighters"]:
        if fighter["fighter"] == "rayman":
            fighter.update(placement)
    ssf2.SETTINGS.write_text(json.dumps(settings, indent=2) + "\n", encoding="utf-8")
    print(f"rayman={len(crops)} {placement}")


if __name__ == "__main__":
    main(sys.argv[1])
