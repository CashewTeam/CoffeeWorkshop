"""
Diagnostic: enumerate every coffeework: namespace string used inside
models/block/*.json and models/item/*.json; for each, attempt to
resolve under two candidate paths:

    naive      = assets/coffeework/<tail>.png
    with-textures = assets/coffeework/textures/<tail>.png

References that resolve only under `textures/` are clearly missing
the leading `textures/` segment. References that resolve under neither
are genuinely missing textures (need either a texture or a model fix).

This is the audit script the porting plan was missing.
"""
from __future__ import annotations
import json
from pathlib import Path
from collections import Counter, defaultdict

NS = "coffeework"
ASSETS = Path("src/main/resources/assets/coffeework")
MODEL_DIRS = [
    ASSETS / "models" / "block",
    ASSETS / "models" / "item",
]


def walk(n, out):
    if isinstance(n, dict):
        for v in n.values():
            walk(v, out)
    elif isinstance(n, list):
        for v in n:
            walk(v, out)
    elif isinstance(n, str) and n.startswith(NS + ":"):
        out.append(n)


def collect(p: Path) -> list[str]:
    out: list[str] = []
    try:
        data = json.loads(p.read_text(encoding="utf-8"))
    except json.JSONDecodeError:
        return out
    walk(data, out)
    return out


def main() -> int:
    refs = Counter()
    for d in MODEL_DIRS:
        for p in d.glob("*.json"):
            for r in collect(p):
                refs[r] += 1

    cat_total = Counter()
    cat_unique = defaultdict(set)
    cat_resolved = defaultdict(int)
    cat_broken_unique = defaultdict(set)
    cat_broken_total = defaultdict(int)
    needs_textures_only = []  # resolves only under textures/

    for ref, c in refs.items():
        tail = ref[len(NS) + 1:]
        naive = ASSETS / (tail + ".png")
        tex = ASSETS / "textures" / (tail + ".png")
        cat = tail.split("/")[0]
        cat_total[cat] += c
        cat_unique[cat].add(ref)
        if naive.exists() or tex.exists():
            cat_resolved[cat] += c
            if not naive.exists() and tex.exists():
                needs_textures_only.append((ref, c))
        else:
            cat_broken_unique[cat].add(ref)
            cat_broken_total[cat] += c

    print(f"Total unique coffeework: refs : {len(refs)}")
    print(f"Total coffeework: references: {sum(refs.values())}")
    print()
    print("=== Per-category resolution ===")
    print(f"{'category':<14} {'unique':>7} {'total':>7} {'broken_unique':>14} {'broken_total':>14}")
    for cat in sorted(cat_total, key=lambda x: -cat_total[x]):
        u = len(cat_unique[cat])
        t = cat_total[cat]
        bu = len(cat_broken_unique[cat])
        bt = cat_broken_total[cat]
        print(f"coffeework:{cat:<7} {u:>7} {t:>7} {bu:>14} {bt:>14}")

    print()
    print("=== Reference totals needing textures/ prefix (resolved only with textures/) ===")
    by_cat = defaultdict(list)
    for ref, c in needs_textures_only:
        cat = ref[len(NS) + 1:].split("/")[0]
        by_cat[cat].append((ref, c))
    for cat in sorted(by_cat, key=lambda x: -sum(c for _, c in by_cat[x])):
        total = sum(c for _, c in by_cat[cat])
        print(f"\n  coffeework:{cat}/... ({total} refs across {len(by_cat[cat])} unique):")
        for ref, c in by_cat[cat]:
            print(f"    {c:>4}  {ref}")

    print()
    print("=== Genuinely broken (no PNG anywhere) ===")
    for cat in sorted(cat_broken_total, key=lambda x: -cat_broken_total[x]):
        print(f"\n  coffeework:{cat}/... ({cat_broken_total[cat]} refs across {len(cat_broken_unique[cat])} unique):")
        for ref in sorted(cat_broken_unique[cat]):
            print(f"    {refs[ref]:>4}  {ref}")


if __name__ == "__main__":
    main()
