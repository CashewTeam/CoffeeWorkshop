package net.langball.coffee.datagen;

import net.langball.coffee.CoffeeWork;
import net.langball.coffee.init.ModBlocks;
import net.langball.coffee.init.ModItems;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Blocks;

import java.util.function.Consumer;

public class ModRecipeProvider extends RecipeProvider {

    public ModRecipeProvider(PackOutput output) {
        super(output);
    }

    @Override
    protected void buildRecipes(Consumer<FinishedRecipe> writer) {
        // ===================================================================
        // SMELTING (Furnace)
        // ===================================================================
        smelting(ModItems.COFFEE_BEAN_RAW.get(), ModItems.COFFEE_BEAN.get(), 0.35F, "coffee_bean").save(writer, modLoc("coffee_bean_from_raw"));
        smelting(Items.COCOA_BEANS, ModItems.COCOA_POWDER.get(), 0.35F, "cocoa_powder").save(writer, modLoc("cocoa_powder_from_beans"));

        // Dough → Baked goods
        smelting(ModItems.DOUGH_BREAD.get(), Items.BREAD, 0.35F, "bread").save(writer, modLoc("bread_from_dough_bread"));
        smelting(ModItems.DOUGH_BREAD_ROUND.get(), ModItems.BREAD_ROUND.get(), 0.35F, "bread_round").save(writer, modLoc("bread_round_from_dough"));
        smelting(ModItems.DOUGH_BAGUETTE.get(), ModItems.BAGUETTE.get(), 0.35F, "baguette").save(writer, modLoc("baguette_from_dough"));
        smelting(ModItems.DOUGH_BAGEL.get(), ModItems.BAGEL.get(), 0.35F, "bagel").save(writer, modLoc("bagel_from_dough"));
        smelting(ModItems.DOUGH_TOAST.get(), ModItems.TOAST.get(), 0.35F, "toast").save(writer, modLoc("toast_from_dough"));
        smelting(ModItems.DOUGH_COOKIE.get(), Items.COOKIE, 0.35F, "cookie").save(writer, modLoc("cookie_from_dough_cookie"));

        // Smelt plate_dough_ginger → gingerbread (using a generic output since we don't have dessert_1[8])
        // TODO: Add gingerbread item when porting dessert items
        // smelting(ModItems.PLATE_DOUGH_GINGER.get(), ???, 0.35F).save(writer, modLoc("gingerbread_from_plate"));

        // ===================================================================
        // SHAPED RECIPES
        // ===================================================================

        // --- Tools & Molds ---
        shaped(RecipeCategory.MISC, ModItems.EMPTY_COLDBREW_POT.get())
                .pattern(" I ")
                .pattern("WWW")
                .pattern("III")
                .define('I', ModItems.PLATE_IRON.get())
                .define('W', Items.GLASS_PANE)
                .save(writer, modLoc("empty_coldbrew_pot"));

        shaped(RecipeCategory.MISC, ModItems.CAKE_MODEL.get())
                .pattern(" I ")
                .pattern("IWI")
                .pattern(" I ")
                .define('I', Items.IRON_INGOT)
                .define('W', ModItems.PLATE_IRON.get())
                .save(writer, modLoc("cake_model"));

        shaped(RecipeCategory.MISC, ModItems.MIXING_BOWL.get())
                .pattern("I I")
                .pattern(" W ")
                .define('I', Items.IRON_INGOT)
                .define('W', ModItems.PLATE_IRON.get())
                .save(writer, modLoc("mixing_bowl"));

        shaped(RecipeCategory.MISC, ModItems.SMALL_MODEL.get(), 8)
                .pattern("I")
                .pattern("W")
                .define('I', Items.IRON_INGOT)
                .define('W', ModItems.PLATE_IRON.get())
                .save(writer, modLoc("small_model"));

        shaped(RecipeCategory.MISC, ModItems.IRON_BOWL.get())
                .pattern("I I")
                .pattern("W W")
                .pattern(" W ")
                .define('I', Items.IRON_INGOT)
                .define('W', ModItems.PLATE_IRON.get())
                .save(writer, modLoc("iron_bowl"));

        shaped(RecipeCategory.MISC, ModItems.MOONCAKE_MODEL.get())
                .pattern("IWI")
                .define('I', Items.IRON_INGOT)
                .define('W', ModItems.PLATE_IRON.get())
                .save(writer, modLoc("mooncake_model"));

        shaped(RecipeCategory.MISC, ModItems.CAKE_MODEL_PLATE.get())
                .pattern("IWI")
                .define('I', ModItems.PLATE_IRON.get())
                .define('W', Items.IRON_INGOT)
                .save(writer, modLoc("cake_model_plate"));

        shaped(RecipeCategory.MISC, ModItems.CAKE_MODEL_SQUARE.get())
                .pattern("IWI")
                .define('I', ModItems.PLATE_IRON.get())
                .define('W', ModItems.CAKE_MODEL_PLATE.get())
                .save(writer, modLoc("cake_model_square"));

        // --- Containers & Decor ---
        shaped(RecipeCategory.DECORATIONS, ModBlocks.PLATE.get())
                .pattern("   ")
                .pattern("WDW")
                .pattern(" W ")
                .define('W', Items.TERRACOTTA)
                .define('D', Items.WHITE_DYE)
                .save(writer, modLoc("plate"));

        shaped(RecipeCategory.MISC, ModItems.BAG_CLOTH.get(), 8)
                .pattern(" D ")
                .pattern("DWD")
                .pattern(" D ")
                .define('D', Items.STRING)
                .define('W', Blocks.WHITE_WOOL)
                .save(writer, modLoc("bag_cloth"));

        shaped(RecipeCategory.MISC, ModItems.BAG.get(), 4)
                .pattern(" W ")
                .pattern("DWD")
                .pattern(" W ")
                .define('W', ModItems.BAG_CLOTH.get())
                .define('D', Items.STRING)
                .save(writer, modLoc("bag"));

        shaped(RecipeCategory.MISC, ModItems.SYRUP_EMPTY.get(), 8)
                .pattern("WGW")
                .pattern("W W")
                .pattern(" W ")
                .define('W', Items.GLASS_PANE)
                .define('G', ItemTags.PLANKS)
                .save(writer, modLoc("syrup_empty"));

        // --- Machines ---
        shaped(RecipeCategory.MISC, ModBlocks.GRINDER.get())
                .pattern("LLL")
                .pattern("ISI")
                .pattern("LHL")
                .define('L', ItemTags.LOGS)
                .define('I', Items.IRON_INGOT)
                .define('S', Items.STONE)
                .define('H', Blocks.FURNACE)
                .save(writer, modLoc("grinder"));

        shaped(RecipeCategory.MISC, ModBlocks.ROLLER.get())
                .pattern("LIL")
                .pattern("IHI")
                .pattern("LIL")
                .define('L', ItemTags.LOGS)
                .define('I', Items.IRON_INGOT)
                .define('H', Blocks.FURNACE)
                .save(writer, modLoc("roller"));

        shaped(RecipeCategory.MISC, ModBlocks.COFFEE_MACHINE.get())
                .pattern("III")
                .pattern("ISI")
                .pattern("LHL")
                .define('I', ModItems.PLATE_IRON.get())
                .define('L', Items.REDSTONE_BLOCK)
                .define('S', Items.STONE)
                .define('H', Items.WATER_BUCKET)
                .save(writer, modLoc("coffee_machine"));

        shaped(RecipeCategory.MISC, ModBlocks.ICECREAM_MACHINE.get())
                .pattern("LLL")
                .pattern("ISI")
                .pattern("LHL")
                .define('L', ItemTags.LOGS)
                .define('I', Items.IRON_BLOCK)
                .define('S', Items.STONE)
                .define('H', Blocks.SNOW_BLOCK)
                .save(writer, modLoc("icecream_machine"));

        shaped(RecipeCategory.MISC, ModBlocks.OVEN.get())
                .pattern("WWW")
                .pattern("W W")
                .pattern("WBW")
                .define('W', Items.TERRACOTTA)
                .define('B', Blocks.FURNACE)
                .save(writer, modLoc("oven"));

        // --- Buildings & Decor ---
        shaped(RecipeCategory.DECORATIONS, ModBlocks.GINGER_HOUSE.get())
                .pattern(" W ")
                .pattern("WWW")
                .pattern("WWW")
                .define('W', ModItems.DOUGH_GINGER.get())
                .save(writer, modLoc("ginger_house"));

        // --- Drinks storage ---
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.COFFEE_INSTANT_BOX.get())
                .pattern("WWW")
                .pattern("WWW")
                .pattern("WWW")
                .define('W', ModItems.COFFEE_INSTANT.get())
                .save(writer, modLoc("coffee_instant_box"));

