package net.langball.coffee.capability;

import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import org.jetbrains.annotations.NotNull;

/**
 * Wraps an {@link IItemHandler} to only allow insertion into specific slots.
 * Extraction always returns {@link ItemStack#EMPTY}.
 */
public class InsertOnlyItemHandler implements IItemHandler {

    private final IItemHandler delegate;
    private final int[] allowedSlots;

    /**
     * @param delegate     the backing full handler
     * @param allowedSlots slot indices where insertion is permitted
     */
    public InsertOnlyItemHandler(IItemHandler delegate, int... allowedSlots) {
        this.delegate = delegate;
        this.allowedSlots = allowedSlots;
    }

    @Override
    public int getSlots() {
        return delegate.getSlots();
    }

    @Override
    @NotNull
    public ItemStack getStackInSlot(int slot) {
        return delegate.getStackInSlot(slot);
    }

    @Override
    @NotNull
    public ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
        for (int s : allowedSlots) {
            if (s == slot) {
                return delegate.insertItem(slot, stack, simulate);
            }
        }
        return stack; // not allowed → bounce
    }

    @Override
    @NotNull
    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        return ItemStack.EMPTY; // never allow extraction
    }

    @Override
    public int getSlotLimit(int slot) {
        return delegate.getSlotLimit(slot);
    }

    @Override
    public boolean isItemValid(int slot, @NotNull ItemStack stack) {
        for (int s : allowedSlots) {
            if (s == slot) {
                return delegate.isItemValid(slot, stack);
            }
        }
        return false;
    }
}
