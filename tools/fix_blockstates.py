#!/usr/bin/env python3
"""
Normalize CoffeeWorkshop blockstate JSON:

  - Rewrites every string value of the form "coffeework:<name>" (no slash
    after the colon) to "coffeework:block/<name>".  Leaves already-qualified
    paths like "coffeework:block/..." or "coffeework:item/..." alone.
  - Asserts the rewritten target file actually exists under `models/block/`.
  - For `coldbrew_pot.json`: removes top-level "forge_marker" and
    "defaults" keys (the existing `variants` block is self-sufficient).
  - Re-serializes with 2-space indent to match `pack.mcmeta`.

Exit codes:
  0  nothing to fix / all fixed
  2  a rewritten target model file does not exist (will not modify!)
  3  I/O error
"""
from __future__ import annotations

import argparse
import json
import re
import sys
from pathlib import Path
from typing import Any

REPO_ROOT = Path(__file__).resolve().parents[1]
BLOCKSTATES_DIR = (
    REPO_ROOT / "src" / "main" / "resources" / "assets" / "coffeework" / "blockstates"
)
MODELS_DIR = (
    REPO_ROOT / "src" / "main" / "resources" / "assets" / "coffeework" / "models" / "block"
)

NAMESPACE = "coffeework"
# Pattern for "coffeework:foo" without a path separator after the colon.
BARE_MODEL = re.compile(rf"^{re.escape(NAMESPACE)}:([A-Za-z0-9_\-]+)$")


def _validate_target(rel_path: str) -> bool:
    return (MODELS_DIR / Path(rel_path).name).exists()


def _rewrite_string(s: str, errors: list[str], file: Path) -> str:
    m = BARE_MODEL.match(s)
    if not m:
        return s
    model_name = m.group(1)
    target = f"{NAMESPACE}:block/{model_name}"
    if not _validate_target(model_name + ".json"):
        errors.append(f"{file}: target missing: {target}")
        return s  # leave unmodified
    return target


def _walk(node: Any, errors: list[str], file: Path) -> Any:
    if isinstance(node, dict):
        return {k: _walk(v, errors, file) for k, v in node.items()}
    if isinstance(node, list):
        return [_walk(v, errors, file) for v in node]
    if isinstance(node, str):
        return _rewrite_string(node, errors, file)
    return node


def process_file(path: Path, dry_run: bool) -> tuple[bool, list[str]]:
    text = path.read_text(encoding="utf-8")
    # Tolerate trailing commas etc.? No — blockstates are machine-produced and strict.
    data = json.loads(text)
    errors: list[str] = []
    is_coldbrew = path.name == "coldbrew_pot.json"
    if is_coldbrew and isinstance(data, dict):
        data.pop("forge_marker", None)
        data.pop("defaults", None)
    new_data = _walk(data, errors, path)
    if errors:
        return False, errors
    rewritten_text = json.dumps(new_data, indent=2, ensure_ascii=False) + "\n"
    changed = rewritten_text != text
    if changed and not dry_run:
        path.write_text(rewritten_text, encoding="utf-8")
    return changed, errors


def main() -> int:
    ap = argparse.ArgumentParser()
    ap.add_argument("--dry-run", action="store_true")
    ap.add_argument("--write", action="store_true")
    args = ap.parse_args()
    if not args.dry_run and not args.write:
        ap.print_help()
        return 1

    files = sorted(BLOCKSTATES_DIR.glob("*.json"))
    print(f"Scanning {len(files)} blockstate files under {BLOCKSTATES_DIR}")
    total_changed = 0
    total_missing_models: list[str] = []
    total_errors: list[str] = []
    for path in files:
        changed, errors = process_file(path, dry_run=args.dry_run)
        if errors:
            for e in errors:
                print(f"  ERROR {e}")
            total_errors.extend(errors)
        elif changed:
            total_changed += 1
            print(f"  CHANGED  {path.name}")
            if path.name == "coldbrew_pot.json":
                print("            (removed forge_marker + defaults)")
    if total_errors:
        print(f"\nFAIL: {len(total_errors)} missing-model target(s) detected.")
        return 2
    verb = "would change" if args.dry_run else "changed"
    print(f"\n{verb}: {total_changed} file(s)")
    return 0


if __name__ == "__main__":
    try:
        sys.exit(main())
    except OSError as exc:
        print(f"I/O error: {exc}", file=sys.stderr)
        sys.exit(3)
