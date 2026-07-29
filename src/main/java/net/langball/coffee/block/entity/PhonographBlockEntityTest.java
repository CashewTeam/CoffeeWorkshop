package net.langball.coffee.block.entity;

import net.langball.coffee.CoffeeWork;
import net.langball.coffee.init.ModBlocks;
import net.langball.coffee.init.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.gametest.GameTestHolder;
import net.minecraftforge.gametest.PrefixGameTestTemplate;

/**
 * Phase 9 Fix9 P2-1: the PhonographBlockEntity test API is
 * package-private (see {@link PhonographBlockEntity} accessors
 * flagged with {@code *ForTesting}).  This test class lives in
 * the same package so the package-private methods are reachable.
 * It also uses the dedicated 3x3x3 Bar Counter template so the
 * block placements stay inside the structure bounds (Phase 9
 * Fix9 P2-2).
 *
 * The legacy NBT migration test (Fix7 P1-3 / Fix8 P2-3) drives
 * the load -> onLoad -> save path with the BE first constructed
 * without a Level so the assertion that {@code level.getGameTime()}
 * is consulted after attachment is genuinely exercised.
 */
@GameTestHolder(CoffeeWork.MODID)
@PrefixGameTestTemplate(false)
public class PhonographBlockEntityTest {

    @GameTest(template = "bar_counter_3x3x3", timeoutTicks = 80)
    public static void phonographLegacyNbtMigrationSurvivesRoundTrip(GameTestHelper helper) {
        BlockPos primary = new BlockPos(1, 1, 1);
        helper.setBlock(primary, ModBlocks.PHONOGRAPH.get());
        BlockEntity be0 = helper.getBlockEntity(primary);
        helper.assertTrue(be0 instanceof PhonographBlockEntity, "BE must be PhonographBlockEntity");
        PhonographBlockEntity ph0 = (PhonographBlockEntity) be0;

        // Build a legacy NBT: record present, PlaybackStartTick absent.
        CompoundTag legacyTag = new CompoundTag();
        ItemStack record = new ItemStack(ModItems.RECORD_KUSA_NOSHI_TO_NE.get());
        CompoundTag recordTag = new CompoundTag();
        record.save(recordTag);
        legacyTag.put("Record", recordTag);
        long beforeLoad = helper.getLevel().getGameTime();

        // Construct a FRESH BE with no Level attached.  The chunk
        // load sequence calls load() before the BE is attached to
        // the Level, so we must reproduce that exact lifecycle
        // here.
        PhonographBlockEntity fresh = new PhonographBlockEntity(
                BlockPos.ZERO, ModBlocks.PHONOGRAPH.get().defaultBlockState());
        helper.assertTrue(fresh.getLevel() == null,
                "Freshly-constructed BE must not have a Level yet");
        fresh.load(legacyTag);
        // The legacy migration flag must be set after load().
        helper.assertTrue(fresh.isRestartLegacyPlaybackFlagSet(),
                "Legacy NBT (no PlaybackStartTick) must set the migration flag");

        // Now attach the Level and call onLoad() to anchor the
        // playback start time to the current game time.
        long afterLoad = helper.getLevel().getGameTime();
        fresh.setLevel(helper.getLevel());
        fresh.onLoad();
        helper.assertTrue(!fresh.isRestartLegacyPlaybackFlagSet(),
                "After onLoad() the migration flag must be cleared");

        long startTick = fresh.getPlaybackStartTickForTesting();
        helper.assertTrue(startTick >= beforeLoad && startTick <= afterLoad,
                "PlaybackStartTick must be inside [beforeLoad, afterLoad], got " + startTick);

        // The save round-trip must carry the populated tick so the
        // next load does not re-trigger the legacy migration.
        CompoundTag savedTag = fresh.snapshotForTesting();
        helper.assertTrue(savedTag.contains("PlaybackStartTick"),
                "Save after migration must carry PlaybackStartTick");

        // Loading a tag that already has PlaybackStartTick must
        // NOT set the migration flag.
        BlockPos ph2Pos = new BlockPos(2, 1, 0);
        helper.setBlock(ph2Pos, ModBlocks.PHONOGRAPH.get());
        PhonographBlockEntity savedBe = (PhonographBlockEntity) helper.getBlockEntity(ph2Pos);
        savedBe.load(savedTag);
        helper.assertTrue(!savedBe.isRestartLegacyPlaybackFlagSet(),
                "Loaded BE with PlaybackStartTick must not re-enter the legacy migration");
        helper.succeed();
    }

    /**
     * Phase 9 Fix10 P2-2: a server sync that bumps the block but
     * does not carry a {@code Record} entry must clear the cached
     * record on the receiver and reset the legacy migration flag.
     * Regression-locks the {@code load(tag)} path so a future bug
     * that keeps the previous record cached on the client cannot
     * slip back in.
     */
    @GameTest(template = "bar_counter_3x3x3", timeoutTicks = 80)
    public static void phonographLoadTagWithoutRecordClearsState(GameTestHelper helper) {
        BlockPos primary = new BlockPos(1, 1, 1);
        helper.setBlock(primary, ModBlocks.PHONOGRAPH.get());
        PhonographBlockEntity ph = (PhonographBlockEntity) helper.getBlockEntity(primary);

        // First load: tag WITH Record.  The BE must hold the
        // record and an explicit PlaybackStartTick.
        CompoundTag withRecord = new CompoundTag();
        ItemStack record = new ItemStack(ModItems.RECORD_KUSA_NOSHI_TO_NE.get());
        CompoundTag recordTag = new CompoundTag();
        record.save(recordTag);
        withRecord.put("Record", recordTag);
        withRecord.putLong("PlaybackStartTick", helper.getLevel().getGameTime());
        ph.load(withRecord);
        helper.assertTrue(ph.hasRecord(),
                "After load(tag with Record) hasRecord() must be true");
        helper.assertTrue(ph.getPlaybackStartTickForTesting() >= 0,
                "After load(tag with Record) playbackStartTick must be populated, got "
                        + ph.getPlaybackStartTickForTesting());

        // Second load: tag WITHOUT Record.  An empty sync must
        // clear the record and the legacy migration flag, and
        // reset playbackStartTick to -1.  This is the path the
        // server's update-tag dispatch takes when ejecting a
        // record.
        CompoundTag emptyTag = new CompoundTag();
        emptyTag.putLong("PlaybackStartTick", -1L);
        ph.load(emptyTag);
        helper.assertTrue(!ph.hasRecord(),
                "After load(tag without Record) hasRecord() must be false");
        helper.assertTrue(ph.getPlaybackStartTickForTesting() == -1L,
                "After load(tag without Record) playbackStartTick must be -1, got "
                        + ph.getPlaybackStartTickForTesting());
        helper.assertTrue(!ph.isRestartLegacyPlaybackFlagSet(),
                "After load(tag without Record) legacy migration flag must be cleared");
        helper.succeed();
    }
}
