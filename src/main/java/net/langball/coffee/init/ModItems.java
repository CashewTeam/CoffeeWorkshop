package net.langball.coffee.init;

import net.langball.coffee.CoffeeWork;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

/**
 * Central item registry hub.
 *
 * All item registrations are delegated to categorized sub-files
 * ({@link ModCoffeeItems}, {@link ModBakeryItems}, {@link ModIngredientItems},
 * {@link ModEquipmentItems}) via their {@code registerAll()} methods.
 *
 * Field declarations remain here for backward-compatible access
 * ({@code ModItems.COFFEE_BEAN.get()}) across the entire codebase.
 */
public class ModItems {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, CoffeeWork.MODID);

    // ========================================================================
    // BlockItems (machines, plants, decor, bags, cakes) — ModEquipmentItems
    // ========================================================================
    public static RegistryObject<Item> GRINDER;
    public static RegistryObject<Item> COFFEE_MACHINE;
    public static RegistryObject<Item> ICECREAM_MACHINE;
    public static RegistryObject<Item> ROLLER;
    public static RegistryObject<Item> OVEN;
    public static RegistryObject<Item> COFFEE_TREE_ITEM;
    public static RegistryObject<Item> BLUEBERRY_BUSH_ITEM;
    public static RegistryObject<Item> PLATE;
    public static RegistryObject<Item> COLD_BREW_POT;
    public static RegistryObject<Item> SODA_ORE;
    public static RegistryObject<Item> XMAS_TREE;
    public static RegistryObject<Item> GINGER_HOUSE;
    public static RegistryObject<Item> BAG_COFFEE;
    public static RegistryObject<Item> BAG_COFFEE_RAW;
    public static RegistryObject<Item> BAG_COCOA;
    public static RegistryObject<Item> BAG_COCOA_POWDER;
    public static RegistryObject<Item> BAG_FLOUR;
    public static RegistryObject<Item> BAG_COFFEE_POWDER;
    public static RegistryObject<Item> BAG_SUGAR;
    public static RegistryObject<Item> DOUBLE_BAG_COFFEE;
    public static RegistryObject<Item> DOUBLE_BAG_COFFEE_RAW;
    public static RegistryObject<Item> DOUBLE_BAG_COCOA;
    public static RegistryObject<Item> DOUBLE_BAG_COCOA_POWDER;
    public static RegistryObject<Item> DOUBLE_BAG_FLOUR;
    public static RegistryObject<Item> DOUBLE_BAG_COFFEE_POWDER;
    public static RegistryObject<Item> DOUBLE_BAG_SUGAR;
    public static RegistryObject<Item> CAKE_SPONGE;
    public static RegistryObject<Item> CAKE_SPONGE_CHOCOLATE;
    public static RegistryObject<Item> CAKE_SPONGE_COFFEE;
    public static RegistryObject<Item> CAKE_SPONGE_PUMPKIN;
    public static RegistryObject<Item> CAKE_SPONGE_CARROT;
    public static RegistryObject<Item> CAKE_SPONGE_REDVELVET;
    public static RegistryObject<Item> CAKE_SPONGE_LEMON;
    public static RegistryObject<Item> CAKE_SPONGE_TEA;
    public static RegistryObject<Item> CAKE_SPONGE_BERRY;
    public static RegistryObject<Item> CAKE_COFFEE;
    public static RegistryObject<Item> CAKE_HARVEST;
    public static RegistryObject<Item> CAKE_LEMON;
    public static RegistryObject<Item> CAKE_TEA;
    public static RegistryObject<Item> CAKE_BERRY;
    public static RegistryObject<Item> CAKE_CHEESE;
    public static RegistryObject<Item> CAKE_SCHWARZWALD;
    public static RegistryObject<Item> CAKE_REDVELVET;
    public static RegistryObject<Item> TIRAMISU;
    public static RegistryObject<Item> MOUSSE_BERRY;
    public static RegistryObject<Item> MOUSSE_LEMON;
    public static RegistryObject<Item> MOUSSE_CHOCOLATE;
    public static RegistryObject<Item> MOUSSE_COFFEE;

    // Phase 5.4
    // Phase 5.4: Cake block
    public static RegistryObject<Item> CAKE_CARROT;

    // Phase 5.4: Cake rolls
    public static RegistryObject<Item> CAKE_ROLL;
    public static RegistryObject<Item> CAKE_BERRY_ROLL;
    public static RegistryObject<Item> CAKE_CARROT_ROLL;
    public static RegistryObject<Item> CAKE_CHOCOLATE_ROLL;
    public static RegistryObject<Item> CAKE_COFFEE_ROLL;
    public static RegistryObject<Item> CAKE_LEMON_ROLL;
    public static RegistryObject<Item> CAKE_PUMPKIN_ROLL;
    public static RegistryObject<Item> CAKE_REDVELVET_ROLL;
    public static RegistryObject<Item> CAKE_TEA_ROLL;

    // Phase 5.4: Cake slice items
    public static RegistryObject<Item> CAKE_SLICES;
    public static RegistryObject<Item> CAKE_BERRY_SLICES;
    public static RegistryObject<Item> CAKE_CHEESE_SLICES;
    public static RegistryObject<Item> CAKE_COFFEE_SLICES;
    public static RegistryObject<Item> CAKE_HARVEST_SLICES;
    public static RegistryObject<Item> CAKE_LEMON_SLICES;
    public static RegistryObject<Item> CAKE_REDVELVET_SLICES;
    public static RegistryObject<Item> CAKE_SCHWARZWALD_SLICES;
    public static RegistryObject<Item> CAKE_TEA_SLICES;
    public static RegistryObject<Item> CAKE_SPONGE_BERRY_SLICES;
    public static RegistryObject<Item> CAKE_SPONGE_CARROT_SLICES;
    public static RegistryObject<Item> CAKE_SPONGE_CHOCOLATE_SLICES;
    public static RegistryObject<Item> CAKE_SPONGE_COFFEE_SLICES;
    public static RegistryObject<Item> CAKE_SPONGE_LEMON_SLICES;
    public static RegistryObject<Item> CAKE_SPONGE_PUMPKIN_SLICES;
    public static RegistryObject<Item> CAKE_SPONGE_REDVELVET_SLICES;
    public static RegistryObject<Item> CAKE_SPONGE_TEA_SLICES;

    // Phase 5.4: Cake intermediates (raw/model)
    public static RegistryObject<Item> CAKE_SPONGE_RAW;
    public static RegistryObject<Item> CAKE_CHEESE_RAW;
    public static RegistryObject<Item> TIRAMISU_RAW;
    public static RegistryObject<Item> CAKE_SPONGE_BERRY_RAW;
    public static RegistryObject<Item> CAKE_SPONGE_CHOCOLATE_RAW;
    public static RegistryObject<Item> CAKE_SPONGE_COFFEE_RAW;
    public static RegistryObject<Item> MOUSSE_BERRY_RAW;
    public static RegistryObject<Item> MOUSSE_CHOCOLATE_RAW;
    public static RegistryObject<Item> MOUSSE_COFFEE_RAW;

    // ========================================================================
    // Seeds — ModEquipmentItems
    // ========================================================================
    public static RegistryObject<Item> COFFEE_SEEDS;
    public static RegistryObject<Item> VANILLA_SEEDS;

    // ========================================================================
    // Materials — ModIngredientItems
    // ========================================================================
    public static RegistryObject<Item> COCOA_BEAN;
    public static RegistryObject<Item> COCOA_POWDER;
    public static RegistryObject<Item> COCOA_BATTER;
    public static RegistryObject<Item> BAG_CLOTH;
    public static RegistryObject<Item> ICE_SLAG;
    public static RegistryObject<Item> YEAST;
    public static RegistryObject<Item> PLATE_IRON;
    public static RegistryObject<Item> SPICES;
    public static RegistryObject<Item> GELATIN;
    public static RegistryObject<Item> SODA;
    public static RegistryObject<Item> FLOUR;
    public static RegistryObject<Item> DOUGH;
    public static RegistryObject<Item> DOUGH_PASTRY;
    public static RegistryObject<Item> DOUGH_COOKIE;
    public static RegistryObject<Item> DOUGH_GINGER;
    public static RegistryObject<Item> DOUGH_BREAD;
    public static RegistryObject<Item> DOUGH_BREAD_ROUND;
    public static RegistryObject<Item> DOUGH_BAGUETTE;
    public static RegistryObject<Item> DOUGH_BAGEL;
    public static RegistryObject<Item> DOUGH_TOAST;
    public static RegistryObject<Item> PLATE_DOUGH;
    public static RegistryObject<Item> PLATE_DOUGH_PASTRY;
    public static RegistryObject<Item> PLATE_DOUGH_GINGER;
    public static RegistryObject<Item> EMPTY_COLDBREW_POT;
    public static RegistryObject<Item> COLDBREW_BOTTLE;

    // ========================================================================
    // Foods — ModBakeryItems
    // ========================================================================
    public static RegistryObject<Item> BREAD_ROUND;
    public static RegistryObject<Item> BAGUETTE;
    public static RegistryObject<Item> BAGEL;
    public static RegistryObject<Item> TOAST;
    public static RegistryObject<Item> BUTTER;
    public static RegistryObject<Item> CHEESE;
    public static RegistryObject<Item> BLUEBERRY;
    public static RegistryObject<Item> CHOCOLATE_BAR;
    public static RegistryObject<Item> CHOCOLATE_CHIP;
    public static RegistryObject<Item> BROWNIE;
    public static RegistryObject<Item> FIELD_RATION;
    public static RegistryObject<Item> CAKE_SPONGE_SLICE;
    public static RegistryObject<Item> PIE_CREAM;
    public static RegistryObject<Item> SANDWICH_BLT;
    public static RegistryObject<Item> SANDWICH_BACON_EGG;
    public static RegistryObject<Item> SANDWICH_BEEF_CHEESE;
    public static RegistryObject<Item> SANDWICH_BLT_LARGE;
    public static RegistryObject<Item> SANDWICH_CLUB;
    public static RegistryObject<Item> SANDWICH_CLUB_LARGE;
    public static RegistryObject<Item> SANDWICH_HAM_CHEESE;
    public static RegistryObject<Item> ICECREAM_MIX_VANILLA;
    public static RegistryObject<Item> ICECREAM_VANILLA;
    public static RegistryObject<Item> ICECREAM_APPLE;
    public static RegistryObject<Item> ICECREAM_BERRY;
    public static RegistryObject<Item> ICECREAM_CHOCOLATE;
    public static RegistryObject<Item> ICECREAM_COFFEE;
    public static RegistryObject<Item> ICECREAM_LEMON;
    public static RegistryObject<Item> ICECREAM_MELON;

    // ========================================================================
    // Coffee & Drinks — ModCoffeeItems
    // ========================================================================
    public static RegistryObject<Item> COFFEE_BEAN_RAW;
    public static RegistryObject<Item> COFFEE_BEAN;
    public static RegistryObject<Item> COFFEE_POWDER;
    public static RegistryObject<Item> COFFEE_INSTANT;
    public static RegistryObject<Item> COFFEE_INSTANT_STICK;
    public static RegistryObject<Item> COFFEE_INSTANT_BOX;
    public static RegistryObject<Item> COFFEE_INSTANT_CUP_UNOPEN;
    public static RegistryObject<Item> COFFEE_INSTANT_CUP;
    public static RegistryObject<Item> ESPRESSO;
    public static RegistryObject<Item> COFFEE_AMERICANO;
    public static RegistryObject<Item> COFFEE_LATTE;
    public static RegistryObject<Item> COFFEE_CAPPUCCINO;
    public static RegistryObject<Item> COFFEE_MACCHIATO;
    public static RegistryObject<Item> COFFEE_MOCHACCINO;
    public static RegistryObject<Item> COFFEE_GREEN_TEA;
    public static RegistryObject<Item> COFFEE_BLACK_TEA;
    public static RegistryObject<Item> COFFEE_MILK_TEA;
    public static RegistryObject<Item> COFFEE_MANDARIN_DRINK;
    public static RegistryObject<Item> COFFEE_COLDBREW;
    public static RegistryObject<Item> COCOA;
    public static RegistryObject<Item> COCOA_STRONG;
    // Iced variants
    public static RegistryObject<Item> COFFEE_AMERICANO_ICE;
    public static RegistryObject<Item> COFFEE_LATTE_ICE;
    public static RegistryObject<Item> COFFEE_CAPPUCCINO_ICE;
    public static RegistryObject<Item> COFFEE_MACCHIATO_ICE;
    public static RegistryObject<Item> COFFEE_MOCHACCINO_ICE;
    public static RegistryObject<Item> COFFEE_GREEN_TEA_ICE;
    public static RegistryObject<Item> COFFEE_BLACK_TEA_ICE;
    public static RegistryObject<Item> COFFEE_MILK_TEA_ICE;
    public static RegistryObject<Item> COFFEE_MANDARIN_DRINK_ICE;
    public static RegistryObject<Item> COFFEE_COLDBREW_ICE;
    public static RegistryObject<Item> COCOA_ICE;
    public static RegistryObject<Item> COCOA_STRONG_ICE;
    // Flavored lattes
    public static RegistryObject<Item> COFFEE_LATTE_CARAMEL;
    public static RegistryObject<Item> COFFEE_LATTE_CHOCOLATE;
    public static RegistryObject<Item> COFFEE_LATTE_FRUIT;
    public static RegistryObject<Item> COFFEE_LATTE_MINT;
    public static RegistryObject<Item> COFFEE_LATTE_VANILLA;
    public static RegistryObject<Item> COFFEE_LATTE_SAKURA;
    public static RegistryObject<Item> COFFEE_LATTE_CARAMEL_ICE;
    public static RegistryObject<Item> COFFEE_LATTE_CHOCOLATE_ICE;
    public static RegistryObject<Item> COFFEE_LATTE_FRUIT_ICE;
    public static RegistryObject<Item> COFFEE_LATTE_MINT_ICE;
    public static RegistryObject<Item> COFFEE_LATTE_VANILLA_ICE;
    public static RegistryObject<Item> COFFEE_LATTE_SAKURA_ICE;
    // Americano extensions
    public static RegistryObject<Item> COFFEE_AMERICANO_FRUIT;
    public static RegistryObject<Item> COFFEE_AMERICANO_FRUIT_ICE;
    public static RegistryObject<Item> COFFEE_AMERICANO_NITRO_ICE;
    public static RegistryObject<Item> COFFEE_AMERICANO_NITRO_FRUIT_ICE;
    // Coldbrew extensions
    public static RegistryObject<Item> COFFEE_COLDBREW_FRUIT;
    public static RegistryObject<Item> COFFEE_COLDBREW_LATTE;
    public static RegistryObject<Item> COFFEE_COLDBREW_LATTE_CARAMEL;
    public static RegistryObject<Item> COFFEE_COLDBREW_LATTE_CHOCOLATE;
    public static RegistryObject<Item> COFFEE_COLDBREW_LATTE_FRUIT;
    public static RegistryObject<Item> COFFEE_COLDBREW_LATTE_MINT;
    public static RegistryObject<Item> COFFEE_COLDBREW_LATTE_VANILLA;
    public static RegistryObject<Item> COFFEE_COLDBREW_FRUIT_ICE;
    public static RegistryObject<Item> COFFEE_COLDBREW_LATTE_ICE;
    public static RegistryObject<Item> COFFEE_COLDBREW_LATTE_CARAMEL_ICE;
    public static RegistryObject<Item> COFFEE_COLDBREW_LATTE_CHOCOLATE_ICE;
    public static RegistryObject<Item> COFFEE_COLDBREW_LATTE_FRUIT_ICE;
    public static RegistryObject<Item> COFFEE_COLDBREW_LATTE_MINT_ICE;
    public static RegistryObject<Item> COFFEE_COLDBREW_LATTE_VANILLA_ICE;

    // ========================================================================
    // Empty cups — ModIngredientItems
    // ========================================================================
    public static RegistryObject<Item> CUP;
    public static RegistryObject<Item> CUP_GLASS;

    // ========================================================================
    // Tools & Specials — ModIngredientItems
    // ========================================================================
    public static RegistryObject<Item> VANILLA;
    public static RegistryObject<Item> BAG;
    public static RegistryObject<Item> LEMON;
    public static RegistryObject<Item> CARAMEL;
    public static RegistryObject<Item> CUSTARD;
    public static RegistryObject<Item> MILK_FORM;
    public static RegistryObject<Item> POT;

    // ========================================================================
    // Bakery (Phase 5.3 additions)
    // ========================================================================
    public static RegistryObject<Item> CARAMEL_APPLE;
    public static RegistryObject<Item> HARDTACK;
    public static RegistryObject<Item> COOKIE_BLACK;
    public static RegistryObject<Item> COOKIE_OREO;
    public static RegistryObject<Item> MARSHMALLOW;
    public static RegistryObject<Item> MARSHMALLOW_ROAST;
    public static RegistryObject<Item> MARSHMALLOW_CHOCOLATE;
    public static RegistryObject<Item> SMORE;
    public static RegistryObject<Item> SYRUP_EMPTY;
    public static RegistryObject<Item> SYRUP_CARAMEL;
    public static RegistryObject<Item> SYRUP_CHOCOLATE;
    public static RegistryObject<Item> SYRUP_FRUIT;
    public static RegistryObject<Item> SYRUP_MINT;
    public static RegistryObject<Item> SYRUP_VANILLA;
    public static RegistryObject<Item> SYRUP_SAKURA;
    public static RegistryObject<Item> TEA_LEAF;
    public static RegistryObject<Item> BLACK_TEA_LEAF;
    public static RegistryObject<Item> IRON_BOWL;
    public static RegistryObject<Item> CAKE_MODEL;
    public static RegistryObject<Item> CAKE_MODEL_SQUARE;
    public static RegistryObject<Item> CAKE_MODEL_PLATE;
    public static RegistryObject<Item> SMALL_MODEL;
    public static RegistryObject<Item> MOONCAKE_MODEL;
    public static RegistryObject<Item> MIXING_BOWL;

    // ========================================================================
    // Records — ModEquipmentItems
    // ========================================================================
    public static RegistryObject<Item> RECORD_BLANK;
    public static RegistryObject<Item> RECORD_KUSA_NOSHI_TO_NE;
    public static RegistryObject<Item> RECORD_LAZY_LADY_KAGUYA;
    public static RegistryObject<Item> RECORD_THE_GRIMOIRE_OF_MARISA;

    // ========================================================================
    // Registration bootstrap
    // ========================================================================
    static {
        ModEquipmentItems.registerAll(ITEMS);
        ModIngredientItems.registerAll(ITEMS);
        ModBakeryItems.registerAll(ITEMS);
        ModCoffeeItems.registerAll(ITEMS);
    }
}
