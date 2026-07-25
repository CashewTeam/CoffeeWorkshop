package net.langball.coffee.block;

import net.langball.coffee.block.entity.IcecreamMachineBlockEntity;
import net.langball.coffee.init.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class BlockIcecreamMachine extends MachineBlock {
    public BlockIcecreamMachine(Properties properties) {
        super(properties);
    }

    @Nullable
    @Override
    protected BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new IcecreamMachineBlockEntity(pos, state);
    }

    @Nullable
    @Override
    protected <T extends BlockEntity> BlockEntityTicker<T> createTicker(Level level, BlockEntityType<T> type) {
        return createTickerHelper(type, ModBlockEntities.ICECREAM_MACHINE.get(),
                (l, p, s, be) -> be.tick(l, p, s));
    }
}
