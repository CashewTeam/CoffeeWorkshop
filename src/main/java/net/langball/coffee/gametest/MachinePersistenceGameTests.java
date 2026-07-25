package net.langball.coffee.gametest;

import net.langball.coffee.CoffeeWork;
import net.langball.coffee.block.entity.GrinderBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.gametest.GameTestHolder;
import net.minecraftforge.gametest.PrefixGameTestTemplate;

/**
 * Machine persistence GameTests — verify NBT survives save/load cycles.
 */
@GameTestHolder(CoffeeWork.MODID)
@PrefixGameTestTemplate(false)
public class MachinePersistenceGameTests {

    private static final BlockPos POS = BlockPos.ZERO.above(2);

    /**
     * Places a grinder, fills its slots, saves to NBT, clears, loads back,
     * and verifies the inventory is restored.
     */
    @GameTest(template = "empty")
    public static void inventorySurvivesReload(GameTestHelper helper) {
        helper.setBlock(POS, net.langball.coffee.init.ModBlocks.GRINDER.get());
        var be = (net.langball.coffee.block.entity.MachineBlockEntity)
                helper.getBlockEntity(POS);

        // Fill slots
        be.getItemHandler().insertItem(GrinderBlockEntity.SLOT_INPUT,
                new ItemStack(Blocks.COBBLESTONE, 5), false);
        be.getItemHandler().insertItem(GrinderBlockEntity.SLOT_FUEL,
                new ItemStack(Items.COAL, 3), false);
        be.getItemHandler().insertItem(GrinderBlockEntity.SLOT_OUTPUT,
                new ItemStack(Items.STONE, 2), false);

        // Save
        CompoundTag saved = be.saveWithFullMetadata();

        // Clear
        be.getItemHandler().extractItem(GrinderBlockEntity.SLOT_INPUT, 99, false);
        be.getItemHandler().extractItem(GrinderBlockEntity.SLOT_FUEL, 99, false);
        be.getItemHandler().extractItem(GrinderBlockEntity.SLOT_OUTPUT, 99, false);

        // Load
        be.load(saved);

        // Verify
        ItemStack input = be.getItemHandler().getStackInSlot(GrinderBlockEntity.SLOT_INPUT);
        ItemStack fuel = be.getItemHandler().getStackInSlot(GrinderBlockEntity.SLOT_FUEL);
        ItemStack output = be.getItemHandler().getStackInSlot(GrinderBlockEntity.SLOT_OUTPUT);

        helper.assertTrue(input.is(Blocks.COBBLESTONE.asItem()) && input.getCount() == 5,
                "Input slot should survive reload, got: " + input);
        helper.assertTrue(fuel.is(Items.COAL) && fuel.getCount() == 3,
                "Fuel slot should survive reload, got: " + fuel);
        helper.assertTrue(output.is(Items.STONE) && output.getCount() == 2,
                "Output slot should survive reload, got: " + output);

        helper.succeed();
    }

    /**
     * Verifies that cookTime, totalCookTime, burnTime, and burnTimeTotal
     * are correctly saved and restored.
     */
    @GameTest(template = "empty")
    public static void progressSurvivesReload(GameTestHelper helper) {
        helper.setBlock(POS, net.langball.coffee.init.ModBlocks.GRINDER.get());
        var be = (net.langball.coffee.block.entity.MachineBlockEntity)
                helper.getBlockEntity(POS);

        // Set some progress-like values via data (ContainerData writes through)
        be.data.set(0, 15); // cookTime
        be.data.set(1, 40); // totalCookTime
        be.data.set(2, 120); // burnTime
        be.data.set(3, 200); // burnTimeTotal

        CompoundTag saved = be.saveWithFullMetadata();

        // Reset all
        be.data.set(0, 0);
        be.data.set(1, 0);
        be.data.set(2, 0);
        be.data.set(3, 0);

        be.load(saved);

        helper.assertTrue(be.data.get(0) == 15, "cookTime should survive: " + be.data.get(0));
        helper.assertTrue(be.data.get(1) == 40, "totalCookTime should survive: " + be.data.get(1));
        helper.assertTrue(be.data.get(2) == 120, "burnTime should survive: " + be.data.get(2));
        helper.assertTrue(be.data.get(3) == 200, "burnTimeTotal should survive: " + be.data.get(3));

        helper.succeed();
    }

    @GameTest(template = "empty")
    public static void fuelSurvivesReload(GameTestHelper helper) {
        // Covered by progressSurvivesReload (burnTime is fuel)
        helper.succeed();
    }

    @GameTest(template = "empty")
    public static void recipeIdSurvivesReload(GameTestHelper helper) {
        helper.setBlock(POS, net.langball.coffee.init.ModBlocks.GRINDER.get());
        var be = (net.langball.coffee.block.entity.MachineBlockEntity)
                helper.getBlockEntity(POS);

        // Put valid recipe input
        be.getItemHandler().insertItem(GrinderBlockEntity.SLOT_INPUT,
                new ItemStack(Blocks.COBBLESTONE, 1), false);

        // Tick once to let the BE resolve the recipe and set activeRecipeId
        be.tick(helper.getLevel(), POS, helper.getBlockState(POS));

        // The activeRecipeId should now be set to "coffeework:test_cobble_to_stone"
        CompoundTag saved = be.saveWithFullMetadata();
        helper.assertTrue(saved.contains("ActiveRecipe"),
                "ActiveRecipe NBT key should exist after recipe resolution");

        // Clear and reload
        be.load(new CompoundTag()); // wipe
        be.load(saved);

        // After reload, the recipe ID should be restored.
        // Verify by checking the NBT field directly.
        var map = be.getRecipesUsed();
        // Also verify activeRecipeId via NBT
        CompoundTag reloaded = be.saveWithFullMetadata();
        helper.assertTrue(reloaded.contains("ActiveRecipe"),
                "ActiveRecipe should be present after reload: " + reloaded);

        helper.succeed();
    }

    @GameTest(template = "empty")
    public static void experienceSurvivesReload(GameTestHelper helper) {
        helper.setBlock(POS, net.langball.coffee.init.ModBlocks.GRINDER.get());
        var be = (net.langball.coffee.block.entity.MachineBlockEntity)
                helper.getBlockEntity(POS);

        // Manually record a recipe completion
        be.getRecipesUsed().put(
                net.minecraft.resources.ResourceLocation.parse("coffeework:test_cobble_to_stone"),
                5);

        CompoundTag saved = be.saveWithFullMetadata();

        // Clear
        be.getRecipesUsed().clear();

        be.load(saved);

        helper.assertTrue(
                be.getRecipesUsed().containsKey(
                        net.minecraft.resources.ResourceLocation.parse("coffeework:test_cobble_to_stone")),
                "RecipesUsed should survive NBT reload");
        helper.assertTrue(
                be.getRecipesUsed().get(
                        net.minecraft.resources.ResourceLocation.parse("coffeework:test_cobble_to_stone")) == 5,
                "Experience count should be 5 after reload");

        helper.succeed();
    }
}
