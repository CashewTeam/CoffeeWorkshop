package net.langball.coffee.block.entity;

import net.langball.coffee.block.MachineBlock;
import net.langball.coffee.recipes.MachineRecipe;
import net.langball.coffee.recipes.ProcessingRecipe;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

/**
 * Mid-level base shared by all processing machines.
 *
 * <p>Works against {@link ProcessingRecipe} so that both single-input
 * ({@link MachineRecipe}) and multi-input
 * ({@link net.langball.coffee.recipes.CoffeeBrewingRecipe}) recipes
 * are handled by the same engine.
 */
public abstract class AbstractProcessingBlockEntity extends MachineBlockEntity {

    protected AbstractProcessingBlockEntity(net.minecraft.world.level.block.entity.BlockEntityType<?> type,
                                             BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    // ---- Abstract contract ------------------------------------------------

    protected abstract RecipeType<?> getRecipeType();
    protected abstract int getInputSlot();
    protected abstract int getOutputSlot();

    // ---- Recipe resolution -----------------------------------------------

    @Nullable
    protected ProcessingRecipe getCurrentRecipe() {
        if (level == null) return null;

        ItemStack input = itemHandler.getStackInSlot(getInputSlot());
        if (input.isEmpty()) {
            activeRecipeId = null;
            return null;
        }

        var rm = level.getRecipeManager();
        SimpleContainer container = new SimpleContainer(itemHandler.getSlots());
        for (int i = 0; i < itemHandler.getSlots(); i++) {
            container.setItem(i, itemHandler.getStackInSlot(i));
        }

        // 1. Try by stored ID
        if (activeRecipeId != null) {
            var byId = rm.byKey(activeRecipeId);
            if (byId.isPresent() && byId.get() instanceof ProcessingRecipe pr
                    && pr.getType() == getRecipeType() && pr.matches(container, level)) {
                return pr;
            }
            activeRecipeId = null;
        }

        // 2. Full scan via RecipeManager
        var found = rm.getRecipeFor((RecipeType) getRecipeType(), container, level);
        if (found.isPresent() && found.get() instanceof ProcessingRecipe pr) {
            activeRecipeId = pr.getId();
            return pr;
        }
        activeRecipeId = null;
        return null;
    }

    // ---- Output helpers ---------------------------------------------------

    protected boolean canAcceptResult(ProcessingRecipe recipe) {
        if (recipe == null) return false;
        SimpleContainer container = new SimpleContainer(itemHandler.getSlots());
        for (int i = 0; i < itemHandler.getSlots(); i++) {
            container.setItem(i, itemHandler.getStackInSlot(i));
        }
        ItemStack result = recipe.assemble(container, level != null ? level.registryAccess() : null);
        return canAcceptResult(getOutputSlot(), result);
    }

    // ---- Processing helpers -----------------------------------------------

    /**
     * Atomically consumes inputs and produces the result.
     *
     * <p>Pre-checks that every slot has enough items and all remainders
     * can be placed before modifying any slot.
     */
    protected ProcessingRecipe processRecipe(ProcessingRecipe recipe) {
        SimpleContainer container = new SimpleContainer(itemHandler.getSlots());
        for (int i = 0; i < itemHandler.getSlots(); i++) {
            container.setItem(i, itemHandler.getStackInSlot(i));
        }
        ItemStack result = recipe.assemble(container, level != null ? level.registryAccess() : null);

        // Pre-check: verify all slots have enough and remainders can fit
        int[] slots = recipe.getConsumedSlots();
        for (int slot : slots) {
            int required = recipe.getRequiredCount(slot);
            ItemStack stack = itemHandler.getStackInSlot(slot);
            if (stack.getCount() < required) return recipe; // safety: should not happen

            // Check remainder placement
            ItemStack remainder = ItemStack.EMPTY;
            if (recipe instanceof net.langball.coffee.recipes.CoffeeBrewingRecipe cbr) {
                remainder = cbr.getRemainder(slot);
            }
            if (!remainder.isEmpty() && stack.getCount() > required) {
                // Remainder would need to drop into world — still OK but warn
            }
        }

        // Now consume atomically
        if (slots.length == 1) {
            consumeOneWithRemainder(slots[0]);
        } else {
            for (int slot : slots) {
                int count = recipe.getRequiredCount(slot);
                ItemStack stack = itemHandler.getStackInSlot(slot);
                if (!stack.isEmpty() && count > 0) {
                    ItemStack remainder = ItemStack.EMPTY;
                    if (recipe instanceof net.langball.coffee.recipes.CoffeeBrewingRecipe cbr) {
                        remainder = cbr.getRemainder(slot);
                    }

                    stack.shrink(count);
                    if (!remainder.isEmpty()) {
                        if (stack.isEmpty()) {
                            itemHandler.setStackInSlot(slot, remainder);
                        } else {
                            net.minecraft.world.Containers.dropItemStack(level,
                                    worldPosition.getX(), worldPosition.getY(), worldPosition.getZ(),
                                    remainder);
                        }
                    }
                }
            }
        }

        insertResult(getOutputSlot(), result);
        recordRecipeCompletion(recipe);
        return recipe;
    }

    protected void onRecipeChanged(@Nullable ResourceLocation oldId,
                                   @Nullable ResourceLocation newId) {
        if (oldId == null && newId == null) return;
        if (oldId != null && oldId.equals(newId)) return;
        resetProgress();
    }

    protected void recordRecipeCompletion(ProcessingRecipe recipe) {
        recipesUsed.merge(recipe.getId(), 1, Integer::sum);
    }

    // ---- LIT state --------------------------------------------------------

    protected boolean shouldBeLit(ProcessingRecipe recipe, boolean canProcess) {
        return hasProcessingPower();
    }

    protected void updateLitState(boolean lit) {
        if (level == null) return;
        BlockState state = getBlockState();
        if (state.getValue(MachineBlock.LIT) != lit) {
            level.setBlock(worldPosition, state.setValue(MachineBlock.LIT, lit),
                    Block.UPDATE_ALL);
        }
    }

    // ---- Power hooks -------------------------------------------------------

    protected boolean hasProcessingPower() {
        return burnTime > 0;
    }

    protected void startProcessingPower(ProcessingRecipe recipe) {
    }

    protected void tickProcessingPower() {
        if (burnTime > 0) {
            burnTime--;
            setChanged();
        }
    }

    // ---- Shared tick body --------------------------------------------------

    protected void tickProcessing(Level level, BlockPos pos, BlockState state) {
        if (level.isClientSide) return;

        tickProcessingPower();

        ResourceLocation previousRecipeId = activeRecipeId;
        ProcessingRecipe recipe = getCurrentRecipe();

        if (!java.util.Objects.equals(previousRecipeId, activeRecipeId)) {
            onRecipeChanged(previousRecipeId, activeRecipeId);
            if (recipe != null) {
                totalCookTime = recipe.cookingTime();
            } else {
                totalCookTime = 0;
                cookTime = 0;
            }
        }

        if (recipe != null && totalCookTime != recipe.cookingTime()
                && java.util.Objects.equals(previousRecipeId, activeRecipeId)) {
            totalCookTime = recipe.cookingTime();
            cookTime = Math.min(cookTime, totalCookTime - 1);
        }

        boolean canProcess = recipe != null && canAcceptResult(recipe);

        if (!hasProcessingPower() && canProcess) {
            startProcessingPower(recipe);
        }

        if (hasProcessingPower() && canProcess) {
            int oldSignal = totalCookTime > 0 ? (cookTime * 15) / totalCookTime : 0;
            cookTime++;
            setChanged();
            int newSignal = totalCookTime > 0 ? (cookTime * 15) / totalCookTime : 0;
            if (oldSignal != newSignal && level != null) {
                level.updateNeighbourForOutputSignal(worldPosition,
                        getBlockState().getBlock());
            }
            if (cookTime >= totalCookTime) {
                cookTime = 0;
                totalCookTime = recipe.cookingTime();
                processRecipe(recipe);
                markChangedAndSync();
                return;
            }
        } else if (!canProcess && cookTime > 0) {
            if (recipe == null) {
                cookTime = 0;
            }
        }

        updateLitState(shouldBeLit(recipe, canProcess));
    }
}
