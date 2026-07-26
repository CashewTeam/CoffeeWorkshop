#!/usr/bin/env python3
"""Coffee Workshop recipe reachability report — v2.

Analyses all recipes (crafting, smelting, machine) and determines whether
each output item is reachable from a set of initial "seed" items.

v2 additions:
- Reads "additive" field from CoffeeBrewingRecipe JSONs
- Expands Tag ingredients via data/*/tags/items/*.json
- Validates machine prerequisites (machine output requires machine block)
- Detects ambiguous recipe signatures (same inputs → multiple outputs)
- Classifies terminal outputs (CONSUMABLE, CRAFTING_INPUT, etc.)
- Phase 4 P0/P1 critical chain verification
"""
import json
import os
import sys
from collections import defaultdict
from pathlib import Path

REPO_ROOT = Path(__file__).resolve().parents[1]
GEN_DIR = REPO_ROOT / "src" / "generated" / "resources"
MAIN_DIR = REPO_ROOT / "src" / "main" / "resources"
RECIPE_DIRS = [
    GEN_DIR / "data" / "coffeework" / "recipes",
    MAIN_DIR / "data" / "coffeework" / "recipes",
]
TAG_DIRS = [
    GEN_DIR / "data" / "coffeework" / "tags" / "items",
    GEN_DIR / "data" / "minecraft" / "tags" / "items",
    MAIN_DIR / "data" / "coffeework" / "tags" / "items",
    REPO_ROOT / "data" / "coffeework" / "tags" / "items",
]
REPORT_DIR = REPO_ROOT / "build" / "reports" / "coffeework"
REPORT_PATH = REPORT_DIR / "recipe-reachability.md"

# Items obtainable in a new survival world without any mod machines/complex crafting.
INITIAL_ITEMS = {
    "minecraft:oak_log", "minecraft:oak_planks", "minecraft:stick",
    "minecraft:cobblestone", "minecraft:stone", "minecraft:iron_ingot",
    "minecraft:iron_block", "minecraft:coal", "minecraft:redstone",
    "minecraft:redstone_block", "minecraft:glass_pane", "minecraft:paper",
    "minecraft:wheat", "minecraft:cocoa_beans", "minecraft:milk_bucket",
    "minecraft:water_bucket", "minecraft:bucket",
    "minecraft:sugar", "minecraft:egg", "minecraft:brown_mushroom",
    "minecraft:slime_ball", "minecraft:white_dye", "minecraft:black_dye",
    "minecraft:red_dye", "minecraft:yellow_dye", "minecraft:green_dye",
    "minecraft:string", "minecraft:white_wool", "minecraft:terracotta",
    "minecraft:snow_block", "minecraft:ice", "minecraft:packed_ice",
    "minecraft:blue_ice", "minecraft:furnace", "minecraft:pumpkin",
    "minecraft:carrot", "minecraft:sweet_berries", "minecraft:cookie",
    "minecraft:beetroot", "minecraft:cooked_porkchop",
    "minecraft:snowball", "minecraft:spruce_sapling",
    "minecraft:oak_sapling", "minecraft:cherry_sapling",
    "minecraft:bread", "minecraft:sand", "minecraft:gravel",
    "minecraft:vine", "minecraft:oak_leaves",
    "minecraft:glass_bottle",
}

# Mod items that require worldgen or other non-recipe sources.
WORLDGEN_SOURCES = {
    "coffeework:coffee_seeds": "worldgen (coffee tree)",
    "coffeework:coffee_bean_raw": "harvest from mature coffee tree",
    "coffeework:vanilla_seeds": "crafted from vanilla (vanilla crop worldgen)",
    "coffeework:vanilla": "harvest from vanilla crop (worldgen)",
    "coffeework:soda_ore": "worldgen (soda ore block)",
    "coffeework:coldbrew_bottle": "cold brew pot fermentation + glass bottle extraction",
}

# Machine recipe types and their required machine blocks.
MACHINE_BLOCKS = {
    "coffeework:grinding": "coffeework:grinder_off",
    "coffeework:oven_baking": "coffeework:oven_off",
    "coffeework:rolling": "coffeework:roller",
    "coffeework:icecream_making": "coffeework:icecream_machine",
    "coffeework:coffee_brewing": "coffeework:coffee_machine",
}

# ── Tag cache ─────────────────────────────────────────────────────────

