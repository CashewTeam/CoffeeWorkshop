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
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.block.Blocks;

import java.util.function.Consumer;

public class ModRecipeProvider extends RecipeProvider {

    public ModRecipeProvider(PackOutput output) {
        super(output);
    }

    @Override
    protected void buildRecipes(Consumer<FinishedRecipe> writer) {
        // ===================================================================
        // SMELTING (Furnace) — only legacy paths not yet replaced by machines
        // ===================================================================
        // Cocoa beans → cocoa powder (keep as alternative to Grinder)
        smelting(Items.COCOA_BEANS, ModItems.COCOA_POWDER.get(), 0.35F, "cocoa_powder").save(writer, modLoc("cocoa_powder_from_beans"));

        // NOTE: coffee_bean_raw → coffee_bean smelting REMOVED — use Oven machine
        // NOTE: dough_bread → bread smelting REMOVED — use Oven machine
        // NOTE: all dough variant smelting REMOVED — use Oven machine

        // Smelt plate_dough_ginger → gingerbread (using a generic output since we don't have dessert_1[8])
        // TODO: Add gingerbread item when porting dessert items
        // smelting(ModItems.PLATE_DOUGH_GINGER.get(), ???, 0.35F).save(writer, modLoc("gingerbread_from_plate"));

        // ===================================================================
        // SHAPED RECIPES
        // ===================================================================

        // --- Tools & Molds ---
        shaped(RecipeCategory.MISC, ModItems.EMPTY_COLDBREW_POT.get(), ModItems.PLATE_IRON.get())
                .pattern(" I ")
                .pattern("WWW")
                .pattern("III")
                .define('I', ModItems.PLATE_IRON.get())
                .define('W', Items.GLASS_PANE)
                .save(writer, modLoc("empty_coldbrew_pot"));

        shaped(RecipeCategory.MISC, ModItems.CAKE_MODEL.get(), Items.IRON_INGOT)
                .pattern(" I ")
                .pattern("IWI")
                .pattern(" I ")
                .define('I', Items.IRON_INGOT)
                .define('W', ModItems.PLATE_IRON.get())
                .save(writer, modLoc("cake_model"));

        shaped(RecipeCategory.MISC, ModItems.MIXING_BOWL.get(), Items.IRON_INGOT)
                .pattern("I I")
                .pattern(" W ")
                .define('I', Items.IRON_INGOT)
                .define('W', ModItems.PLATE_IRON.get())
                .save(writer, modLoc("mixing_bowl"));

        shaped(RecipeCategory.MISC, ModItems.SMALL_MODEL.get(), 8, Items.IRON_INGOT)
                .pattern("I")
                .pattern("W")
                .define('I', Items.IRON_INGOT)
                .define('W', ModItems.PLATE_IRON.get())
                .save(writer, modLoc("small_model"));

        shaped(RecipeCategory.MISC, ModItems.IRON_BOWL.get(), Items.IRON_INGOT)
                .pattern("I I")
                .pattern("W W")
                .pattern(" W ")
                .define('I', Items.IRON_INGOT)
                .define('W', ModItems.PLATE_IRON.get())
                .save(writer, modLoc("iron_bowl"));

        shaped(RecipeCategory.MISC, ModItems.MOONCAKE_MODEL.get(), Items.IRON_INGOT)
                .pattern("IWI")
                .define('I', Items.IRON_INGOT)
                .define('W', ModItems.PLATE_IRON.get())
                .save(writer, modLoc("mooncake_model"));

        shaped(RecipeCategory.MISC, ModItems.CAKE_MODEL_PLATE.get(), ModItems.PLATE_IRON.get())
                .pattern("IWI")
                .define('I', ModItems.PLATE_IRON.get())
                .define('W', Items.IRON_INGOT)
                .save(writer, modLoc("cake_model_plate"));

        shaped(RecipeCategory.MISC, ModItems.CAKE_MODEL_SQUARE.get(), ModItems.CAKE_MODEL_PLATE.get())
                .pattern("IWI")
                .define('I', ModItems.PLATE_IRON.get())
                .define('W', ModItems.CAKE_MODEL_PLATE.get())
                .save(writer, modLoc("cake_model_square"));

        // --- Containers & Decor ---
        shaped(RecipeCategory.DECORATIONS, ModBlocks.PLATE.get(), Items.TERRACOTTA)
                .pattern("   ")
                .pattern("WDW")
                .pattern(" W ")
                .define('W', Items.TERRACOTTA)
                .define('D', Items.WHITE_DYE)
                .save(writer, modLoc("plate"));

        shaped(RecipeCategory.MISC, ModItems.BAG_CLOTH.get(), 8, Items.STRING)
                .pattern(" D ")
                .pattern("DWD")
                .pattern(" D ")
                .define('D', Items.STRING)
                .define('W', Blocks.WHITE_WOOL)
                .save(writer, modLoc("bag_cloth"));

        shaped(RecipeCategory.MISC, ModItems.BAG.get(), 4, ModItems.BAG_CLOTH.get())
                .pattern(" W ")
                .pattern("DWD")
                .pattern(" W ")
                .define('W', ModItems.BAG_CLOTH.get())
                .define('D', Items.STRING)
                .save(writer, modLoc("bag"));

        shaped(RecipeCategory.MISC, ModItems.SYRUP_EMPTY.get(), 8, Items.GLASS_PANE)
                .pattern("WGW")
                .pattern("W W")
                .pattern(" W ")
                .define('W', Items.GLASS_PANE)
                .define('G', ItemTags.PLANKS)
                .save(writer, modLoc("syrup_empty"));

        // Flavored syrups: syrup_empty + flavor ingredient + sugar (shapeless)
        shapeless(RecipeCategory.MISC, ModItems.SYRUP_CARAMEL.get(), ModItems.SYRUP_EMPTY.get())
                .requires(ModItems.SYRUP_EMPTY.get())
                .requires(Items.SUGAR)
                .requires(Items.SUGAR)
                .save(writer, modLoc("syrup_caramel"));

        shapeless(RecipeCategory.MISC, ModItems.SYRUP_CHOCOLATE.get(), ModItems.SYRUP_EMPTY.get())
                .requires(ModItems.SYRUP_EMPTY.get())
                .requires(ModItems.COCOA_POWDER.get())
                .requires(Items.SUGAR)
                .save(writer, modLoc("syrup_chocolate"));

        shapeless(RecipeCategory.MISC, ModItems.SYRUP_FRUIT.get(), ModItems.SYRUP_EMPTY.get())
                .requires(ModItems.SYRUP_EMPTY.get())
                .requires(Items.SWEET_BERRIES)
                .requires(Items.SUGAR)
                .save(writer, modLoc("syrup_fruit"));

        shapeless(RecipeCategory.MISC, ModItems.SYRUP_MINT.get(), ModItems.SYRUP_EMPTY.get())
                .requires(ModItems.SYRUP_EMPTY.get())
                .requires(Items.VINE)
                .requires(Items.SUGAR)
                .save(writer, modLoc("syrup_mint"));

        shapeless(RecipeCategory.MISC, ModItems.SYRUP_VANILLA.get(), ModItems.SYRUP_EMPTY.get())
                .requires(ModItems.SYRUP_EMPTY.get())
                .requires(ModItems.VANILLA.get())
                .requires(Items.SUGAR)
                .save(writer, modLoc("syrup_vanilla"));

        shapeless(RecipeCategory.MISC, ModItems.SYRUP_SAKURA.get(), ModItems.SYRUP_EMPTY.get())
                .requires(ModItems.SYRUP_EMPTY.get())
                .requires(Items.CHERRY_SAPLING)
                .requires(Items.SUGAR)
                .save(writer, modLoc("syrup_sakura"));

        // --- Machines (unlock with Iron Ingot) ---
        shaped(RecipeCategory.MISC, ModBlocks.GRINDER.get(), Items.IRON_INGOT)
                .pattern("LLL")
                .pattern("ISI")
                .pattern("LHL")
                .define('L', ItemTags.LOGS)
                .define('I', Items.IRON_INGOT)
                .define('S', Items.STONE)
                .define('H', Blocks.FURNACE)
                .save(writer, modLoc("grinder"));

        shaped(RecipeCategory.MISC, ModBlocks.ROLLER.get(), Items.IRON_INGOT)
                .pattern("LIL")
                .pattern("IHI")
                .pattern("LIL")
                .define('L', ItemTags.LOGS)
                .define('I', Items.IRON_INGOT)
                .define('H', Blocks.FURNACE)
                .save(writer, modLoc("roller"));

        shaped(RecipeCategory.MISC, ModBlocks.COFFEE_MACHINE.get(), ModItems.PLATE_IRON.get())
                .pattern("III")
                .pattern("ISI")
                .pattern("LHL")
                .define('I', ModItems.PLATE_IRON.get())
                .define('L', Items.REDSTONE_BLOCK)
                .define('S', Items.STONE)
                .define('H', Items.WATER_BUCKET)
                .save(writer, modLoc("coffee_machine"));

        shaped(RecipeCategory.MISC, ModBlocks.ICECREAM_MACHINE.get(), Items.IRON_INGOT)
                .pattern("LLL")
                .pattern("ISI")
                .pattern("LHL")
                .define('L', ItemTags.LOGS)
                .define('I', Items.IRON_BLOCK)
                .define('S', Items.STONE)
                .define('H', Blocks.SNOW_BLOCK)
                .save(writer, modLoc("icecream_machine"));

        shaped(RecipeCategory.MISC, ModBlocks.OVEN.get(), Items.TERRACOTTA)
                .pattern("WWW")
                .pattern("W W")
                .pattern("WBW")
                .define('W', Items.TERRACOTTA)
                .define('B', Blocks.FURNACE)
                .save(writer, modLoc("oven"));

        // --- Buildings & Decor ---
        shaped(RecipeCategory.DECORATIONS, ModBlocks.GINGER_HOUSE.get(), ModItems.DOUGH_GINGER.get())
                .pattern(" W ")
                .pattern("WWW")
                .pattern("WWW")
                .define('W', ModItems.DOUGH_GINGER.get())
                .save(writer, modLoc("ginger_house"));

        // Ginger house from plate_dough_ginger (2×2, more efficient)
        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, ModBlocks.GINGER_HOUSE.get())
                .pattern("WW")
                .pattern("WW")
                .define('W', ModItems.PLATE_DOUGH_GINGER.get())
                .unlockedBy("has_item", has(ModItems.PLATE_DOUGH_GINGER.get()))
                .save(writer, modLoc("ginger_house_from_plate"));

