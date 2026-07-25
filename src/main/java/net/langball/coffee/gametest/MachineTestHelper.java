package net.langball.coffee.gametest;

import net.langball.coffee.block.MachineBlock;
import net.langball.coffee.block.entity.MachineBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;

/**
 * Shared helper methods for machine GameTests.
 *
 * <p>All helpers operate on the server-side test world provided by
 * {@link GameTestHelper}.  They abstract common patterns such as
 * "insert item into slot N" or "verify the LIT state".
 */
public final class MachineTestHelper {

    private MachineTestHelper() {}

    /**
     * Places {@code stack} into the machine's inventory slot {@code slot},
     * bypassing {@code isItemValid} checks.  Use this for pre-filling
     * slots (including output) in tests.
     */
    public static void setItem(GameTestHelper helper, BlockPos pos, int slot, ItemStack stack) {
        BlockEntity be = helper.getBlockEntity(pos);
        if (be instanceof MachineBlockEntity mbe) {
            var handler = mbe.getItemHandler();
            if (handler instanceof net.minecraftforge.items.ItemStackHandler ish) {
                ish.setStackInSlot(slot, stack.copy());
                mbe.setChanged();
            }
        }
    }

    /**
     * Places {@code stack} into the machine's inventory slot {@code slot}.
     * Goes through normal insertion (respects {@code isItemValid}).
     * Returns the remainder if insertion was partial.
     */
    public static ItemStack insertItem(GameTestHelper helper, BlockPos pos, int slot, ItemStack stack) {
        BlockEntity be = helper.getBlockEntity(pos);
        if (be instanceof MachineBlockEntity mbe) {
            var handler = mbe.getItemHandler();
            ItemStack remainder = handler.insertItem(slot, stack, false);
            mbe.setChanged();
            return remainder;
        }
        return stack;
    }

    /**
     * Returns the item currently in the machine's slot {@code slot}.
     */
    public static ItemStack getItem(GameTestHelper helper, BlockPos pos, int slot) {
        BlockEntity be = helper.getBlockEntity(pos);
        if (be instanceof MachineBlockEntity mbe) {
            return mbe.getItemHandler().getStackInSlot(slot);
        }
        return ItemStack.EMPTY;
    }

    /**
     * Returns {@code true} if the machine's LIT property is set.
     */
    public static boolean isLit(GameTestHelper helper, BlockPos pos) {
        var state = helper.getBlockState(pos);
        return state.hasProperty(MachineBlock.LIT) && state.getValue(MachineBlock.LIT);
    }

    /**
     * Pulses the block at {@code pos} for {@code ticks} game ticks by
     * calling {@link GameTestHelper#succeedWhenTick} after the delay.
     *
     * <p>Use this to give the block entity time to process.  Typical usage:
     * <pre>{@code
     * helper.succeedWhen(() -> {
     *     // Assert conditions after N ticks
     * });
     * }</pre>
     * The helper itself calls {@code runAfterDelay} or
     * {@code succeedWhenTick}.
     */
    public static void waitTicks(GameTestHelper helper, int ticks, Runnable then) {
        helper.runAfterDelay(ticks, () -> {
            then.run();
        });
    }
}
