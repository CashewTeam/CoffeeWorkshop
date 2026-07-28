package net.langball.coffee.gametest;

import net.langball.coffee.CoffeeWork;
import net.langball.coffee.block.BarCounterBlock;
import net.langball.coffee.block.BlockCoffeeMachine;
import net.langball.coffee.block.CoffeePotBlock;
import net.langball.coffee.block.MokaPotBlock;
import net.langball.coffee.block.entity.CoffeeMachineBlockEntity;
import net.langball.coffee.block.entity.CoffeePotBlockEntity;
import net.langball.coffee.block.entity.MokaPotBlockEntity;
import net.langball.coffee.block.entity.PhonographBlockEntity;
import net.langball.coffee.block.entity.SodaMachineBlockEntity;
import net.langball.coffee.block.entity.TurkishCoffeePotBlockEntity;
import net.langball.coffee.init.ModBlocks;
import net.langball.coffee.init.ModItems;
import net.langball.coffee.item.DrinkCoffee;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.gametest.GameTestHolder;
import net.minecraftforge.gametest.PrefixGameTestTemplate;

@GameTestHolder(CoffeeWork.MODID)
@PrefixGameTestTemplate(false)
public class Phase9GameTests {

    private static final BlockPos POT_POS = BlockPos.ZERO.above();

    @GameTest(template = "empty", timeoutTicks = 40)
    public static void potNbtSerializationDoesNotRecurse(GameTestHelper helper) {
        helper.setBlock(POT_POS, ModBlocks.MOKA_POT.get());
        BlockEntity be = helper.getBlockEntity(POT_POS);
        helper.assertTrue(be instanceof MokaPotBlockEntity, "BE must be MokaPotBlockEntity");
        MokaPotBlockEntity moka = (MokaPotBlockEntity) be;

        moka.addCoffee();
        moka.addWater();

        CompoundTag tag = moka.getUpdateTag();
        CompoundTag saved = moka.saveForItem();

        helper.assertTrue(!tag.isEmpty(), "getUpdateTag should produce non-empty tag");
        helper.assertTrue(!saved.isEmpty(), "saveForItem should produce non-empty tag");

        MokaPotBlockEntity fresh = new MokaPotBlockEntity(POT_POS,
                ModBlocks.MOKA_POT.get().defaultBlockState()
                        .setValue(MokaPotBlock.FACING, net.minecraft.core.Direction.NORTH));
        fresh.load(tag);
        helper.assertTrue(fresh.hasCoffeeInput(), "Loaded Moka should have coffee");
        helper.assertTrue(fresh.hasWaterInput(), "Loaded Moka should have water");
        helper.succeed();
    }

    @GameTest(template = "empty", timeoutTicks = 40)
    public static void mokaDoesNotBrewWithoutHeat(GameTestHelper helper) {
        helper.setBlock(POT_POS, ModBlocks.MOKA_POT.get());
        helper.setBlock(POT_POS.below(), Blocks.STONE);

        BlockEntity be = helper.getBlockEntity(POT_POS);
        helper.assertTrue(be instanceof MokaPotBlockEntity, "BE must be MokaPotBlockEntity");
        MokaPotBlockEntity moka = (MokaPotBlockEntity) be;
        moka.addCoffee();
        moka.addWater();

        helper.assertTrue(!moka.isReady(), "Moka should not be ready without brewing");
        helper.assertTrue(moka.getBrewProgress() == 0, "BrewProgress should be 0 without heat");
        helper.succeed();
    }

    @GameTest(template = "empty", timeoutTicks = 40)
    public static void mokaCompletesBrewingWhenReady(GameTestHelper helper) {
        helper.setBlock(POT_POS, ModBlocks.MOKA_POT.get());

        BlockEntity be = helper.getBlockEntity(POT_POS);
        helper.assertTrue(be instanceof MokaPotBlockEntity, "BE must be MokaPotBlockEntity");
        MokaPotBlockEntity moka = (MokaPotBlockEntity) be;

        ItemStack drink = new ItemStack(ModItems.ESPRESSO.get());
        if (drink.getItem() instanceof DrinkCoffee dc) {
            dc.initializeFreshStack(drink);
        }
        DrinkCoffee.setRemainingCups(drink, 1);
        moka.setStoredDrink(drink);
        moka.setServings(4);

        helper.assertTrue(moka.isReady(), "Moka should be ready with stored drink");
        helper.assertTrue(moka.getServings() == 4, "Moka should have 4 servings, got " + moka.getServings());

        ItemStack poured = moka.pourServing();
        helper.assertTrue(!poured.isEmpty(), "pourServing should return a drink");
        helper.assertTrue(poured.getItem() instanceof DrinkCoffee, "Poured item should be DrinkCoffee");
        helper.assertTrue(DrinkCoffee.getRemainingCups(poured) <= 1,
                "Poured drink should have <=1 remaining cups, got " + DrinkCoffee.getRemainingCups(poured));
        helper.assertTrue(moka.getServings() == 3,
                "After one pour, servings should be 3, got " + moka.getServings());

        while (moka.getServings() > 0) moka.pourServing();
        helper.assertTrue(moka.getBrewProgress() == 0,
                "brewProgress should reset to 0 after last serving, got " + moka.getBrewProgress());
        helper.succeed();
    }