        // ===================================================================
        // SHAPELESS RECIPES
        // ===================================================================

        // --- Materials ---
        shapeless(RecipeCategory.FOOD, ModItems.DOUGH_PASTRY.get())
                .requires(ModItems.DOUGH.get())
                .requires(ModItems.BUTTER.get())
                .requires(Items.EGG)
                .save(writer, modLoc("dough_pastry"));

        shapeless(RecipeCategory.FOOD, ModItems.DOUGH_GINGER.get())
                .requires(ModItems.DOUGH.get())
                .requires(ModItems.SPICES.get())
                .save(writer, modLoc("dough_ginger"));

        shapeless(RecipeCategory.FOOD, ModItems.SPICES.get(), 4)
                .requires(Items.COCOA_BEANS)
                .requires(Items.COCOA_BEANS)
                .requires(Items.COCOA_BEANS)
                .requires(Items.COCOA_BEANS)
                .save(writer, modLoc("spices"));

        shapeless(RecipeCategory.MISC, ModItems.GELATIN.get(), 4)
                .requires(Items.SLIME_BALL)
                .requires(Items.SLIME_BALL)
                .requires(Items.WHITE_DYE)
                .save(writer, modLoc("gelatin"));

        // --- Seeds ---
        shapeless(RecipeCategory.MISC, ModItems.VANILLA_SEEDS.get(), 2)
                .requires(ModItems.VANILLA.get())
                .save(writer, modLoc("vanilla_seeds_from_vanilla"));

