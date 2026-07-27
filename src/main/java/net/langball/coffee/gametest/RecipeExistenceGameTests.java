package net.langball.coffee.gametest;

import net.langball.coffee.CoffeeWork;
import net.langball.coffee.init.ModItems;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.ShapelessRecipe;
import net.minecraft.world.item.crafting.SmeltingRecipe;
import net.minecraftforge.gametest.GameTestHolder;
import net.minecraftforge.gametest.PrefixGameTestTemplate;

/**
 * Recipe existence and structural GameTests.
 *
 * <p>Verifies recipe IDs exist/do-not-exist AND validates ingredient structure
 * (Mixing Bowl, Egg Batter, output counts) for correctness beyond simple ID checks.
 */
@GameTestHolder(CoffeeWork.MODID)
@PrefixGameTestTemplate(false)
public class RecipeExistenceGameTests {

    private static ResourceLocation rl(String path) {
        return new ResourceLocation(CoffeeWork.MODID, path);
    }

    private static boolean recipeExists(GameTestHelper helper, String path) {
        return helper.getLevel().getRecipeManager().byKey(rl(path)).isPresent();
    }

    private static Recipe<?> requireRecipe(GameTestHelper helper, String path) {
        return helper.getLevel().getRecipeManager().byKey(rl(path))
                .orElseThrow(() -> new AssertionError("Recipe " + rl(path) + " must exist"));
    }

    // ---- Structural assertion helpers ----

    /** Assert any ingredient in the recipe matches the given item. */
    private static void assertIngredient(Recipe<?> recipe, Item item, int expectedCount,
                                          GameTestHelper helper, String label) {
        boolean found = false;
        for (Ingredient ing : recipe.getIngredients()) {
            if (ing.test(new ItemStack(item))) {
                found = true;
                break;
            }
        }
        helper.assertTrue(found, label + " must contain " + item + " as ingredient");
    }

    /** Assert the recipe output item matches and count matches. */
    private static void assertResultItem(Recipe<?> recipe, Item expectedItem, int expectedCount,
                                          GameTestHelper helper, String label) {
        ItemStack result = recipe.getResultItem(helper.getLevel().registryAccess());
        helper.assertTrue(result.is(expectedItem),
                label + " result must be " + expectedItem + ", got: " + result.getItem());
        helper.assertTrue(result.getCount() == expectedCount,
                label + " result count must be " + expectedCount + ", got: " + result.getCount());
    }

    // ========================================================================
    // Forbidden shortcuts — must NOT exist
    // ========================================================================

    @GameTest(template = "empty")
    public static void cheesecakeDirectShortcut_doesNotExist(GameTestHelper helper) {
        helper.assertTrue(!recipeExists(helper, "cake_cheese"),
                "Direct cheesecake shortcut (cake_cheese) must not exist — use Raw→Oven→Model chain");
        helper.succeed();
    }

    @GameTest(template = "empty")
    public static void icecreamVanillaFlavorShortcut_doesNotExist(GameTestHelper helper) {
        helper.assertTrue(!recipeExists(helper, "icecream_apple"),
                "icecream_apple shortcut must not exist — use Cream→Icecream Machine path");
        helper.assertTrue(!recipeExists(helper, "icecream_berry"),
                "icecream_berry shortcut must not exist");
        helper.assertTrue(!recipeExists(helper, "icecream_chocolate"),
                "icecream_chocolate shortcut must not exist");
        helper.assertTrue(!recipeExists(helper, "icecream_coffee"),
                "icecream_coffee shortcut must not exist");
        helper.assertTrue(!recipeExists(helper, "icecream_lemon"),
                "icecream_lemon shortcut must not exist");
        helper.assertTrue(!recipeExists(helper, "icecream_melon"),
                "icecream_melon shortcut must not exist");
        helper.succeed();
    }

