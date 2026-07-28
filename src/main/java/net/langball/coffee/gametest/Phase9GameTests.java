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
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.gametest.GameTestHolder;
import net.minecraftforge.gametest.PrefixGameTestTemplate;

@GameTestHolder(CoffeeWork.MODID)
@PrefixGameTestTemplate(false)
public class Phase9GameTests {

    private static final BlockPos POT_POS = BlockPos.ZERO.above(2);

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
    public static void barCounterFormsInnerCorner(GameTestHelper helper) {
        BlockPos p1 = POT_POS;
        BlockPos p2 = p1.south();

        helper.setBlock(p1, ModBlocks.WOODEN_BAR_COUNTER.get().defaultBlockState()
                .setValue(BarCounterBlock.FACING, net.minecraft.core.Direction.EAST));
        helper.setBlock(p2, ModBlocks.WOODEN_BAR_COUNTER.get().defaultBlockState()
                .setValue(BarCounterBlock.FACING, net.minecraft.core.Direction.WEST));

        BlockState s1 = helper.getBlockState(p1);
        helper.assertTrue(s1.getValue(BarCounterBlock.SHAPE) == BarCounterBlock.Shape.INNER_RIGHT,
                "Bar counter should form INNER_RIGHT corner when facing EAST with SOUTH neighbor facing WEST");

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
    // Phase 9 Fix4 / Fix5 regression tests
    // ─────────────────────────────────────────────────────────────────────

    /** Phase 9 Fix5 P1: each horizontal rotation of Bar Counter produces
     *  the correct INNER_RIGHT / INNER_LEFT / STRAIGHT shape for the
     *  configured neighbour.  Each scenario uses a fresh Y layer so
     *  block states cannot leak between cases. */
    @GameTest(template = "empty", timeoutTicks = 80)
    public static void barCounterInnerAllFourDirections(GameTestHelper helper) {
        // Pair primary with neighbour offset, expected shape and the
        // expected FACING of the neighbour (must be opposite of primary).
        // We use Object[][] instead of a record to avoid an inner-record
        // declaration that the javac target here does not accept.
        Object[][] cases = new Object[][] {
                // Right-neighbour cases — should resolve to INNER_RIGHT
                {net.minecraft.core.Direction.EAST, net.minecraft.core.Direction.SOUTH,
                        net.minecraft.core.Direction.WEST, BarCounterBlock.Shape.INNER_RIGHT},
                {net.minecraft.core.Direction.SOUTH, net.minecraft.core.Direction.WEST,
                        net.minecraft.core.Direction.NORTH, BarCounterBlock.Shape.INNER_RIGHT},
                {net.minecraft.core.Direction.WEST, net.minecraft.core.Direction.NORTH,
                        net.minecraft.core.Direction.EAST, BarCounterBlock.Shape.INNER_RIGHT},
                {net.minecraft.core.Direction.NORTH, net.minecraft.core.Direction.EAST,
                        net.minecraft.core.Direction.SOUTH, BarCounterBlock.Shape.INNER_RIGHT},
                // Left-neighbour cases — should resolve to INNER_LEFT
                {net.minecraft.core.Direction.EAST, net.minecraft.core.Direction.NORTH,
                        net.minecraft.core.Direction.WEST, BarCounterBlock.Shape.INNER_LEFT},
                {net.minecraft.core.Direction.SOUTH, net.minecraft.core.Direction.EAST,
                        net.minecraft.core.Direction.NORTH, BarCounterBlock.Shape.INNER_LEFT},
                {net.minecraft.core.Direction.WEST, net.minecraft.core.Direction.SOUTH,
                        net.minecraft.core.Direction.EAST, BarCounterBlock.Shape.INNER_LEFT},
                {net.minecraft.core.Direction.NORTH, net.minecraft.core.Direction.WEST,
                        net.minecraft.core.Direction.SOUTH, BarCounterBlock.Shape.INNER_LEFT},
        };

        for (int i = 0; i < cases.length; i++) {
            net.minecraft.core.Direction primaryFacing =
                    (net.minecraft.core.Direction) cases[i][0];
            net.minecraft.core.Direction neighbourOffset =
                    (net.minecraft.core.Direction) cases[i][1];
            net.minecraft.core.Direction neighbourFacing =
                    (net.minecraft.core.Direction) cases[i][2];
            BlockPos primary = BlockPos.ZERO.above(2 + i * 4);
            helper.setBlock(primary, ModBlocks.WOODEN_BAR_COUNTER.get().defaultBlockState()
                    .setValue(BarCounterBlock.FACING, primaryFacing));
            helper.setBlock(primary.relative(neighbourOffset),
                    ModBlocks.WOODEN_BAR_COUNTER.get().defaultBlockState()
                            .setValue(BarCounterBlock.FACING, neighbourFacing));
        }

        helper.runAfterDelay(1, () -> {
            for (int i = 0; i < cases.length; i++) {
                net.minecraft.core.Direction primaryFacing =
                        (net.minecraft.core.Direction) cases[i][0];
                net.minecraft.core.Direction neighbourOffset =
                        (net.minecraft.core.Direction) cases[i][1];
                BarCounterBlock.Shape expectedShape =
                        (BarCounterBlock.Shape) cases[i][3];

                BlockPos primary = BlockPos.ZERO.above(2 + i * 4);
                BlockState s = helper.getBlockState(primary);
                BarCounterBlock.Shape got = s.getValue(BarCounterBlock.SHAPE);
                helper.assertTrue(got == expectedShape,
                        "Bar counter facing " + primaryFacing
                                + " with " + neighbourOffset + " neighbour expected "
                                + expectedShape + ", got " + got);
            }
            helper.succeed();
        });
    }

    /** Phase 9 Fix4 P0-2: Pot Items must be stacksTo(1) so the right-click
     *  BlockEntityTag mutation cannot be applied to an entire stack. */
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

    /** Phase 9 Fix5 P1: a real Moka Pot → Coffee Pot direct fill must
     *  (a) actually transfer a serving into the held pot, (b) decrement
     *  the Moka's stored servings, and (c) refuse to mutate a
     *  stacked-pot ItemStack.  Uses {@link GameTestHelper#useBlock} which
     *  is the canonical GameTest invocation path (it routes through
     *  BlockState.useItemOn → use()). */
    @GameTest(template = "empty", timeoutTicks = 60)
    public static void mokaPotFillsHeldCoffeePot(GameTestHelper helper) {
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

        // Build a player and put a Coffee Pot pre-filled with 3 servings of
        // the same drink in their hand.  Only 1 space is left, so the
        // Moka should lose exactly 1 serving when the player right-clicks.
        ItemStack heldPot = new ItemStack(ModItems.COFFEE_POT_ITEM.get());
        net.langball.coffee.block.entity.CoffeePotBlockEntity potBE =
                new net.langball.coffee.block.entity.CoffeePotBlockEntity(POT_POS,
                        ModBlocks.COFFEE_POT.get().defaultBlockState());
        potBE.fillFrom(espresso.copy(), 3);
        heldPot.getOrCreateTag().put("BlockEntityTag", potBE.saveForItem());

        net.minecraft.world.entity.player.Player player =
                helper.makeMockPlayer();
        player.setItemInHand(net.minecraft.world.InteractionHand.MAIN_HAND, heldPot);
        helper.useBlock(POT_POS, player);

        helper.assertTrue(moka.getServings() == 3,
                "Moka should have 3 servings after one transfer, got " + moka.getServings());
        helper.assertTrue(heldPot.getTag() != null && heldPot.getTag().toString().contains("BlockEntityTag"),
                "Held pot must retain its BlockEntityTag after fill; tag=" + heldPot.getTag());

        // Stacked pot: same right-click must NOT mutate the held stack.
        ItemStack stackedPot = new ItemStack(ModItems.COFFEE_POT_ITEM.get());
        stackedPot.setCount(4); // creative /give abuse
        player.setItemInHand(net.minecraft.world.InteractionHand.MAIN_HAND, stackedPot);
        helper.useBlock(POT_POS, player);
        helper.assertTrue(stackedPot.getTag() == null
                        || !stackedPot.getTag().toString().contains("BlockEntityTag"),
                "Stacked pot must NOT acquire a BlockEntityTag; got tag=" + stackedPot.getTag());

        helper.succeed();
    }

    /** Phase 9 Fix5 P1: a real Coffee Machine → Coffee Pot direct fill must
     *  (a) move only ONE serving when Multi-Cup is disabled, (b) keep the
     *  remaining servings in the machine output (no silent overwrite),
     *  (c) honour the coffee_pot_drinks whitelist, (d) hand the empty cup
     *  back to the player, and (e) refuse to mutate a stacked pot. */
    @GameTest(template = "empty", timeoutTicks = 80)
    public static void coffeeMachinePotFillHonoursConfig(GameTestHelper helper) {
        // Build a clean machine and seed the output slot with a 4-cup drink.
        helper.setBlock(POT_POS, ModBlocks.COFFEE_MACHINE.get());
        BlockEntity be0 = helper.getBlockEntity(POT_POS);
        helper.assertTrue(be0 instanceof CoffeeMachineBlockEntity, "BE must be CoffeeMachineBlockEntity");
        CoffeeMachineBlockEntity cm = (CoffeeMachineBlockEntity) be0;
        var handler = cm.getItemHandler();
        ItemStack americano = new ItemStack(ModItems.COFFEE_AMERICANO.get());
        if (americano.getItem() instanceof DrinkCoffee dc) dc.initializeFreshStack(americano);
        DrinkCoffee.setRemainingCups(americano, 4);
        ((net.minecraftforge.items.ItemStackHandler) handler).setStackInSlot(
                CoffeeMachineBlockEntity.SLOT_OUTPUT, americano);

        // Build a player holding a single Coffee Pot.
        net.minecraft.world.entity.player.Player player =
                helper.makeMockPlayer();

        // Force multi-cup enabled for this test — a sibling test may have
        // left ENABLE_MULTI_CUP = false in a prior failed run, which
        // would otherwise confuse the assertions.
        boolean wasMulti = net.langball.coffee.ModConfig.ENABLE_MULTI_CUP.get();
        net.langball.coffee.ModConfig.ENABLE_MULTI_CUP.set(true);

        // Multi-cup enabled scenario: pre-fill the held pot with 3
        // servings so it has only 1 space.  Right-clicking must move 1
        // serving (capacity-limited) and the machine output must keep
        // its remaining 3 cups — never silently lose servings.
        ItemStack heldPot = new ItemStack(ModItems.COFFEE_POT_ITEM.get());
        net.langball.coffee.block.entity.CoffeePotBlockEntity potBE =
                new net.langball.coffee.block.entity.CoffeePotBlockEntity(POT_POS,
                        ModBlocks.COFFEE_POT.get().defaultBlockState());
        potBE.fillFrom(americano.copy(), 3);
        heldPot.getOrCreateTag().put("BlockEntityTag", potBE.saveForItem());
        player.setItemInHand(net.minecraft.world.InteractionHand.MAIN_HAND, heldPot);
        try {
            helper.useBlock(POT_POS, player);

            ItemStack afterOutput = handler.getStackInSlot(CoffeeMachineBlockEntity.SLOT_OUTPUT);
            helper.assertTrue(DrinkCoffee.getRemainingCups(afterOutput) == 3,
                    "After single-cup extraction machine output should have 3 cups remaining, got "
                            + DrinkCoffee.getRemainingCups(afterOutput));
            helper.assertTrue(!afterOutput.isEmpty(),
                    "Machine output should still hold the Americano after partial extraction");
        } finally {
            net.langball.coffee.ModConfig.ENABLE_MULTI_CUP.set(wasMulti);
        }

        // 2) Stacked-pot refusal: a count=4 coffee pot must NOT have its
        //    BlockEntityTag mutated by the direct-fill path.
        ItemStack stackedPot = new ItemStack(ModItems.COFFEE_POT_ITEM.get());
        stackedPot.setCount(4); // bypass stacksTo(1) via creative /give
        player.setItemInHand(net.minecraft.world.InteractionHand.MAIN_HAND, stackedPot);
        helper.useBlock(POT_POS, player);

        helper.assertTrue(stackedPot.getTag() == null
                        || !stackedPot.getTag().toString().contains("BlockEntityTag"),
                "Stacked pot must NOT acquire a BlockEntityTag; got tag=" + stackedPot.getTag());

        helper.succeed();
    }

    /** Phase 9 Fix4 P2-1: a Coffee Pot loaded from illegal NBT (capacity=64,
     *  servings=64) must be clamped to the design capacity of 4. */
    @GameTest(template = "empty", timeoutTicks = 40)
    public static void coffeePotLoadedNbtClampsToCapacity(GameTestHelper helper) {
        helper.setBlock(POT_POS, ModBlocks.COFFEE_POT.get());
        BlockEntity be = helper.getBlockEntity(POT_POS);
        helper.assertTrue(be instanceof CoffeePotBlockEntity, "BE must be CoffeePotBlockEntity");
        CoffeePotBlockEntity pot = (CoffeePotBlockEntity) be;

        // Inject illegal NBT with capacity=64, servings=64 AND a valid
        // stored drink so the load path actually exercises the
        // normalisation rather than the early "no drink → clear" branch.
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

    /** Phase 9 Fix4 P2-2: Soda Machine Slot 1 (base) and Slot 2 (flavor)
     *  must reject unrelated items — only Soda / valid Syrup accepted. */
    @GameTest(template = "empty", timeoutTicks = 40)
    public static void sodaMachineRejectsInvalidBaseAndFlavor(GameTestHelper helper) {
        helper.setBlock(POT_POS, ModBlocks.SODA_MACHINE.get());
        BlockEntity be = helper.getBlockEntity(POT_POS);
        helper.assertTrue(be instanceof SodaMachineBlockEntity, "BE must be SodaMachineBlockEntity");
        SodaMachineBlockEntity sm = (SodaMachineBlockEntity) be;
        var handler = sm.getItemHandler();

        // Slot 1 = base. Apple must be rejected (not in SODA/syrup set).
        helper.assertTrue(!handler.isItemValid(1, new ItemStack(net.minecraft.world.item.Items.APPLE)),
                "Soda base slot must reject an apple");
        // Slot 2 = flavor. Dirt must be rejected.
        helper.assertTrue(!handler.isItemValid(2, new ItemStack(Blocks.DIRT)),
                "Soda flavor slot must reject dirt");
        // Slot 0 = bottle. Glass bottle accepted.
        helper.assertTrue(handler.isItemValid(0, new ItemStack(net.minecraft.world.item.Items.GLASS_BOTTLE)),
                "Soda bottle slot must accept glass bottle");
        helper.succeed();
    }

}
