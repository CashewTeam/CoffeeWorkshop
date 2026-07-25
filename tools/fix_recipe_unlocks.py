#!/usr/bin/env python3
"""
Re-fix ModRecipeProvider to apply the unlock mapping table correctly.

The previous version of this script had a bug where it checked
`ns == "ModItems"` but `ns` was actually the item name (e.g. "DOUGH"),
not the namespace ("ModItems").  As a result every recipe fell through
to the `Items.IRON_INGOT` fallback.

This version uses the result item name directly, matching the auditor's
suggested pattern:

    result_name = re.search(r"([A-Z_]+)\.get\(\)", result).group(1)
    unlock = UNLOCK_BY_RESULT.get(result_name, "Items.IRON_INGOT")
"""
from __future__ import annotations

import re
from pathlib import Path

REPO_ROOT = Path(__file__).resolve().parents[1]
FILE = REPO_ROOT / "src/main/java/net/langball/coffee/datagen/ModRecipeProvider.java"

# ---------------------------------------------------------------------------
# Mapping: result item name → unlock ItemLike expression
# ---------------------------------------------------------------------------
UNLOCK_BY_RESULT = {
    # Tools / utility (player acquires iron → unlocks tool recipes)
    "EMPTY_COLDBREW_POT": "ModItems.PLATE_IRON.get()",
    "CAKE_MODEL":            "Items.IRON_INGOT",
    "MIXING_BOWL":           "Items.IRON_INGOT",
    "SMALL_MODEL":           "Items.IRON_INGOT",
    "IRON_BOWL":             "Items.IRON_INGOT",
    "MOONCAKE_MODEL":        "Items.IRON_INGOT",
    "CAKE_MODEL_PLATE":      "ModItems.PLATE_IRON.get()",
    "CAKE_MODEL_SQUARE":     "ModItems.CAKE_MODEL_PLATE.get()",
    "SYRUP_EMPTY":           "Items.GLASS_PANE",
    "BAG_CLOTH":             "Items.STRING",
    "BAG":                   "ModItems.BAG_CLOTH.get()",
    "PLATE":                 "Items.TERRACOTTA",
    "GINGER_HOUSE":          "ModItems.DOUGH_GINGER.get()",
    "VANILLA_SEEDS":         "ModItems.VANILLA.get()",

    # Ingredients (dough chain → flour, then dough)
    "DOUGH":          "ModItems.FLOUR.get()",
    "DOUGH_PASTRY":   "ModItems.DOUGH.get()",
    "DOUGH_GINGER":   "ModItems.DOUGH.get()",
    "DOUGH_BREAD":    "ModItems.DOUGH.get()",
    "DOUGH_BREAD_ROUND": "ModItems.DOUGH.get()",
    "DOUGH_BAGUETTE": "ModItems.DOUGH.get()",
    "DOUGH_BAGEL":    "ModItems.DOUGH.get()",
    "DOUGH_TOAST":    "ModItems.DOUGH.get()",
    "DOUGH_COOKIE":   "ModItems.DOUGH.get()",
    "YEAST":          "ModItems.MIXING_BOWL.get()",
    "BUTTER":         "ModItems.MIXING_BOWL.get()",
    "CHEESE":         "ModItems.MIXING_BOWL.get()",
    "SPICES":         "Items.COCOA_BEANS",
    "GELATIN":        "Items.SLIME_BALL",
    "CHOCOLATE_BAR":  "ModItems.COCOA_BATTER.get()",
    "FIELD_RATION":   "ModItems.COCOA_BATTER.get()",
    "BROWNIE":        "ModItems.CAKE_MODEL_SQUARE.get()",
    "SANDWICH_BLT":   "Items.BREAD",

    # Drinks & misc
    "COFFEE_INSTANT":       "ModItems.COFFEE_INSTANT_BOX.get()",
    "COLD_BREW_POT":        "ModItems.EMPTY_COLDBREW_POT.get()",
    "RECORD_BLANK":         "ModItems.PLATE_IRON.get()",
    "RECORD_KUSA_NOSHI_TO_NE":    "ModItems.RECORD_BLANK.get()",
    "RECORD_LAZY_LADY_KAGUYA":    "ModItems.RECORD_BLANK.get()",
    "RECORD_THE_GRIMOIRE_OF_MARISA": "ModItems.RECORD_BLANK.get()",
    "XMAS_TREE":            "Items.SPRUCE_SAPLING",

    # Cake sponges (unlock with EGG — player finds eggs very early)
    "CAKE_SPONGE":           "Items.EGG",
    "CAKE_SPONGE_CHOCOLATE": "Items.EGG",
    "CAKE_SPONGE_COFFEE":    "Items.EGG",
    "CAKE_SPONGE_PUMPKIN":   "Items.EGG",
    "CAKE_SPONGE_CARROT":    "Items.EGG",
    "CAKE_SPONGE_REDVELVET": "Items.EGG",
    "CAKE_SPONGE_LEMON":     "Items.EGG",
    "CAKE_SPONGE_TEA":       "Items.EGG",
    "CAKE_SPONGE_BERRY":     "Items.EGG",

    # Finished cakes (unlock with corresponding sponge)
    "CAKE_COFFEE":         "ModBlocks.CAKE_SPONGE_COFFEE.get()",
    "CAKE_HARVEST":        "ModBlocks.CAKE_SPONGE_PUMPKIN.get()",
    "CAKE_BERRY":          "ModBlocks.CAKE_SPONGE_BERRY.get()",
    "CAKE_LEMON":          "ModBlocks.CAKE_SPONGE_LEMON.get()",
    "CAKE_TEA":            "ModBlocks.CAKE_SPONGE_TEA.get()",
    "CAKE_CHEESE":         "ModItems.CAKE_MODEL.get()",
    "CAKE_SCHWARZWALD":    "ModBlocks.CAKE_SPONGE_CHOCOLATE.get()",
    "CAKE_REDVELVET":      "ModBlocks.CAKE_SPONGE_REDVELVET.get()",
    "TIRAMISU":            "ModBlocks.CAKE_SPONGE.get()",
    "MOUSSE_BERRY":        "ModItems.CAKE_MODEL.get()",
    "MOUSSE_LEMON":        "ModItems.CAKE_MODEL.get()",
    "MOUSSE_CHOCOLATE":    "ModItems.CAKE_MODEL.get()",
    "MOUSSE_COFFEE":       "ModItems.CAKE_MODEL.get()",
    "PIE_CREAM":           "ModItems.PLATE_DOUGH_PASTRY.get()",
    "ICECREAM_VANILLA":    "Items.MILK_BUCKET",
}


