#!/usr/bin/env python3
"""Imports Project Plus (Project M) character images, icons and thumbnail placement.

Source layout (juanwise/Renders-and-Cosmetics):
    <Character> (P+)/CSPs/HD/<costume>_CSP_HD.png   -> thumbnail art
    <Character> (P+)/STCs/HD/<costume>_STC_HD.png   -> selector icon

Writes
    assets/characters/ssbpm/artwork/<url_name>/<alt>.png     (+ costumes.md: alt -> source costume)
    src/main/resources/icons/ssbpm/<url_name>/<alt>.png      (64x64 icons shown when picking a character)
    settings/thumbnails/images/ssbpm/default.json            (offset/scale that frame each fighter in its panel)

Alt numbers: "Standard" costumes first, then the others, each group in natural name order.
Usage: python3 build/importProjectMRenders.py <path-to-Renders-and-Cosmetics>   (run from the repo root)
Requires Pillow and numpy.
"""
import json, re, shutil, statistics, sys
from pathlib import Path

import numpy as np
from PIL import Image

ART_OUT = Path("assets/characters/ssbpm/artwork")
ICON_OUT = Path("src/main/resources/icons/ssbpm")
SETTINGS = Path("settings/thumbnails/images/ssbpm/default.json")

FOLDER_TO_CODE = {
    "Captain Falcon": "captain_falcon", "Dedede": "king_dedede", "Diddy Kong": "diddy_kong",
    "Donkey Kong": "donkey_kong", "Ice Climbers": "ice_climbers", "Meta Knight": "meta_knight",
    "Mr. Game and Watch": "mr_game_and_watch", "ROB": "rob", "Toon Link": "toon_link",
    "Zero Suit Samus": "zero_suit_samus",
}

# Thumbnail framing: each player slot is 640x720; the header bar covers the top and the bottom
# bar the lower part, so fighters are fitted into the visible band between them.
VIS_TOP, VIS_BOTTOM, CENTER_X, MAX_W = 130, 600, 320, 590
UNDER_BAR_Y = 640      # a portrait that is cut off at the bottom is anchored so the cut hides under the bar
MAX_UPSCALE = 1.7


def code_for(folder):
    return FOLDER_TO_CODE.get(folder, folder.lower().replace(" ", "_"))


def natural(s):
    return [int(t) if t.isdigit() else t.lower() for t in re.split(r"(\d+)", s)]


def norm(s):
    s = re.sub(r"(_CSP|_STC)?(_HD)?$", "", s.lower())
    s = re.sub(r"(\d)s$", r"\1", s)
    return re.sub(r"[^a-z0-9]", "", s)


def find_icon(stc_dir, costume, all_stcs):
    """Icon for a costume, tolerating small naming differences in the source repo."""
    wanted = norm(costume)
    for f in all_stcs:
        if norm(f.stem) == wanted:
            return f
    style = re.sub(r"[\d_]*s?$", "", costume.lower())
    for f in all_stcs:
        if norm(f.stem).startswith(norm(style)):
            return f
    return all_stcs[0] if all_stcs else None


def java_resize_dims(w, h, scale):
    """Size produced by the program's ImageService.resizeImage for a given 'scale' setting."""
    target = max(int(scale * w), int(scale * h))
    if h < w:
        return round(w * target / h), target
    return target, round(h * target / w)


def visible_box(path):
    a = np.array(Image.open(path).convert("RGBA"))[:, :, 3]
    ys, xs = np.where(a > 40)
    return xs.min(), ys.min(), xs.max() + 1, ys.max() + 1, a.shape[1], a.shape[0]


def frame(paths):
    boxes = [visible_box(p) for p in paths]
    x0, y0, x1, y1 = (float(statistics.median(b[i] for b in boxes)) for i in range(4))
    w, h = boxes[0][4], boxes[0][5]
    bw, bh = x1 - x0, y1 - y0
    cut_bottom = y1 >= h - 2
    if cut_bottom:
        e = min((UNDER_BAR_Y - (VIS_TOP - 5)) / bh, MAX_W / bw, MAX_UPSCALE)
    else:
        e = min((VIS_BOTTOM - VIS_TOP) / bh, MAX_W / bw, MAX_UPSCALE)
    scale = round(e * w / h if h >= w else e, 3)
    nw, nh = java_resize_dims(w, h, scale)
    e = nw / w
    ox = CENTER_X - (x0 + bw / 2) * e
    oy = UNDER_BAR_Y - nh if cut_bottom else (VIS_TOP + VIS_BOTTOM) / 2 - (y0 + bh / 2) * e
    return {"offset": [int(round(ox)), int(round(oy))], "scale": scale}


def main(src):
    src = Path(src)
    counts, index, frames = {}, ["# Project Plus costumes (alt number -> source file)\n"], {}
    for char_dir in sorted(p for p in src.iterdir() if p.is_dir() and p.name.endswith("(P+)")):
        name = char_dir.name[:-4].strip()
        code = code_for(name)
        files = sorted((char_dir / "CSPs" / "HD").glob("*.png"),
                       key=lambda f: ("standard" not in f.name.lower(), natural(f.name)))
        if not files:
            continue
        stcs = sorted((char_dir / "STCs" / "HD").glob("*.png"), key=lambda f: natural(f.name))
        for out in (ART_OUT / code, ICON_OUT / code):
            if out.exists():
                shutil.rmtree(out)
            out.mkdir(parents=True)
        index.append(f"\n## {name} (`{code}`)\n")
        for i, f in enumerate(files, 1):
            costume = re.sub(r"_CSP_HD$", "", f.stem)
            shutil.copyfile(f, ART_OUT / code / f"{i}.png")
            icon = find_icon(char_dir / "STCs" / "HD", costume, stcs)
            if icon:
                Image.open(icon).convert("RGBA").resize((64, 64), Image.LANCZOS).save(ICON_OUT / code / f"{i}.png")
            index.append(f"- {i}: {costume}")
        counts[code] = len(files)
        frames[code] = frame(files)
    (ART_OUT / "costumes.md").write_text("\n".join(index) + "\n", encoding="utf-8")

    settings = json.loads(SETTINGS.read_text(encoding="utf-8"))
    for fighter in settings["fighters"]:
        if fighter["fighter"] in frames:
            fighter.update(frames[fighter["fighter"]])
    SETTINGS.write_text(json.dumps(settings, indent=2) + "\n", encoding="utf-8")
    for code, n in counts.items():
        print(f"{code}={n} {frames[code]}")


if __name__ == "__main__":
    main(sys.argv[1])
