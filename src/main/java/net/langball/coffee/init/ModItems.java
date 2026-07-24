package net.langball.coffee.init;

import net.langball.coffee.CoffeeWork;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, CoffeeWork.MODID);

    // ========== BlockItems ==========
    // Machines
    public static final RegistryObject<Item> GRINDER = ITEMS.register("grinder_off",
            () -> new BlockItem(ModBlocks.GRINDER.get(), new Item.Properties()));
    public static final RegistryObject<Item> COFFEE_MACHINE = ITEMS.register("coffee_machine",
            () -> new BlockItem(ModBlocks.COFFEE_MACHINE.get(), new Item.Properties()));
    public static final RegistryObject<Item> ICECREAM_MACHINE = ITEMS.register("icecream_machine",
            () -> new BlockItem(ModBlocks.ICECREAM_MACHINE.get(), new Item.Properties()));
    public static final RegistryObject<Item> ROLLER = ITEMS.register("roller",
            () -> new BlockItem(ModBlocks.ROLLER.get(), new Item.Properties()));
    public static final RegistryObject<Item> OVEN = ITEMS.register("oven_off",
            () -> new BlockItem(ModBlocks.OVEN.get(), new Item.Properties()));

    // Plants
    public static final RegistryObject<Item> COFFEE_TREE = ITEMS.register("coffee_tree",
            () -> new BlockItem(ModBlocks.COFFEE_TREE.get(), new Item.Properties()));
    public static final RegistryObject<Item> BLUEBERRY_BUSH = ITEMS.register("blueberry_bush",
            () -> new BlockItem(ModBlocks.BLUEBERRY_BUSH.get(), new Item.Properties()));

    // Decor
    public static final RegistryObject<Item> PLATE = ITEMS.register("plate",
            () -> new BlockItem(ModBlocks.PLATE.get(), new Item.Properties()));
    public static final RegistryObject<Item> COLD_BREW_POT = ITEMS.register("coldbrew_pot",
            () -> new BlockItem(ModBlocks.COLD_BREW_POT.get(), new Item.Properties()));
    public static final RegistryObject<Item> SODA_ORE = ITEMS.register("soda_ore",
            () -> new BlockItem(ModBlocks.SODA_ORE.get(), new Item.Properties()));
    public static final RegistryObject<Item> XMAS_TREE = ITEMS.register("xmas_tree",
            () -> new BlockItem(ModBlocks.XMAS_TREE.get(), new Item.Properties()));
    public static final RegistryObject<Item> GINGER_HOUSE = ITEMS.register("ginger_house",
            () -> new BlockItem(ModBlocks.GINGER_HOUSE.get(), new Item.Properties()));

    // Bags (single)
    public static final RegistryObject<Item> BAG_COFFEE = ITEMS.register("bag_coffee",
            () -> new BlockItem(ModBlocks.BAG_COFFEE.get(), new Item.Properties()));
    public static final RegistryObject<Item> BAG_COFFEE_RAW = ITEMS.register("bag_coffee_raw",
            () -> new BlockItem(ModBlocks.BAG_COFFEE_RAW.get(), new Item.Properties()));
    public static final RegistryObject<Item> BAG_COCOA = ITEMS.register("bag_cocoa",
            () -> new BlockItem(ModBlocks.BAG_COCOA.get(), new Item.Properties()));
    public static final RegistryObject<Item> BAG_COCOA_POWDER = ITEMS.register("bag_cocoa_powder",
            () -> new BlockItem(ModBlocks.BAG_COCOA_POWDER.get(), new Item.Properties()));
    public static final RegistryObject<Item> BAG_FLOUR = ITEMS.register("bag_flour",
            () -> new BlockItem(ModBlocks.BAG_FLOUR.get(), new Item.Properties()));
    public static final RegistryObject<Item> BAG_COFFEE_POWDER = ITEMS.register("bag_coffee_powder",
            () -> new BlockItem(ModBlocks.BAG_COFFEE_POWDER.get(), new Item.Properties()));
    public static final RegistryObject<Item> BAG_SUGAR = ITEMS.register("bag_sugar",
            () -> new BlockItem(ModBlocks.BAG_SUGAR.get(), new Item.Properties()));

    // Bags (double)
    public static final RegistryObject<Item> DOUBLE_BAG_COFFEE = ITEMS.register("double_bag_coffee",
            () -> new BlockItem(ModBlocks.DOUBLE_BAG_COFFEE.get(), new Item.Properties()));
    public static final RegistryObject<Item> DOUBLE_BAG_COFFEE_RAW = ITEMS.register("double_bag_coffee_raw",
            () -> new BlockItem(ModBlocks.DOUBLE_BAG_COFFEE_RAW.get(), new Item.Properties()));
    public static final RegistryObject<Item> DOUBLE_BAG_COCOA = ITEMS.register("double_bag_cocoa",
            () -> new BlockItem(ModBlocks.DOUBLE_BAG_COCOA.get(), new Item.Properties()));
    public static final RegistryObject<Item> DOUBLE_BAG_COCOA_POWDER = ITEMS.register("double_bag_cocoa_powder",
            () -> new BlockItem(ModBlocks.DOUBLE_BAG_COCOA_POWDER.get(), new Item.Properties()));
    public static final RegistryObject<Item> DOUBLE_BAG_FLOUR = ITEMS.register("double_bag_flour",
            () -> new BlockItem(ModBlocks.DOUBLE_BAG_FLOUR.get(), new Item.Properties()));
    public static final RegistryObject<Item> DOUBLE_BAG_COFFEE_POWDER = ITEMS.register("double_bag_coffee_powder",
            () -> new BlockItem(ModBlocks.DOUBLE_BAG_COFFEE_POWDER.get(), new Item.Properties()));
    public static final RegistryObject<Item> DOUBLE_BAG_SUGAR = ITEMS.register("double_bag_sugar",
            () -> new BlockItem(ModBlocks.DOUBLE_BAG_SUGAR.get(), new Item.Properties()));

    // Cakes (sponge)
    public static final RegistryObject<Item> CAKE_SPONGE = ITEMS.register("cake_sponge",
            () -> new BlockItem(ModBlocks.CAKE_SPONGE.get(), new Item.Properties()));
    public static final RegistryObject<Item> CAKE_SPONGE_CHOCOLATE = ITEMS.register("cake_sponge_chocolate",
            () -> new BlockItem(ModBlocks.CAKE_SPONGE_CHOCOLATE.get(), new Item.Properties()));
    public static final RegistryObject<Item> CAKE_SPONGE_COFFEE = ITEMS.register("cake_sponge_coffee",
            () -> new BlockItem(ModBlocks.CAKE_SPONGE_COFFEE.get(), new Item.Properties()));
    public static final RegistryObject<Item> CAKE_SPONGE_PUMPKIN = ITEMS.register("cake_sponge_pumpkin",
            () -> new BlockItem(ModBlocks.CAKE_SPONGE_PUMPKIN.get(), new Item.Properties()));
    public static final RegistryObject<Item> CAKE_SPONGE_CARROT = ITEMS.register("cake_sponge_carrot",
            () -> new BlockItem(ModBlocks.CAKE_SPONGE_CARROT.get(), new Item.Properties()));
    public static final RegistryObject<Item> CAKE_SPONGE_REDVELVET = ITEMS.register("cake_sponge_redvelvet",
            () -> new BlockItem(ModBlocks.CAKE_SPONGE_REDVELVET.get(), new Item.Properties()));
    public static final RegistryObject<Item> CAKE_SPONGE_LEMON = ITEMS.register("cake_sponge_lemon",
            () -> new BlockItem(ModBlocks.CAKE_SPONGE_LEMON.get(), new Item.Properties()));
    public static final RegistryObject<Item> CAKE_SPONGE_TEA = ITEMS.register("cake_sponge_tea",
            () -> new BlockItem(ModBlocks.CAKE_SPONGE_TEA.get(), new Item.Properties()));
    public static final RegistryObject<Item> CAKE_SPONGE_BERRY = ITEMS.register("cake_sponge_berry",
            () -> new BlockItem(ModBlocks.CAKE_SPONGE_BERRY.get(), new Item.Properties()));

    // Cakes (large)
    public static final RegistryObject<Item> CAKE_COFFEE = ITEMS.register("cake_coffee",
            () -> new BlockItem(ModBlocks.CAKE_COFFEE.get(), new Item.Properties()));
    public static final RegistryObject<Item> CAKE_HARVEST = ITEMS.register("cake_harvest",
            () -> new BlockItem(ModBlocks.CAKE_HARVEST.get(), new Item.Properties()));
    public static final RegistryObject<Item> CAKE_LEMON = ITEMS.register("cake_lemon",
            () -> new BlockItem(ModBlocks.CAKE_LEMON.get(), new Item.Properties()));
    public static final RegistryObject<Item> CAKE_TEA = ITEMS.register("cake_tea",
            () -> new BlockItem(ModBlocks.CAKE_TEA.get(), new Item.Properties()));
    public static final RegistryObject<Item> CAKE_BERRY = ITEMS.register("cake_berry",
            () -> new BlockItem(ModBlocks.CAKE_BERRY.get(), new Item.Properties()));
    public static final RegistryObject<Item> CAKE_CHEESE = ITEMS.register("cake_cheese",
            () -> new BlockItem(ModBlocks.CAKE_CHEESE.get(), new Item.Properties()));
    public static final RegistryObject<Item> CAKE_SCHWARZWALD = ITEMS.register("cake_schwarzwald",
            () -> new BlockItem(ModBlocks.CAKE_SCHWARZWALD.get(), new Item.Properties()));
    public static final RegistryObject<Item> CAKE_REDVELVET = ITEMS.register("cake_redvelvet",
            () -> new BlockItem(ModBlocks.CAKE_REDVELVET.get(), new Item.Properties()));

    public static final RegistryObject<Item> TIRAMISU = ITEMS.register("tiramisu",
            () -> new BlockItem(ModBlocks.TIRAMISU.get(), new Item.Properties()));

    // Mousse
    public static final RegistryObject<Item> MOUSSE_BERRY = ITEMS.register("mousse_berry",
            () -> new BlockItem(ModBlocks.MOUSSE_BERRY.get(), new Item.Properties()));
    public static final RegistryObject<Item> MOUSSE_LEMON = ITEMS.register("mousse_lemon",
            () -> new BlockItem(ModBlocks.MOUSSE_LEMON.get(), new Item.Properties()));
    public static final RegistryObject<Item> MOUSSE_CHOCOLATE = ITEMS.register("mousse_chocolate",
            () -> new BlockItem(ModBlocks.MOUSSE_CHOCOLATE.get(), new Item.Properties()));
    public static final RegistryObject<Item> MOUSSE_COFFEE = ITEMS.register("mousse_coffee",
            () -> new BlockItem(ModBlocks.MOUSSE_COFFEE.get(), new Item.Properties()));

    // ========== Materials ==========
    // Seeds
    public static final RegistryObject<Item> COFFEE_SEEDS = ITEMS.register("coffee_seeds",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> VANILLA_SEEDS = ITEMS.register("vanilla_seeds",
            () -> new Item(new Item.Properties()));

    // Raw ingredients
    public static final RegistryObject<Item> COFFEE_BEAN_RAW = ITEMS.register("coffee_bean_raw",
            () -> new Item(new Item.Properties().food(new FoodProperties.Builder().nutrition(1).saturationMod(0.2f).build())));
    public static final RegistryObject<Item> COFFEE_BEAN = ITEMS.register("coffee_bean",
            () -> new Item(new Item.Properties().food(new FoodProperties.Builder().nutrition(1).saturationMod(0.2f).build())));
    public static final RegistryObject<Item> COFFEE_POWDER = ITEMS.register("coffee_powder",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> COCOA_BEAN = ITEMS.register("cocoa_bean",
            () -> new Item(new Item.Properties().food(new FoodProperties.Builder().nutrition(1).saturationMod(0.2f).build())));
    public static final RegistryObject<Item> COCOA_POWDER = ITEMS.register("cocoa_powder",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> COCOA_BATTER = ITEMS.register("cocoa_batter",
            () -> new Item(new Item.Properties()));

    // Baking & crafting
    public static final RegistryObject<Item> BAG_CLOTH = ITEMS.register("bag_cloth",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> ICE_SLAG = ITEMS.register("ice_slag",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> YEAST = ITEMS.register("yeast",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> PLATE_IRON = ITEMS.register("plate_iron",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> SPICES = ITEMS.register("spices",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> GELATIN = ITEMS.register("gelatin",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> SODA = ITEMS.register("soda",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> FLOUR = ITEMS.register("flour",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> DOUGH = ITEMS.register("dough",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> DOUGH_PASTRY = ITEMS.register("dough_pastry",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> DOUGH_COOKIE = ITEMS.register("dough_cookie",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> DOUGH_GINGER = ITEMS.register("dough_ginger",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> DOUGH_BREAD = ITEMS.register("dough_bread",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> DOUGH_BREAD_ROUND = ITEMS.register("dough_bread_round",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> DOUGH_BAGUETTE = ITEMS.register("dough_baguette",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> DOUGH_BAGEL = ITEMS.register("dough_bagel",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> DOUGH_TOAST = ITEMS.register("dough_toast",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> PLATE_DOUGH = ITEMS.register("plate_dough",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> PLATE_DOUGH_PASTRY = ITEMS.register("plate_dough_pastry",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> PLATE_DOUGH_GINGER = ITEMS.register("plate_dough_ginger",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> EMPTY_COLD_BREW_POT = ITEMS.register("empty_coldbrew_pot",
            () -> new Item(new Item.Properties()));

    // ========== Foods ==========
    // Breads
    public static final RegistryObject<Item> BREAD_ROUND = ITEMS.register("bread_round",
            () -> new Item(new Item.Properties().food(new FoodProperties.Builder().nutrition(6).saturationMod(0.6f).build())));
    public static final RegistryObject<Item> BAGUETTE = ITEMS.register("baguette",
            () -> new Item(new Item.Properties().food(new FoodProperties.Builder().nutrition(9).saturationMod(0.6f).build())));
    public static final RegistryObject<Item> BAGEL = ITEMS.register("bagel",
            () -> new Item(new Item.Properties().food(new FoodProperties.Builder().nutrition(7).saturationMod(0.6f).build())));
    public static final RegistryObject<Item> TOAST = ITEMS.register("toast",
            () -> new Item(new Item.Properties().food(new FoodProperties.Builder().nutrition(8).saturationMod(0.6f).build())));

    // Cake slices
    public static final RegistryObject<Item> CAKE_SPONGE_SLICE = ITEMS.register("cake_sponge_slice",
            () -> new Item(new Item.Properties().food(new FoodProperties.Builder().nutrition(1).saturationMod(0.1f).build())));
    // ... more cake slices can be added later

    // Dairy & basic foods
    public static final RegistryObject<Item> BUTTER = ITEMS.register("butter",
            () -> new Item(new Item.Properties().food(new FoodProperties.Builder().nutrition(2).saturationMod(0.4f).build())));
    public static final RegistryObject<Item> CHEESE = ITEMS.register("cheese",
            () -> new Item(new Item.Properties().food(new FoodProperties.Builder().nutrition(3).saturationMod(0.4f).build())));
    public static final RegistryObject<Item> BLUEBERRY = ITEMS.register("blueberry",
            () -> new Item(new Item.Properties().food(new FoodProperties.Builder().nutrition(1).saturationMod(0.5f).fast().build())));
    public static final RegistryObject<Item> CHOCOLATE_BAR = ITEMS.register("chocolate_bar",
            () -> new Item(new Item.Properties().food(new FoodProperties.Builder().nutrition(4).saturationMod(0.5f).build())));
    public static final RegistryObject<Item> CHOCOLATE_CHIP = ITEMS.register("chocolate_chip",
            () -> new Item(new Item.Properties().food(new FoodProperties.Builder().nutrition(4).saturationMod(0.5f).build())));
    public static final RegistryObject<Item> BROWNIE = ITEMS.register("brownie",
            () -> new Item(new Item.Properties().food(new FoodProperties.Builder().nutrition(9).saturationMod(0.5f).build())));
    public static final RegistryObject<Item> FIELD_RATION = ITEMS.register("field_ration",
            () -> new Item(new Item.Properties().food(new FoodProperties.Builder().nutrition(8).saturationMod(1.0f).build())));

    // ========== Tools & Special Items ==========
    public static final RegistryObject<Item> IRON_BOWL = ITEMS.register("iron_bowl",
            () -> new Item(new Item.Properties().stacksTo(16)));
    public static final RegistryObject<Item> CAKE_MODEL = ITEMS.register("cake_model",
            () -> new Item(new Item.Properties().stacksTo(16)));
    public static final RegistryObject<Item> CAKE_MODEL_SQUARE = ITEMS.register("cake_model_square",
            () -> new Item(new Item.Properties().stacksTo(16)));
    public static final RegistryObject<Item> CAKE_MODEL_PLATE = ITEMS.register("cake_model_plate",
            () -> new Item(new Item.Properties().stacksTo(16)));
    public static final RegistryObject<Item> SMALL_MODEL = ITEMS.register("small_model",
            () -> new Item(new Item.Properties().stacksTo(16)));

    // Records
    public static final RegistryObject<Item> RECORD_BLANK = ITEMS.register("record_blank",
            () -> new Item(new Item.Properties().stacksTo(1)));
}
