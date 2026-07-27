package net.langball.coffee.gametest;

import net.langball.coffee.CoffeeWork;
import net.langball.coffee.init.ModItems;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.gametest.GameTestHolder;
import net.minecraftforge.gametest.PrefixGameTestTemplate;

/**
 * Container lifecycle GameTests: mold conservation, crafting remainders, and food container returns.
 *
 * <p>Verifies the 1.12.2 container-item patterns:
 * <ul>
 *   <li>Iron Bowl is plain (no self-remainder) — BatterItem returns the bowl</li>
 *   <li>Molds consumed at Raw stage, returned at Model→Finished stage</li>
 *   <li>Mooncake Mold returns itself at Raw stage (no Model intermediate)</li>
 *   <li>Soufflé returns Small Mold when eaten (ItemFoodContain pattern)</li>
 * </ul>
 */
@GameTestHolder(CoffeeWork.MODID)
@PrefixGameTestTemplate(false)
public class ContainerGameTests {

    // ---- Inventory helpers ----

    private static int countItems(net.minecraft.world.entity.player.Player player, Item item) {
        int count = 0;
        Inventory inv = player.getInventory();
        for (int i = 0; i < inv.getContainerSize(); i++) {
            ItemStack s = inv.getItem(i);
            if (s.is(item)) count += s.getCount();
        }
        return count;
    }

    /** Give the player one stack, clearing the inventory slot first. */
    private static void giveItem(net.minecraft.world.entity.player.Player player, ItemStack stack) {
        player.getInventory().add(stack);
    }

    // ========================================================================
    // Iron Bowl — plain item, no self-remainder
    // ========================================================================

    @GameTest(template = "empty")
    public static void ironBowl_hasNoCraftingRemainder(GameTestHelper helper) {
        ItemStack bowl = new ItemStack(ModItems.IRON_BOWL.get());
        helper.assertTrue(!bowl.getItem().hasCraftingRemainingItem(),
                "IRON_BOWL must not have crafting remainder (causes infinite duplication)");
        helper.succeed();
    }

    @GameTest(template = "empty")
    public static void ironBowlBatter_returnsEmptyBowl(GameTestHelper helper) {
        ItemStack batter = new ItemStack(ModItems.IRON_BOWL_BATTER.get());
        helper.assertTrue(batter.getItem().hasCraftingRemainingItem(),
                "Iron Bowl Batter must have crafting remainder");
        ItemStack remainder = batter.getItem().getCraftingRemainingItem(batter);
        helper.assertTrue(remainder.is(ModItems.IRON_BOWL.get()),
                "Batter remainder must be empty IRON_BOWL, got: " + remainder);
        helper.assertTrue(remainder.getCount() == 1,
                "Batter remainder count must be 1, got: " + remainder.getCount());
        helper.succeed();
    }

    @GameTest(template = "empty")
    public static void ironBowlEgg_returnsEmptyBowl(GameTestHelper helper) {
        ItemStack batter = new ItemStack(ModItems.IRON_BOWL_EGG.get());
        helper.assertTrue(batter.getItem().hasCraftingRemainingItem(),
                "Egg Batter must have crafting remainder");
        ItemStack remainder = batter.getItem().getCraftingRemainingItem(batter);
        helper.assertTrue(remainder.is(ModItems.IRON_BOWL.get()),
                "Egg Batter remainder must be empty IRON_BOWL");
        helper.succeed();
    }

    // ========================================================================
    // Cake Mold lifecycle — consumed at Raw, returned at Model→Finished
    // ========================================================================

    @GameTest(template = "empty")
    public static void cakeModel_isPlain_noSelfRemainder(GameTestHelper helper) {
        ItemStack mold = new ItemStack(ModItems.CAKE_MODEL.get());
        helper.assertTrue(!mold.getItem().hasCraftingRemainingItem(),
                "CAKE_MODEL must be plain (consumed at Raw, returned by Model)");
        helper.succeed();
    }

