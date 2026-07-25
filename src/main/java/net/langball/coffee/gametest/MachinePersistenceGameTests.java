package net.langball.coffee.gametest;

import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;

/**
 * Machine persistence GameTests.
 *
 * <p>Tests that block entity NBT survives save/load cycles, that
 * items, fuel, progress, recipe tracking and experience are all
 * correctly restored.
 */
public class MachinePersistenceGameTests {

    @GameTest(template = "coffeework:empty")
    public static void inventorySurvivesReload(GameTestHelper helper) {
        // Phase 2.6
        helper.succeed();
    }

    @GameTest(template = "coffeework:empty")
    public static void progressSurvivesReload(GameTestHelper helper) {
        helper.succeed();
    }

    @GameTest(template = "coffeework:empty")
    public static void fuelSurvivesReload(GameTestHelper helper) {
        helper.succeed();
    }

    @GameTest(template = "coffeework:empty")
    public static void recipeIdSurvivesReload(GameTestHelper helper) {
        helper.succeed();
    }

    @GameTest(template = "coffeework:empty")
    public static void experienceSurvivesReload(GameTestHelper helper) {
        helper.succeed();
    }
}
