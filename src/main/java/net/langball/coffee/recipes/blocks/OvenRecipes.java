package net.langball.coffee.recipes.blocks;

import net.minecraft.world.item.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class OvenRecipes {
    private static final OvenRecipes INSTANCE = new OvenRecipes();
    private final Map<ItemStack, ItemStack> recipeList = new HashMap<>();
    private final Map<ItemStack, ItemStack> secondaryResultList = new HashMap<>();

    public static OvenRecipes instance() { return INSTANCE; }

    public ItemStack getSmeltingResult(ItemStack input) {
        for (Map.Entry<ItemStack, ItemStack> entry : recipeList.entrySet()) {
            if (ItemStack.isSameItem(entry.getKey(), input)) {
                return entry.getValue().copy();
            }
        }
        return ItemStack.EMPTY;
    }

    public ItemStack getSecondaryResult(ItemStack input) {
        for (Map.Entry<ItemStack, ItemStack> entry : secondaryResultList.entrySet()) {
            if (ItemStack.isSameItem(entry.getKey(), input)) {
                return entry.getValue().copy();
            }
        }
        return ItemStack.EMPTY;
    }

    public void addRecipe(ItemStack input, ItemStack output, ItemStack secondary) {
        recipeList.put(input, output);
        secondaryResultList.put(input, secondary);
    }
}