        // --- Dough variants ---
        shapeless(RecipeCategory.FOOD, ModItems.DOUGH.get())
                .requires(ModItems.MIXING_BOWL.get())
                .requires(ModItems.FLOUR.get())
                .requires(Items.MILK_BUCKET)
                .save(writer, modLoc("dough"));

        shapeless(RecipeCategory.FOOD, ModItems.DOUGH_BREAD.get())
                .requires(ModItems.DOUGH.get())
                .requires(ModItems.FLOUR.get())
                .save(writer, modLoc("dough_bread"));

        shapeless(RecipeCategory.FOOD, ModItems.DOUGH_BREAD_ROUND.get())
                .requires(ModItems.DOUGH.get())
                .save(writer, modLoc("dough_bread_round"));

        shapeless(RecipeCategory.FOOD, ModItems.DOUGH_BAGUETTE.get())
                .requires(ModItems.DOUGH.get())
                .requires(ModItems.DOUGH.get())
                .save(writer, modLoc("dough_baguette"));

        shapeless(RecipeCategory.FOOD, ModItems.DOUGH_BAGEL.get())
                .requires(ModItems.DOUGH.get())
                .requires(Items.EGG)
                .save(writer, modLoc("dough_bagel"));

        shapeless(RecipeCategory.FOOD, ModItems.DOUGH_TOAST.get())
                .requires(ModItems.DOUGH.get())
                .requires(ModItems.DOUGH.get())
                .requires(Items.EGG)
                .save(writer, modLoc("dough_toast"));

        shapeless(RecipeCategory.FOOD, ModItems.DOUGH_COOKIE.get())
                .requires(ModItems.DOUGH.get())
                .requires(ModItems.CHOCOLATE_CHIP.get())
                .save(writer, modLoc("dough_cookie"));

        // --- Dairy & Fermentation ---
        shapeless(RecipeCategory.FOOD, ModItems.YEAST.get(), 8)
                .requires(Items.BROWN_MUSHROOM)
                .requires(ModItems.MIXING_BOWL.get())
                .requires(Items.SUGAR)
                .save(writer, modLoc("yeast"));

        shapeless(RecipeCategory.FOOD, ModItems.BUTTER.get())
                .requires(ModItems.MIXING_BOWL.get())
                .requires(Items.MILK_BUCKET)
                .save(writer, modLoc("butter"));

        shapeless(RecipeCategory.FOOD, ModItems.CHEESE.get())
                .requires(ModItems.MIXING_BOWL.get())
                .requires(Items.MILK_BUCKET)
                .requires(ModItems.YEAST.get())
                .save(writer, modLoc("cheese"));

