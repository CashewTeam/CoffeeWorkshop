package net.langball.coffee.block.entity;

import net.langball.coffee.block.BlockPlate;
import net.langball.coffee.block.DrinkDisplayRegistry;
import net.langball.coffee.init.ModBlockEntities;
import net.langball.coffee.init.ModBlocks;
import net.langball.coffee.item.DrinkCoffee;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
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
    private boolean needsRecovery = false;

    public DrinkDisplayBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.DRINK_DISPLAY.get(), pos, state);
    }

    public void setDrink(ItemStack stack) {
        this.drink = stack.copy();
        this.needsRecovery = false;
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

    public boolean hasValidDrink() {
        if (drink == null || drink.isEmpty()) return false;
        if (!(drink.getItem() instanceof DrinkCoffee dc)) return false;

        ResourceLocation id = ForgeRegistries.ITEMS.getKey(drink.getItem());
        if (id == null) return false;
        if (!DrinkDisplayRegistry.canDisplay(id)) return false;

        int remaining = DrinkCoffee.getRemainingCups(drink);
        int max = DrinkCoffee.getMaxCups(drink);
        return max == dc.getConfiguredMaxCups()
                && remaining >= 1
                && remaining <= max;
    }

    /** Return the drink and clear the internal reference (for pickup). */
    public ItemStack removeDrink() {
        ItemStack result = getDrink();
        drink = ItemStack.EMPTY;
        setChanged();
        return result;
    }

    /** Clear the drink without returning it (for last-cup consumption). */
    public void clearDrink() {
        drink = ItemStack.EMPTY;
        setChanged();
    }

    /**
     * Called every server tick by {@link DrinkDisplayBlock}.  If this BE
     * was loaded with invalid/corrupted NBT (unregistered item, non-drink,
     * bad cup count), revert back to an empty Plate so the block doesn't
     * become a permanent invisible ghost.
     */
    public void recoverIfInvalid() {
        if (!needsRecovery && hasValidDrink()) {
            return;
        }
        LOGGER.warn("DrinkDisplayBlockEntity at {} is invalid (recovery={}, validDrink={}), reverting to plate",
                worldPosition, needsRecovery, hasValidDrink());
        if (level != null && !level.isClientSide) {
            Direction facing = getBlockState().getValue(net.minecraft.world.level.block.HorizontalDirectionalBlock.FACING);
            level.setBlock(worldPosition, ModBlocks.PLATE.get().defaultBlockState()
                    .setValue(BlockPlate.FACING, facing), 3);
        }
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
        tag.putBoolean("NeedsRecovery", needsRecovery);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        needsRecovery = tag.getBoolean("NeedsRecovery");
        if (tag.contains(TAG_DRINK)) {
            ItemStack loaded = ItemStack.of(tag.getCompound(TAG_DRINK));
            if (loaded.isEmpty()) {
                needsRecovery = true;
                return;
            }
            if (ForgeRegistries.ITEMS.getKey(loaded.getItem()) == null) {
                LOGGER.warn("DrinkDisplayBlockEntity at {} loaded unregistered item {}", worldPosition, loaded.getItem());
                needsRecovery = true;
                return;
            }
            if (!(loaded.getItem() instanceof DrinkCoffee)) {
                LOGGER.warn("DrinkDisplayBlockEntity at {} loaded non-drink item {}", worldPosition, loaded.getItem());
                needsRecovery = true;
                return;
            }
            int remaining = DrinkCoffee.getRemainingCups(loaded);
            int max = DrinkCoffee.getMaxCups(loaded);
            if (remaining < 1 || remaining > max) {
                LOGGER.warn("DrinkDisplayBlockEntity at {} loaded invalid cup count {}/{}", worldPosition, remaining, max);
                needsRecovery = true;
                return;
            }
            drink = loaded;
            needsRecovery = false;
        } else {
            needsRecovery = true;
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