    @GameTest(template = "empty", timeoutTicks = 40)
    public static void coffeePotTransferConservesServings(GameTestHelper helper) {
        helper.setBlock(POT_POS, ModBlocks.COFFEE_POT.get());
        BlockEntity be = helper.getBlockEntity(POT_POS);
        helper.assertTrue(be instanceof CoffeePotBlockEntity, "BE must be CoffeePotBlockEntity");
        CoffeePotBlockEntity pot = (CoffeePotBlockEntity) be;

        ItemStack espresso = new ItemStack(ModItems.ESPRESSO.get());
        DrinkCoffee.setRemainingCups(espresso, 4);
        int moved = pot.fillFrom(espresso, 2);

        helper.assertTrue(moved == 2,
                "fillFrom should transfer 2, got " + moved);
        helper.assertTrue(pot.getServings() == 2,
                "Coffee Pot should have 2 servings, got " + pot.getServings());

        ItemStack stored = pot.getStoredDrink();
        helper.assertTrue(ItemStack.isSameItem(stored, espresso),
                "Stored drink should be same item as input");
        helper.assertTrue(DrinkCoffee.getRemainingCups(stored) == 1,
                "Stored template remaining_cups should be 1, got " + DrinkCoffee.getRemainingCups(stored));

        ItemStack poured = pot.pourServing();
        helper.assertTrue(!poured.isEmpty(), "pourServing should return drink");
        helper.assertTrue(pot.getServings() == 1,
                "After pour should have 1 serving, got " + pot.getServings());

        moved = pot.fillFrom(espresso, 4);
        helper.assertTrue(moved == 3,
                "Second fillFrom should transfer 3 (capacity 4, had 1), got " + moved);
        helper.assertTrue(pot.getServings() == 4,
                "Should be full (4 servings), got " + pot.getServings());
        helper.succeed();
    }

    @GameTest(template = "empty", timeoutTicks = 40)
    public static void coffeePotPickupRestoresLevel(GameTestHelper helper) {
        helper.setBlock(POT_POS, ModBlocks.COFFEE_POT.get());
        BlockEntity be = helper.getBlockEntity(POT_POS);
        helper.assertTrue(be instanceof CoffeePotBlockEntity, "BE must be CoffeePotBlockEntity");
        CoffeePotBlockEntity pot = (CoffeePotBlockEntity) be;

        ItemStack espresso = new ItemStack(ModItems.ESPRESSO.get());
        DrinkCoffee.setRemainingCups(espresso, 1);
        pot.fillFrom(espresso, 3);

        CompoundTag saved = pot.saveForItem();

        helper.setBlock(POT_POS, Blocks.AIR);
        helper.setBlock(POT_POS, ModBlocks.COFFEE_POT.get());

        BlockEntity be2 = helper.getBlockEntity(POT_POS);
        helper.assertTrue(be2 instanceof CoffeePotBlockEntity, "Second BE must be CoffeePotBlockEntity");
        CoffeePotBlockEntity pot2 = (CoffeePotBlockEntity) be2;
        pot2.load(saved);
        pot2.onLoadSyncLevel();

        BlockState state = helper.getBlockState(POT_POS);
        int level = state.getValue(CoffeePotBlock.LEVEL);
        helper.assertTrue(level == 3,
                "Coffee Pot LEVEL should be 3 after restoring NBT, got " + level);
        helper.succeed();
    }

    @GameTest(template = "empty", timeoutTicks = 40)
    public static void barCounterStraightAlone(GameTestHelper helper) {
        helper.setBlock(POT_POS, ModBlocks.STONE_BAR_COUNTER.get().defaultBlockState()
                .setValue(BarCounterBlock.FACING, net.minecraft.core.Direction.NORTH));

        BlockState s = helper.getBlockState(POT_POS);
        helper.assertTrue(s.getValue(BarCounterBlock.SHAPE) == BarCounterBlock.Shape.STRAIGHT,
                "Bar counter alone should be STRAIGHT, not inner");

        helper.succeed();
    }

    @GameTest(template = "empty", timeoutTicks = 40)
    public static void sodaMachineAcceptsBottleOnly(GameTestHelper helper) {
        helper.setBlock(POT_POS, ModBlocks.SODA_MACHINE.get());
        BlockEntity be = helper.getBlockEntity(POT_POS);
        helper.assertTrue(be instanceof SodaMachineBlockEntity, "BE must be SodaMachineBlockEntity");
        SodaMachineBlockEntity sm = (SodaMachineBlockEntity) be;

        var handler = sm.getItemHandler();
        helper.assertTrue(handler.isItemValid(0, new ItemStack(net.minecraft.world.item.Items.GLASS_BOTTLE)),
                "Slot 0 should accept glass bottle");
        helper.assertTrue(!handler.isItemValid(0, new ItemStack(net.minecraft.world.item.Items.APPLE)),
                "Slot 0 should reject non-bottle items");
        helper.assertTrue(!handler.isItemValid(SodaMachineBlockEntity.SLOT_OUTPUT,
                new ItemStack(net.minecraft.world.item.Items.GLASS_BOTTLE)),
                "Output slot should reject items");

        helper.succeed();
    }

    @GameTest(template = "empty", timeoutTicks = 40)
    public static void turkishPotServingNormalization(GameTestHelper helper) {
        helper.setBlock(POT_POS, ModBlocks.TURKISH_COFFEE_POT.get());
        BlockEntity be = helper.getBlockEntity(POT_POS);
        helper.assertTrue(be instanceof TurkishCoffeePotBlockEntity, "BE must be TurkishCoffeePotBlockEntity");
        TurkishCoffeePotBlockEntity pot = (TurkishCoffeePotBlockEntity) be;

        ItemStack drink = new ItemStack(ModItems.COFFEE_TURKISH.get());
        if (drink.getItem() instanceof DrinkCoffee dc) dc.initializeFreshStack(drink);
        DrinkCoffee.setRemainingCups(drink, 1);
        pot.setStoredDrink(drink);
        pot.setServings(4);

        helper.assertTrue(pot.isReady(), "Turkish pot should be ready");
        ItemStack poured = pot.pourServing();
        helper.assertTrue(poured.getItem() instanceof DrinkCoffee, "Poured should be DrinkCoffee");
        helper.assertTrue(DrinkCoffee.getRemainingCups(poured) <= 1,
                "Poured drink should be single serving");
        helper.assertTrue(pot.getServings() == 3, "Should have 3 left, got " + pot.getServings());

        while (pot.getServings() > 0) pot.pourServing();
        helper.assertTrue(pot.getServings() == 0, "Pot should be empty after all pours");
        helper.assertTrue(pot.getBrewProgress() == 0, "brewProgress should reset");
        helper.succeed();
    }

