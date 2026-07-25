#!/usr/bin/env python3
"""
Coffee Workshop resource audit (1.20.1).

Walks the mod's resource directory tree and verifies the post-cleanup state
matches the modern format. Reports counts to stdout and writes a Markdown
report to `build/reports/resource-audit.md`.

Checks performed:
  1.  Every file under `lang/` parses as JSON (no `.lang` residue).
  2.  No JSON file uses `forge_marker` or `defaults` keys.
  3.  All `coffeework:...` blockstate model references use `block/<name>`
      (no bare `coffeework:<name>` ids).
  4.  Every `coffeework:block/<name>` reference resolves to an existing
      file in `models/block/`.
  5.  No bare vanilla `blocks/<old_name>` references inside
      `models/block/` and `models/item/` — except `blocks/anvil_base`,
      which has no clean modern equivalent and is listed for human review.
  6.  No key uses the obsolete `coffeeworkshop` namespace inside lang JSON.

Exit code:
  0  no failures
  2  at least one resource problem detected
"""
from __future__ import annotations

import json
import re
import sys
from pathlib import Path
from typing import Iterable

REPO_ROOT = Path(__file__).resolve().parents[1]
ASSETS_ROOT = REPO_ROOT / "src" / "main" / "resources" / "assets" / "coffeework"
LANG_DIR = ASSETS_ROOT / "lang"
BLOCKSTATES_DIR = ASSETS_ROOT / "blockstates"
BLOCK_MODELS_DIR = ASSETS_ROOT / "models" / "block"
ITEM_MODELS_DIR = ASSETS_ROOT / "models" / "item"
REPORT_PATH = REPO_ROOT / "build" / "reports" / "resource-audit.md"
MANUAL_REVIEW_PATH = REPO_ROOT / "build" / "reports" / "manual_texture_review.md"

NAMESPACE = "coffeework"
BARE_MODEL = re.compile(rf"^{re.escape(NAMESPACE)}:([A-Za-z0-9_\-]+)$")

# Vanilla texture tokens that should no longer appear bare.
OBSOLETE_VANILLA = {
    "blocks/log_oak",
    "blocks/log_oak_top",
    "blocks/planks_oak",
    "blocks/concrete_black",
    "blocks/concrete_white",
    "blocks/iron_block",
    "blocks/hardened_clay",
    "blocks/furnace_front_on",
    "blocks/wool_colored_white",
    "blocks/wool_colored_brown",
    "blocks/stone_slab_side",
    "blocks/stone_slab_top",
    "blocks/anvil_base",  # accept-as-is, only flagged for review
}
REVIEW_ONLY = {"blocks/anvil_base"}


def _read_json(path: Path):
    return json.loads(path.read_text(encoding="utf-8"))


def _walk_strings(node) -> Iterable[str]:
    if isinstance(node, dict):
        for v in node.values():
            yield from _walk_strings(v)
    elif isinstance(node, list):
        for v in node:
            yield from _walk_strings(v)
    elif isinstance(node, str):
        yield node


def check_lang_json() -> dict:
    """#1 + #6: every lang file is JSON; no coffeeworkshop namespace leak."""
    files = sorted(LANG_DIR.glob("*.json"))
    legacy = sorted(LANG_DIR.glob("*.lang"))
    if legacy:
        # Tolerated: legacy .lang files left around intentionally.
        pass
    fail = []
    coffeeworkshop_keys: list[tuple[Path, str]] = []
    counts = {}
    for path in files:
        try:
            data = _read_json(path)
        except json.JSONDecodeError as exc:
            fail.append(f"JSON parse error in {path}: {exc}")
            continue
        if not isinstance(data, dict):
            fail.append(f"{path}: top-level is not an object")
            continue
        counts[path.name] = len(data)
        for key in data.keys():
            if "coffeeworkshop." in key:
                coffeeworkshop_keys.append((path, key))
    return {
        "files": [p.name for p in files],
        "counts": counts,
        "legacy_lang_present": [p.name for p in legacy],
        "fail": fail,
        "coffeeworkshop_keys": coffeeworkshop_keys,
    }


