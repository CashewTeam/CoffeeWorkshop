package net.langball.coffee.init;

import net.langball.coffee.block.*;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.registries.DeferredRegister;

/**
 * Plant blocks: coffee_tree, blueberry_bush, vanilla_crop.
 */
public class ModPlantBlocks {

    static void registerAll(DeferredRegister<Block> blocks) {
        ModBlocks.COFFEE_TREE = blocks.register("coffee_tree",
                () -> new BlockCoffeeTree(BlockBehaviour.Properties.of().mapColor(MapColor.PLANT).noCollission().randomTicks().instabreak().sound(SoundType.CROP)));
        ModBlocks.BLUEBERRY_BUSH = blocks.register("blueberry_bush",
                () -> new BlockBlueBerryBush(BlockBehaviour.Properties.of().mapColor(MapColor.PLANT).noCollission().randomTicks().instabreak().sound(SoundType.SWEET_BERRY_BUSH)));
        ModBlocks.VANILLA_CROP = blocks.register("vanilla_crop",
                () -> new BlockVanilla(BlockBehaviour.Properties.of().mapColor(MapColor.PLANT).noCollission().randomTicks().instabreak().sound(SoundType.CROP)));
    }
}
