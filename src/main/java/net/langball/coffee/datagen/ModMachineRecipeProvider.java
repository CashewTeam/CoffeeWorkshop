package net.langball.coffee.datagen;

import net.langball.coffee.CoffeeWork;
import net.langball.coffee.datagen.recipe.CoffeeBrewingRecipeBuilder;
import net.langball.coffee.datagen.recipe.MachineRecipeBuilder;
import net.langball.coffee.init.ModBlocks;
import net.langball.coffee.init.ModItems;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Blocks;

import java.util.function.Consumer;

/**
 * Generates all machine processing recipes via DataGen builders.
 *
 * <p>Called from {@link ModRecipeProvider#buildRecipes(Consumer)}.
 * Not a standalone RecipeProvider to avoid duplicate provider errors.
 */
public final class ModMachineRecipeProvider {

    private ModMachineRecipeProvider() {}

    public static void buildRecipes(Consumer<FinishedRecipe> writer) {
        buildGrindingRecipes(writer);
        buildOvenRecipes(writer);
        buildRollingRecipes(writer);
        buildIcecreamRecipes(writer);
        buildCoffeeRecipes(writer);
    }

    private static void buildGrindingRecipes(Consumer<FinishedRecipe> writer) {
        MachineRecipeBuilder.grinding(Ingredient.of(ModItems.COFFEE_BEAN.get()),
                new ItemStack(ModItems.COFFEE_POWDER.get()))
                .experience(0.2F).cookingTime(200)
                .save(writer, id("grinding/coffee_powder"));

        MachineRecipeBuilder.grinding(Ingredient.of(Items.COCOA_BEANS),
                new ItemStack(ModItems.COCOA_POWDER.get()))
                .experience(0.2F).cookingTime(200)
                .save(writer, id("grinding/cocoa_powder"));

        MachineRecipeBuilder.grinding(Ingredient.of(Items.WHEAT),
                new ItemStack(ModItems.FLOUR.get()))
                .experience(0.1F).cookingTime(200)
                .save(writer, id("grinding/flour"));

        MachineRecipeBuilder.grinding(Ingredient.of(ModItems.COCOA_POWDER.get()),
                new ItemStack(ModItems.COCOA_BATTER.get()))
                .experience(0.15F).cookingTime(200)
                .save(writer, id("grinding/cocoa_batter"));

        MachineRecipeBuilder.grinding(Ingredient.of(Blocks.ICE),
                new ItemStack(ModItems.ICE_SLAG.get()))
                .experience(0.05F).cookingTime(200)
                .save(writer, id("grinding/ice_slag"));

        MachineRecipeBuilder.grinding(Ingredient.of(ModItems.SODA_ORE.get()),
                new ItemStack(ModItems.SODA.get(), 4))
                .experience(0.1F).cookingTime(200)
                .save(writer, id("grinding/soda"));

        // Chocolate bar → chocolate chips (for cocoa_strong, dough_cookie, brownie)
        MachineRecipeBuilder.grinding(Ingredient.of(ModItems.CHOCOLATE_BAR.get()),
                new ItemStack(ModItems.CHOCOLATE_CHIP.get(), 2))
                .experience(0.1F).cookingTime(200)
                .save(writer, id("grinding/chocolate_chip"));
    }

