package net.langball.coffee.gui;

import net.langball.coffee.block.entity.SodaMachineBlockEntity;
import net.langball.coffee.init.ModBlocks;
import net.langball.coffee.init.ModMenuTypes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.SlotItemHandler;

public class ContainerSodaMachine extends AbstractContainerMenu {
    private final SodaMachineBlockEntity blockEntity;
    private final ContainerLevelAccess access;

    public ContainerSodaMachine(int id, Inventory playerInv, SodaMachineBlockEntity be) {
        super(ModMenuTypes.SODA_MACHINE.get(), id);
        this.blockEntity = be;
        this.access = ContainerLevelAccess.create(be.getLevel(), be.getBlockPos());

        var handler = be.getItemHandler();
        addSlot(new SlotItemHandler(handler, 0, 30, 20));  // Bottle
        addSlot(new SlotItemHandler(handler, 1, 52, 20));  // Soda
        addSlot(new SlotItemHandler(handler, 2, 74, 20));  // Flavor
        addSlot(new SlotItemHandler(handler, 3, 130, 38) { // Output
            @Override
            public boolean mayPlace(ItemStack stack) { return false; }
        });

        addPlayerSlots(playerInv, 8, 84);
    }

    public ContainerSodaMachine(int id, Inventory playerInv, FriendlyByteBuf data) {
        this(id, playerInv, (SodaMachineBlockEntity) playerInv.player.level().getBlockEntity(data.readBlockPos()));
    }

    @Override
    public boolean stillValid(Player player) {
        return stillValid(access, player, ModBlocks.SODA_MACHINE.get());
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot.hasItem()) {
            ItemStack stack = slot.getItem();
            itemstack = stack.copy();
            if (index < 4) {
                if (!this.moveItemStackTo(stack, 4, 40, true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.moveItemStackTo(stack, 0, 3, false)) {
                return ItemStack.EMPTY;
            }
            if (stack.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
        }
        return itemstack;
    }

    private void addPlayerSlots(Inventory inv, int x, int y) {
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                addSlot(new Slot(inv, col + row * 9 + 9, x + col * 18, y + row * 18));
            }
        }
        for (int col = 0; col < 9; col++) {
            addSlot(new Slot(inv, col, x + col * 18, y + 58));
        }
    }
}