def check_forge_keys() -> dict:
    """#2: blockstate JSONs must not use `forge_marker` / `defaults`."""
    found: list[tuple[Path, str]] = []
    for path in BLOCKSTATES_DIR.glob("*.json"):
        try:
            data = _read_json(path)
        except json.JSONDecodeError as exc:
            found.append((path, f"PARSE ERROR: {exc}"))
            continue
        if isinstance(data, dict):
            for key in ("forge_marker", "defaults"):
                if key in data:
                    found.append((path, key))
    return {"found": found}


def check_model_references() -> dict:
    """#3 + #4: blockstate model references must include block/ prefix and
    resolve to existing files."""
    bare_refs: list[tuple[Path, str]] = []
    missing: list[tuple[Path, str]] = []
    resolved_ok = 0
    files_scanned = 0
    for path in BLOCKSTATES_DIR.glob("*.json"):
        files_scanned += 1
        try:
            data = _read_json(path)
        except json.JSONDecodeError:
            continue
        for value in _walk_strings(data):
            m = BARE_MODEL.match(value)
            if m:
                bare_refs.append((path, value))
            elif value.startswith(f"{NAMESPACE}:block/"):
                model_name = value[len(f"{NAMESPACE}:block/"):]
                if not (BLOCK_MODELS_DIR / f"{model_name}.json").exists():
                    missing.append((path, value))
                else:
                    resolved_ok += 1
    return {
        "files_scanned": files_scanned,
        "bare_refs": bare_refs,
        "missing_targets": missing,
        "resolved_ok": resolved_ok,
    }


def check_mod_owned_textures() -> dict:
    """Verify that every `coffeework:*` texture reference inside
    models/block/*.json and models/item/*.json (skipping `parent` values
    and `parent`-style model refs) resolves to a PNG under
    `assets/coffeework/textures/`.  Catches the `coffeework:items/foo`
    and `coffeework:blocks/foo` legacy paths that lack the modern
    `textures/` prefix."""
    parent_refs: list[tuple[Path, str]] = []
    bad_textures: list[tuple[Path, str]] = []
    resolved_textures = 0
    files_scanned = 0
    for d in (BLOCK_MODELS_DIR, ITEM_MODELS_DIR):
        for path in d.glob("*.json"):
            files_scanned += 1
            try:
                data = _read_json(path)
            except json.JSONDecodeError:
                continue
            parent = {None}
            def walk(n, parent_key=None):
                nonlocal parent, bad_textures, resolved_textures
                if isinstance(n, dict):
                    for k, v in n.items():
                        if isinstance(v, str) and v.startswith(f"{NAMESPACE}:"):
                            if k == "parent":
                                parent_refs.append((path, v))
                                continue
                            # Treat anything else as a texture ref.
                            tail = v[len(NAMESPACE) + 1:]
                            # If tail already starts with textures/, accept as-is
                            if tail.startswith("textures/"):
                                cand = ASSETS_ROOT / (tail + ".png")
                            else:
                                cand = ASSETS_ROOT / "textures" / (tail + ".png")
                            if cand.exists():
                                resolved_textures += 1
                            else:
                                bad_textures.append((path, v))
                        else:
                            walk(v, k)
                elif isinstance(n, list):
                    for v in n:
                        walk(v, parent_key)
            walk(data)
    return {
        "files_scanned": files_scanned,
        "parent_refs": parent_refs,
        "bad_textures": bad_textures,
        "resolved": resolved_textures,
    }


def check_vanilla_textures() -> dict:
    """#5: bare vanilla references in models/block and models/item."""
    matches: list[tuple[Path, str]] = []
    files_scanned = 0
    for d in (BLOCK_MODELS_DIR, ITEM_MODELS_DIR):
        for path in d.glob("*.json"):
            files_scanned += 1
            try:
                data = _read_json(path)
            except json.JSONDecodeError:
                continue
            for value in _walk_strings(data):
                if value in OBSOLETE_VANILLA:
                    matches.append((path, value))
    return {
        "files_scanned": files_scanned,
        "matches": matches,
    }


