package net.langball.coffee.gui;

import net.langball.coffee.gui.slot.SlotICEMachineOutput;
import net.langball.coffee.gui.slot.SlotMachineICE;
import net.langball.coffee.init.ModBlocks;
import net.langball.coffee.init.ModMenuTypes;
import net.langball.coffee.recipes.blocks.IcecreamMachineRecipes;
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
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.SlotItemHandler;

import java.util.Set;

public class ContainerIcecreamMachine extends AbstractContainerMenu {
    private final IItemHandler itemHandler;
    public final ContainerData data;
    private final BlockEntity blockEntity;
    private final Level level;

    /** Client-side constructor — called from IForgeMenuType factory. */
    public ContainerIcecreamMachine(int id, Inventory inv, BlockPos pos) {
        this(id, inv,
                getItemHandlerAt(inv, pos, 3),
                new SimpleContainerData(4),
                inv.player.level().getBlockEntity(pos));
    }

    /** Server-side constructor — called from BlockEntity.createMenu(). */
    public ContainerIcecreamMachine(int id, Inventory inv, IItemHandler handler, ContainerData data, BlockEntity be) {
        super(ModMenuTypes.ICECREAM_MACHINE.get(), id);
        this.itemHandler = handler;
        this.data = data;
        this.blockEntity = be;
        this.level = inv.player.level();

        checkContainerDataCount(data, 4);

        // Machine slots: input (0), ice/fuel (1), output (2)
        // Positions match the icecream_machine.png texture layout
        this.addSlot(new SlotItemHandler(handler, 0, 56, 17));     // Input
        this.addSlot(new SlotMachineICE(handler, 1, 112, 17));     // Ice/Fuel
        this.addSlot(new SlotICEMachineOutput(handler, 2, 83, 57)); // Output

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

            if (index == 2) {
                // Output slot → player inventory
                if (!this.moveItemStackTo(itemstack1, 3, 39, true)) {
                    return ItemStack.EMPTY;
                }
                slot.onQuickCraft(itemstack1, itemstack);
            } else if (index != 0 && index != 1) {
                // From player inventory or hotbar
                if (!IcecreamMachineRecipes.instance().getSmeltingResult(itemstack1).isEmpty()) {
                    // Valid recipe input → input slot (index 0)
                    if (!this.moveItemStackTo(itemstack1, 0, 1, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (ForgeHooks.getBurnTime(itemstack1, null) > 0
                        || SlotMachineICE.isItemFuel(itemstack1)) {
                    // Ice/fuel → fuel slot (index 1)
                    if (!this.moveItemStackTo(itemstack1, 1, 2, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (index >= 3 && index < 30) {
                    // Main inventory → hotbar
                    if (!this.moveItemStackTo(itemstack1, 30, 39, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (index >= 30 && index < 39) {
                    // Hotbar → main inventory
                    if (!this.moveItemStackTo(itemstack1, 3, 30, false)) {
                        return ItemStack.EMPTY;
                    }
                }
            } else {
                // Input (0) or fuel (1) slot → player inventory
                if (!this.moveItemStackTo(itemstack1, 3, 39, false)) {
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
                            ModBlocks.ICECREAM_MACHINE.get());
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
