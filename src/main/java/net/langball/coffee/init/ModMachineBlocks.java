package net.langball.coffee.init;

import net.langball.coffee.block.*;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.registries.DeferredRegister;

/**
 * Machine blocks: grinder, coffee_machine, icecream_machine, roller, oven.
 */
public class ModMachineBlocks {

    static void registerAll(DeferredRegister<Block> blocks) {
        ModBlocks.GRINDER = blocks.register("grinder",
                () -> new BlockGrinder(BlockBehaviour.Properties.of().mapColor(MapColor.STONE).strength(5.0F).requiresCorrectToolForDrops().noOcclusion()));
        ModBlocks.COFFEE_MACHINE = blocks.register("coffee_machine",
                () -> new BlockCoffeeMachine(BlockBehaviour.Properties.of().mapColor(MapColor.STONE).strength(5.0F).requiresCorrectToolForDrops().noOcclusion()));
        ModBlocks.ICECREAM_MACHINE = blocks.register("icecream_machine",
                () -> new BlockIcecreamMachine(BlockBehaviour.Properties.of().mapColor(MapColor.STONE).strength(5.0F).requiresCorrectToolForDrops().noOcclusion()));
        ModBlocks.ROLLER = blocks.register("roller",
                () -> new BlockRoller(BlockBehaviour.Properties.of().mapColor(MapColor.STONE).strength(5.0F).requiresCorrectToolForDrops().noOcclusion()));
        ModBlocks.OVEN = blocks.register("oven",
                () -> new BlockClayOven(BlockBehaviour.Properties.of().mapColor(MapColor.STONE).strength(5.0F).requiresCorrectToolForDrops().noOcclusion()));
    }
}
