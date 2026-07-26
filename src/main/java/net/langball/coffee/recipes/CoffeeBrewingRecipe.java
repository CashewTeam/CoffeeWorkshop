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
 * <h3>Slot layout (v2 — 5 slots)</h3>
 * <table>
 *   <tr><th>Slot</th><th>Role</th><th>Example</th></tr>
 *   <tr><td>0</td><td>Base</td><td>coffee_powder, cocoa_powder, coldbrew_bottle</td></tr>
 *   <tr><td>1</td><td>Modifier</td><td>water_bucket, milk_bucket (or empty)</td></tr>
 *   <tr><td>2</td><td>Additive</td><td>cocoa_powder, ice_slag, syrup (or empty)</td></tr>
 *   <tr><td>3</td><td>Container</td><td>cup, cup_glass</td></tr>
 *   <tr><td>4</td><td>Output</td><td>DrinkCoffee result</td></tr>
 * </table>
 *
 * <p>When {@code modifier} is {@code null} the modifier slot MUST be empty.
 * When {@code additive} is {@code null} the additive slot MUST be empty.
 *
 * <p>Implements both {@link Recipe} (for RecipeManager lookup) and
 * {@link ProcessingRecipe} (for the shared processing engine).
 */
public record CoffeeBrewingRecipe(
        ResourceLocation id,
        String group,
        SlotIngredient base,
        @Nullable SlotIngredient modifier,
        @Nullable SlotIngredient additive,
        SlotIngredient container,
        ItemStack result,
        float experience,
        int cookingTime
) implements Recipe<SimpleContainer>, ProcessingRecipe {

    /** Pre-v2 constructor for backward compatibility (no additive). */
    public CoffeeBrewingRecipe(ResourceLocation id, String group,
                               SlotIngredient base, @Nullable SlotIngredient modifier,
                               SlotIngredient container, ItemStack result,
                               float experience, int cookingTime) {
        this(id, group, base, modifier, null, container, result, experience, cookingTime);
    }

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
            if (!inv.getItem(1).isEmpty()) {
                return false;
            }
        }
        // Slot 2: additive check
        if (additive != null) {
            if (!additive.ingredient().test(inv.getItem(2))
                    || inv.getItem(2).getCount() < additive.count()) {
                return false;
            }
        } else {
            if (!inv.getItem(2).isEmpty()) {
                return false;
            }
        }
        // Slot 3: container must match
        if (!container.ingredient().test(inv.getItem(3))
                || inv.getItem(3).getCount() < container.count()) {
            return false;
        }
        return true;
    }

    @Override
    @NotNull
    public ItemStack assemble(@NotNull SimpleContainer inv, @NotNull RegistryAccess registryAccess) {
        return createDisplayResult();
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return true;
    }

    @Override
    @NotNull
    public ItemStack getResultItem(@NotNull RegistryAccess registryAccess) {
        return createDisplayResult();
    }

    /** Unified result factory — initialises cup NBT on DrinkCoffee outputs. */
    private ItemStack createDisplayResult() {
        ItemStack output = result.copy();
        if (output.getItem() instanceof net.langball.coffee.item.DrinkCoffee drink) {
            drink.initializeFreshStack(output);
        }
        return output;
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
        // Build list dynamically based on which optional slots are present
        int count = 1; // base always consumed
        if (modifier != null) count++;
        if (additive != null) count++;
        count++; // container always consumed
        int[] slots = new int[count];
        int idx = 0;
        slots[idx++] = 0; // base
        if (modifier != null) slots[idx++] = 1;
        if (additive != null) slots[idx++] = 2;
        slots[idx] = 3; // container
        return slots;
    }

    @Override
    public int getRequiredCount(int slot) {
        return switch (slot) {
            case 0 -> base.count();
            case 1 -> modifier != null ? modifier.count() : 0;
            case 2 -> additive != null ? additive.count() : 0;
            case 3 -> container.count();
            default -> 0;
        };
    }

    @Override
    @NotNull
    public ItemStack getRemainder(int slot) {
        if (slot == 1 && modifier != null) {
            ItemStack one = new ItemStack(modifier.ingredient().getItems()[0].getItem());
            return one.getCraftingRemainingItem();
        }
        if (slot == 2 && additive != null) {
            ItemStack one = new ItemStack(additive.ingredient().getItems()[0].getItem());
            return one.getCraftingRemainingItem();
        }
        return ItemStack.EMPTY;
    }
}
