package net.langball.coffee.init;

import net.langball.coffee.item.DrinkCoffee;
import net.langball.coffee.item.DrinkCoffeeInstant;
import net.langball.coffee.item.DrinkEspresso;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

/**
 * Coffee-related items: beans, powders, drinks, tea leaves, cocoa drinks.
 * All fields are assigned into {@link ModItems} for backward-compatible access.
 */
public class ModCoffeeItems {

    static void registerAll(DeferredRegister<Item> items) {
        // ── Coffee beans, powders ──
        ModItems.COFFEE_BEAN_RAW = items.register("coffee_bean_raw",
                () -> new Item(new Item.Properties().food(new FoodProperties.Builder().nutrition(1).saturationMod(0.2F).build())));
        ModItems.COFFEE_BEAN = items.register("coffee_bean",
                () -> new Item(new Item.Properties().food(new FoodProperties.Builder().nutrition(1).saturationMod(0.2F).fast().build())));
        ModItems.COFFEE_POWDER = items.register("coffee_powder",
                () -> new Item(new Item.Properties()));

        // ── Instant coffee ──
        ModItems.COFFEE_INSTANT = items.register("coffee_instant",
                () -> new DrinkCoffeeInstant(
                        new Item.Properties().food(new FoodProperties.Builder().nutrition(2).saturationMod(0.2F).alwaysEat().build()),
                        new MobEffectInstance[]{}, 3, false));

        ModItems.COFFEE_INSTANT_STICK = items.register("coffee_instant_stick",
                () -> new Item(new Item.Properties().stacksTo(16)));

        ModItems.COFFEE_INSTANT_BOX = items.register("coffee_instant_box",
                () -> new Item(new Item.Properties()));

        // ── Cup-based instant coffee (Phase 5.2-A) ──
        ModItems.COFFEE_INSTANT_CUP_UNOPEN = items.register("coffee_instant_cup_unopen",
                () -> new Item(new Item.Properties().stacksTo(16)));

        ModItems.COFFEE_INSTANT_CUP = items.register("coffee_instant_cup",
                () -> new DrinkCoffeeInstant(
                        new Item.Properties().food(new FoodProperties.Builder().nutrition(2).saturationMod(0.2F).alwaysEat().build()),
                        new MobEffectInstance[]{}, 3, true, () -> ModItems.CUP.get()));

        // ── Espresso ──
        ModItems.ESPRESSO = items.register("espresso",
                () -> new DrinkEspresso(
                        new Item.Properties().food(new FoodProperties.Builder().nutrition(1).saturationMod(0.1F).alwaysEat().build()),
                        new MobEffectInstance[]{
                                new MobEffectInstance(ModEffects.CAFFEINE.get(), 4800, 1),
                        }, 2, () -> ModItems.CUP.get()));

        // ── Americano (hot) ──
        ModItems.COFFEE_AMERICANO = items.register("coffee_americano",
                () -> new DrinkCoffee(
                        new Item.Properties().food(new FoodProperties.Builder().nutrition(2).saturationMod(0.2F).alwaysEat().build()),
                        new MobEffectInstance[]{
                                new MobEffectInstance(ModEffects.CAFFEINE.get(), 3600, 0),
                                new MobEffectInstance(ModEffects.RELAX.get(), 600, 1),
                        }, 4, () -> ModItems.CUP.get()));

        // ── Basic hot drinks ──
        ModItems.COFFEE_LATTE = items.register("coffee_latte",
                () -> new DrinkCoffee(
                        new Item.Properties().food(new FoodProperties.Builder().nutrition(2).saturationMod(0.2F).alwaysEat().build()),
                        new MobEffectInstance[]{
                                new MobEffectInstance(ModEffects.CAFFEINE.get(), 3600, 0),
                                new MobEffectInstance(ModEffects.RELAX.get(), 1200, 0),
                        }, 4, () -> ModItems.CUP.get()));

        ModItems.COFFEE_CAPPUCCINO = items.register("coffee_cappuccino",
                () -> new DrinkCoffee(
                        new Item.Properties().food(new FoodProperties.Builder().nutrition(2).saturationMod(0.3F).alwaysEat().build()),
                        new MobEffectInstance[]{
                                new MobEffectInstance(ModEffects.CAFFEINE.get(), 2400, 0),
                                new MobEffectInstance(ModEffects.RELAX.get(), 1800, 0),
                        }, 3, () -> ModItems.CUP.get()));

        ModItems.COFFEE_MACCHIATO = items.register("coffee_macchiato",
                () -> new DrinkCoffee(
                        new Item.Properties().food(new FoodProperties.Builder().nutrition(2).saturationMod(0.3F).alwaysEat().build()),
                        new MobEffectInstance[]{
                                new MobEffectInstance(ModEffects.CAFFEINE.get(), 3600, 0),
                                new MobEffectInstance(ModEffects.RELAX.get(), 1200, 0),
                                new MobEffectInstance(net.minecraft.world.effect.MobEffects.MOVEMENT_SPEED, 600, 0),
                        }, 3, () -> ModItems.CUP.get()));

        ModItems.COFFEE_MOCHACCINO = items.register("coffee_mochaccino",
                () -> new DrinkCoffee(
                        new Item.Properties().food(new FoodProperties.Builder().nutrition(3).saturationMod(0.3F).alwaysEat().build()),
                        new MobEffectInstance[]{
                                new MobEffectInstance(ModEffects.CAFFEINE.get(), 2400, 0),
                                new MobEffectInstance(ModEffects.RELAX.get(), 1800, 0),
                        }, 3, () -> ModItems.CUP.get()));

        // ── Tea-based drinks (served in glass) ──
        ModItems.COFFEE_GREEN_TEA = items.register("coffee_green_tea",
                () -> new DrinkCoffee(
                        new Item.Properties().food(new FoodProperties.Builder().nutrition(1).saturationMod(0.1F).alwaysEat().build()),
                        new MobEffectInstance[]{
                                new MobEffectInstance(ModEffects.RELAX.get(), 2400, 0),
                        }, 3, () -> ModItems.CUP_GLASS.get()));

        ModItems.COFFEE_BLACK_TEA = items.register("coffee_black_tea",
                () -> new DrinkCoffee(
                        new Item.Properties().food(new FoodProperties.Builder().nutrition(1).saturationMod(0.1F).alwaysEat().build()),
                        new MobEffectInstance[]{
                                new MobEffectInstance(ModEffects.RELAX.get(), 3600, 0),
                        }, 3, () -> ModItems.CUP_GLASS.get()));

        ModItems.COFFEE_MILK_TEA = items.register("coffee_milk_tea",
                () -> new DrinkCoffee(
                        new Item.Properties().food(new FoodProperties.Builder().nutrition(2).saturationMod(0.2F).alwaysEat().build()),
                        new MobEffectInstance[]{
                                new MobEffectInstance(ModEffects.RELAX.get(), 3600, 0),
                        }, 3, () -> ModItems.CUP_GLASS.get()));

        ModItems.COFFEE_MANDARIN_DRINK = items.register("coffee_mandarin_drink",
                () -> new DrinkCoffee(
                        new Item.Properties().food(new FoodProperties.Builder().nutrition(2).saturationMod(0.2F).alwaysEat().build()),
                        new MobEffectInstance[]{
                                new MobEffectInstance(ModEffects.CAFFEINE.get(), 2400, 0),
                                new MobEffectInstance(ModEffects.RELAX.get(), 2400, 0),
                        }, 3, () -> ModItems.CUP_GLASS.get()));

        // ── Cold brew (served in glass) ──
        ModItems.COFFEE_COLDBREW = items.register("coffee_coldbrew",
                () -> new DrinkCoffee(
                        new Item.Properties().food(new FoodProperties.Builder().nutrition(2).saturationMod(0.2F).alwaysEat().build()),
                        new MobEffectInstance[]{
                                new MobEffectInstance(ModEffects.CAFFEINE.get(), 4800, 0),
                        }, 3, () -> ModItems.CUP_GLASS.get()));

        // ── Cocoa drinks (served in cup) ──
        ModItems.COCOA = items.register("cocoa",
                () -> new DrinkCoffee(
                        new Item.Properties().food(new FoodProperties.Builder().nutrition(3).saturationMod(0.3F).alwaysEat().build()),
                        new MobEffectInstance[]{
                                new MobEffectInstance(net.minecraft.world.effect.MobEffects.SATURATION, 1200, 0),
                        }, 3, () -> ModItems.CUP.get()));

        ModItems.COCOA_STRONG = items.register("cocoa_strong",
                () -> new DrinkCoffee(
                        new Item.Properties().food(new FoodProperties.Builder().nutrition(4).saturationMod(0.4F).alwaysEat().build()),
                        new MobEffectInstance[]{
                                new MobEffectInstance(net.minecraft.world.effect.MobEffects.SATURATION, 2400, 0),
                                new MobEffectInstance(net.minecraft.world.effect.MobEffects.MOVEMENT_SPEED, 1200, 0),
                        }, 2, () -> ModItems.CUP.get()));

        // ── Extensions: flavored lattes, iced variants, coldbrew variants ──
        // (Ice/Hot variants kept together for readability)
        registerIcedVariants(items);
        registerFlavoredLattes(items);
        registerAmericanoExtensions(items);
        registerColdbrewExtensions(items);
    }

