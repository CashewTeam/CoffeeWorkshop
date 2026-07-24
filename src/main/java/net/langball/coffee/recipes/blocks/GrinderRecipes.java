package net.langball.coffee.recipes.blocks;

import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;

import java.util.HashMap;
import java.util.Map;

public class GrinderRecipes {
    private static final GrinderRecipes INSTANCE = new GrinderRecipes();
    private final Map<ItemStack, ItemStack> recipeList = new HashMap<>();

    public static GrinderRecipes instance() { return INSTANCE; }

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

    public boolean isEmpty() { return recipeList.isEmpty(); }
}
