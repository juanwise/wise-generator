#!/usr/bin/env python3
"""Imports Project Plus costume sheets (5 columns x 3 rows of 384x480 portraits) for fighters that are
missing from the Renders-and-Cosmetics repository.

Put one sheet per fighter in a folder, named after the fighter's URL name (mewtwo.png, bowser.png, ...):

    python3 build/importProjectMSheets.py <folder with the sheets>      (from the repo root)

Costume 1 is the top-left cell; they are numbered left to right, then down. Writes the same files as
importProjectMRenders.py (images, 64x64 icons, thumbnail placement); the icon is the top of the portrait.
Run it after importProjectMRenders.py, which rewrites costumes.md.
"""
import json, sys
from pathlib import Path

import numpy as np
from PIL import Image

sys.path.insert(0, str(Path(__file__).parent))
import importProjectMRenders as pm

COLS, ROWS, CELL_W, CELL_H = 5, 3, 384, 480


def main(folder):
    settings = json.loads(pm.SETTINGS.read_text(encoding="utf-8"))
    lines = []
    for sheet in sorted(Path(folder).glob("*.png")):
        code = sheet.stem
        img = Image.open(sheet).convert("RGBA")
        art, icons = pm.ART_OUT / code, pm.ICON_OUT / code
        for out in (art, icons):
            out.mkdir(parents=True, exist_ok=True)
            for old in out.glob("*.png"):
                old.unlink()
        n = 0
        for r in range(img.height // CELL_H):
            for c in range(img.width // CELL_W):
                cell = img.crop((c * CELL_W, r * CELL_H, (c + 1) * CELL_W, (r + 1) * CELL_H))
                if not np.array(cell)[:, :, 3].max():
                    continue
                n += 1
                cell.save(art / f"{n}.png", optimize=True)
                cell.crop((0, 0, CELL_W, CELL_W)).resize((64, 64), Image.LANCZOS).save(icons / f"{n}.png")
        placement = pm.frame(sorted(art.glob("*.png")))
        for fighter in settings["fighters"]:
            if fighter["fighter"] == code:
                fighter.update(placement)
        lines.append(f"\n## {code} (`{code}`)\n")
        lines += [f"- {i}: costume {i} of the sheet (left to right, top to bottom)" for i in range(1, n + 1)]
        print(f"{code}={n} {placement}")
    pm.SETTINGS.write_text(json.dumps(settings, indent=2) + "\n", encoding="utf-8")
    index = pm.ART_OUT / "costumes.md"
    with index.open("a", encoding="utf-8") as f:
        f.write("\n".join(lines) + "\n")


if __name__ == "__main__":
    main(sys.argv[1])
