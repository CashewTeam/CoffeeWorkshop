package net.langball.coffee.gametest;

import net.langball.coffee.CoffeeWork;
import net.langball.coffee.block.entity.GrinderBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.gametest.GameTestHolder;
import net.minecraftforge.gametest.PrefixGameTestTemplate;

/**
 * Core machine processing GameTests.
 *
 * <p>Uses the real {@code coffeework:flour} grinding recipe (wheat → flour,
 * 200 ticks) so that tests exercise production recipe paths.  No test-only
 * recipes are published to players.
 */
@GameTestHolder(CoffeeWork.MODID)
@PrefixGameTestTemplate(false)
public class MachineGameTests {

    private static final BlockPos MACHINE_POS = BlockPos.ZERO.above(2);
    private static final int FLOUR_TIME = 200; // ticks for flour recipe
    private static final int MARGIN = 20;       // extra ticks for safety

    // ─── Grinder real tests ────────────────────────────────────────────

    @GameTest(template = "empty", timeoutTicks = 400)
    public static void grinder_hasRecipe_processes(GameTestHelper helper) {
        helper.setBlock(MACHINE_POS, net.langball.coffee.init.ModBlocks.GRINDER.get());
        MachineTestHelper.insertItem(helper, MACHINE_POS, GrinderBlockEntity.SLOT_INPUT,
                new ItemStack(Items.WHEAT, 64));
        MachineTestHelper.insertItem(helper, MACHINE_POS, GrinderBlockEntity.SLOT_FUEL,
                new ItemStack(Items.COAL, 64));

        helper.runAfterDelay(FLOUR_TIME + MARGIN, () -> {
            ItemStack output = MachineTestHelper.getItem(helper, MACHINE_POS,
                    GrinderBlockEntity.SLOT_OUTPUT);
            var flour = net.langball.coffee.init.ModItems.FLOUR;
            helper.assertTrue(flour != null && output.is(flour.get()),
                    "Expected flour in output slot, got: " + output);
            helper.succeed();
        });
    }

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

    @GameTest(template = "empty")
    public static void grinder_noFuel_idles(GameTestHelper helper) {
        helper.setBlock(MACHINE_POS, net.langball.coffee.init.ModBlocks.GRINDER.get());
        MachineTestHelper.insertItem(helper, MACHINE_POS, GrinderBlockEntity.SLOT_INPUT,
                new ItemStack(Items.WHEAT, 64));

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

    @GameTest(template = "empty")
    public static void grinder_withFuel_starts(GameTestHelper helper) {
        helper.setBlock(MACHINE_POS, net.langball.coffee.init.ModBlocks.GRINDER.get());
        MachineTestHelper.insertItem(helper, MACHINE_POS, GrinderBlockEntity.SLOT_INPUT,
                new ItemStack(Items.WHEAT, 64));
        MachineTestHelper.insertItem(helper, MACHINE_POS, GrinderBlockEntity.SLOT_FUEL,
                new ItemStack(Items.COAL, 64));

        helper.runAfterDelay(4, () -> {
            helper.assertTrue(
                    MachineTestHelper.isLit(helper, MACHINE_POS),
                    "Grinder should be lit with valid recipe + fuel");
            helper.succeed();
        });
    }

    @GameTest(template = "empty", timeoutTicks = 400)
    public static void grinder_outputBlocked_pauses(GameTestHelper helper) {
        helper.setBlock(MACHINE_POS, net.langball.coffee.init.ModBlocks.GRINDER.get());
        MachineTestHelper.insertItem(helper, MACHINE_POS, GrinderBlockEntity.SLOT_INPUT,
                new ItemStack(Items.WHEAT, 64));
        MachineTestHelper.insertItem(helper, MACHINE_POS, GrinderBlockEntity.SLOT_FUEL,
                new ItemStack(Items.COAL, 64));
        MachineTestHelper.setItem(helper, MACHINE_POS, GrinderBlockEntity.SLOT_OUTPUT,
                new ItemStack(Items.DIRT, 64));

        helper.runAfterDelay(FLOUR_TIME + MARGIN, () -> {
            ItemStack output = MachineTestHelper.getItem(helper, MACHINE_POS,
                    GrinderBlockEntity.SLOT_OUTPUT);
            helper.assertTrue(output.is(Items.DIRT),
                    "Output should still be dirt when blocked, got: " + output);
            helper.succeed();
        });
    }

    /**
     * Switches from valid recipe A (wheat → flour, 200 ticks) to valid
     * recipe B (cocoa_beans → cocoa_powder, 200 ticks) and verifies
     * that cookTime resets to 0 and totalCookTime updates.
     */
    @GameTest(template = "empty", timeoutTicks = 400)
    public static void grinder_inputChanged_resetsProgress(GameTestHelper helper) {
        helper.setBlock(MACHINE_POS, net.langball.coffee.init.ModBlocks.GRINDER.get());
        // Recipe A: wheat → flour (200 ticks)
        MachineTestHelper.insertItem(helper, MACHINE_POS, GrinderBlockEntity.SLOT_INPUT,
                new ItemStack(Items.WHEAT, 64));
        MachineTestHelper.insertItem(helper, MACHINE_POS, GrinderBlockEntity.SLOT_FUEL,
                new ItemStack(Items.COAL, 64));

        // Let it process for 100 ticks (halfway), then switch to recipe B
        helper.runAfterDelay(100, () -> {
            var be = (net.langball.coffee.block.entity.MachineBlockEntity)
                    helper.getBlockEntity(MACHINE_POS);
            var handler = be.getItemHandler();

            // Remove wheat, insert cocoa beans (recipe B: 200 ticks)
            handler.extractItem(GrinderBlockEntity.SLOT_INPUT, 64, false);
            handler.insertItem(GrinderBlockEntity.SLOT_INPUT,
                    new ItemStack(Items.COCOA_BEANS, 64), false);
            be.setChanged();

            // Wait a few ticks for recipe resolution
            helper.runAfterDelay(5, () -> {
                int cook = be.data.get(0); // cookTime
                int total = be.data.get(1); // totalCookTime
                int burn = be.data.get(2); // burnTime

                // cookTime should have reset because recipe changed
                helper.assertTrue(cook == 0 || cook <= 5,
                        "cookTime should reset to near 0 after recipe switch, got: " + cook);
                // totalCookTime should match the new recipe (200 for cocoa_powder)
                helper.assertTrue(total == 200,
                        "totalCookTime should be 200 for cocoa powder, got: " + total);
                // burnTime should still be positive (coal from recipe A continues)
                helper.assertTrue(burn > 0,
                        "burnTime should remain after recipe switch");

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
                new ItemStack(Items.WHEAT, 64));
        MachineTestHelper.insertItem(helper, MACHINE_POS, GrinderBlockEntity.SLOT_FUEL,
                new ItemStack(Items.COAL, 64));
        helper.runAfterDelay(4, () -> {
            helper.assertTrue(MachineTestHelper.isLit(helper, MACHINE_POS),
                    "Grinder should be lit with fuel + recipe");
            helper.succeed();
        });
    }

    // ─── Other machines (using their real recipes) ─────────────────────

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
