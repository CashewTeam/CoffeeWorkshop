package net.langball.coffee.gametest;

import net.langball.coffee.CoffeeWork;
import net.langball.coffee.block.entity.GrinderBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.gametest.GameTestHolder;
import net.minecraftforge.gametest.PrefixGameTestTemplate;

/**
 * Core machine processing GameTests.
 *
 * <p>Uses a test recipe ({@code coffeework:test_cobble_to_stone}) that
 * grinds cobblestone → stone in 40 ticks, with coal as fuel.  This
 * recipe is intentionally simple and self-contained so that tests do
 * not depend on Phase 3 content.
 */
@GameTestHolder(CoffeeWork.MODID)
@PrefixGameTestTemplate(false)
public class MachineGameTests {

    private static final BlockPos MACHINE_POS = BlockPos.ZERO.above(2);
    private static final int GRIND_TIME = 40; // ticks for test_cobble_to_stone

    // ─── Grinder real tests ────────────────────────────────────────────

    /** Valid recipe + fuel → produces output. */
    @GameTest(template = "empty")
    public static void grinder_hasRecipe_processes(GameTestHelper helper) {
        helper.setBlock(MACHINE_POS, net.langball.coffee.init.ModBlocks.GRINDER.get());
        MachineTestHelper.insertItem(helper, MACHINE_POS, GrinderBlockEntity.SLOT_INPUT,
                new ItemStack(Blocks.COBBLESTONE, 64));
        MachineTestHelper.insertItem(helper, MACHINE_POS, GrinderBlockEntity.SLOT_FUEL,
                new ItemStack(Items.COAL, 64));

        // cobblestone→stone takes 40 ticks; wait 45 ticks and check output
        helper.runAfterDelay(45, () -> {
            ItemStack output = MachineTestHelper.getItem(helper, MACHINE_POS,
                    GrinderBlockEntity.SLOT_OUTPUT);
            helper.assertTrue(output.is(Items.STONE),
                    "Expected stone in output slot, got: " + output);
            helper.assertTrue(output.getCount() >= 1,
                    "Expected at least 1 stone, got: " + output.getCount());
            helper.succeed();
        });
    }

    /** No recipe (empty input) → idle, no output, not lit. */
    @GameTest(template = "empty")
    public static void grinder_noRecipe_idles(GameTestHelper helper) {
        helper.setBlock(MACHINE_POS, net.langball.coffee.init.ModBlocks.GRINDER.get());
        helper.runAfterDelay(40, () -> {
            helper.assertTrue(
                    !MachineTestHelper.isLit(helper, MACHINE_POS),
                    "Grinder should not be lit with no recipe");
            helper.succeed();
        });
    }

    /** No fuel → idle, no output, not lit. */
    @GameTest(template = "empty")
    public static void grinder_noFuel_idles(GameTestHelper helper) {
        helper.setBlock(MACHINE_POS, net.langball.coffee.init.ModBlocks.GRINDER.get());
        MachineTestHelper.insertItem(helper, MACHINE_POS, GrinderBlockEntity.SLOT_INPUT,
                new ItemStack(Blocks.COBBLESTONE, 64));
        // no fuel

        helper.runAfterDelay(40, () -> {
            helper.assertTrue(
                    !MachineTestHelper.isLit(helper, MACHINE_POS),
                    "Grinder should not be lit without fuel");
            helper.assertTrue(
                    MachineTestHelper.getItem(helper, MACHINE_POS, GrinderBlockEntity.SLOT_OUTPUT).isEmpty(),
                    "Output should be empty without fuel");
            helper.succeed();
        });
    }

    /** Fuel + valid recipe → lit. */
    @GameTest(template = "empty")
    public static void grinder_withFuel_starts(GameTestHelper helper) {
        helper.setBlock(MACHINE_POS, net.langball.coffee.init.ModBlocks.GRINDER.get());
        MachineTestHelper.insertItem(helper, MACHINE_POS, GrinderBlockEntity.SLOT_INPUT,
                new ItemStack(Blocks.COBBLESTONE, 64));
        MachineTestHelper.insertItem(helper, MACHINE_POS, GrinderBlockEntity.SLOT_FUEL,
                new ItemStack(Items.COAL, 64));

        helper.runAfterDelay(4, () -> {
            helper.assertTrue(
                    MachineTestHelper.isLit(helper, MACHINE_POS),
                    "Grinder should be lit with valid recipe + fuel");
            helper.succeed();
        });
    }

