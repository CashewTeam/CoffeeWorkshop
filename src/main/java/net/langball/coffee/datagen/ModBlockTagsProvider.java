package net.langball.coffee.datagen;

import net.langball.coffee.CoffeeWork;
import net.langball.coffee.init.ModBlocks;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.minecraftforge.common.data.BlockTagsProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class ModBlockTagsProvider extends BlockTagsProvider {

    public ModBlockTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, CoffeeWork.MODID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        // Machines: mineable with pickaxe
        tag(BlockTags.MINEABLE_WITH_PICKAXE)
                .add(ModBlocks.GRINDER.get())
                .add(ModBlocks.COFFEE_MACHINE.get())
                .add(ModBlocks.ICECREAM_MACHINE.get())
                .add(ModBlocks.ROLLER.get())
                .add(ModBlocks.OVEN.get())
                .add(ModBlocks.COLD_BREW_POT.get())
                .add(ModBlocks.SODA_ORE.get())
                .add(ModBlocks.PLATE.get());

        // Ores: requires iron or stone tier
        tag(BlockTags.NEEDS_IRON_TOOL)
                .add(ModBlocks.SODA_ORE.get());

        // Plants: no tool required (crops handled by VanillaCrop mechanism)
        // Cakes and bags: no tool required (default)
    }
}
