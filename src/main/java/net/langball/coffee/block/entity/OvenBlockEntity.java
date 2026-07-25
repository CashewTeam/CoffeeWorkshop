package net.langball.coffee.block.entity;

import net.langball.coffee.CoffeeWork;
import net.langball.coffee.init.ModBlockEntities;
import net.langball.coffee.init.ModRecipeTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Oven block entity — bakes dough into bread, cookies, etc.
 *
 * <p>Uses vanilla furnace fuels.  Shares the fuel + processing state
 * machine via {@link AbstractFueledProcessingBlockEntity}.
 */
public class OvenBlockEntity extends AbstractFueledProcessingBlockEntity {

    public static final int SLOT_INPUT = 0;
    public static final int SLOT_FUEL = 1;
    public static final int SLOT_OUTPUT = 2;
    private static final int INVENTORY_SIZE = 3;

    public OvenBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.OVEN.get(), pos, state);
        this.itemHandler = new ItemStackHandler(INVENTORY_SIZE) {
            @Override
            protected void onContentsChanged(int slot) {
                setChanged();
            }

            @Override
            public boolean isItemValid(int slot, @NotNull ItemStack stack) {
                return switch (slot) {
                    case SLOT_OUTPUT -> false;
                    case SLOT_FUEL -> isValidFuel(stack);
                    default -> true;
                };
            }
        };
    }

    @Override
    protected RecipeType<net.langball.coffee.recipes.MachineRecipe> getRecipeType() {
        return ModRecipeTypes.OVEN_BAKING;
    }

    @Override
    protected int getInputSlot() { return SLOT_INPUT; }

    @Override
    protected int getOutputSlot() { return SLOT_OUTPUT; }

    // ---- MachineBlockEntity slot groups for automation -----------------

    @Override
    protected int[] getInputSlots() { return new int[]{SLOT_INPUT}; }

    @Override
    protected int[] getFuelSlots() { return new int[]{SLOT_FUEL}; }

    @Override
    protected int[] getOutputSlots() { return new int[]{SLOT_OUTPUT}; }

    @Override
    protected int getFuelSlot() { return SLOT_FUEL; }

    @Override
    protected int getFuelTime(ItemStack stack) {
        return ForgeHooks.getBurnTime(stack, null);
    }

    @Override
    protected boolean isValidFuel(ItemStack stack) {
        return ForgeHooks.getBurnTime(stack, null) > 0;
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("container." + CoffeeWork.MODID + ".oven");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
        return new net.langball.coffee.gui.ContainerOven(id, inventory, itemHandler, data, this);
    }

    @Override
    public void tick(Level level, BlockPos pos, BlockState state) {
        tickProcessing(level, pos, state);
    }
}
