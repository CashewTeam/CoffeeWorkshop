package net.langball.coffee.block.entity;

import net.langball.coffee.CoffeeWork;
import net.langball.coffee.block.MachineBlock;
import net.langball.coffee.init.ModBlockEntities;
import net.langball.coffee.init.ModRecipeTypes;
import net.langball.coffee.recipes.MachineRecipe;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Coffee Machine block entity — brews drinks from coffee powder etc.
 *
 * <p>Self-powered: has no fuel slot.  When idle and a valid recipe is
 * present, the machine starts a self-cycle for exactly
 * {@code recipe.cookingTime()} ticks.  The {@code burnTime} field is
 * repurposed as the self-cycle timer (matches the recipe's duration).
 *
 * <p>Extends {@link AbstractProcessingBlockEntity} directly (not the
 * fueled variant) because it does not consume external fuel.
 */
public class CoffeeMachineBlockEntity extends AbstractProcessingBlockEntity {

    public static final int SLOT_INPUT = 0;
    public static final int SLOT_OUTPUT = 1;
    private static final int INVENTORY_SIZE = 2;

    public CoffeeMachineBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.COFFEE_MACHINE.get(), pos, state);
        this.itemHandler = new ItemStackHandler(INVENTORY_SIZE) {
            @Override
            protected void onContentsChanged(int slot) {
                setChanged();
                if (slot == SLOT_INPUT) activeRecipeId = null;
            }

            @Override
            public boolean isItemValid(int slot, @NotNull ItemStack stack) {
                return slot != SLOT_OUTPUT;
            }
        };
    }

    @Override
    protected RecipeType<MachineRecipe> getRecipeType() {
        return ModRecipeTypes.COFFEE_BREWING;
    }

    @Override
    protected int getInputSlot() { return SLOT_INPUT; }

    @Override
    protected int getOutputSlot() { return SLOT_OUTPUT; }

    // ---- MachineBlockEntity slot groups for automation -----------------

    @Override
    protected int[] getInputSlots() { return new int[]{SLOT_INPUT}; }

    @Override
    protected int[] getFuelSlots() { return new int[0]; } // no fuel slot

    @Override
    protected int[] getOutputSlots() { return new int[]{SLOT_OUTPUT}; }

    public boolean isBurning() {
        return burnTime > 0;
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

    @Override
    public void tick(Level level, BlockPos pos, BlockState state) {
        if (level.isClientSide) return;

        boolean wasBurning = burnTime > 0;
        boolean dirty = false;

        // Self-cycle timer
        if (burnTime > 0) {
            burnTime--;
        }

        MachineRecipe recipe = getCurrentRecipe();
        var prevRecipeId = activeRecipeId;
        boolean canProcess = recipe != null && canAcceptResult(recipe);

        // Start a new self-cycle when idle and work is available
        if (burnTime == 0 && canProcess) {
            burnTime = recipe.cookingTime();
            burnTimeTotal = recipe.cookingTime();
            totalCookTime = recipe.cookingTime();
            dirty = true;
        }

        // Detect recipe change
        if (recipe != null && prevRecipeId != null && !prevRecipeId.equals(recipe.getId())) {
            onRecipeChanged(prevRecipeId, recipe.getId());
            dirty = true;
        }

        // Advance progress
        if (burnTime > 0 && canProcess) {
            cookTime++;
            if (cookTime >= totalCookTime) {
                cookTime = 0;
                totalCookTime = recipe.cookingTime();
                processRecipe(recipe);
                dirty = true;
            }
        } else if (!canProcess && cookTime > 0) {
            if (recipe == null) {
                cookTime = 0;
                dirty = true;
            }
        }

        // Sync LIT
        if (wasBurning != (burnTime > 0)) {
            dirty = true;
            level.setBlock(pos, state.setValue(MachineBlock.LIT, burnTime > 0),
                    Block.UPDATE_ALL);
        }

        if (dirty) {
            setChanged(level, pos, state);
        }
    }
}
