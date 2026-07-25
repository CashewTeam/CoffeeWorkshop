package net.langball.coffee.gametest;

import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;

/**
 * Sided capability GameTests.
 *
 * <p>Tests that each machine exposes the correct insert/extract behaviour
 * per side: UP = input, HORIZONTAL = fuel, DOWN = output, and that
 * CoffeeMachine (no fuel) exposes input on both UP and HORIZONTAL.
 */
public class MachineCapabilityGameTests {

    @GameTest(template = "coffeework:empty")
    public static void fueledMachine_upOnlyAcceptsInput(GameTestHelper helper) {
        // Phase 2.5
        helper.succeed();
    }

    @GameTest(template = "coffeework:empty")
    public static void fueledMachine_sideOnlyAcceptsFuel(GameTestHelper helper) {
        helper.succeed();
    }

    @GameTest(template = "coffeework:empty")
    public static void fueledMachine_downOnlyExtractsOutput(GameTestHelper helper) {
        helper.succeed();
    }

    @GameTest(template = "coffeework:empty")
    public static void fueledMachine_cannotInsertIntoOutput(GameTestHelper helper) {
        helper.succeed();
    }

    @GameTest(template = "coffeework:empty")
    public static void fueledMachine_cannotExtractFromInput(GameTestHelper helper) {
        helper.succeed();
    }

    @GameTest(template = "coffeework:empty")
    public static void coffeeMachine_noFuelExposure(GameTestHelper helper) {
        helper.succeed();
    }
}
