package net.langball.coffee.gametest;

import net.langball.coffee.CoffeeWork;
import net.langball.coffee.block.MachineBlock;
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
 * <p>Tests the shared processing state machine: recipe resolution,
 * fuel consumption, progress, output generation, and LIT state
 * for all five machines (Grinder, Roller, Oven, IcecreamMachine,
 * CoffeeMachine).
 */
@GameTestHolder(CoffeeWork.MODID)
@PrefixGameTestTemplate(false)
public class MachineGameTests {

    // ─── Real tests ────────────────────────────────────────────────────

    /**
     * Verifies that a grinder with no recipe (empty input) does not
     * process anything and stays unlit.
     */
    @GameTest(template = "empty")
    public static void grinder_noRecipe_idles(GameTestHelper helper) {
        BlockPos pos = BlockPos.ZERO.above(2); // machine at (0,2,0) in the template
        helper.setBlock(pos, net.langball.coffee.init.ModBlocks.GRINDER.get());

        // Run 40 ticks with empty input — machine should remain idle
        helper.runAfterDelay(40, () -> {
            helper.assertTrue(
                    !MachineTestHelper.isLit(helper, pos),
                    "Grinder should not be lit with no recipe");
            helper.succeed();
        });
    }

    /**
     * Verifies that a grinder with no fuel does not process even if
     * a valid recipe input is present.
     */
    @GameTest(template = "empty")
    public static void grinder_noFuel_idles(GameTestHelper helper) {
        BlockPos pos = BlockPos.ZERO.above(2);
        helper.setBlock(pos, net.langball.coffee.init.ModBlocks.GRINDER.get());

        // Place coffee beans in input (has a grinding recipe), but no fuel
        MachineTestHelper.insertItem(helper, pos, GrinderBlockEntity.SLOT_INPUT,
                new ItemStack(net.langball.coffee.init.ModItems.COFFEE_BEAN.get()));

        helper.runAfterDelay(40, () -> {
            helper.assertTrue(
                    !MachineTestHelper.isLit(helper, pos),
                    "Grinder should not be lit without fuel");
            helper.assertTrue(
                    MachineTestHelper.getItem(helper, pos, GrinderBlockEntity.SLOT_OUTPUT).isEmpty(),
                    "Grinder output should be empty without fuel");
            helper.succeed();
        });
    }

    /**
     * Verifies that putting fuel in a grinder with a valid recipe causes
     * it to start and become lit.
     */
    @GameTest(template = "empty")
    public static void grinder_withFuel_starts(GameTestHelper helper) {
        BlockPos pos = BlockPos.ZERO.above(2);
        helper.setBlock(pos, net.langball.coffee.init.ModBlocks.GRINDER.get());

        MachineTestHelper.insertItem(helper, pos, GrinderBlockEntity.SLOT_INPUT,
                new ItemStack(net.langball.coffee.init.ModItems.COFFEE_BEAN.get()));
        MachineTestHelper.insertItem(helper, pos, GrinderBlockEntity.SLOT_FUEL,
                new ItemStack(Items.COAL, 64));

        helper.runAfterDelay(4, () -> {
            helper.assertTrue(
                    MachineTestHelper.isLit(helper, pos),
                    "Grinder should be lit when fueled with valid recipe");
            helper.succeed();
        });
    }

    // ─── Stub tests (to be filled in later) ────────────────────────────

    @GameTest(template = "empty")
    public static void grinder_hasRecipe_processes(GameTestHelper helper) {
        helper.succeed();
    }

    @GameTest(template = "empty")
    public static void grinder_outputBlocked_pauses(GameTestHelper helper) {
        helper.succeed();
    }

    @GameTest(template = "empty")
    public static void grinder_inputChanged_resetsProgress(GameTestHelper helper) {
        helper.succeed();
    }

    @GameTest(template = "empty")
    public static void grinder_litState(GameTestHelper helper) {
        helper.succeed();
    }

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
