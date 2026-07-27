package net.langball.coffee.gametest;

import net.langball.coffee.CoffeeWork;
import net.langball.coffee.block.CoffeePotBlock;
import net.langball.coffee.block.MokaPotBlock;
import net.langball.coffee.block.entity.CoffeePotBlockEntity;
import net.langball.coffee.block.entity.MokaPotBlockEntity;
import net.langball.coffee.init.ModBlocks;
import net.langball.coffee.init.ModItems;
import net.langball.coffee.item.DrinkCoffee;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.gametest.GameTestHolder;
import net.minecraftforge.gametest.PrefixGameTestTemplate;

@GameTestHolder(CoffeeWork.MODID)
@PrefixGameTestTemplate(false)
public class Phase9GameTests {

    private static final BlockPos POT_POS = BlockPos.ZERO.above(2);
    private static final int TICK_DELAY = 2;

    @GameTest(template = "empty", timeoutTicks = 40)
    public static void potNbtSerializationDoesNotRecurse(GameTestHelper helper) {
        helper.setBlock(POT_POS, ModBlocks.MOKA_POT.get());
        BlockEntity be = helper.getBlockEntity(POT_POS);
        if (!(be instanceof MokaPotBlockEntity moka)) {
            helper.fail("Expected MokaPotBlockEntity", POT_POS);
            return;
        }
        moka.addCoffee();
        moka.addWater();

        CompoundTag tag = moka.getUpdateTag();
        CompoundTag saved = moka.saveForItem();

        helper.assertTrue(!tag.isEmpty(), "saveAdditional should produce non-empty tag");
        helper.assertTrue(!saved.isEmpty(), "saveForItem should produce non-empty tag");

        MokaPotBlockEntity fresh = new MokaPotBlockEntity(POT_POS,
                ModBlocks.MOKA_POT.get().defaultBlockState()
                        .setValue(MokaPotBlock.FACING, net.minecraft.core.Direction.NORTH));
        fresh.load(tag);
        helper.assertTrue(fresh.hasCoffeeInput(), "Loaded Moka should have coffee");
        helper.assertTrue(fresh.hasWaterInput(), "Loaded Moka should have water");
        helper.succeed();
    }

    @GameTest(template = "empty", timeoutTicks = 800)
    public static void mokaDoesNotBrewWithoutHeat(GameTestHelper helper) {
        helper.setBlock(POT_POS, ModBlocks.MOKA_POT.get());
        helper.setBlock(POT_POS.below(), Blocks.STONE);

        BlockEntity be = helper.getBlockEntity(POT_POS);
        if (!(be instanceof MokaPotBlockEntity moka)) {
            helper.fail("Expected MokaPotBlockEntity", POT_POS);
            return;
        }
        moka.addCoffee();
        moka.addWater();

        helper.runAfterDelay(20, () -> {
            BlockEntity be2 = helper.getBlockEntity(POT_POS);
            if (be2 instanceof MokaPotBlockEntity m) {
                helper.assertTrue(!m.isReady(),
                        "Moka should not brew without heat source");
                helper.assertTrue(m.getBrewProgress() == 0,
                        "Moka brewProgress should be 0 without heat");
                helper.succeed();
            } else {
                helper.fail("BlockEntity lost", POT_POS);
            }
        });
    }

    @GameTest(template = "empty", timeoutTicks = 900)
    public static void mokaBrewsWithHeatSource(GameTestHelper helper) {
        helper.setBlock(POT_POS, ModBlocks.MOKA_POT.get());
        helper.setBlock(POT_POS.below(), Blocks.CAMPFIRE.defaultBlockState()
                .setValue(net.minecraft.world.level.block.CampfireBlock.LIT, true));

        BlockEntity be = helper.getBlockEntity(POT_POS);
        if (!(be instanceof MokaPotBlockEntity moka)) {
            helper.fail("Expected MokaPotBlockEntity", POT_POS);
            return;
        }
        moka.addCoffee();
        moka.addWater();

        helper.succeedWhen(() -> {
            BlockEntity be2 = helper.getBlockEntity(POT_POS);
            if (be2 instanceof MokaPotBlockEntity m) {
                if (m.isReady() && m.getServings() == 4) {
                    ItemStack drink = m.getStoredDrink();
                    boolean isSingle = drink.getItem() instanceof DrinkCoffee
                            && DrinkCoffee.getRemainingCups(drink) <= 1
                            && DrinkCoffee.getMaxCups(drink) <= 1;
                    helper.assertTrue(isSingle,
                            "Stored drink should be single-cup (remaining_cups=1, max_cups=1)");
                }
            }
        });
    }

