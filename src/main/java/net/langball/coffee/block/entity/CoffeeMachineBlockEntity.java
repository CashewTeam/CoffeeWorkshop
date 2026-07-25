package net.langball.coffee.block.entity;

import net.langball.coffee.CoffeeWork;
import net.langball.coffee.block.MachineBlock;
import net.langball.coffee.init.ModBlockEntities;
import net.langball.coffee.init.ModRecipeTypes;
import net.langball.coffee.recipes.MachineRecipe;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.SimpleContainer;
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

    @Nullable
    private MachineRecipe cachedRecipe;

    public CoffeeMachineBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.COFFEE_MACHINE.get(), pos, state);
        this.itemHandler = new ItemStackHandler(INVENTORY_SIZE) {
            @Override
            protected void onContentsChanged(int slot) {
                setChanged();
                if (slot == SLOT_INPUT) cachedRecipe = null;
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

    // ─── Recipe lookup ─────────────────────────────────────────────────────

    @Nullable
    private MachineRecipe getCurrentRecipe() {
        ItemStack input = itemHandler.getStackInSlot(SLOT_INPUT);
        if (input.isEmpty()) {
            cachedRecipe = null;
            return null;
        }
        if (cachedRecipe != null && cachedRecipe.matches(new SimpleContainer(input), getLevel())) {
            return cachedRecipe;
        }
        cachedRecipe = getLevel().getRecipeManager()
                .getRecipeFor(ModRecipeTypes.COFFEE_BREWING, new SimpleContainer(input), getLevel())
                .orElse(null);
        return cachedRecipe;
    }

    // ─── Tick body ─────────────────────────────────────────────────────────

    @Override
    public void tick(Level level, BlockPos pos, BlockState state) {
        if (level.isClientSide) return;

        boolean wasBurning = burnTime > 0;
        boolean dirty = false;

        if (burnTime > 0) {
            burnTime--;
        }

        ItemStack output = itemHandler.getStackInSlot(SLOT_OUTPUT);
        MachineRecipe recipe = getCurrentRecipe();

        boolean canSmelt = recipe != null
                && (output.isEmpty()
                || (ItemStack.isSameItemSameTags(output, recipe.result())
                && output.getCount() + recipe.result().getCount() <= output.getMaxStackSize()));

        /* Self-powered: when idle and work is available, start a burn cycle.
         * CoffeeMachine has no fuel slot — it always runs for cookingTime ticks. */
        if (burnTime == 0 && canSmelt) {
            burnTime = recipe.cookingTime();
            burnTimeTotal = recipe.cookingTime();
            totalCookTime = recipe.cookingTime();
            dirty = true;
        }

        if (burnTime > 0 && canSmelt) {
            cookTime++;
            if (cookTime >= totalCookTime) {
                cookTime = 0;
                totalCookTime = recipe.cookingTime();
                if (output.isEmpty()) {
                    itemHandler.setStackInSlot(SLOT_OUTPUT, recipe.result().copy());
                } else {
                    int newCount = Math.min(output.getCount() + recipe.result().getCount(),
                            output.getMaxStackSize());
                    output.setCount(newCount);
                }
                consumeOneWithRemainder(SLOT_INPUT);
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

        if (dirty) {
            setChanged(level, pos, state);
        }
    }

    private void smeltItem(MachineRecipe recipe) {
        ItemStack input = itemHandler.getStackInSlot(SLOT_INPUT);
        ItemStack output = itemHandler.getStackInSlot(SLOT_OUTPUT);
        if (output.isEmpty()) {
            itemHandler.setStackInSlot(SLOT_OUTPUT, recipe.result().copy());
        } else {
            int newCount = Math.min(output.getCount() + recipe.result().getCount(),
                    output.getMaxStackSize());
            output.setCount(newCount);
        }
        ItemStack container = input.getCraftingRemainingItem();
        if (!container.isEmpty()) {
            itemHandler.setStackInSlot(SLOT_INPUT, container);
        } else {
            input.shrink(1);
        }
    }
}
