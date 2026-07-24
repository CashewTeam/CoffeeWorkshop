package net.langball.coffee.init;

import net.langball.coffee.CoffeeWork;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModBlocks {
    public static final DeferredRegister<Block> BLOCKS =
            DeferredRegister.create(ForgeRegistries.BLOCKS, CoffeeWork.MODID);

    // ========== Machines ==========
    public static final RegistryObject<Block> GRINDER = BLOCKS.register("grinder_off",
            () -> new Block(BlockBehaviour.Properties.of().mapColor(MapColor.STONE).strength(3.0f, 5.0f).requiresCorrectToolForDrops()));
    public static final RegistryObject<Block> GRINDER_ON = BLOCKS.register("grinder_on",
            () -> new Block(BlockBehaviour.Properties.of().mapColor(MapColor.STONE).strength(3.0f, 5.0f).lightLevel(s -> 14).requiresCorrectToolForDrops()));

    public static final RegistryObject<Block> COFFEE_MACHINE = BLOCKS.register("coffeemachine_off",
            () -> new Block(BlockBehaviour.Properties.of().mapColor(MapColor.STONE).strength(3.0f, 5.0f).requiresCorrectToolForDrops()));
    public static final RegistryObject<Block> COFFEE_MACHINE_ON = BLOCKS.register("coffeemachine_on",
            () -> new Block(BlockBehaviour.Properties.of().mapColor(MapColor.STONE).strength(3.0f, 5.0f).lightLevel(s -> 14).requiresCorrectToolForDrops()));

    public static final RegistryObject<Block> ICECREAM_MACHINE = BLOCKS.register("icecreammachine_off",
            () -> new Block(BlockBehaviour.Properties.of().mapColor(MapColor.STONE).strength(3.0f, 5.0f).requiresCorrectToolForDrops()));
    public static final RegistryObject<Block> ICECREAM_MACHINE_ON = BLOCKS.register("icecreammachine_on",
            () -> new Block(BlockBehaviour.Properties.of().mapColor(MapColor.STONE).strength(3.0f, 5.0f).lightLevel(s -> 14).requiresCorrectToolForDrops()));

    public static final RegistryObject<Block> ROLLER = BLOCKS.register("roller_off",
            () -> new Block(BlockBehaviour.Properties.of().mapColor(MapColor.STONE).strength(3.0f, 5.0f).requiresCorrectToolForDrops()));
    public static final RegistryObject<Block> ROLLER_ON = BLOCKS.register("roller_on",
            () -> new Block(BlockBehaviour.Properties.of().mapColor(MapColor.STONE).strength(3.0f, 5.0f).lightLevel(s -> 14).requiresCorrectToolForDrops()));

    public static final RegistryObject<Block> OVEN = BLOCKS.register("oven_off",
            () -> new Block(BlockBehaviour.Properties.of().mapColor(MapColor.STONE).strength(3.0f, 5.0f).requiresCorrectToolForDrops()));
    public static final RegistryObject<Block> OVEN_ON = BLOCKS.register("oven_on",
            () -> new Block(BlockBehaviour.Properties.of().mapColor(MapColor.STONE).strength(3.0f, 5.0f).lightLevel(s -> 14).requiresCorrectToolForDrops()));

    // ========== Plants ==========
    public static final RegistryObject<Block> COFFEE_TREE = BLOCKS.register("coffee_tree",
            () -> new Block(BlockBehaviour.Properties.of().mapColor(MapColor.PLANT).noCollission().randomTicks().instabreak().sound(SoundType.CROP)));

    public static final RegistryObject<Block> BLUEBERRY_BUSH = BLOCKS.register("blueberry_bush",
            () -> new Block(BlockBehaviour.Properties.of().mapColor(MapColor.PLANT).noCollission().randomTicks().instabreak().sound(SoundType.SWEET_BERRY_BUSH)));

    public static final RegistryObject<Block> VANILLA_CROP = BLOCKS.register("vanilla_crop",
            () -> new Block(BlockBehaviour.Properties.of().mapColor(MapColor.PLANT).noCollission().randomTicks().instabreak().sound(SoundType.CROP)));

    // ========== Decor & Utility ==========
    public static final RegistryObject<Block> PLATE = BLOCKS.register("plate",
            () -> new Block(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_LIGHT_GRAY).strength(0.5f).sound(SoundType.STONE).noOcclusion()));

    public static final RegistryObject<Block> COLD_BREW_POT = BLOCKS.register("coldbrew_pot",
            () -> new Block(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(1.0f).sound(SoundType.LANTERN).noOcclusion()));

    public static final RegistryObject<Block> SODA_ORE = BLOCKS.register("soda_ore",
            () -> new Block(BlockBehaviour.Properties.of().mapColor(MapColor.STONE).strength(3.0f, 3.0f).requiresCorrectToolForDrops()));

    public static final RegistryObject<Block> XMAS_TREE = BLOCKS.register("xmas_tree",
            () -> new Block(BlockBehaviour.Properties.of().mapColor(MapColor.WOOD).strength(0.5f).sound(SoundType.WOOD).noOcclusion()));

    public static final RegistryObject<Block> GINGER_HOUSE = BLOCKS.register("ginger_house",
            () -> new Block(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_BROWN).strength(0.5f).sound(SoundType.WOOD).noOcclusion()));

    // ========== Coffee Plates (instant coffee display block) ==========
    public static final RegistryObject<Block> COFFEE_INSTANT_PLATE = BLOCKS.register("coffee_instant_plate",
            () -> new Block(BlockBehaviour.Properties.of().mapColor(MapColor.STONE).strength(-1.0f, 3600000.0f).noOcclusion().noLootTable()));

    // ========== Bags (single) ==========
    public static final RegistryObject<Block> BAG_COFFEE = BLOCKS.register("bag_coffee",
            () -> new Block(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_BROWN).strength(0.5f).sound(SoundType.WOOL)));
    public static final RegistryObject<Block> BAG_COFFEE_RAW = BLOCKS.register("bag_coffee_raw",
            () -> new Block(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_GREEN).strength(0.5f).sound(SoundType.WOOL)));
    public static final RegistryObject<Block> BAG_COCOA = BLOCKS.register("bag_cocoa",
            () -> new Block(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_BROWN).strength(0.5f).sound(SoundType.WOOL)));
    public static final RegistryObject<Block> BAG_COCOA_POWDER = BLOCKS.register("bag_cocoa_powder",
            () -> new Block(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_BROWN).strength(0.5f).sound(SoundType.WOOL)));
    public static final RegistryObject<Block> BAG_FLOUR = BLOCKS.register("bag_flour",
            () -> new Block(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_WHITE).strength(0.5f).sound(SoundType.WOOL)));
    public static final RegistryObject<Block> BAG_COFFEE_POWDER = BLOCKS.register("bag_coffee_powder",
            () -> new Block(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_BROWN).strength(0.5f).sound(SoundType.WOOL)));
    public static final RegistryObject<Block> BAG_SUGAR = BLOCKS.register("bag_sugar",
            () -> new Block(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_WHITE).strength(0.5f).sound(SoundType.WOOL)));

    // ========== Double Bags ==========
    public static final RegistryObject<Block> DOUBLE_BAG_COFFEE = BLOCKS.register("double_bag_coffee",
            () -> new Block(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_BROWN).strength(0.5f).sound(SoundType.WOOL)));
    public static final RegistryObject<Block> DOUBLE_BAG_COFFEE_RAW = BLOCKS.register("double_bag_coffee_raw",
            () -> new Block(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_GREEN).strength(0.5f).sound(SoundType.WOOL)));
    public static final RegistryObject<Block> DOUBLE_BAG_COCOA = BLOCKS.register("double_bag_cocoa",
            () -> new Block(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_BROWN).strength(0.5f).sound(SoundType.WOOL)));
    public static final RegistryObject<Block> DOUBLE_BAG_COCOA_POWDER = BLOCKS.register("double_bag_cocoa_powder",
            () -> new Block(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_BROWN).strength(0.5f).sound(SoundType.WOOL)));
    public static final RegistryObject<Block> DOUBLE_BAG_FLOUR = BLOCKS.register("double_bag_flour",
            () -> new Block(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_WHITE).strength(0.5f).sound(SoundType.WOOL)));
    public static final RegistryObject<Block> DOUBLE_BAG_COFFEE_POWDER = BLOCKS.register("double_bag_coffee_powder",
            () -> new Block(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_BROWN).strength(0.5f).sound(SoundType.WOOL)));
    public static final RegistryObject<Block> DOUBLE_BAG_SUGAR = BLOCKS.register("double_bag_sugar",
            () -> new Block(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_WHITE).strength(0.5f).sound(SoundType.WOOL)));

    // ========== Cakes ==========
    // Sponge cakes (layer 1)
    public static final RegistryObject<Block> CAKE_SPONGE = BLOCKS.register("cake_sponge",
            () -> new Block(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_LIGHT_GRAY).strength(0.5f).sound(SoundType.WOOL)));
    public static final RegistryObject<Block> CAKE_SPONGE_CHOCOLATE = BLOCKS.register("cake_sponge_chocolate",
            () -> new Block(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_BROWN).strength(0.5f).sound(SoundType.WOOL)));
    public static final RegistryObject<Block> CAKE_SPONGE_COFFEE = BLOCKS.register("cake_sponge_coffee",
            () -> new Block(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_BROWN).strength(0.5f).sound(SoundType.WOOL)));
    public static final RegistryObject<Block> CAKE_SPONGE_PUMPKIN = BLOCKS.register("cake_sponge_pumpkin",
            () -> new Block(BlockBehaviour.Properties.of().mapColor(MapColor.TERRACOTTA_ORANGE).strength(0.5f).sound(SoundType.WOOL)));
    public static final RegistryObject<Block> CAKE_SPONGE_CARROT = BLOCKS.register("cake_sponge_carrot",
            () -> new Block(BlockBehaviour.Properties.of().mapColor(MapColor.TERRACOTTA_ORANGE).strength(0.5f).sound(SoundType.WOOL)));
    public static final RegistryObject<Block> CAKE_SPONGE_REDVELVET = BLOCKS.register("cake_sponge_redvelvet",
            () -> new Block(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_RED).strength(0.5f).sound(SoundType.WOOL)));
    public static final RegistryObject<Block> CAKE_SPONGE_LEMON = BLOCKS.register("cake_sponge_lemon",
            () -> new Block(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_YELLOW).strength(0.5f).sound(SoundType.WOOL)));
    public static final RegistryObject<Block> CAKE_SPONGE_TEA = BLOCKS.register("cake_sponge_tea",
            () -> new Block(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_GREEN).strength(0.5f).sound(SoundType.WOOL)));
    public static final RegistryObject<Block> CAKE_SPONGE_BERRY = BLOCKS.register("cake_sponge_berry",
            () -> new Block(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_PURPLE).strength(0.5f).sound(SoundType.WOOL)));

    // Large cakes (layer 3)
    public static final RegistryObject<Block> CAKE_COFFEE = BLOCKS.register("cake_coffee",
            () -> new Block(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_BROWN).strength(0.5f).sound(SoundType.WOOL)));
    public static final RegistryObject<Block> CAKE_HARVEST = BLOCKS.register("cake_harvest",
            () -> new Block(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_ORANGE).strength(0.5f).sound(SoundType.WOOL)));
    public static final RegistryObject<Block> CAKE_LEMON = BLOCKS.register("cake_lemon",
            () -> new Block(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_YELLOW).strength(0.5f).sound(SoundType.WOOL)));
    public static final RegistryObject<Block> CAKE_TEA = BLOCKS.register("cake_tea",
            () -> new Block(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_GREEN).strength(0.5f).sound(SoundType.WOOL)));
    public static final RegistryObject<Block> CAKE_BERRY = BLOCKS.register("cake_berry",
            () -> new Block(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_PURPLE).strength(0.5f).sound(SoundType.WOOL)));
    public static final RegistryObject<Block> CAKE_CHEESE = BLOCKS.register("cake_cheese",
            () -> new Block(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_YELLOW).strength(0.5f).sound(SoundType.WOOL)));
    public static final RegistryObject<Block> CAKE_SCHWARZWALD = BLOCKS.register("cake_schwarzwald",
            () -> new Block(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_BROWN).strength(0.5f).sound(SoundType.WOOL)));
    public static final RegistryObject<Block> CAKE_REDVELVET = BLOCKS.register("cake_redvelvet",
            () -> new Block(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_RED).strength(0.5f).sound(SoundType.WOOL)));

    // Special
    public static final RegistryObject<Block> TIRAMISU = BLOCKS.register("tiramisu",
            () -> new Block(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_BROWN).strength(0.5f).sound(SoundType.WOOL)));

    // Mousse (layer 3)
    public static final RegistryObject<Block> MOUSSE_BERRY = BLOCKS.register("mousse_berry",
            () -> new Block(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_PURPLE).strength(0.5f).sound(SoundType.WOOL)));
    public static final RegistryObject<Block> MOUSSE_LEMON = BLOCKS.register("mousse_lemon",
            () -> new Block(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_YELLOW).strength(0.5f).sound(SoundType.WOOL)));
    public static final RegistryObject<Block> MOUSSE_CHOCOLATE = BLOCKS.register("mousse_chocolate",
            () -> new Block(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_BROWN).strength(0.5f).sound(SoundType.WOOL)));
    public static final RegistryObject<Block> MOUSSE_COFFEE = BLOCKS.register("mousse_coffee",
            () -> new Block(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_BROWN).strength(0.5f).sound(SoundType.WOOL)));
}
