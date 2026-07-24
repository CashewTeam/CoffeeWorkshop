package net.langball.coffee.world;

import net.langball.coffee.ModConfig;
import net.langball.coffee.init.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

public class BlueBerryFeature extends Feature<NoneFeatureConfiguration> {
    public BlueBerryFeature() {
        super(NoneFeatureConfiguration.CODEC);
    }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
        WorldGenLevel level = context.level();
        BlockPos pos = context.origin();
        RandomSource random = context.random();

        // Apply rarity from config (same logic as old 1.12.2 code: rarity / 8.0F)
        if (!(random.nextFloat() < ModConfig.BLUEBERRY_RARITY.get() / 8.0F)) {
            return false;
        }

        // Check the block below is suitable for planting
        BlockPos belowPos = pos.below();
        BlockState belowState = level.getBlockState(belowPos);
        if (!belowState.is(Blocks.GRASS_BLOCK) && !belowState.is(Blocks.DIRT) && !belowState.is(Blocks.FARMLAND)) {
            return false;
        }

        // Check the target position is empty or replaceable
        BlockState currentState = level.getBlockState(pos);
        if (!currentState.isAir() && !currentState.canBeReplaced()) {
            return false;
        }

        level.setBlock(pos, ModBlocks.BLUEBERRY_BUSH.get().defaultBlockState(), 2);
        return true;
    }
}
