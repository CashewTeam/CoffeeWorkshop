package net.langball.coffee.gametest;

import net.langball.coffee.CoffeeWork;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraftforge.gametest.GameTestHolder;
import net.minecraftforge.gametest.PrefixGameTestTemplate;

/**
 * Sided capability GameTests.
 */
@GameTestHolder(CoffeeWork.MODID)
@PrefixGameTestTemplate(false)
public class MachineCapabilityGameTests {

    @GameTest(template = "empty")
    public static void fueledMachine_upOnlyAcceptsInput(GameTestHelper helper) {
        helper.succeed();
    }

    @GameTest(template = "empty")
    public static void fueledMachine_sideOnlyAcceptsFuel(GameTestHelper helper) {
        helper.succeed();
    }

    @GameTest(template = "empty")
    public static void fueledMachine_downOnlyExtractsOutput(GameTestHelper helper) {
        helper.succeed();
    }

    @GameTest(template = "empty")
    public static void fueledMachine_cannotInsertIntoOutput(GameTestHelper helper) {
        helper.succeed();
    }

    @GameTest(template = "empty")
    public static void fueledMachine_cannotExtractFromInput(GameTestHelper helper) {
        helper.succeed();
    }

    @GameTest(template = "empty")
    public static void coffeeMachine_noFuelExposure(GameTestHelper helper) {
        helper.succeed();
    }
}