    @GameTest(template = "empty")
    public static void rollerCakeRollShortcut_doesNotExist(GameTestHelper helper) {
        helper.assertTrue(!recipeExists(helper, "rolling/cake_roll"),
                "Roller cake_roll shortcut must not exist — use Base + Cream → Roll path");
        helper.assertTrue(!recipeExists(helper, "rolling/cake_berry_roll"),
                "Roller cake_berry_roll shortcut must not exist");
        helper.assertTrue(!recipeExists(helper, "rolling/cake_chocolate_roll"),
                "Roller cake_chocolate_roll shortcut must not exist");
        helper.succeed();
    }

    @GameTest(template = "empty")
    public static void creamMilkMachineShortcut_doesNotExist(GameTestHelper helper) {
        helper.assertTrue(!recipeExists(helper, "icecream_making/cream_milk"),
                "icecream_making/cream_milk must not exist — use Mixing Bowl + Milk + Vanilla");
        helper.succeed();
    }

    @GameTest(template = "empty")
    public static void jiggyDirectShortcut_doesNotExist(GameTestHelper helper) {
        // The old direct jiggy shortcuts (gelatin+sugar+milk and jiggy+flavor) are removed
        // Verify the removed recipe names don't resolve to present recipes
        helper.assertTrue(!recipeExists(helper, "jiggy_cake"),
                "Direct jiggy_cake shortcut must not exist — use Raw→Oven→Model chain");
        helper.assertTrue(!recipeExists(helper, "jiggy_cake_berry"),
                "Direct jiggy_cake_berry shortcut must not exist");
        helper.assertTrue(!recipeExists(helper, "jiggy_cake_carrot"),
                "Direct jiggy_cake_carrot shortcut must not exist");
        helper.succeed();
    }

    // ========================================================================
    // Required recipes — MUST exist
    // ========================================================================

    @GameTest(template = "empty")
    public static void cheesecakeModelToBlock_exists(GameTestHelper helper) {
        helper.assertTrue(recipeExists(helper, "cake_cheese_model_to_block"),
                "Cheesecake model→block recipe must exist");
        helper.succeed();
    }

    @GameTest(template = "empty")
    public static void cakeSlicesBidirectional_exists(GameTestHelper helper) {
        // Generic cake_slices ↔ vanilla minecraft:cake
        helper.assertTrue(recipeExists(helper, "cake_vanilla_to_slices"),
                "cake_vanilla_to_slices must exist");
        helper.assertTrue(recipeExists(helper, "cake_vanilla_slices_to_cake"),
                "cake_vanilla_slices_to_cake must exist");
        // Sample: sponge cake → slices (plain)
        helper.assertTrue(recipeExists(helper, "cake_sponge_to_slices"),
                "cake_sponge_to_slices must exist");
        helper.assertTrue(recipeExists(helper, "cake_sponge_slices_to_cake"),
                "cake_sponge_slices_to_cake must exist");
        // Sample: large cake → slices
        helper.assertTrue(recipeExists(helper, "cake_coffee_to_slices"),
                "cake_coffee_to_slices must exist");
        helper.assertTrue(recipeExists(helper, "cake_coffee_slices_to_cake"),
                "cake_coffee_slices_to_cake must exist");
        helper.assertTrue(recipeExists(helper, "cake_cheese_to_slices"),
                "cake_cheese_to_slices must exist");
        helper.assertTrue(recipeExists(helper, "cake_cheese_slices_to_cake"),
                "cake_cheese_slices_to_cake must exist");
        helper.succeed();
    }

    @GameTest(template = "empty")
    public static void cakeCarrotSliceBidirectional_doesNotExist(GameTestHelper helper) {
        // Cake Carrot has no generic slice mapping (prevents cross-type conversion)
        helper.assertTrue(!recipeExists(helper, "cake_carrot_to_slices"),
                "cake_carrot_to_slices must not exist");
        helper.assertTrue(!recipeExists(helper, "cake_carrot_slices_to_cake"),
                "cake_carrot_slices_to_cake must not exist");
        helper.succeed();
    }

