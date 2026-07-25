#!/usr/bin/env python3
"""
Fix mod-owned texture references in model JSON files that are missing the
modern 'textures/' prefix.

The mod uses the legacy 1.12.2 single-segment convention (e.g.
`coffeework:blocks/cake_berry_side`), but the texture PNGs actually live
under `assets/coffeework/textures/blocks/...` (the modern convention).
This breaks ALL non-vanilla textures in the mod.

Script rules (conservative on purpose):

  * A string reference is classified by *where it appears*:
      - value of a `parent` key            -> model parent ref (untouched)
      - value of a `textures`/`layer*` slot or of a `faces.<face>.texture`
                                           -> texture ref (auto-fixable)
      - everything else (string under any other position)
                                           -> treated as texture too,
                                              because the only non-parent
                                              coffeework:* refs in model
                                              JSONs are texture refs.
  * For each candidate rewrite, verify the target PNG exists before
    committing. If the target exists under `textures/<tail>.png`, rewrite.
    If the target would still be missing, try a `coffee/` segment for
    `model/...` refs (since many actual textures live under
    `textures/model/coffee/...`).
  * For references that no candidate can resolve, we don't blindly edit
    them by default. If `--resolve-broken` is passed, a small substitution
    table is applied (e.g. map legacy `texture1` to an existing
    `coffee_americano`). Otherwise they are listed for human review.

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
from typing import Any, Callable

REPO_ROOT = Path(__file__).resolve().parents[1]
ASSETS = REPO_ROOT / "src" / "main" / "resources" / "assets" / "coffeework"
MODELS_DIR = ASSETS / "models"
BLOCK_DIR = MODELS_DIR / "block"
ITEM_DIR = MODELS_DIR / "item"
TEXTURES_DIR = ASSETS / "textures"

NS = "coffeework"

# Manual substitution for genuinely missing legacy Blockbench textures.
BROKEN_SUBSTITUTIONS: dict[str, str] = {
    f"{NS}:model/coffee/texture1": f"{NS}:textures/model/coffee/coffee_americano",
    f"{NS}:model/texture1":        f"{NS}:textures/model/coffee/coffee_americano",
    f"{NS}:model/texture3":        f"{NS}:textures/model/coffee/coffee_cappuccino",
    f"{NS}:model/texture9":        f"{NS}:textures/model/coffee/coffee_americano",
    f"{NS}:model/texture17":       f"{NS}:textures/model/coffee/coffee_latte",
    # Only stages 2 (and 3) of the coffee tree are present; reuse stage_2 for
    # stage_0 (the most-ripe drop texture is acceptable for the seedling
    # shader here since the recipe is for the bean drop item).
    f"{NS}:blocks/coffee_stage_0": f"{NS}:textures/blocks/coffee_stage_2",
}

# Texture-slot keys (value of these keys is a texture reference).
TEXTURE_SLOT_KEYS = {"particle"} | {f"layer{i}" for i in range(8)}


def _try_resolve_texture(ref: str) -> tuple[bool, str | None]:
    """Try to resolve the reference as a PNG texture. Returns
    (resolved, target_or_None).

    Handles both legacy (e.g. `coffeework:items/foo` resolving under
    `assets/coffeework/textures/items/foo.png`) and modern (already
    prefixed, e.g. `coffeework:textures/items/foo`) forms.
    """
    if not ref.startswith(NS + ":"):
        return False, None
    tail = ref[len(NS) + 1:]
    # If the ref is already under `textures/`, accept it as-is.
    if tail.startswith("textures/"):
        if (ASSETS / (tail + ".png")).exists():
            return True, ref
        return False, None
    # Otherwise try the modern path with `textures/` prepended.
    cand = TEXTURES_DIR / (tail + ".png")
    if cand.exists():
        return True, f"{NS}:textures/{tail}"
    # For legacy `model/...` tails, also try the `model/coffee/...` segment.
    if tail.startswith("model/"):
        rest = tail[len("model/"):]
        alt = TEXTURES_DIR / "model" / "coffee" / (rest + ".png")
        if alt.exists():
            return True, f"{NS}:textures/model/coffee/{rest}"
    return False, None


def transform(
    node: Any,
    file: Path,
    replacements: dict[Path, list[tuple[str, str]]],
    broken: dict[Path, set[str]],
    in_parent_value: bool = False,
) -> Any:
    """Recursive walker with two pieces of context:
       - in_parent_value : we are currently the value of a `parent` key.
                            Strings here are MODEL refs and must be left
                            alone.
       Otherwise, any string starting with `coffeework:` is a TEXTURE ref.
    """
    if isinstance(node, dict):
        new = {}
        for k, v in node.items():
            if isinstance(v, str) and v.startswith(NS + ":"):
                if k == "parent":
                    # Parent ref - leave alone.
                    new[k] = v
                    continue
                # Texture-slot key (textures/layer*/particle) or face.texture:
                # these are texture refs.
                tail = v[len(NS) + 1:]
                resolved, target = _try_resolve_texture(v)
                if resolved and target is not None:
                    replacements.setdefault(file, []).append((v, target))
                    new[k] = target
                else:
                    broken.setdefault(file, set()).add(v)
                    new[k] = v
            else:
                # Recurse, propagating "we are a parent value" only when
                # the *next* key is literally 'parent'.
                new[k] = transform(v, file, replacements, broken,
                                    in_parent_value=False)
        return new
    if isinstance(node, list):
        return [transform(v, file, replacements, broken, in_parent_value) for v in node]
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
        # After substitution, all should resolve; nothing left in broken set
        # for the post-walk.

    if json.dumps(data, sort_keys=True, ensure_ascii=False) == json.dumps(
        new_data, sort_keys=True, ensure_ascii=False
    ):
        return False

    if not dry_run:
        path.write_text(json.dumps(new_data, indent=2, ensure_ascii=False) + "\n",
                        encoding="utf-8")
    return True


def collect_report(path: Path, resolve_broken: bool):
    """Walk a file purely to collect replacement/broken lists for reporting."""
    data = json.loads(path.read_text(encoding="utf-8"))
    repl: dict[Path, list] = {}
    broken: dict[Path, set] = {}
    transform(data, path, repl, broken)
    return repl.get(path, []), broken.get(path, set())


def main() -> int:
    ap = argparse.ArgumentParser()
    ap.add_argument("--dry-run", action="store_true")
    ap.add_argument("--write", action="store_true")
    ap.add_argument("--resolve-broken", action="store_true",
                    help="Apply BROKEN_SUBSTITUTIONS to references that have no "
                         "auto-fix candidate (default: only list them).")
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

    # Always run an extra pass to collect replacement counts and broken refs
    print("\n=== Replacement summary ===")
    total_repl = 0
    for path in files:
        repl, _ = collect_report(path, args.resolve_broken)
        if repl:
            total_repl += len(repl)
            print(f"\n  {path.relative_to(REPO_ROOT)}:")
            for old, new in repl:
                print(f"    {old}  ->  {new}")
    print(f"\nTotal texture refs rewritten: {total_repl}")

    # Combined broken refs across files
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
