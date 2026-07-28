package net.langball.coffee.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.util.StringRepresentable;
import org.jetbrains.annotations.Nullable;

import java.util.EnumMap;
import java.util.Locale;
import java.util.Map;

public class BarCounterBlock extends Block {
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;

    /**
     * Phase 9 Fix5: a bar counter can form either an INNER_RIGHT corner
     * (neighbour on the player's right side) or an INNER_LEFT corner
     * (neighbour on the player's left side).  The legacy {@code bar_*}
     * _inner} model only fills the right-front quadrant of the block
     * footprint; mirror-rotating it 180° around Y produces the matching
     * left-front quadrant model used by INNER_LEFT variants.
     *
     * STRAIGHT is the legacy NORMAL alias — the two are kept distinct so
     * legacy state comparisons continue to work, but visually identical.
     */
    public enum Shape implements StringRepresentable {
        STRAIGHT, INNER_RIGHT, INNER_LEFT;

        @Override
        public String getSerializedName() {
            return name().toLowerCase(Locale.ROOT);
        }

        /** Legacy NORMAL is preserved as an alias for STRAIGHT. */
        public boolean isStraight() { return this == STRAIGHT; }
        public boolean isInner() { return this != STRAIGHT; }
    }

    public static final EnumProperty<Shape> SHAPE = EnumProperty.create("shape", Shape.class);

    /**
     * Per-shape collision shapes.  Each variant maps a {@link Shape} to
     * the voxel footprint it actually occupies — STRAIGHT uses the full
     * 1×1 footprint, INNER_RIGHT/INNER_LEFT use the L-shape corresponding
     * to the rendered model.  This prevents players from walking through
     * the empty back-quadrant of an inner corner.
     */
    private static final Map<Shape, Map<Direction, VoxelShape>> SHAPES = new EnumMap<>(Shape.class);
    static {
        EnumMap<Direction, VoxelShape> straight = new EnumMap<>(Direction.class);
        // STRAIGHT (1×1 footprint for all facings)
        straight.put(Direction.NORTH, Shapes.box(0, 0, 0.25, 1, 1, 1));
        straight.put(Direction.SOUTH, Shapes.box(0, 0, 0, 1, 1, 0.75));
        straight.put(Direction.WEST,  Shapes.box(0.25, 0, 0, 1, 1, 1));
        straight.put(Direction.EAST,  Shapes.box(0, 0, 0, 0.75, 1, 1));
        SHAPES.put(Shape.STRAIGHT, straight);

        // INNER_RIGHT: filled quadrant is right-front from the player.
        // For FACING=NORTH (player looks south, right = east): the inner
        // model occupies the east-front quadrant (x=4..16, z=4..16).
        // For other facings, rotate accordingly.
        EnumMap<Direction, VoxelShape> innerRight = new EnumMap<>(Direction.class);
        innerRight.put(Direction.NORTH, Shapes.box(0.25, 0, 0.25, 1, 1, 1));
        innerRight.put(Direction.SOUTH, Shapes.box(0, 0, 0, 0.75, 1, 0.75));
        innerRight.put(Direction.WEST,  Shapes.box(0.25, 0, 0.25, 1, 1, 0.75));
        innerRight.put(Direction.EAST,  Shapes.box(0, 0, 0.25, 0.75, 1, 1));
        SHAPES.put(Shape.INNER_RIGHT, innerRight);

        // INNER_LEFT: mirror of INNER_RIGHT — the corner is left-front.
        EnumMap<Direction, VoxelShape> innerLeft = new EnumMap<>(Direction.class);
        innerLeft.put(Direction.NORTH, Shapes.box(0, 0, 0.25, 0.75, 1, 1));
        innerLeft.put(Direction.SOUTH, Shapes.box(0.25, 0, 0, 1, 1, 0.75));
        innerLeft.put(Direction.WEST,  Shapes.box(0.25, 0, 0, 1, 1, 0.75));
        innerLeft.put(Direction.EAST,  Shapes.box(0, 0, 0, 0.75, 1, 1));
        SHAPES.put(Shape.INNER_LEFT, innerLeft);
    }

    private final boolean isStone;

    public BarCounterBlock(Properties props, boolean isStone) {
        super(props);
        this.isStone = isStone;
        registerDefaultState(stateDefinition.any()
                .setValue(FACING, Direction.NORTH)
                .setValue(SHAPE, Shape.STRAIGHT));
    }

    public boolean isStone() { return isStone; }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext ctx) {
        Shape shape = state.getValue(SHAPE);
        Direction facing = state.getValue(FACING);
        Map<Direction, VoxelShape> perFacing = SHAPES.get(shape);
        return perFacing.getOrDefault(facing, Shapes.block());
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, SHAPE);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        Direction facing = ctx.getHorizontalDirection().getOpposite();
        Shape shape = determineShape(ctx.getLevel(), ctx.getClickedPos(), facing, this);
        return defaultBlockState().setValue(FACING, facing).setValue(SHAPE, shape);
    }

    @Override
    public void neighborChanged(BlockState state, Level level, BlockPos pos, Block neighborBlock,
                                 BlockPos neighborPos, boolean isMoving) {
        super.neighborChanged(state, level, pos, neighborBlock, neighborPos, isMoving);
        Shape newShape = determineShape(level, pos, state.getValue(FACING), this);
        if (newShape != state.getValue(SHAPE)) {
            level.setBlock(pos, state.setValue(SHAPE, newShape), 3);
        }
    }

    /**
     * Phase 9 Fix5: resolve the counter's corner shape based on which
     * side has a matching neighbour.  The legacy implementation
     * collapsed both sides to a single INNER value, which produced the
     * wrong model rotation when the corner was on the player's left.
     */
    public static Shape determineShape(Level level, BlockPos pos, Direction facing, Block thisBlock) {
        // Right neighbour first — clockwise neighbour
        Direction right = facing.getClockWise();
        if (hasMatchingNeighbour(level, pos.relative(right), thisBlock, facing.getOpposite())) {
            return Shape.INNER_RIGHT;
        }
        // Left neighbour — counter-clockwise neighbour
        Direction left = facing.getCounterClockWise();
        if (hasMatchingNeighbour(level, pos.relative(left), thisBlock, facing.getOpposite())) {
            return Shape.INNER_LEFT;
        }
        return Shape.STRAIGHT;
    }

    private static boolean hasMatchingNeighbour(Level level, BlockPos neighbourPos, Block thisBlock,
                                                 Direction expectedFacing) {
        BlockState neighbour = level.getBlockState(neighbourPos);
        return neighbour.is(thisBlock) && neighbour.getValue(FACING) == expectedFacing;
    }
}
