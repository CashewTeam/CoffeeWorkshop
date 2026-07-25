#!/usr/bin/env python3
"""
Convert legacy Minecraft Forge `.lang` files to modern `.json` language files.

Behavior:
  - UTF-8 with optional BOM (`utf-8-sig`).
  - Strips blank lines and lines starting with `#`.
  - For each remaining line, splits on the FIRST `=` into key/value.
  - Applies a namespace rewrite in keys:
        coffeeworkshop.*  ->  coffeework.*
  - For duplicate keys (later sources normally override earlier ones in
    Minecraft resource loading), keeps the LAST occurrence, matching the
    behavior of `LangMigrator` in Forge and of `runData` collapsing.
  - JSON output uses 2-space indent and `ensure_ascii=False`, mirroring the
    style of `pack.mcmeta`.

Usage:
  python tools/convert_lang.py --dry-run
  python tools/convert_lang.py --write

Exit codes:
  0  success
  2  parse error
  3  I/O error
"""
from __future__ import annotations

import argparse
import json
import sys
from pathlib import Path

REPO_ROOT = Path(__file__).resolve().parents[1]
LANG_DIR = REPO_ROOT / "src" / "main" / "resources" / "assets" / "coffeework" / "lang"

LANGS = ("en_us", "zh_cn", "ja_jp")

# Rewrite key `coffeeworkshop.*` -> `coffeework.*` (only in keys, not in values).
def _rewrite_key(key: str) -> str:
    return key.replace("coffeeworkshop.", "coffeework.")


def parse_lang(path: Path) -> tuple[dict[str, str], list[str]]:
    """Return (entries_last_wins, malformed_lines)."""
    entries: dict[str, str] = {}
    malformed: list[str] = []
    text = path.read_text(encoding="utf-8-sig")
    for lineno, raw in enumerate(text.splitlines(), start=1):
        line = raw.strip()
        if not line or line.startswith("#"):
            continue
        if "=" not in line:
            malformed.append(f"{path}:{lineno}: missing '=': {raw!r}")
            continue
        key, _, value = line.partition("=")
        key = _rewrite_key(key.strip())
        value = value.strip()
        if not key:
            malformed.append(f"{path}:{lineno}: empty key")
            continue
        # last-wins semantics
        entries[key] = value
    return entries, malformed


def render_json(entries: dict[str, str]) -> str:
    return json.dumps(entries, indent=2, ensure_ascii=False, sort_keys=False)


def main() -> int:
    ap = argparse.ArgumentParser()
    ap.add_argument("--dry-run", action="store_true",
                    help="Print per-file summary without writing output.")
    ap.add_argument("--write", action="store_true",
                    help="Write .json files alongside the .lang sources.")
    args = ap.parse_args()
    if not args.dry_run and not args.write:
        ap.print_help()
        return 1

    overall_ok = True
    for lang in LANGS:
        src = LANG_DIR / f"{lang}.lang"
        if not src.exists():
            print(f"  [MISSING] {src}", file=sys.stderr)
            overall_ok = False
            continue
        entries, malformed = parse_lang(src)
        print(f"\n--- {lang} ---")
        print(f"  source        : {src}")
        print(f"  unique keys   : {len(entries)}")
        if malformed:
            print("  WARN malformed lines:")
            for m in malformed:
                print(f"    {m}")
            overall_ok = False

        # show rewritten keys
        rewritten = 0
        # Cheap re-scan of the raw file just to count rewrites for reporting.
        raw = src.read_text(encoding="utf-8-sig")
        for raw_line in raw.splitlines():
            stripped = raw_line.strip()
            if stripped.startswith("#") or "=" not in stripped:
                continue
            k = stripped.partition("=")[0].strip()
            if "coffeeworkshop." in k:
                rewritten += 1
        if rewritten:
            print(f"  rewrote {rewritten} coffeeworkshop key(s) -> coffeework")

        if args.write:
            out = src.with_suffix(".json")
            out.write_text(render_json(entries) + "\n", encoding="utf-8")
            print(f"  wrote         : {out}  ({out.stat().st_size} bytes)")
        else:
            sample_keys = list(entries.keys())[:3]
            print(f"  sample keys   : {sample_keys}")
    return 0 if overall_ok else 2


if __name__ == "__main__":
    try:
        sys.exit(main())
    except OSError as exc:
        print(f"I/O error: {exc}", file=sys.stderr)
        sys.exit(3)
