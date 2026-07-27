package net.langball.coffee.init;

import net.langball.coffee.CoffeeWork;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

/**
 * Central block registry hub.
 *
 * All block registrations are delegated to categorized sub-files
 * ({@link ModMachineBlocks}, {@link ModPlantBlocks}, {@link ModDecorBlocks},
 * {@link ModBagBlocks}, {@link ModCakeBlocks}) via their {@code registerAll()} methods.
 */
public class ModBlocks {
    public static final DeferredRegister<Block> BLOCKS =
            DeferredRegister.create(ForgeRegistries.BLOCKS, CoffeeWork.MODID);

    // ========== Machines ==========
    public static RegistryObject<Block> GRINDER;
    public static RegistryObject<Block> COFFEE_MACHINE;
    public static RegistryObject<Block> ICECREAM_MACHINE;
    public static RegistryObject<Block> ROLLER;
    public static RegistryObject<Block> OVEN;

    // ========== Plants ==========
    public static RegistryObject<Block> COFFEE_TREE;
    public static RegistryObject<Block> BLUEBERRY_BUSH;
    public static RegistryObject<Block> VANILLA_CROP;

    // ========== Decor & Utility ==========
    public static RegistryObject<Block> PLATE;
    public static RegistryObject<Block> COLD_BREW_POT;
    public static RegistryObject<Block> SODA_ORE;
    public static RegistryObject<Block> XMAS_TREE;
    public static RegistryObject<Block> GINGER_HOUSE;

    // ========== Bags (single) ==========
    public static RegistryObject<Block> BAG_COFFEE;
    public static RegistryObject<Block> BAG_COFFEE_RAW;
    public static RegistryObject<Block> BAG_COCOA;
    public static RegistryObject<Block> BAG_COCOA_POWDER;
    public static RegistryObject<Block> BAG_FLOUR;
    public static RegistryObject<Block> BAG_COFFEE_POWDER;
    public static RegistryObject<Block> BAG_SUGAR;

    // ========== Double Bags ==========
    public static RegistryObject<Block> DOUBLE_BAG_COFFEE;
    public static RegistryObject<Block> DOUBLE_BAG_COFFEE_RAW;
    public static RegistryObject<Block> DOUBLE_BAG_COCOA;
    public static RegistryObject<Block> DOUBLE_BAG_COCOA_POWDER;
    public static RegistryObject<Block> DOUBLE_BAG_FLOUR;
    public static RegistryObject<Block> DOUBLE_BAG_COFFEE_POWDER;
    public static RegistryObject<Block> DOUBLE_BAG_SUGAR;

    // ========== Cakes (sponge, layer 1) ==========
    public static RegistryObject<Block> CAKE_SPONGE;
    public static RegistryObject<Block> CAKE_SPONGE_CHOCOLATE;
    public static RegistryObject<Block> CAKE_SPONGE_COFFEE;
    public static RegistryObject<Block> CAKE_SPONGE_PUMPKIN;
    public static RegistryObject<Block> CAKE_SPONGE_CARROT;
    public static RegistryObject<Block> CAKE_SPONGE_REDVELVET;
    public static RegistryObject<Block> CAKE_SPONGE_LEMON;
    public static RegistryObject<Block> CAKE_SPONGE_TEA;
    public static RegistryObject<Block> CAKE_SPONGE_BERRY;

    // ========== Cakes (large, layer 3) ==========
    public static RegistryObject<Block> CAKE_COFFEE;
    public static RegistryObject<Block> CAKE_HARVEST;
    public static RegistryObject<Block> CAKE_LEMON;
    public static RegistryObject<Block> CAKE_TEA;
    public static RegistryObject<Block> CAKE_BERRY;
    public static RegistryObject<Block> CAKE_CHEESE;
    public static RegistryObject<Block> CAKE_SCHWARZWALD;
    public static RegistryObject<Block> CAKE_REDVELVET;

    // ========== Special cakes ==========
    public static RegistryObject<Block> TIRAMISU;

    // ========== Mousses ==========
    public static RegistryObject<Block> MOUSSE_BERRY;
    public static RegistryObject<Block> MOUSSE_LEMON;
    public static RegistryObject<Block> MOUSSE_CHOCOLATE;
    public static RegistryObject<Block> MOUSSE_COFFEE;

    // Phase 5.4
    public static RegistryObject<Block> CAKE_CARROT;

    // Phase 8: Drink display system
    public static RegistryObject<Block> DRINK_DISPLAY;

    // ========== Registration bootstrap ==========
    static {
        ModMachineBlocks.registerAll(BLOCKS);
        ModPlantBlocks.registerAll(BLOCKS);
        ModDecorBlocks.registerAll(BLOCKS);
        ModBagBlocks.registerAll(BLOCKS);
        ModCakeBlocks.registerAll(BLOCKS);
    }
}