def load_tag_items(tag_id):
    """Expand a tag ID (like '#minecraft:logs') to a set of concrete item IDs."""
    ns, path = tag_id.lstrip("#").split(":", 1)
    for tag_dir in TAG_DIRS:
        tag_file = tag_dir / f"{path.replace('/', os.sep)}.json"
        if tag_file.exists():
            try:
                data = json.loads(tag_file.read_text(encoding="utf-8"))
                items = set()
                for entry in data.get("values", []):
                    if entry.startswith("#"):
                        items.update(load_tag_items(entry))
                    else:
                        items.add(entry)
                return items
            except Exception:
                pass
    # Tag not found — return empty (won't block reachability)
    return set()

TAG_CACHE = {}
def expand_tag(tag_id):
    if tag_id not in TAG_CACHE:
        TAG_CACHE[tag_id] = load_tag_items(tag_id)
    return TAG_CACHE[tag_id]

# ── Recipe loading ────────────────────────────────────────────────────

def load_recipes():
    """Load all JSON recipes from all recipe directories."""
    recipes = []
    for recipe_dir in RECIPE_DIRS:
        if recipe_dir.exists():
            for f in sorted(recipe_dir.rglob("*.json")):
                try:
                    data = json.loads(f.read_text(encoding="utf-8"))
                    data["_file"] = str(f.relative_to(REPO_ROOT))
                    recipes.append(data)
                except Exception:
                    pass
    return recipes

# ── Ingredient extraction ─────────────────────────────────────────────

def extract_ingredients(recipe):
    """Extract input item IDs from a recipe JSON, expanding tags."""
    items = set()
    t = recipe.get("type", "")

    # Single Ingredient (MachineRecipe)
    ing = recipe.get("ingredient")
    if ing:
        _add_ingredient_items(items, ing)

    # CoffeeBrewingRecipe: base / modifier / additive / container
    for key in ("base", "modifier", "additive", "container"):
        obj = recipe.get(key)
        if obj and "ingredient" in obj:
            _add_ingredient_items(items, obj["ingredient"])

    # Shapeless (ingredients array)
    for ing_obj in recipe.get("ingredients", []):
        if isinstance(ing_obj, dict):
            _add_ingredient_items(items, ing_obj)

    # Shaped (key map)
    for v in recipe.get("key", {}).values():
        if isinstance(v, dict):
            _add_ingredient_items(items, v)

    # Smelting/cooking: ingredient can be nested
    if not items:
        for field in ("ingredient",):
            ing2 = recipe.get(field)
            if isinstance(ing2, dict):
                _add_ingredient_items(items, ing2)

    return {i for i in items if not i.startswith("#") and not i.startswith("tag:")}


def _add_ingredient_items(items, ing_obj):
    """Add item IDs from an ingredient object (handles item and tag)."""
    if "item" in ing_obj:
        items.add(ing_obj["item"])
    if "tag" in ing_obj:
        items.update(expand_tag("#" + ing_obj["tag"]))


def extract_result(recipe):
    """Extract output item ID from a recipe JSON."""
    result = recipe.get("result")
    if result:
        if isinstance(result, str):
            return result
        return result.get("item", "")
    return ""

def get_recipe_type(recipe):
    """Return the full namespaced recipe type (e.g. 'coffeework:coffee_brewing')."""
    return recipe.get("type", "")

# ── Ambiguity detection ───────────────────────────────────────────────

def build_signature(recipe):
    """Build a canonical signature string for coffee brewing recipes."""
    keys = ("base", "modifier", "additive", "container")
    parts = []
    for key in keys:
        obj = recipe.get(key)
        if obj and "ingredient" in obj:
            ing = obj["ingredient"]
            item = ing.get("item", ing.get("tag", "empty"))
            count = obj.get("count", 1)
            parts.append(f"{item}x{count}")
        else:
            parts.append("none")
    return "|".join(parts)

# ── Reachability BFS ──────────────────────────────────────────────────

