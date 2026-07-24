package net.langball.coffee.block.entity;

import net.langball.coffee.CoffeeWork;
import net.langball.coffee.block.BlockGrinder;
import net.langball.coffee.init.ModBlockEntities;
import net.langball.coffee.init.ModBlocks;
import net.langball.coffee.recipes.blocks.GrinderRecipes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class GrinderBlockEntity extends BlockEntity implements MenuProvider {
    public static final int SLOT_INPUT = 0;
    public static final int SLOT_FUEL = 1;
    public static final int SLOT_OUTPUT = 2;
    private static final int INVENTORY_SIZE = 3;

    private final ItemStackHandler itemHandler = new ItemStackHandler(INVENTORY_SIZE) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
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
    private LazyOptional<IItemHandler> lazyHandler = LazyOptional.empty();

    private int cookTime;
    private int totalCookTime;
    private int burnTime;
    private int burnTimeTotal;

    public final ContainerData data = new ContainerData() {
        @Override
        public int get(int index) {
            return switch (index) {
                case 0 -> cookTime;
                case 1 -> totalCookTime;
                case 2 -> burnTime;
                case 3 -> burnTimeTotal;
                default -> 0;
            };
        }

        @Override
        public void set(int index, int value) {
            switch (index) {
                case 0 -> cookTime = value;
                case 1 -> totalCookTime = value;
                case 2 -> burnTime = value;
                case 3 -> burnTimeTotal = value;
            }
        }

        @Override
        public int getCount() {
            return 4;
        }
    };

    public GrinderBlockEntity(BlockPos pos, BlockState state) {
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
        tag.putInt("BurnTimeTotal", burnTimeTotal);
        tag.put("Items", itemHandler.serializeNBT());
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        burnTime = tag.getInt("BurnTime");
        cookTime = tag.getInt("CookTime");
        totalCookTime = tag.getInt("CookTimeTotal");
        burnTimeTotal = tag.getInt("BurnTimeTotal");
        itemHandler.deserializeNBT(tag.getCompound("Items"));
    }

    @Override
    public CompoundTag getUpdateTag() {
        CompoundTag tag = super.getUpdateTag();
        saveAdditional(tag);
        return tag;
    }

    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    public static boolean isItemFuel(ItemStack stack) {
        return ForgeHooks.getBurnTime(stack, null) > 0;
    }

    public static int getItemBurnTime(ItemStack stack) {
        return ForgeHooks.getBurnTime(stack, null);
    }

    public static int getCookTime(ItemStack stack) {
        return 200;
    }

    public static void tick(Level level, BlockPos pos, BlockState state, GrinderBlockEntity be) {
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
                    && (output.isEmpty()
                    || (ItemStack.isSameItemSameTags(output, result)
                    && output.getCount() + result.getCount() <= output.getMaxStackSize()));

            if (be.burnTime == 0 && canSmelt && !fuel.isEmpty()) {
                int fuelBurnTime = ForgeHooks.getBurnTime(fuel, null);
                if (fuelBurnTime > 0) {
                    be.burnTime = fuelBurnTime;
                    be.burnTimeTotal = fuelBurnTime;
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
                    be.totalCookTime = getCookTime(input);
                    be.cookTime = 0;
                    if (output.isEmpty()) {
                        be.itemHandler.setStackInSlot(SLOT_OUTPUT, result.copy());
                    } else {
                        output.grow(result.getCount());
                    }
                    input.shrink(1);
                    dirty = true;
                }
            } else {
                if (be.cookTime > 0) {
                    be.cookTime = Math.max(be.cookTime - 2, 0);
                }
            }

            if (wasBurning != (be.burnTime > 0)) {
                dirty = true;
                switchGrinderBlock(level, pos, state, be.burnTime > 0);
            }
        }

        if (dirty) {
            setChanged(level, pos, state);
        }
    }

    private static void switchGrinderBlock(Level level, BlockPos pos, BlockState state, boolean isBurning) {
        Direction facing = state.getValue(HorizontalDirectionalBlock.FACING);
        Block targetBlock = isBurning ? ModBlocks.GRINDER_ON.get() : ModBlocks.GRINDER.get();
        BlockState newState = targetBlock.defaultBlockState()
                .setValue(HorizontalDirectionalBlock.FACING, facing);
        level.setBlock(pos, newState, 3);
    }
}
