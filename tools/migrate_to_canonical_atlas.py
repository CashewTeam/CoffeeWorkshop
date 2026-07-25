"""Migrate mod textures to canonical 1.20.1 paths and rewrite the
corresponding model references.

Root cause that this fixes:
    In Minecraft 1.20.1 the block texture atlas only scans directories
    matching the literal source names declared in
    `assets/minecraft/atlases/blocks.json` — i.e. `textures/block/`
    (singular) and `textures/item/`. Subdirectories like `textures/blocks/`,
    `textures/items/`, or `textures/model/...` are NOT stitched into the
    atlas at all, so any model reference naming those as sprite IDs will
    fall back to "missing texture".

    The previous 'fix' was wrong: it added `textures/` to references and
    kept the plural directory names. That did not actually load anything
    because (a) `coffeework:textures/blocks/foo` is a literal sprite ID
    and the atlas has no source that produces it, and (b) `textures/blocks/`
    isn't scanned.

This script does the actual canonical migration:

    Directory moves:
        textures/blocks/   -> textures/block/
        textures/items/    -> textures/item/
        textures/model/    -> textures/block/model/

    Reference rewrites (in model JSONs):
        coffeework:textures/blocks/<x>   -> coffeework:block/<x>
        coffeework:textures/items/<x>    -> coffeework:item/<x>
        coffeework:textures/model/<x>    -> coffeework:block/model/<x>

Run order matters: directories move first, references rewrite second.

Usage: python tools/migrate_to_canonical_atlas.py [--dry-run]
"""
from __future__ import annotations
import argparse
import json
import re
import shutil
import sys
from pathlib import Path

REPO_ROOT = Path(__file__).resolve().parents[1]
ASSETS = REPO_ROOT / "src" / "main" / "resources" / "assets" / "coffeework"
TEXTURES = ASSETS / "textures"

# Each (old-path-under-textures, new-path-under-textures)
DIRECTORY_MOVES = [
    (TEXTURES / "blocks", TEXTURES / "block"),
    (TEXTURES / "items",  TEXTURES / "item"),
    (TEXTURES / "model",  TEXTURES / "block" / "model"),
]

# String-level rewrite in model JSONs (only the value is touched,
# key structure remains intact, parents are left alone).
REWRITES = [
    (f"coffeework:textures/blocks/", f"coffeework:block/"),
    (f"coffeework:textures/items/",  f"coffeework:item/"),
    (f"coffeework:textures/model/",  f"coffeework:block/model/"),
]


def _move_dirs(dry_run: bool) -> int:
    moved = 0
    print("=== Directory renames ===")
    for src, dst in DIRECTORY_MOVES:
        if not src.exists():
            print(f"  skip (not present): {src.relative_to(REPO_ROOT)}")
            continue
        if dst.exists():
            print(f"  ERROR target exists, will not move: {src} -> {dst}")
            continue
        print(f"  {src.relative_to(REPO_ROOT)}  ->  {dst.relative_to(REPO_ROOT)}")
        if not dry_run:
            dst.parent.mkdir(parents=True, exist_ok=True)
            shutil.move(str(src), str(dst))
            moved += 1
    return moved


def _rewrite_models(dry_run: bool) -> int:
    print("\n=== Model JSON reference rewrites ===")
    files = sorted(p for d in (ASSETS / "models" / "block", ASSETS / "models" / "item") for p in d.glob("*.json"))
    print(f"Scanning {len(files)} model files")
    changed = 0
    rewrites_total = 0
    for path in files:
        text = path.read_text(encoding="utf-8")
        try:
            data = json.loads(text)
        except json.JSONDecodeError as exc:
            print(f"  PARSE ERROR {path}: {exc}")
            continue

        per_file = 0

        def walk(node):
            nonlocal per_file
            if isinstance(node, dict):
                return {k: walk(v) for k, v in node.items()}
            if isinstance(node, list):
                return [walk(v) for v in node]
            if isinstance(node, str):
                for old, new in REWRITES:
                    if node.startswith(old):
                        per_file += 1
                        return new + node[len(old):]
                return node
            return node

        new_data = walk(data)
        if json.dumps(data, sort_keys=True, ensure_ascii=False) == json.dumps(
            new_data, sort_keys=True, ensure_ascii=False
        ):
            continue
        if not dry_run:
            path.write_text(json.dumps(new_data, indent=2, ensure_ascii=False) + "\n",
                            encoding="utf-8")
        changed += 1
        rewrites_total += per_file
        if per_file:
            print(f"  CHANGED  {path.relative_to(REPO_ROOT)}  ({per_file} rewrites)")
    print(f"\n{('would change' if dry_run else 'changed')}: {changed} file(s), {rewrites_total} string rewrites")
    return changed


def main() -> int:
    ap = argparse.ArgumentParser()
    ap.add_argument("--dry-run", action="store_true")
    ap.add_argument("--yes", "-y", action="store_true",
                    help="Skip the confirmation prompt (for non-interactive shells).")
    args = ap.parse_args()
    if not args.dry_run:
        if not args.yes:
            try:
                ans = input("About to MOVE texture directories in-place. Continue? [y/N] ")
            except EOFError:
                print("No interactive stdin. Re-run with --yes to confirm.", file=sys.stderr)
                return 1
            if ans.lower() != "y":
                print("Aborted.")
                return 1
    _move_dirs(args.dry_run)
    _rewrite_models(args.dry_run)
    return 0


if __name__ == "__main__":
    sys.exit(main())
