package net.langball.coffee.gui.slot;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

public class SlotMachineICE extends SlotItemHandler {
    public SlotMachineICE(IItemHandler handler, int slot, int x, int y) {
        super(handler, slot, x, y);
    }

    @Override
    public boolean mayPlace(ItemStack stack) {
        return isItemFuel(stack);
    }

    /**
     * Checks if the given stack is a valid fuel for the ice cream machine.
     * Accepts items that Forge considers fuel, plus ice/lava-related items.
     */
    public static boolean isItemFuel(ItemStack stack) {
        // Accept standard furnace fuels
        if (ForgeHooks.getBurnTime(stack, null) > 0) {
            return true;
        }
        // Also accept ice-specific items (cold source for the ice cream machine)
        return stack.is(Items.ICE)
                || stack.is(Items.PACKED_ICE)
                || stack.is(Items.BLUE_ICE);
    }
}
