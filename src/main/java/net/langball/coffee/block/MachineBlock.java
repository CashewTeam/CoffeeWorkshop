package net.langball.coffee.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

/**
 * Common base class for all five processing machines
 * (Grinder / CoffeeMachine / IcecreamMachine / Roller / Oven).
 *
 * Each machine is registered under ONE block id (no more
 * <code>_off</code> / <code>_on</code> pair).  The lit state is encoded in
 * the {@link #LIT} {@link BooleanProperty} on the same BlockState — when
 * {@code LIT = true} the block emits light, plays furnace-style particles
 * and crackles.  Flipping the LIT bit is done via
 * {@code level.setBlock(pos, state.setValue(LIT, ...))} which preserves
 * the same {@link BlockEntity} across the transition, so an in-progress
 * recipe and its inventory are no longer destroyed by the
 * state-swap that the pre-PR-05 code relied on.
 *
 * Subclasses implement:
 *   - {@link #newBlockEntity(BlockPos, BlockState)}
 *   - {@link #getTicker(Level, BlockState, BlockEntityType)} (typically
 *     delegating to a static tick method on the matching BlockEntity)
 *
 * The base class carries BlockState boilerplate (FACING property + LIT +
 * default state), the 1x1x1 voxel shape, and the menu-opening
 * inventory-drop / comparator hooks used by Phase 2 PR-06.
 */
public abstract class MachineBlock extends BaseEntityBlock {
    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
    public static final BooleanProperty LIT = BlockStateProperties.LIT;

    private static final VoxelShape SHAPE = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D);

    protected MachineBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(
            this.stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(LIT, Boolean.FALSE)
        );
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, LIT);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    public BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    @Override
    public BlockState mirror(BlockState state, Mirror mirror) {
        return state.rotate(mirror.getRotation(state.getValue(FACING)));
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    public int getLightEmission(BlockState state, BlockGetter level, BlockPos pos) {
        return state.getValue(LIT) ? 13 : 0;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return createBlockEntity(pos, state);
    }

    /** Factory hook for the matching BlockEntity; subclass returns the right one. */
    @Nullable
    protected abstract BlockEntity createBlockEntity(BlockPos pos, BlockState state);

    /**
     * Subclasses opt into their own ticker implementation.  Returning the
     * {@code null} ticker means the block entity will not tick (used by
     * decorative machines if any are added later).
     */
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return createTicker(level, type);
    }

    /** Hook for subclasses to wire a ticker.  Default: no ticking. */
    @Nullable
    protected abstract <T extends BlockEntity> BlockEntityTicker<T> createTicker(Level level, BlockEntityType<T> type);
}
