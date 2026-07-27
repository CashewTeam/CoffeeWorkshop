package net.langball.coffee.init;

import net.langball.coffee.block.*;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraftforge.registries.DeferredRegister;

/**
 * Decor/utility blocks: plate, coldbrew_pot, soda_ore, xmas_tree, ginger_house.
 */
public class ModDecorBlocks {

    static void registerAll(DeferredRegister<Block> blocks) {
        ModBlocks.PLATE = blocks.register("plate",
                () -> new BlockPlate(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_LIGHT_GRAY).strength(0.5F).sound(SoundType.STONE).noOcclusion()));
        ModBlocks.COLD_BREW_POT = blocks.register("coldbrew_pot",
                () -> new BlockColdBrewPot(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(1.0F).sound(SoundType.LANTERN).noOcclusion()));
        ModBlocks.SODA_ORE = blocks.register("soda_ore",
                () -> new BlockOreSoda(BlockBehaviour.Properties.of().mapColor(MapColor.STONE).strength(3.0F, 3.0F).requiresCorrectToolForDrops()));
        ModBlocks.XMAS_TREE = blocks.register("xmas_tree",
                () -> new BlockXmasTree(BlockBehaviour.Properties.of().mapColor(MapColor.WOOD).strength(0.5F).sound(SoundType.WOOD).noOcclusion()));
        ModBlocks.GINGER_HOUSE = blocks.register("ginger_house",
                () -> new BlockGingerHouse(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_BROWN).strength(0.5F).sound(SoundType.WOOD).noOcclusion()));
        ModBlocks.DRINK_DISPLAY = blocks.register("drink_display",
                () -> new DrinkDisplayBlock(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_LIGHT_GRAY).strength(0.5F).sound(SoundType.STONE).noOcclusion().pushReaction(PushReaction.DESTROY)));
        ModBlocks.MOKA_POT = blocks.register("moka_pot",
                () -> new MokaPotBlock(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(2.0F).sound(SoundType.LANTERN).noOcclusion()));
        ModBlocks.TURKISH_COFFEE_POT = blocks.register("turkish_coffee_pot",
                () -> new TurkishCoffeePotBlock(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(2.0F).sound(SoundType.LANTERN).noOcclusion()));
        ModBlocks.COFFEE_POT = blocks.register("coffee_pot",
                () -> new CoffeePotBlock(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(2.0F).sound(SoundType.LANTERN).noOcclusion()));
    }
}