        // --- Drinks storage ---
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.COFFEE_INSTANT_BOX.get())
                .pattern("WWW")
                .pattern("WWW")
                .pattern("WWW")
                .define('W', ModItems.COFFEE_INSTANT.get())
                .unlockedBy("has_item", has(ModItems.COFFEE_INSTANT.get()))
                .save(writer, modLoc("coffee_instant_box"));

        // ===================================================================
        // SHAPELESS RECIPES
        // ===================================================================

        // --- Materials ---
        shapeless(RecipeCategory.FOOD, ModItems.DOUGH_PASTRY.get(), ModItems.DOUGH.get())
                .requires(ModItems.DOUGH.get())
                .requires(ModItems.BUTTER.get())
                .requires(Items.EGG)
                .save(writer, modLoc("dough_pastry"));

        shapeless(RecipeCategory.FOOD, ModItems.DOUGH_GINGER.get(), ModItems.DOUGH.get())
                .requires(ModItems.DOUGH.get())
                .requires(ModItems.SPICES.get())
                .save(writer, modLoc("dough_ginger"));

        shapeless(RecipeCategory.FOOD, ModItems.SPICES.get(), 4, Items.COCOA_BEANS)
                .requires(Items.COCOA_BEANS)
                .requires(Items.COCOA_BEANS)
                .requires(Items.COCOA_BEANS)
                .requires(Items.COCOA_BEANS)
                .save(writer, modLoc("spices"));

        shapeless(RecipeCategory.MISC, ModItems.GELATIN.get(), 4, Items.SLIME_BALL)
                .requires(Items.SLIME_BALL)
                .requires(Items.SLIME_BALL)
                .requires(Items.WHITE_DYE)
                .save(writer, modLoc("gelatin"));

        // --- Seeds ---
        shapeless(RecipeCategory.MISC, ModItems.VANILLA_SEEDS.get(), 2, ModItems.VANILLA.get())
                .requires(ModItems.VANILLA.get())
                .save(writer, modLoc("vanilla_seeds_from_vanilla"));

        // --- Dough variants ---
        shapeless(RecipeCategory.FOOD, ModItems.DOUGH.get(), ModItems.FLOUR.get())
                .requires(ModItems.MIXING_BOWL.get())
                .requires(ModItems.FLOUR.get())
                .requires(Items.MILK_BUCKET)
                .save(writer, modLoc("dough"));

        shapeless(RecipeCategory.FOOD, ModItems.DOUGH_BREAD.get(), ModItems.DOUGH.get())
                .requires(ModItems.DOUGH.get())
                .requires(ModItems.FLOUR.get())
                .save(writer, modLoc("dough_bread"));

        shapeless(RecipeCategory.FOOD, ModItems.DOUGH_BREAD_ROUND.get(), ModItems.DOUGH.get())
                .requires(ModItems.DOUGH.get())
                .save(writer, modLoc("dough_bread_round"));

        shapeless(RecipeCategory.FOOD, ModItems.DOUGH_BAGUETTE.get(), ModItems.DOUGH.get())
                .requires(ModItems.DOUGH.get())
                .requires(ModItems.DOUGH.get())
                .save(writer, modLoc("dough_baguette"));

        shapeless(RecipeCategory.FOOD, ModItems.DOUGH_BAGEL.get(), ModItems.DOUGH.get())
                .requires(ModItems.DOUGH.get())
                .requires(Items.EGG)
                .save(writer, modLoc("dough_bagel"));

        shapeless(RecipeCategory.FOOD, ModItems.DOUGH_TOAST.get(), ModItems.DOUGH.get())
                .requires(ModItems.DOUGH.get())
                .requires(ModItems.DOUGH.get())
                .requires(Items.EGG)
                .save(writer, modLoc("dough_toast"));

        shapeless(RecipeCategory.FOOD, ModItems.DOUGH_COOKIE.get(), ModItems.DOUGH.get())
                .requires(ModItems.DOUGH.get())
                .requires(ModItems.CHOCOLATE_CHIP.get())
                .save(writer, modLoc("dough_cookie"));

        // --- Dairy & Fermentation ---
        shapeless(RecipeCategory.FOOD, ModItems.YEAST.get(), 8, ModItems.MIXING_BOWL.get())
                .requires(Items.BROWN_MUSHROOM)
                .requires(ModItems.MIXING_BOWL.get())
                .requires(Items.SUGAR)
                .save(writer, modLoc("yeast"));

        shapeless(RecipeCategory.FOOD, ModItems.BUTTER.get(), ModItems.MIXING_BOWL.get())
                .requires(ModItems.MIXING_BOWL.get())
                .requires(Items.MILK_BUCKET)
                .save(writer, modLoc("butter"));

        shapeless(RecipeCategory.FOOD, ModItems.CHEESE.get(), ModItems.MIXING_BOWL.get())
                .requires(ModItems.MIXING_BOWL.get())
                .requires(Items.MILK_BUCKET)
                .requires(ModItems.YEAST.get())
                .save(writer, modLoc("cheese"));

        // --- Sweets ---
        shapeless(RecipeCategory.FOOD, ModItems.CHOCOLATE_BAR.get(), ModItems.COCOA_BATTER.get())
                .requires(ModItems.COCOA_BATTER.get())
                .requires(ModItems.COCOA_BATTER.get())
                .requires(Items.SUGAR)
                .save(writer, modLoc("chocolate_bar"));

        shapeless(RecipeCategory.FOOD, ModItems.FIELD_RATION.get(), ModItems.COCOA_BATTER.get())
                .requires(ModItems.COCOA_BATTER.get())
                .requires(ModItems.COCOA_BATTER.get())
                .requires(Items.SUGAR)
                .requires(ModItems.FLOUR.get())
                .requires(ModItems.FLOUR.get())
                .save(writer, modLoc("field_ration"));

        shapeless(RecipeCategory.FOOD, ModItems.BROWNIE.get(), ModItems.CAKE_MODEL_SQUARE.get())
                .requires(ModItems.CAKE_MODEL_SQUARE.get())
                .requires(ModItems.COCOA_BATTER.get())
                .requires(ModItems.CHOCOLATE_CHIP.get())
                .save(writer, modLoc("brownie"));

        // --- Sandwiches ---
        shapeless(RecipeCategory.FOOD, ModItems.SANDWICH_BLT.get(), Items.BREAD)
                .requires(Items.BREAD)
                .requires(Items.COOKED_PORKCHOP)
                .requires(Items.BEETROOT)    // lettuce substitute
                .requires(Items.BEETROOT)    // tomato substitute
                .save(writer, modLoc("sandwich_blt"));

        // --- Beverage items ---
        // Instant coffee: coffee_powder + water + stir_stick (returns bucket)
        shapeless(RecipeCategory.FOOD, ModItems.COFFEE_INSTANT.get(), ModItems.COFFEE_INSTANT_STICK.get())
                .requires(ModItems.COFFEE_POWDER.get())
                .requires(Items.WATER_BUCKET)
                .requires(ModItems.COFFEE_INSTANT_STICK.get())
                .save(writer, modLoc("coffee_instant"));

        // Instant coffee from box
        shapeless(RecipeCategory.FOOD, ModItems.COFFEE_INSTANT.get(), 9, ModItems.COFFEE_INSTANT_BOX.get())
                .requires(ModItems.COFFEE_INSTANT_BOX.get())
                .save(writer, modLoc("coffee_instant_from_box"));

        // Cup (paper cup for hot drinks)
        shaped(RecipeCategory.MISC, ModItems.CUP.get(), 4, Items.PAPER)
                .pattern("P P")
                .pattern(" P ")
                .define('P', Items.PAPER)
                .save(writer, modLoc("cup"));

        // Glass cup (for iced/tea drinks)
        shaped(RecipeCategory.MISC, ModItems.CUP_GLASS.get(), 4, Items.GLASS_PANE)
                .pattern("G G")
                .pattern(" G ")
                .define('G', Items.GLASS_PANE)
                .save(writer, modLoc("cup_glass"));

        // Tea leaves
        shaped(RecipeCategory.MISC, ModItems.TEA_LEAF.get(), 4, Items.OAK_LEAVES)
                .pattern("LL")
                .define('L', Items.OAK_LEAVES)
                .save(writer, modLoc("tea_leaf"));

        // Cocoa bean (mod item) from vanilla cocoa beans
        smelting(Items.COCOA_BEANS, ModItems.COCOA_BEAN.get(), 0.1F, "cocoa_bean")
                .save(writer, modLoc("cocoa_bean"));

        // Coffee instant stir stick from vanilla stick
        shaped(RecipeCategory.MISC, ModItems.COFFEE_INSTANT_STICK.get(), 4, Items.STICK)
                .pattern("S")
                .pattern("P")
                .define('S', Items.STICK)
                .define('P', Items.PAPER)
                .save(writer, modLoc("coffee_instant_stick"));

        // Black tea leaf (smelt green tea leaf)
        smelting(ModItems.TEA_LEAF.get(), ModItems.BLACK_TEA_LEAF.get(), 0.1F, "black_tea_leaf")
                .save(writer, modLoc("black_tea_leaf"));

        // Cold Brew Pot (filled)
        shapeless(RecipeCategory.MISC, ModBlocks.COLD_BREW_POT.get(), ModItems.EMPTY_COLDBREW_POT.get())
                .requires(ModItems.COFFEE_POWDER.get())
                .requires(ModItems.COFFEE_POWDER.get())
                .requires(ModItems.COFFEE_POWDER.get())
                .requires(ModItems.COFFEE_POWDER.get())
                .requires(Items.WATER_BUCKET)
                .requires(ModItems.EMPTY_COLDBREW_POT.get())
                .save(writer, modLoc("coldbrew_pot"));

        // --- Records ---
        shapeless(RecipeCategory.MISC, ModItems.RECORD_BLANK.get(), ModItems.PLATE_IRON.get())
                .requires(ModItems.PLATE_IRON.get())
                .requires(Items.BLACK_DYE)
                .requires(Items.BLACK_DYE)
                .save(writer, modLoc("record_blank"));

        shapeless(RecipeCategory.MISC, ModItems.RECORD_KUSA_NOSHI_TO_NE.get(), ModItems.RECORD_BLANK.get())
                .requires(ModItems.RECORD_BLANK.get())
                .requires(Items.BLACK_DYE)
                .save(writer, modLoc("record_kusa_noshi_to_ne"));

        shapeless(RecipeCategory.MISC, ModItems.RECORD_LAZY_LADY_KAGUYA.get(), ModItems.RECORD_BLANK.get())
                .requires(ModItems.RECORD_BLANK.get())
                .requires(Items.RED_DYE)
                .save(writer, modLoc("record_lazy_lady_kaguya"));

        shapeless(RecipeCategory.MISC, ModItems.RECORD_THE_GRIMOIRE_OF_MARISA.get(), ModItems.RECORD_BLANK.get())
                .requires(ModItems.RECORD_BLANK.get())
                .requires(Items.YELLOW_DYE)
                .save(writer, modLoc("record_the_grimoire_of_marisa"));

        // --- Decoration ---
        shapeless(RecipeCategory.DECORATIONS, ModBlocks.XMAS_TREE.get(), Items.SPRUCE_SAPLING)
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

        shapeless(RecipeCategory.FOOD, ModBlocks.CAKE_SPONGE.get(), Items.EGG)
                .requires(ModItems.MIXING_BOWL.get())
                .requires(Items.EGG)
                .requires(Items.MILK_BUCKET)
                .requires(ModItems.FLOUR.get())
                .requires(Items.SUGAR)
                .save(writer, modLoc("cake_sponge"));

        shapeless(RecipeCategory.FOOD, ModBlocks.CAKE_SPONGE_CHOCOLATE.get(), Items.EGG)
                .requires(ModItems.MIXING_BOWL.get())
                .requires(Items.EGG)
                .requires(Items.MILK_BUCKET)
                .requires(ModItems.FLOUR.get())
                .requires(Items.SUGAR)
                .requires(ModItems.COCOA_POWDER.get())
                .save(writer, modLoc("cake_sponge_chocolate"));

        shapeless(RecipeCategory.FOOD, ModBlocks.CAKE_SPONGE_COFFEE.get(), Items.EGG)
                .requires(ModItems.MIXING_BOWL.get())
                .requires(Items.EGG)
                .requires(Items.MILK_BUCKET)
                .requires(ModItems.FLOUR.get())
                .requires(Items.SUGAR)
                .requires(ModItems.COFFEE_POWDER.get())
                .save(writer, modLoc("cake_sponge_coffee"));

        shapeless(RecipeCategory.FOOD, ModBlocks.CAKE_SPONGE_PUMPKIN.get(), Items.EGG)
                .requires(ModItems.MIXING_BOWL.get())
                .requires(Items.EGG)
                .requires(Items.MILK_BUCKET)
                .requires(ModItems.FLOUR.get())
                .requires(Items.SUGAR)
                .requires(Items.PUMPKIN)
                .save(writer, modLoc("cake_sponge_pumpkin"));

        shapeless(RecipeCategory.FOOD, ModBlocks.CAKE_SPONGE_CARROT.get(), Items.EGG)
                .requires(ModItems.MIXING_BOWL.get())
                .requires(Items.EGG)
                .requires(Items.MILK_BUCKET)
                .requires(ModItems.FLOUR.get())
                .requires(Items.SUGAR)
                .requires(Items.CARROT)
                .save(writer, modLoc("cake_sponge_carrot"));

        shapeless(RecipeCategory.FOOD, ModBlocks.CAKE_SPONGE_REDVELVET.get(), Items.EGG)
                .requires(ModItems.MIXING_BOWL.get())
                .requires(Items.EGG)
                .requires(Items.MILK_BUCKET)
                .requires(ModItems.FLOUR.get())
                .requires(Items.SUGAR)
                .requires(Items.RED_DYE)
                .save(writer, modLoc("cake_sponge_redvelvet"));

        shapeless(RecipeCategory.FOOD, ModBlocks.CAKE_SPONGE_LEMON.get(), Items.EGG)
                .requires(ModItems.MIXING_BOWL.get())
                .requires(Items.EGG)
                .requires(Items.MILK_BUCKET)
                .requires(ModItems.FLOUR.get())
                .requires(Items.SUGAR)
                .requires(Items.YELLOW_DYE)
                .save(writer, modLoc("cake_sponge_lemon"));

        shapeless(RecipeCategory.FOOD, ModBlocks.CAKE_SPONGE_TEA.get(), Items.EGG)
                .requires(ModItems.MIXING_BOWL.get())
                .requires(Items.EGG)
                .requires(Items.MILK_BUCKET)
                .requires(ModItems.FLOUR.get())
                .requires(Items.SUGAR)
                .requires(Items.GREEN_DYE)
                .save(writer, modLoc("cake_sponge_tea"));

        shapeless(RecipeCategory.FOOD, ModBlocks.CAKE_SPONGE_BERRY.get(), Items.EGG)
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
        shapeless(RecipeCategory.FOOD, Blocks.CAKE, ModBlocks.CAKE_SPONGE.get())
                .requires(ModBlocks.CAKE_SPONGE.get())
                .requires(Items.SUGAR)
                .requires(Items.MILK_BUCKET)
                .requires(Items.SWEET_BERRIES)
                .save(writer, modLoc("cake_vanilla"));

        // Coffee Cake
        shapeless(RecipeCategory.FOOD, ModBlocks.CAKE_COFFEE.get(), ModBlocks.CAKE_SPONGE_COFFEE.get())
                .requires(ModBlocks.CAKE_SPONGE_COFFEE.get())
                .requires(Items.MILK_BUCKET)
                .requires(Items.SUGAR)
                .requires(ModItems.COFFEE_POWDER.get())
                .save(writer, modLoc("cake_coffee"));

        // Harvest Cake (pumpkin + carrot)
        shapeless(RecipeCategory.FOOD, ModBlocks.CAKE_HARVEST.get(), ModBlocks.CAKE_SPONGE_PUMPKIN.get())
                .requires(ModBlocks.CAKE_SPONGE_PUMPKIN.get())
                .requires(Items.MILK_BUCKET)
                .requires(Items.SUGAR)
                .requires(Items.CARROT)
                .save(writer, modLoc("cake_harvest"));

        // Berry Cake
        shapeless(RecipeCategory.FOOD, ModBlocks.CAKE_BERRY.get(), ModBlocks.CAKE_SPONGE_BERRY.get())
                .requires(ModBlocks.CAKE_SPONGE_BERRY.get())
                .requires(Items.MILK_BUCKET)
                .requires(Items.SUGAR)
                .requires(Items.SWEET_BERRIES)
                .save(writer, modLoc("cake_berry"));

        // Lemon Cake
        shapeless(RecipeCategory.FOOD, ModBlocks.CAKE_LEMON.get(), ModBlocks.CAKE_SPONGE_LEMON.get())
                .requires(ModBlocks.CAKE_SPONGE_LEMON.get())
                .requires(Items.MILK_BUCKET)
                .requires(Items.SUGAR)
                .requires(Items.YELLOW_DYE)
                .save(writer, modLoc("cake_lemon"));

        // Tea Cake
        shapeless(RecipeCategory.FOOD, ModBlocks.CAKE_TEA.get(), ModBlocks.CAKE_SPONGE_TEA.get())
                .requires(ModBlocks.CAKE_SPONGE_TEA.get())
                .requires(Items.MILK_BUCKET)
                .requires(Items.SUGAR)
                .requires(Items.GREEN_DYE)
                .save(writer, modLoc("cake_tea"));

        // Red Velvet Cake
        shapeless(RecipeCategory.FOOD, ModBlocks.CAKE_REDVELVET.get(), ModBlocks.CAKE_SPONGE_REDVELVET.get())
                .requires(ModBlocks.CAKE_SPONGE_REDVELVET.get())
                .requires(Items.SUGAR)
                .requires(Items.MILK_BUCKET)
                .requires(ModItems.CHEESE.get())
                .save(writer, modLoc("cake_redvelvet"));

        // Cheese Cake
        shapeless(RecipeCategory.FOOD, ModBlocks.CAKE_CHEESE.get(), ModItems.CAKE_MODEL.get())
                .requires(ModItems.CAKE_MODEL.get())
                .requires(ModItems.CHEESE.get())
                .requires(Items.MILK_BUCKET)
                .requires(Items.SUGAR)
                .save(writer, modLoc("cake_cheese"));

        // Black Forest Cake (Schwarzwald)
        shapeless(RecipeCategory.FOOD, ModBlocks.CAKE_SCHWARZWALD.get(), ModBlocks.CAKE_SPONGE_CHOCOLATE.get())
                .requires(ModBlocks.CAKE_SPONGE_CHOCOLATE.get())
                .requires(Items.SUGAR)
                .requires(Items.MILK_BUCKET)
                .requires(ModItems.COCOA_POWDER.get())
                .save(writer, modLoc("cake_schwarzwald"));

        // ===================================================================
        // MOUSSE & TIRAMISU RECIPES
        // ===================================================================
        // Simplified: originally used icecream machine; now direct crafting

        shapeless(RecipeCategory.FOOD, ModBlocks.MOUSSE_BERRY.get(), ModItems.CAKE_MODEL.get())
                .requires(Items.MILK_BUCKET)
                .requires(Items.EGG)
                .requires(ModItems.GELATIN.get())
                .requires(ModItems.CAKE_MODEL.get())
                .requires(Items.SWEET_BERRIES)
                .save(writer, modLoc("mousse_berry"));

        shapeless(RecipeCategory.FOOD, ModBlocks.MOUSSE_CHOCOLATE.get(), ModItems.CAKE_MODEL.get())
                .requires(Items.MILK_BUCKET)
                .requires(Items.EGG)
                .requires(ModItems.GELATIN.get())
                .requires(ModItems.CAKE_MODEL.get())
                .requires(ModItems.COCOA_POWDER.get())
                .save(writer, modLoc("mousse_chocolate"));

        shapeless(RecipeCategory.FOOD, ModBlocks.MOUSSE_LEMON.get(), ModItems.CAKE_MODEL.get())
                .requires(Items.MILK_BUCKET)
                .requires(Items.EGG)
                .requires(ModItems.GELATIN.get())
                .requires(ModItems.CAKE_MODEL.get())
                .requires(Items.YELLOW_DYE)
                .save(writer, modLoc("mousse_lemon"));

        shapeless(RecipeCategory.FOOD, ModBlocks.MOUSSE_COFFEE.get(), ModItems.CAKE_MODEL.get())
                .requires(Items.MILK_BUCKET)
                .requires(Items.EGG)
                .requires(ModItems.GELATIN.get())
                .requires(ModItems.CAKE_MODEL.get())
                .requires(ModItems.COFFEE_POWDER.get())
                .save(writer, modLoc("mousse_coffee"));

        // Tiramisu
        shapeless(RecipeCategory.FOOD, ModBlocks.TIRAMISU.get(), ModBlocks.CAKE_SPONGE.get())
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
        shapeless(RecipeCategory.FOOD, ModItems.PIE_CREAM.get(), ModItems.PLATE_DOUGH_PASTRY.get())
                .requires(ModItems.PLATE_DOUGH_PASTRY.get())
                .requires(Items.SUGAR)
                .requires(Items.MILK_BUCKET)
                .save(writer, modLoc("pie_cream"));

        // ===================================================================
        // ICE CREAM RECIPES
        // ===================================================================
        // Vanilla Ice Cream Mix (workbench → machine)
        shapeless(RecipeCategory.FOOD, ModItems.ICECREAM_MIX_VANILLA.get(), Items.MILK_BUCKET)
                .requires(ModItems.MIXING_BOWL.get())
                .requires(Items.MILK_BUCKET)
                .requires(Items.SUGAR)
                .requires(ModItems.VANILLA.get())
                .save(writer, modLoc("icecream_mix_vanilla"));

        // NOTE: plate_dough, plate_dough_pastry, plate_dough_ginger crafting
        // fallbacks REMOVED — use Roller machine instead

        // ===================================================================
        // COOLING RECIPES (hot drink + ice_slag → iced drink)
        // These handle iced variants that cannot be expressed in the Coffee
        // Machine because they would need both a flavor additive AND ice_slag
        // simultaneously (single additive slot limitation).
        // ===================================================================

        // ===================================================================
        // COOLING RECIPES (hot drink + ice_slag → iced drink)

        // Nitro Fruit Americano: nitro_ice + fruit_syrup → nitro_fruit_ice
        // Uses DrinkTransformRecipe to preserve remaining_cups NBT.
        {
            var sourceItem = ModItems.COFFEE_AMERICANO_NITRO_ICE.get();
            var additiveItem = ModItems.SYRUP_FRUIT.get();
            var resultItem = ModItems.COFFEE_AMERICANO_NITRO_FRUIT_ICE.get();
            final ResourceLocation sourceId = net.minecraft.core.registries.BuiltInRegistries.ITEM.getKey(sourceItem);
            final ResourceLocation additiveId = net.minecraft.core.registries.BuiltInRegistries.ITEM.getKey(additiveItem);
            final ResourceLocation resultId = net.minecraft.core.registries.BuiltInRegistries.ITEM.getKey(resultItem);
            final ResourceLocation recipeId = modLoc("drink_transform/coffee_americano_nitro_fruit_ice");
            writer.accept(new FinishedRecipe() {
                @Override
                public void serializeRecipeData(com.google.gson.JsonObject json) {
                    json.addProperty("source", sourceId.toString());
                    json.addProperty("additive", additiveId.toString());
                    json.addProperty("result", resultId.toString());
                }
                @Override
                public ResourceLocation getId() { return recipeId; }
                @Override
                public RecipeSerializer<?> getType() {
                    return net.langball.coffee.init.ModRecipeTypes.DRINK_TRANSFORM_SERIALIZER.get();
                }
                @Override @org.jetbrains.annotations.Nullable
                public ResourceLocation getAdvancementId() { return null; }
                @Override @org.jetbrains.annotations.Nullable
                public com.google.gson.JsonObject serializeAdvancement() { return null; }
            });
        }

        // ===================================================================
        // COOLING RECIPES (custom CoolingRecipe serializer, NBT-preserving)
        // Hot drink + ice_slag → iced drink with cups NBT copied.
        // ===================================================================
        registerCoolingRecipes(writer);

        // ===================================================================
        // MACHINE RECIPES (Grinder, Oven, Roller, Icecream Machine)
        // ===================================================================
        ModMachineRecipeProvider.buildRecipes(writer);
    }

    private void registerCoolingRecipes(Consumer<FinishedRecipe> writer) {
        // hot_drink → iced_drink mappings.
        // NOTE: americano_nitro_fruit_ice is crafted via shapeless recipe:
        // coffee_americano_nitro_ice + syrup_fruit → coffee_americano_nitro_fruit_ice
        // (matches the 1.12.2 workbench recipe pattern)
        var mappings = new Object[][]{
                {ModItems.COFFEE_COLDBREW_FRUIT, ModItems.COFFEE_COLDBREW_FRUIT_ICE},
                {ModItems.COFFEE_COLDBREW_LATTE_CARAMEL, ModItems.COFFEE_COLDBREW_LATTE_CARAMEL_ICE},
                {ModItems.COFFEE_COLDBREW_LATTE_CHOCOLATE, ModItems.COFFEE_COLDBREW_LATTE_CHOCOLATE_ICE},
                {ModItems.COFFEE_COLDBREW_LATTE_FRUIT, ModItems.COFFEE_COLDBREW_LATTE_FRUIT_ICE},
                {ModItems.COFFEE_COLDBREW_LATTE_MINT, ModItems.COFFEE_COLDBREW_LATTE_MINT_ICE},
                {ModItems.COFFEE_COLDBREW_LATTE_VANILLA, ModItems.COFFEE_COLDBREW_LATTE_VANILLA_ICE},
                {ModItems.COFFEE_MANDARIN_DRINK, ModItems.COFFEE_MANDARIN_DRINK_ICE},
        };

        for (Object[] mapping : mappings) {
            var hotSupplier = (java.util.function.Supplier<net.minecraft.world.item.Item>) mapping[0];
            var icedSupplier = (java.util.function.Supplier<net.minecraft.world.item.Item>) mapping[1];
            var hotItem = hotSupplier.get();
            var icedItem = icedSupplier.get();
            // Stable semantic IDs: cooling/<hot_name>_to_<iced_name>
            String hotPath = net.minecraft.core.registries.BuiltInRegistries.ITEM.getKey(hotItem).getPath();
            String icedPath = net.minecraft.core.registries.BuiltInRegistries.ITEM.getKey(icedItem).getPath();
            final ResourceLocation hotId = net.minecraft.core.registries.BuiltInRegistries.ITEM.getKey(hotItem);
            final ResourceLocation recipeId = modLoc("cooling/" + hotPath + "_to_" + icedPath);
            writer.accept(new FinishedRecipe() {
                @Override
                public void serializeRecipeData(com.google.gson.JsonObject json) {
                    json.addProperty("hot", hotId.toString());
                    json.addProperty("iced", net.minecraft.core.registries.BuiltInRegistries.ITEM
                            .getKey(icedItem).toString());
                }
                @Override
                public ResourceLocation getId() { return recipeId; }
                @Override
                public RecipeSerializer<?> getType() {
                    return net.langball.coffee.init.ModRecipeTypes.COOLING_SERIALIZER.get();
                }
                @Override @org.jetbrains.annotations.Nullable
                public ResourceLocation getAdvancementId() { return null; }
                @Override @org.jetbrains.annotations.Nullable
                public com.google.gson.JsonObject serializeAdvancement() { return null; }
            });
        }
    }

    // =======================================================================
    // HELPER METHODS
    // =======================================================================

    /** Shapeless convenience — unlocks with the given item. */
    private static ShapelessRecipeBuilder shapeless(RecipeCategory category, net.minecraft.world.level.ItemLike result, net.minecraft.world.level.ItemLike unlockItem) {
        return ShapelessRecipeBuilder.shapeless(category, result)
                .unlockedBy("has_item", has(unlockItem));
    }

    private static ShapelessRecipeBuilder shapeless(RecipeCategory category, net.minecraft.world.level.ItemLike result, int count, net.minecraft.world.level.ItemLike unlockItem) {
        return ShapelessRecipeBuilder.shapeless(category, result, count)
                .unlockedBy("has_item", has(unlockItem));
    }

    /** Shaped convenience — unlocks with the given item. */
    private static ShapedRecipeBuilder shaped(RecipeCategory category, net.minecraft.world.level.ItemLike result, net.minecraft.world.level.ItemLike unlockItem) {
        return ShapedRecipeBuilder.shaped(category, result)
                .unlockedBy("has_item", has(unlockItem));
    }

    private static ShapedRecipeBuilder shaped(RecipeCategory category, net.minecraft.world.level.ItemLike result, int count, net.minecraft.world.level.ItemLike unlockItem) {
        return ShapedRecipeBuilder.shaped(category, result, count)
                .unlockedBy("has_item", has(unlockItem));
    }

    /** Smelting convenience */
    private static SimpleCookingRecipeBuilder smelting(net.minecraft.world.level.ItemLike input, net.minecraft.world.level.ItemLike output, float xp, String group) {
        return SimpleCookingRecipeBuilder.smelting(
                Ingredient.of(input),
                RecipeCategory.FOOD,
                output,
                xp,
                200
        ).unlockedBy("has_" + group, has(input));
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
        shapeless(RecipeCategory.DECORATIONS, bagBlock, ModItems.BAG.get())
                .requires(ModItems.BAG.get())
                .requires(ingredient, 8)
                .save(writer, modLoc("bag_" + name));

        // Empty bag
        shapeless(RecipeCategory.MISC, ingredient, 8, bagBlock)
                .requires(bagBlock)
                .save(writer, modLoc(name + "_from_bag"));

        // Merge into double
        shapeless(RecipeCategory.DECORATIONS, doubleBagBlock, bagBlock)
                .requires(bagBlock)
                .requires(bagBlock)
                .save(writer, modLoc("double_bag_" + name));

        // Split double
        shapeless(RecipeCategory.DECORATIONS, bagBlock, 2, doubleBagBlock)
                .requires(doubleBagBlock)
                .save(writer, modLoc(name + "_from_double_bag"));
    }

    /** Variant for vanilla items (Items.SUGAR) */
    private void registerBagVanillaRecipes(Consumer<FinishedRecipe> writer,
                                           net.minecraft.world.level.ItemLike bagBlock,
                                           net.minecraft.world.level.ItemLike doubleBagBlock,
                                           net.minecraft.world.level.ItemLike ingredient,
                                           String name) {
        shapeless(RecipeCategory.DECORATIONS, bagBlock, ModItems.BAG.get())
                .requires(ModItems.BAG.get())
                .requires(ingredient, 8)
                .save(writer, modLoc("bag_" + name));

        shapeless(RecipeCategory.MISC, ingredient, 8, bagBlock)
                .requires(bagBlock)
                .save(writer, modLoc(name + "_from_bag"));

        shapeless(RecipeCategory.DECORATIONS, doubleBagBlock, bagBlock)
                .requires(bagBlock)
                .requires(bagBlock)
                .save(writer, modLoc("double_bag_" + name));

        shapeless(RecipeCategory.DECORATIONS, bagBlock, 2, doubleBagBlock)
                .requires(doubleBagBlock)
                .save(writer, modLoc(name + "_from_double_bag"));
    }

    private static ResourceLocation modLoc(String path) {
        return new ResourceLocation(CoffeeWork.MODID, path);
    }
}