    @GameTest(template = "empty")
    public static void jiggyFullChain_exists(GameTestHelper helper) {
        // Raw recipes (sample: berry, chocolate)
        helper.assertTrue(recipeExists(helper, "jiggy_cake_raw"),
                "jiggy_cake_raw must exist");
        helper.assertTrue(recipeExists(helper, "jiggy_cake_berry_raw"),
                "jiggy_cake_berry_raw must exist");
        // Model → finished (should output 2)
        Recipe<?> jiggyFromModel = requireRecipe(helper, "jiggy_cake_from_model");
        assertResultItem(jiggyFromModel, ModItems.JIGGY_CAKE.get(), 2, helper,
                "jiggy_cake_from_model");
        Recipe<?> jiggyBerryFromModel = requireRecipe(helper, "jiggy_cake_berry_from_model");
        assertResultItem(jiggyBerryFromModel, ModItems.JIGGY_CAKE_BERRY.get(), 2, helper,
                "jiggy_cake_berry_from_model");
        helper.succeed();
    }

    @GameTest(template = "empty")
    public static void brownieChain_exists(GameTestHelper helper) {
        helper.assertTrue(recipeExists(helper, "brownie_raw"),
                "brownie_raw recipe must exist");
        helper.assertTrue(recipeExists(helper, "brownie_from_model"),
                "brownie_from_model recipe must exist");
        // Model → 4× brownie
        Recipe<?> brownieFromModel = requireRecipe(helper, "brownie_from_model");
        assertResultItem(brownieFromModel, ModItems.BROWNIE.get(), 4, helper,
                "brownie_from_model");
        // Old direct shortcut must be gone
        helper.assertTrue(!recipeExists(helper, "brownie"),
                "Old direct brownie shortcut must not exist");
        helper.succeed();
    }

    @GameTest(template = "empty")
    public static void tiramisuChain_exists(GameTestHelper helper) {
        helper.assertTrue(recipeExists(helper, "tiramisu_raw"),
                "tiramisu_raw must exist");
        helper.assertTrue(recipeExists(helper, "tiramisu_model_to_block"),
                "tiramisu_model_to_block must exist");
        // Verify tiramisu_raw uses CAKE_SPONGE_BASE (not CAKE_SPONGE block)
        Recipe<?> tira = requireRecipe(helper, "tiramisu_raw");
        assertIngredient(tira, ModItems.CAKE_SPONGE_BASE.get(), 1, helper,
                "tiramisu_raw must use CAKE_SPONGE_BASE");
        helper.succeed();
    }

    @GameTest(template = "empty")
    public static void mousseChain_exists(GameTestHelper helper) {
        helper.assertTrue(recipeExists(helper, "mousse_berry_raw"),
                "mousse_berry_raw must exist");
        helper.assertTrue(recipeExists(helper, "mousse_chocolate_raw"),
                "mousse_chocolate_raw must exist");
        helper.assertTrue(recipeExists(helper, "mousse_coffee_raw"),
                "mousse_coffee_raw must exist");
        helper.assertTrue(recipeExists(helper, "mousse_lemon_raw"),
                "mousse_lemon_raw must exist");
        // Model → block
        helper.assertTrue(recipeExists(helper, "mousse_berry_model_to_block"),
                "mousse_berry_model_to_block must exist");
        helper.assertTrue(recipeExists(helper, "mousse_chocolate_model_to_block"),
                "mousse_chocolate_model_to_block must exist");
        helper.succeed();
    }

    @GameTest(template = "empty")
    public static void puffRecipe_usesEggBatter(GameTestHelper helper) {
        helper.assertTrue(recipeExists(helper, "puff_raw"),
                "puff_raw recipe must exist");
        Recipe<?> recipe = requireRecipe(helper, "puff_raw");
        assertIngredient(recipe, ModItems.IRON_BOWL_EGG.get(), 1, helper,
                "puff_raw");
        helper.succeed();
    }

    // ========================================================================
    // Furnace dual-channel — smelting recipes for bakery raw items
    // ========================================================================