    private static void buildOvenRecipes(Consumer<FinishedRecipe> writer) {
        MachineRecipeBuilder.ovenBaking(Ingredient.of(ModItems.COFFEE_BEAN_RAW.get()),
                new ItemStack(ModItems.COFFEE_BEAN.get()))
                .experience(0.35F).cookingTime(200)
                .save(writer, id("oven_baking/coffee_bean"));

        MachineRecipeBuilder.ovenBaking(Ingredient.of(ModItems.DOUGH_BREAD.get()),
                new ItemStack(Items.BREAD))
                .experience(0.35F).cookingTime(200)
                .save(writer, id("oven_baking/bread"));

        MachineRecipeBuilder.ovenBaking(Ingredient.of(ModItems.DOUGH_BREAD_ROUND.get()),
                new ItemStack(ModItems.BREAD_ROUND.get()))
                .experience(0.35F).cookingTime(200)
                .save(writer, id("oven_baking/bread_round"));

        MachineRecipeBuilder.ovenBaking(Ingredient.of(ModItems.DOUGH_BAGUETTE.get()),
                new ItemStack(ModItems.BAGUETTE.get()))
                .experience(0.35F).cookingTime(200)
                .save(writer, id("oven_baking/baguette"));

        MachineRecipeBuilder.ovenBaking(Ingredient.of(ModItems.DOUGH_BAGEL.get()),
                new ItemStack(ModItems.BAGEL.get()))
                .experience(0.35F).cookingTime(200)
                .save(writer, id("oven_baking/bagel"));

        MachineRecipeBuilder.ovenBaking(Ingredient.of(ModItems.DOUGH_TOAST.get()),
                new ItemStack(ModItems.TOAST.get()))
                .experience(0.35F).cookingTime(200)
                .save(writer, id("oven_baking/toast"));

        MachineRecipeBuilder.ovenBaking(Ingredient.of(ModItems.DOUGH_COOKIE.get()),
                new ItemStack(Items.COOKIE))
                .experience(0.35F).cookingTime(200)
                .save(writer, id("oven_baking/cookie"));

        // Phase 4: Dough intermediate closures
        MachineRecipeBuilder.ovenBaking(Ingredient.of(ModItems.PLATE_DOUGH.get()),
                new ItemStack(ModItems.BREAD_ROUND.get()))
                .experience(0.35F).cookingTime(200)
                .save(writer, id("oven_baking/plate_dough_to_bread_round"));

        // Phase 5.3: Caramel (sugar → oven)
        MachineRecipeBuilder.ovenBaking(Ingredient.of(Items.SUGAR),
                new ItemStack(ModItems.CARAMEL.get()))
                .experience(0.1F).cookingTime(300)
                .save(writer, id("oven_baking/caramel"));

        // Phase 5.3: Hardtack (dough → oven — uses dough, NOT dough_bread, to avoid ambiguity)
        MachineRecipeBuilder.ovenBaking(Ingredient.of(ModItems.DOUGH.get()),
                new ItemStack(ModItems.HARDTACK.get()))
                .experience(0.15F).cookingTime(200)
                .save(writer, id("oven_baking/hardtack"));

        // Phase 7: Croissant raw → oven (1.12.2 registerRaw2CookedRecipes)
        MachineRecipeBuilder.ovenBaking(Ingredient.of(ModItems.CROISSANT_RAW.get()),
                new ItemStack(ModItems.CROISSANT.get()))
                .experience(0.15F).cookingTime(200)
                .save(writer, id("oven_baking/croissant_from_raw"));
        MachineRecipeBuilder.ovenBaking(Ingredient.of(ModItems.CROISSANT_CHOCOLATE_RAW.get()),
                new ItemStack(ModItems.CROISSANT_CHOCOLATE.get()))
                .experience(0.15F).cookingTime(200)
                .save(writer, id("oven_baking/croissant_chocolate_from_raw"));

        // Phase 7: Puff raw → oven (1.12.2 registerRaw2CookedRecipes)
        MachineRecipeBuilder.ovenBaking(Ingredient.of(ModItems.PUFF_RAW.get()),
                new ItemStack(ModItems.PUFF.get()))
                .experience(0.15F).cookingTime(200)
                .save(writer, id("oven_baking/puff_from_raw"));

        // Phase 7: Ginger bread man raw → oven
        MachineRecipeBuilder.ovenBaking(Ingredient.of(ModItems.GINGER_BREAD_MAN_RAW.get()),
                new ItemStack(ModItems.GINGER_BREAD_MAN.get()))
                .experience(0F).cookingTime(200)
                .save(writer, id("oven_baking/ginger_bread_man_from_raw"));

        // Phase 5.4: Raw → oven → Model → craft → Block (1.12.2 chain)
        MachineRecipeBuilder.ovenBaking(Ingredient.of(ModItems.CAKE_CHEESE_RAW.get()),
                new ItemStack(ModItems.CAKE_CHEESE_MODEL.get()))
                .experience(0F).cookingTime(300)
                .save(writer, id("oven_baking/cake_cheese_from_raw"));
        MachineRecipeBuilder.ovenBaking(Ingredient.of(ModItems.CAKE_SPONGE_RAW.get()),
                new ItemStack(ModItems.CAKE_SPONGE_MODEL.get()))
                .experience(0F).cookingTime(200)
                .save(writer, id("oven_baking/sponge_from_raw"));
        MachineRecipeBuilder.ovenBaking(Ingredient.of(ModItems.CAKE_SPONGE_BERRY_RAW.get()),
                new ItemStack(ModItems.CAKE_SPONGE_BERRY_MODEL.get()))
                .experience(0F).cookingTime(200)
                .save(writer, id("oven_baking/sponge_berry_from_raw"));
        MachineRecipeBuilder.ovenBaking(Ingredient.of(ModItems.CAKE_SPONGE_CHOCOLATE_RAW.get()),
                new ItemStack(ModItems.CAKE_SPONGE_CHOCOLATE_MODEL.get()))
                .experience(0F).cookingTime(200)
                .save(writer, id("oven_baking/sponge_chocolate_from_raw"));
        MachineRecipeBuilder.ovenBaking(Ingredient.of(ModItems.CAKE_SPONGE_COFFEE_RAW.get()),
                new ItemStack(ModItems.CAKE_SPONGE_COFFEE_MODEL.get()))
                .experience(0F).cookingTime(200)
                .save(writer, id("oven_baking/sponge_coffee_from_raw"));
        MachineRecipeBuilder.ovenBaking(Ingredient.of(ModItems.CAKE_SPONGE_PUMPKIN_RAW.get()),
                new ItemStack(ModItems.CAKE_SPONGE_PUMPKIN_MODEL.get()))
                .experience(0F).cookingTime(200)
                .save(writer, id("oven_baking/sponge_pumpkin_from_raw"));
        MachineRecipeBuilder.ovenBaking(Ingredient.of(ModItems.CAKE_SPONGE_CARROT_RAW.get()),
                new ItemStack(ModItems.CAKE_SPONGE_CARROT_MODEL.get()))
                .experience(0F).cookingTime(200)
                .save(writer, id("oven_baking/sponge_carrot_from_raw"));
        MachineRecipeBuilder.ovenBaking(Ingredient.of(ModItems.CAKE_SPONGE_REDVELVET_RAW.get()),
                new ItemStack(ModItems.CAKE_SPONGE_REDVELVET_MODEL.get()))
                .experience(0F).cookingTime(200)
                .save(writer, id("oven_baking/sponge_redvelvet_from_raw"));
        MachineRecipeBuilder.ovenBaking(Ingredient.of(ModItems.CAKE_SPONGE_LEMON_RAW.get()),
                new ItemStack(ModItems.CAKE_SPONGE_LEMON_MODEL.get()))
                .experience(0F).cookingTime(200)
                .save(writer, id("oven_baking/sponge_lemon_from_raw"));
        MachineRecipeBuilder.ovenBaking(Ingredient.of(ModItems.CAKE_SPONGE_TEA_RAW.get()),
                new ItemStack(ModItems.CAKE_SPONGE_TEA_MODEL.get()))
                .experience(0F).cookingTime(200)
                .save(writer, id("oven_baking/sponge_tea_from_raw"));

        // Phase 5.4: Cake plate raw → oven → plate model
        MachineRecipeBuilder.ovenBaking(Ingredient.of(ModItems.CAKE_SPONGE_PLATE_RAW.get()),
                new ItemStack(ModItems.CAKE_SPONGE_PLATE_MODEL.get()))
                .experience(0F).cookingTime(200)
                .save(writer, id("oven_baking/sponge_plate_from_raw"));
        MachineRecipeBuilder.ovenBaking(Ingredient.of(ModItems.CAKE_SPONGE_BERRY_PLATE_RAW.get()),
                new ItemStack(ModItems.CAKE_SPONGE_BERRY_PLATE_MODEL.get()))
                .experience(0F).cookingTime(200)
                .save(writer, id("oven_baking/sponge_berry_plate_from_raw"));
        MachineRecipeBuilder.ovenBaking(Ingredient.of(ModItems.CAKE_SPONGE_CARROT_PLATE_RAW.get()),
                new ItemStack(ModItems.CAKE_SPONGE_CARROT_PLATE_MODEL.get()))
                .experience(0F).cookingTime(200)
                .save(writer, id("oven_baking/sponge_carrot_plate_from_raw"));
        MachineRecipeBuilder.ovenBaking(Ingredient.of(ModItems.CAKE_SPONGE_CHOCOLATE_PLATE_RAW.get()),
                new ItemStack(ModItems.CAKE_SPONGE_CHOCOLATE_PLATE_MODEL.get()))
                .experience(0F).cookingTime(200)
                .save(writer, id("oven_baking/sponge_chocolate_plate_from_raw"));
        MachineRecipeBuilder.ovenBaking(Ingredient.of(ModItems.CAKE_SPONGE_COFFEE_PLATE_RAW.get()),
                new ItemStack(ModItems.CAKE_SPONGE_COFFEE_PLATE_MODEL.get()))
                .experience(0F).cookingTime(200)
                .save(writer, id("oven_baking/sponge_coffee_plate_from_raw"));
        MachineRecipeBuilder.ovenBaking(Ingredient.of(ModItems.CAKE_SPONGE_LEMON_PLATE_RAW.get()),
                new ItemStack(ModItems.CAKE_SPONGE_LEMON_PLATE_MODEL.get()))
                .experience(0F).cookingTime(200)
                .save(writer, id("oven_baking/sponge_lemon_plate_from_raw"));
        MachineRecipeBuilder.ovenBaking(Ingredient.of(ModItems.CAKE_SPONGE_PUMPKIN_PLATE_RAW.get()),
                new ItemStack(ModItems.CAKE_SPONGE_PUMPKIN_PLATE_MODEL.get()))
                .experience(0F).cookingTime(200)
                .save(writer, id("oven_baking/sponge_pumpkin_plate_from_raw"));
        MachineRecipeBuilder.ovenBaking(Ingredient.of(ModItems.CAKE_SPONGE_REDVELVET_PLATE_RAW.get()),
                new ItemStack(ModItems.CAKE_SPONGE_REDVELVET_PLATE_MODEL.get()))
                .experience(0F).cookingTime(200)
                .save(writer, id("oven_baking/sponge_redvelvet_plate_from_raw"));
        MachineRecipeBuilder.ovenBaking(Ingredient.of(ModItems.CAKE_SPONGE_TEA_PLATE_RAW.get()),
                new ItemStack(ModItems.CAKE_SPONGE_TEA_PLATE_MODEL.get()))
                .experience(0F).cookingTime(200)
                .save(writer, id("oven_baking/sponge_tea_plate_from_raw"));

        // Phase 7: Jiggy raw → oven → jiggy model (generic + 8 flavored)
        MachineRecipeBuilder.ovenBaking(Ingredient.of(ModItems.JIGGY_CAKE_RAW.get()),
                new ItemStack(ModItems.JIGGY_CAKE_MODEL.get()))
                .experience(0F).cookingTime(200)
                .save(writer, id("oven_baking/jiggy_from_raw"));
        MachineRecipeBuilder.ovenBaking(Ingredient.of(ModItems.JIGGY_CAKE_BERRY_RAW.get()),
                new ItemStack(ModItems.JIGGY_CAKE_BERRY_MODEL.get()))
                .experience(0F).cookingTime(200)
                .save(writer, id("oven_baking/jiggy_berry_from_raw"));
        MachineRecipeBuilder.ovenBaking(Ingredient.of(ModItems.JIGGY_CAKE_CARROT_RAW.get()),
                new ItemStack(ModItems.JIGGY_CAKE_CARROT_MODEL.get()))
                .experience(0F).cookingTime(200)
                .save(writer, id("oven_baking/jiggy_carrot_from_raw"));
        MachineRecipeBuilder.ovenBaking(Ingredient.of(ModItems.JIGGY_CAKE_CHOCOLATE_RAW.get()),
                new ItemStack(ModItems.JIGGY_CAKE_CHOCOLATE_MODEL.get()))
                .experience(0F).cookingTime(200)
                .save(writer, id("oven_baking/jiggy_chocolate_from_raw"));
        MachineRecipeBuilder.ovenBaking(Ingredient.of(ModItems.JIGGY_CAKE_COFFEE_RAW.get()),
                new ItemStack(ModItems.JIGGY_CAKE_COFFEE_MODEL.get()))
                .experience(0F).cookingTime(200)
                .save(writer, id("oven_baking/jiggy_coffee_from_raw"));
        MachineRecipeBuilder.ovenBaking(Ingredient.of(ModItems.JIGGY_CAKE_LEMON_RAW.get()),
                new ItemStack(ModItems.JIGGY_CAKE_LEMON_MODEL.get()))
                .experience(0F).cookingTime(200)
                .save(writer, id("oven_baking/jiggy_lemon_from_raw"));
        MachineRecipeBuilder.ovenBaking(Ingredient.of(ModItems.JIGGY_CAKE_PUMPKIN_RAW.get()),
                new ItemStack(ModItems.JIGGY_CAKE_PUMPKIN_MODEL.get()))
                .experience(0F).cookingTime(200)
                .save(writer, id("oven_baking/jiggy_pumpkin_from_raw"));
        MachineRecipeBuilder.ovenBaking(Ingredient.of(ModItems.JIGGY_CAKE_REDVELVET_RAW.get()),
                new ItemStack(ModItems.JIGGY_CAKE_REDVELVET_MODEL.get()))
                .experience(0F).cookingTime(200)
                .save(writer, id("oven_baking/jiggy_redvelvet_from_raw"));
        MachineRecipeBuilder.ovenBaking(Ingredient.of(ModItems.JIGGY_CAKE_TEA_RAW.get()),
                new ItemStack(ModItems.JIGGY_CAKE_TEA_MODEL.get()))
                .experience(0F).cookingTime(200)
                .save(writer, id("oven_baking/jiggy_tea_from_raw"));

        // Mousse: raw → Icecream Machine → model (1.12.2 cold-set)
        MachineRecipeBuilder.icecreamMaking(Ingredient.of(ModItems.MOUSSE_BERRY_RAW.get()),
                new ItemStack(ModItems.MOUSSE_BERRY_MODEL.get()))
                .experience(0.25F).cookingTime(300)
                .save(writer, id("icecream_making/mousse_berry_from_raw"));
        MachineRecipeBuilder.icecreamMaking(Ingredient.of(ModItems.MOUSSE_CHOCOLATE_RAW.get()),
                new ItemStack(ModItems.MOUSSE_CHOCOLATE_MODEL.get()))
                .experience(0.25F).cookingTime(300)
                .save(writer, id("icecream_making/mousse_chocolate_from_raw"));
        MachineRecipeBuilder.icecreamMaking(Ingredient.of(ModItems.MOUSSE_COFFEE_RAW.get()),
                new ItemStack(ModItems.MOUSSE_COFFEE_MODEL.get()))
                .experience(0.25F).cookingTime(300)
                .save(writer, id("icecream_making/mousse_coffee_from_raw"));
        MachineRecipeBuilder.icecreamMaking(Ingredient.of(ModItems.MOUSSE_LEMON_RAW.get()),
                new ItemStack(ModItems.MOUSSE_LEMON_MODEL.get()))
                .experience(0.25F).cookingTime(300)
                .save(writer, id("icecream_making/mousse_lemon_from_raw"));

        // Phase 7: Muffin raw → Oven (Match 1.12.2 registerRaw2CookedRecipes)
        MachineRecipeBuilder.ovenBaking(Ingredient.of(ModItems.MUFFIN_RAW.get()),
                new ItemStack(ModItems.MUFFIN.get()))
                .experience(0.2F).cookingTime(200)
                .save(writer, id("oven_baking/muffin_from_raw"));
        MachineRecipeBuilder.ovenBaking(Ingredient.of(ModItems.MUFFIN_BERRY_RAW.get()),
                new ItemStack(ModItems.MUFFIN_BERRY.get()))
                .experience(0.2F).cookingTime(200)
                .save(writer, id("oven_baking/muffin_berry_from_raw"));
        MachineRecipeBuilder.ovenBaking(Ingredient.of(ModItems.MUFFIN_CARROT_RAW.get()),
                new ItemStack(ModItems.MUFFIN_CARROT.get()))
                .experience(0.2F).cookingTime(200)
                .save(writer, id("oven_baking/muffin_carrot_from_raw"));
        MachineRecipeBuilder.ovenBaking(Ingredient.of(ModItems.MUFFIN_CHOCOLATE_RAW.get()),
                new ItemStack(ModItems.MUFFIN_CHOCOLATE.get()))
                .experience(0.2F).cookingTime(200)
                .save(writer, id("oven_baking/muffin_chocolate_from_raw"));
        MachineRecipeBuilder.ovenBaking(Ingredient.of(ModItems.MUFFIN_COFFEE_RAW.get()),
                new ItemStack(ModItems.MUFFIN_COFFEE.get()))
                .experience(0.2F).cookingTime(200)
                .save(writer, id("oven_baking/muffin_coffee_from_raw"));
        MachineRecipeBuilder.ovenBaking(Ingredient.of(ModItems.MUFFIN_LEMON_RAW.get()),
                new ItemStack(ModItems.MUFFIN_LEMON.get()))
                .experience(0.2F).cookingTime(200)
                .save(writer, id("oven_baking/muffin_lemon_from_raw"));
        MachineRecipeBuilder.ovenBaking(Ingredient.of(ModItems.MUFFIN_PUMPKIN_RAW.get()),
                new ItemStack(ModItems.MUFFIN_PUMPKIN.get()))
                .experience(0.2F).cookingTime(200)
                .save(writer, id("oven_baking/muffin_pumpkin_from_raw"));
        MachineRecipeBuilder.ovenBaking(Ingredient.of(ModItems.MUFFIN_REDVELVET_RAW.get()),
                new ItemStack(ModItems.MUFFIN_REDVELVET.get()))
                .experience(0.2F).cookingTime(200)
                .save(writer, id("oven_baking/muffin_redvelvet_from_raw"));
        MachineRecipeBuilder.ovenBaking(Ingredient.of(ModItems.MUFFIN_TEA_RAW.get()),
                new ItemStack(ModItems.MUFFIN_TEA.get()))
                .experience(0.2F).cookingTime(200)
                .save(writer, id("oven_baking/muffin_tea_from_raw"));

        // Phase 7: Brownie raw → oven → brownie model
        MachineRecipeBuilder.ovenBaking(Ingredient.of(ModItems.BROWNIE_RAW.get()),
                new ItemStack(ModItems.BROWNIE_MODEL.get()))
                .experience(0F).cookingTime(200)
                .save(writer, id("oven_baking/brownie_from_raw"));

        // Phase 7: Soufflé raw → Oven
        MachineRecipeBuilder.ovenBaking(Ingredient.of(ModItems.SOUFFLE_RAW.get()),
                new ItemStack(ModItems.SOUFFLE.get()))
                .experience(0.25F).cookingTime(300)
                .save(writer, id("oven_baking/souffle_from_raw"));
        MachineRecipeBuilder.ovenBaking(Ingredient.of(ModItems.SOUFFLE_CHOCOLATE_RAW.get()),
                new ItemStack(ModItems.SOUFFLE_CHOCOLATE.get()))
                .experience(0.25F).cookingTime(300)
                .save(writer, id("oven_baking/souffle_chocolate_from_raw"));

        // Phase 7: Mooncake raw → Oven (mooncake_model is crafting remainder, returned at Raw stage)
        MachineRecipeBuilder.ovenBaking(Ingredient.of(ModItems.MOONCAKE_RAW.get()),
                new ItemStack(ModItems.MOONCAKE.get()))
                .experience(0.2F).cookingTime(200)
                .save(writer, id("oven_baking/mooncake_from_raw"));
        MachineRecipeBuilder.ovenBaking(Ingredient.of(ModItems.MOONCAKE_EGG_RAW.get()),
                new ItemStack(ModItems.MOONCAKE_EGG.get()))
                .experience(0.2F).cookingTime(200)
                .save(writer, id("oven_baking/mooncake_egg_from_raw"));
        MachineRecipeBuilder.ovenBaking(Ingredient.of(ModItems.MOONCAKE_FRUIT_RAW.get()),
                new ItemStack(ModItems.MOONCAKE_FRUIT.get()))
                .experience(0.2F).cookingTime(200)
                .save(writer, id("oven_baking/mooncake_fruit_from_raw"));
        MachineRecipeBuilder.ovenBaking(Ingredient.of(ModItems.MOONCAKE_HAM_RAW.get()),
                new ItemStack(ModItems.MOONCAKE_HAM.get()))
                .experience(0.2F).cookingTime(200)
                .save(writer, id("oven_baking/mooncake_ham_from_raw"));
    }