    // ── Iced drink variants ──────────────────────────────────────

    private static void registerIcedVariants(DeferredRegister<Item> items) {
        ModItems.COFFEE_AMERICANO_ICE = items.register("coffee_americano_ice",
                () -> new DrinkCoffee(
                        new Item.Properties().food(new FoodProperties.Builder().nutrition(2).saturationMod(0.2F).alwaysEat().build()),
                        new MobEffectInstance[]{
                                new MobEffectInstance(ModEffects.CAFFEINE.get(), 2400, 0),
                                new MobEffectInstance(ModEffects.RELAX.get(), 400, 1),
                        }, 4, () -> ModItems.CUP_GLASS.get()));

        ModItems.COFFEE_LATTE_ICE = items.register("coffee_latte_ice",
                () -> new DrinkCoffee(
                        new Item.Properties().food(new FoodProperties.Builder().nutrition(2).saturationMod(0.2F).alwaysEat().build()),
                        new MobEffectInstance[]{
                                new MobEffectInstance(ModEffects.CAFFEINE.get(), 2400, 0),
                                new MobEffectInstance(ModEffects.RELAX.get(), 800, 0),
                        }, 4, () -> ModItems.CUP_GLASS.get()));

        ModItems.COFFEE_CAPPUCCINO_ICE = items.register("coffee_cappuccino_ice",
                () -> new DrinkCoffee(
                        new Item.Properties().food(new FoodProperties.Builder().nutrition(2).saturationMod(0.3F).alwaysEat().build()),
                        new MobEffectInstance[]{
                                new MobEffectInstance(ModEffects.CAFFEINE.get(), 1600, 0),
                                new MobEffectInstance(ModEffects.RELAX.get(), 1200, 0),
                        }, 3, () -> ModItems.CUP_GLASS.get()));

        ModItems.COFFEE_MACCHIATO_ICE = items.register("coffee_macchiato_ice",
                () -> new DrinkCoffee(
                        new Item.Properties().food(new FoodProperties.Builder().nutrition(2).saturationMod(0.3F).alwaysEat().build()),
                        new MobEffectInstance[]{
                                new MobEffectInstance(ModEffects.CAFFEINE.get(), 2400, 0),
                                new MobEffectInstance(ModEffects.RELAX.get(), 800, 0),
                                new MobEffectInstance(net.minecraft.world.effect.MobEffects.MOVEMENT_SPEED, 400, 0),
                        }, 3, () -> ModItems.CUP_GLASS.get()));

        ModItems.COFFEE_MOCHACCINO_ICE = items.register("coffee_mochaccino_ice",
                () -> new DrinkCoffee(
                        new Item.Properties().food(new FoodProperties.Builder().nutrition(3).saturationMod(0.3F).alwaysEat().build()),
                        new MobEffectInstance[]{
                                new MobEffectInstance(ModEffects.CAFFEINE.get(), 1600, 0),
                                new MobEffectInstance(ModEffects.RELAX.get(), 1200, 0),
                        }, 3, () -> ModItems.CUP_GLASS.get()));

        ModItems.COFFEE_GREEN_TEA_ICE = items.register("coffee_green_tea_ice",
                () -> new DrinkCoffee(
                        new Item.Properties().food(new FoodProperties.Builder().nutrition(1).saturationMod(0.1F).alwaysEat().build()),
                        new MobEffectInstance[]{
                                new MobEffectInstance(ModEffects.RELAX.get(), 1600, 0),
                        }, 3, () -> ModItems.CUP_GLASS.get()));

        ModItems.COFFEE_BLACK_TEA_ICE = items.register("coffee_black_tea_ice",
                () -> new DrinkCoffee(
                        new Item.Properties().food(new FoodProperties.Builder().nutrition(1).saturationMod(0.1F).alwaysEat().build()),
                        new MobEffectInstance[]{
                                new MobEffectInstance(ModEffects.RELAX.get(), 2400, 0),
                        }, 3, () -> ModItems.CUP_GLASS.get()));

        ModItems.COFFEE_MILK_TEA_ICE = items.register("coffee_milk_tea_ice",
                () -> new DrinkCoffee(
                        new Item.Properties().food(new FoodProperties.Builder().nutrition(2).saturationMod(0.2F).alwaysEat().build()),
                        new MobEffectInstance[]{
                                new MobEffectInstance(ModEffects.RELAX.get(), 2400, 0),
                        }, 3, () -> ModItems.CUP_GLASS.get()));

        ModItems.COFFEE_MANDARIN_DRINK_ICE = items.register("coffee_mandarin_drink_ice",
                () -> new DrinkCoffee(
                        new Item.Properties().food(new FoodProperties.Builder().nutrition(2).saturationMod(0.2F).alwaysEat().build()),
                        new MobEffectInstance[]{
                                new MobEffectInstance(ModEffects.CAFFEINE.get(), 1600, 0),
                                new MobEffectInstance(ModEffects.RELAX.get(), 1600, 0),
                        }, 3, () -> ModItems.CUP_GLASS.get()));

        ModItems.COFFEE_COLDBREW_ICE = items.register("coffee_coldbrew_ice",
                () -> new DrinkCoffee(
                        new Item.Properties().food(new FoodProperties.Builder().nutrition(2).saturationMod(0.2F).alwaysEat().build()),
                        new MobEffectInstance[]{
                                new MobEffectInstance(ModEffects.CAFFEINE.get(), 3200, 0),
                        }, 3, () -> ModItems.CUP_GLASS.get()));

        ModItems.COCOA_ICE = items.register("cocoa_ice",
                () -> new DrinkCoffee(
                        new Item.Properties().food(new FoodProperties.Builder().nutrition(3).saturationMod(0.3F).alwaysEat().build()),
                        new MobEffectInstance[]{
                                new MobEffectInstance(net.minecraft.world.effect.MobEffects.SATURATION, 800, 0),
                        }, 3, () -> ModItems.CUP_GLASS.get()));

        ModItems.COCOA_STRONG_ICE = items.register("cocoa_strong_ice",
                () -> new DrinkCoffee(
                        new Item.Properties().food(new FoodProperties.Builder().nutrition(4).saturationMod(0.4F).alwaysEat().build()),
                        new MobEffectInstance[]{
                                new MobEffectInstance(net.minecraft.world.effect.MobEffects.SATURATION, 1600, 0),
                                new MobEffectInstance(net.minecraft.world.effect.MobEffects.MOVEMENT_SPEED, 800, 0),
                        }, 2, () -> ModItems.CUP_GLASS.get()));
    }

