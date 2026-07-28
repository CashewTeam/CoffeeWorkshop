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
     * Phase 9 Fix6: the bar counter can form an INNER_RIGHT corner (neighbour
     * to the player's right side) or an INNER_LEFT corner (neighbour to the
     * player's left).  STRAIGHT is the no-neighbour case.  The legacy
     * {@code NORMAL} and {@code INNER} names are kept as legacy
     * serialization aliases so that worlds saved before Fix5 keep loading
     * without data loss — we expose them via {@link #SHAPE} through the
     * enum's normalised name lookup.
     */
    public enum Shape implements StringRepresentable {
        STRAIGHT("straight"),
        NORMAL("normal"),
        INNER_RIGHT("inner_right"),
        INNER("inner"),
        INNER_LEFT("inner_left");

        private final String serializedName;

        Shape(String serializedName) {
            this.serializedName = serializedName;
        }

        @Override
        public String getSerializedName() {
            return serializedName;
        }

        /** Normalise legacy aliases onto the modern enum constants. */
        public Shape normalise() {
            return switch (this) {
                case NORMAL, STRAIGHT -> STRAIGHT;
                case INNER, INNER_RIGHT -> INNER_RIGHT;
                case INNER_LEFT -> INNER_LEFT;
            };
        }

        /** Legacy NORMAL alias for STRAIGHT. */
        public boolean isStraight() { return this == STRAIGHT || this == NORMAL; }
        public boolean isInner() { return this == INNER_RIGHT || this == INNER_LEFT || this == INNER; }
    }

    public static final EnumProperty<Shape> SHAPE = EnumProperty.create("shape", Shape.class);

    /**
     * Per-shape collision.  The model has a 14-pixel-tall inner body in
     * one quadrant and a 2-pixel full-footprint countertop on top.  We
     * compose the L-shape via {@link Shapes#or} so the inner corner
     * matches the rendered silhouette: the empty back-quadrant of the
     * body is passable, but the top surface is blocked all the way
     * across.
     *
     * Coordinates are in 0..1 block units.  The body is anchored at
     * y=0..0.875 (14/16) and the top at y=0.875..1.
     */
    private static final Map<Shape, Map<Direction, VoxelShape>> SHAPES = new EnumMap<>(Shape.class);

    private static final double Y_TOP = 14.0 / 16.0; // top of body, bottom of countertop

    static {
        // STRAIGHT: model bar_stone_normal.json — full 1x1 footprint at all
        // facings.  The body is the full 14-pixel lower box and the top is
        // the full 2-pixel countertop.
        VoxelShape straightBody = Shapes.box(0, 0, 0, 1, Y_TOP, 1);
        VoxelShape straightTop = Shapes.box(0, Y_TOP, 0, 1, 1, 1);
        EnumMap<Direction, VoxelShape> straight = new EnumMap<>(Direction.class);
        VoxelShape straightShape = Shapes.or(straightBody, straightTop);
        for (Direction d : Direction.Plane.HORIZONTAL) {
            straight.put(d, straightShape);
        }
        SHAPES.put(Shape.STRAIGHT, straight);
        SHAPES.put(Shape.NORMAL, straight); // legacy alias

        // INNER_RIGHT: the inner body occupies the +x +z quadrant in the
        // base orientation (model y=0).  The countertop is full 1x1.
        // For each facing we recompute the rotated quadrant.
        EnumMap<Direction, VoxelShape> innerRight = new EnumMap<>(Direction.class);
        // NORTH (model y=0): body +x +z
        innerRight.put(Direction.NORTH,
                Shapes.or(Shapes.box(0.25, 0, 0.25, 1, Y_TOP, 1), straightTop));
        // SOUTH (model y=180): body -x -z
        innerRight.put(Direction.SOUTH,
                Shapes.or(Shapes.box(0, 0, 0, 0.75, Y_TOP, 0.75), straightTop));
        // WEST (model y=270): body +x -z
        innerRight.put(Direction.WEST,
                Shapes.or(Shapes.box(0.25, 0, 0, 1, Y_TOP, 0.75), straightTop));
        // EAST (model y=90): body -x +z
        innerRight.put(Direction.EAST,
                Shapes.or(Shapes.box(0, 0, 0.25, 0.75, Y_TOP, 1), straightTop));
        SHAPES.put(Shape.INNER_RIGHT, innerRight);
        SHAPES.put(Shape.INNER, innerRight); // legacy alias pre-Fix5

        // INNER_LEFT: the inner body occupies the -x -z quadrant in the
        // base orientation (model y=180).  The countertop is full 1x1.
        EnumMap<Direction, VoxelShape> innerLeft = new EnumMap<>(Direction.class);
        // NORTH (model y=180): body -x -z
        innerLeft.put(Direction.NORTH,
                Shapes.or(Shapes.box(0, 0, 0, 0.75, Y_TOP, 0.75), straightTop));
        // SOUTH (model y=0): body +x +z
        innerLeft.put(Direction.SOUTH,
                Shapes.or(Shapes.box(0.25, 0, 0.25, 1, Y_TOP, 1), straightTop));
        // WEST (model y=90): body -x +z
        innerLeft.put(Direction.WEST,
                Shapes.or(Shapes.box(0, 0, 0.25, 0.75, Y_TOP, 1), straightTop));
        // EAST (model y=270): body +x -z
        innerLeft.put(Direction.EAST,
                Shapes.or(Shapes.box(0.25, 0, 0, 1, Y_TOP, 0.75), straightTop));
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
        Shape shape = state.getValue(SHAPE).normalise();
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
        Shape current = state.getValue(SHAPE).normalise();
        Shape newShape = determineShape(level, pos, state.getValue(FACING), this);
        if (newShape != current) {
            level.setBlock(pos, state.setValue(SHAPE, newShape), 3);
        }
    }

    /**
     * Phase 9 Fix5 / Fix6: resolve the counter's corner shape based on
     * which side has a matching neighbour.  Only INNER_RIGHT and INNER_LEFT
     * are written by placement/neighbour updates — NORMAL and INNER are
     * recognised on load but never written, so the live enum only
     * contains the canonical three values.
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