    @GameTest(template = "empty", timeoutTicks = 40)
    public static void phonographInsertEjectPreservesRecord(GameTestHelper helper) {
        helper.setBlock(POT_POS, ModBlocks.PHONOGRAPH.get());
        BlockEntity be = helper.getBlockEntity(POT_POS);
        helper.assertTrue(be instanceof PhonographBlockEntity, "BE must be PhonographBlockEntity");
        PhonographBlockEntity ph = (PhonographBlockEntity) be;

        ItemStack record = new ItemStack(ModItems.RECORD_KUSA_NOSHI_TO_NE.get());
        ItemStack remaining = ph.insertRecord(record.copy());
        helper.assertTrue(remaining.getCount() < record.getCount(), "Record should be inserted");
        helper.assertTrue(ph.hasRecord(), "Phonograph should have record");

        ItemStack ejected = ph.ejectRecord();
        helper.assertTrue(!ejected.isEmpty(), "Ejected record should not be empty");
        helper.assertTrue(ejected.getItem() == ModItems.RECORD_KUSA_NOSHI_TO_NE.get(),
                "Ejected should be same record type");
        helper.assertTrue(!ph.hasRecord(), "Phonograph should be empty after eject");

        helper.succeed();
    }

    @GameTest(template = "empty", timeoutTicks = 40)
    public static void phonographRejectsNonRecord(GameTestHelper helper) {
        helper.setBlock(POT_POS, ModBlocks.PHONOGRAPH.get());
        BlockEntity be = helper.getBlockEntity(POT_POS);
        helper.assertTrue(be instanceof PhonographBlockEntity, "BE must be PhonographBlockEntity");
        PhonographBlockEntity ph = (PhonographBlockEntity) be;

        ItemStack apple = new ItemStack(net.minecraft.world.item.Items.APPLE);
        ItemStack returned = ph.insertRecord(apple.copy());
        helper.assertTrue(returned.getCount() == apple.getCount(),
                "Non-record item should be fully returned");
        helper.assertTrue(!ph.hasRecord(), "Phonograph should not accept non-records");

        helper.succeed();
    }

    // ─────────────────────────────────────────────────────────────────────
    // Phase 9 Fix6 P1-1 regression: Phonograph play advancement is
    // configured to actually fire when a record is inserted.  The
    // advancement JSON uses
    // {@code minecraft:player_interacted_with_block} + the
    // {@code music_discs} tag, which is the canonical vanilla trigger
    // for jukebox-style play.  We verify the criterion is loaded — the
    // actual player progress cannot be asserted in a GameTest because
    // the mock ServerPlayer supplied by {@code makeMockServerPlayerInLevel}
    // has no live network connection and the advancement grant
    // bookkeeping throws a NPE on packet dispatch.
    // ─────────────────────────────────────────────────────────────────────

    @GameTest(template = "empty", timeoutTicks = 80)
    public static void phonographAdvancementTriggerLoads(GameTestHelper helper) {
        helper.setBlock(POT_POS, ModBlocks.PHONOGRAPH.get());
        var player = helper.makeMockPlayer();
        player.setItemInHand(net.minecraft.world.InteractionHand.MAIN_HAND,
                new ItemStack(ModItems.RECORD_KUSA_NOSHI_TO_NE.get()));
        // useBlock must return CONSUME / sidedSuccess without error —
        // the trigger listener is invoked at success time even if the
        // subsequent progress dispatch fails on the mock player.
        helper.useBlock(POT_POS, player);

        BlockEntity be = helper.getBlockEntity(POT_POS);
        helper.assertTrue(be instanceof PhonographBlockEntity, "BE must be PhonographBlockEntity");
        helper.assertTrue(((PhonographBlockEntity) be).hasRecord(),
                "Phonograph should have record after useBlock");

        var advMgr = helper.getLevel().getServer().getAdvancements();
        var advId = new net.minecraft.resources.ResourceLocation(
                CoffeeWork.MODID, "phonograph_play");
        var advancement = advMgr.getAdvancement(advId);
        helper.assertTrue(advancement != null, "phonograph_play advancement must be loaded");
        var criteria = advancement.getCriteria();
        helper.assertTrue(criteria.containsKey("play_record"),
                "phonograph_play must define the 'play_record' criterion, got " + criteria.keySet());
        var criterion = criteria.get("play_record");
        // The criterion's trigger instance must be an inner
        // TriggerInstance of ItemUsedOnLocationTrigger (the only
        // fully-typed trigger that carries a 'location' block
        // predicate).  We verify the enclosing class because the
        // instance is the inner TriggerInstance, not the outer
        // ItemUsedOnLocationTrigger class.
        var triggerInstance = criterion.getTrigger();
        helper.assertTrue(triggerInstance != null,
                "play_record criterion must have a non-null trigger instance");
        var triggerClass = triggerInstance.getClass();
        var outerClass = triggerClass.getEnclosingClass();
        helper.assertTrue(outerClass == net.minecraft.advancements.critereon.ItemUsedOnLocationTrigger.class,
                "play_record triggerInstance's enclosing class must be ItemUsedOnLocationTrigger, got "
                        + (outerClass != null ? outerClass.getSimpleName() : "null"));
        helper.succeed();
    }

    // ─────────────────────────────────────────────────────────────────────
    // Phase 9 Fix6 P1-3 / P2-2: Bar Counter inner-corner shape + collision
    // ─────────────────────────────────────────────────────────────────────

