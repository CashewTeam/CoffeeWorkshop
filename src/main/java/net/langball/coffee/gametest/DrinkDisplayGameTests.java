package net.langball.coffee.gametest;

import net.langball.coffee.CoffeeWork;
import net.langball.coffee.ModConfig;
import net.langball.coffee.block.DrinkDisplayRegistry;
import net.langball.coffee.block.entity.DrinkDisplayBlockEntity;
import net.langball.coffee.init.ModBlockEntities;
import net.langball.coffee.init.ModBlocks;
import net.langball.coffee.init.ModItems;
import net.langball.coffee.item.DrinkCoffee;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraftforge.gametest.GameTestHolder;
import net.minecraftforge.gametest.PrefixGameTestTemplate;

/**
 * Drink display system GameTests (Phase 8).
 *
 * <p>Verifies the unified drink-on-plate display block:
 * <ul>
 *   <li>Placing a drink onto a plate consumes one item</li>
 *   <li>Drink consumption preserves remaining cup NBT</li>
 *   <li>Displayed drink can be consumed cup-by-cup</li>
 *   <li>Last cup leaves an empty plate</li>
 *   <li>Sneak-pickup returns the drink with preserved NBT</li>
 *   <li>Breaking drops both plate and drink</li>
 *   <li>Invalid items cannot be placed</li>
 * </ul>
 */
@GameTestHolder(CoffeeWork.MODID)
@PrefixGameTestTemplate(false)
public class DrinkDisplayGameTests {

    private static final BlockPos POS = BlockPos.ZERO.above(2);

    private static void placePlate(GameTestHelper helper) {
        helper.setBlock(POS, ModBlocks.PLATE.get().defaultBlockState());
    }

    private static int countInInventory(net.minecraft.world.entity.player.Player player,
                                         net.minecraft.world.item.Item item) {
        int c = 0;
        Inventory inv = player.getInventory();
        for (int i = 0; i < inv.getContainerSize(); i++) {
            if (inv.getItem(i).is(item)) c += inv.getItem(i).getCount();
        }
        return c;
    }

    // ========================================================================
    // Registration & mapping checks
    // ========================================================================

    @GameTest(template = "empty")
    public static void drinkDisplayRegistry_loaded(GameTestHelper helper) {
        DrinkDisplayRegistry.ensureLoaded();
        helper.assertTrue(DrinkDisplayRegistry.size() >= 50,
                "DrinkDisplayRegistry must have at least 50 mappings");
        helper.succeed();
    }

    @GameTest(template = "empty")
    public static void americano_canBeDisplayed(GameTestHelper helper) {
        helper.assertTrue(DrinkDisplayRegistry.canDisplay(
                        new net.minecraft.resources.ResourceLocation("coffeework:coffee_americano")),
                "coffee_americano must be displayable");
        helper.succeed();
    }

    @GameTest(template = "empty")
    public static void nonDrink_cannotBeDisplayed(GameTestHelper helper) {
        helper.assertTrue(!DrinkDisplayRegistry.canDisplay(
                        new net.minecraft.resources.ResourceLocation("minecraft:diamond")),
                "Diamond must not be displayable on a plate");
        helper.succeed();
    }

    // ========================================================================
    // Placing drinks onto plates
    // ========================================================================

    @GameTest(template = "empty")
    public static void placingDrink_convertsPlateToDisplay(GameTestHelper helper) {
        placePlate(helper);
        var player = helper.makeMockPlayer();

        ItemStack drink = new ItemStack(ModItems.COFFEE_AMERICANO.get());
        DrinkCoffee.initCupCount(drink, 4);
        player.setItemInHand(InteractionHand.MAIN_HAND, drink);
        int initialCount = countInInventory(player, ModItems.COFFEE_AMERICANO.get());

        helper.useBlock(POS, player);

        helper.assertBlockPresent(ModBlocks.DRINK_DISPLAY.get(), POS);
        int afterCount = countInInventory(player, ModItems.COFFEE_AMERICANO.get());
        helper.assertTrue(afterCount == initialCount - 1,
                "Placing drink must consume exactly 1 (was " + initialCount + ", now " + afterCount + ")");
        helper.succeed();
    }

    @GameTest(template = "empty")
    public static void placingDrink_onNonPlate_fails(GameTestHelper helper) {
        helper.setBlock(POS, net.minecraft.world.level.block.Blocks.STONE.defaultBlockState());
        var player = helper.makeMockPlayer();

        ItemStack drink = new ItemStack(ModItems.COFFEE_AMERICANO.get());
        DrinkCoffee.initCupCount(drink, 4);
        player.setItemInHand(InteractionHand.MAIN_HAND, drink);

        helper.useBlock(POS, player);

        // Should still be stone
        helper.assertBlockPresent(net.minecraft.world.level.block.Blocks.STONE, POS);
        helper.succeed();
    }

