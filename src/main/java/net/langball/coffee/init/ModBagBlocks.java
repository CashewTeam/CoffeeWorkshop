package net.langball.coffee.init;

import net.langball.coffee.block.*;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.registries.DeferredRegister;

/**
 * Bag blocks: 7 single bags + 7 double bags.
 */
public class ModBagBlocks {

    static void registerAll(DeferredRegister<Block> blocks) {
        ModBlocks.BAG_COFFEE = blocks.register("bag_coffee",
                () -> new BlockBag(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_BROWN).strength(0.5F).sound(SoundType.WOOL)));
        ModBlocks.BAG_COFFEE_RAW = blocks.register("bag_coffee_raw",
                () -> new BlockBag(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_GREEN).strength(0.5F).sound(SoundType.WOOL)));
        ModBlocks.BAG_COCOA = blocks.register("bag_cocoa",
                () -> new BlockBag(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_BROWN).strength(0.5F).sound(SoundType.WOOL)));
        ModBlocks.BAG_COCOA_POWDER = blocks.register("bag_cocoa_powder",
                () -> new BlockBag(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_BROWN).strength(0.5F).sound(SoundType.WOOL)));
        ModBlocks.BAG_FLOUR = blocks.register("bag_flour",
                () -> new BlockBag(BlockBehaviour.Properties.of().mapColor(MapColor.SNOW).strength(0.5F).sound(SoundType.WOOL)));
        ModBlocks.BAG_COFFEE_POWDER = blocks.register("bag_coffee_powder",
                () -> new BlockBag(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_BROWN).strength(0.5F).sound(SoundType.WOOL)));
        ModBlocks.BAG_SUGAR = blocks.register("bag_sugar",
                () -> new BlockBag(BlockBehaviour.Properties.of().mapColor(MapColor.SNOW).strength(0.5F).sound(SoundType.WOOL)));

        ModBlocks.DOUBLE_BAG_COFFEE = blocks.register("double_bag_coffee",
                () -> new BlockBag2(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_BROWN).strength(0.5F).sound(SoundType.WOOL)));
        ModBlocks.DOUBLE_BAG_COFFEE_RAW = blocks.register("double_bag_coffee_raw",
                () -> new BlockBag2(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_GREEN).strength(0.5F).sound(SoundType.WOOL)));
        ModBlocks.DOUBLE_BAG_COCOA = blocks.register("double_bag_cocoa",
                () -> new BlockBag2(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_BROWN).strength(0.5F).sound(SoundType.WOOL)));
        ModBlocks.DOUBLE_BAG_COCOA_POWDER = blocks.register("double_bag_cocoa_powder",
                () -> new BlockBag2(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_BROWN).strength(0.5F).sound(SoundType.WOOL)));
        ModBlocks.DOUBLE_BAG_FLOUR = blocks.register("double_bag_flour",
                () -> new BlockBag2(BlockBehaviour.Properties.of().mapColor(MapColor.SNOW).strength(0.5F).sound(SoundType.WOOL)));
        ModBlocks.DOUBLE_BAG_COFFEE_POWDER = blocks.register("double_bag_coffee_powder",
                () -> new BlockBag2(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_BROWN).strength(0.5F).sound(SoundType.WOOL)));
        ModBlocks.DOUBLE_BAG_SUGAR = blocks.register("double_bag_sugar",
                () -> new BlockBag2(BlockBehaviour.Properties.of().mapColor(MapColor.SNOW).strength(0.5F).sound(SoundType.WOOL)));
    }
}
