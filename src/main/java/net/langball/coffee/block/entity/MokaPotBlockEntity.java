package net.langball.coffee.block.entity;

import net.langball.coffee.brewing.ServingContainer;
import net.langball.coffee.init.ModBlockEntities;
import net.langball.coffee.init.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class MokaPotBlockEntity extends BlockEntity implements ServingContainer {
    private static final int MAX_BREW_TIME = 400;
    private static final int MAX_SERVINGS = 4;

    private boolean hasCoffee;
    private boolean hasWater;
    private int brewProgress;
    private ServingData serving = new ServingData(MAX_SERVINGS);
    private int tickCounter;

    public MokaPotBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.MOKA_POT.get(), pos, state);
    }

    public boolean isBrewing() { return brewProgress > 0 && brewProgress < MAX_BREW_TIME; }
    public boolean isReady() { return brewProgress >= MAX_BREW_TIME && !serving.isEmpty(); }
    public boolean hasCoffeeInput() { return hasCoffee; }
    public boolean hasWaterInput() { return hasWater; }
    public int getBrewProgress() { return brewProgress; }
    public int getMaxBrewTime() { return MAX_BREW_TIME; }

    public boolean addCoffee() {
        if (hasCoffee || isReady() || isBrewing()) return false;
        hasCoffee = true;
        setChanged();
        sync();
        return true;
    }

    public boolean addWater() {
        if (hasWater || isReady() || isBrewing()) return false;
        hasWater = true;
        setChanged();
        sync();
        return true;
    }

    public ItemStack pourServing() {
        if (!isReady()) return ItemStack.EMPTY;
        ItemStack result = pourOneServing();
        setChanged();
        sync();
        return result;
    }

    public void pickUpPot(Level level, BlockPos pos) {
        ItemStack drop = new ItemStack(ModItems.MOKA_POT_ITEM.get());
        CompoundTag tag = new CompoundTag();
        saveAdditional(tag);
        if (!tag.isEmpty()) {
            drop.getOrCreateTag().put("BlockEntityTag", tag);
        }
        net.minecraft.world.entity.item.ItemEntity item = new net.minecraft.world.entity.item.ItemEntity(
                level, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, drop);
        level.addFreshEntity(item);
    }

    @Override
    public ItemStack getStoredDrink() { return serving.getStoredDrink(); }
    @Override
    public void setStoredDrink(ItemStack drink) { serving.setStoredDrink(drink); }
    @Override
    public int getServings() { return serving.getServings(); }
    @Override
    public void setServings(int s) { serving.setServings(s); }
    @Override
    public int getCapacity() { return serving.getCapacity(); }
    @Override
    public void setCapacity(int c) { serving.setCapacity(c); }

    public void serverTick(Level level, BlockPos pos, BlockState state) {
        this.tickCounter++;
        boolean wasHeated = state.getValue(net.langball.coffee.block.MokaPotBlock.HEATED);

        if (this.isReady()) {
            if (!wasHeated) {
                level.setBlock(pos, state.setValue(net.langball.coffee.block.MokaPotBlock.HEATED, true), 3);
            }
            return;
        }

        boolean onHeat = isOnHeatSource(level, pos);
        boolean shouldHeat = this.hasCoffee && this.hasWater && onHeat && !this.isReady();

        if (shouldHeat) {
            this.brewProgress++;
            if (!wasHeated) {
                level.setBlock(pos, state.setValue(net.langball.coffee.block.MokaPotBlock.HEATED, true), 3);
            }
            if (this.brewProgress >= MAX_BREW_TIME) {
                this.hasCoffee = false;
                this.hasWater = false;
                this.brewProgress = MAX_BREW_TIME;
                ItemStack drink = new ItemStack(ModItems.ESPRESSO.get());
                if (drink.getItem() instanceof net.langball.coffee.item.DrinkCoffee dc) {
                    dc.initializeFreshStack(drink);
                }
                this.setStoredDrink(drink);
                this.setServings(MAX_SERVINGS);
                this.setChanged();
                this.sync();
            }
        } else if (!onHeat && this.brewProgress > 0 && this.brewProgress < MAX_BREW_TIME) {
            if (wasHeated) {
                level.setBlock(pos, state.setValue(net.langball.coffee.block.MokaPotBlock.HEATED, false), 3);
            }
        }
    }

    static boolean isOnHeatSource(Level level, BlockPos pos) {
        BlockPos below = pos.below();
        BlockState belowState = level.getBlockState(below);
        ResourceLocation tagId = new ResourceLocation("coffeework", "moka_heat_sources");
        return belowState.is(BlockTags.create(tagId));
    }

    void sync() {
        if (level != null && !level.isClientSide) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
            setChanged();
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putBoolean("HasCoffee", hasCoffee);
        tag.putBoolean("HasWater", hasWater);
        tag.putInt("BrewProgress", brewProgress);
        saveToTag(tag);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        hasCoffee = tag.getBoolean("HasCoffee");
        hasWater = tag.getBoolean("HasWater");
        brewProgress = tag.getInt("BrewProgress");
        loadFromTag(tag);
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

    private static class ServingData implements ServingContainer {
        private ItemStack storedDrink = ItemStack.EMPTY;
        private int servings;
        private final int capacity;

        ServingData(int capacity) { this.capacity = capacity; }

        @Override public ItemStack getStoredDrink() { return storedDrink; }
        @Override public void setStoredDrink(ItemStack d) { storedDrink = d; }
        @Override public int getServings() { return servings; }
        @Override public void setServings(int s) { servings = s; }
        @Override public int getCapacity() { return capacity; }
        @Override public void setCapacity(int c) {}
    }
}
