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
}
