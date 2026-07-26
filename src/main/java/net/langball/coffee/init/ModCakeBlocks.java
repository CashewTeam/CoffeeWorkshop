package net.langball.coffee.init;

import net.langball.coffee.block.BlockCakeBasic;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.registries.DeferredRegister;

import java.util.function.Supplier;

/**
 * Cake and dessert blocks: sponge cakes, large cakes, mousse, tiramisu.
 *
 * <p>Each cake can optionally provide a {@code sliceItem} supplier.
 * When a player right-clicks the cake with a Plate, they receive one
 * slice item and the BITES counter advances by 1.
 */
public class ModCakeBlocks {

    static void registerAll(DeferredRegister<Block> blocks) {
        // ── Sponge cakes (layer 1) ──
        ModBlocks.CAKE_SPONGE = blocks.register("cake_sponge",
                () -> new BlockCakeBasic(1, 2, 0.2F, BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_LIGHT_GRAY).strength(0.5F).sound(SoundType.WOOL),
                        () -> ModItems.CAKE_SPONGE_SLICE.get()));
        ModBlocks.CAKE_SPONGE_CHOCOLATE = blocks.register("cake_sponge_chocolate",
                () -> new BlockCakeBasic(1, 2, 0.2F, BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_BROWN).strength(0.5F).sound(SoundType.WOOL),
                        () -> ModItems.CAKE_SPONGE_CHOCOLATE_SLICES.get()));
        ModBlocks.CAKE_SPONGE_COFFEE = blocks.register("cake_sponge_coffee",
                () -> new BlockCakeBasic(1, 2, 0.2F, BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_BROWN).strength(0.5F).sound(SoundType.WOOL),
                        () -> ModItems.CAKE_SPONGE_COFFEE_SLICES.get()));
        ModBlocks.CAKE_SPONGE_PUMPKIN = blocks.register("cake_sponge_pumpkin",
                () -> new BlockCakeBasic(1, 2, 0.2F, BlockBehaviour.Properties.of().mapColor(MapColor.TERRACOTTA_ORANGE).strength(0.5F).sound(SoundType.WOOL),
                        () -> ModItems.CAKE_SPONGE_PUMPKIN_SLICES.get()));
        ModBlocks.CAKE_SPONGE_CARROT = blocks.register("cake_sponge_carrot",
                () -> new BlockCakeBasic(1, 2, 0.2F, BlockBehaviour.Properties.of().mapColor(MapColor.TERRACOTTA_ORANGE).strength(0.5F).sound(SoundType.WOOL),
                        () -> ModItems.CAKE_SPONGE_CARROT_SLICES.get()));
        ModBlocks.CAKE_SPONGE_REDVELVET = blocks.register("cake_sponge_redvelvet",
                () -> new BlockCakeBasic(1, 2, 0.2F, BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_RED).strength(0.5F).sound(SoundType.WOOL),
                        () -> ModItems.CAKE_SPONGE_REDVELVET_SLICES.get()));
        ModBlocks.CAKE_SPONGE_LEMON = blocks.register("cake_sponge_lemon",
                () -> new BlockCakeBasic(1, 2, 0.2F, BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_YELLOW).strength(0.5F).sound(SoundType.WOOL),
                        () -> ModItems.CAKE_SPONGE_LEMON_SLICES.get()));
        ModBlocks.CAKE_SPONGE_TEA = blocks.register("cake_sponge_tea",
                () -> new BlockCakeBasic(1, 2, 0.2F, BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_GREEN).strength(0.5F).sound(SoundType.WOOL),
                        () -> ModItems.CAKE_SPONGE_TEA_SLICES.get()));
        ModBlocks.CAKE_SPONGE_BERRY = blocks.register("cake_sponge_berry",
                () -> new BlockCakeBasic(1, 2, 0.2F, BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_PURPLE).strength(0.5F).sound(SoundType.WOOL),
                        () -> ModItems.CAKE_SPONGE_BERRY_SLICES.get()));

        // ── Large cakes (layer 3) ──
        ModBlocks.CAKE_COFFEE = blocks.register("cake_coffee",
                () -> new BlockCakeBasic(3, 4, 0.3F, BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_BROWN).strength(0.5F).sound(SoundType.WOOL),
                        () -> ModItems.CAKE_COFFEE_SLICES.get()));
        ModBlocks.CAKE_HARVEST = blocks.register("cake_harvest",
                () -> new BlockCakeBasic(3, 4, 0.3F, BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_ORANGE).strength(0.5F).sound(SoundType.WOOL),
                        () -> ModItems.CAKE_HARVEST_SLICES.get()));
        ModBlocks.CAKE_LEMON = blocks.register("cake_lemon",
                () -> new BlockCakeBasic(3, 4, 0.3F, BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_YELLOW).strength(0.5F).sound(SoundType.WOOL),
                        () -> ModItems.CAKE_LEMON_SLICES.get()));
        ModBlocks.CAKE_TEA = blocks.register("cake_tea",
                () -> new BlockCakeBasic(3, 4, 0.3F, BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_GREEN).strength(0.5F).sound(SoundType.WOOL),
                        () -> ModItems.CAKE_TEA_SLICES.get()));
        ModBlocks.CAKE_BERRY = blocks.register("cake_berry",
                () -> new BlockCakeBasic(3, 4, 0.3F, BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_PURPLE).strength(0.5F).sound(SoundType.WOOL),
                        () -> ModItems.CAKE_BERRY_SLICES.get()));
        ModBlocks.CAKE_CHEESE = blocks.register("cake_cheese",
                () -> new BlockCakeBasic(3, 4, 0.3F, BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_YELLOW).strength(0.5F).sound(SoundType.WOOL),
                        () -> ModItems.CAKE_CHEESE_SLICES.get()));
        ModBlocks.CAKE_SCHWARZWALD = blocks.register("cake_schwarzwald",
                () -> new BlockCakeBasic(3, 4, 0.3F, BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_BROWN).strength(0.5F).sound(SoundType.WOOL),
                        () -> ModItems.CAKE_SCHWARZWALD_SLICES.get()));
        ModBlocks.CAKE_REDVELVET = blocks.register("cake_redvelvet",
                () -> new BlockCakeBasic(3, 4, 0.3F, BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_RED).strength(0.5F).sound(SoundType.WOOL),
                        () -> ModItems.CAKE_REDVELVET_SLICES.get()));

        // ── Special cakes ──
        ModBlocks.TIRAMISU = blocks.register("tiramisu",
                () -> new BlockCakeBasic(5, 6, 0.5F, BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_BROWN).strength(0.5F).sound(SoundType.WOOL),
                        () -> ModItems.TIRAMISU_SLICE.get()));

        // ── Mousses ──
        ModBlocks.MOUSSE_BERRY = blocks.register("mousse_berry",
                () -> new BlockCakeBasic(3, 4, 0.3F, BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_PURPLE).strength(0.5F).sound(SoundType.WOOL),
                        () -> ModItems.CAKE_SLICES.get()));
        ModBlocks.MOUSSE_LEMON = blocks.register("mousse_lemon",
                () -> new BlockCakeBasic(3, 4, 0.3F, BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_YELLOW).strength(0.5F).sound(SoundType.WOOL),
                        () -> ModItems.CAKE_SLICES.get()));
        ModBlocks.MOUSSE_CHOCOLATE = blocks.register("mousse_chocolate",
                () -> new BlockCakeBasic(3, 4, 0.3F, BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_BROWN).strength(0.5F).sound(SoundType.WOOL),
                        () -> ModItems.CAKE_SLICES.get()));
        ModBlocks.MOUSSE_COFFEE = blocks.register("mousse_coffee",
                () -> new BlockCakeBasic(3, 4, 0.3F, BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_BROWN).strength(0.5F).sound(SoundType.WOOL),
                        () -> ModItems.CAKE_SLICES.get()));

        // Phase 5.4: cake_carrot
        ModBlocks.CAKE_CARROT = blocks.register("cake_carrot",
                () -> new BlockCakeBasic(3, 4, 0.3F, BlockBehaviour.Properties.of().mapColor(MapColor.TERRACOTTA_ORANGE).strength(0.5F).sound(SoundType.WOOL),
                        () -> ModItems.CAKE_SLICES.get()));
    }
}
