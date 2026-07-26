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

        // Find the best matching recipe using a two-tier score:
        //   1. PRIMARY score (×100): number of slots where available count == required
        //      (exact match).  This strongly prefers recipes whose quantity
        //      requirements are exactly satisfied.
        //   2. TIEBREAKER score (+1): total items to be consumed.  When two
        //      recipes both have the same number of exact matches, the one
        //      consuming more items wins (avoids wasteful partial consumption).
        //
        // Together these disambiguate e.g. Latte (count=1) vs Macchiato (count=2):
        //   with 1 coffee powder → Latte wins (1 exact) Macchiato invalid (0 matches)
        //   with 2+ coffee powder → Macchiato wins (1 exact, higher tiebreaker)
        //                   OR Latte wins (1 exact, lower tiebreaker)
        //     - Macchiato gets exact match (=2) → score = 100
        //     - Latte gets excess match (≥1) → score = 0
        var allRecipes = rm.getAllRecipesFor((RecipeType) getRecipeType());
        ProcessingRecipe best = null;
        int bestScore = Integer.MIN_VALUE;

        for (var recipe : allRecipes) {
            if (recipe instanceof ProcessingRecipe pr && pr.matches(container, level)) {
                int exact = 0;
                int total = 0;
                for (int s : pr.getConsumedSlots()) {
                    int required = pr.getRequiredCount(s);
                    int available = container.getItem(s).getCount();
                    if (available == required) exact++;
                    total += required;
                }
                int score = exact * 100 + total;
                if (score > bestScore) {
                    bestScore = score;
                    best = pr;
                }
            }
        }

        // Verify cached activeRecipeId is still best; switch if not.
        if (best != null) {
            if (activeRecipeId != null) {
                var byId = rm.byKey(activeRecipeId);
                if (byId.isPresent() && byId.get() instanceof ProcessingRecipe cachedPr
                        && cachedPr.getType() == getRecipeType() && cachedPr.matches(container, level)) {
                    // Compute cached's score and compare
                    int cachedExact = 0;
                    int cachedTotal = 0;
                    for (int s : cachedPr.getConsumedSlots()) {
                        int required = cachedPr.getRequiredCount(s);
                        int available = container.getItem(s).getCount();
                        if (available == required) cachedExact++;
                        cachedTotal += required;
                    }
                    int cachedScore = cachedExact * 100 + cachedTotal;
                    if (cachedScore >= bestScore) {
                        // Cached is still the best
                        return cachedPr;
                    }
                    // Otherwise, fall through and pick best
                }
            }
            activeRecipeId = best.getId();
            return best;
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
     * Atomically consumes inputs and produces the result using a
     * {@link net.langball.coffee.recipes.ConsumptionPlan}.
     *
     * <h3>Guarantees</h3>
     * <ul>
     *   <li>All consumed slots are verified to have enough items.</li>
     *   <li>Every remainder has a legal destination: the same slot after
     *       it has been emptied by consumption.  If the slot still holds
     *       items after consumption, processing is <b>halted</b> and
     *       nothing is consumed (remainders are never dropped to the
     *       world under normal operation).</li>
     *   <li>Output capacity is checked before any slot mutation.</li>
     *   <li>If any check fails, no slot is modified.</li>
     * </ul>
     *
     * <p>Rema​inder sources (checked in order):
     * <ol>
     *   <li>{@link ProcessingRecipe#getRemainder(int)} — for recipe-specific logic</li>
     *   <li>If that returns empty, the consumed item's
     *       {@link net.minecraft.world.item.ItemStack#getCraftingRemainingItem()}</li>
     * </ol>
     */
    protected ProcessingRecipe processRecipe(ProcessingRecipe recipe) {
        if (level == null) return recipe;

        // 1. Assemble result
        SimpleContainer container = new SimpleContainer(itemHandler.getSlots());
        for (int i = 0; i < itemHandler.getSlots(); i++) {
            container.setItem(i, itemHandler.getStackInSlot(i));
        }
        ItemStack result = recipe.assemble(container, level.registryAccess());

        // 2. Check output capacity upfront
        if (!canAcceptResult(getOutputSlot(), result)) {
            return recipe; // output blocked — consume nothing
        }

        // 3. Build & verify consumption plan
        int[] consumedSlots = recipe.getConsumedSlots();
        java.util.List<net.langball.coffee.recipes.ConsumptionEntry> entries =
                new java.util.ArrayList<>(consumedSlots.length);
        boolean canProcess = true;

        for (int slot : consumedSlots) {
            int required = recipe.getRequiredCount(slot);
            ItemStack current = itemHandler.getStackInSlot(slot);

            if (current.getCount() < required) {
                canProcess = false;
                break; // not enough items — abort
            }

            // Get remainder: recipe-specific first, then item's own crafting remainder
            ItemStack remainder = recipe.getRemainder(slot);
            if (remainder.isEmpty()) {
                // Simulate consuming one item to get its crafting remainder
                ItemStack one = current.copyWithCount(1);
                remainder = one.getCraftingRemainingItem();
            }

            int remainingAfter = current.getCount() - required;

            // Remainder legality: a remainder can ONLY be placed back if the
            // slot is fully vacated (count drops to 0).  If there are still
            // items left, the remainder has no home → halt processing.
            if (!remainder.isEmpty() && remainingAfter > 0) {
                canProcess = false;
                break;
            }

            entries.add(new net.langball.coffee.recipes.ConsumptionEntry(slot, required, remainder));
        }

        if (!canProcess) {
            return recipe; // remainder placement impossible — consume nothing
        }

        // 4. Apply atomically — every remainder is guaranteed to have room
        for (var entry : entries) {
            ItemStack stack = itemHandler.getStackInSlot(entry.slot());
            stack.shrink(entry.count());

            if (!entry.remainder().isEmpty()) {
                // By pre-check: stack must be empty here (remainingAfter == 0)
                itemHandler.setStackInSlot(entry.slot(), entry.remainder().copy());
            }
        }

        // 5. Place result and record completion
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
