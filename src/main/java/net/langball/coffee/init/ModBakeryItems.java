package net.langball.coffee.init;

import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

/**
 * Bakery-related items: breads, doughs, pastries, pies, sandwiches, ice cream.
 */
public class ModBakeryItems {

    static void registerAll(DeferredRegister<Item> items) {
        // ── Breads ──
        ModItems.BREAD_ROUND = items.register("bread_round",
                () -> new Item(new Item.Properties().food(new FoodProperties.Builder().nutrition(6).saturationMod(0.6F).build())));
        ModItems.BAGUETTE = items.register("baguette",
                () -> new Item(new Item.Properties().food(new FoodProperties.Builder().nutrition(9).saturationMod(0.6F).build())));
        ModItems.BAGEL = items.register("bagel",
                () -> new Item(new Item.Properties().food(new FoodProperties.Builder().nutrition(7).saturationMod(0.6F).build())));
        ModItems.TOAST = items.register("toast",
                () -> new Item(new Item.Properties().food(new FoodProperties.Builder().nutrition(8).saturationMod(0.6F).build())));

        // ── Dairy & sweets ──
        ModItems.BUTTER = items.register("butter",
                () -> new Item(new Item.Properties().food(new FoodProperties.Builder().nutrition(2).saturationMod(0.4F).build())));
        ModItems.CHEESE = items.register("cheese",
                () -> new Item(new Item.Properties().food(new FoodProperties.Builder().nutrition(3).saturationMod(0.4F).build())));
        ModItems.BLUEBERRY = items.register("blueberry",
                () -> new Item(new Item.Properties().food(new FoodProperties.Builder().nutrition(1).saturationMod(0.5F).fast().build())));
        ModItems.CHOCOLATE_BAR = items.register("chocolate_bar",
                () -> new Item(new Item.Properties().food(new FoodProperties.Builder().nutrition(4).saturationMod(0.5F).build())));
        ModItems.CHOCOLATE_CHIP = items.register("chocolate_chip",
                () -> new Item(new Item.Properties().food(new FoodProperties.Builder().nutrition(4).saturationMod(0.5F).build())));
        ModItems.BROWNIE = items.register("brownie",
                () -> new Item(new Item.Properties().food(new FoodProperties.Builder().nutrition(9).saturationMod(0.5F).build())));
        ModItems.FIELD_RATION = items.register("field_ration",
                () -> new Item(new Item.Properties().food(new FoodProperties.Builder().nutrition(8).saturationMod(1.0F).build())));

        // ── Cake slices ──
        ModItems.CAKE_SPONGE_SLICE = items.register("cake_sponge_slice",
                () -> new Item(new Item.Properties().food(new FoodProperties.Builder().nutrition(1).saturationMod(0.1F).build())));

        // ── Pies ──
        ModItems.PIE_CREAM = items.register("pie_cream",
                () -> new Item(new Item.Properties().food(new FoodProperties.Builder().nutrition(10).saturationMod(0.6F).build())));

        // ── Sandwiches ──
        ModItems.SANDWICH_BLT = items.register("sandwich_blt",
                () -> new Item(new Item.Properties().food(new FoodProperties.Builder().nutrition(10).saturationMod(0.9F).build())));

        // Phase 5.2-C: Six sandwich variants
        ModItems.SANDWICH_BACON_EGG = items.register("sandwich_bacon_egg",
                () -> new Item(new Item.Properties().food(new FoodProperties.Builder().nutrition(10).saturationMod(0.9F).build())));
        ModItems.SANDWICH_BEEF_CHEESE = items.register("sandwich_beef_cheese",
                () -> new Item(new Item.Properties().food(new FoodProperties.Builder().nutrition(12).saturationMod(1.0F).build())));
        ModItems.SANDWICH_BLT_LARGE = items.register("sandwich_blt_large",
                () -> new Item(new Item.Properties().food(new FoodProperties.Builder().nutrition(14).saturationMod(1.0F).build())));
        ModItems.SANDWICH_CLUB = items.register("sandwich_club",
                () -> new Item(new Item.Properties().food(new FoodProperties.Builder().nutrition(12).saturationMod(1.0F).build())));
        ModItems.SANDWICH_CLUB_LARGE = items.register("sandwich_club_large",
                () -> new Item(new Item.Properties().food(new FoodProperties.Builder().nutrition(16).saturationMod(1.0F).build())));
        ModItems.SANDWICH_HAM_CHEESE = items.register("sandwich_ham_cheese",
                () -> new Item(new Item.Properties().food(new FoodProperties.Builder().nutrition(10).saturationMod(0.9F).build())));

        // Phase 5.3: Confectionery & bakery foundation
        ModItems.CARAMEL_APPLE = items.register("caramel_apple",
                () -> new Item(new Item.Properties().food(new FoodProperties.Builder().nutrition(6).saturationMod(0.5F).build())));
        ModItems.HARDTACK = items.register("hardtack",
                () -> new Item(new Item.Properties().food(new FoodProperties.Builder().nutrition(3).saturationMod(0.6F).build())));
        ModItems.COOKIE_BLACK = items.register("cookie_black",
                () -> new Item(new Item.Properties().food(new FoodProperties.Builder().nutrition(2).saturationMod(0.3F).fast().build())));
        ModItems.COOKIE_OREO = items.register("cookie_oreo",
                () -> new Item(new Item.Properties().food(new FoodProperties.Builder().nutrition(4).saturationMod(0.4F).build())));
        ModItems.MARSHMALLOW = items.register("marshmallow",
                () -> new Item(new Item.Properties().food(new FoodProperties.Builder().nutrition(2).saturationMod(0.2F).fast().build())));
        ModItems.MARSHMALLOW_ROAST = items.register("marshmallow_roast",
                () -> new Item(new Item.Properties().food(new FoodProperties.Builder().nutrition(3).saturationMod(0.3F).build())));
        ModItems.MARSHMALLOW_CHOCOLATE = items.register("marshmallow_chocolate",
                () -> new Item(new Item.Properties().food(new FoodProperties.Builder().nutrition(4).saturationMod(0.4F).build())));
        ModItems.SMORE = items.register("smore",
                () -> new Item(new Item.Properties().food(new FoodProperties.Builder().nutrition(7).saturationMod(0.6F).build())));

        // ── Ice creams ──
        ModItems.ICECREAM_MIX_VANILLA = items.register("icecream_mix_vanilla",
                () -> new Item(new Item.Properties().stacksTo(16)));
        ModItems.ICECREAM_VANILLA = items.register("icecream_vanilla",
                () -> new Item(new Item.Properties().food(new FoodProperties.Builder().nutrition(4).saturationMod(0.5F).build())));

        // Phase 5.2-B: Six flavored ice creams
        ModItems.ICECREAM_APPLE = items.register("icecream_apple",
                () -> new Item(new Item.Properties().food(new FoodProperties.Builder().nutrition(4).saturationMod(0.5F).build())));
        ModItems.ICECREAM_BERRY = items.register("icecream_berry",
                () -> new Item(new Item.Properties().food(new FoodProperties.Builder().nutrition(4).saturationMod(0.5F).build())));
        ModItems.ICECREAM_CHOCOLATE = items.register("icecream_chocolate",
                () -> new Item(new Item.Properties().food(new FoodProperties.Builder().nutrition(4).saturationMod(0.5F).build())));
        ModItems.ICECREAM_COFFEE = items.register("icecream_coffee",
                () -> new Item(new Item.Properties().food(new FoodProperties.Builder().nutrition(4).saturationMod(0.5F).build())));
        ModItems.ICECREAM_LEMON = items.register("icecream_lemon",
                () -> new Item(new Item.Properties().food(new FoodProperties.Builder().nutrition(4).saturationMod(0.5F).build())));
        ModItems.ICECREAM_MELON = items.register("icecream_melon",
                () -> new Item(new Item.Properties().food(new FoodProperties.Builder().nutrition(4).saturationMod(0.5F).build())));
    }
}
