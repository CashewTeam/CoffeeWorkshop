package net.langball.coffee.gui.slot;

import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

public class SlotICEMachineOutput extends SlotItemHandler {
    public SlotICEMachineOutput(IItemHandler handler, int slot, int x, int y) {
        super(handler, slot, x, y);
    }

    @Override
    public boolean mayPlace(ItemStack stack) {
        return false;
    }
}
