package net.langball.coffee.block.entity;

import net.langball.coffee.CoffeeWork;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Common base for all five processing-machine BlockEntities.
 *
 * The class holds:
 * <ul>
 *   <li>an {@link ItemStackHandler} (size determined per subclass)</li>
 *   <li>four tracking fields: {@link #cookTime}, {@link #totalCookTime},
 *       {@link #burnTime}, {@link #burnTimeTotal}</li>
 *   <li>a ready-made {@link ContainerData} implementation ({@link #data})
 *       that exposes those four fields to the server/client sync</li>
 *   <li>save / load / sync boilerplate</li>
 *   <li>capability lifecycle with {@link #reviveCaps()}</li>
 *   <li>furnace-style comparator output via {@link #getComparatorOutput()}</li>
 * </ul>
 *
 * Subclasses implement:
 * <ul>
 *   <li>{@link #getDisplayName()} – I18N key for the container title</li>
 *   <li>{@link #createMenu(int, Inventory, Player)} – the menu factory</li>
 *   <li>{@link #tick(Level, BlockPos, BlockState)} – the tick body
 *       (called by the static tick method registered in the companion Block)</li>
 * </ul>
 */
public abstract class MachineBlockEntity extends BlockEntity implements MenuProvider {

    /** Subclasses must initialise this in their constructor. */
    protected ItemStackHandler itemHandler;
    protected LazyOptional<IItemHandler> lazyHandler = LazyOptional.empty();

    protected int cookTime;
    protected int totalCookTime;
    protected int burnTime;
    protected int burnTimeTotal;

    public final ContainerData data = new ContainerData() {
        @Override public int get(int index) {
            return switch (index) {
                case 0 -> cookTime;
                case 1 -> totalCookTime;
                case 2 -> burnTime;
                case 3 -> burnTimeTotal;
                default -> 0;
            };
        }

        @Override public void set(int index, int value) {
            switch (index) {
                case 0 -> cookTime = value;
                case 1 -> totalCookTime = value;
                case 2 -> burnTime = value;
                case 3 -> burnTimeTotal = value;
            }
        }

        @Override public int getCount() { return 4; }
    };

    protected MachineBlockEntity(BlockEntityType<?> type, BlockPos pos,
                                  BlockState state) {
        super(type, pos, state);
    }

    /** Convenience getter for the inventory handler.
     *  Subclasses override {@code isItemValid} on the handler if they
     *  need to restrict which items go into each slot (fuel vs input). */
    public IItemHandler getItemHandler() {
        return itemHandler;
    }

    // ---- Capability lifecycle -------------------------------------------

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
    public void reviveCaps() {
        super.reviveCaps();
        lazyHandler = LazyOptional.of(() -> itemHandler);
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap,
                                                       @Nullable Direction side) {
        if (cap == ForgeCapabilities.ITEM_HANDLER) {
            return lazyHandler.cast();
        }
        return super.getCapability(cap, side);
    }

    // ---- Persistence / sync --------------------------------------------

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

    // ---- Comparator output ---------------------------------------------

    /** Furnace-style redstone signal: 0 when idle, 1–15 proportional to
     *  the fraction of {@link #cookTime} / {@link #totalCookTime}. */
    public int getComparatorOutput() {
        int total = Math.max(1, totalCookTime);
        return Math.min(15, (cookTime * 15) / total);
    }

    // ---- Tick hook -----------------------------------------------------

    /** Subclass tick body.  The companion static tick method registered
     *  on the Block should delegate to this instance method. */
    public abstract void tick(Level level, BlockPos pos, BlockState state);
}
