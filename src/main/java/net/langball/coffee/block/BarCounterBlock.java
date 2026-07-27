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

import java.util.Locale;

public class BarCounterBlock extends Block {
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    public enum Shape implements StringRepresentable { NORMAL, INNER;

        @Override
        public String getSerializedName() {
            return name().toLowerCase(Locale.ROOT);
        }
    }
    public static final EnumProperty<Shape> SHAPE = EnumProperty.create("shape", Shape.class);

    private static final VoxelShape SHAPE_VOXEL = Shapes.box(0, 0, 0.25, 1, 1, 1);
    private final boolean isStone;

    public BarCounterBlock(Properties props, boolean isStone) {
        super(props);
        this.isStone = isStone;
        registerDefaultState(stateDefinition.any()
                .setValue(FACING, Direction.NORTH)
                .setValue(SHAPE, Shape.NORMAL));
    }

    public boolean isStone() { return isStone; }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext ctx) {
        return SHAPE_VOXEL;
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

    static Shape determineShape(Level level, BlockPos pos, Direction facing, Block thisBlock) {
        Direction right = facing.getClockWise();
        BlockPos rightPos = pos.relative(right);
        BlockState rightState = level.getBlockState(rightPos);
        if (rightState.is(thisBlock) && rightState.getValue(FACING) != facing) {
            return Shape.INNER;
        }
        return Shape.NORMAL;
    }
}
