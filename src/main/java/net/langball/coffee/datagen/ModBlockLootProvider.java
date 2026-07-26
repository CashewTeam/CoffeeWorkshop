package net.langball.coffee.datagen;

import net.langball.coffee.block.BlockCoffeeTree;
import net.langball.coffee.block.BlockVanilla;
import net.langball.coffee.init.ModBlocks;
import net.langball.coffee.init.ModItems;
import net.minecraft.advancements.critereon.StatePropertiesPredicate;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.ApplyBonusCount;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemBlockStatePropertyCondition;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
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
        // Coffee tree: Silk Touch or Shears → block itself (mutually exclusive).
        // No Silk Touch / no Shears: mature (age=3) drops 1-3 coffee_bean_raw + 0-1 seeds;
        // immature drops 0-1 coffee_seeds.
        // The regular pools carry inverse conditions so they do NOT stack with tool drops.
        // Silk Touch + Shears combination (enchanted shears): only one block drops.
        add(ModBlocks.COFFEE_TREE.get(), LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .when(HAS_SILK_TOUCH)
                        .add(LootItem.lootTableItem(ModBlocks.COFFEE_TREE.get())))
                .withPool(LootPool.lootPool()
                        .when(HAS_SHEARS)
                        .when(HAS_SILK_TOUCH.invert())
                        .add(LootItem.lootTableItem(ModBlocks.COFFEE_TREE.get())))
                .withPool(LootPool.lootPool()
                        .when(HAS_SILK_TOUCH.invert())
                        .when(HAS_SHEARS.invert())
                        .when(LootItemBlockStatePropertyCondition.hasBlockStateProperties(ModBlocks.COFFEE_TREE.get())
                                .setProperties(StatePropertiesPredicate.Builder.properties()
                                        .hasProperty(BlockCoffeeTree.AGE, 3)))
                        .add(LootItem.lootTableItem(ModItems.COFFEE_BEAN_RAW.get())
                                .apply(SetItemCountFunction.setCount(UniformGenerator.between(1.0F, 3.0F)))))
                .withPool(LootPool.lootPool()
                        .when(HAS_SILK_TOUCH.invert())
                        .when(HAS_SHEARS.invert())
                        .add(LootItem.lootTableItem(ModItems.COFFEE_SEEDS.get())
                                .apply(SetItemCountFunction.setCount(UniformGenerator.between(0.0F, 1.0F))))));

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
        // Soda ore: Silk Touch → ore block; otherwise 4-8 soda with Fortune + explosion decay
        add(ModBlocks.SODA_ORE.get(), applyExplosionDecay(ModBlocks.SODA_ORE.get(),
                createSilkTouchDispatchTable(
                        ModBlocks.SODA_ORE.get(),
                        LootItem.lootTableItem(ModItems.SODA.get())
                                .apply(SetItemCountFunction.setCount(UniformGenerator.between(4.0F, 8.0F)))
                                .apply(ApplyBonusCount.addOreBonusCount(Enchantments.BLOCK_FORTUNE)))));
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

        // === Cakes & Desserts (no drop to prevent "eat half, break, get whole cake" exploit) ===
        add(ModBlocks.CAKE_SPONGE.get(), noDrop());
        add(ModBlocks.CAKE_SPONGE_CHOCOLATE.get(), noDrop());
        add(ModBlocks.CAKE_SPONGE_COFFEE.get(), noDrop());
        add(ModBlocks.CAKE_SPONGE_PUMPKIN.get(), noDrop());
        add(ModBlocks.CAKE_SPONGE_CARROT.get(), noDrop());
        add(ModBlocks.CAKE_SPONGE_REDVELVET.get(), noDrop());
        add(ModBlocks.CAKE_SPONGE_LEMON.get(), noDrop());
        add(ModBlocks.CAKE_SPONGE_TEA.get(), noDrop());
        add(ModBlocks.CAKE_SPONGE_BERRY.get(), noDrop());
        add(ModBlocks.CAKE_COFFEE.get(), noDrop());
        add(ModBlocks.CAKE_HARVEST.get(), noDrop());
        add(ModBlocks.CAKE_LEMON.get(), noDrop());
        add(ModBlocks.CAKE_TEA.get(), noDrop());
        add(ModBlocks.CAKE_BERRY.get(), noDrop());
        add(ModBlocks.CAKE_CHEESE.get(), noDrop());
        add(ModBlocks.CAKE_SCHWARZWALD.get(), noDrop());
        add(ModBlocks.CAKE_REDVELVET.get(), noDrop());
        add(ModBlocks.TIRAMISU.get(), noDrop());
        add(ModBlocks.MOUSSE_BERRY.get(), noDrop());
        add(ModBlocks.MOUSSE_LEMON.get(), noDrop());
        add(ModBlocks.MOUSSE_CHOCOLATE.get(), noDrop());
        add(ModBlocks.MOUSSE_COFFEE.get(), noDrop());
        add(ModBlocks.CAKE_CARROT.get(), noDrop());
    }

    @Override
    protected Iterable<Block> getKnownBlocks() {
        return ModBlocks.BLOCKS.getEntries().stream()
                .map(RegistryObject::get)
                .collect(Collectors.toList());
    }
}
