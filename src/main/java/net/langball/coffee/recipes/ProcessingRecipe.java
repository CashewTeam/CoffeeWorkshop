package net.langball.coffee.recipes;

import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

/**
 * Lightweight interface shared by {@link MachineRecipe} (single-input)
 * and {@link CoffeeBrewingRecipe} (multi-input).
 *
 * <p>The public processing engine in
 * {@link net.langball.coffee.block.entity.AbstractProcessingBlockEntity}
 * works against this interface rather than any concrete recipe class,
 * so that Coffee Machine can use multi-input recipes without
 * modifying the shared state machine.
 *
 * <h3>Remainder contract</h3>
 * <p>Implementations that need non-standard remainder handling
 * (e.g. returning empty buckets for milk/water) should override
 * {@link #getRemainder(int)}.  The default falls back to
 * {@link ItemStack#getCraftingRemainingItem()} on the consumed stack.
 */
public interface ProcessingRecipe {

    ResourceLocation getId();

    /** Whether the given container matches this recipe's inputs. */
    boolean matches(SimpleContainer container, Level level);

    /** Creates the output ItemStack, with any NBT initialisation
     *  (e.g. cup count for drinks). */
    ItemStack assemble(SimpleContainer container, RegistryAccess registryAccess);

    /** XP awarded when a player takes the result. */
    float experience();

    /** The {@link RecipeType} for RecipeManager queries. */
    RecipeType<?> getType();

    /** Ticks required to complete one craft. */
    int cookingTime();

    /**
     * Which slots this recipe consumes from (1 item per slot).
     * Used by the multi-input consumer to know which slots to shrink.
     */
    int[] getConsumedSlots();

    /**
     * How many items are required from the given slot.
     * Returns 0 if the slot is not consumed by this recipe.
     */
    int getRequiredCount(int slot);

    /**
     * Returns the remainder ItemStack left behind after consuming from
     * {@code slot}, or {@link ItemStack#EMPTY} if none.
     *
     * <p>The default implementation returns the crafting remaining item
     * for the stack in the given slot (e.g. glass_bottle from
     * coldbrew_bottle, bucket from milk_bucket, syrup_empty from
     * syrup).  Override for recipes that need custom per-slot logic.
     */
    default ItemStack getRemainder(int slot) {
        return ItemStack.EMPTY;
    }
}