    private static void buildRollingRecipes(Consumer<FinishedRecipe> writer) {
        MachineRecipeBuilder.rolling(Ingredient.of(Items.IRON_INGOT),
                new ItemStack(ModItems.PLATE_IRON.get()))
                .experience(0.1F).cookingTime(200)
                .save(writer, id("rolling/plate_iron"));

        MachineRecipeBuilder.rolling(Ingredient.of(ModItems.DOUGH.get()),
                new ItemStack(ModItems.PLATE_DOUGH.get()))
                .experience(0.1F).cookingTime(200)
                .save(writer, id("rolling/plate_dough"));

        MachineRecipeBuilder.rolling(Ingredient.of(ModItems.DOUGH_PASTRY.get()),
                new ItemStack(ModItems.PLATE_DOUGH_PASTRY.get()))
                .experience(0.1F).cookingTime(200)
                .save(writer, id("rolling/plate_dough_pastry"));

        MachineRecipeBuilder.rolling(Ingredient.of(ModItems.DOUGH_GINGER.get()),
                new ItemStack(ModItems.PLATE_DOUGH_GINGER.get()))
                .experience(0.1F).cookingTime(200)
                .save(writer, id("rolling/plate_dough_ginger"));

        // Phase 5.4: Cake rolls via Base + Cream workbench path only
        // (Sponge → Roller shortcuts REMOVED — use Cake Base + Cream → Roll path)
    }