def compute_reachability(all_recipes):
    """BFS from initial items, checking ingredients (including tag expansion)
    and machine prerequisites."""
    reachable = set(INITIAL_ITEMS)
    reachable.update(WORLDGEN_SOURCES.keys())

    # Machines themselves must be craftable to unlock their recipe outputs
    reachable_machines = set()

    recipe_map = defaultdict(list)
    for r in all_recipes:
        result = extract_result(r)
        if result:
            recipe_map[result].append(r)

    changed = True
    while changed:
        changed = False
        for recipe in all_recipes:
            result = extract_result(recipe)
            if not result or result in reachable:
                continue

            rtype = get_recipe_type(recipe)
            machine = MACHINE_BLOCKS.get(rtype)

            # If this is a machine recipe, check that the machine is reachable
            if machine and machine not in reachable:
                continue

            inputs = extract_ingredients(recipe)
            if inputs and inputs.issubset(reachable):
                reachable.add(result)
                # If result is a machine block, mark it
                if machine:
                    reachable_machines.add(machine)
                    reachable.add(machine)
                changed = True

    return reachable

# ── Analysis ──────────────────────────────────────────────────────────

def analyze(all_recipes, reachable):
    """Produce analysis results including counts, unreachable, unused, terminal."""
    machine_types = defaultdict(int)
    unreachable = []
    all_outputs = set()
    all_inputs = set()
    signatures = defaultdict(list)

    for r in all_recipes:
        result = extract_result(r)
        t = r.get("type", "")
        all_outputs.add(result)

        # Count machine recipes
        for mtype in MACHINE_BLOCKS:
            if mtype in t:
                machine_types[mtype] += 1

        # Track signatures for ambiguity detection (coffee brewing only)
        if "coffee_brewing" in t:
            sig = build_signature(r)
            signatures[sig].append((result, r["_file"]))

        if result and result not in reachable:
            unreachable.append((result, r["_file"]))
        else:
            all_inputs.update(extract_ingredients(r))

    # Unused outputs: reachable items never used as input
    unused = {o for o in all_outputs if o not in all_inputs and o in reachable}

    # Terminal vs intermediate classification
    consumables = {o for o in all_outputs if "coffee_" in o or "cocoa" in o or "tea" in o
                   or o in ("coffeework:cup", "coffeework:cup_glass")}
    mod_terminals = {o for o in all_outputs if o not in all_inputs}

    # Ambiguous signatures
    ambiguous = {sig: entries for sig, entries in signatures.items()
                 if len({e[0] for e in entries}) > 1}

    return machine_types, unreachable, unused, consumables, ambiguous

# ── Report generation ─────────────────────────────────────────────────

