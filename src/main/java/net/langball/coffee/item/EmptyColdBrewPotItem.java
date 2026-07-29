package net.langball.coffee.item;

import net.langball.coffee.block.BlockColdBrewPot;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public final class EmptyColdBrewPotItem extends BlockItem {

    public EmptyColdBrewPotItem(Block block, Properties properties) {
        super(block, properties);
    }

    @Override
    @Nullable
    protected BlockState getPlacementState(BlockPlaceContext context) {
        BlockState state = super.getPlacementState(context);
        if (state == null) {
            return null;
        }
        return state.setValue(BlockColdBrewPot.FERM, 8);
    }
}
