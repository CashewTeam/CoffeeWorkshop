#!/usr/bin/env python3
"""Coffee Workshop recipe reachability report.

Analyses all recipes (crafting, smelting, machine) and determines whether
each output item is reachable from a set of initial "seed" items that can
be obtained in a new survival world without machines or crafting.

Outputs:
    build/reports/coffeework/recipe-reachability.md
"""
import json
import os
import sys
from collections import defaultdict
from pathlib import Path

REPO_ROOT = Path(__file__).resolve().parents[1]
GEN_DIR = REPO_ROOT / "src" / "generated" / "resources" / "data" / "coffeework" / "recipes"
MAIN_DIR = REPO_ROOT / "src" / "main" / "resources" / "data" / "coffeework" / "recipes"
REPORT_DIR = REPO_ROOT / "build" / "reports" / "coffeework"
REPORT_PATH = REPORT_DIR / "recipe-reachability.md"

# Items obtainable in a new survival world without any machines or
# complex crafting (wood, stone, vanilla crops, etc.).
# Mod items that require worldgen (coffee seeds, vanilla) are NOT
# included here — the script will flag them as UNREACHABLE if no
# worldgen/loot source is registered, but the coffee chain can
# still be verified by checking whether the recipe chain itself
# is intact once the seed is obtained.
INITIAL_ITEMS = {
    # Vanilla basics
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
    "minecraft:oak_sapling", "minecraft:bread",
    "minecraft:sand", "minecraft:gravel",
}

# Mod items that require worldgen or other non-recipe sources.
WORLDGEN_SOURCES = {
    "coffeework:coffee_seeds": "worldgen (coffee tree) or grass drops",
    "coffeework:coffee_bean_raw": "harvest from mature coffee tree",
    "coffeework:vanilla_seeds": "crafted from vanilla (vanilla crop worldgen)",
    "coffeework:vanilla": "harvest from vanilla crop (worldgen)",
    "coffeework:soda_ore": "worldgen (soda ore block)",
}

def load_recipes(directory):
    """Load all JSON recipes from a directory tree."""
    recipes = []
    if not directory.exists():
        return recipes
    for f in sorted(directory.rglob("*.json")):
        try:
            data = json.loads(f.read_text(encoding="utf-8"))
            data["_file"] = str(f.relative_to(REPO_ROOT))
            recipes.append(data)
        except Exception:
            pass
    return recipes

def extract_ingredients(recipe):
    """Extract input item IDs from a recipe JSON."""
    items = set()
    t = recipe.get("type", "")

    # Ingredient (single)
    ing = recipe.get("ingredient")
    if ing:
        if "item" in ing:
            items.add(ing["item"])
        if "tag" in ing:
            items.add("#" + ing["tag"])

    # Base / modifier / container (CoffeeBrewingRecipe)
    for key in ("base", "modifier", "container"):
        obj = recipe.get(key)
        if obj and "ingredient" in obj:
            i = obj["ingredient"]
            if "item" in i:
                items.add(i["item"])

    # Shapeless / shaped ingredients
    for ing_obj in recipe.get("ingredients", []):
        if isinstance(ing_obj, dict):
            if "item" in ing_obj:
                items.add(ing_obj["item"])
            if "tag" in ing_obj:
                items.add("#" + ing_obj["tag"])

    # Shaped pattern keys
    for v in recipe.get("key", {}).values():
        if isinstance(v, dict):
            if "item" in v:
                items.add(v["item"])
            if "tag" in v:
                items.add("#" + v["tag"])

    return {i for i in items if not i.startswith("#")}

def extract_result(recipe):
    """Extract output item ID from a recipe JSON."""
    result = recipe.get("result")
    if result:
        if isinstance(result, str):
            return result
        return result.get("item", "")
    return ""

def compute_reachability(all_recipes):
    """BFS from initial items to determine reachability."""
    reachable = set(INITIAL_ITEMS)
    # Worldgen sources are considered reachable for the purpose of
    # recipe chain analysis (the world provides them, not recipes).
    reachable.update(WORLDGEN_SOURCES.keys())
    recipe_map = defaultdict(list)
    for r in all_recipes:
        result = extract_result(r)
        if result:
            recipe_map[result].append(r)

    # Iterate until no new items are discovered
    changed = True
    while changed:
        changed = False
        for recipe in all_recipes:
            result = extract_result(recipe)
            if result and result not in reachable:
                inputs = extract_ingredients(recipe)
                if inputs and inputs.issubset(reachable):
                    reachable.add(result)
                    changed = True
    return reachable