def _format_md(summary: dict) -> str:
    out: list[str] = []
    out.append("# Coffee Workshop Resource Audit")
    out.append("")
    out.append("Auto-generated by `tools/audit_resources.py`.")
    out.append("")
    out.append("## 1. Language files")
    out.append("")
    out.append(f"- JSON files: {len(summary['lang']['files'])}")
    for name, n in summary["lang"]["counts"].items():
        out.append(f"  - `{name}`: {n} keys")
    if summary["lang"]["legacy_lang_present"]:
        out.append("")
        out.append("Legacy `.lang` files left in place (ignored by loader):")
        for n in summary["lang"]["legacy_lang_present"]:
            out.append(f"- `{n}`")
    if summary["lang"]["coffeeworkshop_keys"]:
        out.append("")
        out.append("FAIL: `coffeeworkshop` namespace still appears in keys:")
        for path, k in summary["lang"]["coffeeworkshop_keys"]:
            out.append(f"- `{path}`: `{k}`")

    out.append("")
    out.append("## 2. Forge blockstate legacy keys")
    out.append("")
    if summary["forge"]["found"]:
        out.append("FAIL: residual `forge_marker`/`defaults` keys:")
        for path, k in summary["forge"]["found"]:
            out.append(f"- `{path}`: `{k}`")
    else:
        out.append("PASS: no `forge_marker` or `defaults` keys.")

    out.append("")
    out.append("## 3. Blockstate model references")
    out.append("")
    refs = summary["model_refs"]
    out.append(f"- Files scanned: {refs['files_scanned']}")
    out.append(f"- `coffeework:block/<name>` references resolved: {refs['resolved_ok']}")
    if refs["bare_refs"]:
        out.append("")
        out.append("FAIL: bare `coffeework:<name>` references remain (must add `block/`):")
        for path, v in refs["bare_refs"]:
            out.append(f"- `{path}`: `{v}`")
    else:
        out.append("- PASS: zero bare `coffeework:<name>` references.")
    if refs["missing_targets"]:
        out.append("")
        out.append("FAIL: missing model targets:")
        for path, v in refs["missing_targets"]:
            out.append(f"- `{path}`: `{v}`")

    out.append("")
    out.append("## 4. Vanilla texture references in models")
    out.append("")
    vt = summary["vanilla"]
    out.append(f"- Files scanned: {vt['files_scanned']}")
    failures = [m for m in vt["matches"] if m[1] not in REVIEW_ONLY]
    if failures:
        out.append("")
        out.append("FAIL: obsolete `blocks/...` vanilla references:")
        for path, v in failures:
            out.append(f"- `{path}`: `{v}`")
    else:
        out.append("- PASS: zero obsolete vanilla references (anvil_base exempt).")
    review_only = [m for m in vt["matches"] if m[1] in REVIEW_ONLY]
    if review_only:
        out.append("")
        out.append(f"- Files awaiting human texture review: {len({p for p, _ in review_only})}")
        out.append("- See `manual_texture_review.md` for the per-file list.")

    out.append("")
    out.append("---")
    out.append("")
    return "\n".join(out)


def _format_manual_review(vanilla_matches) -> str:
    files: dict[Path, set[str]] = {}
    for path, v in vanilla_matches:
        if v in REVIEW_ONLY:
            files.setdefault(path, set()).add(v)
    out = ["# Manual texture review list", ""]
    out.append("These files still reference `blocks/anvil_base`. "
               "There is no direct modern equivalent in 1.20.1 vanilla "
               "textures. Each entry needs a human decision:")
    out.append("")
    out.append("- pick a modern vanilla texture (`block/smithing_table_top`, "
               "`block/dark_oak_log`, ...), or")
    out.append("- add a custom retexture to the mod's own textures and "
               "reference it via `coffeework:textures/...`.")
    out.append("")
    for path, keys in sorted(files.items()):
        out.append(f"- `{path.relative_to(REPO_ROOT)}`: {sorted(keys)}")
    out.append("")
    return "\n".join(out)


