package net.langball.coffee.recipes;

import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

/**
 * Generic single-input / single-output machine recipe shared by all five
 * processing machines (Grinder, CoffeeMachine, IcecreamMachine, Roller, Oven).
 *
 * <p>Each machine variant has its own {@link RecipeType} and
 * {@link RecipeSerializer} registered in {@link net.langball.coffee.init.ModRecipeTypes};
 * the JSON {@code "type"} field dispatches to the correct serializer.
 *
 * <p>Because five distinct serializer registry entries are used, even though
 * this same Java class backs all of them, the recipe manager can correctly
 * filter by type at query time.
 *
 * @param id          unique resource location
 * @param group       optional recipe group for the recipe book
 * @param ingredient  the single required input
 * @param result      the output ItemStack (count &amp; components matter)
 * @param experience  smelting-style XP awarded when the result is taken
 * @param cookingTime tick count the burn must last to complete one craft
 * @param type        the machine-specific RecipeType (must match the
 *                    serializer that created this instance)
 * @param serializer  the machine-specific RecipeSerializer that created this
 *                    instance (returned by {@link #getSerializer()})
 */
public record MachineRecipe(
        ResourceLocation id,
        String group,
        Ingredient ingredient,
        ItemStack result,
        float experience,
        int cookingTime,
        RecipeType<?> type,
        RecipeSerializer<?> serializer
) implements Recipe<SimpleContainer> {

    @Override
    public boolean matches(@NotNull SimpleContainer container, @NotNull Level level) {
        return ingredient.test(container.getItem(0));
    }

    @Override
    @NotNull
    public ItemStack assemble(@NotNull SimpleContainer container, @NotNull RegistryAccess access) {
        return result.copy();
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return true;
    }

    @Override
    @NotNull
    public ItemStack getResultItem(@NotNull RegistryAccess access) {
        return result;
    }

    @Override
    @NotNull
    public ResourceLocation getId() {
        return id;
    }

    @Override
    @NotNull
    public RecipeSerializer<?> getSerializer() {
        return serializer;
    }

    @Override
    @NotNull
    public RecipeType<?> getType() {
        return type;
    }
}