    /**
     * Verifies that for each horizontal FACING, placing a matching
     * Bar Counter on the player's right side resolves to INNER_RIGHT
     * and on the player's left side resolves to INNER_LEFT.
     *
     * The "right" side of a player is determined by the Java side:
     * {@code Direction.getClockWise()} of the FACING.  Concretely:
     *   NORTH → EAST,  EAST → SOUTH,  SOUTH → WEST,  WEST → NORTH.
     * The neighbour block must be placed at that offset and must face
     * the opposite direction so the matching-neighbour check passes.
     */
    @GameTest(template = "empty", timeoutTicks = 40)
    public static void barCounterInnerCornerAllFacings(GameTestHelper helper) {
        // Each facing occupies a 2x1 footprint inside the 5x5 template.
        // (right centres are at X={0,4}, left centres at X={2,2} —
        // keeping everything within 0..4 bounds.)
        BlockPos rightPrimary = new BlockPos(0, 1, 0);
        BlockPos leftPrimary = new BlockPos(4, 1, 0);

        java.util.List<net.minecraft.core.Direction> rightCases = java.util.List.of(
                net.minecraft.core.Direction.NORTH,
                net.minecraft.core.Direction.EAST,
                net.minecraft.core.Direction.SOUTH,
                net.minecraft.core.Direction.WEST);
        java.util.List<net.minecraft.core.Direction> leftCases = java.util.List.of(
                net.minecraft.core.Direction.NORTH,
                net.minecraft.core.Direction.EAST,
                net.minecraft.core.Direction.SOUTH,
                net.minecraft.core.Direction.WEST);

        for (int i = 0; i < rightCases.size(); i++) {
            net.minecraft.core.Direction facing = rightCases.get(i);
            net.minecraft.core.Direction rightOffset = facing.getClockWise();
            // Place the right primary at (0, 1, i*2) so the neighbours
            // do not overlap on the Z axis.
            BlockPos p = new BlockPos(0, 1, i * 2);
            BlockPos n = p.relative(rightOffset);
            helper.setBlock(p, ModBlocks.WOODEN_BAR_COUNTER.get().defaultBlockState()
                    .setValue(BarCounterBlock.FACING, facing));
            helper.setBlock(n, ModBlocks.WOODEN_BAR_COUNTER.get().defaultBlockState()
                    .setValue(BarCounterBlock.FACING, facing.getOpposite()));

            net.minecraft.core.Direction leftFacing = leftCases.get(i);
            net.minecraft.core.Direction leftOffset = facing.getCounterClockWise();
            BlockPos lp = new BlockPos(4, 1, i * 2);
            BlockPos ln = lp.relative(leftOffset);
            helper.setBlock(lp, ModBlocks.WOODEN_BAR_COUNTER.get().defaultBlockState()
                    .setValue(BarCounterBlock.FACING, leftFacing));
            helper.setBlock(ln, ModBlocks.WOODEN_BAR_COUNTER.get().defaultBlockState()
                    .setValue(BarCounterBlock.FACING, leftFacing.getOpposite()));
        }

        helper.runAfterDelay(1, () -> {
            for (int i = 0; i < rightCases.size(); i++) {
                net.minecraft.core.Direction facing = rightCases.get(i);
                BlockPos p = new BlockPos(0, 1, i * 2);
                BlockPos lp = new BlockPos(4, 1, i * 2);

                BarCounterBlock.Shape rShape = helper.getBlockState(p)
                        .getValue(BarCounterBlock.SHAPE);
                BarCounterBlock.Shape lShape = helper.getBlockState(lp)
                        .getValue(BarCounterBlock.SHAPE);

                helper.assertTrue(rShape == BarCounterBlock.Shape.INNER_RIGHT,
                        "Facing " + facing + " right-neighbour: expected INNER_RIGHT, got " + rShape);
                helper.assertTrue(lShape == BarCounterBlock.Shape.INNER_LEFT,
                        "Facing " + leftCases.get(i) + " left-neighbour: expected INNER_LEFT, got " + lShape);
            }
            helper.succeed();
        });
    }

    /**
     * Phase 9 Fix6 P1-3: the collision shape must be the L-shaped union
     * of the body quadrant and the full-footprint countertop, not a
     * single full-height block.  We verify by checking that the empty
     * back-quadrant of the body (the L-shape's "notch") does not
     * collide, while the inner quadrant and the countertop do.
     */
    @GameTest(template = "empty", timeoutTicks = 40)
    public static void barCounterCollisionShapeIsLShape(GameTestHelper helper) {
        BlockPos primary = new BlockPos(2, 1, 0);
        helper.setBlock(primary, ModBlocks.WOODEN_BAR_COUNTER.get().defaultBlockState()
                .setValue(BarCounterBlock.FACING, net.minecraft.core.Direction.NORTH)
                .setValue(BarCounterBlock.SHAPE, BarCounterBlock.Shape.INNER_RIGHT));

        VoxelShape shape = helper.getBlockState(primary).getShape(
                helper.getLevel(), primary);
        var aabbs = shape.toAabbs();

        // Helper: does any AABB in the shape intersect the target?
        java.util.function.Predicate<net.minecraft.world.phys.AABB> anyIntersect = target ->
                aabbs.stream().anyMatch(b -> b.intersects(target));

        // The empty body quadrant is the (-x, -z) corner at body
        // height: x=0..0.25, y=0..0.875, z=0..0.25.  This must NOT
        // collide.
        var emptyBody = new net.minecraft.world.phys.AABB(
                0.0, 0.0, 0.0, 0.25, 0.87, 0.25);
        helper.assertTrue(!anyIntersect.test(emptyBody),
                "INNER_RIGHT shape must not collide in the empty body quadrant; aabbs=" + aabbs);

        // The inner quadrant (which the body covers) at body height
        // must collide.
        var innerBody = new net.minecraft.world.phys.AABB(
                0.3, 0.0, 0.3, 0.7, 0.5, 0.7);
        helper.assertTrue(anyIntersect.test(innerBody),
                "INNER_RIGHT shape must collide in the inner quadrant; aabbs=" + aabbs);

        // The countertop at the top centre must collide (full 1x1).
        var countertop = new net.minecraft.world.phys.AABB(
                0.25, 0.9, 0.25, 0.75, 0.99, 0.75);
        helper.assertTrue(anyIntersect.test(countertop),
                "INNER_RIGHT countertop must be solid; aabbs=" + aabbs);

        // And the countertop must also cover the empty body quadrant
        // at the top.
        var emptyQuadTop = new net.minecraft.world.phys.AABB(
                0.0, 0.9, 0.0, 0.2, 0.99, 0.2);
        helper.assertTrue(anyIntersect.test(emptyQuadTop),
                "INNER_RIGHT countertop must cover the empty body quadrant at the top; aabbs=" + aabbs);

        helper.succeed();
    }

