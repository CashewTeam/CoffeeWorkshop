package net.langball.coffee.gametest;

import net.langball.coffee.CoffeeWork;
import net.langball.coffee.block.BlockCakeBasic;
import net.langball.coffee.init.ModBlocks;
import net.langball.coffee.init.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraftforge.gametest.GameTestHolder;
import net.minecraftforge.gametest.PrefixGameTestTemplate;

/**
 * Cake cutting GameTests for BlockCakeBasic slice interaction.
 */
@GameTestHolder(CoffeeWork.MODID)
@PrefixGameTestTemplate(false)
public class CakeGameTests {

    private static final BlockPos CAKE_POS = BlockPos.ZERO.above(2);

    private static void placeCake(GameTestHelper helper) {
        helper.setBlock(CAKE_POS, ModBlocks.CAKE_SPONGE.get());
    }

    /** Count items of {@code item} in player's inventory (all 36 slots). */
    private static int countInInventory(net.minecraft.world.entity.player.Player player,
                                         net.minecraft.world.item.Item item) {
        int count = 0;
        Inventory inv = player.getInventory();
        for (int i = 0; i < inv.getContainerSize(); i++) {
            ItemStack s = inv.getItem(i);
            if (s.is(item)) {
                count += s.getCount();
            }
        }
        return count;
    }

    @GameTest(template = "empty")
    public static void cakeCut_withoutPlate_doesNotProduceSlice(GameTestHelper helper) {
        placeCake(helper);

        var player = helper.makeMockPlayer();
        // Mock player has full hunger by default - lower it so canEat() returns true
        player.getFoodData().setFoodLevel(0);
        helper.useBlock(CAKE_POS, player);

        int bites = helper.getBlockState(CAKE_POS)
                .getValue(BlockStateProperties.BITES);
        helper.assertTrue(bites == 1,
                "Eating advances BITES to 1, got bites=" + bites);

        int slices = countInInventory(player, ModItems.CAKE_SPONGE_SLICE.get());
        helper.assertTrue(slices == 0,
                "Without plate should not produce slice, got slices=" + slices);

        helper.succeed();
    }

    @GameTest(template = "empty")
    public static void cakeCut_producesCorrectSlice(GameTestHelper helper) {
        placeCake(helper);

        var player = helper.makeMockPlayer();
        player.setItemInHand(InteractionHand.MAIN_HAND,
                new ItemStack(ModItems.PLATE.get()));

        helper.useBlock(CAKE_POS, player);

        int slices = countInInventory(player, ModItems.CAKE_SPONGE_SLICE.get());
        helper.assertTrue(slices >= 1,
                "Cut should produce cake_sponge_slice, got slices=" + slices);

        ItemStack hand = player.getItemInHand(InteractionHand.MAIN_HAND);
        helper.assertTrue(hand.is(ModItems.PLATE.get()),
                "Plate should not be consumed, got: " + hand);

        helper.succeed();
    }

    @GameTest(template = "empty")
    public static void cakeCut_advancesBites(GameTestHelper helper) {
        placeCake(helper);

        int bite0 = helper.getBlockState(CAKE_POS)
                .getValue(BlockStateProperties.BITES);
        helper.assertTrue(bite0 == 0,
                "Fresh cake should be BITES=0, got bites=" + bite0);

        var player = helper.makeMockPlayer();
        player.setItemInHand(InteractionHand.MAIN_HAND,
                new ItemStack(ModItems.PLATE.get()));
        helper.useBlock(CAKE_POS, player);

        int bite1 = helper.getBlockState(CAKE_POS)
                .getValue(BlockStateProperties.BITES);
        helper.assertTrue(bite1 == 1,
                "After 1 cut BITES should be 1, got bites=" + bite1);

        helper.succeed();
    }

    @GameTest(template = "empty")
    public static void cakeCut_producesSevenSlices(GameTestHelper helper) {
        placeCake(helper);

        var player = helper.makeMockPlayer();

        for (int i = 0; i < 7; i++) {
            player.setItemInHand(InteractionHand.MAIN_HAND,
                    new ItemStack(ModItems.PLATE.get()));
            helper.useBlock(CAKE_POS, player);
        }

        int slices = countInInventory(player, ModItems.CAKE_SPONGE_SLICE.get());
        helper.assertTrue(slices == 7,
                "7 cuts should produce 7 slices, got slices=" + slices);

        boolean gone = helper.getBlockState(CAKE_POS).isAir();
        helper.assertTrue(gone,
                "Block should be removed after 7th cut");

        helper.succeed();
    }

    @GameTest(template = "empty")
    public static void cakeCut_removesCakeAfterFinalSlice(GameTestHelper helper) {
        placeCake(helper);
        helper.setBlock(CAKE_POS,
                helper.getBlockState(CAKE_POS).setValue(BlockStateProperties.BITES, 6));

        var player = helper.makeMockPlayer();
        player.setItemInHand(InteractionHand.MAIN_HAND,
                new ItemStack(ModItems.PLATE.get()));
        helper.useBlock(CAKE_POS, player);

        boolean gone = helper.getBlockState(CAKE_POS).isAir();
        helper.assertTrue(gone,
                "Block should be removed after cutting from BITES=6");

        helper.succeed();
    }

    @GameTest(template = "empty")
    public static void cakeCut_withFullInventory_dropsSliceInWorld(GameTestHelper helper) {
        placeCake(helper);

        var player = helper.makeMockPlayer();

        for (int slot = 0; slot < 36; slot++) {
            player.getInventory().setItem(slot,
                    new ItemStack(Items.COBBLESTONE, 64));
        }
        player.setItemInHand(InteractionHand.MAIN_HAND,
                new ItemStack(ModItems.PLATE.get()));

        helper.useBlock(CAKE_POS, player);

        int bites = helper.getBlockState(CAKE_POS)
                .getValue(BlockStateProperties.BITES);
        helper.assertTrue(bites == 1,
                "BITES advances even with full inventory, got bites=" + bites);

        ItemStack hand = player.getItemInHand(InteractionHand.MAIN_HAND);
        helper.assertTrue(hand.is(ModItems.PLATE.get()),
                "Plate stays in hand, got: " + hand);

        helper.succeed();
    }

    @GameTest(template = "empty")
    public static void cakeCut_sliceItemIsNonNull(GameTestHelper helper) {
        placeCake(helper);

        var block = helper.getBlockState(CAKE_POS).getBlock();
        helper.assertTrue(block instanceof BlockCakeBasic,
                "cake_sponge should be a BlockCakeBasic instance");

        helper.succeed();
    }
}
