package net.langball.coffee.block.entity;

import net.langball.coffee.CoffeeWork;
import net.langball.coffee.init.ModBlockEntities;
import net.langball.coffee.init.ModRecipeTypes;
import net.langball.coffee.recipes.ProcessingRecipe;
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
 * Coffee Machine block entity — brews drinks from coffee powder, water/milk, and cups.
 *
 * <p>Self-powered (no fuel slot).  Four-slot layout:
 * <ul>
 *   <li>Slot 0 — coffee powder (base ingredient)</li>
 *   <li>Slot 1 — water bucket / milk bucket (modifier, or empty for Espresso)</li>
 *   <li>Slot 2 — cup (container)</li>
 *   <li>Slot 3 — output</li>
 * </ul>
 */
public class CoffeeMachineBlockEntity extends AbstractProcessingBlockEntity {

    public static final int SLOT_BASE = 0;
    public static final int SLOT_MODIFIER = 1;
    public static final int SLOT_CUP = 2;
    public static final int SLOT_OUTPUT = 3;
    private static final int INVENTORY_SIZE = 4;
    /** Version tag for NBT migration from old 2-slot layout. */
    private static final int CURRENT_INVENTORY_VERSION = 1;

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
    protected RecipeType<?> getRecipeType() {
        return ModRecipeTypes.COFFEE_BREWING;
    }

    @Override
    protected int getInputSlot() { return SLOT_BASE; }

    @Override
    protected int getOutputSlot() { return SLOT_OUTPUT; }

    @Override
    protected int[] getInputSlots() { return new int[]{SLOT_BASE, SLOT_MODIFIER, SLOT_CUP}; }

    @Override
    protected int[] getFuelSlots() { return new int[0]; }

    @Override
    protected int[] getOutputSlots() { return new int[]{SLOT_OUTPUT}; }

    public boolean isBurning() {
        return burnTime > 0;
    }

    @Override
    protected void startProcessingPower(ProcessingRecipe recipe) {
        burnTime = recipe.cookingTime();
        burnTimeTotal = recipe.cookingTime();
    }

    @Override
    protected boolean shouldBeLit(ProcessingRecipe recipe, boolean canProcess) {
        return canProcess && hasProcessingPower();
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
        tickProcessing(level, pos, state);
    }
}
