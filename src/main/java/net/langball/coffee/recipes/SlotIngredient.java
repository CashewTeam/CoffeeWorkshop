package net.langball.coffee.recipes;

import net.minecraft.world.item.crafting.Ingredient;

/**
 * An ingredient paired with a required count for one machine slot.
 *
 * <p>Used by {@link CoffeeBrewingRecipe} to express per-slot
 * requirements (e.g. "2 × coffee_powder in slot 0").
 */
public record SlotIngredient(Ingredient ingredient, int count) {

    public SlotIngredient {
        if (count < 1) throw new IllegalArgumentException("count must be >= 1, got " + count);
    }
}