    // ── Flavored latte variants ──────────────────────────────────

    private static void registerFlavoredLattes(DeferredRegister<Item> items) {
        // Hot, served in cup
        ModItems.COFFEE_LATTE_CARAMEL = items.register("coffee_latte_caramel",
                () -> new DrinkCoffee(
                        new Item.Properties().food(new FoodProperties.Builder().nutrition(2).saturationMod(0.3F).alwaysEat().build()),
                        new MobEffectInstance[]{
                                new MobEffectInstance(ModEffects.CAFFEINE.get(), 3600, 0),
                                new MobEffectInstance(ModEffects.RELAX.get(), 1200, 0),
                                new MobEffectInstance(net.minecraft.world.effect.MobEffects.DIG_SPEED, 600, 0),
                        }, 4, () -> ModItems.CUP.get()));

        ModItems.COFFEE_LATTE_CHOCOLATE = items.register("coffee_latte_chocolate",
                () -> new DrinkCoffee(
                        new Item.Properties().food(new FoodProperties.Builder().nutrition(3).saturationMod(0.3F).alwaysEat().build()),
                        new MobEffectInstance[]{
                                new MobEffectInstance(ModEffects.CAFFEINE.get(), 3600, 0),
                                new MobEffectInstance(ModEffects.RELAX.get(), 1200, 0),
                        }, 4, () -> ModItems.CUP.get()));

        ModItems.COFFEE_LATTE_FRUIT = items.register("coffee_latte_fruit",
                () -> new DrinkCoffee(
                        new Item.Properties().food(new FoodProperties.Builder().nutrition(3).saturationMod(0.3F).alwaysEat().build()),
                        new MobEffectInstance[]{
                                new MobEffectInstance(ModEffects.CAFFEINE.get(), 3600, 0),
                                new MobEffectInstance(ModEffects.RELAX.get(), 1200, 0),
                        }, 4, () -> ModItems.CUP.get()));

        ModItems.COFFEE_LATTE_MINT = items.register("coffee_latte_mint",
                () -> new DrinkCoffee(
                        new Item.Properties().food(new FoodProperties.Builder().nutrition(2).saturationMod(0.3F).alwaysEat().build()),
                        new MobEffectInstance[]{
                                new MobEffectInstance(ModEffects.CAFFEINE.get(), 3600, 0),
                                new MobEffectInstance(ModEffects.RELAX.get(), 1200, 0),
                        }, 4, () -> ModItems.CUP.get()));

        ModItems.COFFEE_LATTE_VANILLA = items.register("coffee_latte_vanilla",
                () -> new DrinkCoffee(
                        new Item.Properties().food(new FoodProperties.Builder().nutrition(2).saturationMod(0.3F).alwaysEat().build()),
                        new MobEffectInstance[]{
                                new MobEffectInstance(ModEffects.CAFFEINE.get(), 3600, 0),
                                new MobEffectInstance(ModEffects.RELAX.get(), 1200, 0),
                        }, 4, () -> ModItems.CUP.get()));

        ModItems.COFFEE_LATTE_SAKURA = items.register("coffee_latte_sakura",
                () -> new DrinkCoffee(
                        new Item.Properties().food(new FoodProperties.Builder().nutrition(2).saturationMod(0.3F).alwaysEat().build()),
                        new MobEffectInstance[]{
                                new MobEffectInstance(ModEffects.CAFFEINE.get(), 3600, 0),
                                new MobEffectInstance(ModEffects.RELAX.get(), 1200, 0),
                                new MobEffectInstance(net.minecraft.world.effect.MobEffects.LUCK, 1200, 0),
                        }, 4, () -> ModItems.CUP.get()));

        // Iced, served in glass
        ModItems.COFFEE_LATTE_CARAMEL_ICE = items.register("coffee_latte_caramel_ice",
                () -> new DrinkCoffee(
                        new Item.Properties().food(new FoodProperties.Builder().nutrition(2).saturationMod(0.3F).alwaysEat().build()),
                        new MobEffectInstance[]{
                                new MobEffectInstance(ModEffects.CAFFEINE.get(), 2400, 0),
                                new MobEffectInstance(ModEffects.RELAX.get(), 800, 0),
                                new MobEffectInstance(net.minecraft.world.effect.MobEffects.DIG_SPEED, 400, 0),
                        }, 4, () -> ModItems.CUP_GLASS.get()));

        ModItems.COFFEE_LATTE_CHOCOLATE_ICE = items.register("coffee_latte_chocolate_ice",
                () -> new DrinkCoffee(
                        new Item.Properties().food(new FoodProperties.Builder().nutrition(3).saturationMod(0.3F).alwaysEat().build()),
                        new MobEffectInstance[]{
                                new MobEffectInstance(ModEffects.CAFFEINE.get(), 2400, 0),
                                new MobEffectInstance(ModEffects.RELAX.get(), 800, 0),
                        }, 4, () -> ModItems.CUP_GLASS.get()));

        ModItems.COFFEE_LATTE_FRUIT_ICE = items.register("coffee_latte_fruit_ice",
                () -> new DrinkCoffee(
                        new Item.Properties().food(new FoodProperties.Builder().nutrition(3).saturationMod(0.3F).alwaysEat().build()),
                        new MobEffectInstance[]{
                                new MobEffectInstance(ModEffects.CAFFEINE.get(), 2400, 0),
                                new MobEffectInstance(ModEffects.RELAX.get(), 800, 0),
                        }, 4, () -> ModItems.CUP_GLASS.get()));

        ModItems.COFFEE_LATTE_MINT_ICE = items.register("coffee_latte_mint_ice",
                () -> new DrinkCoffee(
                        new Item.Properties().food(new FoodProperties.Builder().nutrition(2).saturationMod(0.3F).alwaysEat().build()),
                        new MobEffectInstance[]{
                                new MobEffectInstance(ModEffects.CAFFEINE.get(), 2400, 0),
                                new MobEffectInstance(ModEffects.RELAX.get(), 800, 0),
                        }, 4, () -> ModItems.CUP_GLASS.get()));

        ModItems.COFFEE_LATTE_VANILLA_ICE = items.register("coffee_latte_vanilla_ice",
                () -> new DrinkCoffee(
                        new Item.Properties().food(new FoodProperties.Builder().nutrition(2).saturationMod(0.3F).alwaysEat().build()),
                        new MobEffectInstance[]{
                                new MobEffectInstance(ModEffects.CAFFEINE.get(), 2400, 0),
                                new MobEffectInstance(ModEffects.RELAX.get(), 800, 0),
                        }, 4, () -> ModItems.CUP_GLASS.get()));

        ModItems.COFFEE_LATTE_SAKURA_ICE = items.register("coffee_latte_sakura_ice",
                () -> new DrinkCoffee(
                        new Item.Properties().food(new FoodProperties.Builder().nutrition(2).saturationMod(0.3F).alwaysEat().build()),
                        new MobEffectInstance[]{
                                new MobEffectInstance(ModEffects.CAFFEINE.get(), 2400, 0),
                                new MobEffectInstance(ModEffects.RELAX.get(), 800, 0),
                                new MobEffectInstance(net.minecraft.world.effect.MobEffects.LUCK, 800, 0),
                        }, 4, () -> ModItems.CUP_GLASS.get()));
    }