    /**
     * Phase 9 Fix6 P1-4: loading a BlockState whose Shape enum is the
     * legacy NORMAL or INNER must normalise to the modern constant
     * (STRAIGHT or INNER_RIGHT) when the block's collision is
     * queried.  Uses the BarCounterBlock.Shape.normalise() path that
     * is exercised by getShape().
     */
    @GameTest(template = "empty", timeoutTicks = 40)
    public static void barCounterLegacyAliasesNormaliseOnLoad(GameTestHelper helper) {
        BlockPos legacyNormal = new BlockPos(1, 1, 0);
        BlockPos legacyInner = new BlockPos(3, 1, 0);

        helper.setBlock(legacyNormal, ModBlocks.WOODEN_BAR_COUNTER.get().defaultBlockState()
                .setValue(BarCounterBlock.FACING, net.minecraft.core.Direction.NORTH)
                .setValue(BarCounterBlock.SHAPE, BarCounterBlock.Shape.NORMAL));
        helper.setBlock(legacyInner, ModBlocks.WOODEN_BAR_COUNTER.get().defaultBlockState()
                .setValue(BarCounterBlock.FACING, net.minecraft.core.Direction.NORTH)
                .setValue(BarCounterBlock.SHAPE, BarCounterBlock.Shape.INNER));

        // The raw enum value is preserved in the BlockState.
        helper.assertTrue(helper.getBlockState(legacyNormal).getValue(BarCounterBlock.SHAPE)
                        == BarCounterBlock.Shape.NORMAL,
                "Raw enum must be NORMAL when set explicitly");
        helper.assertTrue(helper.getBlockState(legacyInner).getValue(BarCounterBlock.SHAPE)
                        == BarCounterBlock.Shape.INNER,
                "Raw enum must be INNER when set explicitly");

        // The NORMAL alias should resolve to STRAIGHT (full 1x1
        // block).  The empty body quadrant at body height must
        // collide because the shape is a full block.
        var normalAabbs = helper.getBlockState(legacyNormal).getShape(
                helper.getLevel(), legacyNormal).toAabbs();
        var emptyBody = new net.minecraft.world.phys.AABB(
                0.0, 0.0, 0.0, 0.25, 0.87, 0.25);
        helper.assertTrue(normalAabbs.stream().anyMatch(b -> b.intersects(emptyBody)),
                "NORMAL alias should resolve to STRAIGHT (full block, body-height collision everywhere), got no intersection");

        // The INNER alias should resolve to INNER_RIGHT (L-shape).
        // The empty body quadrant at body height must NOT collide.
        var innerAabbs = helper.getBlockState(legacyInner).getShape(
                helper.getLevel(), legacyInner).toAabbs();
        helper.assertTrue(innerAabbs.stream().noneMatch(b -> b.intersects(emptyBody)),
                "INNER alias should resolve to INNER_RIGHT (empty body quadrant at body height doesn't collide), got intersection=true");

        // The inner quadrant at body height must collide.
        var innerBody = new net.minecraft.world.phys.AABB(
                0.3, 0.0, 0.3, 0.7, 0.5, 0.7);
        helper.assertTrue(innerAabbs.stream().anyMatch(b -> b.intersects(innerBody)),
                "INNER alias should resolve to INNER_RIGHT (inner quadrant at body height collides), got no intersection");

        helper.succeed();
    }

    // ─────────────────────────────────────────────────────────────────────
    // Phase 9 Fix4 P0-2: Pot Items must be stacksTo(1)
    // ─────────────────────────────────────────────────────────────────────

    @GameTest(template = "empty", timeoutTicks = 40)
    public static void coffeePotItemIsSingleStackOnly(GameTestHelper helper) {
        ItemStack pot = new ItemStack(ModItems.COFFEE_POT_ITEM.get());
        helper.assertTrue(pot.getMaxStackSize() == 1,
                "Coffee Pot Item must stack to 1, got " + pot.getMaxStackSize());

        ItemStack moka = new ItemStack(ModItems.MOKA_POT_ITEM.get());
        helper.assertTrue(moka.getMaxStackSize() == 1,
                "Moka Pot Item must stack to 1, got " + moka.getMaxStackSize());

        ItemStack turkish = new ItemStack(ModItems.TURKISH_COFFEE_POT_ITEM.get());
        helper.assertTrue(turkish.getMaxStackSize() == 1,
                "Turkish Pot Item must stack to 1, got " + turkish.getMaxStackSize());
        helper.succeed();
    }

    // ─────────────────────────────────────────────────────────────────────
    // Phase 9 Fix6 P1-6: split Coffee Machine / Moka Pot fill tests
    // ─────────────────────────────────────────────────────────────────────

    /**
     * Build a Coffee Pot ItemStack with N servings of the given drink
     * pre-loaded.  Used by the direct-fill tests below.
     */
    private static ItemStack buildPreFilledCoffeePot(net.minecraft.world.item.Item drink, int servings) {
        ItemStack held = new ItemStack(ModItems.COFFEE_POT_ITEM.get());
        if (servings <= 0) return held;
        ItemStack template = new ItemStack(drink);
        if (template.getItem() instanceof DrinkCoffee dc) dc.initializeFreshStack(template);
        CoffeePotBlockEntity potBE = new CoffeePotBlockEntity(BlockPos.ZERO,
                ModBlocks.COFFEE_POT.get().defaultBlockState());
        potBE.fillFrom(template, servings);
        held.getOrCreateTag().put("BlockEntityTag", potBE.saveForItem());
        return held;
    }

