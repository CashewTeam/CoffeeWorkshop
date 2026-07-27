package net.langball.coffee.block.entity;

import net.langball.coffee.init.ModBlockEntities;
import net.langball.coffee.item.DrinkCoffee;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

public class DrinkDisplayBlockEntity extends BlockEntity {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final String TAG_DRINK = "Drink";

    @Nullable
    private ItemStack drink = ItemStack.EMPTY;

    public DrinkDisplayBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.DRINK_DISPLAY.get(), pos, state);
    }

    public void setDrink(ItemStack stack) {
        this.drink = stack.copy();
        setChanged();
        if (level != null) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
        }
    }

    public ItemStack getDrink() {
        return drink == null ? ItemStack.EMPTY : drink.copy();
    }

    public ItemStack getDrinkRaw() {
        return drink == null ? ItemStack.EMPTY : drink;
    }

    public int getRemainingCups() {
        if (drink == null || drink.isEmpty()) return 0;
        return DrinkCoffee.getRemainingCups(drink);
    }

    public int getMaxCups() {
        if (drink == null || drink.isEmpty()) return 0;
        return DrinkCoffee.getMaxCups(drink);
    }

    public ItemStack consumeOneCup() {
        if (drink == null || drink.isEmpty()) return ItemStack.EMPTY;
        int remaining = getRemainingCups();
        if (remaining <= 0) return ItemStack.EMPTY;

        remaining--;
        DrinkCoffee.setRemainingCups(drink, remaining);
        setChanged();
        if (level != null) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
        }

        if (remaining <= 0) {
            ItemStack empty = drink.getCraftingRemainingItem();
            return empty;
        }
        return drink.copy();
    }

    public boolean hasValidDrink() {
        return drink != null && !drink.isEmpty()
                && ForgeRegistries.ITEMS.getKey(drink.getItem()) != null;
    }

    @Nullable
    public ResourceLocation getDrinkId() {
        if (!hasValidDrink()) return null;
        return ForgeRegistries.ITEMS.getKey(drink.getItem());
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        if (drink != null && !drink.isEmpty()) {
            CompoundTag drinkTag = new CompoundTag();
            drink.save(drinkTag);
            tag.put(TAG_DRINK, drinkTag);
        }
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        if (tag.contains(TAG_DRINK)) {
            ItemStack loaded = ItemStack.of(tag.getCompound(TAG_DRINK));
            if (loaded.isEmpty()) {
                drink = ItemStack.EMPTY;
                return;
            }
            if (ForgeRegistries.ITEMS.getKey(loaded.getItem()) == null) {
                LOGGER.warn("DrinkDisplayBlockEntity at {} loaded unregistered item {}, clearing",
                        worldPosition, loaded.getItem());
                drink = ItemStack.EMPTY;
                return;
            }
            if (!(loaded.getItem() instanceof DrinkCoffee)) {
                LOGGER.warn("DrinkDisplayBlockEntity at {} loaded non-drink item {}, clearing",
                        worldPosition, loaded.getItem());
                drink = ItemStack.EMPTY;
                return;
            }
            int remaining = DrinkCoffee.getRemainingCups(loaded);
            int max = DrinkCoffee.getMaxCups(loaded);
            if (remaining < 1 || remaining > max) {
                LOGGER.warn("DrinkDisplayBlockEntity at {} loaded invalid cup count {}/{}, clearing",
                        worldPosition, remaining, max);
                drink = ItemStack.EMPTY;
                return;
            }
            drink = loaded;
        } else {
            drink = ItemStack.EMPTY;
        }
    }

    @Override
    public CompoundTag getUpdateTag() {
        CompoundTag tag = super.getUpdateTag();
        saveAdditional(tag);
        return tag;
    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }
}