    // ── Americano extensions ─────────────────────────────────────

    private static void registerAmericanoExtensions(DeferredRegister<Item> items) {
        ModItems.COFFEE_AMERICANO_FRUIT = items.register("coffee_americano_fruit",
                () -> new DrinkCoffee(
                        new Item.Properties().food(new FoodProperties.Builder().nutrition(3).saturationMod(0.3F).alwaysEat().build()),
                        new MobEffectInstance[]{
                                new MobEffectInstance(ModEffects.CAFFEINE.get(), 3600, 0),
                                new MobEffectInstance(ModEffects.RELAX.get(), 600, 1),
                        }, 4, () -> ModItems.CUP.get()));

        ModItems.COFFEE_AMERICANO_FRUIT_ICE = items.register("coffee_americano_fruit_ice",
                () -> new DrinkCoffee(
                        new Item.Properties().food(new FoodProperties.Builder().nutrition(3).saturationMod(0.3F).alwaysEat().build()),
                        new MobEffectInstance[]{
                                new MobEffectInstance(ModEffects.CAFFEINE.get(), 2400, 0),
                                new MobEffectInstance(ModEffects.RELAX.get(), 400, 1),
                        }, 4, () -> ModItems.CUP_GLASS.get()));

        ModItems.COFFEE_AMERICANO_NITRO_ICE = items.register("coffee_americano_nitro_ice",
                () -> new DrinkCoffee(
                        new Item.Properties().food(new FoodProperties.Builder().nutrition(2).saturationMod(0.2F).alwaysEat().build()),
                        new MobEffectInstance[]{
                                new MobEffectInstance(ModEffects.CAFFEINE.get(), 2400, 0),
                                new MobEffectInstance(ModEffects.RELAX.get(), 400, 1),
                                new MobEffectInstance(net.minecraft.world.effect.MobEffects.MOVEMENT_SPEED, 600, 1),
                        }, 4, () -> ModItems.CUP_GLASS.get()));

        ModItems.COFFEE_AMERICANO_NITRO_FRUIT_ICE = items.register("coffee_americano_nitro_fruit_ice",
                () -> new DrinkCoffee(
                        new Item.Properties().food(new FoodProperties.Builder().nutrition(3).saturationMod(0.3F).alwaysEat().build()),
                        new MobEffectInstance[]{
                                new MobEffectInstance(ModEffects.CAFFEINE.get(), 2400, 0),
                                new MobEffectInstance(ModEffects.RELAX.get(), 400, 1),
                                new MobEffectInstance(net.minecraft.world.effect.MobEffects.MOVEMENT_SPEED, 600, 1),
                        }, 4, () -> ModItems.CUP_GLASS.get()));
    }