def apply_mapping(content: str) -> tuple[str, int]:
    """Replace Items.IRON_INGOT in shapeless/shaped calls with the
    proper unlock item from the mapping table."""
    changes = 0

    # Matches:   shapeless(RecipeCategory.XXX, ModItems.RESULT.get(), Items.IRON_INGOT)
    #          or shapeless(RecipeCategory.XXX, ModItems.RESULT.get(), <int>, Items.IRON_INGOT)
    # Group: 1=indent  2=fn  3=cat  4=result  5=opt_count  6=IRON
    pat = re.compile(
        r"^(\s*)(shapeless|shaped)\(RecipeCategory\.([A-Z_]+),\s*"
        r"([A-Za-z_][A-Za-z0-9_.]*(?:\.[A-Za-z_][A-Za-z0-9_]*)*\.get\(\))\s*,"
        r"(\s*\d+\s*,)?\s*"
        r"Items\.IRON_INGOT\s*\)"
        r"(.*)",
        re.MULTILINE,
    )

    def replace(m: re.Match) -> str:
        nonlocal changes
        indent = m.group(1)
        fn = m.group(2)            # shapeless | shaped
        cat = m.group(3)           # MISC / FOOD / DECORATIONS
        result = m.group(4)        # ModItems.DOUGH.get() etc.
        count_part = m.group(5) or ""
        rest = m.group(6) or ""

        # Extract the bare item name from e.g. "ModItems.DOUGH.get()"
        name_m = re.search(r"([A-Z_]+)\.get\(\)", result)
        if not name_m:
            return m.group(0)       # shouldn't happen; leave unchanged
        item_name = name_m.group(1)

        unlock = UNLOCK_BY_RESULT.get(item_name)
        if unlock is None:
            # Keep Items.IRON_INGOT for items not in the table
            return m.group(0)

        changes += 1
        trailing = f",{count_part} {unlock}){rest}" if count_part.strip() else f", {unlock}){rest}"
        return f"{indent}{fn}(RecipeCategory.{cat}, {result}{trailing}"

    return pat.sub(replace, content), changes


def main() -> int:
    original = FILE.read_text(encoding="utf-8")
    updated, changes = apply_mapping(original)
    if changes == 0:
        print("No changes needed.")
        return 0
    FILE.write_text(updated, encoding="utf-8")
    print(f"Updated {FILE} — {changes} recipe(s) now use correct unlock item.")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
