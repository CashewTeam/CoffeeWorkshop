package net.langball.coffee.init;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

/**
 * Bakery items: breads, sandwiches, cakes, pies, muffins, pastries, creams, icecreams.
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
                () -> new Item(new Item.Properties().food(new FoodProperties.Builder().nutrition(1).saturationMod(0.5F).build())));
        ModItems.CHOCOLATE_BAR = items.register("chocolate_bar",
                () -> new Item(new Item.Properties().food(new FoodProperties.Builder().nutrition(4).saturationMod(0.5F).build())));
        ModItems.CHOCOLATE_CHIP = items.register("chocolate_chip",
                () -> new Item(new Item.Properties().food(new FoodProperties.Builder().nutrition(4).saturationMod(0.5F).build())));
        ModItems.BROWNIE = items.register("brownie",
                () -> new Item(new Item.Properties().food(new FoodProperties.Builder().nutrition(9).saturationMod(0.5F).build())));
        ModItems.FIELD_RATION = items.register("field_ration",
                () -> new Item(new Item.Properties().food(new FoodProperties.Builder().nutrition(8).saturationMod(1.0F).build())));

        // ── Cake slices ── 1.12.2: sponge=1, large=3, sat=0.5 (plain sponge=0.1)
        ModItems.CAKE_SPONGE_SLICE = items.register("cake_sponge_slice",
                () -> new Item(new Item.Properties().food(new FoodProperties.Builder().nutrition(1).saturationMod(0.1F).build())));
        ModItems.CAKE_SPONGE_BERRY_SLICES = items.register("cake_sponge_berry_slices",
                () -> new Item(new Item.Properties().food(new FoodProperties.Builder().nutrition(1).saturationMod(0.5F).build())));
        ModItems.CAKE_SPONGE_CARROT_SLICES = items.register("cake_sponge_carrot_slices",
                () -> new Item(new Item.Properties().food(new FoodProperties.Builder().nutrition(1).saturationMod(0.5F).build())));
        ModItems.CAKE_SPONGE_CHOCOLATE_SLICES = items.register("cake_sponge_chocolate_slices",
                () -> new Item(new Item.Properties().food(new FoodProperties.Builder().nutrition(1).saturationMod(0.5F).build())));
        ModItems.CAKE_SPONGE_COFFEE_SLICES = items.register("cake_sponge_coffee_slices",
                () -> new Item(new Item.Properties().food(new FoodProperties.Builder().nutrition(1).saturationMod(0.5F).build())));
        ModItems.CAKE_SPONGE_LEMON_SLICES = items.register("cake_sponge_lemon_slices",
                () -> new Item(new Item.Properties().food(new FoodProperties.Builder().nutrition(1).saturationMod(0.5F).build())));
        ModItems.CAKE_SPONGE_PUMPKIN_SLICES = items.register("cake_sponge_pumpkin_slices",
                () -> new Item(new Item.Properties().food(new FoodProperties.Builder().nutrition(1).saturationMod(0.5F).build())));
        ModItems.CAKE_SPONGE_REDVELVET_SLICES = items.register("cake_sponge_redvelvet_slices",
                () -> new Item(new Item.Properties().food(new FoodProperties.Builder().nutrition(1).saturationMod(0.5F).build())));
        ModItems.CAKE_SPONGE_TEA_SLICES = items.register("cake_sponge_tea_slices",
                () -> new Item(new Item.Properties().food(new FoodProperties.Builder().nutrition(1).saturationMod(0.5F).build())));
        // Large cake slices (3/0.5)
        ModItems.CAKE_SLICES = items.register("cake_slices",
                () -> new Item(new Item.Properties().food(new FoodProperties.Builder().nutrition(3).saturationMod(0.5F).build())));
        ModItems.CAKE_BERRY_SLICES = items.register("cake_berry_slices",
                () -> new Item(new Item.Properties().food(new FoodProperties.Builder().nutrition(3).saturationMod(0.5F).build())));
        ModItems.CAKE_CHEESE_SLICES = items.register("cake_cheese_slices",
                () -> new Item(new Item.Properties().food(new FoodProperties.Builder().nutrition(3).saturationMod(0.5F).build())));
        ModItems.CAKE_COFFEE_SLICES = items.register("cake_coffee_slices",
                () -> new Item(new Item.Properties().food(new FoodProperties.Builder().nutrition(3).saturationMod(0.5F).build())));
        ModItems.CAKE_HARVEST_SLICES = items.register("cake_harvest_slices",
                () -> new Item(new Item.Properties().food(new FoodProperties.Builder().nutrition(3).saturationMod(0.5F).build())));
        ModItems.CAKE_LEMON_SLICES = items.register("cake_lemon_slices",
                () -> new Item(new Item.Properties().food(new FoodProperties.Builder().nutrition(3).saturationMod(0.5F).build())));
        ModItems.CAKE_REDVELVET_SLICES = items.register("cake_redvelvet_slices",
                () -> new Item(new Item.Properties().food(new FoodProperties.Builder().nutrition(3).saturationMod(0.5F).build())));
        ModItems.CAKE_SCHWARZWALD_SLICES = items.register("cake_schwarzwald_slices",
                () -> new Item(new Item.Properties().food(new FoodProperties.Builder().nutrition(3).saturationMod(0.5F).build())));
        ModItems.CAKE_TEA_SLICES = items.register("cake_tea_slices",
                () -> new Item(new Item.Properties().food(new FoodProperties.Builder().nutrition(3).saturationMod(0.5F).build())));
        // Tiramisu slice — 1.20.1 extension (not in 1.12.2), higher nutrition
        ModItems.TIRAMISU_SLICE = items.register("tiramisu_slice",
                () -> new Item(new Item.Properties().food(new FoodProperties.Builder().nutrition(6).saturationMod(0.5F).build())));

        // ── Pie cream ──
        ModItems.PIE_CREAM = items.register("pie_cream",
                () -> new Item(new Item.Properties().food(new FoodProperties.Builder().nutrition(10).saturationMod(0.6F).build())));

        // ── Sandwiches ──
        ModItems.SANDWICH_BLT = items.register("sandwich_blt",
                () -> new Item(new Item.Properties().food(new FoodProperties.Builder().nutrition(10).saturationMod(0.9F).build())));
        ModItems.SANDWICH_BACON_EGG = items.register("sandwich_bacon_egg",
                () -> new Item(new Item.Properties().food(new FoodProperties.Builder().nutrition(10).saturationMod(0.9F).build())));
        ModItems.SANDWICH_BEEF_CHEESE = items.register("sandwich_beef_cheese",
                () -> new Item(new Item.Properties().food(new FoodProperties.Builder().nutrition(11).saturationMod(0.9F).build())));
        ModItems.SANDWICH_BLT_LARGE = items.register("sandwich_blt_large",
                () -> new Item(new Item.Properties().food(new FoodProperties.Builder().nutrition(12).saturationMod(0.9F).build())));
        ModItems.SANDWICH_CLUB = items.register("sandwich_club",
                () -> new Item(new Item.Properties().food(new FoodProperties.Builder().nutrition(10).saturationMod(0.9F).build())));
        ModItems.SANDWICH_CLUB_LARGE = items.register("sandwich_club_large",
                () -> new Item(new Item.Properties().food(new FoodProperties.Builder().nutrition(12).saturationMod(0.9F).build())));
        ModItems.SANDWICH_HAM_CHEESE = items.register("sandwich_ham_cheese",
                () -> new Item(new Item.Properties().food(new FoodProperties.Builder().nutrition(10).saturationMod(0.9F).build())));

        // Phase 5.3: Confectionery
        ModItems.CARAMEL_APPLE = items.register("caramel_apple",
                () -> new Item(new Item.Properties().food(new FoodProperties.Builder().nutrition(7).saturationMod(0.6F).build())));
        ModItems.HARDTACK = items.register("hardtack",
                () -> new Item(new Item.Properties().food(new FoodProperties.Builder().nutrition(5).saturationMod(0.6F).build())));
        ModItems.COOKIE_BLACK = items.register("cookie_black",
                () -> new Item(new Item.Properties().food(new FoodProperties.Builder().nutrition(3).saturationMod(0.4F).build())));
        ModItems.COOKIE_OREO = items.register("cookie_oreo",
                () -> new Item(new Item.Properties().food(new FoodProperties.Builder().nutrition(9).saturationMod(0.6F).build())));
        ModItems.MARSHMALLOW = items.register("marshmallow",
                () -> new Item(new Item.Properties().food(new FoodProperties.Builder().nutrition(1).saturationMod(0.5F).build())));
        ModItems.MARSHMALLOW_ROAST = items.register("marshmallow_roast",
                () -> new Item(new Item.Properties().food(new FoodProperties.Builder().nutrition(8).saturationMod(0.5F).build())));
        ModItems.MARSHMALLOW_CHOCOLATE = items.register("marshmallow_chocolate",
                () -> new Item(new Item.Properties().food(new FoodProperties.Builder().nutrition(3).saturationMod(0.5F).build())));
        ModItems.SMORE = items.register("smore",
                () -> new Item(new Item.Properties().food(new FoodProperties.Builder().nutrition(6).saturationMod(0.5F).build())));

        // Phase 5.4: Cake rolls — 1.12.2 values: plain=4, flavored=6, sat=0.8
        ModItems.CAKE_ROLL = items.register("cake_roll",
                () -> new Item(new Item.Properties().food(new FoodProperties.Builder().nutrition(4).saturationMod(0.8F).build())));
        ModItems.CAKE_BERRY_ROLL = items.register("cake_berry_roll",
                () -> new Item(new Item.Properties().food(new FoodProperties.Builder().nutrition(6).saturationMod(0.8F).build())));
        ModItems.CAKE_CARROT_ROLL = items.register("cake_carrot_roll",
                () -> new Item(new Item.Properties().food(new FoodProperties.Builder().nutrition(6).saturationMod(0.8F).build())));
        ModItems.CAKE_CHOCOLATE_ROLL = items.register("cake_chocolate_roll",
                () -> new Item(new Item.Properties().food(new FoodProperties.Builder().nutrition(6).saturationMod(0.8F).build())));
        ModItems.CAKE_COFFEE_ROLL = items.register("cake_coffee_roll",
                () -> new Item(new Item.Properties().food(new FoodProperties.Builder().nutrition(6).saturationMod(0.8F).build())));
        ModItems.CAKE_LEMON_ROLL = items.register("cake_lemon_roll",
                () -> new Item(new Item.Properties().food(new FoodProperties.Builder().nutrition(6).saturationMod(0.8F).build())));
        ModItems.CAKE_PUMPKIN_ROLL = items.register("cake_pumpkin_roll",
                () -> new Item(new Item.Properties().food(new FoodProperties.Builder().nutrition(6).saturationMod(0.8F).build())));
        ModItems.CAKE_REDVELVET_ROLL = items.register("cake_redvelvet_roll",
                () -> new Item(new Item.Properties().food(new FoodProperties.Builder().nutrition(6).saturationMod(0.8F).build())));
        ModItems.CAKE_TEA_ROLL = items.register("cake_tea_roll",
                () -> new Item(new Item.Properties().food(new FoodProperties.Builder().nutrition(6).saturationMod(0.8F).build())));

        // Phase 5.4: Cake intermediates (raw)
        ModItems.CAKE_SPONGE_RAW = items.register("cake_sponge_raw",
                () -> new Item(new Item.Properties()));
        ModItems.CAKE_CHEESE_RAW = items.register("cake_cheese_raw",
                () -> new Item(new Item.Properties()));
        ModItems.CAKE_SPONGE_BERRY_RAW = items.register("cake_sponge_berry_raw",
                () -> new Item(new Item.Properties()));
        ModItems.CAKE_SPONGE_CHOCOLATE_RAW = items.register("cake_sponge_chocolate_raw",
                () -> new Item(new Item.Properties()));
        ModItems.CAKE_SPONGE_COFFEE_RAW = items.register("cake_sponge_coffee_raw",
                () -> new Item(new Item.Properties()));
        ModItems.MOUSSE_BERRY_RAW = items.register("mousse_berry_raw",
                () -> new Item(new Item.Properties()));
        ModItems.MOUSSE_CHOCOLATE_RAW = items.register("mousse_chocolate_raw",
                () -> new Item(new Item.Properties()));
        ModItems.MOUSSE_COFFEE_RAW = items.register("mousse_coffee_raw",
                () -> new Item(new Item.Properties()));
        ModItems.MOUSSE_LEMON_RAW = items.register("mousse_lemon_raw",
                () -> new Item(new Item.Properties()));
        ModItems.CAKE_SPONGE_PUMPKIN_RAW = items.register("cake_sponge_pumpkin_raw",
                () -> new Item(new Item.Properties()));
        ModItems.CAKE_SPONGE_CARROT_RAW = items.register("cake_sponge_carrot_raw",
                () -> new Item(new Item.Properties()));
        ModItems.CAKE_SPONGE_REDVELVET_RAW = items.register("cake_sponge_redvelvet_raw",
                () -> new Item(new Item.Properties()));
        ModItems.CAKE_SPONGE_LEMON_RAW = items.register("cake_sponge_lemon_raw",
                () -> new Item(new Item.Properties()));
        ModItems.CAKE_SPONGE_TEA_RAW = items.register("cake_sponge_tea_raw",
                () -> new Item(new Item.Properties()));

        // Phase 5.4: Cake model intermediates — non-edible, return mold when assembled into block
        // 1.12.2: model items serve purely as an intermediate; mold is returned at Model→Finished
        ModItems.CAKE_SPONGE_MODEL = items.register("cake_sponge_model",
                () -> new Item(new Item.Properties()) {
                    @Override public boolean hasCraftingRemainingItem() { return true; }
                    @Override public ItemStack getCraftingRemainingItem(ItemStack stack) {
                        return new ItemStack(ModItems.CAKE_MODEL.get());
                    }
                });
        ModItems.CAKE_SPONGE_BERRY_MODEL = items.register("cake_sponge_berry_model",
                () -> new Item(new Item.Properties()) {
                    @Override public boolean hasCraftingRemainingItem() { return true; }
                    @Override public ItemStack getCraftingRemainingItem(ItemStack stack) {
                        return new ItemStack(ModItems.CAKE_MODEL.get());
                    }
                });
        ModItems.CAKE_SPONGE_CARROT_MODEL = items.register("cake_sponge_carrot_model",
                () -> new Item(new Item.Properties()) {
                    @Override public boolean hasCraftingRemainingItem() { return true; }
                    @Override public ItemStack getCraftingRemainingItem(ItemStack stack) {
                        return new ItemStack(ModItems.CAKE_MODEL.get());
                    }
                });
        ModItems.CAKE_SPONGE_CHOCOLATE_MODEL = items.register("cake_sponge_chocolate_model",
                () -> new Item(new Item.Properties()) {
                    @Override public boolean hasCraftingRemainingItem() { return true; }
                    @Override public ItemStack getCraftingRemainingItem(ItemStack stack) {
                        return new ItemStack(ModItems.CAKE_MODEL.get());
                    }
                });
        ModItems.CAKE_SPONGE_COFFEE_MODEL = items.register("cake_sponge_coffee_model",
                () -> new Item(new Item.Properties()) {
                    @Override public boolean hasCraftingRemainingItem() { return true; }
                    @Override public ItemStack getCraftingRemainingItem(ItemStack stack) {
                        return new ItemStack(ModItems.CAKE_MODEL.get());
                    }
                });
        ModItems.CAKE_SPONGE_LEMON_MODEL = items.register("cake_sponge_lemon_model",
                () -> new Item(new Item.Properties()) {
                    @Override public boolean hasCraftingRemainingItem() { return true; }
                    @Override public ItemStack getCraftingRemainingItem(ItemStack stack) {
                        return new ItemStack(ModItems.CAKE_MODEL.get());
                    }
                });
        ModItems.CAKE_SPONGE_PUMPKIN_MODEL = items.register("cake_sponge_pumpkin_model",
                () -> new Item(new Item.Properties()) {
                    @Override public boolean hasCraftingRemainingItem() { return true; }
                    @Override public ItemStack getCraftingRemainingItem(ItemStack stack) {
                        return new ItemStack(ModItems.CAKE_MODEL.get());
                    }
                });
        ModItems.CAKE_SPONGE_REDVELVET_MODEL = items.register("cake_sponge_redvelvet_model",
                () -> new Item(new Item.Properties()) {
                    @Override public boolean hasCraftingRemainingItem() { return true; }
                    @Override public ItemStack getCraftingRemainingItem(ItemStack stack) {
                        return new ItemStack(ModItems.CAKE_MODEL.get());
                    }
                });
        ModItems.CAKE_SPONGE_TEA_MODEL = items.register("cake_sponge_tea_model",
                () -> new Item(new Item.Properties()) {
                    @Override public boolean hasCraftingRemainingItem() { return true; }
                    @Override public ItemStack getCraftingRemainingItem(ItemStack stack) {
                        return new ItemStack(ModItems.CAKE_MODEL.get());
                    }
                });
        ModItems.CAKE_CHEESE_MODEL = items.register("cake_cheese_model",
                () -> new Item(new Item.Properties()) {
                    @Override public boolean hasCraftingRemainingItem() { return true; }
                    @Override public ItemStack getCraftingRemainingItem(ItemStack stack) {
                        return new ItemStack(ModItems.CAKE_MODEL.get());
                    }
                });

        // Phase 5.4: Cake plate intermediates (raw→model→base)
        // Plate models return CAKE_MODEL_PLATE when assembled into base
        ModItems.CAKE_SPONGE_PLATE_RAW = items.register("cake_sponge_plate_raw",
                () -> new Item(new Item.Properties()));
        ModItems.CAKE_SPONGE_PLATE_MODEL = items.register("cake_sponge_plate_model",
                () -> new Item(new Item.Properties()) {
                    @Override public boolean hasCraftingRemainingItem() { return true; }
                    @Override public ItemStack getCraftingRemainingItem(ItemStack stack) {
                        return new ItemStack(ModItems.CAKE_MODEL_PLATE.get());
                    }
                });
        ModItems.CAKE_SPONGE_BASE = items.register("cake_sponge_base",
                () -> new Item(new Item.Properties().food(new FoodProperties.Builder().nutrition(4).saturationMod(0.6F).build())));
        // Berry plates
        ModItems.CAKE_SPONGE_BERRY_PLATE_RAW = items.register("cake_sponge_berry_plate_raw",
                () -> new Item(new Item.Properties()));
        ModItems.CAKE_SPONGE_BERRY_PLATE_MODEL = items.register("cake_sponge_berry_plate_model",
                () -> new Item(new Item.Properties()) {
                    @Override public boolean hasCraftingRemainingItem() { return true; }
                    @Override public ItemStack getCraftingRemainingItem(ItemStack stack) {
                        return new ItemStack(ModItems.CAKE_MODEL_PLATE.get());
                    }
                });
        ModItems.CAKE_SPONGE_BERRY_BASE = items.register("cake_sponge_berry_base",
                () -> new Item(new Item.Properties().food(new FoodProperties.Builder().nutrition(6).saturationMod(0.6F).build())));
        // Carrot plates
        ModItems.CAKE_SPONGE_CARROT_PLATE_RAW = items.register("cake_sponge_carrot_plate_raw",
                () -> new Item(new Item.Properties()));
        ModItems.CAKE_SPONGE_CARROT_PLATE_MODEL = items.register("cake_sponge_carrot_plate_model",
                () -> new Item(new Item.Properties()) {
                    @Override public boolean hasCraftingRemainingItem() { return true; }
                    @Override public ItemStack getCraftingRemainingItem(ItemStack stack) {
                        return new ItemStack(ModItems.CAKE_MODEL_PLATE.get());
                    }
                });
        ModItems.CAKE_SPONGE_CARROT_BASE = items.register("cake_sponge_carrot_base",
                () -> new Item(new Item.Properties().food(new FoodProperties.Builder().nutrition(6).saturationMod(0.6F).build())));
        // Chocolate plates
        ModItems.CAKE_SPONGE_CHOCOLATE_PLATE_RAW = items.register("cake_sponge_chocolate_plate_raw",
                () -> new Item(new Item.Properties()));
        ModItems.CAKE_SPONGE_CHOCOLATE_PLATE_MODEL = items.register("cake_sponge_chocolate_plate_model",
                () -> new Item(new Item.Properties()) {
                    @Override public boolean hasCraftingRemainingItem() { return true; }
                    @Override public ItemStack getCraftingRemainingItem(ItemStack stack) {
                        return new ItemStack(ModItems.CAKE_MODEL_PLATE.get());
                    }
                });
        ModItems.CAKE_SPONGE_CHOCOLATE_BASE = items.register("cake_sponge_chocolate_base",
                () -> new Item(new Item.Properties().food(new FoodProperties.Builder().nutrition(6).saturationMod(0.6F).build())));
        // Coffee plates
        ModItems.CAKE_SPONGE_COFFEE_PLATE_RAW = items.register("cake_sponge_coffee_plate_raw",
                () -> new Item(new Item.Properties()));
        ModItems.CAKE_SPONGE_COFFEE_PLATE_MODEL = items.register("cake_sponge_coffee_plate_model",
                () -> new Item(new Item.Properties()) {
                    @Override public boolean hasCraftingRemainingItem() { return true; }
                    @Override public ItemStack getCraftingRemainingItem(ItemStack stack) {
                        return new ItemStack(ModItems.CAKE_MODEL_PLATE.get());
                    }
                });
        ModItems.CAKE_SPONGE_COFFEE_BASE = items.register("cake_sponge_coffee_base",
                () -> new Item(new Item.Properties().food(new FoodProperties.Builder().nutrition(6).saturationMod(0.6F).build())));
        // Lemon plates
        ModItems.CAKE_SPONGE_LEMON_PLATE_RAW = items.register("cake_sponge_lemon_plate_raw",
                () -> new Item(new Item.Properties()));
        ModItems.CAKE_SPONGE_LEMON_PLATE_MODEL = items.register("cake_sponge_lemon_plate_model",
                () -> new Item(new Item.Properties()) {
                    @Override public boolean hasCraftingRemainingItem() { return true; }
                    @Override public ItemStack getCraftingRemainingItem(ItemStack stack) {
                        return new ItemStack(ModItems.CAKE_MODEL_PLATE.get());
                    }
                });
        ModItems.CAKE_SPONGE_LEMON_BASE = items.register("cake_sponge_lemon_base",
                () -> new Item(new Item.Properties().food(new FoodProperties.Builder().nutrition(6).saturationMod(0.6F).build())));
        // Pumpkin plates
        ModItems.CAKE_SPONGE_PUMPKIN_PLATE_RAW = items.register("cake_sponge_pumpkin_plate_raw",
                () -> new Item(new Item.Properties()));
        ModItems.CAKE_SPONGE_PUMPKIN_PLATE_MODEL = items.register("cake_sponge_pumpkin_plate_model",
                () -> new Item(new Item.Properties()) {
                    @Override public boolean hasCraftingRemainingItem() { return true; }
                    @Override public ItemStack getCraftingRemainingItem(ItemStack stack) {
                        return new ItemStack(ModItems.CAKE_MODEL_PLATE.get());
                    }
                });
        ModItems.CAKE_SPONGE_PUMPKIN_BASE = items.register("cake_sponge_pumpkin_base",
                () -> new Item(new Item.Properties().food(new FoodProperties.Builder().nutrition(6).saturationMod(0.6F).build())));
        // Red velvet plates
        ModItems.CAKE_SPONGE_REDVELVET_PLATE_RAW = items.register("cake_sponge_redvelvet_plate_raw",
                () -> new Item(new Item.Properties()));
        ModItems.CAKE_SPONGE_REDVELVET_PLATE_MODEL = items.register("cake_sponge_redvelvet_plate_model",
                () -> new Item(new Item.Properties()) {
                    @Override public boolean hasCraftingRemainingItem() { return true; }
                    @Override public ItemStack getCraftingRemainingItem(ItemStack stack) {
                        return new ItemStack(ModItems.CAKE_MODEL_PLATE.get());
                    }
                });
        ModItems.CAKE_SPONGE_REDVELVET_BASE = items.register("cake_sponge_redvelvet_base",
                () -> new Item(new Item.Properties().food(new FoodProperties.Builder().nutrition(6).saturationMod(0.6F).build())));
        // Tea plates
        ModItems.CAKE_SPONGE_TEA_PLATE_RAW = items.register("cake_sponge_tea_plate_raw",
                () -> new Item(new Item.Properties()));
        ModItems.CAKE_SPONGE_TEA_PLATE_MODEL = items.register("cake_sponge_tea_plate_model",
                () -> new Item(new Item.Properties()) {
                    @Override public boolean hasCraftingRemainingItem() { return true; }
                    @Override public ItemStack getCraftingRemainingItem(ItemStack stack) {
                        return new ItemStack(ModItems.CAKE_MODEL_PLATE.get());
                    }
                });
        ModItems.CAKE_SPONGE_TEA_BASE = items.register("cake_sponge_tea_base",
                () -> new Item(new Item.Properties().food(new FoodProperties.Builder().nutrition(6).saturationMod(0.6F).build())));

        // Phase 5.4: Jiggy raw/model (generic, returns CAKE_MODEL_SQUARE at Model→Finished)
        ModItems.JIGGY_CAKE_RAW = items.register("jiggy_cake_raw",
                () -> new Item(new Item.Properties()));
        ModItems.JIGGY_CAKE_MODEL = items.register("jiggy_cake_model",
                () -> new Item(new Item.Properties()) {
                    @Override public boolean hasCraftingRemainingItem() { return true; }
                    @Override public ItemStack getCraftingRemainingItem(ItemStack stack) {
                        return new ItemStack(ModItems.CAKE_MODEL_SQUARE.get());
                    }
                });

        // Phase 5.4: Mousse model intermediates (raw→icecream machine→model→block)
        // Models return CAKE_MODEL (round mold) when assembled into block
        ModItems.MOUSSE_BERRY_MODEL = items.register("mousse_berry_model",
                () -> new Item(new Item.Properties()) {
                    @Override public boolean hasCraftingRemainingItem() { return true; }
                    @Override public ItemStack getCraftingRemainingItem(ItemStack stack) {
                        return new ItemStack(ModItems.CAKE_MODEL.get());
                    }
                });
        ModItems.MOUSSE_CHOCOLATE_MODEL = items.register("mousse_chocolate_model",
                () -> new Item(new Item.Properties()) {
                    @Override public boolean hasCraftingRemainingItem() { return true; }
                    @Override public ItemStack getCraftingRemainingItem(ItemStack stack) {
                        return new ItemStack(ModItems.CAKE_MODEL.get());
                    }
                });
        ModItems.MOUSSE_COFFEE_MODEL = items.register("mousse_coffee_model",
                () -> new Item(new Item.Properties()) {
                    @Override public boolean hasCraftingRemainingItem() { return true; }
                    @Override public ItemStack getCraftingRemainingItem(ItemStack stack) {
                        return new ItemStack(ModItems.CAKE_MODEL.get());
                    }
                });
        ModItems.MOUSSE_LEMON_MODEL = items.register("mousse_lemon_model",
                () -> new Item(new Item.Properties()) {
                    @Override public boolean hasCraftingRemainingItem() { return true; }
                    @Override public ItemStack getCraftingRemainingItem(ItemStack stack) {
                        return new ItemStack(ModItems.CAKE_MODEL.get());
                    }
                });

        // Phase 5.4: Tiramisu intermediates — model returns CAKE_MODEL_SQUARE
        ModItems.TIRAMISU_RAW = items.register("tiramisu_raw",
                () -> new Item(new Item.Properties()));
        ModItems.TIRAMISU_MODEL = items.register("tiramisu_model",
                () -> new Item(new Item.Properties()) {
                    @Override public boolean hasCraftingRemainingItem() { return true; }
                    @Override public ItemStack getCraftingRemainingItem(ItemStack stack) {
                        return new ItemStack(ModItems.CAKE_MODEL_SQUARE.get());
                    }
                });

        // Phase 6: Creams — 1.12.2: milk=2(0.4), flavored=4(0.4)
        ModItems.CREAM_MILK = items.register("cream_milk",
                () -> new Item(new Item.Properties().food(new FoodProperties.Builder().nutrition(2).saturationMod(0.4F).build())));
        ModItems.CREAM_APPLE = items.register("cream_apple",
                () -> new Item(new Item.Properties().food(new FoodProperties.Builder().nutrition(4).saturationMod(0.4F).build())));
        ModItems.CREAM_BERRY = items.register("cream_berry",
                () -> new Item(new Item.Properties().food(new FoodProperties.Builder().nutrition(4).saturationMod(0.4F).build())));
        ModItems.CREAM_CHOCOLATE = items.register("cream_chocolate",
                () -> new Item(new Item.Properties().food(new FoodProperties.Builder().nutrition(4).saturationMod(0.4F).build())));
        ModItems.CREAM_COFFEE = items.register("cream_coffee",
                () -> new Item(new Item.Properties().food(new FoodProperties.Builder().nutrition(4).saturationMod(0.4F).build())));
        ModItems.CREAM_LEMON = items.register("cream_lemon",
                () -> new Item(new Item.Properties().food(new FoodProperties.Builder().nutrition(4).saturationMod(0.4F).build())));
        ModItems.CREAM_MELON = items.register("cream_melon",
                () -> new Item(new Item.Properties().food(new FoodProperties.Builder().nutrition(4).saturationMod(0.4F).build())));

        // Phase 6: Cookie Ice Creams — 1.12.2: vanilla=8(0.6), flavored=10(0.6)
        ModItems.COOKIE_ICECREAM_VANILLA = items.register("cookie_icecream_vanilla",
                () -> new Item(new Item.Properties().food(new FoodProperties.Builder().nutrition(8).saturationMod(0.6F).build())));
        ModItems.COOKIE_ICECREAM_APPLE = items.register("cookie_icecream_apple",
                () -> new Item(new Item.Properties().food(new FoodProperties.Builder().nutrition(10).saturationMod(0.6F).build())));
        ModItems.COOKIE_ICECREAM_BERRY = items.register("cookie_icecream_berry",
                () -> new Item(new Item.Properties().food(new FoodProperties.Builder().nutrition(10).saturationMod(0.6F).build())));
        ModItems.COOKIE_ICECREAM_CHOCOLATE = items.register("cookie_icecream_chocolate",
                () -> new Item(new Item.Properties().food(new FoodProperties.Builder().nutrition(10).saturationMod(0.6F).build())));
        ModItems.COOKIE_ICECREAM_COFFEE = items.register("cookie_icecream_coffee",
                () -> new Item(new Item.Properties().food(new FoodProperties.Builder().nutrition(10).saturationMod(0.6F).build())));
        ModItems.COOKIE_ICECREAM_LEMON = items.register("cookie_icecream_lemon",
                () -> new Item(new Item.Properties().food(new FoodProperties.Builder().nutrition(10).saturationMod(0.6F).build())));
        ModItems.COOKIE_ICECREAM_MELON = items.register("cookie_icecream_melon",
                () -> new Item(new Item.Properties().food(new FoodProperties.Builder().nutrition(10).saturationMod(0.6F).build())));

        // ── Ice creams — 1.12.2: vanilla=4(0.5), flavored=6(0.5) ──
        ModItems.ICECREAM_MIX_VANILLA = items.register("icecream_mix_vanilla",
                () -> new Item(new Item.Properties().stacksTo(16)));
        ModItems.ICECREAM_VANILLA = items.register("icecream_vanilla",
                () -> new Item(new Item.Properties().food(new FoodProperties.Builder().nutrition(4).saturationMod(0.5F).build())));
        ModItems.ICECREAM_APPLE = items.register("icecream_apple",
                () -> new Item(new Item.Properties().food(new FoodProperties.Builder().nutrition(6).saturationMod(0.5F).build())));
        ModItems.ICECREAM_BERRY = items.register("icecream_berry",
                () -> new Item(new Item.Properties().food(new FoodProperties.Builder().nutrition(6).saturationMod(0.5F).build())));
        ModItems.ICECREAM_CHOCOLATE = items.register("icecream_chocolate",
                () -> new Item(new Item.Properties().food(new FoodProperties.Builder().nutrition(6).saturationMod(0.5F).build())));
        ModItems.ICECREAM_COFFEE = items.register("icecream_coffee",
                () -> new Item(new Item.Properties().food(new FoodProperties.Builder().nutrition(6).saturationMod(0.5F).build())));
        ModItems.ICECREAM_LEMON = items.register("icecream_lemon",
                () -> new Item(new Item.Properties().food(new FoodProperties.Builder().nutrition(6).saturationMod(0.5F).build())));
        ModItems.ICECREAM_MELON = items.register("icecream_melon",
                () -> new Item(new Item.Properties().food(new FoodProperties.Builder().nutrition(6).saturationMod(0.5F).build())));

        // ── Phase 7: Pies — 1.12.2: cream=10(0.6), flavored=12(0.6) ──
        ModItems.PIE_APPLE = items.register("pie_apple",
                () -> new Item(new Item.Properties().food(new FoodProperties.Builder().nutrition(12).saturationMod(0.6F).build())));
        ModItems.PIE_BERRY = items.register("pie_berry",
                () -> new Item(new Item.Properties().food(new FoodProperties.Builder().nutrition(12).saturationMod(0.6F).build())));
        ModItems.PIE_CARAMEL = items.register("pie_caramel",
                () -> new Item(new Item.Properties().food(new FoodProperties.Builder().nutrition(12).saturationMod(0.6F).build())));
        ModItems.PIE_CHOCOLATE = items.register("pie_chocolate",
                () -> new Item(new Item.Properties().food(new FoodProperties.Builder().nutrition(12).saturationMod(0.6F).build())));
        ModItems.PIE_COFFEE = items.register("pie_coffee",
                () -> new Item(new Item.Properties().food(new FoodProperties.Builder().nutrition(12).saturationMod(0.6F).build())));
        ModItems.PIE_LEMON = items.register("pie_lemon",
                () -> new Item(new Item.Properties().food(new FoodProperties.Builder().nutrition(12).saturationMod(0.6F).build())));
        ModItems.PIE_MELON = items.register("pie_melon",
                () -> new Item(new Item.Properties().food(new FoodProperties.Builder().nutrition(12).saturationMod(0.6F).build())));
        ModItems.PIE_TEA = items.register("pie_tea",
                () -> new Item(new Item.Properties().food(new FoodProperties.Builder().nutrition(12).saturationMod(0.6F).build())));

        // Phase 7: Muffins — 1.12.2: plain=4(0.6), flavored=6(0.6)
        ModItems.MUFFIN = items.register("muffin",
                () -> new Item(new Item.Properties().food(new FoodProperties.Builder().nutrition(4).saturationMod(0.6F).build())));
        ModItems.MUFFIN_BERRY = items.register("muffin_berry",
                () -> new Item(new Item.Properties().food(new FoodProperties.Builder().nutrition(6).saturationMod(0.6F).build())));
        ModItems.MUFFIN_CARROT = items.register("muffin_carrot",
                () -> new Item(new Item.Properties().food(new FoodProperties.Builder().nutrition(6).saturationMod(0.6F).build())));
        ModItems.MUFFIN_CHOCOLATE = items.register("muffin_chocolate",
                () -> new Item(new Item.Properties().food(new FoodProperties.Builder().nutrition(6).saturationMod(0.6F).build())));
        ModItems.MUFFIN_COFFEE = items.register("muffin_coffee",
                () -> new Item(new Item.Properties().food(new FoodProperties.Builder().nutrition(6).saturationMod(0.6F).build())));
        ModItems.MUFFIN_LEMON = items.register("muffin_lemon",
                () -> new Item(new Item.Properties().food(new FoodProperties.Builder().nutrition(6).saturationMod(0.6F).build())));
        ModItems.MUFFIN_PUMPKIN = items.register("muffin_pumpkin",
                () -> new Item(new Item.Properties().food(new FoodProperties.Builder().nutrition(6).saturationMod(0.6F).build())));
        ModItems.MUFFIN_REDVELVET = items.register("muffin_redvelvet",
                () -> new Item(new Item.Properties().food(new FoodProperties.Builder().nutrition(6).saturationMod(0.6F).build())));
        ModItems.MUFFIN_TEA = items.register("muffin_tea",
                () -> new Item(new Item.Properties().food(new FoodProperties.Builder().nutrition(6).saturationMod(0.6F).build())));

        // Phase 7: Pastries — 1.12.2: croissant=8/10, ginger=4, gingerMan=9, puff=4, mille=14
        ModItems.CROISSANT = items.register("croissant",
                () -> new Item(new Item.Properties().food(new FoodProperties.Builder().nutrition(8).saturationMod(0.6F).build())));
        ModItems.CROISSANT_CHOCOLATE = items.register("croissant_chocolate",
                () -> new Item(new Item.Properties().food(new FoodProperties.Builder().nutrition(10).saturationMod(0.6F).build())));
        ModItems.GINGER_BREAD = items.register("ginger_bread",
                () -> new Item(new Item.Properties().food(new FoodProperties.Builder().nutrition(4).saturationMod(0.6F).build())));
        ModItems.GINGER_BREAD_MAN = items.register("ginger_bread_man",
                () -> new Item(new Item.Properties().food(new FoodProperties.Builder().nutrition(9).saturationMod(0.6F).build())));
        ModItems.GINGER_BREAD_MAN_RAW = items.register("ginger_bread_man_raw",
                () -> new Item(new Item.Properties().food(new FoodProperties.Builder().nutrition(2).saturationMod(0.6F).build())));
        ModItems.PUFF = items.register("puff",
                () -> new Item(new Item.Properties().food(new FoodProperties.Builder().nutrition(4).saturationMod(0.4F).build())));
        ModItems.MILLE_FEUILLE = items.register("mille_feuille",
                () -> new Item(new Item.Properties().food(new FoodProperties.Builder().nutrition(14).saturationMod(0.6F).build())));

        // Phase 7: Pastry raw intermediates (croissant, puff) — 1.12.2 registerRaw2CookedRecipes
        ModItems.CROISSANT_RAW = items.register("croissant_raw",
                () -> new Item(new Item.Properties().food(new FoodProperties.Builder().nutrition(2).saturationMod(0.4F).build())));
        ModItems.CROISSANT_CHOCOLATE_RAW = items.register("croissant_chocolate_raw",
                () -> new Item(new Item.Properties().food(new FoodProperties.Builder().nutrition(2).saturationMod(0.4F).build())));
        ModItems.PUFF_RAW = items.register("puff_raw",
                () -> new Item(new Item.Properties().food(new FoodProperties.Builder().nutrition(1).saturationMod(0.4F).build())));

        // Phase 7: Jiggy Cakes — 1.12.2: plain=4(0.8), flavored=5(0.8)
        ModItems.JIGGY_CAKE = items.register("jiggy_cake",
                () -> new Item(new Item.Properties().food(new FoodProperties.Builder().nutrition(4).saturationMod(0.8F).build())));
        ModItems.JIGGY_CAKE_BERRY = items.register("jiggy_cake_berry",
                () -> new Item(new Item.Properties().food(new FoodProperties.Builder().nutrition(5).saturationMod(0.8F).build())));
        ModItems.JIGGY_CAKE_CARROT = items.register("jiggy_cake_carrot",
                () -> new Item(new Item.Properties().food(new FoodProperties.Builder().nutrition(5).saturationMod(0.8F).build())));
        ModItems.JIGGY_CAKE_CHOCOLATE = items.register("jiggy_cake_chocolate",
                () -> new Item(new Item.Properties().food(new FoodProperties.Builder().nutrition(5).saturationMod(0.8F).build())));
        ModItems.JIGGY_CAKE_COFFEE = items.register("jiggy_cake_coffee",
                () -> new Item(new Item.Properties().food(new FoodProperties.Builder().nutrition(5).saturationMod(0.8F).build())));
        ModItems.JIGGY_CAKE_LEMON = items.register("jiggy_cake_lemon",
                () -> new Item(new Item.Properties().food(new FoodProperties.Builder().nutrition(5).saturationMod(0.8F).build())));
        ModItems.JIGGY_CAKE_PUMPKIN = items.register("jiggy_cake_pumpkin",
                () -> new Item(new Item.Properties().food(new FoodProperties.Builder().nutrition(5).saturationMod(0.8F).build())));
        ModItems.JIGGY_CAKE_REDVELVET = items.register("jiggy_cake_redvelvet",
                () -> new Item(new Item.Properties().food(new FoodProperties.Builder().nutrition(5).saturationMod(0.8F).build())));
        ModItems.JIGGY_CAKE_TEA = items.register("jiggy_cake_tea",
                () -> new Item(new Item.Properties().food(new FoodProperties.Builder().nutrition(5).saturationMod(0.8F).build())));

        // Phase 7: Mooncakes — 1.12.2: plain=8(0.6), flavored=10(0.6)
        ModItems.MOONCAKE = items.register("mooncake",
                () -> new Item(new Item.Properties().food(new FoodProperties.Builder().nutrition(8).saturationMod(0.6F).build())));
        ModItems.MOONCAKE_EGG = items.register("mooncake_egg",
                () -> new Item(new Item.Properties().food(new FoodProperties.Builder().nutrition(10).saturationMod(0.6F).build())));
        ModItems.MOONCAKE_FRUIT = items.register("mooncake_fruit",
                () -> new Item(new Item.Properties().food(new FoodProperties.Builder().nutrition(10).saturationMod(0.6F).build())));
        ModItems.MOONCAKE_HAM = items.register("mooncake_ham",
                () -> new Item(new Item.Properties().food(new FoodProperties.Builder().nutrition(10).saturationMod(0.6F).build())));

        // Phase 7: Soufflés — 1.12.2: plain=8(0.6), choc=10(0.6)
        // Override finishUsingItem to return Small Mold (1.12.2 ItemFoodContain pattern)
        ModItems.SOUFFLE = items.register("souffle",
                () -> new SouffleItem(new Item.Properties().food(new FoodProperties.Builder().nutrition(8).saturationMod(0.6F).build())));
        ModItems.SOUFFLE_CHOCOLATE = items.register("souffle_chocolate",
                () -> new SouffleItem(new Item.Properties().food(new FoodProperties.Builder().nutrition(10).saturationMod(0.6F).build())));

        // Phase 7: Raw intermediates (muffin, soufflé, mooncake) — 1.12.2 values
        // Muffin raw: all 2(0.2); Soufflé raw: all 2(0.2); Mooncake raw: all 2(0.4)
        ModItems.MUFFIN_RAW = items.register("muffin_raw",
                () -> new Item(new Item.Properties().food(new FoodProperties.Builder().nutrition(2).saturationMod(0.2F).build())));
        ModItems.MUFFIN_BERRY_RAW = items.register("muffin_berry_raw",
                () -> new Item(new Item.Properties().food(new FoodProperties.Builder().nutrition(2).saturationMod(0.2F).build())));
        ModItems.MUFFIN_CARROT_RAW = items.register("muffin_carrot_raw",
                () -> new Item(new Item.Properties().food(new FoodProperties.Builder().nutrition(2).saturationMod(0.2F).build())));
        ModItems.MUFFIN_CHOCOLATE_RAW = items.register("muffin_chocolate_raw",
                () -> new Item(new Item.Properties().food(new FoodProperties.Builder().nutrition(2).saturationMod(0.2F).build())));
        ModItems.MUFFIN_COFFEE_RAW = items.register("muffin_coffee_raw",
                () -> new Item(new Item.Properties().food(new FoodProperties.Builder().nutrition(2).saturationMod(0.2F).build())));
        ModItems.MUFFIN_LEMON_RAW = items.register("muffin_lemon_raw",
                () -> new Item(new Item.Properties().food(new FoodProperties.Builder().nutrition(2).saturationMod(0.2F).build())));
        ModItems.MUFFIN_PUMPKIN_RAW = items.register("muffin_pumpkin_raw",
                () -> new Item(new Item.Properties().food(new FoodProperties.Builder().nutrition(2).saturationMod(0.2F).build())));
        ModItems.MUFFIN_REDVELVET_RAW = items.register("muffin_redvelvet_raw",
                () -> new Item(new Item.Properties().food(new FoodProperties.Builder().nutrition(2).saturationMod(0.2F).build())));
        ModItems.MUFFIN_TEA_RAW = items.register("muffin_tea_raw",
                () -> new Item(new Item.Properties().food(new FoodProperties.Builder().nutrition(2).saturationMod(0.2F).build())));
        ModItems.SOUFFLE_RAW = items.register("souffle_raw",
                () -> new Item(new Item.Properties().food(new FoodProperties.Builder().nutrition(2).saturationMod(0.2F).build())));
        ModItems.SOUFFLE_CHOCOLATE_RAW = items.register("souffle_chocolate_raw",
                () -> new Item(new Item.Properties().food(new FoodProperties.Builder().nutrition(2).saturationMod(0.2F).build())));
        ModItems.MOONCAKE_RAW = items.register("mooncake_raw",
                () -> new Item(new Item.Properties().food(new FoodProperties.Builder().nutrition(2).saturationMod(0.4F).build())));
        ModItems.MOONCAKE_EGG_RAW = items.register("mooncake_egg_raw",
                () -> new Item(new Item.Properties().food(new FoodProperties.Builder().nutrition(2).saturationMod(0.4F).build())));
        ModItems.MOONCAKE_FRUIT_RAW = items.register("mooncake_fruit_raw",
                () -> new Item(new Item.Properties().food(new FoodProperties.Builder().nutrition(2).saturationMod(0.4F).build())));
        ModItems.MOONCAKE_HAM_RAW = items.register("mooncake_ham_raw",
                () -> new Item(new Item.Properties().food(new FoodProperties.Builder().nutrition(2).saturationMod(0.4F).build())));
    }

    /**
     * Soufflé food item that returns a Small Mold when finished eating.
     * Matches 1.12.2 {@code ItemFoodContain} pattern: container returned via
     * {@code onItemUseFinish} rather than crafting remainder.
     */
    private static class SouffleItem extends Item {
        SouffleItem(Properties props) {
            super(props);
        }

        @Override
        public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entity) {
            ItemStack result = super.finishUsingItem(stack, level, entity);
            if (entity instanceof net.minecraft.world.entity.player.Player player) {
                ItemStack mold = new ItemStack(ModItems.SMALL_MODEL.get());
                if (!player.getInventory().add(mold)) {
                    player.drop(mold, false);
                }
            }
            return result;
        }
    }
}
