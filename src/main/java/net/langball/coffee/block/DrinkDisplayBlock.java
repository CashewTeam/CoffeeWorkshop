package net.langball.coffee.block;

import net.langball.coffee.block.entity.DrinkDisplayBlockEntity;
import net.langball.coffee.init.ModBlocks;
import net.langball.coffee.item.DrinkCoffee;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;

public class DrinkDisplayBlock extends BaseEntityBlock {
    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
    protected static final VoxelShape SHAPE = Block.box(1.0D, 0.0D, 1.0D, 15.0D, 4.0D, 15.0D);

    public DrinkDisplayBlock(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext ctx) {
        return SHAPE;
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext ctx) {
        return SHAPE;
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.INVISIBLE;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        return this.defaultBlockState().setValue(FACING, ctx.getHorizontalDirection().getOpposite());
    }

    @Override
    public BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    @Override
    public BlockState mirror(BlockState state, Mirror mirror) {
        return state.rotate(mirror.getRotation(state.getValue(FACING)));
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new DrinkDisplayBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return null;
    }

    @Override
    @SuppressWarnings("deprecation")
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        if (!state.is(newState.getBlock())) {
            if (level.getBlockEntity(pos) instanceof DrinkDisplayBlockEntity be) {
                if (be.hasValidDrink()) {
                    Containers.dropItemStack(level, pos.getX(), pos.getY(), pos.getZ(), be.getDrink());
                }
                Containers.dropItemStack(level, pos.getX(), pos.getY(), pos.getZ(), new ItemStack(ModBlocks.PLATE.get()));
            }
            super.onRemove(state, level, pos, newState, isMoving);
        }
    }

    @Override
    @SuppressWarnings("deprecation")
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player,
                                  InteractionHand hand, BlockHitResult hit) {
        if (!(level.getBlockEntity(pos) instanceof DrinkDisplayBlockEntity be)) {
            return InteractionResult.PASS;
        }
        if (!be.hasValidDrink()) {
            return InteractionResult.PASS;
        }

        // Sneak + empty hand → pickup drink, revert to plate
        if (player.isShiftKeyDown() && player.getItemInHand(hand).isEmpty()) {
            if (!level.isClientSide) {
                ItemStack drink = be.getDrink();
                if (!player.getInventory().add(drink)) {
                    Containers.dropItemStack(level, pos.getX(), pos.getY() + 0.5, pos.getZ(), drink);
                }
                level.setBlock(pos, ModBlocks.PLATE.get().defaultBlockState()
                        .setValue(FACING, state.getValue(FACING)), 3);
            }
            return InteractionResult.sidedSuccess(level.isClientSide);
        }

        // Empty hand → consume one cup
        if (player.getItemInHand(hand).isEmpty()) {
            if (!level.isClientSide) {
                int remaining = be.getRemainingCups();
                if (remaining > 0) {
                    ItemStack drinkStack = be.getDrinkRaw();
                    // Apply food stats (same as DrinkCoffee.finishUsingItem)
                    FoodProperties food = drinkStack.getFoodProperties(player);
                    if (food != null) {
                        player.getFoodData().eat(food.getNutrition(), food.getSaturationModifier());
                    }

                    level.playSound(null, pos, SoundEvents.GENERIC_DRINK, SoundSource.PLAYERS, 0.5F, level.random.nextFloat() * 0.1F + 0.9F);

                    // Apply effects
                    if (drinkStack.getItem() instanceof DrinkCoffee dc) {
                        MobEffectInstance[][] effectTable = dc.getEffectTable();
                        if (effectTable != null && effectTable.length > 0) {
                            MobEffectInstance[] variant = effectTable[0];
                            if (variant != null) {
                                for (MobEffectInstance effect : variant) {
                                    if (effect != null) {
                                        player.addEffect(new MobEffectInstance(effect));
                                    }
                                }
                            }
                        }
                    }

                    player.awardStat(Stats.ITEM_USED.get(drinkStack.getItem()));

                    boolean multiCup = net.langball.coffee.ModConfig.ENABLE_MULTI_CUP.get()
                            && be.getMaxCups() > 1;

                    if (multiCup) {
                        be.consumeOneCup();
                    } else {
                        DrinkCoffee.setRemainingCups(drinkStack, 0);
                        be.setDrink(drinkStack);
                    }

                    if (be.getRemainingCups() <= 0) {
                        // Last cup: revert to plate, handle empty container
                        Item emptyCup = null;
                        if (drinkStack.getItem() instanceof DrinkCoffee dc) {
                            emptyCup = dc.getEmptyCupItem();
                        }
                        if (emptyCup != null) {
                            ItemStack cupStack = new ItemStack(emptyCup);
                            if (!player.getInventory().add(cupStack)) {
                                Containers.dropItemStack(level, pos.getX(), pos.getY() + 0.5, pos.getZ(), cupStack);
                            }
                        }
                        level.setBlock(pos, ModBlocks.PLATE.get().defaultBlockState()
                                .setValue(FACING, state.getValue(FACING)), 3);
                    }
                }
            }
            return InteractionResult.sidedSuccess(level.isClientSide);
        }

        return InteractionResult.PASS;
    }

    /**
     * Convert a Plate block at {@code pos} into a DrinkDisplayBlock with the given drink.
     * Called from BlockPlate when a player right-clicks with a drink item.
     * Returns true if conversion was successful (drink consumed from hand).
     */
    public static boolean placeDrink(Level level, BlockPos pos, Direction facing, ItemStack drinkStack) {
        if (level.isClientSide) return false;
        if (drinkStack.isEmpty()) return false;

        ResourceLocation drinkId = ForgeRegistries.ITEMS.getKey(drinkStack.getItem());
        if (drinkId == null) return false;

        BlockState displayState = ModBlocks.DRINK_DISPLAY.get().defaultBlockState()
                .setValue(FACING, facing);
        level.setBlock(pos, displayState, 3);

        if (level.getBlockEntity(pos) instanceof DrinkDisplayBlockEntity be) {
            ItemStack stored = drinkStack.copy();
            stored.setCount(1);
            be.setDrink(stored);
            return true;
        }

        level.setBlock(pos, ModBlocks.PLATE.get().defaultBlockState()
                .setValue(FACING, facing), 3);
        return false;
    }
}