        // --- Sweets ---
        shapeless(RecipeCategory.FOOD, ModItems.CHOCOLATE_BAR.get())
                .requires(ModItems.COCOA_BATTER.get())
                .requires(ModItems.COCOA_BATTER.get())
                .requires(Items.SUGAR)
                .save(writer, modLoc("chocolate_bar"));

        shapeless(RecipeCategory.FOOD, ModItems.FIELD_RATION.get())
                .requires(ModItems.COCOA_BATTER.get())
                .requires(ModItems.COCOA_BATTER.get())
                .requires(Items.SUGAR)
                .requires(ModItems.FLOUR.get())
                .requires(ModItems.FLOUR.get())
                .save(writer, modLoc("field_ration"));

        shapeless(RecipeCategory.FOOD, ModItems.BROWNIE.get())
                .requires(ModItems.CAKE_MODEL_SQUARE.get())
                .requires(ModItems.COCOA_BATTER.get())
                .requires(ModItems.CHOCOLATE_CHIP.get())
                .save(writer, modLoc("brownie"));

        // --- Sandwiches ---
        shapeless(RecipeCategory.FOOD, ModItems.SANDWICH_BLT.get())
                .requires(Items.BREAD)
                .requires(Items.COOKED_PORKCHOP)
                .requires(Items.BEETROOT)    // lettuce substitute
                .requires(Items.BEETROOT)    // tomato substitute
                .save(writer, modLoc("sandwich_blt"));

        // --- Beverage items ---
        shapeless(RecipeCategory.FOOD, ModItems.COFFEE_INSTANT.get(), 9)
                .requires(ModItems.COFFEE_INSTANT_BOX.get())
                .save(writer, modLoc("coffee_instant_from_box"));

        // Cold Brew Pot (filled)
        shapeless(RecipeCategory.MISC, ModBlocks.COLD_BREW_POT.get())
                .requires(ModItems.COFFEE_POWDER.get())
                .requires(ModItems.COFFEE_POWDER.get())
                .requires(ModItems.COFFEE_POWDER.get())
                .requires(ModItems.COFFEE_POWDER.get())
                .requires(Items.WATER_BUCKET)
                .requires(ModItems.EMPTY_COLDBREW_POT.get())
                .save(writer, modLoc("coldbrew_pot"));

        // --- Records ---
        shapeless(RecipeCategory.MISC, ModItems.RECORD_BLANK.get())
                .requires(ModItems.PLATE_IRON.get())
                .requires(Items.BLACK_DYE)
                .requires(Items.BLACK_DYE)
                .save(writer, modLoc("record_blank"));

        shapeless(RecipeCategory.MISC, ModItems.RECORD_KUSA_NOSHI_TO_NE.get())
                .requires(ModItems.RECORD_BLANK.get())
                .requires(Items.BLACK_DYE)
                .save(writer, modLoc("record_kusa_noshi_to_ne"));

        shapeless(RecipeCategory.MISC, ModItems.RECORD_LAZY_LADY_KAGUYA.get())
                .requires(ModItems.RECORD_BLANK.get())
                .requires(Items.RED_DYE)
                .save(writer, modLoc("record_lazy_lady_kaguya"));

        shapeless(RecipeCategory.MISC, ModItems.RECORD_THE_GRIMOIRE_OF_MARISA.get())
                .requires(ModItems.RECORD_BLANK.get())
                .requires(Items.YELLOW_DYE)
                .save(writer, modLoc("record_the_grimoire_of_marisa"));

        // --- Decoration ---
        shapeless(RecipeCategory.DECORATIONS, ModBlocks.XMAS_TREE.get())
                .requires(ItemTags.SAPLINGS)
                .requires(ItemTags.LEAVES)
                .requires(ItemTags.LEAVES)
                .requires(Items.RED_DYE)
                .requires(Items.GREEN_DYE)
                .save(writer, modLoc("xmas_tree"));

        // ===================================================================
        // BAG STORAGE RECIPES
        // ===================================================================
        // Each bag type:   bag + 8 ingredient  →  bag_block
        //                   bag_block           →  8 ingredient
        //                   2 × bag_block       →  double_bag_block
        //                   double_bag_block    →  2 × bag_block

        // --- Flour ---
        registerBagRecipes(writer, ModBlocks.BAG_FLOUR.get(), ModBlocks.DOUBLE_BAG_FLOUR.get(), ModItems.FLOUR.get(), "flour");

