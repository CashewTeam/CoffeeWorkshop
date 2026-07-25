package net.langball.coffee.gui.slot;

import net.langball.coffee.block.entity.IcecreamMachineBlockEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

/**
 * Ice-fuel slot for the Ice-cream Machine.
 *
 * <p>Validation is delegated to
 * {@link IcecreamMachineBlockEntity#COOLING_FUEL}, keeping the GUI slot
 * in sync with the block entity's fuel policy.
 */
public class SlotMachineICE extends SlotItemHandler {
    public SlotMachineICE(IItemHandler handler, int slot, int x, int y) {
        super(handler, slot, x, y);
    }

    @Override
    public boolean mayPlace(ItemStack stack) {
        return isItemFuel(stack);
    }

    /**
     * Checks if the given stack is valid cooling fuel.
     * Uses the same registry as {@link IcecreamMachineBlockEntity}.
     */
    public static boolean isItemFuel(ItemStack stack) {
        return IcecreamMachineBlockEntity.COOLING_FUEL.containsKey(stack.getItem());
    }
}
