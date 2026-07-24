package net.langball.coffee.block.tileentity;

import net.langball.coffee.CoffeeWork;
import net.langball.coffee.init.ModBlockEntities;
import net.langball.coffee.init.ModBlocks;
import net.langball.coffee.recipes.blocks.GrinderRecipes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class TileEntityGrinder extends BlockEntity implements MenuProvider {
    public static final int SLOT_INPUT = 0;
    public static final int SLOT_FUEL = 1;
    public static final int SLOT_OUTPUT = 2;
    private static final int INVENTORY_SIZE = 3;

    private final ItemStackHandler itemHandler = new ItemStackHandler(INVENTORY_SIZE) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
        }
    };
    private LazyOptional<IItemHandler> lazyHandler = LazyOptional.empty();

    public int cookTime;
    public int totalCookTime;
    public int burnTime;
    public int currentBurnTime;

    public final ContainerData data = new ContainerData() {
        @Override
        public int get(int index) {
            return switch (index) {
                case 0 -> cookTime;
                case 1 -> totalCookTime;
                case 2 -> burnTime;
                case 3 -> currentBurnTime;
                default -> 0;
            };
        }
        @Override
        public void set(int index, int value) {
            switch (index) {
                case 0 -> cookTime = value;
                case 1 -> totalCookTime = value;
                case 2 -> burnTime = value;
                case 3 -> currentBurnTime = value;
            }
        }
        @Override
        public int getCount() { return 4; }
    };

    public TileEntityGrinder(BlockPos pos, BlockState state) {
        super(ModBlockEntities.GRINDER.get(), pos, state);
    }

    public IItemHandler getItemHandler() {
        return itemHandler;
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

    @Override
    public void onLoad() {
        super.onLoad();
        lazyHandler = LazyOptional.of(() -> itemHandler);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        lazyHandler.invalidate();
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.ITEM_HANDLER) {
            return lazyHandler.cast();
        }
        return super.getCapability(cap, side);
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putInt("BurnTime", burnTime);
        tag.putInt("CookTime", cookTime);
        tag.putInt("CookTimeTotal", totalCookTime);
        tag.putInt("CurrentBurnTime", currentBurnTime);
        tag.put("Items", itemHandler.serializeNBT());
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        burnTime = tag.getInt("BurnTime");
        cookTime = tag.getInt("CookTime");
        totalCookTime = tag.getInt("CookTimeTotal");
        currentBurnTime = tag.getInt("CurrentBurnTime");
        itemHandler.deserializeNBT(tag.getCompound("Items"));
    }

    public static boolean isItemFuel(ItemStack stack) {
        return ForgeHooks.getBurnTime(stack, null) > 0;
    }

    public static void tick(Level level, BlockPos pos, BlockState state, TileEntityGrinder be) {
        boolean wasBurning = be.burnTime > 0;
        boolean dirty = false;

        if (be.burnTime > 0) {
            be.burnTime--;
        }

        if (!level.isClientSide) {
            ItemStack fuel = be.itemHandler.getStackInSlot(SLOT_FUEL);
            ItemStack input = be.itemHandler.getStackInSlot(SLOT_INPUT);
            ItemStack output = be.itemHandler.getStackInSlot(SLOT_OUTPUT);
            ItemStack result = !input.isEmpty() ? GrinderRecipes.instance().getSmeltingResult(input) : ItemStack.EMPTY;

            boolean canSmelt = !result.isEmpty()
                    && (output.isEmpty() || (ItemStack.isSameItem(output, result) && output.getCount() + result.getCount() <= output.getMaxStackSize()));

            if (be.burnTime == 0 && canSmelt && isItemFuel(fuel)) {
                be.currentBurnTime = be.burnTime = ForgeHooks.getBurnTime(fuel, null);
                if (be.burnTime > 0) {
                    fuel.shrink(1);
                    if (fuel.isEmpty()) {
                        be.itemHandler.setStackInSlot(SLOT_FUEL, fuel.getCraftingRemainingItem());
                    }
                    dirty = true;
                }
            }

            if (be.burnTime > 0 && canSmelt) {
                be.cookTime++;
                if (be.cookTime >= be.totalCookTime) {
                    be.cookTime = 0;
                    be.totalCookTime = 0;
                    if (output.isEmpty()) {
                        be.itemHandler.setStackInSlot(SLOT_OUTPUT, result.copy());
                    } else {
                        output.grow(result.getCount());
                    }
                    input.shrink(1);
                    dirty = true;
                }
            } else {
                be.cookTime = 0;
            }

            if (wasBurning != (be.burnTime > 0)) {
                dirty = true;
                level.setBlock(pos, state.setValue(net.minecraft.world.level.block.HorizontalDirectionalBlock.FACING,
                        state.getValue(net.minecraft.world.level.block.HorizontalDirectionalBlock.FACING)), 3);
            }
        }

        if (dirty) {
            setChanged(level, pos, state);
        }
    }
}