        // --- Coffee Powder ---
        registerBagRecipes(writer, ModBlocks.BAG_COFFEE_POWDER.get(), ModBlocks.DOUBLE_BAG_COFFEE_POWDER.get(), ModItems.COFFEE_POWDER.get(), "coffee_powder");

        // --- Cocoa Beans ---
        registerBagRecipes(writer, ModBlocks.BAG_COCOA.get(), ModBlocks.DOUBLE_BAG_COCOA.get(), ModItems.COCOA_BEAN.get(), "cocoa");

        // --- Cocoa Powder ---
        registerBagRecipes(writer, ModBlocks.BAG_COCOA_POWDER.get(), ModBlocks.DOUBLE_BAG_COCOA_POWDER.get(), ModItems.COCOA_POWDER.get(), "cocoa_powder");

        // --- Sugar ---
        registerBagVanillaRecipes(writer, ModBlocks.BAG_SUGAR.get(), ModBlocks.DOUBLE_BAG_SUGAR.get(), Items.SUGAR, "sugar");

        // --- Coffee Beans (roasted) ---
        registerBagRecipes(writer, ModBlocks.BAG_COFFEE.get(), ModBlocks.DOUBLE_BAG_COFFEE.get(), ModItems.COFFEE_BEAN.get(), "coffee");

        // --- Raw Coffee Beans ---
        registerBagRecipes(writer, ModBlocks.BAG_COFFEE_RAW.get(), ModBlocks.DOUBLE_BAG_COFFEE_RAW.get(), ModItems.COFFEE_BEAN_RAW.get(), "coffee_raw");

        // ===================================================================
        // CAKE SPONGE RECIPES
        // ===================================================================
        // Simplified direct recipes (original used batter intermediates)

        shapeless(RecipeCategory.FOOD, ModBlocks.CAKE_SPONGE.get())
                .requires(ModItems.MIXING_BOWL.get())
                .requires(Items.EGG)
                .requires(Items.MILK_BUCKET)
                .requires(ModItems.FLOUR.get())
                .requires(Items.SUGAR)
                .save(writer, modLoc("cake_sponge"));

        shapeless(RecipeCategory.FOOD, ModBlocks.CAKE_SPONGE_CHOCOLATE.get())
                .requires(ModItems.MIXING_BOWL.get())
                .requires(Items.EGG)
                .requires(Items.MILK_BUCKET)
                .requires(ModItems.FLOUR.get())
                .requires(Items.SUGAR)
                .requires(ModItems.COCOA_POWDER.get())
                .save(writer, modLoc("cake_sponge_chocolate"));

        shapeless(RecipeCategory.FOOD, ModBlocks.CAKE_SPONGE_COFFEE.get())
                .requires(ModItems.MIXING_BOWL.get())
                .requires(Items.EGG)
                .requires(Items.MILK_BUCKET)
                .requires(ModItems.FLOUR.get())
                .requires(Items.SUGAR)
                .requires(ModItems.COFFEE_POWDER.get())
                .save(writer, modLoc("cake_sponge_coffee"));

        shapeless(RecipeCategory.FOOD, ModBlocks.CAKE_SPONGE_PUMPKIN.get())
                .requires(ModItems.MIXING_BOWL.get())
                .requires(Items.EGG)
                .requires(Items.MILK_BUCKET)
                .requires(ModItems.FLOUR.get())
                .requires(Items.SUGAR)
                .requires(Items.PUMPKIN)
                .save(writer, modLoc("cake_sponge_pumpkin"));

        shapeless(RecipeCategory.FOOD, ModBlocks.CAKE_SPONGE_CARROT.get())
                .requires(ModItems.MIXING_BOWL.get())
                .requires(Items.EGG)
                .requires(Items.MILK_BUCKET)
                .requires(ModItems.FLOUR.get())
                .requires(Items.SUGAR)
                .requires(Items.CARROT)
                .save(writer, modLoc("cake_sponge_carrot"));

        shapeless(RecipeCategory.FOOD, ModBlocks.CAKE_SPONGE_REDVELVET.get())
                .requires(ModItems.MIXING_BOWL.get())
                .requires(Items.EGG)
                .requires(Items.MILK_BUCKET)
                .requires(ModItems.FLOUR.get())
                .requires(Items.SUGAR)
                .requires(Items.RED_DYE)
                .save(writer, modLoc("cake_sponge_redvelvet"));

