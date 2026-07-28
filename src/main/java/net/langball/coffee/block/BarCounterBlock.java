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
     * Phase 9 Fix7: counters can form an INNER_RIGHT corner (neighbour
     * on the player's right side) or an INNER_LEFT corner (neighbour
     * on the player's left).  STRAIGHT is the no-neighbour case.
     *
     * The legacy {@code NORMAL} and {@code INNER} values are kept as
     * recognised enum constants so palettes saved by Fix4 and earlier
     * keep loading — both blockstate JSONs now define
     * {@code shape=normal} and {@code shape=inner} variants that map
     * to the same models as their canonical counterparts, and the
     * neighbour-update logic compares raw enum values so the block
     * gets rewritten to the canonical form on the next neighbour
     * change.
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

        /** Legacy aliases map onto the modern enum constants. */
        public Shape normalise() {
            return switch (this) {
                case NORMAL, STRAIGHT -> STRAIGHT;
                case INNER, INNER_RIGHT -> INNER_RIGHT;
                case INNER_LEFT -> INNER_LEFT;
            };
        }

        public boolean isStraight() { return this == STRAIGHT || this == NORMAL; }
        public boolean isInner() { return this == INNER_RIGHT || this == INNER_LEFT || this == INNER; }
    }

    public static final EnumProperty<Shape> SHAPE = EnumProperty.create("shape", Shape.class);

    /**
     * Per-shape collision.  The bar_stone_normal model has a
     * 14-pixel-tall body that occupies 12/16 of the depth on the
     * BACK side (the side away from the facing's "front"), and a
     * 2-pixel full-footprint countertop on top.  The inner model
     * adds a 1/4-notch L-shape on the back side.
     *
     * The collision must mirror the rendered geometry so the empty
     * 4-pixel front strip on the STRAIGHT counter is passable, and
     * the inner-counter back-quadrant is passable.
     */
    private static final Map<Shape, Map<Direction, VoxelShape>> SHAPES = new EnumMap<>(Shape.class);

    /**
     * 14/16 = top of body, bottom of countertop.  12/16 = back-side
     * extent of the STRAIGHT body in axis-aligned coords.
     */
    private static final double Y_TOP = 14.0 / 16.0;
    private static final double STRAIGHT_INSET = 4.0 / 16.0;

    /**
     * Build the STRAIGHT body for one FACING.  The body is 12 pixels
     * deep on the back side (the side opposite the player's facing).
     * The countertop is a full 1x1 plate at y=0.875..1.
     */
    private static VoxelShape straightShape(Direction facing) {
        VoxelShape top = Shapes.box(0, Y_TOP, 0, 1, 1, 1);
        VoxelShape body = switch (facing) {
            // Player looks at -z (north).  Back side = +z.  Body is
            // z=0.25..1 (the +z 12/16).
            case NORTH -> Shapes.box(0, 0, STRAIGHT_INSET, 1, Y_TOP, 1);
            // Player looks at +z (south).  Back side = -z.  Body is
            // z=0..0.75 (the -z 12/16).
            case SOUTH -> Shapes.box(0, 0, 0, 1, Y_TOP, 1 - STRAIGHT_INSET);
            // Player looks at +x (east).  Back side = -x.  Body is
            // x=0..0.75.
            case EAST -> Shapes.box(0, 0, 0, 1 - STRAIGHT_INSET, Y_TOP, 1);
            // Player looks at -x (west).  Back side = +x.  Body is
            // x=0.25..1.
            case WEST -> Shapes.box(STRAIGHT_INSET, 0, 0, 1, Y_TOP, 1);
            default -> Shapes.box(0, 0, 0, 1, Y_TOP, 1);
        };
        return Shapes.or(body, top);
    }

    static {
        EnumMap<Direction, VoxelShape> straight = new EnumMap<>(Direction.class);
        for (Direction d : Direction.Plane.HORIZONTAL) {
            straight.put(d, straightShape(d));
        }
        SHAPES.put(Shape.STRAIGHT, straight);
        SHAPES.put(Shape.NORMAL, straight); // legacy alias

        // INNER_RIGHT: the inner body occupies the +x +z quadrant in
        // the base orientation (model y=0).  The countertop is full 1x1.
        EnumMap<Direction, VoxelShape> innerRight = new EnumMap<>(Direction.class);
        // NORTH (model y=0): body +x +z
        innerRight.put(Direction.NORTH,
                Shapes.or(Shapes.box(0.25, 0, 0.25, 1, Y_TOP, 1),
                        Shapes.box(0, Y_TOP, 0, 1, 1, 1)));
        // SOUTH (model y=180): body -x -z
        innerRight.put(Direction.SOUTH,
                Shapes.or(Shapes.box(0, 0, 0, 0.75, Y_TOP, 0.75),
                        Shapes.box(0, Y_TOP, 0, 1, 1, 1)));
        // WEST (model y=270): body +x -z
        innerRight.put(Direction.WEST,
                Shapes.or(Shapes.box(0.25, 0, 0, 1, Y_TOP, 0.75),
                        Shapes.box(0, Y_TOP, 0, 1, 1, 1)));
        // EAST (model y=90): body -x +z
        innerRight.put(Direction.EAST,
                Shapes.or(Shapes.box(0, 0, 0.25, 0.75, Y_TOP, 1),
                        Shapes.box(0, Y_TOP, 0, 1, 1, 1)));
        SHAPES.put(Shape.INNER_RIGHT, innerRight);
        SHAPES.put(Shape.INNER, innerRight); // legacy alias pre-Fix5

        // INNER_LEFT: the inner body occupies the -x -z quadrant in
        // the base orientation (model y=180).  The countertop is full 1x1.
        EnumMap<Direction, VoxelShape> innerLeft = new EnumMap<>(Direction.class);
        // NORTH (model y=180): body -x -z
        innerLeft.put(Direction.NORTH,
                Shapes.or(Shapes.box(0, 0, 0, 0.75, Y_TOP, 0.75),
                        Shapes.box(0, Y_TOP, 0, 1, 1, 1)));
        // SOUTH (model y=0): body +x +z
        innerLeft.put(Direction.SOUTH,
                Shapes.or(Shapes.box(0.25, 0, 0.25, 1, Y_TOP, 1),
                        Shapes.box(0, Y_TOP, 0, 1, 1, 1)));
        // WEST (model y=90): body -x +z
        innerLeft.put(Direction.WEST,
                Shapes.or(Shapes.box(0, 0, 0.25, 0.75, Y_TOP, 1),
                        Shapes.box(0, Y_TOP, 0, 1, 1, 1)));
        // EAST (model y=270): body +x -z
        innerLeft.put(Direction.EAST,
                Shapes.or(Shapes.box(0.25, 0, 0, 1, Y_TOP, 0.75),
                        Shapes.box(0, Y_TOP, 0, 1, 1, 1)));
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
        // Phase 9 Fix7: compare raw enum values so legacy NORMAL/INNER
        // palettes are rewritten to canonical on the first neighbour
        // neighbour update.  Without the raw comparison, a stored
        // shape=normal would stay shape=normal forever because both
        // sides normalise to STRAIGHT.
        Shape canonical = determineShape(level, pos, state.getValue(FACING), this);
        if (state.getValue(SHAPE) != canonical) {
            level.setBlock(pos, state.setValue(SHAPE, canonical), 3);
        }
    }

    /**
     * Phase 9 Fix5 / Fix7: resolve the counter's corner shape based on
     * which side has a matching neighbour.  Returns only the canonical
     * three values (STRAIGHT, INNER_RIGHT, INNER_LEFT).  NORMAL and
     * INNER are written by the legacy un-alias path on neighbour
     * update, never by placement.
     */
    public static Shape determineShape(Level level, BlockPos pos, Direction facing, Block thisBlock) {
        Direction right = facing.getClockWise();
        if (hasMatchingNeighbour(level, pos.relative(right), thisBlock, facing.getOpposite())) {
            return Shape.INNER_RIGHT;
        }
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