    @GameTest(template = "empty")
    public static void furnaceBakerySmelting_exists(GameTestHelper helper) {
        // Sample: sponge models (raw → model via furnace)
        helper.assertTrue(recipeExists(helper, "smelting/cake_sponge"),
                "Furnace smelting/cake_sponge must exist");
        helper.assertTrue(recipeExists(helper, "smelting/cake_sponge_berry"),
                "Furnace smelting/cake_sponge_berry must exist");
        // Sample: jiggy
        helper.assertTrue(recipeExists(helper, "smelting/jiggy"),
                "Furnace smelting/jiggy must exist");
        helper.assertTrue(recipeExists(helper, "smelting/jiggy_berry"),
                "Furnace smelting/jiggy_berry must exist");
        helper.assertTrue(recipeExists(helper, "smelting/brownie"),
                "Furnace smelting/brownie must exist");
        // Sample: muffins
        helper.assertTrue(recipeExists(helper, "smelting/muffin"),
                "Furnace smelting/muffin must exist");
        helper.assertTrue(recipeExists(helper, "smelting/muffin_berry"),
                "Furnace smelting/muffin_berry must exist");
        // Sample: mooncake
        helper.assertTrue(recipeExists(helper, "smelting/mooncake"),
                "Furnace smelting/mooncake must exist");
        // Sample: souffle
        helper.assertTrue(recipeExists(helper, "smelting/souffle"),
                "Furnace smelting/souffle must exist");
        // Sample: croissant, puff, ginger bread
        helper.assertTrue(recipeExists(helper, "smelting/croissant"),
                "Furnace smelting/croissant must exist");
        helper.assertTrue(recipeExists(helper, "smelting/puff"),
                "Furnace smelting/puff must exist");
        helper.assertTrue(recipeExists(helper, "smelting/ginger_bread_man"),
                "Furnace smelting/ginger_bread_man must exist");
        helper.succeed();
    }

    @GameTest(template = "empty")
    public static void furnaceBakerySmelting_plateCakesExist(GameTestHelper helper) {
        helper.assertTrue(recipeExists(helper, "smelting/cake_sponge_plate"),
                "Furnace smelting/cake_sponge_plate must exist");
        helper.assertTrue(recipeExists(helper, "smelting/cake_sponge_berry_plate"),
                "Furnace smelting/cake_sponge_berry_plate must exist");
        helper.assertTrue(recipeExists(helper, "smelting/cake_sponge_chocolate_plate"),
                "Furnace smelting/cake_sponge_chocolate_plate must exist");
        helper.succeed();
    }

    // ========================================================================
    // Cream recipes — Mixing Bowl required, machine shortcut absent
    // ========================================================================

    @GameTest(template = "empty")
    public static void flavoredCreamRecipes_exist(GameTestHelper helper) {
        helper.assertTrue(recipeExists(helper, "cream_apple"),
                "cream_apple must exist");
        helper.assertTrue(recipeExists(helper, "cream_berry"),
                "cream_berry must exist");
        // Verify Mixing Bowl is an ingredient in flavored cream
        Recipe<?> creamApple = requireRecipe(helper, "cream_apple");
        assertIngredient(creamApple, ModItems.MIXING_BOWL.get(), 1, helper,
                "cream_apple");
        Recipe<?> creamChoc = requireRecipe(helper, "cream_chocolate");
        assertIngredient(creamChoc, ModItems.MIXING_BOWL.get(), 1, helper,
                "cream_chocolate");
        helper.succeed();
    }

    @GameTest(template = "empty")
    public static void creamMilkBowlRecipe_exists(GameTestHelper helper) {
        helper.assertTrue(recipeExists(helper, "cream_milk_bowl"),
                "cream_milk_bowl (Mixing Bowl + Milk + Vanilla) must exist");
        helper.succeed();
    }

    // ========================================================================
    // Cake roll — Base + Cream path exists, Roller path absent
    // ========================================================================

    @GameTest(template = "empty")
    public static void cakeRollFromBase_exists(GameTestHelper helper) {
        helper.assertTrue(recipeExists(helper, "cake_roll_from_base"),
                "cake_roll_from_base must exist (Base + Cream Milk → Roll)");
        helper.assertTrue(recipeExists(helper, "cake_berry_roll_from_base"),
                "cake_berry_roll_from_base must exist");
        helper.succeed();
    }
}