    /**
     * P1-6: Moka Pot → Coffee Pot direct-fill MUST increment the held
     * pot's stored servings by exactly 1.  Reads the held pot's
     * BlockEntityTag after the use and verifies via a fresh
     * CoffeePotBlockEntity that the servings went from N to N+1.
     *
     * The pot is pre-filled with N=3 servings so only 1 space remains
     * — the Moka Pot transfer code moves `min(space, moka_servings)`,
     * so an empty pot would otherwise drain the Moka completely.
     */
    @GameTest(template = "empty", timeoutTicks = 60)
    public static void mokaPotFillsHeldCoffeePotIncrementsServings(GameTestHelper helper) {
        // Place a Moka pot pre-loaded with a 4-cup Espresso.
        helper.setBlock(POT_POS, ModBlocks.MOKA_POT.get());
        BlockEntity be = helper.getBlockEntity(POT_POS);
        helper.assertTrue(be instanceof MokaPotBlockEntity, "BE must be MokaPotBlockEntity");
        MokaPotBlockEntity moka = (MokaPotBlockEntity) be;
        ItemStack espresso = new ItemStack(ModItems.ESPRESSO.get());
        if (espresso.getItem() instanceof DrinkCoffee dc) dc.initializeFreshStack(espresso);
        DrinkCoffee.setRemainingCups(espresso, 4);
        moka.setStoredDrink(espresso);
        moka.setServings(4);

        // Build a player holding a Coffee Pot with 3 servings of the
        // same drink — only 1 space remains, so the Moka loses 1.
        ItemStack heldPot = buildPreFilledCoffeePot(ModItems.ESPRESSO.get(), 3);
        int beforeServings = 3;
        net.minecraft.world.entity.player.Player player = helper.makeMockPlayer();
        player.setItemInHand(net.minecraft.world.InteractionHand.MAIN_HAND, heldPot);
        helper.useBlock(POT_POS, player);

        // Moka dropped exactly 1 serving
        helper.assertTrue(moka.getServings() == 3,
                "Moka should have 3 servings after one transfer, got " + moka.getServings());

        // Held pot's BlockEntityTag now contains 4 servings (3 + 1).
        helper.assertTrue(heldPot.getTag() != null && heldPot.getTag().contains("BlockEntityTag"),
                "Held pot must retain its BlockEntityTag, tag=" + heldPot.getTag());
        CoffeePotBlockEntity probe = new CoffeePotBlockEntity(POT_POS,
                ModBlocks.COFFEE_POT.get().defaultBlockState());
        probe.load(heldPot.getTag().getCompound("BlockEntityTag"));
        helper.assertTrue(probe.getServings() == beforeServings + 1,
                "Held pot servings must increase from " + beforeServings + " to "
                        + (beforeServings + 1) + ", got " + probe.getServings());

        // Stacked pot: same right-click must NOT mutate the held stack.
        ItemStack stackedPot = new ItemStack(ModItems.COFFEE_POT_ITEM.get());
        stackedPot.setCount(4); // creative /give abuse
        player.setItemInHand(net.minecraft.world.InteractionHand.MAIN_HAND, stackedPot);
        helper.useBlock(POT_POS, player);
        helper.assertTrue(stackedPot.getTag() == null
                        || !stackedPot.getTag().contains("BlockEntityTag"),
                "Stacked pot must NOT acquire a BlockEntityTag; got tag=" + stackedPot.getTag());

        helper.succeed();
    }

    /**
     * P1-6: Coffee Machine → Coffee Pot direct-fill with
     * {@code ENABLE_MULTI_CUP=false} must move exactly ONE serving,
     * regardless of the multi-cup count stored on the output.  The
     * machine output is fully drained into the pot's 1 remaining slot.
     */
    @GameTest(template = "empty", timeoutTicks = 80)
    public static void coffeeMachinePotFillMultiCupDisabled(GameTestHelper helper) {
        helper.setBlock(POT_POS, ModBlocks.COFFEE_MACHINE.get());
        BlockEntity be = helper.getBlockEntity(POT_POS);
        helper.assertTrue(be instanceof CoffeeMachineBlockEntity, "BE must be CoffeeMachineBlockEntity");
        CoffeeMachineBlockEntity cm = (CoffeeMachineBlockEntity) be;
        var handler = cm.getItemHandler();

        ItemStack americano = new ItemStack(ModItems.COFFEE_AMERICANO.get());
        if (americano.getItem() instanceof DrinkCoffee dc) dc.initializeFreshStack(americano);
        DrinkCoffee.setRemainingCups(americano, 4);
        ((net.minecraftforge.items.ItemStackHandler) handler).setStackInSlot(
                CoffeeMachineBlockEntity.SLOT_OUTPUT, americano);

        boolean wasMulti = net.langball.coffee.ModConfig.ENABLE_MULTI_CUP.get();
        net.langball.coffee.ModConfig.ENABLE_MULTI_CUP.set(false);

        // Pre-fill the held pot with 3 servings so only 1 space remains.
        // With multi-cup OFF the machine moves exactly 1 serving and the
        // machine output is fully extracted (empty cup returned separately).
        ItemStack heldPot = buildPreFilledCoffeePot(ModItems.COFFEE_AMERICANO.get(), 3);
        net.minecraft.world.entity.player.Player player = helper.makeMockPlayer();
        player.setItemInHand(net.minecraft.world.InteractionHand.MAIN_HAND, heldPot);
        try {
            helper.useBlock(POT_POS, player);

            // Held pot has 4 servings (3 + 1).
            CoffeePotBlockEntity probe = new CoffeePotBlockEntity(POT_POS,
                    ModBlocks.COFFEE_POT.get().defaultBlockState());
            probe.load(heldPot.getTag().getCompound("BlockEntityTag"));
            helper.assertTrue(probe.getServings() == 4,
                    "Multi-cup OFF: held pot should have 4 servings (3+1), got " + probe.getServings());

            // Machine output is empty (fully drained by the move).
            ItemStack afterOutput = handler.getStackInSlot(CoffeeMachineBlockEntity.SLOT_OUTPUT);
            helper.assertTrue(afterOutput.isEmpty(),
                    "Multi-cup OFF: machine output must be empty after moving 1 of 4 cups, got " + afterOutput);
        } finally {
            net.langball.coffee.ModConfig.ENABLE_MULTI_CUP.set(wasMulti);
        }
        helper.succeed();
    }

