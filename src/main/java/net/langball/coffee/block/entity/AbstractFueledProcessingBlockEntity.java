package net.langball.coffee.block.entity;

import net.langball.coffee.block.MachineBlock;
import net.langball.coffee.recipes.MachineRecipe;
import net.minecraft.core.BlockPos;
import net.minecraft.world.Containers;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Base for machines that require fuel to operate (Grinder, Roller,
 * Oven, IcecreamMachine).
 *
 * <p>This class adds fuel lifecycle management on top of
 * {@link AbstractProcessingBlockEntity}: fuel slot, burn-time
 * consumption, and the rule that new fuel is only consumed when
 * work is available and the current fuel has run out.
 *
 * <h3>Concrete subclass contract</h3>
 * Subclasses must implement:
 * <ul>
 *   <li>{@link #getFuelSlot()} — fuel slot index</li>
 *   <li>{@link #getFuelTime(ItemStack)} — burn time for a fuel item</li>
 *   <li>{@link #isValidFuel(ItemStack)} — whether an item is valid fuel</li>
 *   <li>{@link #getRecipeType()}, {@link #getInputSlot()}, {@link #getOutputSlot()}
 *       (inherited from {@link AbstractProcessingBlockEntity})</li>
 * </ul>
 */
public abstract class AbstractFueledProcessingBlockEntity extends AbstractProcessingBlockEntity {

    protected AbstractFueledProcessingBlockEntity(net.minecraft.world.level.block.entity.BlockEntityType<?> type,
                                                   BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    // ---- Abstract fuel contract -------------------------------------------

    /** Index of the fuel slot. */
    protected abstract int getFuelSlot();

    /**
     * Returns the burn time (in ticks) for the given fuel item, or 0
     * if the item is not valid fuel.
     */
    protected abstract int getFuelTime(ItemStack stack);

    /**
     * Returns {@code true} if {@code stack} is a valid fuel item for
     * this machine (used for slot validation).
     */
    protected abstract boolean isValidFuel(ItemStack stack);

    // ---- Fuel consumption --------------------------------------------------

    /**
     * Attempts to consume one fuel item from the fuel slot.
     *
     * <p>If the slot contains valid fuel, sets {@link #burnTime} and
     * {@link #burnTimeTotal} to the fuel's burn time, shrinks the fuel
     * stack by one, and handles any remainder.
     *
     * @return {@code true} if fuel was consumed
     */
    protected boolean tryConsumeFuel() {
        ItemStack fuel = itemHandler.getStackInSlot(getFuelSlot());
        if (fuel.isEmpty()) return false;

        int time = getFuelTime(fuel);
        if (time <= 0) return false;

        burnTime = time;
        burnTimeTotal = time;

        ItemStack remainder = fuel.getCraftingRemainingItem();
        fuel.shrink(1);
        if (fuel.isEmpty() && !remainder.isEmpty()) {
            itemHandler.setStackInSlot(getFuelSlot(), remainder);
        } else if (!fuel.isEmpty() && !remainder.isEmpty()) {
            Containers.dropItemStack(level, worldPosition.getX(),
                    worldPosition.getY(), worldPosition.getZ(), remainder);
        }
        return true;
    }

    // ---- Fueled tick -------------------------------------------------------

    /**
     * Common server-side tick for fuel-burning machines.
     *
     * <p>This is the primary tick method for Grinder, Roller, Oven, and
     * IcecreamMachine.  It orchestrates:
     *
     * <ol>
     *   <li>Burn down existing fuel</li>
     *   <li>Resolve the current recipe</li>
     *   <li>Detect recipe changes (reset progress)</li>
     *   <li>Consume new fuel when needed and work is available</li>
     *   <li>Advance processing progress while fuel and recipe are valid</li>
     *   <li>Complete the craft and produce output</li>
     *   <li>Pause (but don't reset) when output is blocked</li>
     *   <li>Sync LIT and mark dirty when state changes</li>
     * </ol>
     */
    protected void tickFueledProcessing(Level level, BlockPos pos, BlockState state) {
        if (level.isClientSide) return;

        boolean wasBurning = burnTime > 0;
        boolean dirty = false;

        // 1. Burn down fuel
        if (burnTime > 0) {
            burnTime--;
        }

        MachineRecipe recipe = getCurrentRecipe();
        var prevRecipeId = activeRecipeId; // snapshot before resolveRecipe changed it

        boolean canProcess = recipe != null && canAcceptResult(recipe);

        // 2. Consume new fuel when needed and work is available
        if (burnTime == 0 && canProcess) {
            if (tryConsumeFuel()) {
                totalCookTime = recipe.cookingTime();
                dirty = true;
            }
        }

        // 3. Detect recipe change
        if (recipe != null && prevRecipeId != null && !prevRecipeId.equals(recipe.getId())) {
            onRecipeChanged(prevRecipeId, recipe.getId());
            dirty = true;
        }

        // 4. Advance progress
        if (burnTime > 0 && canProcess) {
            cookTime++;
            if (cookTime >= totalCookTime) {
                cookTime = 0;
                totalCookTime = recipe.cookingTime();
                processRecipe(recipe);
                dirty = true;
            }
        } else if (!canProcess && cookTime > 0) {
            // Output blocked or recipe invalid: pause but preserve cookTime
            // (cookTime stays where it is; will resume when output clears)
            // Only reset if the recipe itself went away
            if (recipe == null) {
                cookTime = 0;
                dirty = true;
            }
        }

        // 5. Sync LIT
        if (wasBurning != (burnTime > 0)) {
            dirty = true;
            level.setBlock(pos, state.setValue(MachineBlock.LIT, burnTime > 0),
                    net.minecraft.world.level.block.Block.UPDATE_ALL);
        }

        if (dirty) {
            setChanged(level, pos, state);
        }
    }
}
