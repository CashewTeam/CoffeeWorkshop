package net.langball.coffee.block.entity;

import net.langball.coffee.init.ModBlockEntities;
import net.langball.coffee.init.ModItems;
import net.langball.coffee.init.ModRecipeTypes;
import net.langball.coffee.recipes.SodaMachineRecipe;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.wrapper.RecipeWrapper;
import net.langball.coffee.capability.ExtractOnlyItemHandler;
import net.langball.coffee.capability.InsertOnlyItemHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class SodaMachineBlockEntity extends BlockEntity implements MenuProvider {
    public static final int SLOT_BOTTLE = 0;
    public static final int SLOT_SODA = 1;
    public static final int SLOT_FLAVOR = 2;
    public static final int SLOT_OUTPUT = 3;
    public static final int SLOT_COUNT = 4;

    private final ItemStackHandler itemHandler = new ItemStackHandler(SLOT_COUNT) {
        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack) {
            return switch (slot) {
                // Phase 9 Fix4 P2-2: restrict base/flavor slots to their
                // intended role items so junk like an apple can't lock up
                // the recipe matcher or drain a glass bottle.
                case SLOT_BOTTLE -> stack.getItem() == Items.GLASS_BOTTLE;
                case SLOT_SODA -> stack.getItem() == ModItems.SODA.get();
                case SLOT_FLAVOR -> isValidFlavor(stack);
                case SLOT_OUTPUT -> false;
                default -> false;
            };
        }
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
            if (level != null && !level.isClientSide) {
                level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
            }
        }
    };

    /** Phase 9 Fix4 P2-2: accepted flavor items for the Soda Machine.
     *  Includes the syrup bottles themselves plus the empty syrup container
     *  (so a player can refill from an empty bottle without first filling). */
    private static boolean isValidFlavor(ItemStack stack) {
        if (stack.isEmpty()) return false;
        var item = stack.getItem();
        return item == ModItems.SYRUP_EMPTY.get()
                || item == ModItems.SYRUP_CARAMEL.get()
                || item == ModItems.SYRUP_CHOCOLATE.get()
                || item == ModItems.SYRUP_FRUIT.get()
                || item == ModItems.SYRUP_MINT.get()
                || item == ModItems.SYRUP_VANILLA.get()
                || item == ModItems.SYRUP_SAKURA.get();
    }

    private LazyOptional<IItemHandler> lazyHandler = LazyOptional.empty();

    public int cookTime;
    public int totalCookTime;
    public boolean active;

    public SodaMachineBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.SODA_MACHINE.get(), pos, state);
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("block.coffeework.soda_machine");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inv, Player player) {
        return new net.langball.coffee.gui.ContainerSodaMachine(id, inv, this);
    }

    public void serverTick(Level level, BlockPos pos, BlockState state) {
        RecipeWrapper wrapper = new RecipeWrapper(itemHandler);

        Optional<SodaMachineRecipe> recipeOpt = level.getRecipeManager()
                .getRecipeFor(ModRecipeTypes.SODA_MAKING, wrapper, level);

        if (recipeOpt.isPresent()) {
            SodaMachineRecipe recipe = recipeOpt.get();
            ItemStack resultSlot = itemHandler.getStackInSlot(SLOT_OUTPUT);
            ItemStack result = recipe.result();

            if (resultSlot.isEmpty()
                    || (ItemStack.isSameItemSameTags(resultSlot, result)
                    && resultSlot.getCount() + result.getCount() <= resultSlot.getMaxStackSize())) {

                if (totalCookTime == 0) {
                    totalCookTime = recipe.cookingTime();
                }
                cookTime++;

                if (!active) {
                active = true;
                setActiveState(level, pos, true);
            }

                if (cookTime >= totalCookTime) {
                    itemHandler.extractItem(SLOT_BOTTLE, 1, false);
                    itemHandler.extractItem(SLOT_SODA, 1, false);
                    ItemStack flavorExtracted = itemHandler.extractItem(SLOT_FLAVOR, 1, false);
                    if (!flavorExtracted.isEmpty() && flavorExtracted.getItem().hasCraftingRemainingItem()) {
                        ItemStack remainder = new ItemStack(flavorExtracted.getItem().getCraftingRemainingItem());
                        ItemStack leftover = itemHandler.insertItem(SLOT_FLAVOR, remainder, false);
                        if (!leftover.isEmpty()) {
                            net.minecraft.world.Containers.dropItemStack(level,
                                    pos.getX() + 0.5, pos.getY() + 1.0, pos.getZ() + 0.5, leftover);
                        }
                    }

                    if (resultSlot.isEmpty()) {
                        itemHandler.setStackInSlot(SLOT_OUTPUT, result.copy());
                    } else {
                        resultSlot.grow(result.getCount());
                    }
                    cookTime = 0;
                    totalCookTime = 0;
                }

                // Phase 9 Fix4 P2-3: emit bubble particles while processing
                // to give visual feedback that the machine is active.
                // Emission rate is throttled to one particle every 10 ticks
                // so we don't flood the client packet budget.
                if (cookTime % 10 == 0) {
                    double x = pos.getX() + 0.5 + (level.random.nextDouble() - 0.5) * 0.4;
                    double y = pos.getY() + 0.6;
                    double z = pos.getZ() + 0.5 + (level.random.nextDouble() - 0.5) * 0.4;
                    level.addParticle(
                            net.minecraft.core.particles.ParticleTypes.BUBBLE,
                            x, y, z,
                            0.0, 0.04, 0.0);
                }
            } else {
                cookTime = 0;
                totalCookTime = 0;
                if (active) {
                    active = false;
                    setActiveState(level, pos, false);
                }
            }
        } else {
            if (cookTime > 0 || active) {
                cookTime = 0;
                totalCookTime = 0;
                active = false;
                setActiveState(level, pos, false);
            }
        }
        setChanged();
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.ITEM_HANDLER) {
            if (!lazyHandler.isPresent()) {
                lazyHandler = LazyOptional.of(() -> itemHandler);
            }
            IItemHandler handler = lazyHandler.orElse(itemHandler);
            if (side == null) return lazyHandler.cast();
            if (side == Direction.DOWN) {
                return LazyOptional.of(() -> new ExtractOnlyItemHandler(handler, SLOT_OUTPUT)).cast();
            }
            return LazyOptional.of(() -> new InsertOnlyItemHandler(handler, SLOT_BOTTLE, SLOT_SODA, SLOT_FLAVOR)).cast();
        }
        return super.getCapability(cap, side);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        lazyHandler.invalidate();
    }

    @Override
    public void reviveCaps() {
        super.reviveCaps();
        lazyHandler = LazyOptional.of(() -> itemHandler);
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.put("Inventory", itemHandler.serializeNBT());
        tag.putInt("CookTime", cookTime);
        tag.putInt("TotalCookTime", totalCookTime);
        tag.putBoolean("Active", active);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        itemHandler.deserializeNBT(tag.getCompound("Inventory"));
        cookTime = tag.getInt("CookTime");
        totalCookTime = tag.getInt("TotalCookTime");
        active = tag.getBoolean("Active");
    }

    @Override
    public CompoundTag getUpdateTag() {
        CompoundTag tag = new CompoundTag();
        saveAdditional(tag);
        return tag;
    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    public ItemStackHandler getItemHandler() {
        return itemHandler;
    }

    private void setActiveState(Level level, BlockPos pos, boolean active) {
        BlockState lower = level.getBlockState(pos);
        if (lower.hasProperty(net.langball.coffee.block.SodaMachineBlock.ACTIVE)) {
            level.setBlock(pos, lower.setValue(net.langball.coffee.block.SodaMachineBlock.ACTIVE, active), 3);
        }
        BlockPos upperPos = pos.above();
        BlockState upper = level.getBlockState(upperPos);
        if (upper.hasProperty(net.langball.coffee.block.SodaMachineBlock.ACTIVE)) {
            level.setBlock(upperPos, upper.setValue(net.langball.coffee.block.SodaMachineBlock.ACTIVE, active), 3);
        }
    }
}
