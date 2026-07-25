#!/usr/bin/env python3
"""
Migrate bare legacy `blocks/<old_name>` vanilla texture references in mod
model JSONs (under `assets/coffeework/models/block/` and `models/item/`)
to the modern `block/<new_name>` paths.

Constraints:
  - Only touches UNQUALIFIED (bare) `blocks/<old>` string values. Leaves
    `coffeework:blocks/...` alone because those point to the mod's own
    texture directory and remain valid.
  - Exact string match (no fuzzy/substring), so we never munge unrelated
    paths like `blocks/...something else...`.
  - Applies safe replacements even in files that ALSO contain
    `blocks/anvil_base` (the latter is reported for human review; see
    `build/reports/manual_texture_review.md`).

Usage:
  python tools/migrate_vanilla_textures.py --dry-run
  python tools/migrate_vanilla_textures.py --write
"""
from __future__ import annotations

import argparse
import json
import re
import sys
from pathlib import Path

REPO_ROOT = Path(__file__).resolve().parents[1]
MODELS_ROOT = (
    REPO_ROOT / "src" / "main" / "resources" / "assets" / "coffeework" / "models"
)

# Hard-coded dictionary. Each entry maps a bare vanilla texture token to its
# modern equivalent. Be conservative: if you don't know the modern name for
# sure, DON'T add it here. Add to SKIP_KEYS or REPORT_KEYS instead.
MIGRATION = {
    "blocks/log_oak": "block/oak_log",
    "blocks/log_oak_top": "block/oak_log_top",
    "blocks/planks_oak": "block/oak_planks",
    "blocks/concrete_black": "block/black_concrete",
    "blocks/concrete_white": "block/white_concrete",
    "blocks/iron_block": "block/iron_block",
    "blocks/hardened_clay": "block/terracotta",
    "blocks/furnace_front_on": "block/furnace_front_on",
    "blocks/wool_colored_white": "block/white_wool",
    "blocks/wool_colored_brown": "block/brown_wool",
    "blocks/stone_slab_side": "block/smooth_stone_slab_side",
    "blocks/stone_slab_top": "block/smooth_stone_slab_top",
}

# Bare vanilla tokens that have no clean modern equivalent and require a
# human to pick a replacement. Files containing these are listed in a review
# report. Safe replacements still apply.
REPORT_KEYS = {"blocks/anvil_base"}

EXACT_RE = re.compile(r"^[A-Za-z0-9_\-]+/[A-Za-z0-9_\-]+$")


def _walk_replace(node, file_replacements, file_reports, path):
    if isinstance(node, dict):
        return {k: _walk_replace(v, file_replacements, file_reports, path) for k, v in node.items()}
    if isinstance(node, list):
        return [_walk_replace(v, file_replacements, file_reports, path) for v in node]
    if isinstance(node, str):
        if node in MIGRATION:
            file_replacements[path].append((node, MIGRATION[node]))
            return MIGRATION[node]
        if node in REPORT_KEYS:
            file_reports.setdefault(path, set()).add(node)
        return node
    return node


def process_file(path: Path, dry_run: bool, file_replacements, file_reports):
    text = path.read_text(encoding="utf-8")
    try:
        data = json.loads(text)
    except json.JSONDecodeError as exc:
        print(f"  PARSE ERROR in {path}: {exc}")
        return False, None
    new_data = _walk_replace(data, file_replacements, file_reports, path)
    # Compare by canonical JSON to detect a real *content* difference rather
    # than just a re-formatting cosmetic change.  Use sort_keys for stability
    # and ensure_ascii so the strings we just changed compare equal.
    if json.dumps(data, sort_keys=True, ensure_ascii=False) == json.dumps(
        new_data, sort_keys=True, ensure_ascii=False
    ):
        # No actual replacement happened (or the file already used these names).
        return False, text
    if not dry_run:
        # Re-emit with 2-space indent to match pack.mcmeta style.
        path.write_text(
            json.dumps(new_data, indent=2, ensure_ascii=False) + "\n",
            encoding="utf-8",
        )
    return True, text


def main() -> int:
    ap = argparse.ArgumentParser()
    ap.add_argument("--dry-run", action="store_true")
    ap.add_argument("--write", action="store_true")
    args = ap.parse_args()
    if not args.dry_run and not args.write:
        ap.print_help()
        return 1

    files = sorted(MODELS_ROOT.rglob("*.json"))
    print(f"Scanning {len(files)} model files under {MODELS_ROOT}")

    from collections import defaultdict
    file_replacements = defaultdict(list)
    file_reports = {}

    total_changed = 0
    for path in files:
        changed, _orig = process_file(path, args.dry_run, file_replacements, file_reports)
        if changed:
            total_changed += 1
            n = len(file_replacements.get(path, []))
            tag = "PARTS" if n else ""
            print(f"  CHANGED  {path.relative_to(REPO_ROOT)}  ({n} replacements)")

    # Per-file breakdown
    print("\n=== Replacements summary ===")
    for path, repls in sorted(file_replacements.items()):
        print(f"  {path.relative_to(REPO_ROOT)}")
        for old, new in repls:
            print(f"    {old}  ->  {new}")

    # Manual review list
    print("\n=== Files needing human review (anvil_base / etc.) ===")
    if not file_reports:
        print("  (none)")
    else:
        for path, keys in sorted(file_reports.items()):
            rel = path.relative_to(REPO_ROOT)
            print(f"  {rel}  -> {sorted(keys)}")

    verb = "would change" if args.dry_run else "changed"
    print(f"\n{verb}: {total_changed} file(s)")
    return 0


if __name__ == "__main__":
    try:
        sys.exit(main())
    except OSError as exc:
        print(f"I/O error: {exc}", file=sys.stderr)
        sys.exit(3)
