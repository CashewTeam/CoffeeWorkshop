package net.langball.coffee.datagen;

import net.langball.coffee.block.BlockCoffeeTree;
import net.langball.coffee.block.BlockVanilla;
import net.langball.coffee.init.ModBlocks;
import net.langball.coffee.init.ModItems;
import net.minecraft.advancements.critereon.StatePropertiesPredicate;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.predicates.LootItemBlockStatePropertyCondition;
import net.minecraftforge.registries.RegistryObject;

import java.util.Collections;
import java.util.stream.Collectors;

public class ModBlockLootProvider extends BlockLootSubProvider {

    public ModBlockLootProvider() {
        super(Collections.emptySet(), FeatureFlags.VANILLA_SET);
    }

    @Override
    protected void generate() {
        // === Machines (self-drop) ===
        dropSelf(ModBlocks.GRINDER.get());
        dropSelf(ModBlocks.COFFEE_MACHINE.get());
        dropSelf(ModBlocks.ICECREAM_MACHINE.get());
        dropSelf(ModBlocks.ROLLER.get());
        dropSelf(ModBlocks.OVEN.get());

        // === Plants ===
        // Coffee tree: at mature (age=3) drops coffee_seeds, otherwise drops the block itself
        add(ModBlocks.COFFEE_TREE.get(), createCropDrops(
                ModBlocks.COFFEE_TREE.get(),
                ModItems.COFFEE_SEEDS.get(),
                ModItems.COFFEE_SEEDS.get(),
                LootItemBlockStatePropertyCondition.hasBlockStateProperties(ModBlocks.COFFEE_TREE.get())
                        .setProperties(StatePropertiesPredicate.Builder.properties()
                                .hasProperty(BlockCoffeeTree.AGE, 3))));

        // Blueberry bush: self-drop (harvest is done via right-click)
        dropSelf(ModBlocks.BLUEBERRY_BUSH.get());

        // Vanilla crop: at mature (age=7) drops vanilla, otherwise vanilla_seeds
        add(ModBlocks.VANILLA_CROP.get(), createCropDrops(
                ModBlocks.VANILLA_CROP.get(),
                ModItems.VANILLA.get(),
                ModItems.VANILLA_SEEDS.get(),
                LootItemBlockStatePropertyCondition.hasBlockStateProperties(ModBlocks.VANILLA_CROP.get())
                        .setProperties(StatePropertiesPredicate.Builder.properties()
                                .hasProperty(BlockVanilla.AGE, 7))));

        // === Decor & Utility (self-drop) ===
        dropSelf(ModBlocks.PLATE.get());
        add(ModBlocks.COLD_BREW_POT.get(), noDrop()); // drops handled by BlockColdBrewPot#onRemove
        dropSelf(ModBlocks.SODA_ORE.get());
        dropSelf(ModBlocks.XMAS_TREE.get());
        dropSelf(ModBlocks.GINGER_HOUSE.get());

        // === Bags (self-drop) ===
        dropSelf(ModBlocks.BAG_COFFEE.get());
        dropSelf(ModBlocks.BAG_COFFEE_RAW.get());
        dropSelf(ModBlocks.BAG_COCOA.get());
        dropSelf(ModBlocks.BAG_COCOA_POWDER.get());
        dropSelf(ModBlocks.BAG_FLOUR.get());
        dropSelf(ModBlocks.BAG_COFFEE_POWDER.get());
        dropSelf(ModBlocks.BAG_SUGAR.get());

        // === Double Bags (self-drop) ===
        dropSelf(ModBlocks.DOUBLE_BAG_COFFEE.get());
        dropSelf(ModBlocks.DOUBLE_BAG_COFFEE_RAW.get());
        dropSelf(ModBlocks.DOUBLE_BAG_COCOA.get());
        dropSelf(ModBlocks.DOUBLE_BAG_COCOA_POWDER.get());
        dropSelf(ModBlocks.DOUBLE_BAG_FLOUR.get());
        dropSelf(ModBlocks.DOUBLE_BAG_COFFEE_POWDER.get());
        dropSelf(ModBlocks.DOUBLE_BAG_SUGAR.get());

        // === Cakes (self-drop) ===
        dropSelf(ModBlocks.CAKE_SPONGE.get());
        dropSelf(ModBlocks.CAKE_SPONGE_CHOCOLATE.get());
        dropSelf(ModBlocks.CAKE_SPONGE_COFFEE.get());
        dropSelf(ModBlocks.CAKE_SPONGE_PUMPKIN.get());
        dropSelf(ModBlocks.CAKE_SPONGE_CARROT.get());
        dropSelf(ModBlocks.CAKE_SPONGE_REDVELVET.get());
        dropSelf(ModBlocks.CAKE_SPONGE_LEMON.get());
        dropSelf(ModBlocks.CAKE_SPONGE_TEA.get());
        dropSelf(ModBlocks.CAKE_SPONGE_BERRY.get());
        dropSelf(ModBlocks.CAKE_COFFEE.get());
        dropSelf(ModBlocks.CAKE_HARVEST.get());
        dropSelf(ModBlocks.CAKE_LEMON.get());
        dropSelf(ModBlocks.CAKE_TEA.get());
        dropSelf(ModBlocks.CAKE_BERRY.get());
        dropSelf(ModBlocks.CAKE_CHEESE.get());
        dropSelf(ModBlocks.CAKE_SCHWARZWALD.get());
        dropSelf(ModBlocks.CAKE_REDVELVET.get());
        dropSelf(ModBlocks.TIRAMISU.get());
        dropSelf(ModBlocks.MOUSSE_BERRY.get());
        dropSelf(ModBlocks.MOUSSE_LEMON.get());
        dropSelf(ModBlocks.MOUSSE_CHOCOLATE.get());
        dropSelf(ModBlocks.MOUSSE_COFFEE.get());
    }

    @Override
    protected Iterable<Block> getKnownBlocks() {
        return ModBlocks.BLOCKS.getEntries().stream()
                .map(RegistryObject::get)
                .collect(Collectors.toList());
    }
}
