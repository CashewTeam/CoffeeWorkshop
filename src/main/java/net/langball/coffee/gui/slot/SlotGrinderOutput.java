package net.langball.coffee.gui.slot;

import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

public class SlotGrinderOutput extends SlotItemHandler {
    public SlotGrinderOutput(IItemHandler handler, int slot, int x, int y) {
        super(handler, slot, x, y);
    }

    @Override
    public boolean mayPlace(ItemStack stack) {
        return false;
    }
}