    @GameTest(template = "empty")
    public static void cakeModelSquare_isPlain_noSelfRemainder(GameTestHelper helper) {
        ItemStack mold = new ItemStack(ModItems.CAKE_MODEL_SQUARE.get());
        helper.assertTrue(!mold.getItem().hasCraftingRemainingItem(),
                "CAKE_MODEL_SQUARE must be plain (consumed at Raw, returned by Model)");
        helper.succeed();
    }

    @GameTest(template = "empty")
    public static void cakeModelPlate_isPlain_noSelfRemainder(GameTestHelper helper) {
        ItemStack mold = new ItemStack(ModItems.CAKE_MODEL_PLATE.get());
        helper.assertTrue(!mold.getItem().hasCraftingRemainingItem(),
                "CAKE_MODEL_PLATE must be plain (consumed at Raw, returned by Model)");
        helper.succeed();
    }

    @GameTest(template = "empty")
    public static void smallModel_isPlain_noSelfRemainder(GameTestHelper helper) {
        ItemStack mold = new ItemStack(ModItems.SMALL_MODEL.get());
        helper.assertTrue(!mold.getItem().hasCraftingRemainingItem(),
                "SMALL_MODEL must be plain (consumed at Raw, returned on eat)");
        helper.succeed();
    }

    @GameTest(template = "empty")
    public static void spongeModel_returnsCakeModelMold(GameTestHelper helper) {
        ItemStack model = new ItemStack(ModItems.CAKE_SPONGE_MODEL.get());
        helper.assertTrue(model.getItem().hasCraftingRemainingItem(),
                "Sponge Model must return mold");
        ItemStack remainder = model.getItem().getCraftingRemainingItem(model);
        helper.assertTrue(remainder.is(ModItems.CAKE_MODEL.get()),
                "Sponge Model remainder must be CAKE_MODEL, got: " + remainder);
        helper.succeed();
    }

    @GameTest(template = "empty")
    public static void cheeseModel_returnsCakeModelMold(GameTestHelper helper) {
        ItemStack model = new ItemStack(ModItems.CAKE_CHEESE_MODEL.get());
        helper.assertTrue(model.getItem().hasCraftingRemainingItem(),
                "Cheese Model must return mold");
        ItemStack remainder = model.getItem().getCraftingRemainingItem(model);
        helper.assertTrue(remainder.is(ModItems.CAKE_MODEL.get()),
                "Cheese Model remainder must be CAKE_MODEL");
        helper.succeed();
    }

    @GameTest(template = "empty")
    public static void plateModel_returnsCakeModelPlateMold(GameTestHelper helper) {
        ItemStack model = new ItemStack(ModItems.CAKE_SPONGE_PLATE_MODEL.get());
        helper.assertTrue(model.getItem().hasCraftingRemainingItem(),
                "Plate Model must return mold");
        ItemStack remainder = model.getItem().getCraftingRemainingItem(model);
        helper.assertTrue(remainder.is(ModItems.CAKE_MODEL_PLATE.get()),
                "Plate Model remainder must be CAKE_MODEL_PLATE, got: " + remainder);
        helper.succeed();
    }

    @GameTest(template = "empty")
    public static void jiggyModel_returnsSquareMold(GameTestHelper helper) {
        ItemStack model = new ItemStack(ModItems.JIGGY_CAKE_MODEL.get());
        helper.assertTrue(model.getItem().hasCraftingRemainingItem(),
                "Jiggy Model must return mold");
        ItemStack remainder = model.getItem().getCraftingRemainingItem(model);
        helper.assertTrue(remainder.is(ModItems.CAKE_MODEL_SQUARE.get()),
                "Jiggy Model remainder must be CAKE_MODEL_SQUARE, got: " + remainder);
        helper.succeed();
    }

