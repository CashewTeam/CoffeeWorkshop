package net.langball.coffee.datagen;

import net.langball.coffee.CoffeeWork;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Collections;
import java.util.List;

@Mod.EventBusSubscriber(modid = CoffeeWork.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class DataGenerators {
    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        PackOutput output = generator.getPackOutput();
        ExistingFileHelper existingFileHelper = event.getExistingFileHelper();

        // Recipes (crafting, smelting, shapeless + machine recipes)
        generator.addProvider(event.includeServer(), new ModRecipeProvider(output));

        // Loot Tables
        generator.addProvider(event.includeServer(), new LootTableProvider(
                output,
                Collections.emptySet(),
                List.of(new LootTableProvider.SubProviderEntry(
                        ModBlockLootProvider::new,
                        LootContextParamSets.BLOCK))));

        // Block Tags + Item Tags
        ModBlockTagsProvider blockTags = new ModBlockTagsProvider(
                output, event.getLookupProvider(), existingFileHelper);
        generator.addProvider(event.includeServer(), blockTags);
        generator.addProvider(
                event.includeServer(),
                new ModItemTagsProvider(
                        output,
                        event.getLookupProvider(),
                        blockTags.contentsGetter(),
                        existingFileHelper));
    }
}
