#!/usr/bin/env python3
"""Imports Smash Flash 2 costume sheets (one image per character with every costume) as thumbnail art.

Source (juanwise/Renders-and-Cosmetics): "smash flash 2 renders/SSF2_<Character>_Costumes.jpg"
(the files are WebP images with a transparent background; costumes are laid out in a grid).

Writes
    assets/characters/ssbf2/artwork/<url_name>/<alt>.png   one enlarged PNG per costume (+ costumes.md)
    src/main/resources/icons/ssbf2/<url_name>/<alt>.png    64x64 icons shown when picking a character
    settings/thumbnails/images/ssbf2/default.json          offset/scale that frame each fighter in its panel
Alt numbers follow reading order on the sheet (left to right, top to bottom); alt 1 is the first costume.

Usage (from the repo root):  python3 build/importSmashFlash2Renders.py <path-to-Renders-and-Cosmetics>
Requires Pillow, numpy and scipy.
"""
import json, re, shutil, statistics, sys
from pathlib import Path

import numpy as np
from PIL import Image
from scipy import ndimage as ndi

ART_OUT = Path("assets/characters/ssbf2/artwork")
ICON_OUT = Path("src/main/resources/icons/ssbf2")
SETTINGS = Path("settings/thumbnails/images/ssbf2/default.json")

SHEET_TO_CODE = {"Chibi-Robo": "chibi_robo", "PAC-MAN": "pac_man", "Mr._Game_%26_Watch": "mr_game_and_watch"}
THRESHOLDS = (24, 100, 200, 250)   # alpha cut-offs tried to separate costumes that touch each other
TARGET_HEIGHT = 480                # costume images are enlarged so the tallest one is this high
PAD = 12                           # transparent margin so portraits never touch the image edge

# Thumbnail framing (same idea as importProjectMRenders.py): each player slot is 640x720 and the
# header/bottom bars hide the top and bottom, so fighters are fitted into the visible band.
VIS_TOP, VIS_BOTTOM, CENTER_X, MAX_W, MAX_UPSCALE = 130, 600, 320, 590, 1.7


def code_for(sheet_name):
    return SHEET_TO_CODE.get(sheet_name, sheet_name.lower().replace("-", "_"))


def label_sprites(alpha, thr):
    mask = alpha > thr
    lab, n = ndi.label(mask, structure=np.ones((3, 3)))
    areas = np.array(ndi.sum(mask, lab, range(1, n + 1))) if n else np.array([])
    return lab, areas


