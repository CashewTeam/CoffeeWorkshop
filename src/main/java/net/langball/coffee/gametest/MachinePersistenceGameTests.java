package net.langball.coffee.gametest;

import net.langball.coffee.CoffeeWork;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraftforge.gametest.GameTestHolder;
import net.minecraftforge.gametest.PrefixGameTestTemplate;

/**
 * Machine persistence GameTests.
 */
@GameTestHolder(CoffeeWork.MODID)
@PrefixGameTestTemplate(false)
public class MachinePersistenceGameTests {

    @GameTest(template = "empty")
    public static void inventorySurvivesReload(GameTestHelper helper) {
        helper.succeed();
    }

    @GameTest(template = "empty")
    public static void progressSurvivesReload(GameTestHelper helper) {
        helper.succeed();
    }

    @GameTest(template = "empty")
    public static void fuelSurvivesReload(GameTestHelper helper) {
        helper.succeed();
    }

    @GameTest(template = "empty")
    public static void recipeIdSurvivesReload(GameTestHelper helper) {
        helper.succeed();
    }

    @GameTest(template = "empty")
    public static void experienceSurvivesReload(GameTestHelper helper) {
        helper.succeed();
    }
}
