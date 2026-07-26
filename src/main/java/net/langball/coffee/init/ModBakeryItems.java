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

        // ── Ice creams ──
        ModItems.ICECREAM_MIX_VANILLA = items.register("icecream_mix_vanilla",
                () -> new Item(new Item.Properties().stacksTo(16)));
        ModItems.ICECREAM_VANILLA = items.register("icecream_vanilla",
                () -> new Item(new Item.Properties().food(new FoodProperties.Builder().nutrition(4).saturationMod(0.5F).build())));
    }
}