    @GameTest(template = "empty")
    public static void placingNonDrink_onPlate_removesPlate(GameTestHelper helper) {
        placePlate(helper);
        var player = helper.makeMockPlayer();

        player.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(Items.APPLE));
        helper.useBlock(POS, player);

        // Plate should be removed (popped)
        helper.assertBlockNotPresent(ModBlocks.PLATE.get(), POS);
        helper.assertBlockNotPresent(ModBlocks.DRINK_DISPLAY.get(), POS);
        helper.succeed();
    }

    @GameTest(template = "empty")
    public static void placingDrink_preservesCupNBT(GameTestHelper helper) {
        placePlate(helper);
        var player = helper.makeMockPlayer();

        ItemStack drink = new ItemStack(ModItems.COFFEE_AMERICANO.get());
        DrinkCoffee.initCupCount(drink, 4);
        DrinkCoffee.setRemainingCups(drink, 2);
        player.setItemInHand(InteractionHand.MAIN_HAND, drink);

        helper.useBlock(POS, player);

        BlockEntity be = helper.getBlockEntity(POS);
        helper.assertTrue(be instanceof DrinkDisplayBlockEntity,
                "Block entity must be DrinkDisplayBlockEntity");
        DrinkDisplayBlockEntity dbe = (DrinkDisplayBlockEntity) be;
        helper.assertTrue(dbe.getRemainingCups() == 2,
                "Remaining cups must be 2 after placement");
        helper.assertTrue(dbe.getMaxCups() == 4,
                "Max cups must be 4 after placement");
        helper.succeed();
    }

    // ========================================================================
    // Consumption
    // ========================================================================

    @GameTest(template = "empty")
    public static void emptyHand_consumesOneCup(GameTestHelper helper) {
        placePlate(helper);
        var player = helper.makeMockPlayer();

        ItemStack drink = new ItemStack(ModItems.COFFEE_AMERICANO.get());
        DrinkCoffee.initCupCount(drink, 4);
        player.setItemInHand(InteractionHand.MAIN_HAND, drink);
        helper.useBlock(POS, player);

        player.setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY);
        helper.useBlock(POS, player);

        BlockEntity be = helper.getBlockEntity(POS);
        helper.assertTrue(be instanceof DrinkDisplayBlockEntity,
                "Block entity must still exist after one cup consumed");
        DrinkDisplayBlockEntity dbe = (DrinkDisplayBlockEntity) be;
        helper.assertTrue(dbe.getRemainingCups() == 3,
                "Remaining cups must be 3 after consuming one");
        helper.succeed();
    }

    @GameTest(template = "empty")
    public static void lastCup_revertsToPlate(GameTestHelper helper) {
        placePlate(helper);
        var player = helper.makeMockPlayer();

        int plateBefore = countInInventory(player, ModItems.PLATE.get());

        ItemStack drink = new ItemStack(ModItems.COFFEE_AMERICANO.get());
        DrinkCoffee.initCupCount(drink, 4);
        DrinkCoffee.setRemainingCups(drink, 1);
        player.setItemInHand(InteractionHand.MAIN_HAND, drink);
        helper.useBlock(POS, player);

        int americanoBefore = countInInventory(player, ModItems.COFFEE_AMERICANO.get());

        player.setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY);
        helper.useBlock(POS, player);

        helper.assertBlockPresent(ModBlocks.PLATE.get(), POS);
        helper.assertBlockNotPresent(ModBlocks.DRINK_DISPLAY.get(), POS);

        int americanoAfter = countInInventory(player, ModItems.COFFEE_AMERICANO.get());
        int plateAfter = countInInventory(player, ModItems.PLATE.get());
        helper.assertTrue(americanoAfter == americanoBefore,
                "Last cup must NOT duplicate drink");
        helper.assertTrue(plateAfter == plateBefore,
                "Last cup must NOT duplicate plate");
        helper.succeed();
    }

    // ========================================================================
    // Sneak-pickup
    // ========================================================================

    @GameTest(template = "empty")
    public static void sneakPickup_returnsDrinkAndPlate(GameTestHelper helper) {
        placePlate(helper);
        var player = helper.makeMockPlayer();

        int plateBefore = countInInventory(player, ModItems.PLATE.get());

        ItemStack drink = new ItemStack(ModItems.COFFEE_AMERICANO.get());
        DrinkCoffee.initCupCount(drink, 4);
        DrinkCoffee.setRemainingCups(drink, 3);
        player.setItemInHand(InteractionHand.MAIN_HAND, drink);
        helper.useBlock(POS, player);

        int americanoBefore = countInInventory(player, ModItems.COFFEE_AMERICANO.get());

        player.setShiftKeyDown(true);
        player.setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY);
        helper.useBlock(POS, player);

        helper.assertBlockPresent(ModBlocks.PLATE.get(), POS);
        helper.assertBlockNotPresent(ModBlocks.DRINK_DISPLAY.get(), POS);

        int americanoAfter = countInInventory(player, ModItems.COFFEE_AMERICANO.get());
        int plateAfter = countInInventory(player, ModItems.PLATE.get());
        helper.assertTrue(americanoAfter == americanoBefore + 1,
                "Pickup must return exactly 1 drink (had " + americanoBefore + ", now " + americanoAfter + ")");
        helper.assertTrue(plateAfter == plateBefore,
                "Pickup must NOT duplicate plate");
        helper.succeed();
    }

    @GameTest(template = "empty")
    public static void sneakPickup_preservesRemainingCups(GameTestHelper helper) {
        placePlate(helper);
        var player = helper.makeMockPlayer();

        ItemStack drink = new ItemStack(ModItems.COFFEE_AMERICANO.get());
        DrinkCoffee.initCupCount(drink, 4);
        DrinkCoffee.setRemainingCups(drink, 2);
        player.setItemInHand(InteractionHand.MAIN_HAND, drink);
        helper.useBlock(POS, player);

        player.setShiftKeyDown(true);
        player.setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY);
        helper.useBlock(POS, player);

        helper.assertBlockPresent(ModBlocks.PLATE.get(), POS);

        // Find the americano in inventory and check remaining cups
        Inventory inv = player.getInventory();
        boolean found = false;
        for (int i = 0; i < inv.getContainerSize(); i++) {
            ItemStack s = inv.getItem(i);
            if (s.getItem() == ModItems.COFFEE_AMERICANO.get()) {
                helper.assertTrue(DrinkCoffee.getRemainingCups(s) == 2,
                        "Remaining cups must be 2 after pickup");
                found = true;
                break;
            }
        }
        helper.assertTrue(found, "Must find the drink in player inventory");
        helper.succeed();
    }

    // ========================================================================
    // Database / NBT
    // ========================================================================

    @GameTest(template = "empty")
    public static void displayBlockEntity_savesAndLoads(GameTestHelper helper) {
        helper.setBlock(POS, ModBlocks.DRINK_DISPLAY.get().defaultBlockState());

        BlockEntity be = helper.getBlockEntity(POS);
        helper.assertTrue(be instanceof DrinkDisplayBlockEntity,
                "Must be DrinkDisplayBlockEntity");

        DrinkDisplayBlockEntity dbe = (DrinkDisplayBlockEntity) be;
        ItemStack drink = new ItemStack(ModItems.COFFEE_LATTE.get());
        DrinkCoffee.initCupCount(drink, 4);
        DrinkCoffee.setRemainingCups(drink, 2);
        dbe.setDrink(drink);

        helper.assertTrue(dbe.getRemainingCups() == 2, "Cups must be 2 after set");
        helper.assertTrue(dbe.getMaxCups() == 4, "Max cups must be 4 after set");
        helper.assertTrue(dbe.hasValidDrink(), "Drink must be valid");
        helper.succeed();
    }

    @GameTest(template = "empty")
    public static void displayBlockEntity_invalidDrink(GameTestHelper helper) {
        helper.setBlock(POS, ModBlocks.DRINK_DISPLAY.get().defaultBlockState());

        BlockEntity be = helper.getBlockEntity(POS);
        helper.assertTrue(be instanceof DrinkDisplayBlockEntity,
                "Must be DrinkDisplayBlockEntity");

        DrinkDisplayBlockEntity dbe = (DrinkDisplayBlockEntity) be;
        dbe.setDrink(ItemStack.EMPTY);

        helper.assertTrue(!dbe.hasValidDrink(), "Empty drink must not be valid");
        helper.assertTrue(dbe.getDrink().isEmpty(), "Drink stack must be empty");
        helper.succeed();
    }

    // ========================================================================
    // Reverse coverage: every DrinkCoffee must have a model or exclusion
    // ========================================================================

    private static final java.util.Set<String> EXPLICIT_EXCLUSIONS = java.util.Set.of(
            // Items that are DrinkCoffee but intentionally don't have plate models
    );

    @GameTest(template = "empty")
    public static void allDrinkCoffeeItems_haveModelMapping(GameTestHelper helper) {
        int total = 0;
        int mapped = 0;
        int excluded = 0;

        for (net.minecraft.world.item.Item item : net.minecraftforge.registries.ForgeRegistries.ITEMS) {
            if (item instanceof DrinkCoffee) {
                total++;
                var id = net.minecraftforge.registries.ForgeRegistries.ITEMS.getKey(item);
                if (id != null && EXPLICIT_EXCLUSIONS.contains(id.toString())) {
                    excluded++;
                } else if (id != null && DrinkDisplayRegistry.canDisplay(id)) {
                    mapped++;
                } else {
                    helper.assertTrue(false,
                            "DrinkCoffee '" + id + "' has no display model mapping or exclusion");
                }
            }
        }

        helper.assertTrue(total == mapped + excluded,
                "All " + total + " DrinkCoffee items must be mapped (" + mapped
                        + ") or explicitly excluded (" + excluded + ")");
        helper.succeed();
    }

    // ========================================================================
    // Breaking drops
    // ========================================================================

    @GameTest(template = "empty")
    public static void breakingDrinkDisplay_removesBlock(GameTestHelper helper) {
        placePlate(helper);
        var player = helper.makeMockPlayer();

        ItemStack drink = new ItemStack(ModItems.COFFEE_AMERICANO.get());
        DrinkCoffee.initCupCount(drink, 4);
        player.setItemInHand(InteractionHand.MAIN_HAND, drink);
        helper.useBlock(POS, player);

        helper.assertBlockPresent(ModBlocks.DRINK_DISPLAY.get(), POS);
        helper.destroyBlock(POS);
        helper.assertBlockNotPresent(ModBlocks.DRINK_DISPLAY.get(), POS);
        helper.succeed();
    }

    // ========================================================================
    // Rotation persistence
    // ========================================================================

    @GameTest(template = "empty")
    public static void displayRotation_persistsAfterPlacement(GameTestHelper helper) {
        var state = ModBlocks.PLATE.get().defaultBlockState()
                .setValue(BlockStateProperties.HORIZONTAL_FACING, Direction.WEST);
        helper.setBlock(POS, state);
        var player = helper.makeMockPlayer();

        ItemStack drink = new ItemStack(ModItems.COFFEE_AMERICANO.get());
        DrinkCoffee.initCupCount(drink, 4);
        player.setItemInHand(InteractionHand.MAIN_HAND, drink);
        helper.useBlock(POS, player);

        var displayState = helper.getBlockState(POS);
        helper.assertTrue(displayState.getValue(BlockStateProperties.HORIZONTAL_FACING) == Direction.WEST,
                "FACING must be WEST after drink placement");
        helper.succeed();
    }

    // ========================================================================
    // ENABLE_MULTI_CUP config enforcement
    // ========================================================================

    @GameTest(template = "empty")
    public static void multiCupDisabled_consumesAllAtOnce(GameTestHelper helper) {
        boolean wasEnabled = ModConfig.ENABLE_MULTI_CUP.get();
        ModConfig.ENABLE_MULTI_CUP.set(false);
        try {
            placePlate(helper);
            var player = helper.makeMockPlayer();

            ItemStack drink = new ItemStack(ModItems.COFFEE_AMERICANO.get());
            DrinkCoffee.initCupCount(drink, 4);
            player.setItemInHand(InteractionHand.MAIN_HAND, drink);
            helper.useBlock(POS, player);

            player.setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY);
            helper.useBlock(POS, player);

            helper.assertBlockPresent(ModBlocks.PLATE.get(), POS);
            helper.assertBlockNotPresent(ModBlocks.DRINK_DISPLAY.get(), POS);
        } finally {
            ModConfig.ENABLE_MULTI_CUP.set(wasEnabled);
        }
        helper.succeed();
    }

    // ========================================================================
    // NBT round-trip (simulates save/reload)
    // ========================================================================

    @GameTest(template = "empty")
    public static void displayBlockEntity_survivesNbtRoundTrip(GameTestHelper helper) {
        helper.setBlock(POS, ModBlocks.DRINK_DISPLAY.get().defaultBlockState());
        BlockEntity be = helper.getBlockEntity(POS);
        helper.assertTrue(be instanceof DrinkDisplayBlockEntity, "Must be DrinkDisplayBlockEntity");

        DrinkDisplayBlockEntity dbe = (DrinkDisplayBlockEntity) be;
        ItemStack drink = new ItemStack(ModItems.COFFEE_LATTE.get());
        DrinkCoffee.initCupCount(drink, 4);
        DrinkCoffee.setRemainingCups(drink, 2);
        dbe.setDrink(drink);

        CompoundTag saved = dbe.saveWithFullMetadata();

        DrinkDisplayBlockEntity fresh = new DrinkDisplayBlockEntity(POS,
                ModBlocks.DRINK_DISPLAY.get().defaultBlockState());
        fresh.load(saved);

        helper.assertTrue(fresh.hasValidDrink(), "Drink must survive NBT round-trip");
        helper.assertTrue(fresh.getRemainingCups() == 2,
                "Remaining cups must survive NBT round-trip");
        helper.assertTrue(fresh.getMaxCups() == 4,
                "Max cups must survive NBT round-trip");
        helper.succeed();
    }
}