def big_ids(areas):
    ref = statistics.median(sorted(areas)[-max(4, len(areas) // 3):])
    return [i + 1 for i, a in enumerate(areas) if a > 0.25 * ref]


def split_merged(lab, ids, per_comp):
    """Cut components that hold several touching costumes into `per_comp` slices at the narrowest columns."""
    out = np.zeros_like(lab)
    nxt = 1
    for k in ids:
        region = lab == k
        ys, xs = np.where(region)
        x0, x1 = xs.min(), xs.max() + 1
        profile = ndi.uniform_filter1d(region.sum(axis=0).astype(float), 5)
        pitch = (x1 - x0) / per_comp
        cuts = [x0]
        for i in range(1, per_comp):
            ideal = int(round(x0 + i * pitch))
            lo, hi = int(ideal - 0.35 * pitch), int(ideal + 0.35 * pitch)
            cuts.append(lo + int(np.argmin(profile[lo:hi])))
        cuts.append(x1)
        for a, b in zip(cuts, cuts[1:]):
            piece = np.zeros_like(region)
            piece[:, a:b] = region[:, a:b]
            out[piece] = nxt
            nxt += 1
    return out


def split_stacked(lab, ids):
    """Cut tall components that hold two costumes stacked on top of each other, at the narrowest row near the middle."""
    out = np.zeros_like(lab)
    nxt = 1
    for k in ids:
        region = lab == k
        ys, xs = np.where(region)
        y0, y1 = ys.min(), ys.max() + 1
        profile = ndi.uniform_filter1d(region.sum(axis=1).astype(float), 5)
        mid, span = (y0 + y1) / 2, 0.25 * (y1 - y0)
        lo, hi = int(mid - span), int(mid + span)
        cut = lo + int(np.argmin(profile[lo:hi]))
        for a, b in ((y0, cut), (cut, y1)):
            piece = np.zeros_like(region)
            piece[a:b, :] = region[a:b, :]
            out[piece] = nxt
            nxt += 1
    return out


def sprite_labels(alpha):
    """Returns a label image with one label per costume (1..n)."""
    best = None
    for thr in THRESHOLDS:
        lab, areas = label_sprites(alpha, thr)
        if not len(areas):
            continue
        ids = big_ids(areas)
        sel = areas[[i - 1 for i in ids]]
        if len(ids) >= 6 and sel.max() / sel.min() <= 1.3 and (best is None or len(ids) > len(best[1])):
            best = (lab, ids, thr)
    if best is None:
        lab, areas = label_sprites(alpha, THRESHOLDS[0])
        ids = big_ids(areas)
        if len(ids) in (1, 2, 3, 4):           # costumes touching each other: assume 12 per sheet
            lab = split_merged(lab, ids, 12 // len(ids))
            ids = list(range(1, int(lab.max()) + 1))
            return lab, ids
        # Costumes made of floating parts (e.g. Rayman): join parts that are close together.
        grown = ndi.binary_dilation(alpha > THRESHOLDS[0], structure=np.ones((3, 3)), iterations=11)
        glab, n = ndi.label(grown, structure=np.ones((3, 3)))
        lab = np.where(alpha > THRESHOLDS[0], glab, 0)
        areas = np.array(ndi.sum(lab > 0, lab, range(1, n + 1)))
        return lab, big_ids(areas)
    lab, ids = best[0], best[1]
    heights = []
    for k in ids:
        ys = np.where((lab == k).any(axis=1))[0]
        heights.append(ys.max() + 1 - ys.min())
    if len(ids) == 6 and min(heights) >= 0.85 * alpha.shape[0]:   # each component is a column of two costumes
        lab = split_stacked(lab, ids)
        ids = list(range(1, int(lab.max()) + 1))
    return lab, ids


def costumes(path):
    """Yields RGBA arrays, one per costume, in reading order."""
    img = np.array(Image.open(path).convert("RGBA"))
    alpha = img[:, :, 3]
    lab, ids = sprite_labels(alpha)
    keep = np.isin(lab, ids)
    # every non-transparent pixel (also faint edges and small loose parts) goes to the nearest costume
    _, (iy, ix) = ndi.distance_transform_edt(~keep, return_indices=True)
    owner = np.where(alpha > 0, lab[iy, ix], 0)
    boxes = []
    for k in ids:
        ys, xs = np.where((owner == k) & (alpha > 0))
        boxes.append((k, xs.min(), ys.min(), xs.max() + 1, ys.max() + 1))
    hmed = statistics.median(b[4] - b[2] for b in boxes)
    boxes.sort(key=lambda b: (b[2] + b[4]) / 2)
    rows, cur = [], [boxes[0]]
    for b in boxes[1:]:
        if abs((b[2] + b[4]) / 2 - statistics.mean((c[2] + c[4]) / 2 for c in cur)) < hmed * 0.5:
            cur.append(b)
        else:
            rows.append(cur)
            cur = [b]
    rows.append(cur)
    for row in rows:
        for k, x0, y0, x1, y1 in sorted(row, key=lambda b: b[1]):
            crop = img[y0:y1, x0:x1].copy()
            crop[owner[y0:y1, x0:x1] != k, 3] = 0
            yield Image.fromarray(crop), (len(rows), len(row))


def java_resize_dims(w, h, scale):
    """Size produced by the program's ImageService.resizeImage for a given 'scale' setting."""
    target = max(int(scale * w), int(scale * h))
    if h < w:
        return round(w * target / h), target
    return target, round(h * target / w)


def frame(images):
    boxes = []
    for p in images:
        a = np.array(p)[:, :, 3]
        ys, xs = np.where(a > 40)
        boxes.append((xs.min(), ys.min(), xs.max() + 1, ys.max() + 1))
    x0, y0, x1, y1 = (float(statistics.median(b[i] for b in boxes)) for i in range(4))
    w, h = images[0].size
    bw, bh = x1 - x0, y1 - y0
    e = min((VIS_BOTTOM - VIS_TOP) / bh, MAX_W / bw, MAX_UPSCALE)
    scale = round(e * min(w, h) / max(w, h), 3)
    nw, nh = java_resize_dims(w, h, scale)
    e = nw / w
    ox = CENTER_X - (x0 + bw / 2) * e
    oy = (VIS_TOP + VIS_BOTTOM) / 2 - (y0 + bh / 2) * e
    return {"offset": [int(round(ox)), int(round(oy))], "scale": scale}


def main(src):
    sheets = sorted((Path(src) / "smash flash 2 renders").glob("SSF2_*_Costumes.jpg"))
    counts, frames, index = {}, {}, ["# Smash Flash 2 costumes (alt number = position on the source sheet, left to right, top to bottom)\n"]
    for sheet in sheets:
        name = sheet.name[len("SSF2_"):-len("_Costumes.jpg")]
        code = code_for(name)
        crops, layout = zip(*costumes(sheet))
        if len(crops) < 4:
            print(f"SKIPPED {name}: could not separate its costumes ({len(crops)} found)")
            continue
        cw, ch = max(c.width for c in crops), max(c.height for c in crops)
        f = TARGET_HEIGHT / ch
        out_w, out_h = round(cw * f) + 2 * PAD, round(ch * f) + 2 * PAD
        for out in (ART_OUT / code, ICON_OUT / code):
            if out.exists():
                shutil.rmtree(out)
            out.mkdir(parents=True)
        finals = []
        for i, c in enumerate(crops, 1):
            big = c.resize((round(c.width * f), round(c.height * f)), Image.LANCZOS)
            canvas = Image.new("RGBA", (out_w, out_h), (0, 0, 0, 0))
            canvas.alpha_composite(big, ((out_w - big.width) // 2, out_h - PAD - big.height))   # bottom-centred
            canvas.save(ART_OUT / code / f"{i}.png", optimize=True)
            finals.append(canvas)
            icon = Image.new("RGBA", (64, 64), (0, 0, 0, 0))
            small = c.copy()
            small.thumbnail((60, 60), Image.LANCZOS)
            icon.alpha_composite(small, ((64 - small.width) // 2, (64 - small.height) // 2))
            icon.save(ICON_OUT / code / f"{i}.png")
        counts[code] = len(crops)
        frames[code] = frame(finals)
        index.append(f"- {name.replace('_%26_', ' & ').replace('_', ' ')} (`{code}`): {len(crops)} costumes")
    (ART_OUT / "costumes.md").write_text("\n".join(index) + "\n", encoding="utf-8")

    settings = json.loads(SETTINGS.read_text(encoding="utf-8"))
    known = {f["fighter"] for f in settings["fighters"]}
    for code in frames:                      # fighters on a sheet but missing from the settings file (e.g. King Dedede)
        if code not in known:
            settings["fighters"].insert(len(settings["fighters"]) - 1, {"fighter": code, "offset": [0, 0], "scale": 0.7, "flip": False})
    for fighter in settings["fighters"]:
        if fighter["fighter"] in frames:
            fighter.update(frames[fighter["fighter"]])
    SETTINGS.write_text(json.dumps(settings, indent=2) + "\n", encoding="utf-8")
    for code, n in counts.items():
        print(f"{code}={n} {frames[code]}")


if __name__ == "__main__":
    main(sys.argv[1])
