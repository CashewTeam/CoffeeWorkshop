package net.langball.coffee.datagen;

import net.langball.coffee.CoffeeWork;
import net.langball.coffee.init.ModItemTags;
import net.langball.coffee.init.ModItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.data.ExistingFileHelper;

import java.util.concurrent.CompletableFuture;

public class ModItemTagsProvider extends ItemTagsProvider {

    public ModItemTagsProvider(
            PackOutput output,
            CompletableFuture<HolderLookup.Provider> lookup,
            CompletableFuture<TagLookup<Block>> blockTags,
            ExistingFileHelper existingFileHelper) {
        super(output, lookup, blockTags, CoffeeWork.MODID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        // Mod-specific tags
        tag(ModItemTags.BERRIES)
                .add(Items.SWEET_BERRIES)
                .add(Items.GLOW_BERRIES)
                .add(ModItems.BLUEBERRY.get());

        tag(ModItemTags.FRUITS)
                .addTag(ModItemTags.BERRIES)
                .add(Items.APPLE)
                .add(Items.MELON_SLICE)
                .add(ModItems.LEMON.get());

        // Forge common tags — register mod seeds so they work with
        // forge:seeds-based recipes (e.g. mooncake_raw).
        tag(Tags.Items.SEEDS)
                .add(ModItems.COFFEE_SEEDS.get())
                .add(ModItems.VANILLA_SEEDS.get());
    }
}
