package net.langball.coffee.block.entity;

import net.langball.coffee.init.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.RecordItem;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class PhonographBlockEntity extends BlockEntity {
    private ItemStack record = ItemStack.EMPTY;

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
        setChanged();
        sync();
        if (level != null) {
            level.setBlock(worldPosition, getBlockState()
                    .setValue(net.langball.coffee.block.PhonographBlock.HAS_RECORD, true), 3);
        }
        return stack;
    }

    public ItemStack ejectRecord() {
        if (!hasRecord()) return ItemStack.EMPTY;
        ItemStack ejected = record.copy();
        record = ItemStack.EMPTY;
        setChanged();
        sync();
        if (level != null) {
            level.setBlock(worldPosition, getBlockState()
                    .setValue(net.langball.coffee.block.PhonographBlock.HAS_RECORD, false), 3);
        }
        return ejected;
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
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        if (tag.contains("Record")) {
            record = ItemStack.of(tag.getCompound("Record"));
        }
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
}
