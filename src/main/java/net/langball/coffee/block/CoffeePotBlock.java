package net.langball.coffee.block;

import net.langball.coffee.block.entity.CoffeePotBlockEntity;
import net.langball.coffee.init.ModItems;
import net.langball.coffee.item.DrinkCoffee;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
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
    private static final TagKey<Item> COFFEE_POT_DRINKS =
            ItemTags.create(new ResourceLocation("coffeework", "coffee_pot_drinks"));

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
            // Phase 9 Fix4 P0-2: ensure pickup writes to a single stack
            // before mutating BlockEntityTag.  Items are stacksTo(1).
            if (held.getCount() != 0) return InteractionResult.PASS;
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
            if (!held.is(COFFEE_POT_DRINKS)) return InteractionResult.PASS;
            DrinkCoffee dc = (DrinkCoffee) held.getItem();
            dc.ensureCupData(held);
            int available = dc.hasMultiCup() ? DrinkCoffee.getRemainingCups(held) : 1;
            if (available <= 0) return InteractionResult.PASS;

            if (!pot.isEmpty() && !ItemStack.isSameItem(held, pot.getStoredDrink())) {
                return InteractionResult.PASS;
            }

            int moved = pot.fillFrom(held, available);

            if (moved > 0 && !player.getAbilities().instabuild) {
                if (dc.hasMultiCup()) {
                    DrinkCoffee.setRemainingCups(held, available - moved);
                    if (DrinkCoffee.getRemainingCups(held) <= 0) {
                        ItemStack container = dc.getEmptyCupItem() != null
                                ? new ItemStack(dc.getEmptyCupItem()) : ItemStack.EMPTY;
                        player.setItemInHand(hand, container);
                    }
                } else {
                    ItemStack container = dc.getEmptyCupItem() != null
                            ? new ItemStack(dc.getEmptyCupItem()) : ItemStack.EMPTY;
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