        shapeless(RecipeCategory.FOOD, ModBlocks.CAKE_SPONGE_LEMON.get())
                .requires(ModItems.MIXING_BOWL.get())
                .requires(Items.EGG)
                .requires(Items.MILK_BUCKET)
                .requires(ModItems.FLOUR.get())
                .requires(Items.SUGAR)
                .requires(Items.YELLOW_DYE)
                .save(writer, modLoc("cake_sponge_lemon"));

        shapeless(RecipeCategory.FOOD, ModBlocks.CAKE_SPONGE_TEA.get())
                .requires(ModItems.MIXING_BOWL.get())
                .requires(Items.EGG)
                .requires(Items.MILK_BUCKET)
                .requires(ModItems.FLOUR.get())
                .requires(Items.SUGAR)
                .requires(Items.GREEN_DYE)
                .save(writer, modLoc("cake_sponge_tea"));

        shapeless(RecipeCategory.FOOD, ModBlocks.CAKE_SPONGE_BERRY.get())
                .requires(ModItems.MIXING_BOWL.get())
                .requires(Items.EGG)
                .requires(Items.MILK_BUCKET)
                .requires(ModItems.FLOUR.get())
                .requires(Items.SUGAR)
                .requires(Items.SWEET_BERRIES)
                .save(writer, modLoc("cake_sponge_berry"));

        // ===================================================================
        // FINISHED CAKE RECIPES
        // ===================================================================

        // Vanilla Cake (replaces vanilla recipe)
        shapeless(RecipeCategory.FOOD, Blocks.CAKE)
                .requires(ModBlocks.CAKE_SPONGE.get())
                .requires(Items.SUGAR)
                .requires(Items.MILK_BUCKET)
                .requires(Items.SWEET_BERRIES)
                .save(writer, modLoc("cake_vanilla"));

        // Coffee Cake
        shapeless(RecipeCategory.FOOD, ModBlocks.CAKE_COFFEE.get())
                .requires(ModBlocks.CAKE_SPONGE_COFFEE.get())
                .requires(Items.MILK_BUCKET)
                .requires(Items.SUGAR)
                .requires(ModItems.COFFEE_POWDER.get())
                .save(writer, modLoc("cake_coffee"));

        // Harvest Cake (pumpkin + carrot)
        shapeless(RecipeCategory.FOOD, ModBlocks.CAKE_HARVEST.get())
                .requires(ModBlocks.CAKE_SPONGE_PUMPKIN.get())
                .requires(Items.MILK_BUCKET)
                .requires(Items.SUGAR)
                .requires(Items.CARROT)
                .save(writer, modLoc("cake_harvest"));

        // Berry Cake
        shapeless(RecipeCategory.FOOD, ModBlocks.CAKE_BERRY.get())
                .requires(ModBlocks.CAKE_SPONGE_BERRY.get())
                .requires(Items.MILK_BUCKET)
                .requires(Items.SUGAR)
                .requires(Items.SWEET_BERRIES)
                .save(writer, modLoc("cake_berry"));

        // Lemon Cake
        shapeless(RecipeCategory.FOOD, ModBlocks.CAKE_LEMON.get())
                .requires(ModBlocks.CAKE_SPONGE_LEMON.get())
                .requires(Items.MILK_BUCKET)
                .requires(Items.SUGAR)
                .requires(Items.YELLOW_DYE)
                .save(writer, modLoc("cake_lemon"));

        // Tea Cake
        shapeless(RecipeCategory.FOOD, ModBlocks.CAKE_TEA.get())
                .requires(ModBlocks.CAKE_SPONGE_TEA.get())
                .requires(Items.MILK_BUCKET)
                .requires(Items.SUGAR)
                .requires(Items.GREEN_DYE)
                .save(writer, modLoc("cake_tea"));

        // Red Velvet Cake
        shapeless(RecipeCategory.FOOD, ModBlocks.CAKE_REDVELVET.get())
                .requires(ModBlocks.CAKE_SPONGE_REDVELVET.get())
                .requires(Items.SUGAR)
                .requires(Items.MILK_BUCKET)
                .requires(ModItems.CHEESE.get())
                .save(writer, modLoc("cake_redvelvet"));

