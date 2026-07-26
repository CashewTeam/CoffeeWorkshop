package net.langball.coffee.init;

import net.langball.coffee.CoffeeWork;
import net.langball.coffee.item.*;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, CoffeeWork.MODID);

    // ========================================================================
    // BlockItems
    // ========================================================================
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

    // Plants & Decor
    public static final RegistryObject<Item> COFFEE_TREE_ITEM = ITEMS.register("coffee_tree",
            () -> new BlockItem(ModBlocks.COFFEE_TREE.get(), new Item.Properties()));
    public static final RegistryObject<Item> BLUEBERRY_BUSH_ITEM = ITEMS.register("blueberry_bush",
            () -> new BlockItem(ModBlocks.BLUEBERRY_BUSH.get(), new Item.Properties()));
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

    // Bags
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

    // Double Bags
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

    // Cakes
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
    public static final RegistryObject<Item> MOUSSE_BERRY = ITEMS.register("mousse_berry",
            () -> new BlockItem(ModBlocks.MOUSSE_BERRY.get(), new Item.Properties()));
    public static final RegistryObject<Item> MOUSSE_LEMON = ITEMS.register("mousse_lemon",
            () -> new BlockItem(ModBlocks.MOUSSE_LEMON.get(), new Item.Properties()));
    public static final RegistryObject<Item> MOUSSE_CHOCOLATE = ITEMS.register("mousse_chocolate",
            () -> new BlockItem(ModBlocks.MOUSSE_CHOCOLATE.get(), new Item.Properties()));
    public static final RegistryObject<Item> MOUSSE_COFFEE = ITEMS.register("mousse_coffee",
            () -> new BlockItem(ModBlocks.MOUSSE_COFFEE.get(), new Item.Properties()));

    // ========================================================================
    // Seeds
    // ========================================================================
    public static final RegistryObject<Item> COFFEE_SEEDS = ITEMS.register("coffee_seeds",
            () -> new SeedCoffee(ModBlocks.COFFEE_TREE.get(), new Item.Properties()));
    public static final RegistryObject<Item> VANILLA_SEEDS = ITEMS.register("vanilla_seeds",
            () -> new SeedCoffee(ModBlocks.VANILLA_CROP.get(), new Item.Properties()));

    // ========================================================================
    // Materials (Crafting Ingredients)
    // ========================================================================
    public static final RegistryObject<Item> COFFEE_BEAN_RAW = ITEMS.register("coffee_bean_raw",
            () -> new Item(new Item.Properties().food(new FoodProperties.Builder().nutrition(1).saturationMod(0.2F).build())));
    public static final RegistryObject<Item> COFFEE_BEAN = ITEMS.register("coffee_bean",
            () -> new Item(new Item.Properties().food(new FoodProperties.Builder().nutrition(1).saturationMod(0.2F).fast().build())));
    public static final RegistryObject<Item> COFFEE_POWDER = ITEMS.register("coffee_powder",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> COCOA_BEAN = ITEMS.register("cocoa_bean",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> COCOA_POWDER = ITEMS.register("cocoa_powder",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> COCOA_BATTER = ITEMS.register("cocoa_batter",
            () -> new Item(new Item.Properties()));
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
    public static final RegistryObject<Item> EMPTY_COLDBREW_POT = ITEMS.register("empty_coldbrew_pot",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> COLDBREW_BOTTLE = ITEMS.register("coldbrew_bottle",
            () -> new Item(new Item.Properties()));

    // ========================================================================
    // Foods (with FoodProperties)
    // ========================================================================
    // Breads
    public static final RegistryObject<Item> BREAD_ROUND = ITEMS.register("bread_round",
            () -> new Item(new Item.Properties().food(new FoodProperties.Builder().nutrition(6).saturationMod(0.6F).build())));
    public static final RegistryObject<Item> BAGUETTE = ITEMS.register("baguette",
            () -> new Item(new Item.Properties().food(new FoodProperties.Builder().nutrition(9).saturationMod(0.6F).build())));
    public static final RegistryObject<Item> BAGEL = ITEMS.register("bagel",
            () -> new Item(new Item.Properties().food(new FoodProperties.Builder().nutrition(7).saturationMod(0.6F).build())));
    public static final RegistryObject<Item> TOAST = ITEMS.register("toast",
            () -> new Item(new Item.Properties().food(new FoodProperties.Builder().nutrition(8).saturationMod(0.6F).build())));

    // Dairy & sweets
    public static final RegistryObject<Item> BUTTER = ITEMS.register("butter",
            () -> new Item(new Item.Properties().food(new FoodProperties.Builder().nutrition(2).saturationMod(0.4F).build())));
    public static final RegistryObject<Item> CHEESE = ITEMS.register("cheese",
            () -> new Item(new Item.Properties().food(new FoodProperties.Builder().nutrition(3).saturationMod(0.4F).build())));
    public static final RegistryObject<Item> BLUEBERRY = ITEMS.register("blueberry",
            () -> new Item(new Item.Properties().food(new FoodProperties.Builder().nutrition(1).saturationMod(0.5F).fast().build())));
    public static final RegistryObject<Item> CHOCOLATE_BAR = ITEMS.register("chocolate_bar",
            () -> new Item(new Item.Properties().food(new FoodProperties.Builder().nutrition(4).saturationMod(0.5F).build())));
    public static final RegistryObject<Item> CHOCOLATE_CHIP = ITEMS.register("chocolate_chip",
            () -> new Item(new Item.Properties().food(new FoodProperties.Builder().nutrition(4).saturationMod(0.5F).build())));
    public static final RegistryObject<Item> BROWNIE = ITEMS.register("brownie",
            () -> new Item(new Item.Properties().food(new FoodProperties.Builder().nutrition(9).saturationMod(0.5F).build())));
    public static final RegistryObject<Item> FIELD_RATION = ITEMS.register("field_ration",
            () -> new Item(new Item.Properties().food(new FoodProperties.Builder().nutrition(8).saturationMod(1.0F).build())));

    // Cake slices
    public static final RegistryObject<Item> CAKE_SPONGE_SLICE = ITEMS.register("cake_sponge_slice",
            () -> new Item(new Item.Properties().food(new FoodProperties.Builder().nutrition(1).saturationMod(0.1F).build())));

    // Pies
    public static final RegistryObject<Item> PIE_CREAM = ITEMS.register("pie_cream",
            () -> new Item(new Item.Properties().food(new FoodProperties.Builder().nutrition(10).saturationMod(0.6F).build())));

    // Sandwiches
    public static final RegistryObject<Item> SANDWICH_BLT = ITEMS.register("sandwich_blt",
            () -> new Item(new Item.Properties().food(new FoodProperties.Builder().nutrition(10).saturationMod(0.9F).build())));

    // Ice creams
    public static final RegistryObject<Item> ICECREAM_MIX_VANILLA = ITEMS.register("icecream_mix_vanilla",
            () -> new Item(new Item.Properties().stacksTo(16)));
    public static final RegistryObject<Item> ICECREAM_VANILLA = ITEMS.register("icecream_vanilla",
            () -> new Item(new Item.Properties().food(new FoodProperties.Builder().nutrition(4).saturationMod(0.5F).build())));

    // ========================================================================
    // Drinks (using DrinkCoffee subclasses)
    // ========================================================================
    // Instant coffee
    public static final RegistryObject<Item> COFFEE_INSTANT = ITEMS.register("coffee_instant",
            () -> new DrinkCoffeeInstant(
                    new Item.Properties().food(new FoodProperties.Builder().nutrition(2).saturationMod(0.2F).alwaysEat().build()),
                    new MobEffectInstance[]{}, 3, false));

    public static final RegistryObject<Item> COFFEE_INSTANT_STICK = ITEMS.register("coffee_instant_stick",
            () -> new Item(new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> COFFEE_INSTANT_BOX = ITEMS.register("coffee_instant_box",
            () -> new Item(new Item.Properties()));

    // Americano (returns cup when finished)
    public static final RegistryObject<Item> COFFEE_AMERICANO = ITEMS.register("coffee_americano",
            () -> new DrinkCoffee(
                    new Item.Properties().food(new FoodProperties.Builder().nutrition(2).saturationMod(0.2F).alwaysEat().build()),
                    new MobEffectInstance[]{
                            new MobEffectInstance(net.langball.coffee.init.ModEffects.CAFFEINE.get(), 3600, 0),
                            new MobEffectInstance(net.langball.coffee.init.ModEffects.RELAX.get(), 600, 1),
                    }, 4, () -> ModItems.CUP.get()));

    // Espresso (returns cup when finished)
    public static final RegistryObject<Item> ESPRESSO = ITEMS.register("espresso",
	            () -> new DrinkEspresso(
	                    new Item.Properties().food(new FoodProperties.Builder().nutrition(1).saturationMod(0.1F).alwaysEat().build()),
	                    new MobEffectInstance[]{
	                            new MobEffectInstance(net.langball.coffee.init.ModEffects.CAFFEINE.get(), 4800, 1),
	                    }, 2, () -> ModItems.CUP.get()));

	    // ========================================================================
	    // Basic hot drinks
	    // ========================================================================
	    public static final RegistryObject<Item> COFFEE_LATTE = ITEMS.register("coffee_latte",
	            () -> new DrinkCoffee(
	                    new Item.Properties().food(new FoodProperties.Builder().nutrition(2).saturationMod(0.2F).alwaysEat().build()),
	                    new MobEffectInstance[]{
	                            new MobEffectInstance(net.langball.coffee.init.ModEffects.CAFFEINE.get(), 3600, 0),
	                            new MobEffectInstance(net.langball.coffee.init.ModEffects.RELAX.get(), 1200, 0),
	                    }, 4, () -> ModItems.CUP.get()));

	    public static final RegistryObject<Item> COFFEE_CAPPUCCINO = ITEMS.register("coffee_cappuccino",
	            () -> new DrinkCoffee(
	                    new Item.Properties().food(new FoodProperties.Builder().nutrition(2).saturationMod(0.3F).alwaysEat().build()),
	                    new MobEffectInstance[]{
	                            new MobEffectInstance(net.langball.coffee.init.ModEffects.CAFFEINE.get(), 2400, 0),
	                            new MobEffectInstance(net.langball.coffee.init.ModEffects.RELAX.get(), 1800, 0),
	                    }, 3, () -> ModItems.CUP.get()));

	    public static final RegistryObject<Item> COFFEE_MACCHIATO = ITEMS.register("coffee_macchiato",
	            () -> new DrinkCoffee(
	                    new Item.Properties().food(new FoodProperties.Builder().nutrition(2).saturationMod(0.3F).alwaysEat().build()),
	                    new MobEffectInstance[]{
	                            new MobEffectInstance(net.langball.coffee.init.ModEffects.CAFFEINE.get(), 3600, 0),
	                            new MobEffectInstance(net.langball.coffee.init.ModEffects.RELAX.get(), 1200, 0),
	                            new MobEffectInstance(net.minecraft.world.effect.MobEffects.MOVEMENT_SPEED, 600, 0),
	                    }, 3, () -> ModItems.CUP.get()));

	    public static final RegistryObject<Item> COFFEE_MOCHACCINO = ITEMS.register("coffee_mochaccino",
	            () -> new DrinkCoffee(
	                    new Item.Properties().food(new FoodProperties.Builder().nutrition(3).saturationMod(0.3F).alwaysEat().build()),
	                    new MobEffectInstance[]{
	                            new MobEffectInstance(net.langball.coffee.init.ModEffects.CAFFEINE.get(), 2400, 0),
	                            new MobEffectInstance(net.langball.coffee.init.ModEffects.RELAX.get(), 1800, 0),
	                    }, 3, () -> ModItems.CUP.get()));

	    // ========================================================================
	    // Tea-based drinks (served in glass)
	    // ========================================================================
	    public static final RegistryObject<Item> COFFEE_GREEN_TEA = ITEMS.register("coffee_green_tea",
	            () -> new DrinkCoffee(
	                    new Item.Properties().food(new FoodProperties.Builder().nutrition(1).saturationMod(0.1F).alwaysEat().build()),
	                    new MobEffectInstance[]{
	                            new MobEffectInstance(net.langball.coffee.init.ModEffects.RELAX.get(), 2400, 0),
	                    }, 3, () -> ModItems.CUP_GLASS.get()));

	    public static final RegistryObject<Item> COFFEE_BLACK_TEA = ITEMS.register("coffee_black_tea",
	            () -> new DrinkCoffee(
	                    new Item.Properties().food(new FoodProperties.Builder().nutrition(1).saturationMod(0.1F).alwaysEat().build()),
	                    new MobEffectInstance[]{
	                            new MobEffectInstance(net.langball.coffee.init.ModEffects.RELAX.get(), 3600, 0),
	                    }, 3, () -> ModItems.CUP_GLASS.get()));

	    public static final RegistryObject<Item> COFFEE_MILK_TEA = ITEMS.register("coffee_milk_tea",
	            () -> new DrinkCoffee(
	                    new Item.Properties().food(new FoodProperties.Builder().nutrition(2).saturationMod(0.2F).alwaysEat().build()),
	                    new MobEffectInstance[]{
	                            new MobEffectInstance(net.langball.coffee.init.ModEffects.RELAX.get(), 3600, 0),
	                    }, 3, () -> ModItems.CUP_GLASS.get()));

	    public static final RegistryObject<Item> COFFEE_MANDARIN_DRINK = ITEMS.register("coffee_mandarin_drink",
	            () -> new DrinkCoffee(
	                    new Item.Properties().food(new FoodProperties.Builder().nutrition(2).saturationMod(0.2F).alwaysEat().build()),
	                    new MobEffectInstance[]{
	                            new MobEffectInstance(net.langball.coffee.init.ModEffects.CAFFEINE.get(), 2400, 0),
	                            new MobEffectInstance(net.langball.coffee.init.ModEffects.RELAX.get(), 2400, 0),
	                    }, 3, () -> ModItems.CUP_GLASS.get()));

	    // ========================================================================
	    // Cold brew (served in glass)
	    // ========================================================================
	    public static final RegistryObject<Item> COFFEE_COLDBREW = ITEMS.register("coffee_coldbrew",
	            () -> new DrinkCoffee(
	                    new Item.Properties().food(new FoodProperties.Builder().nutrition(2).saturationMod(0.2F).alwaysEat().build()),
	                    new MobEffectInstance[]{
	                            new MobEffectInstance(net.langball.coffee.init.ModEffects.CAFFEINE.get(), 4800, 0),
	                    }, 3, () -> ModItems.CUP_GLASS.get()));

	    // ========================================================================
	    // Cocoa drinks (served in cup)
	    // ========================================================================
	    public static final RegistryObject<Item> COCOA = ITEMS.register("cocoa",
	            () -> new DrinkCoffee(
	                    new Item.Properties().food(new FoodProperties.Builder().nutrition(3).saturationMod(0.3F).alwaysEat().build()),
	                    new MobEffectInstance[]{
	                            new MobEffectInstance(net.minecraft.world.effect.MobEffects.SATURATION, 1200, 0),
	                    }, 3, () -> ModItems.CUP.get()));

		    public static final RegistryObject<Item> COCOA_STRONG = ITEMS.register("cocoa_strong",
		            () -> new DrinkCoffee(
		                    new Item.Properties().food(new FoodProperties.Builder().nutrition(4).saturationMod(0.4F).alwaysEat().build()),
		                    new MobEffectInstance[]{
		                            new MobEffectInstance(net.minecraft.world.effect.MobEffects.SATURATION, 2400, 0),
		                            new MobEffectInstance(net.minecraft.world.effect.MobEffects.MOVEMENT_SPEED, 1200, 0),
		                    }, 2, () -> ModItems.CUP.get()));

	    // ========================================================================
	    // Iced drink variants (served in glass)
	    // ========================================================================
	    public static final RegistryObject<Item> COFFEE_AMERICANO_ICE = ITEMS.register("coffee_americano_ice",
	            () -> new DrinkCoffee(
	                    new Item.Properties().food(new FoodProperties.Builder().nutrition(2).saturationMod(0.2F).alwaysEat().build()),
	                    new MobEffectInstance[]{
	                            new MobEffectInstance(net.langball.coffee.init.ModEffects.CAFFEINE.get(), 2400, 0),
	                            new MobEffectInstance(net.langball.coffee.init.ModEffects.RELAX.get(), 400, 1),
	                    }, 4, () -> ModItems.CUP_GLASS.get()));

	    public static final RegistryObject<Item> COFFEE_LATTE_ICE = ITEMS.register("coffee_latte_ice",
	            () -> new DrinkCoffee(
	                    new Item.Properties().food(new FoodProperties.Builder().nutrition(2).saturationMod(0.2F).alwaysEat().build()),
	                    new MobEffectInstance[]{
	                            new MobEffectInstance(net.langball.coffee.init.ModEffects.CAFFEINE.get(), 2400, 0),
	                            new MobEffectInstance(net.langball.coffee.init.ModEffects.RELAX.get(), 800, 0),
	                    }, 4, () -> ModItems.CUP_GLASS.get()));

	    public static final RegistryObject<Item> COFFEE_CAPPUCCINO_ICE = ITEMS.register("coffee_cappuccino_ice",
	            () -> new DrinkCoffee(
	                    new Item.Properties().food(new FoodProperties.Builder().nutrition(2).saturationMod(0.3F).alwaysEat().build()),
	                    new MobEffectInstance[]{
	                            new MobEffectInstance(net.langball.coffee.init.ModEffects.CAFFEINE.get(), 1600, 0),
	                            new MobEffectInstance(net.langball.coffee.init.ModEffects.RELAX.get(), 1200, 0),
	                    }, 3, () -> ModItems.CUP_GLASS.get()));

	    public static final RegistryObject<Item> COFFEE_MACCHIATO_ICE = ITEMS.register("coffee_macchiato_ice",
	            () -> new DrinkCoffee(
	                    new Item.Properties().food(new FoodProperties.Builder().nutrition(2).saturationMod(0.3F).alwaysEat().build()),
	                    new MobEffectInstance[]{
	                            new MobEffectInstance(net.langball.coffee.init.ModEffects.CAFFEINE.get(), 2400, 0),
	                            new MobEffectInstance(net.langball.coffee.init.ModEffects.RELAX.get(), 800, 0),
	                            new MobEffectInstance(net.minecraft.world.effect.MobEffects.MOVEMENT_SPEED, 400, 0),
	                    }, 3, () -> ModItems.CUP_GLASS.get()));

	    public static final RegistryObject<Item> COFFEE_MOCHACCINO_ICE = ITEMS.register("coffee_mochaccino_ice",
	            () -> new DrinkCoffee(
	                    new Item.Properties().food(new FoodProperties.Builder().nutrition(3).saturationMod(0.3F).alwaysEat().build()),
	                    new MobEffectInstance[]{
	                            new MobEffectInstance(net.langball.coffee.init.ModEffects.CAFFEINE.get(), 1600, 0),
	                            new MobEffectInstance(net.langball.coffee.init.ModEffects.RELAX.get(), 1200, 0),
	                    }, 3, () -> ModItems.CUP_GLASS.get()));

	    public static final RegistryObject<Item> COFFEE_GREEN_TEA_ICE = ITEMS.register("coffee_green_tea_ice",
	            () -> new DrinkCoffee(
	                    new Item.Properties().food(new FoodProperties.Builder().nutrition(1).saturationMod(0.1F).alwaysEat().build()),
	                    new MobEffectInstance[]{
	                            new MobEffectInstance(net.langball.coffee.init.ModEffects.RELAX.get(), 1600, 0),
	                    }, 3, () -> ModItems.CUP_GLASS.get()));

	    public static final RegistryObject<Item> COFFEE_BLACK_TEA_ICE = ITEMS.register("coffee_black_tea_ice",
	            () -> new DrinkCoffee(
	                    new Item.Properties().food(new FoodProperties.Builder().nutrition(1).saturationMod(0.1F).alwaysEat().build()),
	                    new MobEffectInstance[]{
	                            new MobEffectInstance(net.langball.coffee.init.ModEffects.RELAX.get(), 2400, 0),
	                    }, 3, () -> ModItems.CUP_GLASS.get()));

	    public static final RegistryObject<Item> COFFEE_MILK_TEA_ICE = ITEMS.register("coffee_milk_tea_ice",
	            () -> new DrinkCoffee(
	                    new Item.Properties().food(new FoodProperties.Builder().nutrition(2).saturationMod(0.2F).alwaysEat().build()),
	                    new MobEffectInstance[]{
	                            new MobEffectInstance(net.langball.coffee.init.ModEffects.RELAX.get(), 2400, 0),
	                    }, 3, () -> ModItems.CUP_GLASS.get()));

	    public static final RegistryObject<Item> COFFEE_MANDARIN_DRINK_ICE = ITEMS.register("coffee_mandarin_drink_ice",
	            () -> new DrinkCoffee(
	                    new Item.Properties().food(new FoodProperties.Builder().nutrition(2).saturationMod(0.2F).alwaysEat().build()),
	                    new MobEffectInstance[]{
	                            new MobEffectInstance(net.langball.coffee.init.ModEffects.CAFFEINE.get(), 1600, 0),
	                            new MobEffectInstance(net.langball.coffee.init.ModEffects.RELAX.get(), 1600, 0),
	                    }, 3, () -> ModItems.CUP_GLASS.get()));

	    public static final RegistryObject<Item> COFFEE_COLDBREW_ICE = ITEMS.register("coffee_coldbrew_ice",
	            () -> new DrinkCoffee(
	                    new Item.Properties().food(new FoodProperties.Builder().nutrition(2).saturationMod(0.2F).alwaysEat().build()),
	                    new MobEffectInstance[]{
	                            new MobEffectInstance(net.langball.coffee.init.ModEffects.CAFFEINE.get(), 3200, 0),
	                    }, 3, () -> ModItems.CUP_GLASS.get()));

	    public static final RegistryObject<Item> COCOA_ICE = ITEMS.register("cocoa_ice",
	            () -> new DrinkCoffee(
	                    new Item.Properties().food(new FoodProperties.Builder().nutrition(3).saturationMod(0.3F).alwaysEat().build()),
	                    new MobEffectInstance[]{
	                            new MobEffectInstance(net.minecraft.world.effect.MobEffects.SATURATION, 800, 0),
	                    }, 3, () -> ModItems.CUP_GLASS.get()));

	    public static final RegistryObject<Item> COCOA_STRONG_ICE = ITEMS.register("cocoa_strong_ice",
	            () -> new DrinkCoffee(
	                    new Item.Properties().food(new FoodProperties.Builder().nutrition(4).saturationMod(0.4F).alwaysEat().build()),
	                    new MobEffectInstance[]{
	                            new MobEffectInstance(net.minecraft.world.effect.MobEffects.SATURATION, 1600, 0),
	                            new MobEffectInstance(net.minecraft.world.effect.MobEffects.MOVEMENT_SPEED, 800, 0),
	                    }, 2, () -> ModItems.CUP_GLASS.get()));

	    // ========================================================================
	    // Flavored latte variants (hot, served in cup)
	    // ========================================================================
	    public static final RegistryObject<Item> COFFEE_LATTE_CARAMEL = ITEMS.register("coffee_latte_caramel",
	            () -> new DrinkCoffee(
	                    new Item.Properties().food(new FoodProperties.Builder().nutrition(2).saturationMod(0.3F).alwaysEat().build()),
	                    new MobEffectInstance[]{
	                            new MobEffectInstance(net.langball.coffee.init.ModEffects.CAFFEINE.get(), 3600, 0),
	                            new MobEffectInstance(net.langball.coffee.init.ModEffects.RELAX.get(), 1200, 0),
	                            new MobEffectInstance(net.minecraft.world.effect.MobEffects.DIG_SPEED, 600, 0),
	                    }, 4, () -> ModItems.CUP.get()));

	    public static final RegistryObject<Item> COFFEE_LATTE_CHOCOLATE = ITEMS.register("coffee_latte_chocolate",
	            () -> new DrinkCoffee(
	                    new Item.Properties().food(new FoodProperties.Builder().nutrition(3).saturationMod(0.3F).alwaysEat().build()),
	                    new MobEffectInstance[]{
	                            new MobEffectInstance(net.langball.coffee.init.ModEffects.CAFFEINE.get(), 3600, 0),
	                            new MobEffectInstance(net.langball.coffee.init.ModEffects.RELAX.get(), 1200, 0),
	                    }, 4, () -> ModItems.CUP.get()));

	    public static final RegistryObject<Item> COFFEE_LATTE_FRUIT = ITEMS.register("coffee_latte_fruit",
	            () -> new DrinkCoffee(
	                    new Item.Properties().food(new FoodProperties.Builder().nutrition(3).saturationMod(0.3F).alwaysEat().build()),
	                    new MobEffectInstance[]{
	                            new MobEffectInstance(net.langball.coffee.init.ModEffects.CAFFEINE.get(), 3600, 0),
	                            new MobEffectInstance(net.langball.coffee.init.ModEffects.RELAX.get(), 1200, 0),
	                    }, 4, () -> ModItems.CUP.get()));

	    public static final RegistryObject<Item> COFFEE_LATTE_MINT = ITEMS.register("coffee_latte_mint",
	            () -> new DrinkCoffee(
	                    new Item.Properties().food(new FoodProperties.Builder().nutrition(2).saturationMod(0.3F).alwaysEat().build()),
	                    new MobEffectInstance[]{
	                            new MobEffectInstance(net.langball.coffee.init.ModEffects.CAFFEINE.get(), 3600, 0),
	                            new MobEffectInstance(net.langball.coffee.init.ModEffects.RELAX.get(), 1200, 0),
	                    }, 4, () -> ModItems.CUP.get()));

	    public static final RegistryObject<Item> COFFEE_LATTE_VANILLA = ITEMS.register("coffee_latte_vanilla",
	            () -> new DrinkCoffee(
	                    new Item.Properties().food(new FoodProperties.Builder().nutrition(2).saturationMod(0.3F).alwaysEat().build()),
	                    new MobEffectInstance[]{
	                            new MobEffectInstance(net.langball.coffee.init.ModEffects.CAFFEINE.get(), 3600, 0),
	                            new MobEffectInstance(net.langball.coffee.init.ModEffects.RELAX.get(), 1200, 0),
	                    }, 4, () -> ModItems.CUP.get()));

	    public static final RegistryObject<Item> COFFEE_LATTE_SAKURA = ITEMS.register("coffee_latte_sakura",
	            () -> new DrinkCoffee(
	                    new Item.Properties().food(new FoodProperties.Builder().nutrition(2).saturationMod(0.3F).alwaysEat().build()),
	                    new MobEffectInstance[]{
	                            new MobEffectInstance(net.langball.coffee.init.ModEffects.CAFFEINE.get(), 3600, 0),
	                            new MobEffectInstance(net.langball.coffee.init.ModEffects.RELAX.get(), 1200, 0),
	                            new MobEffectInstance(net.minecraft.world.effect.MobEffects.LUCK, 1200, 0),
	                    }, 4, () -> ModItems.CUP.get()));

	    // ========================================================================
	    // Flavored latte variants (iced, served in glass)
	    // ========================================================================
	    public static final RegistryObject<Item> COFFEE_LATTE_CARAMEL_ICE = ITEMS.register("coffee_latte_caramel_ice",
	            () -> new DrinkCoffee(
	                    new Item.Properties().food(new FoodProperties.Builder().nutrition(2).saturationMod(0.3F).alwaysEat().build()),
	                    new MobEffectInstance[]{
	                            new MobEffectInstance(net.langball.coffee.init.ModEffects.CAFFEINE.get(), 2400, 0),
	                            new MobEffectInstance(net.langball.coffee.init.ModEffects.RELAX.get(), 800, 0),
	                            new MobEffectInstance(net.minecraft.world.effect.MobEffects.DIG_SPEED, 400, 0),
	                    }, 4, () -> ModItems.CUP_GLASS.get()));

	    public static final RegistryObject<Item> COFFEE_LATTE_CHOCOLATE_ICE = ITEMS.register("coffee_latte_chocolate_ice",
	            () -> new DrinkCoffee(
	                    new Item.Properties().food(new FoodProperties.Builder().nutrition(3).saturationMod(0.3F).alwaysEat().build()),
	                    new MobEffectInstance[]{
	                            new MobEffectInstance(net.langball.coffee.init.ModEffects.CAFFEINE.get(), 2400, 0),
	                            new MobEffectInstance(net.langball.coffee.init.ModEffects.RELAX.get(), 800, 0),
	                    }, 4, () -> ModItems.CUP_GLASS.get()));

	    public static final RegistryObject<Item> COFFEE_LATTE_FRUIT_ICE = ITEMS.register("coffee_latte_fruit_ice",
	            () -> new DrinkCoffee(
	                    new Item.Properties().food(new FoodProperties.Builder().nutrition(3).saturationMod(0.3F).alwaysEat().build()),
	                    new MobEffectInstance[]{
	                            new MobEffectInstance(net.langball.coffee.init.ModEffects.CAFFEINE.get(), 2400, 0),
	                            new MobEffectInstance(net.langball.coffee.init.ModEffects.RELAX.get(), 800, 0),
	                    }, 4, () -> ModItems.CUP_GLASS.get()));

	    public static final RegistryObject<Item> COFFEE_LATTE_MINT_ICE = ITEMS.register("coffee_latte_mint_ice",
	            () -> new DrinkCoffee(
	                    new Item.Properties().food(new FoodProperties.Builder().nutrition(2).saturationMod(0.3F).alwaysEat().build()),
	                    new MobEffectInstance[]{
	                            new MobEffectInstance(net.langball.coffee.init.ModEffects.CAFFEINE.get(), 2400, 0),
	                            new MobEffectInstance(net.langball.coffee.init.ModEffects.RELAX.get(), 800, 0),
	                    }, 4, () -> ModItems.CUP_GLASS.get()));

	    public static final RegistryObject<Item> COFFEE_LATTE_VANILLA_ICE = ITEMS.register("coffee_latte_vanilla_ice",
	            () -> new DrinkCoffee(
	                    new Item.Properties().food(new FoodProperties.Builder().nutrition(2).saturationMod(0.3F).alwaysEat().build()),
	                    new MobEffectInstance[]{
	                            new MobEffectInstance(net.langball.coffee.init.ModEffects.CAFFEINE.get(), 2400, 0),
	                            new MobEffectInstance(net.langball.coffee.init.ModEffects.RELAX.get(), 800, 0),
	                    }, 4, () -> ModItems.CUP_GLASS.get()));

	    public static final RegistryObject<Item> COFFEE_LATTE_SAKURA_ICE = ITEMS.register("coffee_latte_sakura_ice",
	            () -> new DrinkCoffee(
	                    new Item.Properties().food(new FoodProperties.Builder().nutrition(2).saturationMod(0.3F).alwaysEat().build()),
	                    new MobEffectInstance[]{
	                            new MobEffectInstance(net.langball.coffee.init.ModEffects.CAFFEINE.get(), 2400, 0),
	                            new MobEffectInstance(net.langball.coffee.init.ModEffects.RELAX.get(), 800, 0),
	                            new MobEffectInstance(net.minecraft.world.effect.MobEffects.LUCK, 800, 0),
	                    }, 4, () -> ModItems.CUP_GLASS.get()));

	    // ========================================================================
	    // Americano extensions
	    // ========================================================================
	    public static final RegistryObject<Item> COFFEE_AMERICANO_FRUIT = ITEMS.register("coffee_americano_fruit",
	            () -> new DrinkCoffee(
	                    new Item.Properties().food(new FoodProperties.Builder().nutrition(3).saturationMod(0.3F).alwaysEat().build()),
	                    new MobEffectInstance[]{
	                            new MobEffectInstance(net.langball.coffee.init.ModEffects.CAFFEINE.get(), 3600, 0),
	                            new MobEffectInstance(net.langball.coffee.init.ModEffects.RELAX.get(), 600, 1),
	                    }, 4, () -> ModItems.CUP.get()));

	    public static final RegistryObject<Item> COFFEE_AMERICANO_FRUIT_ICE = ITEMS.register("coffee_americano_fruit_ice",
	            () -> new DrinkCoffee(
	                    new Item.Properties().food(new FoodProperties.Builder().nutrition(3).saturationMod(0.3F).alwaysEat().build()),
	                    new MobEffectInstance[]{
	                            new MobEffectInstance(net.langball.coffee.init.ModEffects.CAFFEINE.get(), 2400, 0),
	                            new MobEffectInstance(net.langball.coffee.init.ModEffects.RELAX.get(), 400, 1),
	                    }, 4, () -> ModItems.CUP_GLASS.get()));

	    public static final RegistryObject<Item> COFFEE_AMERICANO_NITRO_ICE = ITEMS.register("coffee_americano_nitro_ice",
	            () -> new DrinkCoffee(
	                    new Item.Properties().food(new FoodProperties.Builder().nutrition(2).saturationMod(0.2F).alwaysEat().build()),
	                    new MobEffectInstance[]{
	                            new MobEffectInstance(net.langball.coffee.init.ModEffects.CAFFEINE.get(), 2400, 0),
	                            new MobEffectInstance(net.langball.coffee.init.ModEffects.RELAX.get(), 400, 1),
	                            new MobEffectInstance(net.minecraft.world.effect.MobEffects.MOVEMENT_SPEED, 600, 1),
	                    }, 4, () -> ModItems.CUP_GLASS.get()));

	    public static final RegistryObject<Item> COFFEE_AMERICANO_NITRO_FRUIT_ICE = ITEMS.register("coffee_americano_nitro_fruit_ice",
	            () -> new DrinkCoffee(
	                    new Item.Properties().food(new FoodProperties.Builder().nutrition(3).saturationMod(0.3F).alwaysEat().build()),
	                    new MobEffectInstance[]{
	                            new MobEffectInstance(net.langball.coffee.init.ModEffects.CAFFEINE.get(), 2400, 0),
	                            new MobEffectInstance(net.langball.coffee.init.ModEffects.RELAX.get(), 400, 1),
	                            new MobEffectInstance(net.minecraft.world.effect.MobEffects.MOVEMENT_SPEED, 600, 1),
	                    }, 4, () -> ModItems.CUP_GLASS.get()));

	    // ========================================================================
	    // Coldbrew extensions (served in glass)
	    // ========================================================================
	    public static final RegistryObject<Item> COFFEE_COLDBREW_FRUIT = ITEMS.register("coffee_coldbrew_fruit",
	            () -> new DrinkCoffee(
	                    new Item.Properties().food(new FoodProperties.Builder().nutrition(3).saturationMod(0.3F).alwaysEat().build()),
	                    new MobEffectInstance[]{
	                            new MobEffectInstance(net.langball.coffee.init.ModEffects.CAFFEINE.get(), 4800, 0),
	                    }, 3, () -> ModItems.CUP_GLASS.get()));

	    public static final RegistryObject<Item> COFFEE_COLDBREW_LATTE = ITEMS.register("coffee_coldbrew_latte",
	            () -> new DrinkCoffee(
	                    new Item.Properties().food(new FoodProperties.Builder().nutrition(2).saturationMod(0.2F).alwaysEat().build()),
	                    new MobEffectInstance[]{
	                            new MobEffectInstance(net.langball.coffee.init.ModEffects.CAFFEINE.get(), 4800, 0),
	                            new MobEffectInstance(net.langball.coffee.init.ModEffects.RELAX.get(), 800, 0),
	                    }, 3, () -> ModItems.CUP_GLASS.get()));

	    public static final RegistryObject<Item> COFFEE_COLDBREW_LATTE_CARAMEL = ITEMS.register("coffee_coldbrew_latte_caramel",
	            () -> new DrinkCoffee(
	                    new Item.Properties().food(new FoodProperties.Builder().nutrition(2).saturationMod(0.3F).alwaysEat().build()),
	                    new MobEffectInstance[]{
	                            new MobEffectInstance(net.langball.coffee.init.ModEffects.CAFFEINE.get(), 4800, 0),
	                            new MobEffectInstance(net.langball.coffee.init.ModEffects.RELAX.get(), 800, 0),
	                    }, 3, () -> ModItems.CUP_GLASS.get()));

	    public static final RegistryObject<Item> COFFEE_COLDBREW_LATTE_CHOCOLATE = ITEMS.register("coffee_coldbrew_latte_chocolate",
	            () -> new DrinkCoffee(
	                    new Item.Properties().food(new FoodProperties.Builder().nutrition(3).saturationMod(0.3F).alwaysEat().build()),
	                    new MobEffectInstance[]{
	                            new MobEffectInstance(net.langball.coffee.init.ModEffects.CAFFEINE.get(), 4800, 0),
	                            new MobEffectInstance(net.langball.coffee.init.ModEffects.RELAX.get(), 800, 0),
	                    }, 3, () -> ModItems.CUP_GLASS.get()));

	    public static final RegistryObject<Item> COFFEE_COLDBREW_LATTE_FRUIT = ITEMS.register("coffee_coldbrew_latte_fruit",
	            () -> new DrinkCoffee(
	                    new Item.Properties().food(new FoodProperties.Builder().nutrition(3).saturationMod(0.3F).alwaysEat().build()),
	                    new MobEffectInstance[]{
	                            new MobEffectInstance(net.langball.coffee.init.ModEffects.CAFFEINE.get(), 4800, 0),
	                            new MobEffectInstance(net.langball.coffee.init.ModEffects.RELAX.get(), 800, 0),
	                    }, 3, () -> ModItems.CUP_GLASS.get()));

	    public static final RegistryObject<Item> COFFEE_COLDBREW_LATTE_MINT = ITEMS.register("coffee_coldbrew_latte_mint",
	            () -> new DrinkCoffee(
	                    new Item.Properties().food(new FoodProperties.Builder().nutrition(2).saturationMod(0.3F).alwaysEat().build()),
	                    new MobEffectInstance[]{
	                            new MobEffectInstance(net.langball.coffee.init.ModEffects.CAFFEINE.get(), 4800, 0),
	                            new MobEffectInstance(net.langball.coffee.init.ModEffects.RELAX.get(), 800, 0),
	                    }, 3, () -> ModItems.CUP_GLASS.get()));

	    public static final RegistryObject<Item> COFFEE_COLDBREW_LATTE_VANILLA = ITEMS.register("coffee_coldbrew_latte_vanilla",
	            () -> new DrinkCoffee(
	                    new Item.Properties().food(new FoodProperties.Builder().nutrition(2).saturationMod(0.3F).alwaysEat().build()),
	                    new MobEffectInstance[]{
	                            new MobEffectInstance(net.langball.coffee.init.ModEffects.CAFFEINE.get(), 4800, 0),
	                            new MobEffectInstance(net.langball.coffee.init.ModEffects.RELAX.get(), 800, 0),
	                    }, 3, () -> ModItems.CUP_GLASS.get()));

	    // ========================================================================
	    // Coldbrew extensions (iced)
	    // ========================================================================
	    public static final RegistryObject<Item> COFFEE_COLDBREW_FRUIT_ICE = ITEMS.register("coffee_coldbrew_fruit_ice",
	            () -> new DrinkCoffee(
	                    new Item.Properties().food(new FoodProperties.Builder().nutrition(3).saturationMod(0.3F).alwaysEat().build()),
	                    new MobEffectInstance[]{
	                            new MobEffectInstance(net.langball.coffee.init.ModEffects.CAFFEINE.get(), 3200, 0),
	                    }, 3, () -> ModItems.CUP_GLASS.get()));

	    public static final RegistryObject<Item> COFFEE_COLDBREW_LATTE_ICE = ITEMS.register("coffee_coldbrew_latte_ice",
	            () -> new DrinkCoffee(
	                    new Item.Properties().food(new FoodProperties.Builder().nutrition(2).saturationMod(0.2F).alwaysEat().build()),
	                    new MobEffectInstance[]{
	                            new MobEffectInstance(net.langball.coffee.init.ModEffects.CAFFEINE.get(), 3200, 0),
	                            new MobEffectInstance(net.langball.coffee.init.ModEffects.RELAX.get(), 500, 0),
	                    }, 3, () -> ModItems.CUP_GLASS.get()));

	    public static final RegistryObject<Item> COFFEE_COLDBREW_LATTE_CARAMEL_ICE = ITEMS.register("coffee_coldbrew_latte_caramel_ice",
	            () -> new DrinkCoffee(
	                    new Item.Properties().food(new FoodProperties.Builder().nutrition(2).saturationMod(0.3F).alwaysEat().build()),
	                    new MobEffectInstance[]{
	                            new MobEffectInstance(net.langball.coffee.init.ModEffects.CAFFEINE.get(), 3200, 0),
	                            new MobEffectInstance(net.langball.coffee.init.ModEffects.RELAX.get(), 500, 0),
	                    }, 3, () -> ModItems.CUP_GLASS.get()));

	    public static final RegistryObject<Item> COFFEE_COLDBREW_LATTE_CHOCOLATE_ICE = ITEMS.register("coffee_coldbrew_latte_chocolate_ice",
	            () -> new DrinkCoffee(
	                    new Item.Properties().food(new FoodProperties.Builder().nutrition(3).saturationMod(0.3F).alwaysEat().build()),
	                    new MobEffectInstance[]{
	                            new MobEffectInstance(net.langball.coffee.init.ModEffects.CAFFEINE.get(), 3200, 0),
	                            new MobEffectInstance(net.langball.coffee.init.ModEffects.RELAX.get(), 500, 0),
	                    }, 3, () -> ModItems.CUP_GLASS.get()));

	    public static final RegistryObject<Item> COFFEE_COLDBREW_LATTE_FRUIT_ICE = ITEMS.register("coffee_coldbrew_latte_fruit_ice",
	            () -> new DrinkCoffee(
	                    new Item.Properties().food(new FoodProperties.Builder().nutrition(3).saturationMod(0.3F).alwaysEat().build()),
	                    new MobEffectInstance[]{
	                            new MobEffectInstance(net.langball.coffee.init.ModEffects.CAFFEINE.get(), 3200, 0),
	                            new MobEffectInstance(net.langball.coffee.init.ModEffects.RELAX.get(), 500, 0),
	                    }, 3, () -> ModItems.CUP_GLASS.get()));

	    public static final RegistryObject<Item> COFFEE_COLDBREW_LATTE_MINT_ICE = ITEMS.register("coffee_coldbrew_latte_mint_ice",
	            () -> new DrinkCoffee(
	                    new Item.Properties().food(new FoodProperties.Builder().nutrition(2).saturationMod(0.3F).alwaysEat().build()),
	                    new MobEffectInstance[]{
	                            new MobEffectInstance(net.langball.coffee.init.ModEffects.CAFFEINE.get(), 3200, 0),
	                            new MobEffectInstance(net.langball.coffee.init.ModEffects.RELAX.get(), 500, 0),
	                    }, 3, () -> ModItems.CUP_GLASS.get()));

	    public static final RegistryObject<Item> COFFEE_COLDBREW_LATTE_VANILLA_ICE = ITEMS.register("coffee_coldbrew_latte_vanilla_ice",
	            () -> new DrinkCoffee(
	                    new Item.Properties().food(new FoodProperties.Builder().nutrition(2).saturationMod(0.3F).alwaysEat().build()),
	                    new MobEffectInstance[]{
	                            new MobEffectInstance(net.langball.coffee.init.ModEffects.CAFFEINE.get(), 3200, 0),
	                            new MobEffectInstance(net.langball.coffee.init.ModEffects.RELAX.get(), 500, 0),
	                    }, 3, () -> ModItems.CUP_GLASS.get()));

    // ========================================================================
    // Empty cups (returned when finishing a drink)
    // ========================================================================
    public static final RegistryObject<Item> CUP = ITEMS.register("cup",
            () -> new Item(new Item.Properties().stacksTo(16)));
    public static final RegistryObject<Item> CUP_GLASS = ITEMS.register("cup_glass",
            () -> new Item(new Item.Properties().stacksTo(16)));

    // ========================================================================
    // Tools & Specials
    // ========================================================================
    public static final RegistryObject<Item> VANILLA = ITEMS.register("vanilla",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> BAG = ITEMS.register("bag",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> SYRUP_EMPTY = ITEMS.register("syrup_empty",
            () -> new Item(new Item.Properties().stacksTo(16)));

    // Tea leaves
    public static final RegistryObject<Item> TEA_LEAF = ITEMS.register("tea_leaf",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> BLACK_TEA_LEAF = ITEMS.register("black_tea_leaf",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> IRON_BOWL = ITEMS.register("iron_bowl",
            () -> new Item(new Item.Properties().stacksTo(16)) {
                @Override public boolean hasCraftingRemainingItem() { return true; }
                @Override public ItemStack getCraftingRemainingItem(ItemStack stack) { return stack.copyWithCount(1); }
            });
    public static final RegistryObject<Item> CAKE_MODEL = ITEMS.register("cake_model",
            () -> new Item(new Item.Properties().stacksTo(16)) {
                @Override public boolean hasCraftingRemainingItem() { return true; }
                @Override public ItemStack getCraftingRemainingItem(ItemStack stack) { return stack.copyWithCount(1); }
            });
    public static final RegistryObject<Item> CAKE_MODEL_SQUARE = ITEMS.register("cake_model_square",
            () -> new Item(new Item.Properties().stacksTo(16)) {
                @Override public boolean hasCraftingRemainingItem() { return true; }
                @Override public ItemStack getCraftingRemainingItem(ItemStack stack) { return stack.copyWithCount(1); }
            });
    public static final RegistryObject<Item> CAKE_MODEL_PLATE = ITEMS.register("cake_model_plate",
            () -> new Item(new Item.Properties().stacksTo(16)) {
                @Override public boolean hasCraftingRemainingItem() { return true; }
                @Override public ItemStack getCraftingRemainingItem(ItemStack stack) { return stack.copyWithCount(1); }
            });
    public static final RegistryObject<Item> SMALL_MODEL = ITEMS.register("small_model",
            () -> new Item(new Item.Properties().stacksTo(16)) {
                @Override public boolean hasCraftingRemainingItem() { return true; }
                @Override public ItemStack getCraftingRemainingItem(ItemStack stack) { return stack.copyWithCount(1); }
            });
    public static final RegistryObject<Item> MOONCAKE_MODEL = ITEMS.register("mooncake_model",
            () -> new Item(new Item.Properties()) {
                @Override public boolean hasCraftingRemainingItem() { return true; }
                @Override public ItemStack getCraftingRemainingItem(ItemStack stack) { return stack.copyWithCount(1); }
            });
    public static final RegistryObject<Item> MIXING_BOWL = ITEMS.register("mixing_bowl",
            () -> new Item(new Item.Properties()) {
                @Override public boolean hasCraftingRemainingItem() { return true; }
                @Override public ItemStack getCraftingRemainingItem(ItemStack stack) { return stack.copyWithCount(1); }
            });

    // Records
    public static final RegistryObject<Item> RECORD_BLANK = ITEMS.register("record_blank",
            () -> new Item(new Item.Properties().stacksTo(1).rarity(Rarity.RARE)));
    public static final RegistryObject<Item> RECORD_KUSA_NOSHI_TO_NE = ITEMS.register("record_kusa_noshi_to_ne",
            () -> new ItemRecordCW(1,
                    ModSounds.RECORD_KUSA_NOSHI_TO_NE,
                    new Item.Properties().rarity(Rarity.RARE),
                    "item.coffeework.record_kusa_noshi_to_ne.desc", 100));
    public static final RegistryObject<Item> RECORD_LAZY_LADY_KAGUYA = ITEMS.register("record_lazy_lady_kaguya",
            () -> new ItemRecordCW(1,
                    ModSounds.RECORD_LAZY_LADY_KAGUYA,
                    new Item.Properties().rarity(Rarity.RARE),
                    "item.coffeework.record_lazy_lady_kaguya.desc", 100));
    public static final RegistryObject<Item> RECORD_THE_GRIMOIRE_OF_MARISA = ITEMS.register("record_the_grimoire_of_marisa",
            () -> new ItemRecordCW(1,
                    ModSounds.RECORD_THE_GRIMOIRE_OF_MARISA,
                    new Item.Properties().rarity(Rarity.RARE),
                    "item.coffeework.record_the_grimoire_of_marisa.desc", 100));
}