    // ── Coldbrew extensions ──────────────────────────────────────

    private static void registerColdbrewExtensions(DeferredRegister<Item> items) {
        ModItems.COFFEE_COLDBREW_FRUIT = items.register("coffee_coldbrew_fruit",
                () -> new DrinkCoffee(
                        new Item.Properties().food(new FoodProperties.Builder().nutrition(3).saturationMod(0.3F).alwaysEat().build()),
                        new MobEffectInstance[]{
                                new MobEffectInstance(ModEffects.CAFFEINE.get(), 4800, 0),
                        }, 3, () -> ModItems.CUP_GLASS.get()));

        ModItems.COFFEE_COLDBREW_LATTE = items.register("coffee_coldbrew_latte",
                () -> new DrinkCoffee(
                        new Item.Properties().food(new FoodProperties.Builder().nutrition(2).saturationMod(0.2F).alwaysEat().build()),
                        new MobEffectInstance[]{
                                new MobEffectInstance(ModEffects.CAFFEINE.get(), 4800, 0),
                                new MobEffectInstance(ModEffects.RELAX.get(), 800, 0),
                        }, 3, () -> ModItems.CUP_GLASS.get()));

        ModItems.COFFEE_COLDBREW_LATTE_CARAMEL = items.register("coffee_coldbrew_latte_caramel",
                () -> new DrinkCoffee(
                        new Item.Properties().food(new FoodProperties.Builder().nutrition(2).saturationMod(0.3F).alwaysEat().build()),
                        new MobEffectInstance[]{
                                new MobEffectInstance(ModEffects.CAFFEINE.get(), 4800, 0),
                                new MobEffectInstance(ModEffects.RELAX.get(), 800, 0),
                        }, 3, () -> ModItems.CUP_GLASS.get()));

        ModItems.COFFEE_COLDBREW_LATTE_CHOCOLATE = items.register("coffee_coldbrew_latte_chocolate",
                () -> new DrinkCoffee(
                        new Item.Properties().food(new FoodProperties.Builder().nutrition(3).saturationMod(0.3F).alwaysEat().build()),
                        new MobEffectInstance[]{
                                new MobEffectInstance(ModEffects.CAFFEINE.get(), 4800, 0),
                                new MobEffectInstance(ModEffects.RELAX.get(), 800, 0),
                        }, 3, () -> ModItems.CUP_GLASS.get()));

        ModItems.COFFEE_COLDBREW_LATTE_FRUIT = items.register("coffee_coldbrew_latte_fruit",
                () -> new DrinkCoffee(
                        new Item.Properties().food(new FoodProperties.Builder().nutrition(3).saturationMod(0.3F).alwaysEat().build()),
                        new MobEffectInstance[]{
                                new MobEffectInstance(ModEffects.CAFFEINE.get(), 4800, 0),
                                new MobEffectInstance(ModEffects.RELAX.get(), 800, 0),
                        }, 3, () -> ModItems.CUP_GLASS.get()));

        ModItems.COFFEE_COLDBREW_LATTE_MINT = items.register("coffee_coldbrew_latte_mint",
                () -> new DrinkCoffee(
                        new Item.Properties().food(new FoodProperties.Builder().nutrition(2).saturationMod(0.3F).alwaysEat().build()),
                        new MobEffectInstance[]{
                                new MobEffectInstance(ModEffects.CAFFEINE.get(), 4800, 0),
                                new MobEffectInstance(ModEffects.RELAX.get(), 800, 0),
                        }, 3, () -> ModItems.CUP_GLASS.get()));

        ModItems.COFFEE_COLDBREW_LATTE_VANILLA = items.register("coffee_coldbrew_latte_vanilla",
                () -> new DrinkCoffee(
                        new Item.Properties().food(new FoodProperties.Builder().nutrition(2).saturationMod(0.3F).alwaysEat().build()),
                        new MobEffectInstance[]{
                                new MobEffectInstance(ModEffects.CAFFEINE.get(), 4800, 0),
                                new MobEffectInstance(ModEffects.RELAX.get(), 800, 0),
                        }, 3, () -> ModItems.CUP_GLASS.get()));

        // Iced coldbrew extensions
        ModItems.COFFEE_COLDBREW_FRUIT_ICE = items.register("coffee_coldbrew_fruit_ice",
                () -> new DrinkCoffee(
                        new Item.Properties().food(new FoodProperties.Builder().nutrition(3).saturationMod(0.3F).alwaysEat().build()),
                        new MobEffectInstance[]{
                                new MobEffectInstance(ModEffects.CAFFEINE.get(), 3200, 0),
                        }, 3, () -> ModItems.CUP_GLASS.get()));

        ModItems.COFFEE_COLDBREW_LATTE_ICE = items.register("coffee_coldbrew_latte_ice",
                () -> new DrinkCoffee(
                        new Item.Properties().food(new FoodProperties.Builder().nutrition(2).saturationMod(0.2F).alwaysEat().build()),
                        new MobEffectInstance[]{
                                new MobEffectInstance(ModEffects.CAFFEINE.get(), 3200, 0),
                                new MobEffectInstance(ModEffects.RELAX.get(), 500, 0),
                        }, 3, () -> ModItems.CUP_GLASS.get()));

        ModItems.COFFEE_COLDBREW_LATTE_CARAMEL_ICE = items.register("coffee_coldbrew_latte_caramel_ice",
                () -> new DrinkCoffee(
                        new Item.Properties().food(new FoodProperties.Builder().nutrition(2).saturationMod(0.3F).alwaysEat().build()),
                        new MobEffectInstance[]{
                                new MobEffectInstance(ModEffects.CAFFEINE.get(), 3200, 0),
                                new MobEffectInstance(ModEffects.RELAX.get(), 500, 0),
                        }, 3, () -> ModItems.CUP_GLASS.get()));

        ModItems.COFFEE_COLDBREW_LATTE_CHOCOLATE_ICE = items.register("coffee_coldbrew_latte_chocolate_ice",
                () -> new DrinkCoffee(
                        new Item.Properties().food(new FoodProperties.Builder().nutrition(3).saturationMod(0.3F).alwaysEat().build()),
                        new MobEffectInstance[]{
                                new MobEffectInstance(ModEffects.CAFFEINE.get(), 3200, 0),
                                new MobEffectInstance(ModEffects.RELAX.get(), 500, 0),
                        }, 3, () -> ModItems.CUP_GLASS.get()));

        ModItems.COFFEE_COLDBREW_LATTE_FRUIT_ICE = items.register("coffee_coldbrew_latte_fruit_ice",
                () -> new DrinkCoffee(
                        new Item.Properties().food(new FoodProperties.Builder().nutrition(3).saturationMod(0.3F).alwaysEat().build()),
                        new MobEffectInstance[]{
                                new MobEffectInstance(ModEffects.CAFFEINE.get(), 3200, 0),
                                new MobEffectInstance(ModEffects.RELAX.get(), 500, 0),
                        }, 3, () -> ModItems.CUP_GLASS.get()));

        ModItems.COFFEE_COLDBREW_LATTE_MINT_ICE = items.register("coffee_coldbrew_latte_mint_ice",
                () -> new DrinkCoffee(
                        new Item.Properties().food(new FoodProperties.Builder().nutrition(2).saturationMod(0.3F).alwaysEat().build()),
                        new MobEffectInstance[]{
                                new MobEffectInstance(ModEffects.CAFFEINE.get(), 3200, 0),
                                new MobEffectInstance(ModEffects.RELAX.get(), 500, 0),
                        }, 3, () -> ModItems.CUP_GLASS.get()));

        ModItems.COFFEE_COLDBREW_LATTE_VANILLA_ICE = items.register("coffee_coldbrew_latte_vanilla_ice",
                () -> new DrinkCoffee(
                        new Item.Properties().food(new FoodProperties.Builder().nutrition(2).saturationMod(0.3F).alwaysEat().build()),
                        new MobEffectInstance[]{
                                new MobEffectInstance(ModEffects.CAFFEINE.get(), 3200, 0),
                                new MobEffectInstance(ModEffects.RELAX.get(), 500, 0),
                        }, 3, () -> ModItems.CUP_GLASS.get()));

        // Phase 9: Turkish Coffee
        ModItems.COFFEE_TURKISH = items.register("coffee_turkish",
                () -> new DrinkCoffee(
                        new Item.Properties().food(new FoodProperties.Builder().nutrition(2).saturationMod(0.2F).alwaysEat().build()),
                        new MobEffectInstance[]{
                                new MobEffectInstance(ModEffects.CAFFEINE.get(), 6000, 2),
                                new MobEffectInstance(ModEffects.RELAX.get(), 800, 1),
                        }, 4, () -> ModItems.CUP.get()));

        // Phase 9: Soda drinks (bottle-based)
        ModItems.SODA_CARAMEL = items.register("soda_caramel",
                () -> new DrinkCoffee(
                        new Item.Properties().food(new FoodProperties.Builder().nutrition(2).saturationMod(0.3F).alwaysEat().build()),
                        new MobEffectInstance[]{
                                new MobEffectInstance(ModEffects.CAFFEINE.get(), 2400, 0),
                                new MobEffectInstance(ModEffects.RELAX.get(), 600, 0),
                        }, 1, () -> net.minecraft.world.item.Items.GLASS_BOTTLE));
        ModItems.SODA_CHOCOLATE = items.register("soda_chocolate",
                () -> new DrinkCoffee(
                        new Item.Properties().food(new FoodProperties.Builder().nutrition(2).saturationMod(0.3F).alwaysEat().build()),
                        new MobEffectInstance[]{
                                new MobEffectInstance(ModEffects.CAFFEINE.get(), 2400, 0),
                                new MobEffectInstance(net.minecraft.world.effect.MobEffects.MOVEMENT_SPEED, 600, 0),
                        }, 1, () -> net.minecraft.world.item.Items.GLASS_BOTTLE));
        ModItems.SODA_FRUIT = items.register("soda_fruit",
                () -> new DrinkCoffee(
                        new Item.Properties().food(new FoodProperties.Builder().nutrition(3).saturationMod(0.4F).alwaysEat().build()),
                        new MobEffectInstance[]{
                                new MobEffectInstance(ModEffects.CAFFEINE.get(), 2400, 0),
                                new MobEffectInstance(net.minecraft.world.effect.MobEffects.REGENERATION, 200, 0),
                        }, 1, () -> net.minecraft.world.item.Items.GLASS_BOTTLE));
        ModItems.SODA_MINT = items.register("soda_mint",
                () -> new DrinkCoffee(
                        new Item.Properties().food(new FoodProperties.Builder().nutrition(2).saturationMod(0.3F).alwaysEat().build()),
                        new MobEffectInstance[]{
                                new MobEffectInstance(ModEffects.CAFFEINE.get(), 3600, 0),
                        }, 1, () -> net.minecraft.world.item.Items.GLASS_BOTTLE));
        ModItems.SODA_VANILLA = items.register("soda_vanilla",
                () -> new DrinkCoffee(
                        new Item.Properties().food(new FoodProperties.Builder().nutrition(2).saturationMod(0.3F).alwaysEat().build()),
                        new MobEffectInstance[]{
                                new MobEffectInstance(ModEffects.CAFFEINE.get(), 2400, 0),
                                new MobEffectInstance(ModEffects.RELAX.get(), 900, 0),
                        }, 1, () -> net.minecraft.world.item.Items.GLASS_BOTTLE));
        ModItems.SODA_SAKURA = items.register("soda_sakura",
                () -> new DrinkCoffee(
                        new Item.Properties().food(new FoodProperties.Builder().nutrition(2).saturationMod(0.3F).alwaysEat().build()),
                        new MobEffectInstance[]{
                                new MobEffectInstance(ModEffects.CAFFEINE.get(), 2400, 0),
                                new MobEffectInstance(ModEffects.GOLDEN_HEART.get(), 600, 0),
                        }, 1, () -> net.minecraft.world.item.Items.GLASS_BOTTLE));
    }
}
