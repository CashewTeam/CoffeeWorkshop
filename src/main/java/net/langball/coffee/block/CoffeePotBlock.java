package net.langball.coffee.block;

import net.langball.coffee.block.entity.CoffeePotBlockEntity;
import net.langball.coffee.init.ModItems;
import net.langball.coffee.item.DrinkCoffee;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.shapes.Shapes;
import org.jetbrains.annotations.Nullable;

public class CoffeePotBlock extends BaseEntityBlock {
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final IntegerProperty LEVEL = IntegerProperty.create("level", 0, 4);

    private static final VoxelShape SHAPE = Shapes.box(0.125, 0, 0.125, 0.875, 0.75, 0.875);

    public CoffeePotBlock(Properties props) {
        super(props);
        registerDefaultState(stateDefinition.any()
                .setValue(FACING, Direction.NORTH)
                .setValue(LEVEL, 0));
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext ctx) {
        return SHAPE;
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, LEVEL);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        return defaultBlockState().setValue(FACING, ctx.getHorizontalDirection().getOpposite());
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player,
                                  InteractionHand hand, BlockHitResult hit) {
        ItemStack held = player.getItemInHand(hand);
        BlockEntity be = level.getBlockEntity(pos);
        if (!(be instanceof CoffeePotBlockEntity pot)) return InteractionResult.PASS;

        if (level.isClientSide) return InteractionResult.CONSUME;

        boolean sneaking = player.isShiftKeyDown();

        if (sneaking && held.isEmpty()) {
            level.removeBlock(pos, false);
            return InteractionResult.CONSUME;
        }

        if (held.getItem() == ModItems.CUP.get() && pot.isReady()) {
            ItemStack drink = pot.pourServing();
            if (!drink.isEmpty()) {
                if (!player.getAbilities().instabuild) held.shrink(1);
                if (!player.addItem(drink)) {
                    player.drop(drink, false);
                }
                level.playSound(null, pos, SoundEvents.BOTTLE_FILL, SoundSource.BLOCKS, 0.5f, 1.0f);
                int newLevel = pot.getFillLevel();
                level.setBlock(pos, state.setValue(LEVEL, newLevel), 3);
                return InteractionResult.CONSUME;
            }
        }

        if (held.getItem() instanceof DrinkCoffee && !pot.isFull()) {
            int available = DrinkCoffee.getRemainingCups(held);
            int space = pot.getCapacity() - pot.getServings();
            int moved = Math.min(available, space);
            if (moved <= 0) return InteractionResult.PASS;

            if (pot.isEmpty()) {
                pot.setStoredDrink(held.copy());
                pot.getStoredDrink().setCount(1);
                DrinkCoffee.setRemainingCups(pot.getStoredDrink(), 1);
            } else if (!pot.canAccept(held)) {
                return InteractionResult.PASS;
            }
            pot.setServings(pot.getServings() + moved);

            if (!player.getAbilities().instabuild) {
                DrinkCoffee.setRemainingCups(held, available - moved);
                if (DrinkCoffee.getRemainingCups(held) <= 0) {
                    ItemStack container = ItemStack.EMPTY;
                    if (held.getItem() instanceof DrinkCoffee dc && dc.getEmptyCupItem() != null) {
                        container = new ItemStack(dc.getEmptyCupItem());
                    }
                    player.setItemInHand(hand, container);
                }
            }

            level.playSound(null, pos, SoundEvents.BOTTLE_FILL, SoundSource.BLOCKS, 0.5f, 1.0f);
            int newLevel = pot.getFillLevel();
            level.setBlock(pos, state.setValue(LEVEL, newLevel), 3);
            return InteractionResult.CONSUME;
        }

        return InteractionResult.PASS;
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        if (!state.is(newState.getBlock())) {
            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof CoffeePotBlockEntity pot) {
                ItemStack drop = new ItemStack(ModItems.COFFEE_POT_ITEM.get());
                CompoundTag beTag = pot.saveForItem();
                if (!beTag.isEmpty()) {
                    drop.getOrCreateTag().put("BlockEntityTag", beTag);
                }
                net.minecraft.world.Containers.dropItemStack(level, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, drop);
            }
            super.onRemove(state, level, pos, newState, isMoving);
        }
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new CoffeePotBlockEntity(pos, state);
    }
}
