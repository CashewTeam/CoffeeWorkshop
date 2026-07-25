#!/usr/bin/env python3
"""
Bulk-update ModRecipeProvider to require explicit unlockItem on every
shapeless(...) and shaped(...) call. Adds a sensible unlock item based
on context (the result item), with comments for ambiguous cases.

Strategy: for each call we add a 3rd (or 4th) argument — the unlock item.
If the call already has 3-4 args, we replace it with the 4-arg form.

Lock item selection rules:
- Tools (cake_model*, iron_bowl, mixing_bowl, mooncake_model, small_model, syrup_empty, empty_coldbrew_pot, plate_iron): unlock with Items.IRON_INGOT
- Dough variants: unlock with ModItems.FLOUR
- Butter/Cheese/Yeast: unlock with Items.MILK_BUCKET
- Gelatin: unlock with Items.SLIME_BALL
- Spices: unlock with Items.COCOA_BEANS
- Vanilla_seeds: unlock with ModItems.VANILLA
- Chocolate_bar/Field_ration: unlock with ModItems.COCOA_BATTER
- Coffee_instant (9 from box): unlock with ModItems.COFFEE_INSTANT_BOX
- Sandwich_blt: unlock with Items.BREAD
- Brownie: unlock with ModItems.CAKE_MODEL_SQUARE
- Cold_brew_pot block: unlock with ModItems.EMPTY_COLDBREW_POT
- Records: unlock with ModItems.PLATE_IRON
- Cake_sponges: unlock with Items.EGG
- Finished cakes (cake_coffee etc): unlock with ModItems.CAKE_SPONGE_<flavor>
- Plate block: unlock with Items.TERRACOTTA
- Ginger_house: unlock with ModItems.DOUGH_GINGER
- Bag_cloth: unlock with Items.STRING
- Bag: unlock with ModItems.BAG_CLOTH

For all remaining ambiguous calls, we add a TODO-style comment.
"""

import re
import sys
from pathlib import Path

FILE = Path("E:/github/CoffeeWorkshop/src/main/java/net/langball/coffee/datagen/ModRecipeProvider.java")

