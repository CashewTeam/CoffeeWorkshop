package net.langball.coffee.item;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class SeedCoffee extends Item {
    private final Block cropBlock;

    public SeedCoffee(Block cropBlock, Properties properties) {
        super(properties);
        this.cropBlock = cropBlock;
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        BlockState state = level.getBlockState(pos);

        if (state.is(net.minecraft.world.level.block.Blocks.FARMLAND)) {
            BlockPos above = pos.above();
            if (level.isEmptyBlock(above)) {
                level.setBlock(above, cropBlock.defaultBlockState(), 2);
                ItemStack stack = context.getItemInHand();
                stack.shrink(1);
                return InteractionResult.sidedSuccess(level.isClientSide);
            }
        }
        return InteractionResult.PASS;
    }
}
