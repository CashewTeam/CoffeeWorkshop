package net.langball.coffee.block.entity;

import net.langball.coffee.CoffeeWork;
import net.langball.coffee.block.MachineBlock;
import net.langball.coffee.init.ModBlockEntities;
import net.langball.coffee.recipes.blocks.RollerRecipes;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Containers;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class RollerBlockEntity extends MachineBlockEntity {
    public static final int SLOT_INPUT = 0;
    public static final int SLOT_FUEL = 1;
    public static final int SLOT_OUTPUT = 2;
    private static final int INVENTORY_SIZE = 3;

    public RollerBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.ROLLER.get(), pos, state);
        this.itemHandler = new ItemStackHandler(INVENTORY_SIZE) {
            @Override
            protected void onContentsChanged(int slot) {
                setChanged();
            }

            @Override
            public boolean isItemValid(int slot, @NotNull ItemStack stack) {
                return switch (slot) {
                    case SLOT_OUTPUT -> false;
                    case SLOT_FUEL -> isItemFuel(stack);
                    default -> true;
                };
            }
        };
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("container." + CoffeeWork.MODID + ".roller");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
        return new net.langball.coffee.gui.ContainerRoller(id, inventory, itemHandler, data, this);
    }

    public static boolean isItemFuel(ItemStack stack) {
        return ForgeHooks.getBurnTime(stack, null) > 0;
    }

    private static int getItemBurnTime(ItemStack stack) {
        return ForgeHooks.getBurnTime(stack, null);
    }

    public static int getCookTime(ItemStack stack) {
        return 200;
    }

    @Override
    public void tick(Level level, BlockPos pos, BlockState state) {
        boolean wasBurning = burnTime > 0;
        boolean dirty = false;

        if (burnTime > 0) {
            burnTime--;
        }

        if (!level.isClientSide) {
            ItemStack fuel = itemHandler.getStackInSlot(SLOT_FUEL);
            ItemStack input = itemHandler.getStackInSlot(SLOT_INPUT);
            ItemStack output = itemHandler.getStackInSlot(SLOT_OUTPUT);
            ItemStack result = !input.isEmpty() ? RollerRecipes.instance().getSmeltingResult(input) : ItemStack.EMPTY;

            boolean canSmelt = !result.isEmpty()
                    && (output.isEmpty()
                    || (ItemStack.isSameItemSameTags(output, result)
                    && output.getCount() + result.getCount() <= output.getMaxStackSize()));

            if (burnTime == 0 && canSmelt && !fuel.isEmpty()) {
                int fuelBurnTime = ForgeHooks.getBurnTime(fuel, null);
                if (fuelBurnTime > 0) {
                    burnTime = fuelBurnTime;
                    burnTimeTotal = fuelBurnTime;
                    totalCookTime = getCookTime(input);
                    ItemStack remainder = fuel.getCraftingRemainingItem();
                    fuel.shrink(1);
                    if (fuel.isEmpty()) {
                        itemHandler.setStackInSlot(SLOT_FUEL, remainder);
                    } else if (!remainder.isEmpty()) {
                        Containers.dropItemStack(level, worldPosition.getX(), worldPosition.getY(), worldPosition.getZ(), remainder);
                    }
                    dirty = true;
                }
            }

            if (burnTime > 0 && canSmelt) {
                cookTime++;
                if (cookTime >= totalCookTime) {
                    totalCookTime = getCookTime(input);
                    cookTime = 0;
                    if (output.isEmpty()) {
                        itemHandler.setStackInSlot(SLOT_OUTPUT, result.copy());
                    } else {
                        int newCount = Math.min(output.getCount() + result.getCount(),
                                output.getMaxStackSize());
                        output.setCount(newCount);
                    }
                    input.shrink(1);
                    dirty = true;
                }
            } else {
                if (cookTime != 0) {
                    cookTime = 0;
                    dirty = true;
                }
            }

            if (wasBurning != (burnTime > 0)) {
                dirty = true;
                level.setBlock(pos, state.setValue(MachineBlock.LIT, burnTime > 0),
                        Block.UPDATE_ALL);
            }
        }

        if (dirty) {
            setChanged(level, pos, state);
        }
    }
}
