#!/usr/bin/env python3
"""Adds the regular (original) Project Plus costumes of fighters that only had their extra costumes.

Put one sheet per fighter in a folder, named after the fighter's URL name (mario.png, link.png, ...):

    python3 build/importProjectMRegularSheets.py <folder with the sheets>      (from the repo root)

The sheets are grids of costumes (see GRID); cells are numbered left to right, then down, and the fighter's
costumes are re-ordered so that the sheet's costumes come first, in sheet order. Costumes that already exist
in assets/characters/ssbpm/artwork/<fighter>/ (recognised by comparing the pictures) keep their existing
image and icon; the existing costumes that are not on the sheet are put after the sheet's. New cells are
scaled to 384x480 and get a 64x64 icon taken from the top of the portrait.

Run it after importProjectMRenders.py and importProjectMSheets.py, which rewrite the same files.
"""
import json, re, sys
from pathlib import Path

import numpy as np
from PIL import Image

sys.path.insert(0, str(Path(__file__).parent))
import importProjectMRenders as pm

CELL_W, CELL_H = 384, 480
# (columns, rows to read, rows the sheet is divided into)
GRID = {"king_dedede": (5, 3, 3), "diddy_kong": (5, 3, 3), "luigi": (5, 3, 3), "mario": (5, 3, 3),
        "marth": (5, 3, 3), "pit": (5, 3, 3), "wolf": (5, 3, 3), "link": (9, 2, 2), "wario": (6, 3, 3),
        "rob": (6, 3, 3),
        "mr_game_and_watch": (7, 2, 3)}  # the 3rd row (Judge) is already imported from the renders
DUPLICATE_BELOW = 8.0      # picture difference under which a cell is an existing costume
REPLACE_EXISTING = {"king_dedede"}   # the sheet has better renders of the same costumes
# Same costume rendered differently, so the pictures can't be compared: cell number (from 1) -> existing alt
MANUAL_MATCH = {"link": {10: 2, 11: 3, 12: 4, 13: 5, 14: 6, 15: 7, 16: 1}}


def read_cells(sheet, code):
    cols, rows, divisions = GRID[code]
    img = Image.open(sheet).convert("RGBA")
    cells = []
    for r in range(rows):
        for c in range(cols):
            box = (round(c * img.width / cols), round(r * img.height / divisions),
                   round((c + 1) * img.width / cols), round((r + 1) * img.height / divisions))
            cell = img.crop(box)
            if (np.array(cell)[:, :, 3] > 40).sum() > 0.03 * cell.width * cell.height:   # skip empty cells
                cells.append(cell)
    return cells


def signature(img):
    a = np.array(img.convert("RGBA"))
    ys, xs = np.where(a[:, :, 3] > 40)
    cropped = img.crop((xs.min(), ys.min(), xs.max() + 1, ys.max() + 1))
    flat = Image.new("RGBA", cropped.size, (0, 0, 0, 255))
    flat.alpha_composite(cropped)
    return np.array(flat.convert("RGB").resize((40, 50), Image.LANCZOS), float)


def existing_costumes(code, names):
    art, icons = pm.ART_OUT / code, pm.ICON_OUT / code
    out = []
    for f in sorted(art.glob("*.png"), key=lambda p: int(p.stem)) if art.exists() else []:
        icon = icons / f.name
        out.append({"art": Image.open(f).convert("RGBA"), "icon": Image.open(icon).convert("RGBA") if icon.exists() else None,
                    "name": names.get(int(f.stem), f"costume {f.stem}"), "file": f})
    return out


def match(code, cells, existing):
    """cell index -> existing index, by manual mapping first and then by comparing the pictures."""
    pairs = {c - 1: e - 1 for c, e in MANUAL_MATCH.get(code, {}).items()}
    taken = set(pairs.values())
    if code in REPLACE_EXISTING or not existing:
        return {}
    sigs = [signature(e["art"]) for e in existing]
    candidates = sorted((np.abs(signature(cell) - s).mean(), i, j)
                        for i, cell in enumerate(cells) if i not in pairs for j, s in enumerate(sigs))
    for d, i, j in candidates:
        if d >= DUPLICATE_BELOW:
            break
        if i not in pairs and j not in taken:
            pairs[i] = j
            taken.add(j)
    return pairs


def costume_names(index_text, code):
    section = re.search(r"## [^\n]*\(`" + re.escape(code) + r"`\)\n(.*?)(?=\n## |\Z)", index_text, re.S)
    names = {}
    if section:
        for m in re.finditer(r"- (\d+): ([^\n]*)", section.group(1)):
            names[int(m.group(1))] = m.group(2)
    return names, section


def main(folder):
    settings = json.loads(pm.SETTINGS.read_text(encoding="utf-8"))
    index_path = pm.ART_OUT / "costumes.md"
    index_text = index_path.read_text(encoding="utf-8")
    for sheet in sorted(Path(folder).glob("*.png")):
        code = sheet.stem
        names, section = costume_names(index_text, code)
        cells = read_cells(sheet, code)
        existing = existing_costumes(code, names)
        pairs = match(code, cells, existing)
        order = []                                     # (art, icon, name)
        for i, cell in enumerate(cells):
            if i in pairs:
                e = existing[pairs[i]]
                order.append((e["art"], e["icon"], e["name"]))
                continue
            art = cell if cell.size == (CELL_W, CELL_H) else cell.resize((CELL_W, CELL_H), Image.LANCZOS)
            icon = art.crop((0, 0, CELL_W, CELL_W)).resize((64, 64), Image.LANCZOS)
            order.append((art, icon, f"regular costume {i + 1} of the sheet (left to right, top to bottom)"))
        used = set(pairs.values())
        if code not in REPLACE_EXISTING:
            order += [(e["art"], e["icon"], e["name"]) for j, e in enumerate(existing) if j not in used]

        art_dir, icon_dir = pm.ART_OUT / code, pm.ICON_OUT / code
        for out in (art_dir, icon_dir):
            out.mkdir(parents=True, exist_ok=True)
            for old in out.glob("*.png"):
                old.unlink()
        for n, (art, icon, _) in enumerate(order, 1):
            art.save(art_dir / f"{n}.png", optimize=True)
            if icon is not None:
                icon.save(icon_dir / f"{n}.png")
        placement = pm.frame(sorted(art_dir.glob("*.png")))
        for fighter in settings["fighters"]:
            if fighter["fighter"] == code:
                fighter.update(placement)

        heading = section.group(0).split("\n")[0] if section else f"## {code} (`{code}`)"
        block = heading + "\n\n" + "\n".join(f"- {n}: {name}" for n, (_, _, name) in enumerate(order, 1))
        index_text = index_text.replace(section.group(0), block) if section else index_text + "\n" + block + "\n"
        print(f"{code}={len(order)} (new {len(cells) - len(pairs)}, already had {len(pairs)}) {placement}")
    pm.SETTINGS.write_text(json.dumps(settings, indent=2) + "\n", encoding="utf-8")
    index_path.write_text(index_text, encoding="utf-8")


if __name__ == "__main__":
    main(sys.argv[1])
