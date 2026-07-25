package net.langball.coffee.block.entity;

import net.langball.coffee.CoffeeWork;
import net.langball.coffee.block.MachineBlock;
import net.langball.coffee.init.ModBlockEntities;
import net.langball.coffee.init.ModRecipeTypes;
import net.langball.coffee.recipes.MachineRecipe;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
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
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class GrinderBlockEntity extends MachineBlockEntity {
    public static final int SLOT_INPUT = 0;
    public static final int SLOT_FUEL = 1;
    public static final int SLOT_OUTPUT = 2;
    private static final int INVENTORY_SIZE = 3;

    /** Cached recipe — re-queried when the input item changes. */
    @Nullable
    private MachineRecipe cachedRecipe;

    public GrinderBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.GRINDER.get(), pos, state);
        this.itemHandler = new ItemStackHandler(INVENTORY_SIZE) {
            @Override
            protected void onContentsChanged(int slot) {
                setChanged();
                // invalidate cached recipe when input changes
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
        return Component.translatable("container." + CoffeeWork.MODID + ".grinder");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
        return new net.langball.coffee.gui.ContainerGrinder(id, inventory, itemHandler, data, this);
    }

    // ─── Fuel helpers ──────────────────────────────────────────────────────

    public static boolean isItemFuel(ItemStack stack) {
        return ForgeHooks.getBurnTime(stack, null) > 0;
    }

    public static int getItemBurnTime(ItemStack stack) {
        return ForgeHooks.getBurnTime(stack, null);
    }

    // ─── Recipe lookup ─────────────────────────────────────────────────────

    /**
     * Returns the current {@link MachineRecipe} for the input slot, caching it
     * until the input changes (lookup is invalidated by
     * {@code onContentsChanged(SLOT_INPUT)}).
     */
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
        // Re-query the recipe manager
        cachedRecipe = getLevel().getRecipeManager()
                .getRecipeFor(ModRecipeTypes.GRINDING, new SimpleContainer(input), getLevel())
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

        ItemStack fuel = itemHandler.getStackInSlot(SLOT_FUEL);
        ItemStack output = itemHandler.getStackInSlot(SLOT_OUTPUT);
        MachineRecipe recipe = getCurrentRecipe();

        boolean canSmelt = recipe != null
                && (output.isEmpty()
                || (ItemStack.isSameItemSameTags(output, recipe.result())
                && output.getCount() + recipe.result().getCount() <= output.getMaxStackSize()));

        if (burnTime == 0 && canSmelt && !fuel.isEmpty()) {
            int fuelBurnTime = ForgeHooks.getBurnTime(fuel, null);
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
}
