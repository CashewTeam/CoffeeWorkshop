package net.langball.coffee.block;

import net.langball.coffee.init.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class BlockColdBrewPot extends Block {
    public static final IntegerProperty FERM = IntegerProperty.create("ferm", 0, 8);
    protected static final VoxelShape SHAPE = Block.box(2.0D, 0.0D, 2.0D, 14.0D, 12.0D, 14.0D);
    private static final int MAX_FERMENT = 7; // ferm 7 = finished, ferm 8 = emptied

    public BlockColdBrewPot(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(FERM, 0));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FERM);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    @SuppressWarnings("deprecation")
    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        int ferm = state.getValue(FERM);
        if (ferm < MAX_FERMENT && random.nextInt(3) == 0) {
            level.setBlock(pos, state.setValue(FERM, ferm + 1), 2);
        }
    }

    @Override
    @SuppressWarnings("deprecation")
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player,
                                 InteractionHand hand, BlockHitResult hit) {
        int ferm = state.getValue(FERM);
        ItemStack held = player.getItemInHand(hand);

        if (ferm == MAX_FERMENT && held.is(Items.GLASS_BOTTLE)) {
            if (!level.isClientSide) {
                // Give the player a cold brew bottle
                ItemStack result = new ItemStack(ModItems.COLDBREW_BOTTLE.get());
                if (held.getCount() > 1) {
                    held.shrink(1);
                    if (!player.getInventory().add(result)) {
                        player.drop(result, false);
                    }
                } else {
                    player.setItemInHand(hand, result);
                }
                level.setBlock(pos, state.setValue(FERM, 8), 2);
                level.playSound(null, pos, SoundEvents.BOTTLE_FILL, SoundSource.BLOCKS, 1.0F, 1.0F);
            }
            return InteractionResult.sidedSuccess(level.isClientSide);
        }

        return super.use(state, level, pos, player, hand, hit);
    }

    @Override
    @SuppressWarnings("deprecation")
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        if (!state.is(newState.getBlock())) {
            int ferm = state.getValue(FERM);
            if (ferm == 8) {
                // Emptied state: drop the empty pot
                popResource(level, pos, new ItemStack(ModItems.EMPTY_COLDBREW_POT.get()));
            } else {
                // Otherwise drop the full pot
                popResource(level, pos, new ItemStack(ModItems.COLD_BREW_POT.get()));
            }
        }
        super.onRemove(state, level, pos, newState, isMoving);
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        int ferm = state.getValue(FERM);
        if (ferm < MAX_FERMENT && ferm > 0 && random.nextInt(5) == 0) {
            double d4 = random.nextBoolean() ? 0.8D : -0.8D;
            double d0 = (double) pos.getX() + 0.5D + random.nextDouble() * d4;
            double d1 = (double) pos.getY() + random.nextDouble();
            double d2 = (double) pos.getZ() + 0.5D + random.nextDouble() * d4;
            level.addParticle(ParticleTypes.DRIPPING_WATER, d0, d1, d2, 0.0D, 0.0D, 0.0D);
        }
    }

    @Override
    public ItemStack getCloneItemStack(BlockGetter level, BlockPos pos, BlockState state) {
        return new ItemStack(
                state.getValue(FERM) == 8
                        ? ModItems.EMPTY_COLDBREW_POT.get()
                        : ModItems.COLD_BREW_POT.get());
    }
}
