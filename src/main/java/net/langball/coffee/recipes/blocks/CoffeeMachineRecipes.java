package net.langball.coffee.recipes.blocks;

import net.minecraft.world.item.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class CoffeeMachineRecipes {
    private static final CoffeeMachineRecipes INSTANCE = new CoffeeMachineRecipes();
    private final Map<ItemStack, ItemStack> recipeList = new HashMap<>();

    public static CoffeeMachineRecipes instance() { return INSTANCE; }

    public ItemStack getSmeltingResult(ItemStack input) {
        for (Map.Entry<ItemStack, ItemStack> entry : recipeList.entrySet()) {
            if (ItemStack.isSameItem(entry.getKey(), input)) {
                return entry.getValue().copy();
            }
        }
        return ItemStack.EMPTY;
    }

    public void addRecipe(ItemStack input, ItemStack output) {
        recipeList.put(input, output);
    }
}