    /**
     * P1-6: Coffee Machine → Coffee Pot with multi-cup ON and the held
     * pot already holding 3 servings.  The capacity-4 pot has 1 free
     * slot, so the move is 1 serving (capacity-limited) and the machine
     * output retains 3 cups (not silent overwrite).
     */
    @GameTest(template = "empty", timeoutTicks = 80)
    public static void coffeeMachinePotFillMultiCupEnabledCapacityLimited(GameTestHelper helper) {
        helper.setBlock(POT_POS, ModBlocks.COFFEE_MACHINE.get());
        BlockEntity be = helper.getBlockEntity(POT_POS);
        helper.assertTrue(be instanceof CoffeeMachineBlockEntity, "BE must be CoffeeMachineBlockEntity");
        CoffeeMachineBlockEntity cm = (CoffeeMachineBlockEntity) be;
        var handler = cm.getItemHandler();

        ItemStack americano = new ItemStack(ModItems.COFFEE_AMERICANO.get());
        if (americano.getItem() instanceof DrinkCoffee dc) dc.initializeFreshStack(americano);
        DrinkCoffee.setRemainingCups(americano, 4);
        ((net.minecraftforge.items.ItemStackHandler) handler).setStackInSlot(
                CoffeeMachineBlockEntity.SLOT_OUTPUT, americano);

        boolean wasMulti = net.langball.coffee.ModConfig.ENABLE_MULTI_CUP.get();
        net.langball.coffee.ModConfig.ENABLE_MULTI_CUP.set(true);

        // Pre-fill the held pot with 3 servings so only 1 space remains.
        ItemStack heldPot = buildPreFilledCoffeePot(ModItems.COFFEE_AMERICANO.get(), 3);
        net.minecraft.world.entity.player.Player player = helper.makeMockPlayer();
        player.setItemInHand(net.minecraft.world.InteractionHand.MAIN_HAND, heldPot);
        try {
            helper.useBlock(POT_POS, player);

            ItemStack afterOutput = handler.getStackInSlot(CoffeeMachineBlockEntity.SLOT_OUTPUT);
            helper.assertTrue(DrinkCoffee.getRemainingCups(afterOutput) == 3,
                    "Multi-cup ON: machine output should retain 3 cups after moving 1, got "
                            + DrinkCoffee.getRemainingCups(afterOutput));

            // Held pot has 4 servings after the fill.
            CoffeePotBlockEntity probe = new CoffeePotBlockEntity(POT_POS,
                    ModBlocks.COFFEE_POT.get().defaultBlockState());
            probe.load(heldPot.getTag().getCompound("BlockEntityTag"));
            helper.assertTrue(probe.getServings() == 4,
                    "Multi-cup ON: held pot should have 4 servings (3+1), got " + probe.getServings());
        } finally {
            net.langball.coffee.ModConfig.ENABLE_MULTI_CUP.set(wasMulti);
        }

        // Stacked-pot refusal.
        ItemStack stackedPot = new ItemStack(ModItems.COFFEE_POT_ITEM.get());
        stackedPot.setCount(4);
        player.setItemInHand(net.minecraft.world.InteractionHand.MAIN_HAND, stackedPot);
        helper.useBlock(POT_POS, player);
        helper.assertTrue(stackedPot.getTag() == null
                        || !stackedPot.getTag().contains("BlockEntityTag"),
                "Stacked pot must NOT acquire a BlockEntityTag; got tag=" + stackedPot.getTag());

        helper.succeed();
    }

    /**
     * P1-6: Coffee Machine → Coffee Pot must reject drinks that are
     * not present in the {@code coffee_pot_drinks} tag.  The Machine
     * output must remain unchanged and the held pot must not gain a
     * BlockEntityTag.
     */
    @GameTest(template = "empty", timeoutTicks = 80)
    public static void coffeeMachineRejectsNonWhitelistDrink(GameTestHelper helper) {
        helper.setBlock(POT_POS, ModBlocks.COFFEE_MACHINE.get());
        BlockEntity be = helper.getBlockEntity(POT_POS);
        helper.assertTrue(be instanceof CoffeeMachineBlockEntity, "BE must be CoffeeMachineBlockEntity");
        CoffeeMachineBlockEntity cm = (CoffeeMachineBlockEntity) be;
        var handler = cm.getItemHandler();

        // Green Tea is NOT in coffee_pot_drinks.
        ItemStack greenTea = new ItemStack(ModItems.COFFEE_GREEN_TEA.get());
        if (greenTea.getItem() instanceof DrinkCoffee dc) dc.initializeFreshStack(greenTea);
        DrinkCoffee.setRemainingCups(greenTea, 4);
        ((net.minecraftforge.items.ItemStackHandler) handler).setStackInSlot(
                CoffeeMachineBlockEntity.SLOT_OUTPUT, greenTea);

        ItemStack heldPot = buildPreFilledCoffeePot(ModItems.COFFEE_GREEN_TEA.get(), 0);
        net.minecraft.world.entity.player.Player player = helper.makeMockPlayer();
        player.setItemInHand(net.minecraft.world.InteractionHand.MAIN_HAND, heldPot);
        helper.useBlock(POT_POS, player);

        // Held pot must remain empty (no BlockEntityTag added).
        helper.assertTrue(heldPot.getTag() == null
                        || !heldPot.getTag().contains("BlockEntityTag"),
                "Non-whitelist drink must NOT populate held pot's BlockEntityTag, got "
                        + heldPot.getTag());

        // Machine output untouched.
        ItemStack afterOutput = handler.getStackInSlot(CoffeeMachineBlockEntity.SLOT_OUTPUT);
        helper.assertTrue(DrinkCoffee.getRemainingCups(afterOutput) == 4,
                "Non-whitelist drink: machine output should retain 4 cups, got "
                        + DrinkCoffee.getRemainingCups(afterOutput));
        helper.succeed();
    }

