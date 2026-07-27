package net.langball.coffee.recipes;

import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraftforge.items.wrapper.RecipeWrapper;

public record SodaMachineRecipe(
        ResourceLocation id,
        String group,
        Ingredient container,
        Ingredient base,
        Ingredient flavor,
        ItemStack result,
        float experience,
        int cookingTime
) implements Recipe<RecipeWrapper> {

    @Override
    public ResourceLocation getId() { return id; }

    @Override
    public boolean matches(RecipeWrapper inv, Level level) {
        if (level.isClientSide) return false;
        int size = inv.getContainerSize();
        return container.test(inv.getItem(0))
                && (size <= 1 || base.test(inv.getItem(1)))
                && (size <= 2 || flavor.test(inv.getItem(2)));
    }

    @Override
    public ItemStack assemble(RecipeWrapper inv, RegistryAccess access) {
        return result.copy();
    }

    @Override
    public boolean canCraftInDimensions(int w, int h) { return true; }

    @Override
    public ItemStack getResultItem(RegistryAccess access) { return result.copy(); }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        NonNullList<Ingredient> list = NonNullList.create();
        list.add(container);
        list.add(base);
        list.add(flavor);
        return list;
    }

    @Override
    public RecipeSerializer<?> getSerializer() { return SodaMachineRecipeSerializer.INSTANCE; }

    @Override
    public RecipeType<?> getType() { return SodaMachineRecipeSerializer.TYPE; }
}
