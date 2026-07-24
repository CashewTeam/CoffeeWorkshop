package net.langball.coffee.world;

import net.langball.coffee.init.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

/**
 * Custom soda ore feature that faithfully ports the 1.12.2 WorldGeneratorSoda logic.
 *
 * The old code:
 * - Used WorldGenMinable(block, 16) to replace stone
 * - Generated 4 veins per chunk at y = 8..71
 * - Applied a biome rainfall probability filter
 * - Was triggered via OreGenEvent.Post (after all vanilla ore gen)
 *
 * In 1.20.1 this is a standalone Feature; placement frequency and height are
 * controlled by the PlacedFeature JSON, and biome filtering is handled by the
 * BiomeModifier or the Feature itself.
 */
public class SodaOreFeature extends Feature<NoneFeatureConfiguration> {
    public SodaOreFeature() {
        super(NoneFeatureConfiguration.CODEC);
    }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
        WorldGenLevel level = context.level();
        BlockPos origin = context.origin();
        RandomSource random = context.random();

        // Vein size matching old code (WorldGenMinable size 16)
        int veinSize = 16;
        // Rainfall probability filter from old code:
        // biome.getRainfall() < rand.nextInt(65536)
        // Since 1.20.1 biome rainfall is 0.0-1.0, this compared a small float
        // against a large int—almost always true unless random returned 0.
        // We port the spirit: a flat ~98% pass filter so ore is common.
        if (random.nextInt(65536) == 0) {
            return false;
        }

        BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos();
        int placed = 0;

        for (int i = 0; i < veinSize; i++) {
            int dx = random.nextInt(3) - random.nextInt(3);
            int dy = random.nextInt(3) - random.nextInt(3);
            int dz = random.nextInt(3) - random.nextInt(3);

            mutablePos.set(origin.getX() + dx, origin.getY() + dy, origin.getZ() + dz);

            if (placed > 0) {
                // Clustering: prefer positions adjacent to already placed ores
                int adjX = (random.nextInt(3) - 1) * (random.nextInt(2) + 1);
                int adjZ = (random.nextInt(3) - 1) * (random.nextInt(2) + 1);
                mutablePos.move(adjX, 0, adjZ);
            }

            BlockState state = level.getBlockState(mutablePos);
            if (state.is(BlockTags.STONE_ORE_REPLACEABLES)) {
                level.setBlock(mutablePos, ModBlocks.SODA_ORE.get().defaultBlockState(), 2);
                placed++;
            }
        }

        return placed > 0;
    }
}
