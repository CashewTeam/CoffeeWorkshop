package net.langball.coffee.init;

import net.langball.coffee.CoffeeWork;
import net.langball.coffee.block.*;
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
            () -> new BlockGrinder(false, BlockBehaviour.Properties.of().mapColor(MapColor.STONE).strength(5.0F).requiresCorrectToolForDrops().noOcclusion()));
    public static final RegistryObject<Block> GRINDER_ON = BLOCKS.register("grinder_on",
            () -> new BlockGrinder(true, BlockBehaviour.Properties.of().mapColor(MapColor.STONE).strength(5.0F).lightLevel(s -> 14).requiresCorrectToolForDrops().noOcclusion()));

    public static final RegistryObject<Block> COFFEE_MACHINE = BLOCKS.register("coffeemachine_off",
            () -> new BlockCoffeeMachine(false, BlockBehaviour.Properties.of().mapColor(MapColor.STONE).strength(5.0F).requiresCorrectToolForDrops().noOcclusion()));
    public static final RegistryObject<Block> COFFEE_MACHINE_ON = BLOCKS.register("coffeemachine_on",
            () -> new BlockCoffeeMachine(true, BlockBehaviour.Properties.of().mapColor(MapColor.STONE).strength(5.0F).lightLevel(s -> 14).requiresCorrectToolForDrops().noOcclusion()));

    public static final RegistryObject<Block> ICECREAM_MACHINE = BLOCKS.register("icecreammachine_off",
            () -> new BlockIcecreamMachine(false, BlockBehaviour.Properties.of().mapColor(MapColor.STONE).strength(5.0F).requiresCorrectToolForDrops().noOcclusion()));
    public static final RegistryObject<Block> ICECREAM_MACHINE_ON = BLOCKS.register("icecreammachine_on",
            () -> new BlockIcecreamMachine(true, BlockBehaviour.Properties.of().mapColor(MapColor.STONE).strength(5.0F).lightLevel(s -> 14).requiresCorrectToolForDrops().noOcclusion()));

    public static final RegistryObject<Block> ROLLER = BLOCKS.register("roller_off",
            () -> new BlockRoller(false, BlockBehaviour.Properties.of().mapColor(MapColor.STONE).strength(5.0F).requiresCorrectToolForDrops().noOcclusion()));
    public static final RegistryObject<Block> ROLLER_ON = BLOCKS.register("roller_on",
            () -> new BlockRoller(true, BlockBehaviour.Properties.of().mapColor(MapColor.STONE).strength(5.0F).lightLevel(s -> 14).requiresCorrectToolForDrops().noOcclusion()));

    public static final RegistryObject<Block> OVEN = BLOCKS.register("oven_off",
            () -> new BlockClayOven(false, BlockBehaviour.Properties.of().mapColor(MapColor.STONE).strength(5.0F).requiresCorrectToolForDrops().noOcclusion()));
    public static final RegistryObject<Block> OVEN_ON = BLOCKS.register("oven_on",
            () -> new BlockClayOven(true, BlockBehaviour.Properties.of().mapColor(MapColor.STONE).strength(5.0F).lightLevel(s -> 14).requiresCorrectToolForDrops().noOcclusion()));

    // ========== Plants ==========
    public static final RegistryObject<Block> COFFEE_TREE = BLOCKS.register("coffee_tree",
            () -> new BlockCoffeeTree(BlockBehaviour.Properties.of().mapColor(MapColor.PLANT).noCollission().randomTicks().instabreak().sound(SoundType.CROP)));

    public static final RegistryObject<Block> BLUEBERRY_BUSH = BLOCKS.register("blueberry_bush",
            () -> new BlockBlueBerryBush(BlockBehaviour.Properties.of().mapColor(MapColor.PLANT).noCollission().randomTicks().instabreak().sound(SoundType.SWEET_BERRY_BUSH)));

    public static final RegistryObject<Block> VANILLA_CROP = BLOCKS.register("vanilla_crop",
            () -> new BlockVanilla(BlockBehaviour.Properties.of().mapColor(MapColor.PLANT).noCollission().randomTicks().instabreak().sound(SoundType.CROP)));

    // ========== Decor & Utility ==========
    public static final RegistryObject<Block> PLATE = BLOCKS.register("plate",
            () -> new BlockPlate(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_LIGHT_GRAY).strength(0.5F).sound(SoundType.STONE).noOcclusion()));

    public static final RegistryObject<Block> COLD_BREW_POT = BLOCKS.register("coldbrew_pot",
            () -> new BlockColdBrewPot(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(1.0F).sound(SoundType.LANTERN).noOcclusion()));

    public static final RegistryObject<Block> SODA_ORE = BLOCKS.register("soda_ore",
            () -> new BlockOreSoda(BlockBehaviour.Properties.of().mapColor(MapColor.STONE).strength(3.0F, 3.0F).requiresCorrectToolForDrops()));

    public static final RegistryObject<Block> XMAS_TREE = BLOCKS.register("xmas_tree",
            () -> new BlockXmasTree(BlockBehaviour.Properties.of().mapColor(MapColor.WOOD).strength(0.5F).sound(SoundType.WOOD).noOcclusion()));

    public static final RegistryObject<Block> GINGER_HOUSE = BLOCKS.register("ginger_house",
            () -> new BlockGingerHouse(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_BROWN).strength(0.5F).sound(SoundType.WOOD).noOcclusion()));

    // ========== Bags (single) ==========
    public static final RegistryObject<Block> BAG_COFFEE = BLOCKS.register("bag_coffee",
            () -> new BlockBag(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_BROWN).strength(0.5F).sound(SoundType.WOOL)));
    public static final RegistryObject<Block> BAG_COFFEE_RAW = BLOCKS.register("bag_coffee_raw",
            () -> new BlockBag(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_GREEN).strength(0.5F).sound(SoundType.WOOL)));
    public static final RegistryObject<Block> BAG_COCOA = BLOCKS.register("bag_cocoa",
            () -> new BlockBag(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_BROWN).strength(0.5F).sound(SoundType.WOOL)));
    public static final RegistryObject<Block> BAG_COCOA_POWDER = BLOCKS.register("bag_cocoa_powder",
            () -> new BlockBag(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_BROWN).strength(0.5F).sound(SoundType.WOOL)));
    public static final RegistryObject<Block> BAG_FLOUR = BLOCKS.register("bag_flour",
            () -> new BlockBag(BlockBehaviour.Properties.of().mapColor(MapColor.SNOW).strength(0.5F).sound(SoundType.WOOL)));
    public static final RegistryObject<Block> BAG_COFFEE_POWDER = BLOCKS.register("bag_coffee_powder",
            () -> new BlockBag(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_BROWN).strength(0.5F).sound(SoundType.WOOL)));
    public static final RegistryObject<Block> BAG_SUGAR = BLOCKS.register("bag_sugar",
            () -> new BlockBag(BlockBehaviour.Properties.of().mapColor(MapColor.SNOW).strength(0.5F).sound(SoundType.WOOL)));

    // ========== Double Bags ==========
    public static final RegistryObject<Block> DOUBLE_BAG_COFFEE = BLOCKS.register("double_bag_coffee",
            () -> new BlockBag2(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_BROWN).strength(0.5F).sound(SoundType.WOOL)));
    public static final RegistryObject<Block> DOUBLE_BAG_COFFEE_RAW = BLOCKS.register("double_bag_coffee_raw",
            () -> new BlockBag2(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_GREEN).strength(0.5F).sound(SoundType.WOOL)));
    public static final RegistryObject<Block> DOUBLE_BAG_COCOA = BLOCKS.register("double_bag_cocoa",
            () -> new BlockBag2(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_BROWN).strength(0.5F).sound(SoundType.WOOL)));
    public static final RegistryObject<Block> DOUBLE_BAG_COCOA_POWDER = BLOCKS.register("double_bag_cocoa_powder",
            () -> new BlockBag2(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_BROWN).strength(0.5F).sound(SoundType.WOOL)));
    public static final RegistryObject<Block> DOUBLE_BAG_FLOUR = BLOCKS.register("double_bag_flour",
            () -> new BlockBag2(BlockBehaviour.Properties.of().mapColor(MapColor.SNOW).strength(0.5F).sound(SoundType.WOOL)));
    public static final RegistryObject<Block> DOUBLE_BAG_COFFEE_POWDER = BLOCKS.register("double_bag_coffee_powder",
            () -> new BlockBag2(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_BROWN).strength(0.5F).sound(SoundType.WOOL)));
    public static final RegistryObject<Block> DOUBLE_BAG_SUGAR = BLOCKS.register("double_bag_sugar",
            () -> new BlockBag2(BlockBehaviour.Properties.of().mapColor(MapColor.SNOW).strength(0.5F).sound(SoundType.WOOL)));

    // ========== Cakes (sponge, layer 1) ==========
    public static final RegistryObject<Block> CAKE_SPONGE = BLOCKS.register("cake_sponge",
            () -> new BlockCakeBasic(1, BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_LIGHT_GRAY).strength(0.5F).sound(SoundType.WOOL)));
    public static final RegistryObject<Block> CAKE_SPONGE_CHOCOLATE = BLOCKS.register("cake_sponge_chocolate",
            () -> new BlockCakeBasic(1, BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_BROWN).strength(0.5F).sound(SoundType.WOOL)));
    public static final RegistryObject<Block> CAKE_SPONGE_COFFEE = BLOCKS.register("cake_sponge_coffee",
            () -> new BlockCakeBasic(1, BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_BROWN).strength(0.5F).sound(SoundType.WOOL)));
    public static final RegistryObject<Block> CAKE_SPONGE_PUMPKIN = BLOCKS.register("cake_sponge_pumpkin",
            () -> new BlockCakeBasic(1, BlockBehaviour.Properties.of().mapColor(MapColor.TERRACOTTA_ORANGE).strength(0.5F).sound(SoundType.WOOL)));
    public static final RegistryObject<Block> CAKE_SPONGE_CARROT = BLOCKS.register("cake_sponge_carrot",
            () -> new BlockCakeBasic(1, BlockBehaviour.Properties.of().mapColor(MapColor.TERRACOTTA_ORANGE).strength(0.5F).sound(SoundType.WOOL)));
    public static final RegistryObject<Block> CAKE_SPONGE_REDVELVET = BLOCKS.register("cake_sponge_redvelvet",
            () -> new BlockCakeBasic(1, BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_RED).strength(0.5F).sound(SoundType.WOOL)));
    public static final RegistryObject<Block> CAKE_SPONGE_LEMON = BLOCKS.register("cake_sponge_lemon",
            () -> new BlockCakeBasic(1, BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_YELLOW).strength(0.5F).sound(SoundType.WOOL)));
    public static final RegistryObject<Block> CAKE_SPONGE_TEA = BLOCKS.register("cake_sponge_tea",
            () -> new BlockCakeBasic(1, BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_GREEN).strength(0.5F).sound(SoundType.WOOL)));
    public static final RegistryObject<Block> CAKE_SPONGE_BERRY = BLOCKS.register("cake_sponge_berry",
            () -> new BlockCakeBasic(1, BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_PURPLE).strength(0.5F).sound(SoundType.WOOL)));

    // ========== Cakes (large, layer 3) ==========
    public static final RegistryObject<Block> CAKE_COFFEE = BLOCKS.register("cake_coffee",
            () -> new BlockCakeBasic(3, BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_BROWN).strength(0.5F).sound(SoundType.WOOL)));
    public static final RegistryObject<Block> CAKE_HARVEST = BLOCKS.register("cake_harvest",
            () -> new BlockCakeBasic(3, BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_ORANGE).strength(0.5F).sound(SoundType.WOOL)));
    public static final RegistryObject<Block> CAKE_LEMON = BLOCKS.register("cake_lemon",
            () -> new BlockCakeBasic(3, BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_YELLOW).strength(0.5F).sound(SoundType.WOOL)));
    public static final RegistryObject<Block> CAKE_TEA = BLOCKS.register("cake_tea",
            () -> new BlockCakeBasic(3, BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_GREEN).strength(0.5F).sound(SoundType.WOOL)));
    public static final RegistryObject<Block> CAKE_BERRY = BLOCKS.register("cake_berry",
            () -> new BlockCakeBasic(3, BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_PURPLE).strength(0.5F).sound(SoundType.WOOL)));
    public static final RegistryObject<Block> CAKE_CHEESE = BLOCKS.register("cake_cheese",
            () -> new BlockCakeBasic(3, BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_YELLOW).strength(0.5F).sound(SoundType.WOOL)));
    public static final RegistryObject<Block> CAKE_SCHWARZWALD = BLOCKS.register("cake_schwarzwald",
            () -> new BlockCakeBasic(3, BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_BROWN).strength(0.5F).sound(SoundType.WOOL)));
    public static final RegistryObject<Block> CAKE_REDVELVET = BLOCKS.register("cake_redvelvet",
            () -> new BlockCakeBasic(3, BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_RED).strength(0.5F).sound(SoundType.WOOL)));

    // ========== Special cakes ==========
    public static final RegistryObject<Block> TIRAMISU = BLOCKS.register("tiramisu",
            () -> new BlockCakeBasic(5, BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_BROWN).strength(0.5F).sound(SoundType.WOOL)));

    // ========== Mousse ==========
    public static final RegistryObject<Block> MOUSSE_BERRY = BLOCKS.register("mousse_berry",
            () -> new BlockCakeBasic(3, BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_PURPLE).strength(0.5F).sound(SoundType.WOOL)));
    public static final RegistryObject<Block> MOUSSE_LEMON = BLOCKS.register("mousse_lemon",
            () -> new BlockCakeBasic(3, BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_YELLOW).strength(0.5F).sound(SoundType.WOOL)));
    public static final RegistryObject<Block> MOUSSE_CHOCOLATE = BLOCKS.register("mousse_chocolate",
            () -> new BlockCakeBasic(3, BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_BROWN).strength(0.5F).sound(SoundType.WOOL)));
    public static final RegistryObject<Block> MOUSSE_COFFEE = BLOCKS.register("mousse_coffee",
            () -> new BlockCakeBasic(3, BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_BROWN).strength(0.5F).sound(SoundType.WOOL)));
}
