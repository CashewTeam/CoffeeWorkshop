package net.langball.coffee.gui;

import net.langball.coffee.block.entity.MachineBlockEntity;
import net.langball.coffee.gui.slot.SlotMachineResult;
import net.langball.coffee.recipes.MachineRecipe;
import net.minecraft.core.BlockPos;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;

import java.util.Set;

/**
 * Common menu base for all five processing machines.
 *
 * <p>Provides player inventory layout, {@link #stillValid}, shift-click,
 * and shared helpers.  Subclasses declare slot positions and machine-
 * specific slot types via {@link #addMachineSlots()}.
 */
public abstract class AbstractMachineMenu extends AbstractContainerMenu {

    protected final IItemHandler itemHandler;
    protected final ContainerData data;
    protected final MachineBlockEntity blockEntity;
    protected final Level level;

    private static final int HOTBAR_SLOT_COUNT = 9;
    private static final int PLAYER_INV_ROW_COUNT = 3;
    private static final int PLAYER_INV_COL_COUNT = 9;

    protected final int playerInvStartIndex;
    protected final int hotbarStartIndex;

    protected AbstractMachineMenu(MenuType<?> type, int id, Inventory inv,
                                  IItemHandler handler, ContainerData data,
                                  MachineBlockEntity be, int machineSlotCount) {
        super(type, id);
        this.itemHandler = handler;
        this.data = data;
        this.blockEntity = be;
        this.level = inv.player.level();

        checkContainerDataCount(data, 4);
        addMachineSlots();
        this.playerInvStartIndex = machineSlotCount;
        addPlayerInventory(inv, machineSlotCount);
        this.hotbarStartIndex = playerInvStartIndex + PLAYER_INV_ROW_COUNT * PLAYER_INV_COL_COUNT;
        this.addDataSlots(data);
    }

    // ---- Abstract contract (subclass provides) ----------------------------

    protected abstract void addMachineSlots();
    protected abstract RecipeType<?> getRecipeType();
    protected abstract Set<Block> getValidBlocks();
    protected abstract int getOutputSlotIndex();
    protected abstract boolean isFuelItem(ItemStack stack);

    /**
     * Returns the fuel slot index, or -1 if this machine has no fuel slot.
     */
    protected int getFuelSlotIndex() {
        return -1;
    }

    // ---- Slot layout helpers --------------------------------------------

    protected void addPlayerInventory(Inventory inv, int startIndex) {
        for (int row = 0; row < PLAYER_INV_ROW_COUNT; row++) {
            for (int col = 0; col < PLAYER_INV_COL_COUNT; col++) {
                this.addSlot(new Slot(inv, col + row * 9 + 9,
                        8 + col * 18, 84 + row * 18));
            }
        }
        for (int col = 0; col < HOTBAR_SLOT_COUNT; col++) {
            this.addSlot(new Slot(inv, col, 8 + col * 18, 142));
        }
    }

    // ---- Validation -----------------------------------------------------

    @Override
    public boolean stillValid(Player player) {
        if (blockEntity == null || blockEntity.isRemoved()) return false;
        return ContainerLevelAccess.create(level, blockEntity.getBlockPos())
                .evaluate((world, pos) -> {
                    return getValidBlocks().contains(world.getBlockState(pos).getBlock())
                            && player.distanceToSqr(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5) <= 64.0;
                }, true);
    }

    // ---- Recipe helper --------------------------------------------------

    protected boolean hasRecipe(ItemStack stack) {
        return level.getRecipeManager()
                .getRecipeFor((RecipeType) getRecipeType(), new SimpleContainer(stack), level)
                .isPresent();
    }

    // ---- Shift-click ----------------------------------------------------

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);

        if (slot != null && slot.hasItem()) {
            ItemStack itemstack1 = slot.getItem();
            itemstack = itemstack1.copy();

            int outputIdx = getOutputSlotIndex();
            int totalSlots = this.slots.size();

            if (index == outputIdx) {
                if (!this.moveItemStackTo(itemstack1, playerInvStartIndex, totalSlots, true)) {
                    return ItemStack.EMPTY;
                }
                slot.onQuickCraft(itemstack1, itemstack);
            } else if (index < playerInvStartIndex) {
                if (!this.moveItemStackTo(itemstack1, playerInvStartIndex, totalSlots, false)) {
                    return ItemStack.EMPTY;
                }
            } else {
                if (hasRecipe(itemstack1)) {
                    if (!this.moveItemStackTo(itemstack1, 0, 1, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (isFuelItem(itemstack1)) {
                    int fuelSlot = getFuelSlotIndex();
                    if (fuelSlot >= 0) {
                        if (!this.moveItemStackTo(itemstack1, fuelSlot, fuelSlot + 1, false)) {
                            return ItemStack.EMPTY;
                        }
                    } else if (index >= playerInvStartIndex && index < hotbarStartIndex) {
                        if (!this.moveItemStackTo(itemstack1, hotbarStartIndex, totalSlots, false)) {
                            return ItemStack.EMPTY;
                        }
                    } else if (index >= hotbarStartIndex && index < totalSlots) {
                        if (!this.moveItemStackTo(itemstack1, playerInvStartIndex, hotbarStartIndex, false)) {
                            return ItemStack.EMPTY;
                        }
                    }
                } else if (index >= playerInvStartIndex && index < hotbarStartIndex) {
                    if (!this.moveItemStackTo(itemstack1, hotbarStartIndex, totalSlots, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (index >= hotbarStartIndex && index < totalSlots) {
                    if (!this.moveItemStackTo(itemstack1, playerInvStartIndex, hotbarStartIndex, false)) {
                        return ItemStack.EMPTY;
                    }
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

    // ---- Shared helpers --------------------------------------------------

    /**
     * Resolves an IItemHandler from the BlockEntity at the given position.
     * Falls back to an empty handler if no BE or capability is present.
     */
    protected static IItemHandler getItemHandlerAt(Inventory inv, BlockPos pos, int size) {
        BlockEntity be = inv.player.level().getBlockEntity(pos);
        if (be != null) {
            return be.getCapability(ForgeCapabilities.ITEM_HANDLER)
                    .resolve()
                    .orElseGet(() -> new ItemStackHandler(size));
        }
        return new ItemStackHandler(size);
    }

    /**
     * Resolves the MachineBlockEntity at the given position, or throws
     * a clear error if missing or of the wrong type.
     */
    protected static MachineBlockEntity getMachineAt(Inventory inv, BlockPos pos) {
        BlockEntity be = inv.player.level().getBlockEntity(pos);
        if (be instanceof MachineBlockEntity mbe) {
            return mbe;
        }
        throw new IllegalStateException(
                "Expected MachineBlockEntity at " + pos + ", got " +
                (be != null ? be.getClass().getName() : "null"));
    }

    // ---- Data access ----------------------------------------------------

    public ContainerData getData() {
        return data;
    }
}
