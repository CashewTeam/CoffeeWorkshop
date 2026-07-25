package net.langball.coffee.block.entity;

import net.langball.coffee.CoffeeWork;
import net.langball.coffee.init.ModBlockEntities;
import net.langball.coffee.init.ModRecipeTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

/**
 * Ice-cream Machine block entity — freezes liquids into ice cream.
 *
 * <p>Uses a custom cooling-fuel system (ice, packed ice, blue ice)
 * instead of furnace fuels.  The legacy {@code Map<ItemStack, Integer>}
 * has been replaced by a type-safe {@code Map<Item, Integer>}.
 */
public class IcecreamMachineBlockEntity extends AbstractFueledProcessingBlockEntity {

    public static final int SLOT_INPUT = 0;
    public static final int SLOT_FUEL = 1;
    public static final int SLOT_OUTPUT = 2;
    private static final int INVENTORY_SIZE = 3;

    /**
     * Cooling-fuel registry: maps Item → burn time in ticks.
     *
     * <p>Populated by {@link net.langball.coffee.CoffeeWork#registerIceFuels()}
     * during mod initialisation.  Default entries:
     * Snowball=100, Ice=200, Packed Ice=800, Blue Ice=3600.
     */
    public static final Map<Item, Integer> COOLING_FUEL = new java.util.HashMap<>();

    public IcecreamMachineBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.ICECREAM_MACHINE.get(), pos, state);
        this.itemHandler = new ItemStackHandler(INVENTORY_SIZE) {
            @Override
            protected void onContentsChanged(int slot) {
                setChanged();
                if (slot == SLOT_INPUT) activeRecipeId = null;
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
        return ModRecipeTypes.ICECREAM_MAKING;
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
        return COOLING_FUEL.getOrDefault(stack.getItem(), 0);
    }

    @Override
    protected boolean isValidFuel(ItemStack stack) {
        return COOLING_FUEL.containsKey(stack.getItem());
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("container." + CoffeeWork.MODID + ".icecream_machine");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
        return new net.langball.coffee.gui.ContainerIcecreamMachine(id, inventory, itemHandler, data, this);
    }

    @Override
    public void tick(Level level, BlockPos pos, BlockState state) {
        tickFueledProcessing(level, pos, state);
    }
}
