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
import org.jetbrains.annotations.Nullable;

/**
 * Multi-input recipe for the Coffee Machine.
 *
 * <h3>Slot layout</h3>
 * <table>
 *   <tr><th>Slot</th><th>Purpose</th></tr>
 *   <tr><td>0</td><td>Base ingredient (coffee powder)</td></tr>
 *   <tr><td>1</td><td>Modifier (water bucket, milk bucket, or empty)</td></tr>
 *   <tr><td>2</td><td>Container (cup)</td></tr>
 *   <tr><td>3</td><td>Output</td></tr>
 * </table>
 *
 * <p>When {@code modifier} is {@code null} the modifier slot MUST be
 * empty (used for Espresso which takes only coffee powder + cup).
 *
 * <p>Implements both {@link Recipe} (for RecipeManager lookup) and
 * {@link ProcessingRecipe} (for the shared processing engine).
 */
public record CoffeeBrewingRecipe(
        ResourceLocation id,
        String group,
        SlotIngredient base,
        @Nullable SlotIngredient modifier,
        SlotIngredient container,
        ItemStack result,
        float experience,
        int cookingTime
) implements Recipe<SimpleContainer>, ProcessingRecipe {

    @Override
    public boolean matches(@NotNull SimpleContainer inv, @NotNull Level level) {
        // Slot 0: base must match
        if (!base.ingredient().test(inv.getItem(0))
                || inv.getItem(0).getCount() < base.count()) {
            return false;
        }
        // Slot 1: modifier check
        if (modifier != null) {
            if (!modifier.ingredient().test(inv.getItem(1))
                    || inv.getItem(1).getCount() < modifier.count()) {
                return false;
            }
        } else {
            // Modifier slot must be empty
            if (!inv.getItem(1).isEmpty()) {
                return false;
            }
        }
        // Slot 2: container (cup) must match
        if (!container.ingredient().test(inv.getItem(2))
                || inv.getItem(2).getCount() < container.count()) {
            return false;
        }
        return true;
    }

    @Override
    @NotNull
    public ItemStack assemble(@NotNull SimpleContainer inv, @NotNull RegistryAccess registryAccess) {
        ItemStack output = result.copy();
        if (output.getItem() instanceof net.langball.coffee.item.DrinkCoffee drink) {
            drink.initializeFreshStack(output);
        }
        return output;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return true;
    }

    @Override
    @NotNull
    public ItemStack getResultItem(@NotNull RegistryAccess registryAccess) {
        return result;
    }

    @Override
    @NotNull
    public ResourceLocation getId() { return id; }

    @Override
    @NotNull
    public RecipeSerializer<?> getSerializer() {
        return net.langball.coffee.init.ModRecipeTypes.COFFEE_BREWING_SERIALIZER.get();
    }

    @Override
    @NotNull
    public RecipeType<?> getType() {
        return net.langball.coffee.init.ModRecipeTypes.COFFEE_BREWING;
    }

    // ── ProcessingRecipe impl ──────────────────────────────────────────

    @Override
    public int[] getConsumedSlots() {
        if (modifier != null) {
            return new int[]{0, 1, 2};
        }
        return new int[]{0, 2};
    }

    @Override
    public int getRequiredCount(int slot) {
        return switch (slot) {
            case 0 -> base.count();
            case 1 -> modifier != null ? modifier.count() : 0;
            case 2 -> container.count();
            default -> 0;
        };
    }

    /**
     * Returns the remainder ItemStack for the given slot after
     * consumption, or {@link ItemStack#EMPTY} if none.
     *
     * <p>For example, water_bucket → bucket, milk_bucket → bucket.
     */
    public ItemStack getRemainder(int slot) {
        if (slot == 1 && modifier != null) {
            // Simulate consuming one modifier item to discover its remainder
            ItemStack one = new ItemStack(modifier.ingredient().getItems()[0].getItem());
            return one.getCraftingRemainingItem();
        }
        return ItemStack.EMPTY;
    }
}
