package net.langball.coffee.block.entity;

import net.langball.coffee.advancement.PhonographPlayTrigger;
import net.langball.coffee.init.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.RecordItem;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import org.jetbrains.annotations.Nullable;

public class PhonographBlockEntity extends BlockEntity {
    private ItemStack record = ItemStack.EMPTY;
    /** Absolute game tick at which the current record was inserted.
     *  Chunk reloads use this to decide whether the song is still
     *  in progress.  Vanilla Minecraft has no sub-song offset API,
     *  so the song restarts from the top on chunk reload. */
    private long playbackStartTick = -1L;
    private long tickCount;
    /** Phase 9 Fix7: a legacy save (no PlaybackStartTick) cannot
     *  safely read {@link Level#getGameTime()} during {@link #load}
     *  because the standard chunk-load sequence creates the BE,
     *  invokes load(), then attaches it to the Level.  We capture
     *  the migration intent here and apply it at {@link #onLoad}
     *  when the Level is available.  Without this flag, the actual
     *  chunk-load path (load() with level == null) keeps
     *  playbackStartTick at -1 and onLoad() bails out, leaving the
     *  song silent forever. */
    private boolean restartLegacyPlayback;

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
        this.restartLegacyPlayback = false;
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

    /** Phase 9 Fix7: resume playback when the chunk loads.  Vanilla
     *  Minecraft music has no sub-song offset API, so chunk reloads
     *  restart the song from the beginning.  This matches the vanilla
     *  Jukebox behaviour and prevents saves (which lacked
     *  {@code PlaybackStartTick}) from going forever silent because the
     *  naive {@code level.getGameTime() - 0L} would exceed the song's
     *  tick budget in any reasonably-played world.
     *
     *  When the legacy migration flag is set we anchor the start
     *  time to the current game time so the song effectively
     *  restarts on the next chunk load. */
    @Override
    public void onLoad() {
        super.onLoad();
        if (level == null || level.isClientSide) return;
        if (!hasRecord()) return;
        if (restartLegacyPlayback) {
            playbackStartTick = level.getGameTime();
            restartLegacyPlayback = false;
            setChanged();
        }
        if (playbackStartTick < 0) return;
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
        // Phase 9 Fix8 P2-4: clear the record when the tag is
        // absent, so a server sync that bumps the block but doesn't
        // carry a Record entry leaves the client BE consistent with
        // the server.  Without this, ejecting a record would leave
        // the previous record cached on the client.  Also reset
        // restartLegacyPlayback so a re-insert does not re-trigger
        // the legacy migration.
        if (tag.contains("Record")) {
            record = ItemStack.of(tag.getCompound("Record"));
        } else {
            record = ItemStack.EMPTY;
            restartLegacyPlayback = false;
        }
        // Phase 9 Fix7: defer the legacy NBT migration to onLoad().
        // During a real chunk load load() is invoked BEFORE the BE is
        // attached to the Level, so level == null here.  Reading
        // level.getGameTime() would throw, and storing 0L would make
        // the song "already finished" on the next onLoad() because
        // elapsed = gameTime - 0 quickly exceeds songTicks.
        if (tag.contains("PlaybackStartTick")) {
            playbackStartTick = tag.getLong("PlaybackStartTick");
            restartLegacyPlayback = false;
        } else if (!record.isEmpty()) {
            playbackStartTick = -1L;
            restartLegacyPlayback = true;
        } else {
            playbackStartTick = -1L;
            restartLegacyPlayback = false;
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

    // Phase 9 Fix8 P2-3: test-only accessors that expose the
    // private NBT-migration state so GameTest can verify the
    // load() -> onLoad() -> save() round-trip without depending
    // on chunk scheduling.  Phase 9 Fix9 P3-1: package-private
    // so they do not leak into the public production API.
    // Phase 9 Fix10 P3-1: the unused runOnLoadForTesting() and
    // getRecordForTesting() hooks were removed once the
    // phonographLoadTagWithoutRecordClearsState test confirmed
    // the load() path through hasRecord() and
    // getPlaybackStartTickForTesting() alone.
    boolean isRestartLegacyPlaybackFlagSet() { return restartLegacyPlayback; }
    long getPlaybackStartTickForTesting() { return playbackStartTick; }

    CompoundTag snapshotForTesting() {
        CompoundTag tag = new CompoundTag();
        saveAdditional(tag);
        return tag;
    }
}
