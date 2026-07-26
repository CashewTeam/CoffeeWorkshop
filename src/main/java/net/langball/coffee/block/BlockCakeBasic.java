package net.langball.coffee.block;

import net.langball.coffee.init.ModEffects;
import net.langball.coffee.init.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.function.Supplier;

public class BlockCakeBasic extends Block {
    public static final IntegerProperty BITES = BlockStateProperties.BITES;
    private static final VoxelShape[] SHAPE_SINGLE_BY_BITE = new VoxelShape[]{
            Block.box(1.0D, 0.0D, 1.0D, 15.0D, 8.0D, 15.0D),
            Block.box(3.0D, 0.0D, 1.0D, 15.0D, 8.0D, 15.0D),
            Block.box(5.0D, 0.0D, 1.0D, 15.0D, 8.0D, 15.0D),
            Block.box(7.0D, 0.0D, 1.0D, 15.0D, 8.0D, 15.0D),
            Block.box(9.0D, 0.0D, 1.0D, 15.0D, 8.0D, 15.0D),
            Block.box(11.0D, 0.0D, 1.0D, 15.0D, 8.0D, 15.0D),
            Block.box(13.0D, 0.0D, 1.0D, 15.0D, 8.0D, 15.0D)
    };
    private static final VoxelShape[] SHAPE_DOUBLE_BY_BITE = new VoxelShape[]{
            Block.box(1.0D, 0.0D, 1.0D, 15.0D, 16.0D, 15.0D),
            Block.box(3.0D, 0.0D, 1.0D, 15.0D, 16.0D, 15.0D),
            Block.box(5.0D, 0.0D, 1.0D, 15.0D, 16.0D, 15.0D),
            Block.box(7.0D, 0.0D, 1.0D, 15.0D, 16.0D, 15.0D),
            Block.box(9.0D, 0.0D, 1.0D, 15.0D, 16.0D, 15.0D),
            Block.box(11.0D, 0.0D, 1.0D, 15.0D, 16.0D, 15.0D),
            Block.box(13.0D, 0.0D, 1.0D, 15.0D, 16.0D, 15.0D)
    };

    private final int layer;
    private final int foodAmount;
    private final float foodSat;
    private final Supplier<Item> sliceItem;

    /** Legacy constructor — no slice item support. */
    public BlockCakeBasic(int layer, int foodAmount, float foodSat, BlockBehaviour.Properties properties) {
        this(layer, foodAmount, foodSat, properties, null);
    }

    /** Full constructor with optional slice item for plate-based cake cutting. */
    public BlockCakeBasic(int layer, int foodAmount, float foodSat,
                          BlockBehaviour.Properties properties, Supplier<Item> sliceItem) {
        super(properties);
        this.layer = layer;
        this.foodAmount = foodAmount;
        this.foodSat = foodSat;
        this.sliceItem = sliceItem;
        this.registerDefaultState(this.stateDefinition.any().setValue(BITES, 0));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(BITES);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        if (layer <= 1) {
            return SHAPE_SINGLE_BY_BITE[state.getValue(BITES)];
        } else {
            return SHAPE_DOUBLE_BY_BITE[state.getValue(BITES)];
        }
    }

    @Override
    @SuppressWarnings("deprecation")
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player,
                                 InteractionHand hand, BlockHitResult hit) {
        ItemStack held = player.getItemInHand(hand);

        // ── Slice interaction: right-click with plate → cut a slice ──
        if (sliceItem != null && held.is(ModItems.PLATE.get())) {
            return cutSlice(level, pos, state, player, hand);
        }

        // ── Eat interaction: normal cake eating ──
        if (level.isClientSide) {
            InteractionResult result = eat(level, pos, state, player);
            if (result.consumesAction()) {
                return InteractionResult.SUCCESS;
            }
        }
        return eat(level, pos, state, player);
    }

    private InteractionResult cutSlice(Level level, BlockPos pos, BlockState state,
                                        Player player, InteractionHand hand) {
        if (level.isClientSide) return InteractionResult.SUCCESS;

        int bites = state.getValue(BITES);
        if (bites >= 6) {
            // Cake is fully consumed — nothing left to slice
            return InteractionResult.PASS;
        }

        // Give the player a slice item
        ItemStack slice = new ItemStack(sliceItem.get());
        if (!player.getInventory().add(slice)) {
            // Inventory full — drop at feet
            level.addFreshEntity(new ItemEntity(level, pos.getX() + 0.5, pos.getY() + 0.5,
                    pos.getZ() + 0.5, slice));
        }

        // Increment BITES
        int newBites = bites + 1;
        if (newBites >= 6) {
            level.removeBlock(pos, false);
        } else {
            level.setBlock(pos, state.setValue(BITES, newBites), 3);
        }

        level.playSound(null, pos, SoundEvents.WOOL_BREAK, SoundSource.BLOCKS, 0.8F, 1.0F);
        return InteractionResult.SUCCESS;
    }

    private InteractionResult eat(Level level, BlockPos pos, BlockState state, Player player) {
        if (!player.canEat(false)) {
            return InteractionResult.PASS;
        }

        player.getFoodData().eat(foodAmount, foodSat);

        // Relax effect bonus
        if (!level.isClientSide) {
            MobEffectInstance relaxEffect = player.getEffect(ModEffects.RELAX.get());
            if (relaxEffect != null) {
                int amplifier = relaxEffect.getAmplifier();
                player.heal(amplifier + 2);
                player.getFoodData().eat(amplifier + 1, amplifier * 1.25F);
            }
        }

        int bites = state.getValue(BITES);
        if (bites < 6) {
            level.setBlock(pos, state.setValue(BITES, bites + 1), 3);
        } else {
            level.removeBlock(pos, false);
        }

        return InteractionResult.SUCCESS;
    }

    @Override
    public ItemStack getCloneItemStack(BlockGetter level, BlockPos pos, BlockState state) {
        return new ItemStack(this);
    }

    public int getLayer() {
        return layer;
    }
}