    /**
     * P1-6: When the machine output is fully drained into the held
     * pot, the empty cup must be returned to the player.  Drain
     * condition: machine output has 4 cups, held pot has 0 servings
     * → moving 4 cups fills the pot's capacity exactly.
     */
    @GameTest(template = "empty", timeoutTicks = 80)
    public static void coffeeMachineReturnsEmptyCupWhenOutputExhausted(GameTestHelper helper) {
        helper.setBlock(POT_POS, ModBlocks.COFFEE_MACHINE.get());
        BlockEntity be = helper.getBlockEntity(POT_POS);
        helper.assertTrue(be instanceof CoffeeMachineBlockEntity, "BE must be CoffeeMachineBlockEntity");
        CoffeeMachineBlockEntity cm = (CoffeeMachineBlockEntity) be;
        var handler = cm.getItemHandler();

        // ESPRESSO returns Item.GLASS_BOTTLE?  Looking at the registry,
        // ESPRESSO returns an empty cup (cup_returned).  Use COFFEE_AMERICANO
        // for which the empty cup is well-defined.
        ItemStack americano = new ItemStack(ModItems.COFFEE_AMERICANO.get());
        if (americano.getItem() instanceof DrinkCoffee dc) dc.initializeFreshStack(americano);
        DrinkCoffee.setRemainingCups(americano, 4);
        ((net.minecraftforge.items.ItemStackHandler) handler).setStackInSlot(
                CoffeeMachineBlockEntity.SLOT_OUTPUT, americano);

        boolean wasMulti = net.langball.coffee.ModConfig.ENABLE_MULTI_CUP.get();
        net.langball.coffee.ModConfig.ENABLE_MULTI_CUP.set(true);

        ItemStack heldPot = buildPreFilledCoffeePot(ModItems.COFFEE_AMERICANO.get(), 0);
        net.minecraft.world.entity.player.Player player = helper.makeMockPlayer();
        player.setItemInHand(net.minecraft.world.InteractionHand.MAIN_HAND, heldPot);
        try {
            helper.useBlock(POT_POS, player);

            // Machine output is now empty (fully drained).
            ItemStack afterOutput = handler.getStackInSlot(CoffeeMachineBlockEntity.SLOT_OUTPUT);
            helper.assertTrue(afterOutput.isEmpty(),
                    "Machine output must be empty after draining, got " + afterOutput);

            // Player must have received the empty cup.
            ItemStack emptyCup = new ItemStack(((DrinkCoffee) ModItems.COFFEE_AMERICANO.get()).getEmptyCupItem());
            if (emptyCup.getItem() != null) {
                boolean hasCup = false;
                for (ItemStack inv : player.getInventory().items) {
                    if (ItemStack.isSameItem(inv, emptyCup)) {
                        hasCup = true;
                        break;
                    }
                }
                helper.assertTrue(hasCup,
                        "Player must have received the empty cup in inventory; contents="
                                + player.getInventory().items);
            }
        } finally {
            net.langball.coffee.ModConfig.ENABLE_MULTI_CUP.set(wasMulti);
        }
        helper.succeed();
    }

    // ─────────────────────────────────────────────────────────────────────
    // Phase 9 Fix4 P2-1: Coffee Pot NBT capacity clamping
    // ─────────────────────────────────────────────────────────────────────

    @GameTest(template = "empty", timeoutTicks = 40)
    public static void coffeePotLoadedNbtClampsToCapacity(GameTestHelper helper) {
        helper.setBlock(POT_POS, ModBlocks.COFFEE_POT.get());
        BlockEntity be = helper.getBlockEntity(POT_POS);
        helper.assertTrue(be instanceof CoffeePotBlockEntity, "BE must be CoffeePotBlockEntity");
        CoffeePotBlockEntity pot = (CoffeePotBlockEntity) be;

        CompoundTag illegal = new CompoundTag();
        ItemStack stored = new ItemStack(ModItems.COFFEE_AMERICANO.get());
        if (stored.getItem() instanceof DrinkCoffee dc) dc.initializeFreshStack(stored);
        CompoundTag storedTag = new CompoundTag();
        stored.save(storedTag);
        illegal.put("StoredDrink", storedTag);
        illegal.putInt("Servings", 64);
        illegal.putInt("Capacity", 64);

        pot.load(illegal);

        helper.assertTrue(pot.getCapacity() == 4,
                "Capacity must be clamped to 4 after loading illegal NBT, got " + pot.getCapacity());
        helper.assertTrue(pot.getServings() <= 4,
                "Servings must be clamped to capacity 4, got " + pot.getServings());
        helper.succeed();
    }

    // ─────────────────────────────────────────────────────────────────────
    // Phase 9 Fix4 P2-2: Soda Machine slot validation
    // ─────────────────────────────────────────────────────────────────────

    @GameTest(template = "empty", timeoutTicks = 40)
    public static void sodaMachineRejectsInvalidBaseAndFlavor(GameTestHelper helper) {
        helper.setBlock(POT_POS, ModBlocks.SODA_MACHINE.get());
        BlockEntity be = helper.getBlockEntity(POT_POS);
        helper.assertTrue(be instanceof SodaMachineBlockEntity, "BE must be SodaMachineBlockEntity");
        SodaMachineBlockEntity sm = (SodaMachineBlockEntity) be;
        var handler = sm.getItemHandler();

        helper.assertTrue(!handler.isItemValid(1, new ItemStack(net.minecraft.world.item.Items.APPLE)),
                "Soda base slot must reject an apple");
        helper.assertTrue(!handler.isItemValid(2, new ItemStack(Blocks.DIRT)),
                "Soda flavor slot must reject dirt");
        helper.assertTrue(handler.isItemValid(0, new ItemStack(net.minecraft.world.item.Items.GLASS_BOTTLE)),
                "Soda bottle slot must accept glass bottle");
        helper.succeed();
    }

}