    @GameTest(template = "empty", timeoutTicks = 60)
    public static void coffeePotTransferConservesServings(GameTestHelper helper) {
        helper.setBlock(POT_POS, ModBlocks.COFFEE_POT.get());
        BlockEntity be = helper.getBlockEntity(POT_POS);
        if (!(be instanceof CoffeePotBlockEntity pot)) {
            helper.fail("Expected CoffeePotBlockEntity", POT_POS);
            return;
        }

        ItemStack espresso = new ItemStack(ModItems.ESPRESSO.get());
        DrinkCoffee.setRemainingCups(espresso, 4);
        pot.fillFrom(espresso, 2);

        helper.assertTrue(pot.getServings() == 2,
                "Coffee Pot should have 2 servings after fillFrom(2)");
        helper.assertTrue(!pot.isEmpty(),
                "Coffee Pot should not be empty after fill");
        helper.assertTrue(pot.getCapacity() == 4,
                "Coffee Pot capacity should be 4");

        ItemStack poured = pot.pourServing();
        helper.assertTrue(!poured.isEmpty(), "pourServing should return drink");
        helper.assertTrue(pot.getServings() == 1,
                "Coffee Pot should have 1 serving after pour");
        helper.succeed();
    }

    @GameTest(template = "empty", timeoutTicks = 60)
    public static void coffeePotPickupRestoresLevel(GameTestHelper helper) {
        helper.setBlock(POT_POS, ModBlocks.COFFEE_POT.get());
        BlockEntity be = helper.getBlockEntity(POT_POS);
        if (!(be instanceof CoffeePotBlockEntity pot)) {
            helper.fail("Expected CoffeePotBlockEntity", POT_POS);
            return;
        }

        ItemStack espresso = new ItemStack(ModItems.ESPRESSO.get());
        DrinkCoffee.setRemainingCups(espresso, 1);
        pot.fillFrom(espresso, 3);

        CompoundTag saved = pot.saveForItem();

        helper.setBlock(POT_POS, Blocks.AIR);
        helper.setBlock(POT_POS, ModBlocks.COFFEE_POT.get());

        BlockEntity be2 = helper.getBlockEntity(POT_POS);
        if (be2 instanceof CoffeePotBlockEntity pot2) {
            pot2.load(saved);
            pot2.onLoadSyncLevel();

            helper.runAfterDelay(5, () -> {
                BlockState state = helper.getBlockState(POT_POS);
                int level = state.getValue(CoffeePotBlock.LEVEL);
                helper.assertTrue(level == 3,
                        "Coffee Pot LEVEL should be 3 after restoring NBT, got " + level);
                helper.succeed();
            });
        } else {
            helper.fail("Failed to create second CoffeePotBlockEntity", POT_POS);
        }
    }

    @GameTest(template = "empty", timeoutTicks = 60)
    public static void mokaSecondBatchRequiresFullBrewTime(GameTestHelper helper) {
        helper.setBlock(POT_POS, ModBlocks.MOKA_POT.get());
        BlockEntity be = helper.getBlockEntity(POT_POS);
        if (!(be instanceof MokaPotBlockEntity moka)) {
            helper.fail("Expected MokaPotBlockEntity", POT_POS);
            return;
        }
        moka.addCoffee();
        moka.addWater();

        ItemStack drink = new ItemStack(ModItems.ESPRESSO.get());
        DrinkCoffee.setRemainingCups(drink, 1);
        moka.setStoredDrink(drink);
        moka.setServings(4);

        while (moka.getServings() > 0) moka.pourServing();

        helper.assertTrue(moka.getServings() <= 0,
                "Moka should be empty after pouring all servings");
        helper.assertTrue(moka.getBrewProgress() == 0,
                "brewProgress should reset to 0 after last serving, got " + moka.getBrewProgress());
        helper.succeed();
    }
}
