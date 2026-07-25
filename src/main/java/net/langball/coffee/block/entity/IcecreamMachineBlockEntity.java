package net.langball.coffee.block.entity;

import net.langball.coffee.CoffeeWork;
import net.langball.coffee.block.MachineBlock;
import net.langball.coffee.init.ModBlockEntities;
import net.langball.coffee.init.ModRecipeTypes;
import net.langball.coffee.recipes.MachineRecipe;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Containers;
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

import java.util.Map;
import java.util.Map.Entry;

public class IcecreamMachineBlockEntity extends MachineBlockEntity {
    public static final int SLOT_INPUT = 0;
    public static final int SLOT_FUEL = 1;
    public static final int SLOT_OUTPUT = 2;
    private static final int INVENTORY_SIZE = 3;

    /* Custom fuel registry for ice/lava-based cooling */
    public static final java.util.Map<ItemStack, Integer> ICE_FUEL_REGISTRY =
            new java.util.HashMap<>();

    @Nullable
    private MachineRecipe cachedRecipe;

    public IcecreamMachineBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.ICECREAM_MACHINE.get(), pos, state);
        this.itemHandler = new ItemStackHandler(INVENTORY_SIZE) {
            @Override
            protected void onContentsChanged(int slot) {
                setChanged();
                if (slot == SLOT_INPUT) cachedRecipe = null;
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
        return Component.translatable("container." + CoffeeWork.MODID + ".icecream_machine");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
        return new net.langball.coffee.gui.ContainerIcecreamMachine(id, inventory, itemHandler, data, this);
    }

    // ─── Fuel helpers ──────────────────────────────────────────────────────

    public static int getItemBurnTime(ItemStack stack) {
        if (stack.isEmpty()) return 0;
        for (Entry<ItemStack, Integer> entry : ICE_FUEL_REGISTRY.entrySet()) {
            if (areItemStacksEqual(stack, entry.getKey())) {
                return entry.getValue();
            }
        }
        return 0;
    }

    public static boolean isItemFuel(ItemStack stack) {
        return getItemBurnTime(stack) > 0;
    }

    private static boolean areItemStacksEqual(ItemStack a, ItemStack b) {
        return a.getItem() == b.getItem()
                && (b.getDamageValue() == 32767 || b.getDamageValue() == a.getDamageValue());
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
                .getRecipeFor(ModRecipeTypes.ICECREAM_MAKING, new SimpleContainer(input), getLevel())
                .orElse(null);
        return cachedRecipe;
    }

    // ─── Tick body ─────────────────────────────────────────────────────────

    @Override
    public void tick(Level level, BlockPos pos, BlockState state) {
        boolean wasBurning = burnTime > 0;
        boolean dirty = false;

        if (burnTime > 0) {
            burnTime--;
        }

        if (!level.isClientSide) {
            ItemStack fuel = itemHandler.getStackInSlot(SLOT_FUEL);
            ItemStack output = itemHandler.getStackInSlot(SLOT_OUTPUT);
            MachineRecipe recipe = getCurrentRecipe();

            boolean canSmelt = recipe != null
                    && (output.isEmpty()
                    || (ItemStack.isSameItemSameTags(output, recipe.result())
                    && output.getCount() + recipe.result().getCount() <= output.getMaxStackSize()));

            if (burnTime == 0 && canSmelt && !fuel.isEmpty()) {
                int fuelBurnTime = getItemBurnTime(fuel);
                if (fuelBurnTime > 0) {
                    burnTime = fuelBurnTime;
                    burnTimeTotal = fuelBurnTime;
                    totalCookTime = recipe.cookingTime();
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
                    cookTime = 0;
                    totalCookTime = recipe.cookingTime();
                    if (output.isEmpty()) {
                        itemHandler.setStackInSlot(SLOT_OUTPUT, recipe.result().copy());
                    } else {
                        int newCount = Math.min(output.getCount() + recipe.result().getCount(),
                                output.getMaxStackSize());
                        output.setCount(newCount);
                    }
                    inputShrink(SLOT_INPUT);
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

    private void inputShrink(int slot) {
        ItemStack stack = itemHandler.getStackInSlot(slot);
        ItemStack container = stack.getCraftingRemainingItem();
        if (!container.isEmpty()) {
            itemHandler.setStackInSlot(slot, container);
        } else {
            stack.shrink(1);
        }
    }
}