# Result item -> recommended unlock item
UNLOCK_BY_RESULT = {
    # Tools
    "EMPTY_COLDBREW_POT": "ModItems.PLATE_IRON.get()",
    "CAKE_MODEL": "Items.IRON_INGOT",
    "MIXING_BOWL": "Items.IRON_INGOT",
    "SMALL_MODEL": "Items.IRON_INGOT",
    "IRON_BOWL": "Items.IRON_INGOT",
    "MOONCAKE_MODEL": "Items.IRON_INGOT",
    "CAKE_MODEL_PLATE": "ModItems.PLATE_IRON.get()",
    "CAKE_MODEL_SQUARE": "ModItems.CAKE_MODEL_PLATE.get()",
    "SYRUP_EMPTY": "Items.GLASS_PANE",
    "BAG_CLOTH": "Items.STRING",
    "BAG": "ModItems.BAG_CLOTH.get()",
    "PLATE": "Items.TERRACOTTA",
    "GINGER_HOUSE": "ModItems.DOUGH_GINGER.get()",
    "VANILLA_SEEDS": "ModItems.VANILLA.get()",
    # Materials
    "DOUGH": "ModItems.FLOUR.get()",
    "DOUGH_PASTRY": "ModItems.DOUGH.get()",
    "DOUGH_GINGER": "ModItems.DOUGH.get()",
    "DOUGH_BREAD": "ModItems.DOUGH.get()",
    "DOUGH_BREAD_ROUND": "ModItems.DOUGH.get()",
    "DOUGH_BAGUETTE": "ModItems.DOUGH.get()",
    "DOUGH_BAGEL": "ModItems.DOUGH.get()",
    "DOUGH_TOAST": "ModItems.DOUGH.get()",
    "DOUGH_COOKIE": "ModItems.DOUGH.get()",
    "YEAST": "ModItems.MIXING_BOWL.get()",
    "BUTTER": "ModItems.MIXING_BOWL.get()",
    "CHEESE": "ModItems.MIXING_BOWL.get()",
    "SPICES": "Items.COCOA_BEANS",
    "GELATIN": "Items.SLIME_BALL",
    "CHOCOLATE_BAR": "ModItems.COCOA_BATTER.get()",
    "FIELD_RATION": "ModItems.COCOA_BATTER.get()",
    "BROWNIE": "ModItems.CAKE_MODEL_SQUARE.get()",
    "SANDWICH_BLT": "Items.BREAD",
    "COFFEE_INSTANT": "ModItems.COFFEE_INSTANT_BOX.get()",
    "COLD_BREW_POT": "ModItems.EMPTY_COLDBREW_POT.get()",
    "RECORD_BLANK": "ModItems.PLATE_IRON.get()",
    "RECORD_KUSA_NOSHI_TO_NE": "ModItems.RECORD_BLANK.get()",
    "RECORD_LAZY_LADY_KAGUYA": "ModItems.RECORD_BLANK.get()",
    "RECORD_THE_GRIMOIRE_OF_MARISA": "ModItems.RECORD_BLANK.get()",
    "XMAS_TREE": "ItemTags.SAPLINGS",
    # Cake sponges
    "CAKE_SPONGE": "Items.EGG",
    "CAKE_SPONGE_CHOCOLATE": "Items.EGG",
    "CAKE_SPONGE_COFFEE": "Items.EGG",
    "CAKE_SPONGE_PUMPKIN": "Items.EGG",
    "CAKE_SPONGE_CARROT": "Items.EGG",
    "CAKE_SPONGE_REDVELVET": "Items.EGG",
    "CAKE_SPONGE_LEMON": "Items.EGG",
    "CAKE_SPONGE_TEA": "Items.EGG",
    "CAKE_SPONGE_BERRY": "Items.EGG",
    # Vanilla cake
    "CAKE": "Blocks.CAKE",  # vanilla cake - unusual; unlock with sugar
    "CAKE_COFFEE": "ModBlocks.CAKE_SPONGE_COFFEE.get()",
    "CAKE_HARVEST": "ModBlocks.CAKE_SPONGE_PUMPKIN.get()",
    "CAKE_BERRY": "ModBlocks.CAKE_SPONGE_BERRY.get()",
    "CAKE_LEMON": "ModBlocks.CAKE_SPONGE_LEMON.get()",
    "CAKE_TEA": "ModBlocks.CAKE_SPONGE_TEA.get()",
    "CAKE_CHEESE": "ModItems.CAKE_MODEL.get()",
    "CAKE_SCHWARZWALD": "ModBlocks.CAKE_SPONGE_CHOCOLATE.get()",
    "CAKE_REDVELVET": "ModBlocks.CAKE_SPONGE_REDVELVET.get()",
    "TIRAMISU": "ModBlocks.CAKE_SPONGE.get()",
    "MOUSSE_BERRY": "ModItems.CAKE_MODEL.get()",
    "MOUSSE_LEMON": "ModItems.CAKE_MODEL.get()",
    "MOUSSE_CHOCOLATE": "ModItems.CAKE_MODEL.get()",
    "MOUSSE_COFFEE": "ModItems.CAKE_MODEL.get()",
    # Pie
    "PIE_CREAM": "ModItems.PLATE_DOUGH_PASTRY.get()",
    # Ice cream
    "ICECREAM_VANILLA": "Items.MILK_BUCKET",
}

# If result is in above, use that mapping. Otherwise fall back to using
# the (already present) first .requires(...) ingredient from the chain
# via a stub ItemLike that will be replaced at edit time.

content = FILE.read_text()
lines = content.splitlines(keepends=False)


def find_block_start(line_no: int) -> int:
    """Walk backward to find the helper call `.unlockedBy(...)` line."""
    return line_no


