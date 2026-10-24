#!/usr/bin/env python3
"""Imports Project M character portraits into assets/characters/ssbpm/artwork.

Source layout (juanwise/Renders-and-Cosmetics):  <Character> (P+)/CSPs/HD/<name>_CSP_HD.png
Output: assets/characters/ssbpm/artwork/<url_name>/<alt>.png  +  costumes.md (alt -> original file)

Alt numbers: "Standard" costumes first, then the others, each group in natural name order.
Usage: python3 build/importProjectMRenders.py <path-to-Renders-and-Cosmetics> [output-dir]
"""
import re, shutil, sys
from pathlib import Path

FOLDER_TO_CODE = {
    "Captain Falcon": "captain_falcon", "Dedede": "king_dedede", "Diddy Kong": "diddy_kong",
    "Donkey Kong": "donkey_kong", "Ice Climbers": "ice_climbers", "Meta Knight": "meta_knight",
    "Mr. Game and Watch": "mr_game_and_watch", "ROB": "rob", "Toon Link": "toon_link",
    "Zero Suit Samus": "zero_suit_samus",
}

def code_for(folder):
    return FOLDER_TO_CODE.get(folder, folder.lower().replace(" ", "_"))

def natural(s):
    return [int(t) if t.isdigit() else t.lower() for t in re.split(r"(\d+)", s)]

def main(src, out):
    src, out = Path(src), Path(out)
    counts, index = {}, ["# Project M costumes (alt number -> source file)\n"]
    for char_dir in sorted(p for p in src.iterdir() if p.is_dir() and p.name.endswith("(P+)")):
        name = char_dir.name[:-4].strip()
        code = code_for(name)
        files = sorted((char_dir / "CSPs" / "HD").glob("*.png"),
                       key=lambda f: ("standard" not in f.name.lower(), natural(f.name)))
        if not files:
            continue
        dest = out / code
        if dest.exists():
            shutil.rmtree(dest)
        dest.mkdir(parents=True)
        index.append(f"\n## {name} (`{code}`)\n")
        for i, f in enumerate(files, 1):
            shutil.copyfile(f, dest / f"{i}.png")
            index.append(f"- {i}: {re.sub(r'_CSP_HD$', '', f.stem)}")
        counts[code] = len(files)
    (out / "costumes.md").write_text("\n".join(index) + "\n", encoding="utf-8")
    for code, n in counts.items():
        print(f"{code}={n}")

if __name__ == "__main__":
    main(sys.argv[1], sys.argv[2] if len(sys.argv) > 2 else "assets/characters/ssbpm/artwork")
