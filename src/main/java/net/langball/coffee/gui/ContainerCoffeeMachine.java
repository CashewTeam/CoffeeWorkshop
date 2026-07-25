package net.langball.coffee.gui;

import net.langball.coffee.gui.slot.SlotCoffeeMachineOutput;
import net.langball.coffee.init.ModBlocks;
import net.langball.coffee.init.ModMenuTypes;
import net.langball.coffee.recipes.blocks.CoffeeMachineRecipes;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.SlotItemHandler;

import java.util.Set;

public class ContainerCoffeeMachine extends AbstractContainerMenu {
    private final IItemHandler itemHandler;
    public final ContainerData data;
    private final BlockEntity blockEntity;
    private final Level level;

    /** Client-side constructor — called from IForgeMenuType factory. */
    public ContainerCoffeeMachine(int id, Inventory inv, BlockPos pos) {
        this(id, inv,
                getItemHandlerAt(inv, pos, 2),
                new SimpleContainerData(4),
                inv.player.level().getBlockEntity(pos));
    }

    /** Server-side constructor — called from BlockEntity.createMenu(). */
    public ContainerCoffeeMachine(int id, Inventory inv, IItemHandler handler, ContainerData data, BlockEntity be) {
        super(ModMenuTypes.COFFEE_MACHINE.get(), id);
        this.itemHandler = handler;
        this.data = data;
        this.blockEntity = be;
        this.level = inv.player.level();

        checkContainerDataCount(data, 4);

        // Machine slots: input (0), output (1) — no fuel slot, uses internal heating
        this.addSlot(new SlotItemHandler(handler, 0, 36, 35));
        this.addSlot(new SlotCoffeeMachineOutput(handler, 1, 116, 35));

        // Main player inventory (3 rows x 9 columns)
        for (int i = 0; i < 3; i++)
            for (int j = 0; j < 9; j++)
                this.addSlot(new Slot(inv, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));

        // Hotbar (1 row x 9 columns)
        for (int k = 0; k < 9; k++)
            this.addSlot(new Slot(inv, k, 8 + k * 18, 142));

        this.addDataSlots(data);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);

        if (slot != null && slot.hasItem()) {
            ItemStack itemstack1 = slot.getItem();
            itemstack = itemstack1.copy();

            if (index == 1) {
                // Output slot → player inventory (indices 2..38)
                if (!this.moveItemStackTo(itemstack1, 2, 38, true)) {
                    return ItemStack.EMPTY;
                }
                slot.onQuickCraft(itemstack1, itemstack);
            } else if (index != 0) {
                // From player inventory or hotbar
                if (!CoffeeMachineRecipes.instance().getSmeltingResult(itemstack1).isEmpty()) {
                    // Valid recipe input → input slot (index 0)
                    if (!this.moveItemStackTo(itemstack1, 0, 1, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (index >= 2 && index < 29) {
                    // Main inventory → hotbar
                    if (!this.moveItemStackTo(itemstack1, 29, 38, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (index >= 29 && index < 38) {
                    // Hotbar → main inventory
                    if (!this.moveItemStackTo(itemstack1, 2, 29, false)) {
                        return ItemStack.EMPTY;
                    }
                }
            } else {
                // Input slot (0) → player inventory
                if (!this.moveItemStackTo(itemstack1, 2, 38, false)) {
                    return ItemStack.EMPTY;
                }
            }

            if (itemstack1.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }

            if (itemstack1.getCount() == itemstack.getCount()) {
                return ItemStack.EMPTY;
            }

            slot.onTake(player, itemstack1);
        }

        return itemstack;
    }

    @Override
    public boolean stillValid(Player player) {
        if (blockEntity == null || blockEntity.isRemoved()) return false;
        return ContainerLevelAccess.create(level, blockEntity.getBlockPos())
                .evaluate((world, pos) -> {
                    Set<Block> validBlocks = Set.of(
                            ModBlocks.COFFEE_MACHINE.get());
                    return validBlocks.contains(world.getBlockState(pos).getBlock())
                            && player.distanceToSqr(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5) <= 64.0;
                }, true);
    }

    private static IItemHandler getItemHandlerAt(Inventory inv, BlockPos pos, int size) {
        BlockEntity be = inv.player.level().getBlockEntity(pos);
        if (be != null) {
            return be.getCapability(ForgeCapabilities.ITEM_HANDLER)
                    .resolve()
                    .orElseGet(() -> new ItemStackHandler(size));
        }
        return new ItemStackHandler(size);
    }
}