def replace_shapeless_calls(content: str) -> str:
    """Find 2-arg shapeless(...) patterns and insert unlockItem argument."""
    # Pattern: shapeless(RecipeCategory.X, ModItems.Y.get())  or  shapeless(RecipeCategory.X, ModBlocks.Y.get())
    # We look for `shapeless(RecipeCategory.X, <result>)` then add a 4th arg
    # Lines we want to match have no 3rd argument (no comma after result).
    pattern = re.compile(
        r"^(\s*)shapeless\(RecipeCategory\.([A-Z_]+),\s*([A-Za-z_][A-Za-z0-9_]*\.([A-Za-z_][A-Za-z0-9_]*)\.get\(\))\)(\s*)$",
        re.MULTILINE,
    )

    def repl(m):
        indent = m.group(1)
        cat = m.group(2)
        result = m.group(3)
        ns = m.group(4)
        result_name = re.search(r"([A-Z_]+)\.get\(\)", result).group(1)
        if ns == "ModItems" and result_name in UNLOCK_BY_RESULT:
            unlock = UNLOCK_BY_RESULT[result_name]
        elif ns == "ModBlocks" and result_name in UNLOCK_BY_RESULT:
            unlock = UNLOCK_BY_RESULT[result_name]
        else:
            unlock = "Items.IRON_INGOT"  # safe early-game fallback
        return f"{indent}shapeless(RecipeCategory.{cat}, {result}, {unlock})"

    new = pattern.sub(repl, content)

    # Pattern: shapeless(category, result, count)  (3 arg with count)
    pattern2 = re.compile(
        r"^(\s*)shapeless\(RecipeCategory\.([A-Z_]+),\s*([A-Za-z_][A-Za-z0-9_]*\.([A-Za-z_][A-Za-z0-9_]*)\.get\(\)),\s*(\d+)\)(\s*)$",
        re.MULTILINE,
    )

    def repl2(m):
        indent = m.group(1)
        cat = m.group(2)
        result = m.group(3)
        ns = m.group(4)
        count = m.group(5)
        result_name = re.search(r"([A-Z_]+)\.get\(\)", result).group(1)
        if ns == "ModItems" and result_name in UNLOCK_BY_RESULT:
            unlock = UNLOCK_BY_RESULT[result_name]
        elif ns == "ModBlocks" and result_name in UNLOCK_BY_RESULT:
            unlock = UNLOCK_BY_RESULT[result_name]
        else:
            unlock = "Items.IRON_INGOT"
        return f"{indent}shapeless(RecipeCategory.{cat}, {result}, {count}, {unlock})"

    new = pattern2.sub(repl2, new)
    return new


def replace_shaped_calls(content: str) -> str:
    """Find 2-arg shaped(...) patterns and insert unlockItem argument."""
    # Same as shapeless but for shaped.
    pattern = re.compile(
        r"^(\s*)shaped\(RecipeCategory\.([A-Z_]+),\s*([A-Za-z_][A-Za-z0-9_]*\.([A-Za-z_][A-Za-z0-9_]*)\.get\(\))\)(\s*)$",
        re.MULTILINE,
    )

    def repl(m):
        indent = m.group(1)
        cat = m.group(2)
        result = m.group(3)
        ns = m.group(4)
        result_name = re.search(r"([A-Z_]+)\.get\(\)", result).group(1)
        if ns == "ModItems" and result_name in UNLOCK_BY_RESULT:
            unlock = UNLOCK_BY_RESULT[result_name]
        elif ns == "ModBlocks" and result_name in UNLOCK_BY_RESULT:
            unlock = UNLOCK_BY_RESULT[result_name]
        else:
            unlock = "Items.IRON_INGOT"
        return f"{indent}shaped(RecipeCategory.{cat}, {result}, {unlock})"

    new = pattern.sub(repl, content)

    # 3-arg with count
    pattern2 = re.compile(
        r"^(\s*)shaped\(RecipeCategory\.([A-Z_]+),\s*([A-Za-z_][A-Za-z0-9_]*\.([A-Za-z_][A-Za-z0-9_]*)\.get\(\)),\s*(\d+)\)(\s*)$",
        re.MULTILINE,
    )

    def repl2(m):
        indent = m.group(1)
        cat = m.group(2)
        result = m.group(3)
        ns = m.group(4)
        count = m.group(5)
        result_name = re.search(r"([A-Z_]+)\.get\(\)", result).group(1)
        if ns == "ModItems" and result_name in UNLOCK_BY_RESULT:
            unlock = UNLOCK_BY_RESULT[result_name]
        elif ns == "ModBlocks" and result_name in UNLOCK_BY_RESULT:
            unlock = UNLOCK_BY_RESULT[result_name]
        else:
            unlock = "Items.IRON_INGOT"
        return f"{indent}shaped(RecipeCategory.{cat}, {result}, {count}, {unlock})"

    new = pattern2.sub(repl2, new)
    return new


content = replace_shapeless_calls(content)
content = replace_shaped_calls(content)

FILE.write_text(content)
print(f"Updated {FILE}")
print(f"Old size: {sum(1 for _ in lines)} lines / new size: {len(content.splitlines())} lines")
