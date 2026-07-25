package net.langball.coffee.block.entity;

import net.langball.coffee.CoffeeWork;
import net.langball.coffee.block.MachineBlock;
import net.langball.coffee.init.ModBlockEntities;
import net.langball.coffee.recipes.blocks.CoffeeMachineRecipes;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CoffeeMachineBlockEntity extends MachineBlockEntity {
    public static final int SLOT_INPUT = 0;
    public static final int SLOT_OUTPUT = 1;
    private static final int INVENTORY_SIZE = 2;

    public CoffeeMachineBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.COFFEE_MACHINE.get(), pos, state);
        this.itemHandler = new ItemStackHandler(INVENTORY_SIZE) {
            @Override
            protected void onContentsChanged(int slot) {
                setChanged();
            }

            @Override
            public boolean isItemValid(int slot, @NotNull ItemStack stack) {
                return slot != SLOT_OUTPUT;
            }
        };
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("container." + CoffeeWork.MODID + ".coffee_machine");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
        return new net.langball.coffee.gui.ContainerCoffeeMachine(id, inventory, itemHandler, data, this);
    }

    public boolean isBurning() {
        return burnTime > 0;
    }

    /** Counterpart to getCookTime() (moved to instance-level for consistency). */
    public static int getCookTime() {
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
            ItemStack input = itemHandler.getStackInSlot(SLOT_INPUT);
            ItemStack output = itemHandler.getStackInSlot(SLOT_OUTPUT);
            ItemStack result = !input.isEmpty() ? CoffeeMachineRecipes.instance().getSmeltingResult(input) : ItemStack.EMPTY;

            boolean canSmelt = !result.isEmpty()
                    && (output.isEmpty()
                    || (ItemStack.isSameItemSameTags(output, result)
                    && output.getCount() + result.getCount() <= output.getMaxStackSize()));

            /* Self-powered: when idle and work is available, start a burn cycle.
             * CoffeeMachine has no fuel slot — it always runs for getCookTime() ticks. */
            if (burnTime == 0 && canSmelt) {
                burnTime = getCookTime();
                burnTimeTotal = getCookTime();
                totalCookTime = getCookTime();
                dirty = true;
                setChanged();
            }

            if (burnTime > 0 && canSmelt) {
                cookTime++;
                if (cookTime >= totalCookTime) {
                    cookTime = 0;
                    totalCookTime = getCookTime();
                    smeltItem(input, result, output);
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

    private void smeltItem(ItemStack input, ItemStack result, ItemStack output) {
        if (output.isEmpty()) {
            itemHandler.setStackInSlot(SLOT_OUTPUT, result.copy());
        } else {
            int newCount = Math.min(output.getCount() + result.getCount(),
                    output.getMaxStackSize());
            output.setCount(newCount);
        }
        ItemStack containerItem = input.getCraftingRemainingItem();
        if (!containerItem.isEmpty()) {
            itemHandler.setStackInSlot(SLOT_INPUT, containerItem);
        } else {
            input.shrink(1);
        }
    }
}
