package net.langball.coffee.capability;

import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import org.jetbrains.annotations.NotNull;

/**
 * Wraps an {@link IItemHandler} to only allow extraction from specific slots.
 * Insertion is always rejected.
 */
public class ExtractOnlyItemHandler implements IItemHandler {

    private final IItemHandler delegate;
    private final int[] allowedSlots;

    /**
     * @param delegate     the backing full handler
     * @param allowedSlots slot indices where extraction is permitted
     */
    public ExtractOnlyItemHandler(IItemHandler delegate, int... allowedSlots) {
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
        return stack; // never allow insertion
    }

    @Override
    @NotNull
    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        for (int s : allowedSlots) {
            if (s == slot) {
                return delegate.extractItem(slot, amount, simulate);
            }
        }
        return ItemStack.EMPTY; // not allowed
    }

    @Override
    public int getSlotLimit(int slot) {
        return delegate.getSlotLimit(slot);
    }

    @Override
    public boolean isItemValid(int slot, @NotNull ItemStack stack) {
        return false; // never allow insertion
    }
}
