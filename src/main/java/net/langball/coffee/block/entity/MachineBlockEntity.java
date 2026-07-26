package net.langball.coffee.block.entity;

import net.langball.coffee.CoffeeWork;
import net.langball.coffee.recipes.MachineRecipe;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Common base for all five processing-machine BlockEntities.
 *
 * <h3>Phase 2 additions</h3>
 * <ul>
 *   <li>{@link #activeRecipeId} — recipe ID (not object) persisted in NBT,
 *       safe across {@code /reload}.</li>
 *   <li>{@link #recipesUsed} — tracks completions per recipe ID for
 *       experience award.</li>
 *   <li>{@link #resolveRecipe(RecipeType, int)} — resolves active recipe
 *       through RecipeManager, preferring by-key lookup.</li>
 *   <li>{@link #canAcceptResult(int, ItemStack)} — output slot capacity
 *       check.</li>
 *   <li>{@link #insertResult(int, ItemStack)} — merges or places result.</li>
 *   <li>{@link #markChangedAndSync()} — unified dirty+sync helper.</li>
 * </ul>
 *
 * Subclasses implement:
 * <ul>
 *   <li>{@link #getDisplayName()} – I18N key for the container title</li>
 *   <li>{@link #createMenu(int, Inventory, Player)} – the menu factory</li>
 *   <li>{@link #tick(Level, BlockPos, BlockState)} – the tick body</li>
 * </ul>
 */
public abstract class MachineBlockEntity extends BlockEntity implements MenuProvider {

    /** Subclasses must initialise this in their constructor. */
    protected ItemStackHandler itemHandler;
    protected LazyOptional<IItemHandler> lazyHandler = LazyOptional.empty();

    /** Per-direction capability handlers for automation. */
    protected LazyOptional<IItemHandler> inputHandler = LazyOptional.empty();
    protected LazyOptional<IItemHandler> fuelHandler = LazyOptional.empty();
    protected LazyOptional<IItemHandler> outputHandler = LazyOptional.empty();

    protected int cookTime;
    protected int totalCookTime;
    protected int burnTime;
    protected int burnTimeTotal;

    /**
     * ID of the recipe currently being processed (if any).
     * Stored in NBT; survives /reload because the recipe is re-resolved
     * through the current RecipeManager on next tick.
     */
    @Nullable
    protected ResourceLocation activeRecipeId;

    /**
     * Number of times each recipe completed on this machine since the
     * last time a player manually extracted output.  Used for experience
     * award.  <em>Not</em> synced to client; only saved in NBT.
     */
    final Map<ResourceLocation, Integer> recipesUsed = new HashMap<>();

    /** @return the recipes-used map for experience tracking (package-private
     *  so {@link net.langball.coffee.gui.slot.SlotMachineResult} can access it). */
    public Map<ResourceLocation, Integer> getRecipesUsed() {
        return recipesUsed;
    }

    public final ContainerData data = new ContainerData() {
        @Override public int get(int index) {
            return switch (index) {
                case 0 -> cookTime;
                case 1 -> totalCookTime;
                case 2 -> burnTime;
                case 3 -> burnTimeTotal;
                default -> 0;
            };
        }

        @Override public void set(int index, int value) {
            switch (index) {
                case 0 -> cookTime = value;
                case 1 -> totalCookTime = value;
                case 2 -> burnTime = value;
                case 3 -> burnTimeTotal = value;
            }
        }

        @Override public int getCount() { return 4; }
    };

    protected MachineBlockEntity(BlockEntityType<?> type, BlockPos pos,
                                  BlockState state) {
        super(type, pos, state);
    }

    /** Convenience getter for the inventory handler. */
    public IItemHandler getItemHandler() {
        return itemHandler;
    }

    // ---- Abstract slot groups for automation --------------------------

    /** Slot indices that accept input from the top/bottom/etc. */
    protected abstract int[] getInputSlots();

    /**
     * Slot indices accessible from the top face.  Defaults to
     * {@link #getInputSlots()}.  Override in machines that need
     * different routing (e.g. Coffee Machine: top = base only).
     */
    protected int[] getTopInputSlots() {
        return getInputSlots();
    }

    /**
     * Slot indices accessible from horizontal faces.  Defaults to
     * {@link #getInputSlots()}.  Override in machines that need
     * different routing (e.g. Coffee Machine: sides = modifier,
     * additive, container; not base).
     */
    protected int[] getHorizontalInputSlots() {
        return getInputSlots();
    }

    /** Slot indices that accept fuel from the sides.  Empty array
     *  for machines without a fuel slot (e.g. Coffee Machine). */
    protected abstract int[] getFuelSlots();

    /** Slot indices from which automation may extract items. */
    protected abstract int[] getOutputSlots();

    // ---- Capability lifecycle -------------------------------------------

    @Override
    public void onLoad() {
        super.onLoad();
        lazyHandler = LazyOptional.of(() -> itemHandler);
        inputHandler = LazyOptional.of(() ->
                new net.langball.coffee.capability.InsertOnlyItemHandler(itemHandler, getInputSlots()));
        fuelHandler = LazyOptional.of(() -> {
            int[] fuelSlots = getFuelSlots();
            if (fuelSlots.length == 0) {
                // No fuel slots → return a handler that rejects everything
                return new net.langball.coffee.capability.InsertOnlyItemHandler(itemHandler);
            }
            return new net.langball.coffee.capability.InsertOnlyItemHandler(itemHandler, fuelSlots);
        });
        outputHandler = LazyOptional.of(() ->
                new net.langball.coffee.capability.ExtractOnlyItemHandler(itemHandler, getOutputSlots()));
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        lazyHandler.invalidate();
        inputHandler.invalidate();
        fuelHandler.invalidate();
        outputHandler.invalidate();
    }

    @Override
    public void reviveCaps() {
        super.reviveCaps();
        lazyHandler = LazyOptional.of(() -> itemHandler);
        inputHandler = LazyOptional.of(() ->
                new net.langball.coffee.capability.InsertOnlyItemHandler(itemHandler, getInputSlots()));
        fuelHandler = LazyOptional.of(() -> {
            int[] fuelSlots = getFuelSlots();
            if (fuelSlots.length == 0) {
                return new net.langball.coffee.capability.InsertOnlyItemHandler(itemHandler);
            }
            return new net.langball.coffee.capability.InsertOnlyItemHandler(itemHandler, fuelSlots);
        });
        outputHandler = LazyOptional.of(() ->
                new net.langball.coffee.capability.ExtractOnlyItemHandler(itemHandler, getOutputSlots()));
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap,
                                                       @Nullable Direction side) {
        if (cap == ForgeCapabilities.ITEM_HANDLER) {
            if (side == null) {
                return lazyHandler.cast();
            }
            return switch (side) {
                case UP -> LazyOptional.of(() ->
                        new net.langball.coffee.capability.InsertOnlyItemHandler(itemHandler, getTopInputSlots())).cast();
                case DOWN -> outputHandler.cast();
                default -> { // horizontal
                    int[] fuelSlots = getFuelSlots();
                    if (fuelSlots.length > 0) {
                        yield fuelHandler.cast();
                    }
                    // Expose horizontal-specific input slots
                    yield LazyOptional.of(() ->
                            new net.langball.coffee.capability.InsertOnlyItemHandler(itemHandler, getHorizontalInputSlots())).cast();
                }
            };
        }
        return super.getCapability(cap, side);
    }

    // ---- Persistence / sync --------------------------------------------

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putInt("BurnTime", burnTime);
        tag.putInt("CookTime", cookTime);
        tag.putInt("CookTimeTotal", totalCookTime);
        tag.putInt("BurnTimeTotal", burnTimeTotal);
        tag.put("Items", itemHandler.serializeNBT());
        if (activeRecipeId != null) {
            tag.putString("ActiveRecipe", activeRecipeId.toString());
        }
        if (!recipesUsed.isEmpty()) {
            CompoundTag used = new CompoundTag();
            for (var entry : recipesUsed.entrySet()) {
                used.putInt(entry.getKey().toString(), entry.getValue());
            }
            tag.put("RecipesUsed", used);
        }
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        burnTime = tag.getInt("BurnTime");
        cookTime = tag.getInt("CookTime");
        totalCookTime = tag.getInt("CookTimeTotal");
        burnTimeTotal = tag.getInt("BurnTimeTotal");
        itemHandler.deserializeNBT(tag.getCompound("Items"));
        // Clear before loading to avoid stale state when fields are absent
        activeRecipeId = null;
        recipesUsed.clear();
        if (tag.contains("ActiveRecipe")) {
            activeRecipeId = ResourceLocation.tryParse(tag.getString("ActiveRecipe"));
        }
        if (tag.contains("RecipesUsed")) {
            CompoundTag used = tag.getCompound("RecipesUsed");
            for (String key : used.getAllKeys()) {
                ResourceLocation rl = ResourceLocation.tryParse(key);
                if (rl != null) {
                    recipesUsed.put(rl, used.getInt(key));
                }
            }
        }
    }

    @Override
    public CompoundTag getUpdateTag() {
        CompoundTag tag = super.getUpdateTag();
        saveAdditional(tag);
        return tag;
    }

    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    // ---- Comparator output ---------------------------------------------

    /** Furnace-style redstone signal: 0 when idle, 1–15 proportional to
     *  the fraction of {@link #cookTime} / {@link #totalCookTime}. */
    public int getComparatorOutput() {
        int total = Math.max(1, totalCookTime);
        return Math.min(15, (cookTime * 15) / total);
    }

    // ---- Unified dirty flag + sync ---------------------------------------

    /**
     * Marks the block entity as changed and sends a block update to clients
     * so that LIT state, comparator output, and render data stay in sync.
     *
     * <p>Call this whenever the inventory, progress, or LIT state changes.
     * Avoid calling it every tick unconditionally.
     */
    protected void markChangedAndSync() {
        setChanged();
        if (level != null) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(),
                    Block.UPDATE_CLIENTS);
            level.updateNeighbourForOutputSignal(worldPosition,
                    getBlockState().getBlock());
        }
    }

    // ---- Recipe resolver ------------------------------------------------

    /**
     * Resolves the matching {@link MachineRecipe} for the given input slot.
     *
     * <h3>Lookup strategy</h3>
     * <ol>
     *   <li>If {@link #activeRecipeId} is set, look it up by key in the
     *       current {@link RecipeManager}.  If it still matches the input,
     *       return it.  If not, clear the ID.</li>
     *   <li>Otherwise, scan all recipes of the given {@code type} for a
     *       match.</li>
     * </ol>
     *
     * <p>This method never caches the recipe object itself — only the ID.
     * After {@code /reload}, the new recipe object is fetched through the
     * current RecipeManager.</p>
     *
     * @param type      the machine-specific RecipeType
     * @param inputSlot the slot index to check for a matching ingredient
     * @return the matching recipe, or {@code null} if none
     */
    @Nullable
    protected MachineRecipe resolveRecipe(RecipeType<MachineRecipe> type, int inputSlot) {
        if (level == null) return null;

        ItemStack input = itemHandler.getStackInSlot(inputSlot);
        if (input.isEmpty()) {
            activeRecipeId = null;
            return null;
        }

        RecipeManager rm = level.getRecipeManager();
        SimpleContainer container = new SimpleContainer(input);

        // 1. Try by stored ID first (fast path after /reload)
        if (activeRecipeId != null) {
            Optional<? extends net.minecraft.world.item.crafting.Recipe<?>> byId =
                    rm.byKey(activeRecipeId);
            if (byId.isPresent() && byId.get() instanceof MachineRecipe recipe
                    && recipe.getType() == type && recipe.matches(container, level)) {
                return recipe;
            }
            activeRecipeId = null;
        }

        // 2. Full scan
        Optional<MachineRecipe> found = rm.getRecipeFor(type, container, level);
        if (found.isPresent()) {
            MachineRecipe recipe = found.get();
            activeRecipeId = recipe.getId();
            return recipe;
        }
        activeRecipeId = null;
        return null;
    }

    /**
     * Checks whether {@code result} can be placed into the output slot.
     *
     * <p>Returns {@code true} if:
     * <ul>
     *   <li>the slot is empty, or</li>
     *   <li>the slot contains the same item (ignoring count), has matching
     *       NBT/components, and the sum does not exceed the item's max
     *       stack size or the slot's capacity.</li>
     * </ul>
     *
     * @param outputSlot the slot index to check
     * @param result     the proposed result (count carries expected amount)
     * @return {@code true} if the result can be accepted
     */
    protected boolean canAcceptResult(int outputSlot, ItemStack result) {
        if (result.isEmpty() || result.getCount() <= 0) return false;

        ItemStack existing = itemHandler.getStackInSlot(outputSlot);
        if (existing.isEmpty()) return true;

        if (!ItemStack.isSameItemSameTags(existing, result)) return false;

        int total = existing.getCount() + result.getCount();
        int slotLimit = Math.min(itemHandler.getSlotLimit(outputSlot), result.getMaxStackSize());
        return total <= slotLimit;
    }

    /**
     * Places {@code result} into the output slot, merging with the
     * existing stack if present.
     *
     * <p>Assumes {@link #canAcceptResult(int, ItemStack)} has already
     * returned {@code true}.  Uses {@link ItemStack#copy()} to avoid
     * mutating the caller's reference.
     *
     * @param outputSlot the slot index
     * @param result     the result to insert
     */
    protected void insertResult(int outputSlot, ItemStack result) {
        ItemStack existing = itemHandler.getStackInSlot(outputSlot);
        if (existing.isEmpty()) {
            itemHandler.setStackInSlot(outputSlot, result.copy());
        } else {
            int newCount = Math.min(existing.getCount() + result.getCount(),
                    Math.min(itemHandler.getSlotLimit(outputSlot), result.getMaxStackSize()));
            existing.setCount(newCount);
        }
    }

    /**
     * Records one completion of the given recipe for experience tracking.
     */
    protected void recordRecipeCompletion(MachineRecipe recipe) {
        recipesUsed.merge(recipe.getId(), 1, Integer::sum);
    }

    /**
     * Resets the processing progress, typically after a recipe change.
     */
    protected void resetProgress() {
        cookTime = 0;
        totalCookTime = 0;
    }

    // ---- Input consumption helpers ---------------------------------------

    /**
     * Consume one item from the given input slot, correctly handling
     * crafting remainders (container items).
     */
    protected void consumeOneWithRemainder(int slot) {
        ItemStack input = itemHandler.getStackInSlot(slot);
        if (input.isEmpty()) return;

        ItemStack one = input.copyWithCount(1);
        ItemStack remainder = one.getCraftingRemainingItem();

        input.shrink(1);

        if (!remainder.isEmpty()) {
            if (input.isEmpty()) {
                itemHandler.setStackInSlot(slot, remainder);
            } else {
                Containers.dropItemStack(level, worldPosition.getX(),
                        worldPosition.getY(), worldPosition.getZ(), remainder);
            }
        }
    }

    // ---- Tick hook -----------------------------------------------------

    /** Subclass tick body. */
    public abstract void tick(Level level, BlockPos pos, BlockState state);
}
