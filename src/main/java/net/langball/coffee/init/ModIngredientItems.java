package net.langball.coffee.init;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

/**
 * Ingredient items: materials, powders, syrups, doughs, tools, molds.
 */
public class ModIngredientItems {

    static void registerAll(DeferredRegister<Item> items) {
        // ── Materials ──
        ModItems.COCOA_BEAN = items.register("cocoa_bean",
                () -> new Item(new Item.Properties()));
        ModItems.COCOA_POWDER = items.register("cocoa_powder",
                () -> new Item(new Item.Properties()));
        ModItems.COCOA_BATTER = items.register("cocoa_batter",
                () -> new Item(new Item.Properties()));
        ModItems.BAG_CLOTH = items.register("bag_cloth",
                () -> new Item(new Item.Properties()));
        ModItems.ICE_SLAG = items.register("ice_slag",
                () -> new Item(new Item.Properties()));
        ModItems.YEAST = items.register("yeast",
                () -> new Item(new Item.Properties()));
        ModItems.PLATE_IRON = items.register("plate_iron",
                () -> new Item(new Item.Properties()));
        ModItems.SPICES = items.register("spices",
                () -> new Item(new Item.Properties()));
        ModItems.GELATIN = items.register("gelatin",
                () -> new Item(new Item.Properties()));
        ModItems.SODA = items.register("soda",
                () -> new Item(new Item.Properties()));
        ModItems.FLOUR = items.register("flour",
                () -> new Item(new Item.Properties()));

        // ── Doughs ──
        ModItems.DOUGH = items.register("dough",
                () -> new Item(new Item.Properties()));
        ModItems.DOUGH_PASTRY = items.register("dough_pastry",
                () -> new Item(new Item.Properties()));
        ModItems.DOUGH_COOKIE = items.register("dough_cookie",
                () -> new Item(new Item.Properties()));
        ModItems.DOUGH_GINGER = items.register("dough_ginger",
                () -> new Item(new Item.Properties()));
        ModItems.DOUGH_BREAD = items.register("dough_bread",
                () -> new Item(new Item.Properties()));
        ModItems.DOUGH_BREAD_ROUND = items.register("dough_bread_round",
                () -> new Item(new Item.Properties()));
        ModItems.DOUGH_BAGUETTE = items.register("dough_baguette",
                () -> new Item(new Item.Properties()));
        ModItems.DOUGH_BAGEL = items.register("dough_bagel",
                () -> new Item(new Item.Properties()));
        ModItems.DOUGH_TOAST = items.register("dough_toast",
                () -> new Item(new Item.Properties()));
        ModItems.PLATE_DOUGH = items.register("plate_dough",
                () -> new Item(new Item.Properties()));
        ModItems.PLATE_DOUGH_PASTRY = items.register("plate_dough_pastry",
                () -> new Item(new Item.Properties()));
        ModItems.PLATE_DOUGH_GINGER = items.register("plate_dough_ginger",
                () -> new Item(new Item.Properties()));

        // ── Coldbrew equipment ──
        ModItems.COLDBREW_BOTTLE = items.register("coldbrew_bottle",
                () -> new Item(new Item.Properties().stacksTo(1)) {
                    @Override public boolean hasCraftingRemainingItem() { return true; }
                    @Override public ItemStack getCraftingRemainingItem(ItemStack stack) { return new ItemStack(Items.GLASS_BOTTLE); }
                });

        // ── Empty cups ──
        ModItems.CUP = items.register("cup",
                () -> new Item(new Item.Properties().stacksTo(16)));
        ModItems.CUP_GLASS = items.register("cup_glass",
                () -> new Item(new Item.Properties().stacksTo(16)));

        // ── Syrups ──
        ModItems.SYRUP_EMPTY = items.register("syrup_empty",
                () -> new Item(new Item.Properties().stacksTo(16)));
        ModItems.SYRUP_CARAMEL = items.register("syrup_caramel",
                () -> new Item(new Item.Properties().stacksTo(1)) {
                    @Override public boolean hasCraftingRemainingItem() { return true; }
                    @Override public ItemStack getCraftingRemainingItem(ItemStack stack) { return new ItemStack(ModItems.SYRUP_EMPTY.get()); }
                });
        ModItems.SYRUP_CHOCOLATE = items.register("syrup_chocolate",
                () -> new Item(new Item.Properties().stacksTo(1)) {
                    @Override public boolean hasCraftingRemainingItem() { return true; }
                    @Override public ItemStack getCraftingRemainingItem(ItemStack stack) { return new ItemStack(ModItems.SYRUP_EMPTY.get()); }
                });
        ModItems.SYRUP_FRUIT = items.register("syrup_fruit",
                () -> new Item(new Item.Properties().stacksTo(1)) {
                    @Override public boolean hasCraftingRemainingItem() { return true; }
                    @Override public ItemStack getCraftingRemainingItem(ItemStack stack) { return new ItemStack(ModItems.SYRUP_EMPTY.get()); }
                });
        ModItems.SYRUP_MINT = items.register("syrup_mint",
                () -> new Item(new Item.Properties().stacksTo(1)) {
                    @Override public boolean hasCraftingRemainingItem() { return true; }
                    @Override public ItemStack getCraftingRemainingItem(ItemStack stack) { return new ItemStack(ModItems.SYRUP_EMPTY.get()); }
                });
        ModItems.SYRUP_VANILLA = items.register("syrup_vanilla",
                () -> new Item(new Item.Properties().stacksTo(1)) {
                    @Override public boolean hasCraftingRemainingItem() { return true; }
                    @Override public ItemStack getCraftingRemainingItem(ItemStack stack) { return new ItemStack(ModItems.SYRUP_EMPTY.get()); }
                });
        ModItems.SYRUP_SAKURA = items.register("syrup_sakura",
                () -> new Item(new Item.Properties().stacksTo(1)) {
                    @Override public boolean hasCraftingRemainingItem() { return true; }
                    @Override public ItemStack getCraftingRemainingItem(ItemStack stack) { return new ItemStack(ModItems.SYRUP_EMPTY.get()); }
                });

        // ── Tea leaves ──
        ModItems.TEA_LEAF = items.register("tea_leaf",
                () -> new Item(new Item.Properties()));
        ModItems.BLACK_TEA_LEAF = items.register("black_tea_leaf",
                () -> new Item(new Item.Properties()));

        // ── Tools / Molds ──
        // Molds are consumed in Raw-stage recipes; returned when Model assembles into Finished
        ModItems.IRON_BOWL = items.register("iron_bowl",
                () -> new Item(new Item.Properties().stacksTo(16)));
        ModItems.CAKE_MODEL = items.register("cake_model",
                () -> new Item(new Item.Properties().stacksTo(16)));
        ModItems.CAKE_MODEL_SQUARE = items.register("cake_model_square",
                () -> new Item(new Item.Properties().stacksTo(16)));
        ModItems.CAKE_MODEL_PLATE = items.register("cake_model_plate",
                () -> new Item(new Item.Properties().stacksTo(16)));
        ModItems.SMALL_MODEL = items.register("small_model",
                () -> new Item(new Item.Properties().stacksTo(16)));
        ModItems.MOONCAKE_MODEL = items.register("mooncake_model",
                () -> new Item(new Item.Properties()) {
                    @Override public boolean hasCraftingRemainingItem() { return true; }
                    @Override public ItemStack getCraftingRemainingItem(ItemStack stack) { return stack.copyWithCount(1); }
                });
        ModItems.MIXING_BOWL = items.register("mixing_bowl",
                () -> new Item(new Item.Properties()) {
                    @Override public boolean hasCraftingRemainingItem() { return true; }
                    @Override public ItemStack getCraftingRemainingItem(ItemStack stack) { return stack.copyWithCount(1); }
                });

        // ── Other ──
        ModItems.VANILLA = items.register("vanilla",
                () -> new Item(new Item.Properties()));
        ModItems.BAG = items.register("bag",
                () -> new Item(new Item.Properties()));
        // Phase 5.2: Lemon ingredient (villager trade source)
        ModItems.LEMON = items.register("lemon",
                () -> new Item(new Item.Properties()));

        // Phase 5.3: Confectionery foundation
        ModItems.CARAMEL = items.register("caramel",
                () -> new Item(new Item.Properties()));
        ModItems.CUSTARD = items.register("custard",
                () -> new Item(new Item.Properties()));
        ModItems.MILK_FORM = items.register("milk_form",
                () -> new Item(new Item.Properties()));
        ModItems.POT = items.register("pot",
                () -> new Item(new Item.Properties().stacksTo(16)) {
                    @Override public boolean hasCraftingRemainingItem() { return true; }
                    @Override public ItemStack getCraftingRemainingItem(ItemStack stack) { return stack.copyWithCount(1); }
                });

        // Phase 9: Moka Pot components
        ModItems.MOKA_BOTTOM = items.register("moka_bottom",
                () -> new Item(new Item.Properties().stacksTo(1)));
        ModItems.MOKA_TOP = items.register("moka_top",
                () -> new Item(new Item.Properties().stacksTo(1)));
    }
}
