package net.langball.coffee.block;

import net.langball.coffee.block.entity.CoffeeMachineBlockEntity;
import net.langball.coffee.block.entity.CoffeePotBlockEntity;
import net.langball.coffee.init.ModBlockEntities;
import net.langball.coffee.init.ModItems;
import net.langball.coffee.item.DrinkCoffee;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.Nullable;

public class BlockCoffeeMachine extends MachineBlock {
    public BlockCoffeeMachine(Properties properties) {
        super(properties);
    }

    @Nullable
    @Override
    protected BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new CoffeeMachineBlockEntity(pos, state);
    }

    @Nullable
    @Override
    protected <T extends BlockEntity> BlockEntityTicker<T> createTicker(Level level, BlockEntityType<T> type) {
        return createTickerHelper(type, ModBlockEntities.COFFEE_MACHINE.get(),
                (l, p, s, be) -> be.tick(l, p, s));
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos,
                                  Player player, InteractionHand hand,
                                  BlockHitResult hit) {
        ItemStack held = player.getItemInHand(hand);
        if (held.getItem() == ModItems.COFFEE_POT_ITEM.get()) {
            if (!level.isClientSide) {
                BlockEntity be = level.getBlockEntity(pos);
                if (be instanceof CoffeeMachineBlockEntity cm) {
                    IItemHandler handler = cm.getItemHandler();
                    ItemStack output = handler.getStackInSlot(CoffeeMachineBlockEntity.SLOT_OUTPUT);
                    if (!output.isEmpty() && output.getItem() instanceof DrinkCoffee) {
                        CompoundTag potTag = held.getOrCreateTagElement("BlockEntityTag");
                        CoffeePotBlockEntity potData = new CoffeePotBlockEntity(pos, state);
                        if (!potTag.isEmpty()) potData.load(potTag);

                        if (potData.isEmpty() || ItemStack.isSameItem(potData.getStoredDrink(), output)) {
                            int remaining = DrinkCoffee.getRemainingCups(output);
                            int moved = potData.fillFrom(output, remaining);
                            if (moved > 0) {
                                DrinkCoffee.setRemainingCups(output, remaining - moved);
                                if (DrinkCoffee.getRemainingCups(output) <= 0) {
                                    handler.extractItem(CoffeeMachineBlockEntity.SLOT_OUTPUT,
                                            output.getCount(), false);
                                }
                                cm.setChanged();
                                CompoundTag saveTag = potData.saveForItem();
                                if (!saveTag.isEmpty()) {
                                    held.getOrCreateTag().put("BlockEntityTag", saveTag);
                                }
                            }
                        }
                    }
                }
            }
            return InteractionResult.sidedSuccess(level.isClientSide);
        }

        if (level.isClientSide) return InteractionResult.sidedSuccess(true);
        BlockEntity be = level.getBlockEntity(pos);
        if (be instanceof net.minecraft.world.MenuProvider mp) {
            NetworkHooks.openScreen((ServerPlayer) player, mp, pos);
            return InteractionResult.CONSUME;
        }
        return InteractionResult.PASS;
    }
}