    @GameTest(template = "empty")
    public static void jiggyFlavoredModel_returnsSquareMold(GameTestHelper helper) {
        ItemStack model = new ItemStack(ModItems.JIGGY_CAKE_BERRY_MODEL.get());
        helper.assertTrue(model.getItem().hasCraftingRemainingItem(),
                "Flavored Jiggy Model must return mold");
        ItemStack remainder = model.getItem().getCraftingRemainingItem(model);
        helper.assertTrue(remainder.is(ModItems.CAKE_MODEL_SQUARE.get()),
                "Flavored Jiggy Model remainder must be CAKE_MODEL_SQUARE");
        helper.succeed();
    }

    @GameTest(template = "empty")
    public static void mousseModel_returnsCakeModelMold(GameTestHelper helper) {
        ItemStack model = new ItemStack(ModItems.MOUSSE_BERRY_MODEL.get());
        helper.assertTrue(model.getItem().hasCraftingRemainingItem(),
                "Mousse Model must return mold");
        ItemStack remainder = model.getItem().getCraftingRemainingItem(model);
        helper.assertTrue(remainder.is(ModItems.CAKE_MODEL.get()),
                "Mousse Model remainder must be CAKE_MODEL, got: " + remainder);
        helper.succeed();
    }

    @GameTest(template = "empty")
    public static void tiramisuModel_returnsSquareMold(GameTestHelper helper) {
        ItemStack model = new ItemStack(ModItems.TIRAMISU_MODEL.get());
        helper.assertTrue(model.getItem().hasCraftingRemainingItem(),
                "Tiramisu Model must return mold");
        ItemStack remainder = model.getItem().getCraftingRemainingItem(model);
        helper.assertTrue(remainder.is(ModItems.CAKE_MODEL_SQUARE.get()),
                "Tiramisu Model remainder must be CAKE_MODEL_SQUARE, got: " + remainder);
        helper.succeed();
    }

    @GameTest(template = "empty")
    public static void brownieModel_returnsSquareMold(GameTestHelper helper) {
        ItemStack model = new ItemStack(ModItems.BROWNIE_MODEL.get());
        helper.assertTrue(model.getItem().hasCraftingRemainingItem(),
                "Brownie Model must return mold");
        ItemStack remainder = model.getItem().getCraftingRemainingItem(model);
        helper.assertTrue(remainder.is(ModItems.CAKE_MODEL_SQUARE.get()),
                "Brownie Model remainder must be CAKE_MODEL_SQUARE, got: " + remainder);
        helper.succeed();
    }

    // ========================================================================
    // Mooncake Mold — returns itself (no Model stage, return at Raw)
    // ========================================================================

    @GameTest(template = "empty")
    public static void mooncakeModel_hasCraftingRemainder_returnsItself(GameTestHelper helper) {
        ItemStack mold = new ItemStack(ModItems.MOONCAKE_MODEL.get());
        helper.assertTrue(mold.getItem().hasCraftingRemainingItem(),
                "MOONCAKE_MODEL must have crafting remainder (returned at Raw stage)");
        ItemStack remainder = mold.getItem().getCraftingRemainingItem(mold);
        helper.assertTrue(remainder.is(ModItems.MOONCAKE_MODEL.get()),
                "MOONCAKE_MODEL remainder must be itself, got: " + remainder);
        helper.assertTrue(remainder.getCount() == 1,
                "MOONCAKE_MODEL remainder count must be 1, got: " + remainder.getCount());
        helper.succeed();
    }

    // ========================================================================
    // Soufflé eat — returns Small Mold (ItemFoodContain pattern)
    // ========================================================================

