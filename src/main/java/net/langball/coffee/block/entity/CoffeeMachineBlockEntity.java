package net.langball.coffee.block.entity;

import net.langball.coffee.CoffeeWork;
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
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Coffee Machine block entity — brews drinks from coffee powder etc.
 *
 * <p>Self-powered: has no fuel slot.  Overrides
 * {@link #startProcessingPower(MachineRecipe)} to begin a self-cycle
 * for exactly {@code recipe.cookingTime()} ticks.  All other tick
 * logic (recipe resolution, progress, output, LIT) is inherited from
 * {@link AbstractProcessingBlockEntity#tickProcessing(Level, BlockPos, BlockState)}.
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
    protected int[] getFuelSlots() { return new int[0]; }

    @Override
    protected int[] getOutputSlots() { return new int[]{SLOT_OUTPUT}; }

    public boolean isBurning() {
        return burnTime > 0;
    }

    // ---- Self-powered hooks ------------------------------------------------

    @Override
    protected void startProcessingPower(MachineRecipe recipe) {
        burnTime = recipe.cookingTime();
        burnTimeTotal = recipe.cookingTime();
    }

    /**
     * Coffee Machine turns off LIT immediately when output is blocked,
     * rather than waiting for the current self-cycle to expire.
     */
    @Override
    protected boolean shouldBeLit(MachineRecipe recipe, boolean canProcess) {
        return canProcess && hasProcessingPower();
    }

    // ---- MenuProvider ------------------------------------------------------

    @Override
    public Component getDisplayName() {
        return Component.translatable("container." + CoffeeWork.MODID + ".coffee_machine");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
        return new net.langball.coffee.gui.ContainerCoffeeMachine(id, inventory, itemHandler, data, this);
    }

    // ---- Tick --------------------------------------------------------------

    @Override
    public void tick(Level level, BlockPos pos, BlockState state) {
        tickProcessing(level, pos, state);
    }
}
