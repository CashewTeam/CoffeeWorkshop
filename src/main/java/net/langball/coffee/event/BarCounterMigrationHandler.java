package net.langball.coffee.event;

import net.langball.coffee.block.BarCounterBlock;
import net.langball.coffee.CoffeeWork;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.chunk.PalettedContainer;
import net.minecraftforge.event.level.ChunkEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Phase 9 Fix9 P1-1: real chunk-load migration for old
 * {@code shape=inner} / {@code shape=normal} Bar Counter blocks.
 *
 * Fix8's {@code updateShape} override is a neighbour-shape callback
 * that only fires when a neighbouring block changes — it does NOT
 * run on a fresh chunk load.  Pre-Fix5 worlds saved as
 * {@code shape=inner} cannot distinguish left from right corners
 * (both sides were written as {@code INNER}), and the model +
 * collision tables also flipped the model 90° incorrectly for the
 * left side.  Without an active migration, a player would have to
 * right-click the affected block before it rewrote to the
 * canonical enum.
 *
 * This handler runs once per server-side chunk load, walks every
 * section in the chunk, and rewrites any Bar Counter block whose
 * shape is {@code NORMAL} or {@code INNER} (the pre-Fix5 legacy
 * values) to the canonical enum via
 * {@link BarCounterBlock#determineShape} so the canonical form
 * is in place by the time any client receives the chunk.
 *
 * The migration runs on the server only, which is the side that
 * owns authoritative state; clients receive the rewritten state
 * via the normal chunk-send protocol.
 */
@Mod.EventBusSubscriber(modid = CoffeeWork.MODID)
public final class BarCounterMigrationHandler {
    private static final Logger LOG = LoggerFactory.getLogger(BarCounterMigrationHandler.class);

    private BarCounterMigrationHandler() {}

    @SubscribeEvent
    public static void onChunkLoad(ChunkEvent.Load event) {
        if (!(event.getLevel() instanceof net.minecraft.server.level.ServerLevel level)) {
            return;
        }
        if (!(event.getChunk() instanceof LevelChunk chunk)) {
            return;
        }
        int migrated = migrateChunk(level, chunk);
        if (migrated > 0) {
            chunk.setUnsaved(true);
            LOG.info("BarCounterMigrationHandler: rewrote {} legacy shape value(s) in chunk {}",
                    migrated, chunk.getPos());
        }
    }

    /**
     * Walk every section of {@code chunk} and rewrite legacy
     * {@code NORMAL} / {@code INNER} BarCounter shapes to their
     * canonical enum.  Returns the number of rewrites performed.
     * The caller is responsible for marking the chunk unsaved if
     * any rewrites happen.
     *
     * Phase 9 Fix9 P1-1: exposed so GameTests can drive the
     * migration deterministically without having to unload and
     * reload an entire chunk (which the GameTest harness does not
     * support).  The {@link #onChunkLoad} subscriber calls into
     * the same method on every fresh chunk load.
     */
    public static int migrateChunk(net.minecraft.server.level.ServerLevel level, LevelChunk chunk) {
        int migrated = 0;
        int minBuildY = level.getMinBuildHeight();
        LevelChunkSection[] sections = chunk.getSections();
        for (int sectionIdx = 0; sectionIdx < sections.length; sectionIdx++) {
            LevelChunkSection section = sections[sectionIdx];
            if (section == null) continue;
            if (section.hasOnlyAir()) continue;
            int sectionY = chunk.getSectionYFromSectionIndex(sectionIdx);
            int sectionBaseY = minBuildY + SectionPos.sectionToBlockCoord(sectionY);
            PalettedContainer<BlockState> states = section.getStates();
            for (int ly = 0; ly < 16; ly++) {
                for (int lz = 0; lz < 16; lz++) {
                    for (int lx = 0; lx < 16; lx++) {
                        BlockState state = states.get(lx, ly, lz);
                        if (!(state.getBlock() instanceof BarCounterBlock block)) continue;
                        BarCounterBlock.Shape shape = state.getValue(BarCounterBlock.SHAPE);
                        if (shape != BarCounterBlock.Shape.NORMAL
                                && shape != BarCounterBlock.Shape.INNER) {
                            continue;
                        }
                        BlockPos pos = new BlockPos(
                                chunk.getPos().getMinBlockX() + lx,
                                sectionBaseY + ly,
                                chunk.getPos().getMinBlockZ() + lz);
                        BarCounterBlock.Shape canonical = BarCounterBlock.determineShape(
                                level, pos, state.getValue(BarCounterBlock.FACING), block);
                        if (canonical == shape) continue;
                        section.setBlockState(lx, ly, lz,
                                state.setValue(BarCounterBlock.SHAPE, canonical));
                        migrated++;
                    }
                }
            }
        }
        return migrated;
    }
}