        // Cheese Cake
        shapeless(RecipeCategory.FOOD, ModBlocks.CAKE_CHEESE.get())
                .requires(ModItems.CAKE_MODEL.get())
                .requires(ModItems.CHEESE.get())
                .requires(Items.MILK_BUCKET)
                .requires(Items.SUGAR)
                .save(writer, modLoc("cake_cheese"));

        // Black Forest Cake (Schwarzwald)
        shapeless(RecipeCategory.FOOD, ModBlocks.CAKE_SCHWARZWALD.get())
                .requires(ModBlocks.CAKE_SPONGE_CHOCOLATE.get())
                .requires(Items.SUGAR)
                .requires(Items.MILK_BUCKET)
                .requires(ModItems.COCOA_POWDER.get())
                .save(writer, modLoc("cake_schwarzwald"));

        // ===================================================================
        // MOUSSE & TIRAMISU RECIPES
        // ===================================================================
        // Simplified: originally used icecream machine; now direct crafting

        shapeless(RecipeCategory.FOOD, ModBlocks.MOUSSE_BERRY.get())
                .requires(Items.MILK_BUCKET)
                .requires(Items.EGG)
                .requires(ModItems.GELATIN.get())
                .requires(ModItems.CAKE_MODEL.get())
                .requires(Items.SWEET_BERRIES)
                .save(writer, modLoc("mousse_berry"));

        shapeless(RecipeCategory.FOOD, ModBlocks.MOUSSE_CHOCOLATE.get())
                .requires(Items.MILK_BUCKET)
                .requires(Items.EGG)
                .requires(ModItems.GELATIN.get())
                .requires(ModItems.CAKE_MODEL.get())
                .requires(ModItems.COCOA_POWDER.get())
                .save(writer, modLoc("mousse_chocolate"));

        shapeless(RecipeCategory.FOOD, ModBlocks.MOUSSE_LEMON.get())
                .requires(Items.MILK_BUCKET)
                .requires(Items.EGG)
                .requires(ModItems.GELATIN.get())
                .requires(ModItems.CAKE_MODEL.get())
                .requires(Items.YELLOW_DYE)
                .save(writer, modLoc("mousse_lemon"));

        shapeless(RecipeCategory.FOOD, ModBlocks.MOUSSE_COFFEE.get())
                .requires(Items.MILK_BUCKET)
                .requires(Items.EGG)
                .requires(ModItems.GELATIN.get())
                .requires(ModItems.CAKE_MODEL.get())
                .requires(ModItems.COFFEE_POWDER.get())
                .save(writer, modLoc("mousse_coffee"));

        // Tiramisu
        shapeless(RecipeCategory.FOOD, ModBlocks.TIRAMISU.get())
                .requires(ModItems.CAKE_MODEL_SQUARE.get())
                .requires(ModBlocks.CAKE_SPONGE.get())
                .requires(ModItems.CHEESE.get())
                .requires(ModItems.ESPRESSO.get())
                .requires(Items.MILK_BUCKET)
                .requires(ModItems.COCOA_POWDER.get())
                .save(writer, modLoc("tiramisu"));

        // ===================================================================
        // PIE RECIPES
        // ===================================================================
        // Cream Pie
        shapeless(RecipeCategory.FOOD, ModItems.PIE_CREAM.get())
                .requires(ModItems.PLATE_DOUGH_PASTRY.get())
                .requires(Items.SUGAR)
                .requires(Items.MILK_BUCKET)
                .save(writer, modLoc("pie_cream"));

        // ===================================================================
        // ICE CREAM RECIPES
        // ===================================================================
        // Vanilla Ice Cream (simplified)
        shapeless(RecipeCategory.FOOD, ModItems.ICECREAM_VANILLA.get())
                .requires(Items.MILK_BUCKET)
                .requires(Items.SUGAR)
                .requires(ModItems.VANILLA.get())
                .requires(ModItems.ICE_SLAG.get())
                .save(writer, modLoc("icecream_vanilla"));

        // ===================================================================
        // PLATE DOUGH (Rolled dough)
        // ===================================================================
        // These were originally Roller machine recipes; manual 2x2 crafting as fallback

        shaped(RecipeCategory.FOOD, ModItems.PLATE_DOUGH.get())
                .pattern("DD")
                .define('D', ModItems.DOUGH.get())
                .save(writer, modLoc("plate_dough"));

