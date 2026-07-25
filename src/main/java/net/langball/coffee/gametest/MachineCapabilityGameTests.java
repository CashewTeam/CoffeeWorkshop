package net.langball.coffee.gametest;

import net.langball.coffee.CoffeeWork;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.gametest.GameTestHolder;
import net.minecraftforge.gametest.PrefixGameTestTemplate;
import net.minecraftforge.items.IItemHandler;

/**
 * Sided capability GameTests — verify UP=input, HORIZONTAL=fuel,
 * DOWN=output, and CoffeeMachine exceptions.
 */
@GameTestHolder(CoffeeWork.MODID)
@PrefixGameTestTemplate(false)
public class MachineCapabilityGameTests {

    private static final BlockPos POS = BlockPos.ZERO.above(2);

    /** Resolves the IItemHandler for a direction, failing the test if absent. */
    private static IItemHandler requireHandler(GameTestHelper helper, BlockPos pos, Direction dir) {
        var be = helper.getBlockEntity(pos);
        return be.getCapability(ForgeCapabilities.ITEM_HANDLER, dir)
                .resolve()
                .orElseThrow(() -> new AssertionError(
                        "Expected ITEM_HANDLER capability on " + dir + " side"));
    }

    @GameTest(template = "empty")
    public static void fueledMachine_upOnlyAcceptsInput(GameTestHelper helper) {
        helper.setBlock(POS, net.langball.coffee.init.ModBlocks.GRINDER.get());
        IItemHandler handler = requireHandler(helper, POS, Direction.UP);

        ItemStack inserted = handler.insertItem(0, new ItemStack(Blocks.COBBLESTONE, 1), false);
        helper.assertTrue(inserted.isEmpty(),
                "UP side should accept cobblestone (input) into slot 0");

        ItemStack rejected = handler.insertItem(1, new ItemStack(Items.COAL, 1), false);
        helper.assertTrue(!rejected.isEmpty(),
                "UP side should reject fuel insertion into slot 1");

        helper.succeed();
    }

    @GameTest(template = "empty")
    public static void fueledMachine_sideOnlyAcceptsFuel(GameTestHelper helper) {
        helper.setBlock(POS, net.langball.coffee.init.ModBlocks.GRINDER.get());
        IItemHandler handler = requireHandler(helper, POS, Direction.NORTH);

        ItemStack inserted = handler.insertItem(1, new ItemStack(Items.COAL, 1), false);
        helper.assertTrue(inserted.isEmpty(),
                "SIDE should accept coal (fuel)");

        ItemStack rejected = handler.insertItem(0, new ItemStack(Blocks.COBBLESTONE, 1), false);
        helper.assertTrue(!rejected.isEmpty(),
                "SIDE should reject input insertion");

        helper.succeed();
    }

    @GameTest(template = "empty")
    public static void fueledMachine_downOnlyExtractsOutput(GameTestHelper helper) {
        helper.setBlock(POS, net.langball.coffee.init.ModBlocks.GRINDER.get());
        MachineTestHelper.setItem(helper, POS, 2, new ItemStack(Items.STONE, 10));

        IItemHandler handler = requireHandler(helper, POS, Direction.DOWN);
        ItemStack extracted = handler.extractItem(2, 5, false);
        helper.assertTrue(extracted.is(Items.STONE) && extracted.getCount() == 5,
                "DOWN should extract output, got: " + extracted);

        helper.succeed();
    }

    @GameTest(template = "empty")
    public static void fueledMachine_cannotInsertIntoOutput(GameTestHelper helper) {
        helper.setBlock(POS, net.langball.coffee.init.ModBlocks.GRINDER.get());

        for (Direction dir : Direction.values()) {
            IItemHandler handler = requireHandler(helper, POS, dir);
            ItemStack rejected = handler.insertItem(2, new ItemStack(Items.DIRT, 1), false);
            helper.assertTrue(!rejected.isEmpty(),
                    "Direction " + dir + " should reject insertion into output slot");
        }
        helper.succeed();
    }

    @GameTest(template = "empty")
    public static void fueledMachine_cannotExtractFromInput(GameTestHelper helper) {
        helper.setBlock(POS, net.langball.coffee.init.ModBlocks.GRINDER.get());

        // Put cobblestone in input via full handler
        var full = helper.getBlockEntity(POS)
                .getCapability(ForgeCapabilities.ITEM_HANDLER)
                .resolve()
                .orElseThrow(() -> new AssertionError("Full handler missing"));
        full.insertItem(0, new ItemStack(Blocks.COBBLESTONE, 10), false);

        for (Direction dir : Direction.values()) {
            IItemHandler handler = requireHandler(helper, POS, dir);
            ItemStack extracted = handler.extractItem(0, 1, false);
            helper.assertTrue(extracted.isEmpty(),
                    "Direction " + dir + " should not extract from input slot");
        }
        helper.succeed();
    }

    @GameTest(template = "empty")
    public static void coffeeMachine_noFuelExposure(GameTestHelper helper) {
        helper.setBlock(POS, net.langball.coffee.init.ModBlocks.COFFEE_MACHINE.get());
        IItemHandler handler = requireHandler(helper, POS, Direction.NORTH);

        // CoffeeMachine has no fuel slot — horizontal should accept input
        var cb = net.langball.coffee.init.ModItems.COFFEE_BEAN;
        ItemStack inserted = handler.insertItem(0,
                new ItemStack(cb != null ? cb.get() : Items.APPLE, 1), false);
        helper.assertTrue(inserted.isEmpty(),
                "CoffeeMachine SIDE should accept input (no fuel slot)");

        helper.succeed();
    }
}
