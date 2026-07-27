package net.langball.coffee.block.entity;

import net.langball.coffee.init.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.RecordItem;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import org.jetbrains.annotations.Nullable;

public class PhonographBlockEntity extends BlockEntity {
    private ItemStack record = ItemStack.EMPTY;
    private long tickCount;

    public PhonographBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.PHONOGRAPH.get(), pos, state);
    }

    public boolean hasRecord() { return !record.isEmpty(); }
    public ItemStack getRecord() { return record; }

    public ItemStack insertRecord(ItemStack stack) {
        if (hasRecord()) return stack;
        if (!(stack.getItem() instanceof RecordItem)) return stack;
        ItemStack inserted = stack.split(1);
        this.record = inserted;
        this.tickCount = 0;
        setChanged();
        sync();
        if (level != null) {
            level.setBlock(worldPosition, getBlockState()
                    .setValue(net.langball.coffee.block.PhonographBlock.HAS_RECORD, true), 3);
            playRecord();
        }
        return stack;
    }

    public ItemStack ejectRecord() {
        if (!hasRecord()) return ItemStack.EMPTY;
        ItemStack ejected = record.copy();
        stopRecord();
        record = ItemStack.EMPTY;
        tickCount = 0;
        setChanged();
        sync();
        if (level != null) {
            level.setBlock(worldPosition, getBlockState()
                    .setValue(net.langball.coffee.block.PhonographBlock.HAS_RECORD, false), 3);
        }
        return ejected;
    }

    private void playRecord() {
        if (level == null || level.isClientSide) return;
        if (record.getItem() instanceof RecordItem) {
            level.gameEvent(GameEvent.JUKEBOX_PLAY, worldPosition, GameEvent.Context.of(getBlockState()));
            level.levelEvent(1010, worldPosition, Item.getId(record.getItem()));
        }
    }

    private void stopRecord() {
        if (level == null || level.isClientSide) return;
        level.gameEvent(GameEvent.JUKEBOX_STOP_PLAY, worldPosition, GameEvent.Context.of(getBlockState()));
        level.levelEvent(1011, worldPosition, 0);
    }

    void sync() {
        if (level != null && !level.isClientSide) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
            setChanged();
        }
    }

    public int getAnalogOutputSignal() {
        return hasRecord() ? 15 : 0;
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        if (!record.isEmpty()) {
            tag.put("Record", record.save(new CompoundTag()));
        }
        tag.putLong("TickCount", tickCount);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        if (tag.contains("Record")) {
            record = ItemStack.of(tag.getCompound("Record"));
        }
        tickCount = tag.getLong("TickCount");
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

    @Override
    public void setRemoved() {
        stopRecord();
        super.setRemoved();
    }
}