        shaped(RecipeCategory.FOOD, ModItems.PLATE_DOUGH_PASTRY.get())
                .pattern("DD")
                .define('D', ModItems.DOUGH_PASTRY.get())
                .save(writer, modLoc("plate_dough_pastry"));

        shaped(RecipeCategory.FOOD, ModItems.PLATE_DOUGH_GINGER.get())
                .pattern("DD")
                .define('D', ModItems.DOUGH_GINGER.get())
                .save(writer, modLoc("plate_dough_ginger"));
    }

    // =======================================================================
    // HELPER METHODS
    // =======================================================================

    /** Shapeless convenience */
    private static ShapelessRecipeBuilder shapeless(RecipeCategory category, net.minecraft.world.level.ItemLike result) {
        return ShapelessRecipeBuilder.shapeless(category, result);
    }

    private static ShapelessRecipeBuilder shapeless(RecipeCategory category, net.minecraft.world.level.ItemLike result, int count) {
        return ShapelessRecipeBuilder.shapeless(category, result, count);
    }

    /** Shaped convenience */
    private static ShapedRecipeBuilder shaped(RecipeCategory category, net.minecraft.world.level.ItemLike result) {
        return ShapedRecipeBuilder.shaped(category, result);
    }

    private static ShapedRecipeBuilder shaped(RecipeCategory category, net.minecraft.world.level.ItemLike result, int count) {
        return ShapedRecipeBuilder.shaped(category, result, count);
    }

    /** Smelting convenience */
    private static SimpleCookingRecipeBuilder smelting(net.minecraft.world.level.ItemLike input, net.minecraft.world.level.ItemLike output, float xp, String group) {
        return SimpleCookingRecipeBuilder.smelting(
                Ingredient.of(input),
                RecipeCategory.FOOD,
                output,
                xp,
                200
        );
    }

    /**
     * Register 4 standard bag-related recipes:
     * 1. bag + 8×ingredient → bag_block
     * 2. bag_block → 8×ingredient
     * 3. 2×bag_block → double_bag_block
     * 4. double_bag_block → 2×bag_block
     */
    private void registerBagRecipes(Consumer<FinishedRecipe> writer,
                                    net.minecraft.world.level.ItemLike bagBlock,
                                    net.minecraft.world.level.ItemLike doubleBagBlock,
                                    net.minecraft.world.level.ItemLike ingredient,
                                    String name) {
        // Fill bag
        shapeless(RecipeCategory.DECORATIONS, bagBlock)
                .requires(ModItems.BAG.get())
                .requires(ingredient, 8)
                .save(writer, modLoc("bag_" + name));

        // Empty bag
        shapeless(RecipeCategory.MISC, ingredient, 8)
                .requires(bagBlock)
                .save(writer, modLoc(name + "_from_bag"));

        // Merge into double
        shapeless(RecipeCategory.DECORATIONS, doubleBagBlock)
                .requires(bagBlock)
                .requires(bagBlock)
                .save(writer, modLoc("double_bag_" + name));

        // Split double
        shapeless(RecipeCategory.DECORATIONS, bagBlock, 2)
                .requires(doubleBagBlock)
                .save(writer, modLoc(name + "_from_double_bag"));
    }

    /** Variant for vanilla items (Items.SUGAR) */
    private void registerBagVanillaRecipes(Consumer<FinishedRecipe> writer,
                                           net.minecraft.world.level.ItemLike bagBlock,
                                           net.minecraft.world.level.ItemLike doubleBagBlock,
                                           net.minecraft.world.level.ItemLike ingredient,
                                           String name) {
        shapeless(RecipeCategory.DECORATIONS, bagBlock)
                .requires(ModItems.BAG.get())
                .requires(ingredient, 8)
                .save(writer, modLoc("bag_" + name));

        shapeless(RecipeCategory.MISC, ingredient, 8)
                .requires(bagBlock)
                .save(writer, modLoc(name + "_from_bag"));

        shapeless(RecipeCategory.DECORATIONS, doubleBagBlock)
                .requires(bagBlock)
                .requires(bagBlock)
                .save(writer, modLoc("double_bag_" + name));

        shapeless(RecipeCategory.DECORATIONS, bagBlock, 2)
                .requires(doubleBagBlock)
                .save(writer, modLoc(name + "_from_double_bag"));
    }

    private static ResourceLocation modLoc(String path) {
        return new ResourceLocation(CoffeeWork.MODID, path);
    }
}
