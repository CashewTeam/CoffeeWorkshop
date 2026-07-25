#!/usr/bin/env python3
"""
Re-runnable companion to `tools/migrate_to_canonical_atlas.py`.

After the one-shot canonical migration, this script keeps models clean.
If a model is added or edited with a non-canonical reference, it fixes
the rewrite and (with `--resolve-broken`) applies substitutions for
genuinely missing Blockbench textures.

Conventions enforced:

    coffeework:textures/blocks/<x>   -> coffeework:block/<x>
    coffeework:textures/items/<x>    -> coffeework:item/<x>
    coffeework:textures/model/<x>    -> coffeework:block/model/<x>
    coffeework:blocks/<x>            -> coffeework:block/<x>
    coffeework:items/<x>             -> coffeework:item/<x>
    coffeework:model/<x>             -> coffeework:block/model/<x>

Texture references are identified by *where* they appear in the JSON
(values of `textures` / `layer*` / `particle` keys and values of a
`face.texture` element). `parent` values are MODEL refs and are left
untouched (they correctly resolve under `models/`, not `textures/`).

Usage:
  python tools/fix_mod_textures.py --dry-run
  python tools/fix_mod_textures.py --write
  python tools/fix_mod_textures.py --write --resolve-broken
"""
from __future__ import annotations

import argparse
import json
import sys
from pathlib import Path
from typing import Any

REPO_ROOT = Path(__file__).resolve().parents[1]
ASSETS = REPO_ROOT / "src" / "main" / "resources" / "assets" / "coffeework"
MODELS_DIR = ASSETS / "models"
BLOCK_DIR = MODELS_DIR / "block"
ITEM_DIR = MODELS_DIR / "item"

NS = "coffeework"

# String-prefix rewrites. Order matters: more-specific first.
REWRITES: list[tuple[str, str]] = [
    ("coffeework:textures/blocks/", "coffeework:block/"),
    ("coffeework:textures/items/",  "coffeework:item/"),
    ("coffeework:textures/model/",  "coffeework:block/model/"),
    ("coffeework:blocks/", "coffeework:block/"),
    ("coffeework:items/",  "coffeework:item/"),
    ("coffeework:model/",  "coffeework:block/model/"),
]

# Substitutions for genuinely missing Blockbench placeholder textures
# that map to an existing visual counterpart.
BROKEN_SUBSTITUTIONS: dict[str, str] = {
    f"{NS}:block/model/coffee/texture1": f"{NS}:block/model/coffee/coffee_americano",
    f"{NS}:block/model/texture1":        f"{NS}:block/model/coffee/coffee_americano",
    f"{NS}:block/model/texture3":        f"{NS}:block/model/coffee/coffee_cappuccino",
    f"{NS}:block/model/texture9":        f"{NS}:block/model/coffee/coffee_americano",
    f"{NS}:block/model/texture17":       f"{NS}:block/model/coffee/coffee_latte",
    f"{NS}:block/coffee_stage_0":        f"{NS}:block/coffee_stage_2",
}


def _tail(resolved_ref: str) -> str:
    return resolved_ref[len(NS) + 1:]


def _png_exists(ref: str) -> bool:
    return (ASSETS / (_tail(ref) + ".png")).exists()


def _apply_rewrites(v: str) -> str:
    for old_prefix, new_prefix in REWRITES:
        if v.startswith(old_prefix):
            return new_prefix + v[len(old_prefix):]
    return v


def transform(
    node: Any,
    file: Path,
    replacements: dict[Path, list[tuple[str, str]]],
    broken: dict[Path, set[str]],
) -> Any:
    if isinstance(node, dict):
        new = {}
        for k, v in node.items():
            if isinstance(v, str) and v.startswith(NS + ":"):
                if k == "parent":
                    new[k] = v
                    continue
                new_v = _apply_rewrites(v)
                if new_v != v:
                    replacements.setdefault(file, []).append((v, new_v))
                if not _png_exists(new_v):
                    broken.setdefault(file, set()).add(new_v)
                new[k] = new_v
            else:
                new[k] = transform(v, file, replacements, broken)
        return new
    if isinstance(node, list):
        return [transform(v, file, replacements, broken) for v in node]
    return node


def process_file(path: Path, dry_run: bool, resolve_broken: bool):
    text = path.read_text(encoding="utf-8")
    try:
        data = json.loads(text)
    except json.JSONDecodeError as exc:
        print(f"  PARSE ERROR in {path}: {exc}")
        return False

    replacements: dict[Path, list] = {}
    broken: dict[Path, set] = {}
    new_data = transform(data, path, replacements, broken)

    if resolve_broken:

        def _apply_broken(node):
            if isinstance(node, dict):
                return {k: (_apply_broken(v) if not (isinstance(v, str) and v in BROKEN_SUBSTITUTIONS)
                           else BROKEN_SUBSTITUTIONS[v])
                        for k, v in node.items()}
            if isinstance(node, list):
                return [_apply_broken(v) for v in node]
            return node
        new_data = _apply_broken(new_data)

    if json.dumps(data, sort_keys=True, ensure_ascii=False) == json.dumps(
        new_data, sort_keys=True, ensure_ascii=False
    ):
        return False

    if not dry_run:
        path.write_text(json.dumps(new_data, indent=2, ensure_ascii=False) + "\n",
                        encoding="utf-8")
    return True


def collect_report(path: Path, resolve_broken: bool):
    data = json.loads(path.read_text(encoding="utf-8"))
    repl: dict[Path, list] = {}
    broken: dict[Path, set] = {}
    transform(data, path, repl, broken)
    return repl.get(path, []), broken.get(path, set())


def main() -> int:
    ap = argparse.ArgumentParser()
    ap.add_argument("--dry-run", action="store_true")
    ap.add_argument("--write", action="store_true")
    ap.add_argument("--resolve-broken", action="store_true")
    args = ap.parse_args()
    if not args.dry_run and not args.write:
        ap.print_help()
        return 1

    files = sorted(p for d in (BLOCK_DIR, ITEM_DIR) for p in d.glob("*.json"))
    print(f"Scanning {len(files)} model files under {MODELS_DIR}")
    total_changed = 0
    for path in files:
        changed = process_file(path, args.dry_run, args.resolve_broken)
        if changed:
            total_changed += 1
            print(f"  CHANGED  {path.relative_to(REPO_ROOT)}")

    print("\n=== Still-broken texture references (no PNG anywhere) ===")
    combined_broken: dict[str, int] = {}
    for path in files:
        _, broken_set = collect_report(path, args.resolve_broken)
        for r in broken_set:
            combined_broken[r] = combined_broken.get(r, 0) + 1
    if not combined_broken:
        print("  (none)")
    else:
        for ref, c in sorted(combined_broken.items(), key=lambda kv: -kv[1]):
            sub = BROKEN_SUBSTITUTIONS.get(ref, "(no substitution defined)")
            print(f"  {c:>3}  {ref}  ->  {sub}")

    verb = "would change" if args.dry_run else "changed"
    print(f"\n{verb}: {total_changed} file(s)")
    return 0


if __name__ == "__main__":
    try:
        sys.exit(main())
    except OSError as exc:
        print(f"I/O error: {exc}", file=sys.stderr)
        sys.exit(3)
