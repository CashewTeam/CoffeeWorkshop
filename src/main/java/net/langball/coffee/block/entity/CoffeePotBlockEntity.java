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
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class CoffeePotBlockEntity extends BlockEntity implements ServingContainer {
    private static final int CAPACITY = 4;

    private ServingDataImpl serving = new ServingDataImpl(CAPACITY);

    public CoffeePotBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.COFFEE_POT.get(), pos, state);
    }

    public boolean isReady() { return !serving.isEmpty(); }

    public ItemStack pourServing() {
        if (!isReady()) return ItemStack.EMPTY;
        ItemStack result = pourOneServing();
        setChanged();
        sync();
        return result;
    }

    public int fillFrom(ItemStack source, int requested) {
        ItemStack dc = source.copy();
        dc.setCount(1);
        if (dc.getItem() instanceof net.langball.coffee.item.DrinkCoffee drink) {
            drink.ensureCupData(dc);
        }
        int available = requested;
        if (getStoredDrink().isEmpty()) {
            ItemStack template = dc.copy();
            if (template.getItem() instanceof net.langball.coffee.item.DrinkCoffee d) {
                net.langball.coffee.item.DrinkCoffee.setRemainingCups(template, 1);
                template.getOrCreateTag().putInt("max_cups", 1);
            }
            setStoredDrink(template);
            int moved = Math.min(available, CAPACITY);
            setServings(moved);
            setChanged();
            sync();
            return moved;
        } else if (ItemStack.isSameItem(dc, getStoredDrink())) {
            int moved = Math.min(available, CAPACITY - getServings());
            if (moved > 0) {
                setServings(getServings() + moved);
            }
            setChanged();
            sync();
            return moved;
        }
        return 0;
    }

    public int getFillLevel() {
        if (serving.isEmpty()) return 0;
        return Math.min(serving.getServings(), 4);
    }

    public CompoundTag saveForItem() {
        CompoundTag tag = new CompoundTag();
        writeContents(tag);
        return tag;
    }

    private void writeContents(CompoundTag tag) {
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

    public void onLoadSyncLevel() {
        if (level != null && !level.isClientSide) {
            BlockState state = getBlockState();
            int lvl = getFillLevel();
            if (state.hasProperty(net.langball.coffee.block.CoffeePotBlock.LEVEL)
                    && state.getValue(net.langball.coffee.block.CoffeePotBlock.LEVEL) != lvl) {
                level.setBlock(worldPosition, state.setValue(net.langball.coffee.block.CoffeePotBlock.LEVEL, lvl), 3);
            }
        }
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
        loadFromTag(tag);
    }

    @Override
    public void onLoad() {
        super.onLoad();
        onLoadSyncLevel();
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
