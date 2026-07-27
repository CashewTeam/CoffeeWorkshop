package net.langball.coffee.block;

import net.langball.coffee.block.entity.CoffeeMachineBlockEntity;
import net.langball.coffee.block.entity.CoffeePotBlockEntity;
import net.langball.coffee.init.ModBlockEntities;
import net.langball.coffee.init.ModItems;
import net.langball.coffee.item.DrinkCoffee;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
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
    /** Phase 9 Fix4: shared whitelist for Coffee Pot fills — used by both
     *  the placed-pot right-click and the held-pot direct extraction here. */
    private static final TagKey<Item> COFFEE_POT_DRINKS =
            ItemTags.create(new ResourceLocation("coffeework", "coffee_pot_drinks"));

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
            if (level.isClientSide) return InteractionResult.sidedSuccess(true);

            // Phase 9 Fix4 P0-1/P0-2: assert single item count to prevent
            // stacked-pot duplication of the BlockEntityTag mutation below.
            if (held.getCount() != 1) {
                return InteractionResult.PASS;
            }

            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof CoffeeMachineBlockEntity cm) {
                IItemHandler handler = cm.getItemHandler();
                ItemStack output = handler.getStackInSlot(CoffeeMachineBlockEntity.SLOT_OUTPUT);
                if (output.isEmpty() || !(output.getItem() instanceof DrinkCoffee dc)) {
                    return InteractionResult.CONSUME;
                }

                // Phase 9 Fix4 P1-1: enforce Coffee Pot whitelist here too.
                if (!output.is(COFFEE_POT_DRINKS)) {
                    return InteractionResult.CONSUME;
                }

                dc.ensureCupData(output);

                // Phase 9 Fix4 P0-1: respect ENABLE_MULTI_CUP.  When multi-cup
                // is disabled we move exactly one serving into the pot,
                // mirroring the CoffeePotBlock.use() path.  Without this,
                // a 4-cup Americano would be transferred as 4 servings.
                int available = dc.hasMultiCup()
                        ? DrinkCoffee.getRemainingCups(output)
                        : 1;
                if (available <= 0) return InteractionResult.CONSUME;

                CompoundTag potTag = held.getOrCreateTagElement("BlockEntityTag");
                CoffeePotBlockEntity potData = new CoffeePotBlockEntity(pos, state);
                if (!potTag.isEmpty()) potData.load(potTag);

                if (!potData.isEmpty()
                        && !ItemStack.isSameItem(potData.getStoredDrink(), output)) {
                    return InteractionResult.CONSUME;
                }

                int moved = potData.fillFrom(output, available);
                if (moved <= 0) return InteractionResult.CONSUME;

                // Phase 9 Fix4 P0-1: deduct based on `available`/`moved`,
                // not raw remaining_cups from NBT.
                int remaining = available - moved;
                DrinkCoffee.setRemainingCups(output, remaining);

                // Phase 9 Fix4 P1-2: when the machine output is exhausted,
                // return the empty container to the player (or output slot)
                // so the cup/glass bottle economy is conserved.
                if (remaining <= 0) {
                    Item emptyCup = dc.getEmptyCupItem();
                    handler.extractItem(CoffeeMachineBlockEntity.SLOT_OUTPUT,
                            output.getCount(), false);
                    if (emptyCup != null) {
                        ItemStack container = new ItemStack(emptyCup);
                        if (!player.addItem(container)) {
                            net.minecraft.world.Containers.dropItemStack(level,
                                    pos.getX() + 0.5, pos.getY() + 0.5,
                                    pos.getZ() + 0.5, container);
                        }
                    }
                }
                cm.setChanged();

                CompoundTag saveTag = potData.saveForItem();
                if (!saveTag.isEmpty()) {
                    held.getOrCreateTag().put("BlockEntityTag", saveTag);
                } else if (held.getTag() != null && held.getTag().contains("BlockEntityTag")) {
                    held.getTag().remove("BlockEntityTag");
                }
                return InteractionResult.CONSUME;
            }
            return InteractionResult.CONSUME;
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
