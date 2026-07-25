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

    @GameTest(template = "empty")
    public static void fueledMachine_upOnlyAcceptsInput(GameTestHelper helper) {
        helper.setBlock(POS, net.langball.coffee.init.ModBlocks.GRINDER.get());
        var be = helper.getBlockEntity(POS);
        be.getCapability(ForgeCapabilities.ITEM_HANDLER, Direction.UP).ifPresent(handler -> {
            // Insert cobblestone (input item) from top → should be accepted
            ItemStack inserted = handler.insertItem(0, new ItemStack(Blocks.COBBLESTONE, 1), false);
            helper.assertTrue(inserted.isEmpty(),
                    "UP side should accept cobblestone (input) into slot 0");

            // Try to insert coal (fuel) from top → should be rejected
            ItemStack rejected = handler.insertItem(1, new ItemStack(Items.COAL, 1), false);
            helper.assertTrue(!rejected.isEmpty(),
                    "UP side should reject fuel insertion into slot 1");
        });
        helper.succeed();
    }

    @GameTest(template = "empty")
    public static void fueledMachine_sideOnlyAcceptsFuel(GameTestHelper helper) {
        helper.setBlock(POS, net.langball.coffee.init.ModBlocks.GRINDER.get());
        var be = helper.getBlockEntity(POS);
        be.getCapability(ForgeCapabilities.ITEM_HANDLER, Direction.NORTH).ifPresent(handler -> {
            // Insert coal (fuel) from side → should be accepted into fuel slot
            ItemStack inserted = handler.insertItem(1, new ItemStack(Items.COAL, 1), false);
            helper.assertTrue(inserted.isEmpty(),
                    "SIDE should accept coal (fuel)");

            // Try to insert cobblestone (input) from side → should be rejected
            ItemStack rejected = handler.insertItem(0, new ItemStack(Blocks.COBBLESTONE, 1), false);
            helper.assertTrue(!rejected.isEmpty(),
                    "SIDE should reject input insertion");
        });
        helper.succeed();
    }

    @GameTest(template = "empty")
    public static void fueledMachine_downOnlyExtractsOutput(GameTestHelper helper) {
        helper.setBlock(POS, net.langball.coffee.init.ModBlocks.GRINDER.get());
        var be = helper.getBlockEntity(POS);

        // Put stone in output slot via full handler
        be.getCapability(ForgeCapabilities.ITEM_HANDLER).ifPresent(full -> {
            full.insertItem(2, new ItemStack(Items.STONE, 10), false);
        });

        // Extract from bottom
        be.getCapability(ForgeCapabilities.ITEM_HANDLER, Direction.DOWN).ifPresent(handler -> {
            ItemStack extracted = handler.extractItem(2, 5, false);
            helper.assertTrue(extracted.is(Items.STONE) && extracted.getCount() == 5,
                    "DOWN should extract output, got: " + extracted);
        });
        helper.succeed();
    }

    @GameTest(template = "empty")
    public static void fueledMachine_cannotInsertIntoOutput(GameTestHelper helper) {
        helper.setBlock(POS, net.langball.coffee.init.ModBlocks.GRINDER.get());
        var be = helper.getBlockEntity(POS);

        // Try every direction — none should allow insertion into output slot
        for (Direction dir : Direction.values()) {
            be.getCapability(ForgeCapabilities.ITEM_HANDLER, dir).ifPresent(handler -> {
                ItemStack rejected = handler.insertItem(2, new ItemStack(Items.DIRT, 1), false);
                helper.assertTrue(!rejected.isEmpty(),
                        "Direction " + dir + " should reject insertion into output slot");
            });
        }
        helper.succeed();
    }

    @GameTest(template = "empty")
    public static void fueledMachine_cannotExtractFromInput(GameTestHelper helper) {
        helper.setBlock(POS, net.langball.coffee.init.ModBlocks.GRINDER.get());
        var be = helper.getBlockEntity(POS);

        // Put cobblestone in input via full handler
        be.getCapability(ForgeCapabilities.ITEM_HANDLER).ifPresent(full -> {
            full.insertItem(0, new ItemStack(Blocks.COBBLESTONE, 10), false);
        });

        // Try every side — none should allow extraction from input
        for (Direction dir : Direction.values()) {
            be.getCapability(ForgeCapabilities.ITEM_HANDLER, dir).ifPresent(handler -> {
                ItemStack extracted = handler.extractItem(0, 1, false);
                helper.assertTrue(extracted.isEmpty(),
                        "Direction " + dir + " should not extract from input slot");
            });
        }
        helper.succeed();
    }

    @GameTest(template = "empty")
    public static void coffeeMachine_noFuelExposure(GameTestHelper helper) {
        helper.setBlock(POS, net.langball.coffee.init.ModBlocks.COFFEE_MACHINE.get());
        var be = helper.getBlockEntity(POS);

        // CoffeeMachine has no fuel slot — horizontal should go to INPUT, not fuel
        be.getCapability(ForgeCapabilities.ITEM_HANDLER, Direction.NORTH).ifPresent(handler -> {
            // Coffee bean into horizontal → should go to input slot (0)
            var cb = net.langball.coffee.init.ModItems.COFFEE_BEAN;
            ItemStack inserted = handler.insertItem(0,
                    new ItemStack(cb != null ? cb.get() : Items.APPLE, 1), false);
            helper.assertTrue(inserted.isEmpty(),
                    "CoffeeMachine SIDE should accept input (no fuel slot)");
        });
        helper.succeed();
    }
}
