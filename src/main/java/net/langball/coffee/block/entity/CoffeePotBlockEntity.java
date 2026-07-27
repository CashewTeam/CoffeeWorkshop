package net.langball.coffee.block.entity;

import net.langball.coffee.brewing.ServingContainer;
import net.langball.coffee.init.ModBlockEntities;
import net.langball.coffee.init.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class CoffeePotBlockEntity extends BlockEntity implements ServingContainer {
    private static final int CAPACITY = 4;

    private ServingData serving = new ServingData(CAPACITY);

    public CoffeePotBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.COFFEE_POT.get(), pos, state);
    }

    public boolean isReady() { return !serving.isEmpty(); }

    public ItemStack pourServing() {
        if (!isReady()) return ItemStack.EMPTY;
        ItemStack result = pourOneServing();
        setChanged();
        sync();
        return result;
    }

    public void fillFrom(ItemStack drink, int servings) {
        fill(drink, servings);
        setChanged();
        sync();
    }

    public void pickUpPot(Level level, BlockPos pos) {
        ItemStack drop = new ItemStack(ModItems.COFFEE_POT_ITEM.get());
        CompoundTag tag = new CompoundTag();
        saveAdditional(tag);
        if (!tag.isEmpty()) {
            drop.getOrCreateTag().put("BlockEntityTag", tag);
        }
        net.minecraft.world.entity.item.ItemEntity item = new net.minecraft.world.entity.item.ItemEntity(
                level, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, drop);
        level.addFreshEntity(item);
    }

    public int getFillLevel() {
        if (serving.isEmpty()) return 0;
        return Math.min(serving.getServings(), 4);
    }

    @Override
    public ItemStack getStoredDrink() { return serving.getStoredDrink(); }
    @Override
    public void setStoredDrink(ItemStack drink) { serving.setStoredDrink(drink); }
    @Override
    public int getServings() { return serving.getServings(); }
    @Override
    public void setServings(int s) { serving.setServings(s); }
    @Override
    public int getCapacity() { return serving.getCapacity(); }
    @Override
    public void setCapacity(int c) { serving.setCapacity(c); }

    void sync() {
        if (level != null && !level.isClientSide) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
            setChanged();
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        saveToTag(tag);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        loadFromTag(tag);
    }

    @Override
    public CompoundTag getUpdateTag() {
        CompoundTag tag = new CompoundTag();
        saveAdditional(tag);
        return tag;
    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    private static class ServingData implements ServingContainer {
        private ItemStack storedDrink = ItemStack.EMPTY;
        private int servings;
        private final int capacity;

        ServingData(int capacity) { this.capacity = capacity; }

        @Override public ItemStack getStoredDrink() { return storedDrink; }
        @Override public void setStoredDrink(ItemStack d) { storedDrink = d; }
        @Override public int getServings() { return servings; }
        @Override public void setServings(int s) { servings = s; }
        @Override public int getCapacity() { return capacity; }
        @Override public void setCapacity(int c) {}
    }
}
