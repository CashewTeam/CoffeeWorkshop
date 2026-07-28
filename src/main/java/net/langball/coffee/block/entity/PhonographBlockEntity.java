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
    /** Phase 9 Fix6 P1-5: when a record is inserted we record the absolute
     *  game time as the playback start.  Chunk reloads use this time to
     *  decide whether the song is still in progress.  We deliberately
     *  do NOT cache a per-record "offset" because vanilla Minecraft music
     *  is a single fire-and-forget client sound — there is no public API
     *  to resume playback at a sub-song offset.  See {@link #onLoad} for
     *  the resume semantics. */
    private long playbackStartTick = -1L;
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
        this.playbackStartTick = level != null ? level.getGameTime() : 0L;
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
        playbackStartTick = -1L;
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
        if (!hasRecord()) return 0;
        // Phase 9 Fix4 P2-4: read the real RecordItem analog signal instead
        // of hard-coding 15.  Vanilla jukebox uses this value so redstone
        // dust strengths and comparator subtraction behave identically for
        // both vanilla and coffeework records.
        return record.getItem() instanceof RecordItem rec ? rec.getAnalogOutput() : 0;
    }

    /** Phase 9 Fix6 P1-5: resume playback when the chunk loads.  Vanilla
     *  Minecraft music has no sub-song offset API, so chunk reloads
     *  restart the song from the beginning.  This matches the vanilla
     *  Jukebox behaviour and prevents saves (which lacked
     *  {@code PlaybackStartTick}) from going forever silent because the
     *  naive {@code level.getGameTime() - 0L} would exceed the song's
     *  tick budget in any reasonably-played world. */
    @Override
    public void onLoad() {
        super.onLoad();
        if (level == null || level.isClientSide) return;
        if (!hasRecord() || playbackStartTick < 0) return;
        if (record.getItem() instanceof RecordItem rec) {
            int songTicks = rec.getLengthInTicks();
            long now = level.getGameTime();
            long elapsed = now - playbackStartTick;
            if (elapsed >= songTicks) return; // already finished
            // Restart the song from the top so clients currently in
            // render distance hear it again.  This is the same audio
            // event InsertRecord uses, so listeners are not affected.
            playRecord();
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        if (!record.isEmpty()) {
            tag.put("Record", record.save(new CompoundTag()));
        }
        tag.putLong("PlaybackStartTick", playbackStartTick);
        tag.putLong("TickCount", tickCount);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        if (tag.contains("Record")) {
            record = ItemStack.of(tag.getCompound("Record"));
        }
        // Phase 9 Fix6 P1-5: legacy saves without PlaybackStartTick
        // restart the song on the next chunk load by anchoring the
        // start time to the current game time.  Doing so means
        // {@code elapsed = gameTime - playbackStartTick} stays small
        // (zero) and onLoad() will re-emit the music event rather than
        // silently bailing out as before.
        if (tag.contains("PlaybackStartTick")) {
            playbackStartTick = tag.getLong("PlaybackStartTick");
        } else if (!record.isEmpty() && level != null) {
            playbackStartTick = level.getGameTime();
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
