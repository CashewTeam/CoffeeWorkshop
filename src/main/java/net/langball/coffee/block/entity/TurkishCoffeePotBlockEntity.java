package net.langball.coffee.block.entity;

import net.langball.coffee.brewing.ServingContainer;
import net.langball.coffee.brewing.ServingDataImpl;
import net.langball.coffee.init.ModBlockEntities;
import net.langball.coffee.init.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.jetbrains.annotations.Nullable;

public class TurkishCoffeePotBlockEntity extends BlockEntity implements ServingContainer {
    private static final int MAX_BREW_TIME = 500;
    private static final int MAX_SERVINGS = 4;
    private static final TagKey<Block> HEAT_SOURCE_TAG =
            BlockTags.create(new ResourceLocation("coffeework", "turkish_pot_heat_sources"));

    private boolean hasCoffee;
    private boolean hasWater;
    private int brewProgress;
    private ServingDataImpl serving = new ServingDataImpl(MAX_SERVINGS);

    public TurkishCoffeePotBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.TURKISH_COFFEE_POT.get(), pos, state);
    }

    public boolean isBrewing() { return brewProgress > 0 && brewProgress < MAX_BREW_TIME; }
    public boolean isReady() { return !serving.isEmpty(); }
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
        if (getServings() <= 0) {
            brewProgress = 0;
        }
        setChanged();
        sync();
        return result;
    }

    public CompoundTag saveForItem() {
        CompoundTag tag = new CompoundTag();
        writeContents(tag);
        return tag;
    }

    private void writeContents(CompoundTag tag) {
        tag.putBoolean("HasCoffee", hasCoffee);
        tag.putBoolean("HasWater", hasWater);
        tag.putInt("BrewProgress", brewProgress);
        ServingContainer.super.saveToTag(tag);
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
        boolean wasHeated = state.getValue(net.langball.coffee.block.TurkishCoffeePotBlock.HEATED);

        if (isReady()) {
            if (!wasHeated) {
                level.setBlock(pos, state.setValue(net.langball.coffee.block.TurkishCoffeePotBlock.HEATED, true), 3);
            }
            return;
        }

        boolean onHeat = isOnHeatSource(level, pos);
        boolean shouldHeat = hasCoffee && hasWater && onHeat && !isReady();

        if (shouldHeat) {
            brewProgress++;
            if (!wasHeated) {
                level.setBlock(pos, state.setValue(net.langball.coffee.block.TurkishCoffeePotBlock.HEATED, true), 3);
            }
            if (brewProgress >= MAX_BREW_TIME) {
                hasCoffee = false;
                hasWater = false;
                brewProgress = MAX_BREW_TIME;
                ItemStack drink = new ItemStack(ModItems.COFFEE_TURKISH.get());
                if (drink.getItem() instanceof net.langball.coffee.item.DrinkCoffee dc) {
                    dc.initializeFreshStack(drink);
                    net.langball.coffee.item.DrinkCoffee.setRemainingCups(drink, 1);
                    drink.getOrCreateTag().putInt("max_cups", 1);
                }
                setStoredDrink(drink);
                setServings(MAX_SERVINGS);
                setChanged();
                sync();
            }
        } else if (!onHeat && brewProgress > 0 && brewProgress < MAX_BREW_TIME) {
            if (wasHeated) {
                level.setBlock(pos, state.setValue(net.langball.coffee.block.TurkishCoffeePotBlock.HEATED, false), 3);
            }
        }
    }

    static boolean isOnHeatSource(Level level, BlockPos pos) {
        BlockState below = level.getBlockState(pos.below());
        if (!below.is(HEAT_SOURCE_TAG)) return false;
        if (below.hasProperty(BlockStateProperties.LIT)) {
            return below.getValue(BlockStateProperties.LIT);
        }
        return true;
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
        writeContents(tag);
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
}