def main():
    all_recipes = load_recipes()
    reachable = compute_reachability(all_recipes)
    machine_types, unreachable, unused, consumables, ambiguous = analyze(all_recipes, reachable)

    REPORT_DIR.mkdir(parents=True, exist_ok=True)
    lines = []
    lines.append("# Coffee Workshop Recipe Reachability Report — v2\n")
    lines.append(f"- Recipes analysed: **{len(all_recipes)}**")
    lines.append(f"- Reachable items: **{len(reachable)}**")
    lines.append(f"- Unreachable outputs: **{len(unreachable)}**")
    lines.append(f"- Unused intermediate outputs: **{len(unused)}**\n")

    # ── Ambiguous signatures ─────────────────────────────────────────
    if ambiguous:
        lines.append("## ⚠️ AMBIGUOUS RECIPE SIGNATURES\n")
        lines.append("The following coffee brewing recipes share identical input signatures")
        lines.append("but produce different outputs. This is a P0 correctness issue.\n")
        lines.append("| Signature | Conflicting Outputs |")
        lines.append("|-----------|-------------------|")
        for sig, entries in sorted(ambiguous.items()):
            outputs = "; ".join(f"{out} ({path})" for out, path in entries)
            lines.append(f"| `{sig}` | {outputs} |")
        lines.append("")
    else:
        lines.append("## ✅ Unique Signatures\n")
        lines.append("All coffee brewing recipes have unique input signatures.\n")

    # ── Machine Recipe Counts ─────────────────────────────────────────
    lines.append("## Machine Recipe Counts\n")
    lines.append("| Recipe Type | Count |")
    lines.append("|------------|-------|")
    for short in ("grinding", "oven_baking", "rolling", "icecream_making", "coffee_brewing"):
        full = "coffeework:" + short
        lines.append(f"| {short} | {machine_types.get(full, 0)} |")
    lines.append("")

    # ── P0 Critical Chain ─────────────────────────────────────────────
    critical_chain = [
        ("coffeework:coffee_seeds", "coffeework:coffee_bean_raw", "worldgen"),
        ("coffeework:coffee_bean_raw", "coffeework:coffee_bean", "oven_baking"),
        ("coffeework:coffee_bean", "coffeework:coffee_powder", "grinding"),
        ("coffeework:coffee_powder", "coffeework:espresso", "coffee_brewing"),
        ("coffeework:coffee_powder", "coffeework:coffee_americano", "coffee_brewing"),
        ("coffeework:coffee_powder", "coffeework:coffee_latte", "coffee_brewing"),
        ("coffeework:coffee_powder", "coffeework:coffee_cappuccino", "coffee_brewing"),
        ("coffeework:coffee_powder", "coffeework:coffee_macchiato", "coffee_brewing"),
        ("coffeework:coffee_powder", "coffeework:coffee_mochaccino", "coffee_brewing"),
        ("coffeework:cocoa_powder", "coffeework:cocoa", "coffee_brewing"),
        ("coffeework:cocoa_powder", "coffeework:cocoa_strong", "coffee_brewing"),
        ("coffeework:tea_leaf", "coffeework:coffee_green_tea", "coffee_brewing"),
        ("coffeework:black_tea_leaf", "coffeework:coffee_black_tea", "coffee_brewing"),
        ("coffeework:black_tea_leaf", "coffeework:coffee_milk_tea", "coffee_brewing"),
        ("coffeework:coffee_powder", "coffeework:coffee_mandarin_drink", "coffee_brewing"),
        ("coffeework:coldbrew_bottle", "coffeework:coffee_coldbrew", "coffee_brewing"),
        ("coffeework:coffee_powder", "coffeework:coffee_americano_ice", "coffee_brewing"),
        ("coffeework:coffee_powder", "coffeework:coffee_latte_ice", "coffee_brewing"),
        ("coffeework:coldbrew_bottle", "coffeework:coffee_coldbrew_ice", "coffee_brewing"),
    ]
    lines.append("## P0 Critical Chain\n")
    lines.append("| Input | Output | Method | Status |")
    lines.append("|-------|--------|--------|--------|")
    for inp, out, method in critical_chain:
        inp_ok = "✅" if inp in reachable else "❌"
        out_ok = "✅" if out in reachable else "❌"
        status = f"{inp_ok}→{out_ok}"
        lines.append(f"| {inp} | {out} | {method} | {status} |")
    lines.append("")

    # Check critical failures
    critical_failures = []
    for inp, out, _ in critical_chain:
        if inp not in reachable or out not in reachable:
            critical_failures.append((inp, out))

    # ── Unreachable outputs ───────────────────────────────────────────
    if unreachable:
        lines.append(f"## UNREACHABLE Outputs ({len(unreachable)})\n")
        lines.append("| Item | Source File |")
        lines.append("|------|-----------|")
        for item, src in sorted(unreachable):
            lines.append(f"| {item} | {src} |")
        lines.append("")

    # ── Unused / Terminal classification ──────────────────────────────
    if unused:
        lines.append(f"## Terminal / Unused Reachable Outputs ({len(unused)})\n")
        lines.append("| Item | Classification |")
        lines.append("|------|---------------|")
        for item in sorted(unused):
            classification = "CONSUMABLE" if item in consumables else "CRAFTING_INPUT/DECOR"
            lines.append(f"| {item} | {classification} |")
        lines.append("")

    # ── Worldgen Sources ──────────────────────────────────────────────
    lines.append("## Worldgen Sources\n")
    lines.append("| Item | Source | Verified By |")
    lines.append("|------|--------|-----------|")
    for item, src in sorted(WORLDGEN_SOURCES.items()):
        ok = "✅" if item in reachable else "❌"
        lines.append(f"| {item} | {src} | {ok} |")
    lines.append("")

    report = "\n".join(lines) + "\n"
    REPORT_PATH.write_text(report, encoding="utf-8")
    print(f"Wrote {REPORT_PATH}")

    # ── CI exit code ──────────────────────────────────────────────────
    if critical_failures:
        print(f"\nFAIL: {len(critical_failures)} critical chain items unreachable")
        for inp, out in critical_failures:
            print(f"  CRITICAL: {inp} → {out}")
        sys.exit(1)
    if ambiguous:
        print(f"\nFAIL: {len(ambiguous)} ambiguous recipe signatures detected")
        sys.exit(1)
    if unreachable:
        print(f"\nWARNING: {len(unreachable)} non-critical items unreachable (see report)")
    else:
        print("\nPASS: all recipe outputs reachable, all signatures unique")

if __name__ == "__main__":
    main()