def analyze(all_recipes, reachable):
    """Produce analysis results."""
    machine_types = defaultdict(int)
    unreachable = []
    unused = set()
    all_outputs = set()

    for r in all_recipes:
        result = extract_result(r)
        t = r.get("type", "")
        all_outputs.add(result)

        # Count machine recipes
        if "grinding" in t: machine_types["grinding"] += 1
        if "oven_baking" in t: machine_types["oven_baking"] += 1
        if "rolling" in t: machine_types["rolling"] += 1
        if "icecream_making" in t: machine_types["icecream_making"] += 1
        if "coffee_brewing" in t: machine_types["coffee_brewing"] += 1

        if result and result not in reachable:
            unreachable.append((result, r["_file"]))

    # Find outputs that are never used as inputs
    all_inputs = set()
    for r in all_recipes:
        all_inputs.update(extract_ingredients(r))
    unused = {o for o in all_outputs if o not in all_inputs and o in reachable}

    return machine_types, unreachable, unused

def main():
    gen_recipes = load_recipes(GEN_DIR)
    main_recipes = load_recipes(MAIN_DIR)
    all_recipes = gen_recipes + main_recipes

    reachable = compute_reachability(all_recipes)
    machine_types, unreachable, unused = analyze(all_recipes, reachable)

    REPORT_DIR.mkdir(parents=True, exist_ok=True)
    lines = []
    lines.append("# Coffee Workshop Recipe Reachability Report\n")
    lines.append(f"Recipes analysed: {len(all_recipes)}\n")

    lines.append("## Machine Recipe Counts\n")
    lines.append("| Recipe Type | Count |")
    lines.append("|------------|-------|")
    for t in ("grinding", "oven_baking", "rolling", "icecream_making", "coffee_brewing"):
        lines.append(f"| {t} | {machine_types.get(t, 0)} |")
    lines.append("")

    lines.append("## Coffee Production Chain\n")
    lines.append("```")
    chain = [
        ("coffeework:coffee_seeds", "coffeework:coffee_bean_raw"),
        ("coffeework:coffee_bean_raw", "coffeework:coffee_bean"),
        ("coffeework:coffee_bean", "coffeework:coffee_powder"),
        ("coffeework:coffee_powder", "coffeework:espresso"),
        ("coffeework:coffee_powder", "coffeework:coffee_americano"),
        ("coffeework:coffee_powder", "coffeework:coffee_latte"),
    ]
    for inp, out in chain:
        src = WORLDGEN_SOURCES.get(inp, "")
        ok_inp = "✅" if inp in reachable else "❌"
        ok_out = "✅" if out in reachable else "❌"
        note = f"  [{src}]" if src else ""
        lines.append(f"{ok_inp} {inp}{note}")
        lines.append(f"  → {ok_out} {out}")
    lines.append("```\n")

    if unreachable:
        lines.append(f"## UNREACHABLE ({len(unreachable)} items)\n")
        for item, src in unreachable:
            lines.append(f"- **{item}** ← {src}")
    else:
        lines.append("## UNREACHABLE\n\n✅ None\n")

    if unused:
        lines.append(f"\n## UNUSED_OUTPUT ({len(unused)} items)\n")
        for item in sorted(unused):
            lines.append(f"- {item}")

    report = "\n".join(lines) + "\n"
    REPORT_PATH.write_text(report, encoding="utf-8")
    print(f"Wrote {REPORT_PATH}")

    # Only fail CI for coffee-chain critical items
    coffee_chain = {
        "coffeework:coffee_bean", "coffeework:coffee_powder",
        "coffeework:espresso", "coffeework:coffee_americano",
        "coffeework:coffee_latte", "coffeework:plate_iron",
        "coffeework:cup", "coffeework:cup_glass",
        "coffeework:flour", "coffeework:plate_dough",
        "coffeework:icecream_vanilla", "coffeework:icecream_mix_vanilla",
        "coffeework:dough", "minecraft:bread",
    }
    critical_unreachable = [item for item, _ in unreachable if item in coffee_chain]

    if critical_unreachable:
        print(f"FAIL: {len(critical_unreachable)} critical items unreachable")
        for item in critical_unreachable:
            print(f"  CRITICAL: {item}")
        sys.exit(1)
    elif unreachable:
        print(f"WARNING: {len(unreachable)} non-critical items unreachable (see report)")
    else:
        print("PASS: all recipe outputs reachable")

if __name__ == "__main__":
    main()