    private static void buildIcecreamRecipes(Consumer<FinishedRecipe> writer) {
        MachineRecipeBuilder.icecreamMaking(
                Ingredient.of(ModItems.ICECREAM_MIX_VANILLA.get()),
                new ItemStack(ModItems.ICECREAM_VANILLA.get()))
                .experience(0.2F).cookingTime(400)
                .save(writer, id("icecream_making/vanilla"));

        // Phase 6: Cream milk base via workbench only (Mixing Bowl + Milk + Vanilla)
        // Milk → Icecream Machine → Cream Milk shortcut REMOVED (bypasses Mixing Bowl and Vanilla)

        // Phase 6: Cream → Icecream Machine → icecream (1.12.2)
        MachineRecipeBuilder.icecreamMaking(Ingredient.of(ModItems.CREAM_APPLE.get()),
                new ItemStack(ModItems.ICECREAM_APPLE.get()))
                .experience(0.2F).cookingTime(400)
                .save(writer, id("icecream_making/apple"));
        MachineRecipeBuilder.icecreamMaking(Ingredient.of(ModItems.CREAM_BERRY.get()),
                new ItemStack(ModItems.ICECREAM_BERRY.get()))
                .experience(0.2F).cookingTime(400)
                .save(writer, id("icecream_making/berry"));
        MachineRecipeBuilder.icecreamMaking(Ingredient.of(ModItems.CREAM_CHOCOLATE.get()),
                new ItemStack(ModItems.ICECREAM_CHOCOLATE.get()))
                .experience(0.2F).cookingTime(400)
                .save(writer, id("icecream_making/chocolate"));
        MachineRecipeBuilder.icecreamMaking(Ingredient.of(ModItems.CREAM_COFFEE.get()),
                new ItemStack(ModItems.ICECREAM_COFFEE.get()))
                .experience(0.2F).cookingTime(400)
                .save(writer, id("icecream_making/coffee"));
        MachineRecipeBuilder.icecreamMaking(Ingredient.of(ModItems.CREAM_LEMON.get()),
                new ItemStack(ModItems.ICECREAM_LEMON.get()))
                .experience(0.2F).cookingTime(400)
                .save(writer, id("icecream_making/lemon"));
        MachineRecipeBuilder.icecreamMaking(Ingredient.of(ModItems.CREAM_MELON.get()),
                new ItemStack(ModItems.ICECREAM_MELON.get()))
                .experience(0.2F).cookingTime(400)
                .save(writer, id("icecream_making/melon"));

        // Tiramisu raw → Icecream Machine → model
        MachineRecipeBuilder.icecreamMaking(Ingredient.of(ModItems.TIRAMISU_RAW.get()),
                new ItemStack(ModItems.TIRAMISU_MODEL.get()))
                .experience(0.5F).cookingTime(500)
                .save(writer, id("icecream_making/tiramisu_from_raw"));
    }