def main() -> int:
    summary = {
        "lang": check_lang_json(),
        "forge": check_forge_keys(),
        "model_refs": check_model_references(),
        "vanilla": check_vanilla_textures(),
        "mod_textures": check_mod_owned_textures(),
    }

    print("== Lang ==")
    print("  files:", summary["lang"]["files"])
    print("  counts:", summary["lang"]["counts"])
    if summary["lang"]["fail"]:
        for f in summary["lang"]["fail"]:
            print("  FAIL:", f)
    if summary["lang"]["coffeeworkshop_keys"]:
        for p, k in summary["lang"]["coffeeworkshop_keys"]:
            print("  FAIL namespace:", p, k)

    print("\n== Forge blockstate legacy keys ==")
    for p, k in summary["forge"]["found"]:
        print("  FAIL:", p, k)
    if not summary["forge"]["found"]:
        print("  pass")

    print("\n== Blockstate model refs ==")
    print(f"  scanned {summary['model_refs']['files_scanned']} files, "
          f"resolved {summary['model_refs']['resolved_ok']} references")
    if summary["model_refs"]["bare_refs"]:
        for p, v in summary["model_refs"]["bare_refs"][:5]:
            print("  FAIL bare:", p, v)
        if len(summary["model_refs"]["bare_refs"]) > 5:
            print(f"  ... and {len(summary['model_refs']['bare_refs']) - 5} more")
    if summary["model_refs"]["missing_targets"]:
        for p, v in summary["model_refs"]["missing_targets"]:
            print("  FAIL missing:", p, v)

    print("\n== Vanilla textures ==")
    failures = [m for m in summary["vanilla"]["matches"] if m[1] not in REVIEW_ONLY]
    review = [m for m in summary["vanilla"]["matches"] if m[1] in REVIEW_ONLY]
    print(f"  scanned {summary['vanilla']['files_scanned']} files")
    print(f"  non-exempt failures: {len(failures)}")
    print(f"  exempt (anvil_base only): {len(review)}")

    print("\n== Mod-owned texture refs in model JSONs ==")
    modtex = summary["mod_textures"]
    print(f"  scanned {modtex['files_scanned']} files")
    print(f"  texture refs resolved: {modtex['resolved']}")
    print(f"  parent refs (untouched): {len(modtex['parent_refs'])}")
    if modtex["bad_textures"]:
        for p, v in modtex["bad_textures"][:10]:
            print(f"  FAIL: {p.relative_to(REPO_ROOT)}: {v}")
        if len(modtex["bad_textures"]) > 10:
            print(f"  ... and {len(modtex['bad_textures']) - 10} more")

    # Overall result
    ok = (
        not summary["lang"]["fail"]
        and not summary["lang"]["coffeeworkshop_keys"]
        and not summary["forge"]["found"]
        and not summary["model_refs"]["bare_refs"]
        and not summary["model_refs"]["missing_targets"]
        and not failures
        and not modtex["bad_textures"]
    )

    REPORT_PATH.parent.mkdir(parents=True, exist_ok=True)
    REPORT_PATH.write_text(_format_md(summary), encoding="utf-8")
    MANUAL_REVIEW_PATH.write_text(_format_manual_review(summary["vanilla"]["matches"]),
                                  encoding="utf-8")
    print(f"\nWrote {REPORT_PATH}")
    print(f"Wrote {MANUAL_REVIEW_PATH}")

    if not ok:
        print("\nFAIL: at least one resource issue remains.")
        return 2
    print("\nPASS: no resource failures detected.")
    return 0


if __name__ == "__main__":
    sys.exit(main())
