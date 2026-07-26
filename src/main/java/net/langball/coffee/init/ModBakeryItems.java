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

        // Phase 5.4: Cake rolls (9 variants)
        ModItems.CAKE_ROLL = items.register("cake_roll",
                () -> new Item(new Item.Properties().food(new FoodProperties.Builder().nutrition(4).saturationMod(0.4F).build())));
        ModItems.CAKE_BERRY_ROLL = items.register("cake_berry_roll",
                () -> new Item(new Item.Properties().food(new FoodProperties.Builder().nutrition(4).saturationMod(0.4F).build())));
        ModItems.CAKE_CARROT_ROLL = items.register("cake_carrot_roll",
                () -> new Item(new Item.Properties().food(new FoodProperties.Builder().nutrition(4).saturationMod(0.4F).build())));
        ModItems.CAKE_CHOCOLATE_ROLL = items.register("cake_chocolate_roll",
                () -> new Item(new Item.Properties().food(new FoodProperties.Builder().nutrition(4).saturationMod(0.4F).build())));
        ModItems.CAKE_COFFEE_ROLL = items.register("cake_coffee_roll",
                () -> new Item(new Item.Properties().food(new FoodProperties.Builder().nutrition(4).saturationMod(0.4F).build())));
        ModItems.CAKE_LEMON_ROLL = items.register("cake_lemon_roll",
                () -> new Item(new Item.Properties().food(new FoodProperties.Builder().nutrition(4).saturationMod(0.4F).build())));
        ModItems.CAKE_PUMPKIN_ROLL = items.register("cake_pumpkin_roll",
                () -> new Item(new Item.Properties().food(new FoodProperties.Builder().nutrition(4).saturationMod(0.4F).build())));
        ModItems.CAKE_REDVELVET_ROLL = items.register("cake_redvelvet_roll",
                () -> new Item(new Item.Properties().food(new FoodProperties.Builder().nutrition(4).saturationMod(0.4F).build())));
        ModItems.CAKE_TEA_ROLL = items.register("cake_tea_roll",
                () -> new Item(new Item.Properties().food(new FoodProperties.Builder().nutrition(4).saturationMod(0.4F).build())));

        // Phase 5.4: Cake slices (for cutting from cake blocks)
        ModItems.CAKE_SLICES = items.register("cake_slices",
                () -> new Item(new Item.Properties().food(new FoodProperties.Builder().nutrition(1).saturationMod(0.1F).build())));
        ModItems.CAKE_BERRY_SLICES = items.register("cake_berry_slices",
                () -> new Item(new Item.Properties().food(new FoodProperties.Builder().nutrition(1).saturationMod(0.1F).build())));
        ModItems.CAKE_CHEESE_SLICES = items.register("cake_cheese_slices",
                () -> new Item(new Item.Properties().food(new FoodProperties.Builder().nutrition(1).saturationMod(0.1F).build())));
        ModItems.CAKE_COFFEE_SLICES = items.register("cake_coffee_slices",
                () -> new Item(new Item.Properties().food(new FoodProperties.Builder().nutrition(1).saturationMod(0.1F).build())));
        ModItems.CAKE_HARVEST_SLICES = items.register("cake_harvest_slices",
                () -> new Item(new Item.Properties().food(new FoodProperties.Builder().nutrition(1).saturationMod(0.1F).build())));
        ModItems.CAKE_LEMON_SLICES = items.register("cake_lemon_slices",
                () -> new Item(new Item.Properties().food(new FoodProperties.Builder().nutrition(1).saturationMod(0.1F).build())));
        ModItems.CAKE_REDVELVET_SLICES = items.register("cake_redvelvet_slices",
                () -> new Item(new Item.Properties().food(new FoodProperties.Builder().nutrition(1).saturationMod(0.1F).build())));
        ModItems.CAKE_SCHWARZWALD_SLICES = items.register("cake_schwarzwald_slices",
                () -> new Item(new Item.Properties().food(new FoodProperties.Builder().nutrition(1).saturationMod(0.1F).build())));
        ModItems.CAKE_TEA_SLICES = items.register("cake_tea_slices",
                () -> new Item(new Item.Properties().food(new FoodProperties.Builder().nutrition(1).saturationMod(0.1F).build())));

        // Phase 5.4: Sponge slices
        ModItems.CAKE_SPONGE_BERRY_SLICES = items.register("cake_sponge_berry_slices",
                () -> new Item(new Item.Properties().food(new FoodProperties.Builder().nutrition(1).saturationMod(0.1F).build())));
        ModItems.CAKE_SPONGE_CARROT_SLICES = items.register("cake_sponge_carrot_slices",
                () -> new Item(new Item.Properties().food(new FoodProperties.Builder().nutrition(1).saturationMod(0.1F).build())));
        ModItems.CAKE_SPONGE_CHOCOLATE_SLICES = items.register("cake_sponge_chocolate_slices",
                () -> new Item(new Item.Properties().food(new FoodProperties.Builder().nutrition(1).saturationMod(0.1F).build())));
        ModItems.CAKE_SPONGE_COFFEE_SLICES = items.register("cake_sponge_coffee_slices",
                () -> new Item(new Item.Properties().food(new FoodProperties.Builder().nutrition(1).saturationMod(0.1F).build())));
        ModItems.CAKE_SPONGE_LEMON_SLICES = items.register("cake_sponge_lemon_slices",
                () -> new Item(new Item.Properties().food(new FoodProperties.Builder().nutrition(1).saturationMod(0.1F).build())));
        ModItems.CAKE_SPONGE_PUMPKIN_SLICES = items.register("cake_sponge_pumpkin_slices",
                () -> new Item(new Item.Properties().food(new FoodProperties.Builder().nutrition(1).saturationMod(0.1F).build())));
        ModItems.CAKE_SPONGE_REDVELVET_SLICES = items.register("cake_sponge_redvelvet_slices",
                () -> new Item(new Item.Properties().food(new FoodProperties.Builder().nutrition(1).saturationMod(0.1F).build())));
        ModItems.CAKE_SPONGE_TEA_SLICES = items.register("cake_sponge_tea_slices",
                () -> new Item(new Item.Properties().food(new FoodProperties.Builder().nutrition(1).saturationMod(0.1F).build())));

        // Phase 5.4: Key intermediates (raw/model/base for registered cakes)
        ModItems.CAKE_SPONGE_RAW = items.register("cake_sponge_raw",
                () -> new Item(new Item.Properties()));
        ModItems.CAKE_CHEESE_RAW = items.register("cake_cheese_raw",
                () -> new Item(new Item.Properties()));
        ModItems.TIRAMISU_RAW = items.register("tiramisu_raw",
                () -> new Item(new Item.Properties()));
        // Sponge raw variants (for flavored sponges)
        ModItems.CAKE_SPONGE_BERRY_RAW = items.register("cake_sponge_berry_raw",
                () -> new Item(new Item.Properties()));
        ModItems.CAKE_SPONGE_CHOCOLATE_RAW = items.register("cake_sponge_chocolate_raw",
                () -> new Item(new Item.Properties()));
        ModItems.CAKE_SPONGE_COFFEE_RAW = items.register("cake_sponge_coffee_raw",
                () -> new Item(new Item.Properties()));
        // Mousse raws
        ModItems.MOUSSE_BERRY_RAW = items.register("mousse_berry_raw",
                () -> new Item(new Item.Properties()));
        ModItems.MOUSSE_CHOCOLATE_RAW = items.register("mousse_chocolate_raw",
                () -> new Item(new Item.Properties()));
        ModItems.MOUSSE_COFFEE_RAW = items.register("mousse_coffee_raw",
                () -> new Item(new Item.Properties()));

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