    private static void buildCoffeeRecipes(Consumer<FinishedRecipe> writer) {
        // ═══════════════════════════════════════════════════════════════════
        // Phase 3 base recipes (v2 compatible)
        // ═══════════════════════════════════════════════════════════════════

        CoffeeBrewingRecipeBuilder.brewing(new ItemStack(ModItems.ESPRESSO.get()))
                .base(Ingredient.of(ModItems.COFFEE_POWDER.get()), 2)
                .container(Ingredient.of(ModItems.CUP.get()), 1)
                .experience(0.2F).cookingTime(80)
                .save(writer, id("coffee_brewing/espresso"));

        CoffeeBrewingRecipeBuilder.brewing(new ItemStack(ModItems.COFFEE_AMERICANO.get()))
                .base(Ingredient.of(ModItems.COFFEE_POWDER.get()), 1)
                .modifier(Ingredient.of(Items.WATER_BUCKET), 1)
                .container(Ingredient.of(ModItems.CUP.get()), 1)
                .experience(0.2F).cookingTime(120)
                .save(writer, id("coffee_brewing/americano"));

        CoffeeBrewingRecipeBuilder.brewing(new ItemStack(ModItems.COFFEE_LATTE.get()))
                .base(Ingredient.of(ModItems.COFFEE_POWDER.get()), 1)
                .modifier(Ingredient.of(Items.MILK_BUCKET), 1)
                .container(Ingredient.of(ModItems.CUP.get()), 1)
                .experience(0.25F).cookingTime(120)
                .save(writer, id("coffee_brewing/latte"));

        // ═══════════════════════════════════════════════════════════════════
        // Phase 4 P0: Coffee, Cocoa & Tea drinks
        // ═══════════════════════════════════════════════════════════════════

        CoffeeBrewingRecipeBuilder.brewing(new ItemStack(ModItems.COFFEE_CAPPUCCINO.get()))
                .base(Ingredient.of(ModItems.COFFEE_POWDER.get()), 1)
                .modifier(Ingredient.of(Items.MILK_BUCKET), 1)
                .additive(Ingredient.of(Items.SUGAR), 1)
                .container(Ingredient.of(ModItems.CUP.get()), 1)
                .experience(0.3F).cookingTime(140)
                .save(writer, id("coffee_brewing/cappuccino"));

        CoffeeBrewingRecipeBuilder.brewing(new ItemStack(ModItems.COFFEE_MACCHIATO.get()))
                .base(Ingredient.of(ModItems.COFFEE_POWDER.get()), 2)
                .modifier(Ingredient.of(Items.MILK_BUCKET), 1)
                .container(Ingredient.of(ModItems.CUP.get()), 1)
                .experience(0.3F).cookingTime(140)
                .save(writer, id("coffee_brewing/macchiato"));

        CoffeeBrewingRecipeBuilder.brewing(new ItemStack(ModItems.COFFEE_MOCHACCINO.get()))
                .base(Ingredient.of(ModItems.COFFEE_POWDER.get()), 1)
                .modifier(Ingredient.of(Items.MILK_BUCKET), 1)
                .additive(Ingredient.of(ModItems.COCOA_POWDER.get()), 1)
                .container(Ingredient.of(ModItems.CUP.get()), 1)
                .experience(0.3F).cookingTime(140)
                .save(writer, id("coffee_brewing/mochaccino"));

        CoffeeBrewingRecipeBuilder.brewing(new ItemStack(ModItems.COCOA.get()))
                .base(Ingredient.of(ModItems.COCOA_POWDER.get()), 2)
                .modifier(Ingredient.of(Items.MILK_BUCKET), 1)
                .container(Ingredient.of(ModItems.CUP.get()), 1)
                .experience(0.25F).cookingTime(120)
                .save(writer, id("coffee_brewing/cocoa"));

        CoffeeBrewingRecipeBuilder.brewing(new ItemStack(ModItems.COCOA_STRONG.get()))
                .base(Ingredient.of(ModItems.COCOA_POWDER.get()), 2)
                .modifier(Ingredient.of(Items.MILK_BUCKET), 1)
                .additive(Ingredient.of(ModItems.CHOCOLATE_CHIP.get()), 1)
                .container(Ingredient.of(ModItems.CUP.get()), 1)
                .experience(0.35F).cookingTime(160)
                .save(writer, id("coffee_brewing/cocoa_strong"));

        CoffeeBrewingRecipeBuilder.brewing(new ItemStack(ModItems.COFFEE_GREEN_TEA.get()))
                .base(Ingredient.of(ModItems.TEA_LEAF.get()), 1)
                .modifier(Ingredient.of(Items.WATER_BUCKET), 1)
                .container(Ingredient.of(ModItems.CUP_GLASS.get()), 1)
                .experience(0.15F).cookingTime(100)
                .save(writer, id("coffee_brewing/green_tea"));

        CoffeeBrewingRecipeBuilder.brewing(new ItemStack(ModItems.COFFEE_BLACK_TEA.get()))
                .base(Ingredient.of(ModItems.BLACK_TEA_LEAF.get()), 1)
                .modifier(Ingredient.of(Items.WATER_BUCKET), 1)
                .container(Ingredient.of(ModItems.CUP_GLASS.get()), 1)
                .experience(0.2F).cookingTime(100)
                .save(writer, id("coffee_brewing/black_tea"));

        CoffeeBrewingRecipeBuilder.brewing(new ItemStack(ModItems.COFFEE_MILK_TEA.get()))
                .base(Ingredient.of(ModItems.BLACK_TEA_LEAF.get()), 1)
                .modifier(Ingredient.of(Items.MILK_BUCKET), 1)
                .additive(Ingredient.of(Items.SUGAR), 1)
                .container(Ingredient.of(ModItems.CUP_GLASS.get()), 1)
                .experience(0.25F).cookingTime(120)
                .save(writer, id("coffee_brewing/milk_tea"));

        CoffeeBrewingRecipeBuilder.brewing(new ItemStack(ModItems.COFFEE_MANDARIN_DRINK.get()))
                .base(Ingredient.of(ModItems.COFFEE_POWDER.get()), 1)
                .modifier(Ingredient.of(Items.MILK_BUCKET), 1)
                .additive(Ingredient.of(ModItems.TEA_LEAF.get()), 1)
                .container(Ingredient.of(ModItems.CUP_GLASS.get()), 1)
                .experience(0.3F).cookingTime(140)
                .save(writer, id("coffee_brewing/mandarin_drink"));

        CoffeeBrewingRecipeBuilder.brewing(new ItemStack(ModItems.COFFEE_COLDBREW.get()))
                .base(Ingredient.of(ModItems.COLDBREW_BOTTLE.get()), 1)
                .container(Ingredient.of(ModItems.CUP_GLASS.get()), 1)
                .experience(0.3F).cookingTime(160)
                .save(writer, id("coffee_brewing/coldbrew"));

        CoffeeBrewingRecipeBuilder.brewing(new ItemStack(ModItems.COFFEE_AMERICANO_ICE.get()))
                .base(Ingredient.of(ModItems.COFFEE_POWDER.get()), 1)
                .modifier(Ingredient.of(Items.WATER_BUCKET), 1)
                .additive(Ingredient.of(ModItems.ICE_SLAG.get()), 1)
                .container(Ingredient.of(ModItems.CUP_GLASS.get()), 1)
                .experience(0.25F).cookingTime(140)
                .save(writer, id("coffee_brewing/iced_americano"));

        CoffeeBrewingRecipeBuilder.brewing(new ItemStack(ModItems.COFFEE_LATTE_ICE.get()))
                .base(Ingredient.of(ModItems.COFFEE_POWDER.get()), 1)
                .modifier(Ingredient.of(Items.MILK_BUCKET), 1)
                .additive(Ingredient.of(ModItems.ICE_SLAG.get()), 1)
                .container(Ingredient.of(ModItems.CUP_GLASS.get()), 1)
                .experience(0.25F).cookingTime(140)
                .save(writer, id("coffee_brewing/iced_latte"));

        CoffeeBrewingRecipeBuilder.brewing(new ItemStack(ModItems.COFFEE_COLDBREW_ICE.get()))
                .base(Ingredient.of(ModItems.COLDBREW_BOTTLE.get()), 1)
                .additive(Ingredient.of(ModItems.ICE_SLAG.get()), 1)
                .container(Ingredient.of(ModItems.CUP_GLASS.get()), 1)
                .experience(0.3F).cookingTime(180)
                .save(writer, id("coffee_brewing/iced_coldbrew"));

        // ═══════════════════════════════════════════════════════════════════
        // Phase 4 P1: Hot Flavored Lattes (syrup as additive)
        // ═══════════════════════════════════════════════════════════════════

        // All: coffee_powder + milk_bucket + [syrup] + cup

        CoffeeBrewingRecipeBuilder.brewing(new ItemStack(ModItems.COFFEE_LATTE_CARAMEL.get()))
                .base(Ingredient.of(ModItems.COFFEE_POWDER.get()), 1)
                .modifier(Ingredient.of(Items.MILK_BUCKET), 1)
                .additive(Ingredient.of(ModItems.SYRUP_CARAMEL.get()), 1)
                .container(Ingredient.of(ModItems.CUP.get()), 1)
                .experience(0.3F).cookingTime(120)
                .save(writer, id("coffee_brewing/latte_caramel"));

        CoffeeBrewingRecipeBuilder.brewing(new ItemStack(ModItems.COFFEE_LATTE_CHOCOLATE.get()))
                .base(Ingredient.of(ModItems.COFFEE_POWDER.get()), 1)
                .modifier(Ingredient.of(Items.MILK_BUCKET), 1)
                .additive(Ingredient.of(ModItems.SYRUP_CHOCOLATE.get()), 1)
                .container(Ingredient.of(ModItems.CUP.get()), 1)
                .experience(0.3F).cookingTime(120)
                .save(writer, id("coffee_brewing/latte_chocolate"));

        CoffeeBrewingRecipeBuilder.brewing(new ItemStack(ModItems.COFFEE_LATTE_FRUIT.get()))
                .base(Ingredient.of(ModItems.COFFEE_POWDER.get()), 1)
                .modifier(Ingredient.of(Items.MILK_BUCKET), 1)
                .additive(Ingredient.of(ModItems.SYRUP_FRUIT.get()), 1)
                .container(Ingredient.of(ModItems.CUP.get()), 1)
                .experience(0.3F).cookingTime(120)
                .save(writer, id("coffee_brewing/latte_fruit"));

        CoffeeBrewingRecipeBuilder.brewing(new ItemStack(ModItems.COFFEE_LATTE_MINT.get()))
                .base(Ingredient.of(ModItems.COFFEE_POWDER.get()), 1)
                .modifier(Ingredient.of(Items.MILK_BUCKET), 1)
                .additive(Ingredient.of(ModItems.SYRUP_MINT.get()), 1)
                .container(Ingredient.of(ModItems.CUP.get()), 1)
                .experience(0.3F).cookingTime(120)
                .save(writer, id("coffee_brewing/latte_mint"));

        CoffeeBrewingRecipeBuilder.brewing(new ItemStack(ModItems.COFFEE_LATTE_VANILLA.get()))
                .base(Ingredient.of(ModItems.COFFEE_POWDER.get()), 1)
                .modifier(Ingredient.of(Items.MILK_BUCKET), 1)
                .additive(Ingredient.of(ModItems.SYRUP_VANILLA.get()), 1)
                .container(Ingredient.of(ModItems.CUP.get()), 1)
                .experience(0.3F).cookingTime(120)
                .save(writer, id("coffee_brewing/latte_vanilla"));

        CoffeeBrewingRecipeBuilder.brewing(new ItemStack(ModItems.COFFEE_LATTE_SAKURA.get()))
                .base(Ingredient.of(ModItems.COFFEE_POWDER.get()), 1)
                .modifier(Ingredient.of(Items.MILK_BUCKET), 1)
                .additive(Ingredient.of(ModItems.SYRUP_SAKURA.get()), 1)
                .container(Ingredient.of(ModItems.CUP.get()), 1)
                .experience(0.3F).cookingTime(120)
                .save(writer, id("coffee_brewing/latte_sakura"));

        // ═══════════════════════════════════════════════════════════════════
        // Phase 4 P1: Iced Flavored Lattes (syrup + cup_glass, no ice_slag)
        // ═══════════════════════════════════════════════════════════════════

        // All: coffee_powder + milk_bucket + [syrup] + cup_glass

        CoffeeBrewingRecipeBuilder.brewing(new ItemStack(ModItems.COFFEE_LATTE_CARAMEL_ICE.get()))
                .base(Ingredient.of(ModItems.COFFEE_POWDER.get()), 1)
                .modifier(Ingredient.of(Items.MILK_BUCKET), 1)
                .additive(Ingredient.of(ModItems.SYRUP_CARAMEL.get()), 1)
                .container(Ingredient.of(ModItems.CUP_GLASS.get()), 1)
                .experience(0.25F).cookingTime(120)
                .save(writer, id("coffee_brewing/iced_latte_caramel"));

        CoffeeBrewingRecipeBuilder.brewing(new ItemStack(ModItems.COFFEE_LATTE_CHOCOLATE_ICE.get()))
                .base(Ingredient.of(ModItems.COFFEE_POWDER.get()), 1)
                .modifier(Ingredient.of(Items.MILK_BUCKET), 1)
                .additive(Ingredient.of(ModItems.SYRUP_CHOCOLATE.get()), 1)
                .container(Ingredient.of(ModItems.CUP_GLASS.get()), 1)
                .experience(0.25F).cookingTime(120)
                .save(writer, id("coffee_brewing/iced_latte_chocolate"));

        CoffeeBrewingRecipeBuilder.brewing(new ItemStack(ModItems.COFFEE_LATTE_FRUIT_ICE.get()))
                .base(Ingredient.of(ModItems.COFFEE_POWDER.get()), 1)
                .modifier(Ingredient.of(Items.MILK_BUCKET), 1)
                .additive(Ingredient.of(ModItems.SYRUP_FRUIT.get()), 1)
                .container(Ingredient.of(ModItems.CUP_GLASS.get()), 1)
                .experience(0.25F).cookingTime(120)
                .save(writer, id("coffee_brewing/iced_latte_fruit"));

        CoffeeBrewingRecipeBuilder.brewing(new ItemStack(ModItems.COFFEE_LATTE_MINT_ICE.get()))
                .base(Ingredient.of(ModItems.COFFEE_POWDER.get()), 1)
                .modifier(Ingredient.of(Items.MILK_BUCKET), 1)
                .additive(Ingredient.of(ModItems.SYRUP_MINT.get()), 1)
                .container(Ingredient.of(ModItems.CUP_GLASS.get()), 1)
                .experience(0.25F).cookingTime(120)
                .save(writer, id("coffee_brewing/iced_latte_mint"));

        CoffeeBrewingRecipeBuilder.brewing(new ItemStack(ModItems.COFFEE_LATTE_VANILLA_ICE.get()))
                .base(Ingredient.of(ModItems.COFFEE_POWDER.get()), 1)
                .modifier(Ingredient.of(Items.MILK_BUCKET), 1)
                .additive(Ingredient.of(ModItems.SYRUP_VANILLA.get()), 1)
                .container(Ingredient.of(ModItems.CUP_GLASS.get()), 1)
                .experience(0.25F).cookingTime(120)
                .save(writer, id("coffee_brewing/iced_latte_vanilla"));

        CoffeeBrewingRecipeBuilder.brewing(new ItemStack(ModItems.COFFEE_LATTE_SAKURA_ICE.get()))
                .base(Ingredient.of(ModItems.COFFEE_POWDER.get()), 1)
                .modifier(Ingredient.of(Items.MILK_BUCKET), 1)
                .additive(Ingredient.of(ModItems.SYRUP_SAKURA.get()), 1)
                .container(Ingredient.of(ModItems.CUP_GLASS.get()), 1)
                .experience(0.25F).cookingTime(120)
                .save(writer, id("coffee_brewing/iced_latte_sakura"));

        // ═══════════════════════════════════════════════════════════════════
        // Phase 4 P1: Iced drink extensions (ice_slag as iced differentiator)
        // ═══════════════════════════════════════════════════════════════════

        // NOTE: Hot tea/cocoa uses cup_glass.  Iced versions MUST have
        // ice_slag or another distinguishing additive to avoid ambiguous
        // recipes (same inputs → two possible outputs).

        // Iced Cappuccino: coffee_powder + milk_bucket + sugar + cup_glass
        CoffeeBrewingRecipeBuilder.brewing(new ItemStack(ModItems.COFFEE_CAPPUCCINO_ICE.get()))
                .base(Ingredient.of(ModItems.COFFEE_POWDER.get()), 1)
                .modifier(Ingredient.of(Items.MILK_BUCKET), 1)
                .additive(Ingredient.of(Items.SUGAR), 1)
                .container(Ingredient.of(ModItems.CUP_GLASS.get()), 1)
                .experience(0.25F).cookingTime(140)
                .save(writer, id("coffee_brewing/iced_cappuccino"));

        // Iced Macchiato: 2×coffee_powder + milk_bucket + cup_glass
        CoffeeBrewingRecipeBuilder.brewing(new ItemStack(ModItems.COFFEE_MACCHIATO_ICE.get()))
                .base(Ingredient.of(ModItems.COFFEE_POWDER.get()), 2)
                .modifier(Ingredient.of(Items.MILK_BUCKET), 1)
                .container(Ingredient.of(ModItems.CUP_GLASS.get()), 1)
                .experience(0.25F).cookingTime(140)
                .save(writer, id("coffee_brewing/iced_macchiato"));

        // Iced Mochaccino: coffee_powder + milk_bucket + cocoa_powder + cup_glass
        CoffeeBrewingRecipeBuilder.brewing(new ItemStack(ModItems.COFFEE_MOCHACCINO_ICE.get()))
                .base(Ingredient.of(ModItems.COFFEE_POWDER.get()), 1)
                .modifier(Ingredient.of(Items.MILK_BUCKET), 1)
                .additive(Ingredient.of(ModItems.COCOA_POWDER.get()), 1)
                .container(Ingredient.of(ModItems.CUP_GLASS.get()), 1)
                .experience(0.25F).cookingTime(140)
                .save(writer, id("coffee_brewing/iced_mochaccino"));

        // Iced Green Tea: tea_leaf + water_bucket + ice_slag + cup_glass
        // (ice_slag distinguishes from hot Green Tea)
        CoffeeBrewingRecipeBuilder.brewing(new ItemStack(ModItems.COFFEE_GREEN_TEA_ICE.get()))
                .base(Ingredient.of(ModItems.TEA_LEAF.get()), 1)
                .modifier(Ingredient.of(Items.WATER_BUCKET), 1)
                .additive(Ingredient.of(ModItems.ICE_SLAG.get()), 1)
                .container(Ingredient.of(ModItems.CUP_GLASS.get()), 1)
                .experience(0.15F).cookingTime(100)
                .save(writer, id("coffee_brewing/iced_green_tea"));

        // Iced Black Tea: black_tea_leaf + water_bucket + ice_slag + cup_glass
        CoffeeBrewingRecipeBuilder.brewing(new ItemStack(ModItems.COFFEE_BLACK_TEA_ICE.get()))
                .base(Ingredient.of(ModItems.BLACK_TEA_LEAF.get()), 1)
                .modifier(Ingredient.of(Items.WATER_BUCKET), 1)
                .additive(Ingredient.of(ModItems.ICE_SLAG.get()), 1)
                .container(Ingredient.of(ModItems.CUP_GLASS.get()), 1)
                .experience(0.15F).cookingTime(100)
                .save(writer, id("coffee_brewing/iced_black_tea"));

        // Iced Milk Tea: black_tea_leaf + milk_bucket + ice_slag + cup_glass
        // (ice_slag instead of sugar — both would need separate additive slots)
        CoffeeBrewingRecipeBuilder.brewing(new ItemStack(ModItems.COFFEE_MILK_TEA_ICE.get()))
                .base(Ingredient.of(ModItems.BLACK_TEA_LEAF.get()), 1)
                .modifier(Ingredient.of(Items.MILK_BUCKET), 1)
                .additive(Ingredient.of(ModItems.ICE_SLAG.get()), 1)
                .container(Ingredient.of(ModItems.CUP_GLASS.get()), 1)
                .experience(0.2F).cookingTime(120)
                .save(writer, id("coffee_brewing/iced_milk_tea"));

        // Iced Mandarin Drink: SKIPPED — same signature as Iced Latte
        // (coffee_powder + milk_bucket + ice_slag + cup_glass).  Both would produce
        // the same input signature.  Item remains creative-only until a second
        // additive slot is available.

        // Iced Cocoa: 2×cocoa_powder + milk_bucket + ice_slag + cup_glass
        CoffeeBrewingRecipeBuilder.brewing(new ItemStack(ModItems.COCOA_ICE.get()))
                .base(Ingredient.of(ModItems.COCOA_POWDER.get()), 2)
                .modifier(Ingredient.of(Items.MILK_BUCKET), 1)
                .additive(Ingredient.of(ModItems.ICE_SLAG.get()), 1)
                .container(Ingredient.of(ModItems.CUP_GLASS.get()), 1)
                .experience(0.2F).cookingTime(120)
                .save(writer, id("coffee_brewing/iced_cocoa"));

        // Iced Cocoa Strong: 2×cocoa_powder + milk_bucket + chocolate_chip + cup_glass
        // (hot cocoa_strong uses CUP → cup_glass differentiator avoids ambiguity)
        CoffeeBrewingRecipeBuilder.brewing(new ItemStack(ModItems.COCOA_STRONG_ICE.get()))
                .base(Ingredient.of(ModItems.COCOA_POWDER.get()), 2)
                .modifier(Ingredient.of(Items.MILK_BUCKET), 1)
                .additive(Ingredient.of(ModItems.CHOCOLATE_CHIP.get()), 1)
                .container(Ingredient.of(ModItems.CUP_GLASS.get()), 1)
                .experience(0.3F).cookingTime(160)
                .save(writer, id("coffee_brewing/iced_cocoa_strong"));

        // Iced Mandarin Drink: SKIPPED — same signature as Iced Latte.
        // Iced Coldbrew Fruit: SKIPPED — same signature as Iced Coldbrew.
        // Iced Coldbrew Latte flavored variants (caramel/choc/fruit/mint/vanilla):
        //   SKIPPED — all share same signature coldbrew_bottle+milk+ice_slag+cup_glass.
        // These use the cooling crafting recipe: hot drink + ice_slag → iced drink.

        // Iced Coldbrew Latte: coldbrew_bottle + milk_bucket + ice_slag + cup_glass
        // (hot coldbrew_latte has no additive → ice_slag distinguishes)
        CoffeeBrewingRecipeBuilder.brewing(new ItemStack(ModItems.COFFEE_COLDBREW_LATTE_ICE.get()))
                .base(Ingredient.of(ModItems.COLDBREW_BOTTLE.get()), 1)
                .modifier(Ingredient.of(Items.MILK_BUCKET), 1)
                .additive(Ingredient.of(ModItems.ICE_SLAG.get()), 1)
                .container(Ingredient.of(ModItems.CUP_GLASS.get()), 1)
                .experience(0.3F).cookingTime(180)
                .save(writer, id("coffee_brewing/iced_coldbrew_latte"));

        // ═══════════════════════════════════════════════════════════════════
        // Phase 4 P1: Americano extensions
        // ═══════════════════════════════════════════════════════════════════

        // Fruit Americano: coffee_powder + water_bucket + syrup_fruit + cup
        CoffeeBrewingRecipeBuilder.brewing(new ItemStack(ModItems.COFFEE_AMERICANO_FRUIT.get()))
                .base(Ingredient.of(ModItems.COFFEE_POWDER.get()), 1)
                .modifier(Ingredient.of(Items.WATER_BUCKET), 1)
                .additive(Ingredient.of(ModItems.SYRUP_FRUIT.get()), 1)
                .container(Ingredient.of(ModItems.CUP.get()), 1)
                .experience(0.3F).cookingTime(120)
                .save(writer, id("coffee_brewing/americano_fruit"));

        // Fruit Americano Ice: coffee_powder + water_bucket + syrup_fruit + cup_glass
        CoffeeBrewingRecipeBuilder.brewing(new ItemStack(ModItems.COFFEE_AMERICANO_FRUIT_ICE.get()))
                .base(Ingredient.of(ModItems.COFFEE_POWDER.get()), 1)
                .modifier(Ingredient.of(Items.WATER_BUCKET), 1)
                .additive(Ingredient.of(ModItems.SYRUP_FRUIT.get()), 1)
                .container(Ingredient.of(ModItems.CUP_GLASS.get()), 1)
                .experience(0.25F).cookingTime(120)
                .save(writer, id("coffee_brewing/iced_americano_fruit"));

        // Nitro Ice Americano: coffee_powder + water_bucket + soda + cup_glass
        CoffeeBrewingRecipeBuilder.brewing(new ItemStack(ModItems.COFFEE_AMERICANO_NITRO_ICE.get()))
                .base(Ingredient.of(ModItems.COFFEE_POWDER.get()), 1)
                .modifier(Ingredient.of(Items.WATER_BUCKET), 1)
                .additive(Ingredient.of(ModItems.SODA.get()), 1)
                .container(Ingredient.of(ModItems.CUP_GLASS.get()), 1)
                .experience(0.3F).cookingTime(140)
                .save(writer, id("coffee_brewing/americano_nitro_ice"));

        // NOTE: americano_nitro_fruit_ice is SKIPPED because it would share the signed
        // (coffee_powder + water_bucket + soda + cup_glass) with americano_nitro_ice
        // without actually consuming fruit syrup (only 1 additive slot available).
        // The item remains registered but creative-only until a second additive slot
        // is added in a future schema version.

        // ═══════════════════════════════════════════════════════════════════
        // Phase 4 P1: Coldbrew extensions
        // ═══════════════════════════════════════════════════════════════════

        // All coldbrew drinks: coldbrew_bottle + [modifier?] + [syrup?] + cup_glass

        // Coldbrew Fruit: coldbrew_bottle + syrup_fruit + cup_glass
        CoffeeBrewingRecipeBuilder.brewing(new ItemStack(ModItems.COFFEE_COLDBREW_FRUIT.get()))
                .base(Ingredient.of(ModItems.COLDBREW_BOTTLE.get()), 1)
                .additive(Ingredient.of(ModItems.SYRUP_FRUIT.get()), 1)
                .container(Ingredient.of(ModItems.CUP_GLASS.get()), 1)
                .experience(0.35F).cookingTime(160)
                .save(writer, id("coffee_brewing/coldbrew_fruit"));

        // Coldbrew Latte: coldbrew_bottle + milk_bucket + cup_glass
        CoffeeBrewingRecipeBuilder.brewing(new ItemStack(ModItems.COFFEE_COLDBREW_LATTE.get()))
                .base(Ingredient.of(ModItems.COLDBREW_BOTTLE.get()), 1)
                .modifier(Ingredient.of(Items.MILK_BUCKET), 1)
                .container(Ingredient.of(ModItems.CUP_GLASS.get()), 1)
                .experience(0.35F).cookingTime(160)
                .save(writer, id("coffee_brewing/coldbrew_latte"));

        // Coldbrew Latte variants: coldbrew_bottle + milk_bucket + [syrup] + cup_glass
        CoffeeBrewingRecipeBuilder.brewing(new ItemStack(ModItems.COFFEE_COLDBREW_LATTE_CARAMEL.get()))
                .base(Ingredient.of(ModItems.COLDBREW_BOTTLE.get()), 1)
                .modifier(Ingredient.of(Items.MILK_BUCKET), 1)
                .additive(Ingredient.of(ModItems.SYRUP_CARAMEL.get()), 1)
                .container(Ingredient.of(ModItems.CUP_GLASS.get()), 1)
                .experience(0.35F).cookingTime(160)
                .save(writer, id("coffee_brewing/coldbrew_latte_caramel"));

        CoffeeBrewingRecipeBuilder.brewing(new ItemStack(ModItems.COFFEE_COLDBREW_LATTE_CHOCOLATE.get()))
                .base(Ingredient.of(ModItems.COLDBREW_BOTTLE.get()), 1)
                .modifier(Ingredient.of(Items.MILK_BUCKET), 1)
                .additive(Ingredient.of(ModItems.SYRUP_CHOCOLATE.get()), 1)
                .container(Ingredient.of(ModItems.CUP_GLASS.get()), 1)
                .experience(0.35F).cookingTime(160)
                .save(writer, id("coffee_brewing/coldbrew_latte_chocolate"));

        CoffeeBrewingRecipeBuilder.brewing(new ItemStack(ModItems.COFFEE_COLDBREW_LATTE_FRUIT.get()))
                .base(Ingredient.of(ModItems.COLDBREW_BOTTLE.get()), 1)
                .modifier(Ingredient.of(Items.MILK_BUCKET), 1)
                .additive(Ingredient.of(ModItems.SYRUP_FRUIT.get()), 1)
                .container(Ingredient.of(ModItems.CUP_GLASS.get()), 1)
                .experience(0.35F).cookingTime(160)
                .save(writer, id("coffee_brewing/coldbrew_latte_fruit"));

        CoffeeBrewingRecipeBuilder.brewing(new ItemStack(ModItems.COFFEE_COLDBREW_LATTE_MINT.get()))
                .base(Ingredient.of(ModItems.COLDBREW_BOTTLE.get()), 1)
                .modifier(Ingredient.of(Items.MILK_BUCKET), 1)
                .additive(Ingredient.of(ModItems.SYRUP_MINT.get()), 1)
                .container(Ingredient.of(ModItems.CUP_GLASS.get()), 1)
                .experience(0.35F).cookingTime(160)
                .save(writer, id("coffee_brewing/coldbrew_latte_mint"));

        CoffeeBrewingRecipeBuilder.brewing(new ItemStack(ModItems.COFFEE_COLDBREW_LATTE_VANILLA.get()))
                .base(Ingredient.of(ModItems.COLDBREW_BOTTLE.get()), 1)
                .modifier(Ingredient.of(Items.MILK_BUCKET), 1)
                .additive(Ingredient.of(ModItems.SYRUP_VANILLA.get()), 1)
                .container(Ingredient.of(ModItems.CUP_GLASS.get()), 1)
                .experience(0.35F).cookingTime(160)
                .save(writer, id("coffee_brewing/coldbrew_latte_vanilla"));

        // Iced Coldbrew Fruit: coldbrew_bottle + ice_slag + cup_glass
        // (Note: same signature as Iced Coldbrew from P0 — skip to avoid ambiguity)
        // COFFEE_COLDBREW_FRUIT_ICE: same signature as coldbrew_fruit (both use cup_glass),
        // making them ambiguous. Skip for now — will be resolved when slot system expands.

        // Iced Coldbrew Latte variants: same slot limitation — skip iced coldbrew latte
        // variants (need both modifier + additive + ice_slag, but only 1 additive slot).
    }

    private static ResourceLocation id(String path) {
        return CoffeeWork.id(path);
    }
}
