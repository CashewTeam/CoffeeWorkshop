package net.langball.coffee.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class BlockCakeBasic extends Block {
    private final int layer;
    protected static final VoxelShape SHAPE_SINGLE = Block.box(1.0D, 0.0D, 1.0D, 15.0D, 8.0D, 15.0D);
    protected static final VoxelShape SHAPE_DOUBLE = Block.box(1.0D, 0.0D, 1.0D, 15.0D, 16.0D, 15.0D);

    public BlockCakeBasic(int layer, BlockBehaviour.Properties properties) {
        super(properties);
        this.layer = layer;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return layer <= 1 ? SHAPE_SINGLE : SHAPE_DOUBLE;
    }

    public int getLayer() {
        return layer;
    }
}
