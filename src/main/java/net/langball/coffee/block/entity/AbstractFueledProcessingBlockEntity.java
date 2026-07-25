package net.langball.coffee.block.entity;

import net.langball.coffee.block.MachineBlock;
import net.langball.coffee.recipes.ProcessingRecipe;
import net.minecraft.core.BlockPos;
import net.minecraft.world.Containers;
import net.minecraft.world.item.ItemStack;
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
 * <p>Subclasses override:
 * <ul>
 *   <li>{@link #getFuelSlot()} — fuel slot index</li>
 *   <li>{@link #getFuelTime(ItemStack)} — burn time for a fuel item</li>
 *   <li>{@link #isValidFuel(ItemStack)} — whether an item is valid fuel</li>
 *   <li>{@link #startProcessingPower(MachineRecipe)} — consume fuel</li>
 *   <li>{@link #getRecipeType()}, {@link #getInputSlot()},
 *       {@link #getOutputSlot()} (from super)</li>
 * </ul>
 *
 * <p>The main tick delegates to
 * {@link AbstractProcessingBlockEntity#tickProcessing(Level, BlockPos, BlockState)}
 * and only adds the {@code wasBurning → setBlock} LIT transition that
 * fuel machines need (via block-state update rather than the lighter
 * {@code updateLitState}).
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

    // ---- Power hook override ----------------------------------------------

    /**
     * Consumes one fuel item from the fuel slot and sets {@link #burnTime}
     * and {@link #burnTimeTotal}.
     */
    @Override
    protected void startProcessingPower(ProcessingRecipe recipe) {
        ItemStack fuel = itemHandler.getStackInSlot(getFuelSlot());
        if (fuel.isEmpty()) return;

        int time = getFuelTime(fuel);
        if (time <= 0) return;

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
    }

    // ---- Tick --------------------------------------------------------------

    /**
     * Fuel-burning machine tick.
     *
     * <p>Wraps {@link AbstractProcessingBlockEntity#tickProcessing(Level, BlockPos, BlockState)}
     * and adds the block-state-level LIT update that fuel machines need
     * (via {@code level.setBlock}) rather than the lighter
     * {@code updateLitState} used by non-fuel machines.
     */
    @Override
    protected void tickProcessing(Level level, BlockPos pos, BlockState state) {
        if (level.isClientSide) return;

        boolean wasBurning = burnTime > 0;

        // Delegate all processing logic to the shared base
        super.tickProcessing(level, pos, state);

        // Fuel machines need a block-state update for LIT (not just the
        // lighter updateLitState which CoffeeMachine uses).  The base
        // tick calls updateLitState; we override with the full setBlock.
        boolean isBurning = burnTime > 0;
        if (wasBurning != isBurning) {
            level.setBlock(pos, state.setValue(MachineBlock.LIT, isBurning),
                    net.minecraft.world.level.block.Block.UPDATE_ALL);
        }
    }
}