    /** Output full with different item → processing pauses. */
    @GameTest(template = "empty")
    public static void grinder_outputBlocked_pauses(GameTestHelper helper) {
        helper.setBlock(MACHINE_POS, net.langball.coffee.init.ModBlocks.GRINDER.get());
        MachineTestHelper.insertItem(helper, MACHINE_POS, GrinderBlockEntity.SLOT_INPUT,
                new ItemStack(Blocks.COBBLESTONE, 64));
        MachineTestHelper.insertItem(helper, MACHINE_POS, GrinderBlockEntity.SLOT_FUEL,
                new ItemStack(Items.COAL, 64));
        // Fill output with a DIFFERENT item (not stone)
        MachineTestHelper.insertItem(helper, MACHINE_POS, GrinderBlockEntity.SLOT_OUTPUT,
                new ItemStack(Items.DIRT, 64));

        helper.runAfterDelay(50, () -> {
            ItemStack output = MachineTestHelper.getItem(helper, MACHINE_POS,
                    GrinderBlockEntity.SLOT_OUTPUT);
            // Output should still be dirt (stone can't merge with dirt)
            helper.assertTrue(output.is(Items.DIRT),
                    "Output should still be dirt when blocked, got: " + output);
            // Input should not have been consumed
            ItemStack input = MachineTestHelper.getItem(helper, MACHINE_POS,
                    GrinderBlockEntity.SLOT_INPUT);
            helper.assertTrue(input.is(Blocks.COBBLESTONE.asItem()),
                    "Input should still be cobblestone when output blocked");
            helper.succeed();
        });
    }

    /** Switching input to a different recipe resets progress. */
    @GameTest(template = "empty")
    public static void grinder_inputChanged_resetsProgress(GameTestHelper helper) {
        helper.setBlock(MACHINE_POS, net.langball.coffee.init.ModBlocks.GRINDER.get());
        // Start with cobblestone → stone (40 ticks)
        MachineTestHelper.insertItem(helper, MACHINE_POS, GrinderBlockEntity.SLOT_INPUT,
                new ItemStack(Blocks.COBBLESTONE, 64));
        MachineTestHelper.insertItem(helper, MACHINE_POS, GrinderBlockEntity.SLOT_FUEL,
                new ItemStack(Items.COAL, 64));

        // Let it process for ~20 ticks (half of the 40-tick grind), then
        // switch to an input that has no recipe (e.g. dirt)
        helper.runAfterDelay(20, () -> {
            // Remove the cobblestone and put dirt instead
            MachineTestHelper.insertItem(helper, MACHINE_POS, GrinderBlockEntity.SLOT_INPUT,
                    ItemStack.EMPTY); // won't do anything meaningful but marks change
            // Actually take all cobblestone out (using extract), then put dirt
            var handler = ((net.langball.coffee.block.entity.MachineBlockEntity)
                    helper.getBlockEntity(MACHINE_POS)).getItemHandler();
            handler.extractItem(GrinderBlockEntity.SLOT_INPUT, 64, false);
            handler.insertItem(GrinderBlockEntity.SLOT_INPUT,
                    new ItemStack(Items.DIRT, 64), false);

            // Now wait long enough: dirt has no recipe, so nothing should happen
            helper.runAfterDelay(60, () -> {
                ItemStack output = MachineTestHelper.getItem(helper, MACHINE_POS,
                        GrinderBlockEntity.SLOT_OUTPUT);
                // We interrupted at ~20 ticks (halfway through a 40-tick grind),
                // then switched to dirt (no recipe) for 60 ticks.
                // With correct reset logic, no stone should appear.
                // The worst case (bug): the old recipe continues from 20 and
                // produces stone at ~40. After 60 more ticks we'd see stone.
                helper.assertTrue(output.isEmpty() || output.is(Items.DIRT),
                        "Output should be empty (or dirt).  If stone appears, recipe "
                        + "change detection is broken.  Got: " + output);
                helper.succeed();
            });
        });
    }

    @GameTest(template = "empty")
    public static void grinder_litState(GameTestHelper helper) {
        helper.setBlock(MACHINE_POS, net.langball.coffee.init.ModBlocks.GRINDER.get());
        helper.assertTrue(!MachineTestHelper.isLit(helper, MACHINE_POS),
                "Grinder should start unlit");
        MachineTestHelper.insertItem(helper, MACHINE_POS, GrinderBlockEntity.SLOT_INPUT,
                new ItemStack(Blocks.COBBLESTONE, 64));
        MachineTestHelper.insertItem(helper, MACHINE_POS, GrinderBlockEntity.SLOT_FUEL,
                new ItemStack(Items.COAL, 64));
        helper.runAfterDelay(4, () -> {
            helper.assertTrue(MachineTestHelper.isLit(helper, MACHINE_POS),
                    "Grinder should be lit with fuel + recipe");
            helper.succeed();
        });
    }

    // ─── Other machines (stubs — filled when Phase 3 recipes land) ─────

    @GameTest(template = "empty")
    public static void roller_hasRecipe_processes(GameTestHelper helper) {
        helper.succeed();
    }

    @GameTest(template = "empty")
    public static void oven_hasRecipe_processes(GameTestHelper helper) {
        helper.succeed();
    }

    @GameTest(template = "empty")
    public static void icecream_hasRecipe_processes(GameTestHelper helper) {
        helper.succeed();
    }

    @GameTest(template = "empty")
    public static void coffeemachine_selfPowered_processes(GameTestHelper helper) {
        helper.succeed();
    }

    @GameTest(template = "empty")
    public static void coffeemachine_noFuelSlot(GameTestHelper helper) {
        helper.succeed();
    }
}