    @GameTest(template = "empty")
    public static void souffleFinishUsing_returnsSmallMold(GameTestHelper helper) {
        var player = helper.makeMockPlayer();
        // Clear inventory for clean assertion
        player.getInventory().clearContent();

        // Give player 1 souffle and set food to 0 so eating is allowed
        ItemStack souffle = new ItemStack(ModItems.SOUFFLE.get());
        player.getInventory().add(souffle);
        player.getFoodData().setFoodLevel(10);

        int moldBefore = countItems(player, ModItems.SMALL_MODEL.get());
        int souffleBefore = countItems(player, ModItems.SOUFFLE.get());
        helper.assertTrue(souffleBefore == 1,
                "Should have 1 souffle before eating, got: " + souffleBefore);
        helper.assertTrue(moldBefore == 0,
                "Should have 0 small models before eating, got: " + moldBefore);

        // Simulate finishing eating the souffle from the inventory
        // Find the souffle stack and call finishUsingItem
        for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
            ItemStack s = player.getInventory().getItem(i);
            if (s.is(ModItems.SOUFFLE.get())) {
                s.finishUsingItem(helper.getLevel(), player);
                break;
            }
        }

        int moldAfter = countItems(player, ModItems.SMALL_MODEL.get());
        helper.assertTrue(moldAfter == 1,
                "After eating souffle should have 1 small model, got: " + moldAfter);

        helper.succeed();
    }

    @GameTest(template = "empty")
    public static void souffleChocolateFinishUsing_returnsSmallMold(GameTestHelper helper) {
        var player = helper.makeMockPlayer();
        player.getInventory().clearContent();

        ItemStack souffle = new ItemStack(ModItems.SOUFFLE_CHOCOLATE.get());
        player.getInventory().add(souffle);
        player.getFoodData().setFoodLevel(10);

        for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
            ItemStack s = player.getInventory().getItem(i);
            if (s.is(ModItems.SOUFFLE_CHOCOLATE.get())) {
                s.finishUsingItem(helper.getLevel(), player);
                break;
            }
        }

        int moldAfter = countItems(player, ModItems.SMALL_MODEL.get());
        helper.assertTrue(moldAfter == 1,
                "After eating chocolate souffle should have 1 small model, got: " + moldAfter);

        helper.succeed();
    }

    // ========================================================================
    // Model items are not edible
    // ========================================================================

    @GameTest(template = "empty")
    public static void spongeModel_isNotEdible(GameTestHelper helper) {
        ItemStack model = new ItemStack(ModItems.CAKE_SPONGE_MODEL.get());
        helper.assertTrue(!model.isEdible(),
                "Sponge Model must NOT be edible (intermediate item)");
        helper.succeed();
    }

    @GameTest(template = "empty")
    public static void jiggyModel_isNotEdible(GameTestHelper helper) {
        ItemStack model = new ItemStack(ModItems.JIGGY_CAKE_MODEL.get());
        helper.assertTrue(!model.isEdible(),
                "Jiggy Model must NOT be edible");
        helper.succeed();
    }

    @GameTest(template = "empty")
    public static void tiramisuModel_isNotEdible(GameTestHelper helper) {
        ItemStack model = new ItemStack(ModItems.TIRAMISU_MODEL.get());
        helper.assertTrue(!model.isEdible(),
                "Tiramisu Model must NOT be edible");
        helper.succeed();
    }

    @GameTest(template = "empty")
    public static void mousseModel_isNotEdible(GameTestHelper helper) {
        ItemStack model = new ItemStack(ModItems.MOUSSE_CHOCOLATE_MODEL.get());
        helper.assertTrue(!model.isEdible(),
                "Mousse Model must NOT be edible");
        helper.succeed();
    }

    // ========================================================================
    // Mixing Bowl — reusable tool (crafting remainder returns itself)
    // ========================================================================

    @GameTest(template = "empty")
    public static void mixingBowl_hasCraftingRemainder_returnsItself(GameTestHelper helper) {
        ItemStack bowl = new ItemStack(ModItems.MIXING_BOWL.get());
        helper.assertTrue(bowl.getItem().hasCraftingRemainingItem(),
                "MIXING_BOWL must have crafting remainder (reusable tool)");
        ItemStack remainder = bowl.getItem().getCraftingRemainingItem(bowl);
        helper.assertTrue(remainder.is(ModItems.MIXING_BOWL.get()),
                "MIXING_BOWL remainder must be itself");
        helper.succeed();
    }
}
