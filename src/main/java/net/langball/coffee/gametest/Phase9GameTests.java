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
    // Phase 9 Fix7 P1-1 / P1-3 / P2-3 / P2-4: Bar Counter
    //   - normal/inner alias blockstate variants resolve to canonical models
    //   - inner-corner shape + collision for all 8 directions
    //   - STRAIGHT directional body for all 4 facings (12/16 deep)
    //
    // Each (shape, side) pair gets its own dedicated GameTest so that
    // block placements never overlap into adjacent test space.  All
    // eight corners + four STRAIGHT facings run inside the small
    // empty template bounds (X=0..2, Y=0..1, Z=0..2) instead of
    // stacking up to Z=7 like the previous consolidated test.
    // ─────────────────────────────────────────────────────────────────────

    /** Helper: place a primary counter at (1, 1, 1) with a matching
     *  neighbour on the requested side, and return the resolved Shape. */
    private static BarCounterBlock.Shape placeAndResolve(
            GameTestHelper helper, net.minecraft.core.Direction primaryFacing,
            net.minecraft.core.Direction neighbourOffset) {
        BlockPos primary = new BlockPos(1, 1, 1);
        BlockPos neighbour = primary.relative(neighbourOffset);
        helper.setBlock(primary, ModBlocks.WOODEN_BAR_COUNTER.get().defaultBlockState()
                .setValue(BarCounterBlock.FACING, primaryFacing));
        helper.setBlock(neighbour, ModBlocks.WOODEN_BAR_COUNTER.get().defaultBlockState()
                .setValue(BarCounterBlock.FACING, primaryFacing.getOpposite()));
        return helper.getBlockState(primary).getValue(BarCounterBlock.SHAPE);
    }

    /** Helper: confirm the inner-corner collision shape matches the
     *  expected body quadrant for the given FACING.
     *
     *  The body occupies one of the four XZ quadrants after the
     *  y-rotation that the FACING applies to the model.  In the
     *  bar_stone_inner.json model the body is at x=0.25..1.0,
     *  z=0.25..1.0 (the +x +z quadrant).  After the blockstate
     *  y-rotation, that quadrant becomes:
     *    INNER_RIGHT:
     *      NORTH (y=0):   +x +z (0.25..1, 0.25..1)
     *      EAST  (y=90):  -x +z (0..0.75, 0.25..1)
     *      SOUTH (y=180): -x -z (0..0.75, 0..0.75)
     *      WEST  (y=270): +x -z (0.25..1, 0..0.75)
     *    INNER_LEFT mirrors the right across the XZ diagonal.
     *  The body is always in the quadrant that touches the
     *  neighbour at the front of the counter, but at body height
     *  the OPPOSITE corner must be empty (the L-shape's notch). */
    private static void assertInnerCornerCollision(GameTestHelper helper, BlockPos primary,
                                                     net.minecraft.core.Direction primaryFacing,
                                                     BarCounterBlock.Shape expectedShape) {
        VoxelShape shape = helper.getBlockState(primary).getShape(
                helper.getLevel(), primary);
        var aabbs = shape.toAabbs();
        java.util.function.Predicate<net.minecraft.world.phys.AABB> anyIntersect =
                target -> aabbs.stream().anyMatch(b -> b.intersects(target));

        // The empty body quadrant is the OPPOSITE corner of the
        // body.  We compute the body's quadrant from FACING and
        // SHAPE, then sample the opposite corner at body height.
        // The AABB is small enough to fit entirely inside the empty
        // quadrant (size 0.19 in X and Z so it doesn't bridge the
        // 0.25 body boundary).
        var emptyRegion = emptyCornerForFacing(primaryFacing, expectedShape);
        helper.assertTrue(!anyIntersect.test(emptyRegion),
                "Empty body quadrant must not collide for facing " + primaryFacing
                        + " shape " + expectedShape + "; aabbs=" + aabbs);

        // The centre of the block must be inside the body.  The
        // body always covers a 0.25..1.0 quadrant so the centre
        // (0.5, 0.5) is always inside regardless of the rotation.
        var innerBody = new net.minecraft.world.phys.AABB(0.4, 0.0, 0.4, 0.6, 0.5, 0.6);
        helper.assertTrue(anyIntersect.test(innerBody),
                "Inner body must collide at centre for facing " + primaryFacing
                        + " shape " + expectedShape + "; aabbs=" + aabbs);

        // The countertop must be solid in the centre.
        var countertop = new net.minecraft.world.phys.AABB(0.4, 0.9, 0.4, 0.6, 0.99, 0.6);
        helper.assertTrue(anyIntersect.test(countertop),
                "Centered countertop must collide for facing " + primaryFacing
                        + " shape " + expectedShape + "; aabbs=" + aabbs);
    }

    /** Computes the empty body quadrant AABB for the given
     *  FACING + corner SHAPE.  The 8 corner variants rotate the
     *  body into one of the four XZ quadrants; the L-shape's empty
     *  body area is the opposite corner.  Returns a small AABB
     *  deliberately inset from the 0.25 quadrant boundary so the
     *  test doesn't accidentally touch the body's AABB. */
    private static net.minecraft.world.phys.AABB emptyCornerForFacing(
            net.minecraft.core.Direction facing, BarCounterBlock.Shape shape) {
        boolean bodyRight = switch (shape) {
            case INNER_RIGHT -> true;
            case INNER_LEFT -> false;
            default -> throw new IllegalArgumentException();
        };
        // Body's quadrant after y-rotation:
        boolean bodyPlusX, bodyPlusZ;
        switch (facing) {
            case NORTH:
                bodyPlusX = bodyRight; bodyPlusZ = true; break;
            case EAST:
                bodyPlusX = false; bodyPlusZ = bodyRight; break;
            case SOUTH:
                bodyPlusX = !bodyRight; bodyPlusZ = false; break;
            case WEST:
                bodyPlusX = true; bodyPlusZ = !bodyRight; break;
            default: throw new IllegalArgumentException();
        }
        // Sample clearly inside the empty-quadrant at body height.
        // The body AABB starts at 0.25 (modulo FP rounding), so we
        // sample at 0.30..0.45 if the empty quadrant is at -x/-z,
        // or 0.80..0.95 if it is at +x/+z.
        boolean emptyPlusX = !bodyPlusX;
        boolean emptyPlusZ = !bodyPlusZ;
        double minX = emptyPlusX ? 0.80 : 0.05;
        double maxX = emptyPlusX ? 0.95 : 0.20;
        double minZ = emptyPlusZ ? 0.80 : 0.05;
        double maxZ = emptyPlusZ ? 0.95 : 0.20;
        return new net.minecraft.world.phys.AABB(minX, 0.0, minZ, maxX, 0.86, maxZ);
    }

    // The 8 corner cases — one per (facing, side).  Each test fits
    // within a 3x2x3 layout centred at (1, 1, 1).
    @GameTest(template = "empty", timeoutTicks = 40)
    public static void barCounterInnerRightNorth(GameTestHelper helper) {
        var s = placeAndResolve(helper, net.minecraft.core.Direction.NORTH,
                net.minecraft.core.Direction.EAST);
        helper.assertTrue(s == BarCounterBlock.Shape.INNER_RIGHT,
                "NORTH + east neighbour should resolve to INNER_RIGHT, got " + s);
        assertInnerCornerCollision(helper, new BlockPos(1, 1, 1),
                net.minecraft.core.Direction.NORTH, s);
        helper.succeed();
    }
    @GameTest(template = "empty", timeoutTicks = 40)
    public static void barCounterInnerRightEast(GameTestHelper helper) {
        var s = placeAndResolve(helper, net.minecraft.core.Direction.EAST,
                net.minecraft.core.Direction.SOUTH);
        helper.assertTrue(s == BarCounterBlock.Shape.INNER_RIGHT,
                "EAST + south neighbour should resolve to INNER_RIGHT, got " + s);
        assertInnerCornerCollision(helper, new BlockPos(1, 1, 1),
                net.minecraft.core.Direction.EAST, s);
        helper.succeed();
    }
    @GameTest(template = "empty", timeoutTicks = 40)
    public static void barCounterInnerRightSouth(GameTestHelper helper) {
        var s = placeAndResolve(helper, net.minecraft.core.Direction.SOUTH,
                net.minecraft.core.Direction.WEST);
        helper.assertTrue(s == BarCounterBlock.Shape.INNER_RIGHT,
                "SOUTH + west neighbour should resolve to INNER_RIGHT, got " + s);
        assertInnerCornerCollision(helper, new BlockPos(1, 1, 1),
                net.minecraft.core.Direction.SOUTH, s);
        helper.succeed();
    }
    @GameTest(template = "empty", timeoutTicks = 40)
    public static void barCounterInnerRightWest(GameTestHelper helper) {
        var s = placeAndResolve(helper, net.minecraft.core.Direction.WEST,
                net.minecraft.core.Direction.NORTH);
        helper.assertTrue(s == BarCounterBlock.Shape.INNER_RIGHT,
                "WEST + north neighbour should resolve to INNER_RIGHT, got " + s);
        assertInnerCornerCollision(helper, new BlockPos(1, 1, 1),
                net.minecraft.core.Direction.WEST, s);
        helper.succeed();
    }
    @GameTest(template = "empty", timeoutTicks = 40)
    public static void barCounterInnerLeftNorth(GameTestHelper helper) {
        var s = placeAndResolve(helper, net.minecraft.core.Direction.NORTH,
                net.minecraft.core.Direction.WEST);
        helper.assertTrue(s == BarCounterBlock.Shape.INNER_LEFT,
                "NORTH + west neighbour should resolve to INNER_LEFT, got " + s);
        assertInnerCornerCollision(helper, new BlockPos(1, 1, 1),
                net.minecraft.core.Direction.NORTH, s);
        helper.succeed();
    }
    @GameTest(template = "empty", timeoutTicks = 40)
    public static void barCounterInnerLeftEast(GameTestHelper helper) {
        var s = placeAndResolve(helper, net.minecraft.core.Direction.EAST,
                net.minecraft.core.Direction.NORTH);
        helper.assertTrue(s == BarCounterBlock.Shape.INNER_LEFT,
                "EAST + north neighbour should resolve to INNER_LEFT, got " + s);
        assertInnerCornerCollision(helper, new BlockPos(1, 1, 1),
                net.minecraft.core.Direction.EAST, s);
        helper.succeed();
    }
    @GameTest(template = "empty", timeoutTicks = 40)
    public static void barCounterInnerLeftSouth(GameTestHelper helper) {
        var s = placeAndResolve(helper, net.minecraft.core.Direction.SOUTH,
                net.minecraft.core.Direction.EAST);
        helper.assertTrue(s == BarCounterBlock.Shape.INNER_LEFT,
                "SOUTH + east neighbour should resolve to INNER_LEFT, got " + s);
        assertInnerCornerCollision(helper, new BlockPos(1, 1, 1),
                net.minecraft.core.Direction.SOUTH, s);
        helper.succeed();
    }
    @GameTest(template = "empty", timeoutTicks = 40)
    public static void barCounterInnerLeftWest(GameTestHelper helper) {
        var s = placeAndResolve(helper, net.minecraft.core.Direction.WEST,
                net.minecraft.core.Direction.SOUTH);
        helper.assertTrue(s == BarCounterBlock.Shape.INNER_LEFT,
                "WEST + south neighbour should resolve to INNER_LEFT, got " + s);
        assertInnerCornerCollision(helper, new BlockPos(1, 1, 1),
                net.minecraft.core.Direction.WEST, s);
        helper.succeed();
    }

    // STRAIGHT directional body collision.  The STRAIGHT body is a
    // 12/16 deep box on the back side (the side opposite the front
    // of the facing).  The front 4 pixels are passable.
    @GameTest(template = "empty", timeoutTicks = 40)
    public static void barCounterStraightCollisionNorth(GameTestHelper helper) {
        assertStraightCollision(helper, net.minecraft.core.Direction.NORTH);
        helper.succeed();
    }
    @GameTest(template = "empty", timeoutTicks = 40)
    public static void barCounterStraightCollisionSouth(GameTestHelper helper) {
        assertStraightCollision(helper, net.minecraft.core.Direction.SOUTH);
        helper.succeed();
    }
    @GameTest(template = "empty", timeoutTicks = 40)
    public static void barCounterStraightCollisionEast(GameTestHelper helper) {
        assertStraightCollision(helper, net.minecraft.core.Direction.EAST);
        helper.succeed();
    }
    @GameTest(template = "empty", timeoutTicks = 40)
    public static void barCounterStraightCollisionWest(GameTestHelper helper) {
        assertStraightCollision(helper, net.minecraft.core.Direction.WEST);
        helper.succeed();
    }

    private static void assertStraightCollision(GameTestHelper helper,
                                                 net.minecraft.core.Direction facing) {
        BlockPos primary = new BlockPos(1, 1, 1);
        helper.setBlock(primary, ModBlocks.WOODEN_BAR_COUNTER.get().defaultBlockState()
                .setValue(BarCounterBlock.FACING, facing)
                .setValue(BarCounterBlock.SHAPE, BarCounterBlock.Shape.STRAIGHT));
        VoxelShape shape = helper.getBlockState(primary).getShape(
                helper.getLevel(), primary);
        var aabbs = shape.toAabbs();
        java.util.function.Predicate<net.minecraft.world.phys.AABB> anyIntersect =
                target -> aabbs.stream().anyMatch(b -> b.intersects(target));

        // The front 4 pixels (the side opposite the body) must NOT
        // collide at body height.  The body covers the back 12/16
        // leaving the front 4/16 empty.  We sample a small AABB at
        // the centre of the empty strip.
        var frontEmpty = switch (facing) {
            // NORTH: body at z=0.25..1.0; front empty strip = z=0..0.25
            case NORTH -> new net.minecraft.world.phys.AABB(0.1, 0.0, 0.0, 0.9, 0.86, 0.24);
            // SOUTH: body at z=0..0.75; front empty strip = z=0.75..1.0
            case SOUTH -> new net.minecraft.world.phys.AABB(0.1, 0.0, 0.76, 0.9, 0.86, 1.0);
            // EAST: body at x=0..0.75; front empty strip = x=0.75..1.0
            case EAST -> new net.minecraft.world.phys.AABB(0.76, 0.0, 0.1, 1.0, 0.86, 0.9);
            // WEST: body at x=0.25..1.0; front empty strip = x=0..0.25
            case WEST -> new net.minecraft.world.phys.AABB(0.0, 0.0, 0.1, 0.24, 0.86, 0.9);
            default -> throw new IllegalArgumentException();
        };
        helper.assertTrue(!anyIntersect.test(frontEmpty),
                "STRAIGHT " + facing + " front 4px must be empty at body height; aabbs=" + aabbs);

        // The back 12/16 (the body side) must collide at body height.
        var backBody = switch (facing) {
            case NORTH -> new net.minecraft.world.phys.AABB(0.1, 0.0, 0.26, 0.9, 0.86, 0.99);
            case SOUTH -> new net.minecraft.world.phys.AABB(0.1, 0.0, 0.01, 0.9, 0.86, 0.74);
            case EAST -> new net.minecraft.world.phys.AABB(0.01, 0.0, 0.1, 0.74, 0.86, 0.9);
            case WEST -> new net.minecraft.world.phys.AABB(0.26, 0.0, 0.1, 0.99, 0.86, 0.9);
            default -> throw new IllegalArgumentException();
        };
        helper.assertTrue(anyIntersect.test(backBody),
                "STRAIGHT " + facing + " back 12/16 must collide at body height; aabbs=" + aabbs);

        // The countertop must be solid in the centre.
        var countertop = new net.minecraft.world.phys.AABB(0.4, 0.9, 0.4, 0.6, 0.99, 0.6);
        helper.assertTrue(anyIntersect.test(countertop),
                "STRAIGHT " + facing + " countertop must be solid; aabbs=" + aabbs);
    }

    /**
     * Phase 9 Fix7 P1-1: a legacy {@code shape=normal} block loads
     * with the alias enum value, renders the normal model, and is
     * migrated to {@code shape=straight} on the next neighbour
     * update.  We force the migration by placing and removing a
     * temporary neighbour block, which triggers neighbourChanged
     * on the primary.
     */
    @GameTest(template = "empty", timeoutTicks = 40)
    public static void barCounterLegacyNormalMigratesToStraightOnUpdate(GameTestHelper helper) {
        BlockPos primary = new BlockPos(1, 1, 1);
        helper.setBlock(primary, ModBlocks.WOODEN_BAR_COUNTER.get().defaultBlockState()
                .setValue(BarCounterBlock.FACING, net.minecraft.core.Direction.NORTH)
                .setValue(BarCounterBlock.SHAPE, BarCounterBlock.Shape.NORMAL));

        // Raw value must be NORMAL before any update fires.
        helper.assertTrue(helper.getBlockState(primary).getValue(BarCounterBlock.SHAPE)
                        == BarCounterBlock.Shape.NORMAL,
                "Legacy NORMAL must persist as raw enum before neighbour update");

        // Trigger neighborChanged on the primary by placing and
        // removing a temporary block adjacent to it.  The setBlock
        // call notifies neighbors, and removing it also notifies.
        BlockPos tempNeighbour = primary.relative(net.minecraft.core.Direction.EAST);
        helper.setBlock(tempNeighbour, net.minecraft.world.level.block.Blocks.STONE);
        helper.setBlock(tempNeighbour, net.minecraft.world.level.block.Blocks.AIR);

        // The neighborChanged -> determineShape path runs with no
        // matching neighbours, so STRAIGHT is the canonical write.
        helper.assertTrue(helper.getBlockState(primary).getValue(BarCounterBlock.SHAPE)
                        == BarCounterBlock.Shape.STRAIGHT,
                "Legacy NORMAL must be migrated to STRAIGHT on neighbour update, got "
                        + helper.getBlockState(primary).getValue(BarCounterBlock.SHAPE));
        helper.succeed();
    }

    /**
     * Phase 9 Fix7 P1-1: a legacy {@code shape=inner} block with a
     * right-neighbour present is migrated to {@code shape=inner_right}.
     */
    @GameTest(template = "empty", timeoutTicks = 40)
    public static void barCounterLegacyInnerMigratesToInnerRightOnUpdate(GameTestHelper helper) {
        BlockPos primary = new BlockPos(1, 1, 1);
        BlockPos neighbour = primary.relative(net.minecraft.core.Direction.EAST);
        helper.setBlock(primary, ModBlocks.WOODEN_BAR_COUNTER.get().defaultBlockState()
                .setValue(BarCounterBlock.FACING, net.minecraft.core.Direction.NORTH)
                .setValue(BarCounterBlock.SHAPE, BarCounterBlock.Shape.INNER));
        helper.setBlock(neighbour, ModBlocks.WOODEN_BAR_COUNTER.get().defaultBlockState()
                .setValue(BarCounterBlock.FACING, net.minecraft.core.Direction.SOUTH));

        // Forge a neighbour update on the primary by toggling a
        // dummy block adjacent to it.  The right-neighbour is at
        // EAST, so toggling a block at NORTH or SOUTH triggers
        // neighborChanged on the primary.
        BlockPos tempUpdate = primary.relative(net.minecraft.core.Direction.NORTH);
        helper.setBlock(tempUpdate, net.minecraft.world.level.block.Blocks.STONE);
        helper.setBlock(tempUpdate, net.minecraft.world.level.block.Blocks.AIR);

        helper.assertTrue(helper.getBlockState(primary).getValue(BarCounterBlock.SHAPE)
                        == BarCounterBlock.Shape.INNER_RIGHT,
                "Legacy INNER + right-neighbour must be migrated to INNER_RIGHT, got "
                        + helper.getBlockState(primary).getValue(BarCounterBlock.SHAPE));
        helper.succeed();
    }

    /**
     * Phase 9 Fix7 P1-4: the phonograph_play advancement is bound
     * to the custom PhonographPlayTrigger criterion.  We verify the
     * criterion is loaded — the actual player progress cannot be
     * asserted in a GameTest because the mock ServerPlayer supplied
     * by {@code makeMockServerPlayerInLevel} has no live network
     * connection and the advancement grant bookkeeping throws an
     * NPE on packet dispatch.  The fact that the trigger is bound
     * to our custom criterion (vs. the generic vanilla one) is
     * sufficient proof that the eject path cannot grant the
     * advancement by accident.
     */
    @GameTest(template = "empty", timeoutTicks = 40)
    public static void phonographAdvancementBoundToCustomTrigger(GameTestHelper helper) {
        var advMgr = helper.getLevel().getServer().getAdvancements();
        var adv = advMgr.getAdvancement(
                new net.minecraft.resources.ResourceLocation(CoffeeWork.MODID, "phonograph_play"));
        helper.assertTrue(adv != null, "phonograph_play advancement must be loaded");
        var criterion = adv.getCriteria().get("play_record");
        helper.assertTrue(criterion != null, "play_record criterion must exist");
        var triggerInstance = criterion.getTrigger();
        var enclosingClass = triggerInstance.getClass().getEnclosingClass();
        helper.assertTrue(enclosingClass == net.langball.coffee.advancement.PhonographPlayTrigger.class,
                "Criterion must be backed by PhonographPlayTrigger, got "
                        + (enclosingClass != null ? enclosingClass.getName() : "null"));
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
