package net.langball.coffee.init;

import net.langball.coffee.item.ItemRecordCW;
import net.langball.coffee.item.SeedCoffee;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

/**
 * Equipment items: BlockItems, seeds, records.
 */
public class ModEquipmentItems {

    static void registerAll(DeferredRegister<Item> items) {
        // ── Machines ──
        ModItems.GRINDER = items.register("grinder_off",
                () -> new BlockItem(ModBlocks.GRINDER.get(), new Item.Properties()));
        ModItems.COFFEE_MACHINE = items.register("coffee_machine",
                () -> new BlockItem(ModBlocks.COFFEE_MACHINE.get(), new Item.Properties()));
        ModItems.ICECREAM_MACHINE = items.register("icecream_machine",
                () -> new BlockItem(ModBlocks.ICECREAM_MACHINE.get(), new Item.Properties()));
        ModItems.ROLLER = items.register("roller",
                () -> new BlockItem(ModBlocks.ROLLER.get(), new Item.Properties()));
        ModItems.OVEN = items.register("oven_off",
                () -> new BlockItem(ModBlocks.OVEN.get(), new Item.Properties()));

        // ── Plants & Decor ──
        ModItems.COFFEE_TREE_ITEM = items.register("coffee_tree",
                () -> new BlockItem(ModBlocks.COFFEE_TREE.get(), new Item.Properties()));
        ModItems.BLUEBERRY_BUSH_ITEM = items.register("blueberry_bush",
                () -> new BlockItem(ModBlocks.BLUEBERRY_BUSH.get(), new Item.Properties()));
        ModItems.PLATE = items.register("plate",
                () -> new BlockItem(ModBlocks.PLATE.get(), new Item.Properties()));
        ModItems.COLD_BREW_POT = items.register("coldbrew_pot",
                () -> new BlockItem(ModBlocks.COLD_BREW_POT.get(), new Item.Properties()));
        ModItems.SODA_ORE = items.register("soda_ore",
                () -> new BlockItem(ModBlocks.SODA_ORE.get(), new Item.Properties()));
        ModItems.XMAS_TREE = items.register("xmas_tree",
                () -> new BlockItem(ModBlocks.XMAS_TREE.get(), new Item.Properties()));
        ModItems.GINGER_HOUSE = items.register("ginger_house",
                () -> new BlockItem(ModBlocks.GINGER_HOUSE.get(), new Item.Properties()));

        // ── Bags ──
        ModItems.BAG_COFFEE = items.register("bag_coffee",
                () -> new BlockItem(ModBlocks.BAG_COFFEE.get(), new Item.Properties()));
        ModItems.BAG_COFFEE_RAW = items.register("bag_coffee_raw",
                () -> new BlockItem(ModBlocks.BAG_COFFEE_RAW.get(), new Item.Properties()));
        ModItems.BAG_COCOA = items.register("bag_cocoa",
                () -> new BlockItem(ModBlocks.BAG_COCOA.get(), new Item.Properties()));
        ModItems.BAG_COCOA_POWDER = items.register("bag_cocoa_powder",
                () -> new BlockItem(ModBlocks.BAG_COCOA_POWDER.get(), new Item.Properties()));
        ModItems.BAG_FLOUR = items.register("bag_flour",
                () -> new BlockItem(ModBlocks.BAG_FLOUR.get(), new Item.Properties()));
        ModItems.BAG_COFFEE_POWDER = items.register("bag_coffee_powder",
                () -> new BlockItem(ModBlocks.BAG_COFFEE_POWDER.get(), new Item.Properties()));
        ModItems.BAG_SUGAR = items.register("bag_sugar",
                () -> new BlockItem(ModBlocks.BAG_SUGAR.get(), new Item.Properties()));

        // ── Double Bags ──
        ModItems.DOUBLE_BAG_COFFEE = items.register("double_bag_coffee",
                () -> new BlockItem(ModBlocks.DOUBLE_BAG_COFFEE.get(), new Item.Properties()));
        ModItems.DOUBLE_BAG_COFFEE_RAW = items.register("double_bag_coffee_raw",
                () -> new BlockItem(ModBlocks.DOUBLE_BAG_COFFEE_RAW.get(), new Item.Properties()));
        ModItems.DOUBLE_BAG_COCOA = items.register("double_bag_cocoa",
                () -> new BlockItem(ModBlocks.DOUBLE_BAG_COCOA.get(), new Item.Properties()));
        ModItems.DOUBLE_BAG_COCOA_POWDER = items.register("double_bag_cocoa_powder",
                () -> new BlockItem(ModBlocks.DOUBLE_BAG_COCOA_POWDER.get(), new Item.Properties()));
        ModItems.DOUBLE_BAG_FLOUR = items.register("double_bag_flour",
                () -> new BlockItem(ModBlocks.DOUBLE_BAG_FLOUR.get(), new Item.Properties()));
        ModItems.DOUBLE_BAG_COFFEE_POWDER = items.register("double_bag_coffee_powder",
                () -> new BlockItem(ModBlocks.DOUBLE_BAG_COFFEE_POWDER.get(), new Item.Properties()));
        ModItems.DOUBLE_BAG_SUGAR = items.register("double_bag_sugar",
                () -> new BlockItem(ModBlocks.DOUBLE_BAG_SUGAR.get(), new Item.Properties()));

        // ── Cakes ──
        ModItems.CAKE_SPONGE = items.register("cake_sponge",
                () -> new BlockItem(ModBlocks.CAKE_SPONGE.get(), new Item.Properties()));
        ModItems.CAKE_SPONGE_CHOCOLATE = items.register("cake_sponge_chocolate",
                () -> new BlockItem(ModBlocks.CAKE_SPONGE_CHOCOLATE.get(), new Item.Properties()));
        ModItems.CAKE_SPONGE_COFFEE = items.register("cake_sponge_coffee",
                () -> new BlockItem(ModBlocks.CAKE_SPONGE_COFFEE.get(), new Item.Properties()));
        ModItems.CAKE_SPONGE_PUMPKIN = items.register("cake_sponge_pumpkin",
                () -> new BlockItem(ModBlocks.CAKE_SPONGE_PUMPKIN.get(), new Item.Properties()));
        ModItems.CAKE_SPONGE_CARROT = items.register("cake_sponge_carrot",
                () -> new BlockItem(ModBlocks.CAKE_SPONGE_CARROT.get(), new Item.Properties()));
        ModItems.CAKE_SPONGE_REDVELVET = items.register("cake_sponge_redvelvet",
                () -> new BlockItem(ModBlocks.CAKE_SPONGE_REDVELVET.get(), new Item.Properties()));
        ModItems.CAKE_SPONGE_LEMON = items.register("cake_sponge_lemon",
                () -> new BlockItem(ModBlocks.CAKE_SPONGE_LEMON.get(), new Item.Properties()));
        ModItems.CAKE_SPONGE_TEA = items.register("cake_sponge_tea",
                () -> new BlockItem(ModBlocks.CAKE_SPONGE_TEA.get(), new Item.Properties()));
        ModItems.CAKE_SPONGE_BERRY = items.register("cake_sponge_berry",
                () -> new BlockItem(ModBlocks.CAKE_SPONGE_BERRY.get(), new Item.Properties()));
        ModItems.CAKE_COFFEE = items.register("cake_coffee",
                () -> new BlockItem(ModBlocks.CAKE_COFFEE.get(), new Item.Properties()));
        ModItems.CAKE_HARVEST = items.register("cake_harvest",
                () -> new BlockItem(ModBlocks.CAKE_HARVEST.get(), new Item.Properties()));
        ModItems.CAKE_LEMON = items.register("cake_lemon",
                () -> new BlockItem(ModBlocks.CAKE_LEMON.get(), new Item.Properties()));
        ModItems.CAKE_TEA = items.register("cake_tea",
                () -> new BlockItem(ModBlocks.CAKE_TEA.get(), new Item.Properties()));
        ModItems.CAKE_BERRY = items.register("cake_berry",
                () -> new BlockItem(ModBlocks.CAKE_BERRY.get(), new Item.Properties()));
        ModItems.CAKE_CHEESE = items.register("cake_cheese",
                () -> new BlockItem(ModBlocks.CAKE_CHEESE.get(), new Item.Properties()));
        ModItems.CAKE_SCHWARZWALD = items.register("cake_schwarzwald",
                () -> new BlockItem(ModBlocks.CAKE_SCHWARZWALD.get(), new Item.Properties()));
        ModItems.CAKE_REDVELVET = items.register("cake_redvelvet",
                () -> new BlockItem(ModBlocks.CAKE_REDVELVET.get(), new Item.Properties()));
        ModItems.TIRAMISU = items.register("tiramisu",
                () -> new BlockItem(ModBlocks.TIRAMISU.get(), new Item.Properties()));
        ModItems.MOUSSE_BERRY = items.register("mousse_berry",
                () -> new BlockItem(ModBlocks.MOUSSE_BERRY.get(), new Item.Properties()));
        ModItems.MOUSSE_LEMON = items.register("mousse_lemon",
                () -> new BlockItem(ModBlocks.MOUSSE_LEMON.get(), new Item.Properties()));
        ModItems.MOUSSE_CHOCOLATE = items.register("mousse_chocolate",
                () -> new BlockItem(ModBlocks.MOUSSE_CHOCOLATE.get(), new Item.Properties()));
        ModItems.MOUSSE_COFFEE = items.register("mousse_coffee",
                () -> new BlockItem(ModBlocks.MOUSSE_COFFEE.get(), new Item.Properties()));

        // Phase 5.4: cake_carrot
        ModItems.CAKE_CARROT = items.register("cake_carrot",
                () -> new BlockItem(ModBlocks.CAKE_CARROT.get(), new Item.Properties()));

        // ── Seeds ──
        ModItems.COFFEE_SEEDS = items.register("coffee_seeds",
                () -> new SeedCoffee(ModBlocks.COFFEE_TREE.get(), new Item.Properties()));
        ModItems.VANILLA_SEEDS = items.register("vanilla_seeds",
                () -> new SeedCoffee(ModBlocks.VANILLA_CROP.get(), new Item.Properties()));

        // ── Records ──
        ModItems.RECORD_BLANK = items.register("record_blank",
                () -> new Item(new Item.Properties().stacksTo(1).rarity(Rarity.RARE)));
        ModItems.RECORD_KUSA_NOSHI_TO_NE = items.register("record_kusa_noshi_to_ne",
                () -> new ItemRecordCW(1,
                        ModSounds.RECORD_KUSA_NOSHI_TO_NE,
                        new Item.Properties().rarity(Rarity.RARE),
                        "item.coffeework.record_kusa_noshi_to_ne.desc", 100));
        ModItems.RECORD_LAZY_LADY_KAGUYA = items.register("record_lazy_lady_kaguya",
                () -> new ItemRecordCW(1,
                        ModSounds.RECORD_LAZY_LADY_KAGUYA,
                        new Item.Properties().rarity(Rarity.RARE),
                        "item.coffeework.record_lazy_lady_kaguya.desc", 100));
        ModItems.RECORD_THE_GRIMOIRE_OF_MARISA = items.register("record_the_grimoire_of_marisa",
                () -> new ItemRecordCW(1,
                        ModSounds.RECORD_THE_GRIMOIRE_OF_MARISA,
                        new Item.Properties().rarity(Rarity.RARE),
                        "item.coffeework.record_the_grimoire_of_marisa.desc", 100));

        // Phase 9: Moka Pot block item
        ModItems.MOKA_POT_ITEM = items.register("moka_pot",
                () -> new BlockItem(ModBlocks.MOKA_POT.get(), new Item.Properties()));
        ModItems.TURKISH_COFFEE_POT_ITEM = items.register("turkish_coffee_pot",
                () -> new BlockItem(ModBlocks.TURKISH_COFFEE_POT.get(), new Item.Properties()));
        ModItems.COFFEE_POT_ITEM = items.register("coffee_pot",
                () -> new BlockItem(ModBlocks.COFFEE_POT.get(), new Item.Properties()));
        ModItems.SODA_MACHINE_ITEM = items.register("soda_machine",
                () -> new BlockItem(ModBlocks.SODA_MACHINE.get(), new Item.Properties()));
    }
}
