package net.langball.coffee.block.entity;

import net.langball.coffee.block.MachineBlock;
import net.langball.coffee.recipes.MachineRecipe;
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
 * <p>This class owns the <em>what</em> of recipe-driven processing:
 * recipe resolution, output checking, result insertion, progress
 * tracking, and LIT state synchronisation.  It does <strong>not</strong>
 * handle fuel — that is added by {@link AbstractFueledProcessingBlockEntity}.
 *
 * <h3>Concrete subclass contract</h3>
 * Subclasses must implement:
 * <ul>
 *   <li>{@link #getRecipeType()} — which RecipeType to query</li>
 *   <li>{@link #getInputSlot()} — input slot index</li>
 *   <li>{@link #getOutputSlot()} — output slot index</li>
 *   <li>{@link #tick(Level, BlockPos, BlockState)} — delegates to
 *       {@link #tickProcessing(Level, BlockPos, BlockState)}</li>
 * </ul>
 */
public abstract class AbstractProcessingBlockEntity extends MachineBlockEntity {

    protected AbstractProcessingBlockEntity(net.minecraft.world.level.block.entity.BlockEntityType<?> type,
                                             BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    // ---- Abstract contract ------------------------------------------------

    /** The recipe type this machine queries. */
    protected abstract RecipeType<MachineRecipe> getRecipeType();

    /** Index of the input slot (where ingredients go). */
    protected abstract int getInputSlot();

    /** Index of the output slot (where results appear). */
    protected abstract int getOutputSlot();

    // ---- Recipe resolution -----------------------------------------------

    /**
     * Resolves the matching recipe from the current RecipeManager.
     *
     * <p>Uses {@link #resolveRecipe(RecipeType, int)} on the base class
     * to get a {@code /reload}-safe recipe.  Tracks the previous recipe
     * ID so it can detect recipe changes.
     */
    @Nullable
    protected MachineRecipe getCurrentRecipe() {
        return resolveRecipe(getRecipeType(), getInputSlot());
    }

    // ---- Output helpers ---------------------------------------------------

    /**
     * Returns {@code true} when the given recipe result can be placed
     * into the output slot.
     */
    protected boolean canAcceptResult(MachineRecipe recipe) {
        if (recipe == null) return false;
        ItemStack result = recipe.assemble(new SimpleContainer(itemHandler.getStackInSlot(getInputSlot())),
                level != null ? level.registryAccess() : null);
        return canAcceptResult(getOutputSlot(), result);
    }

    // ---- Processing helpers -----------------------------------------------

    /**
     * Atomically consumes one input, produces the result, and records
     * the recipe completion for experience tracking.
     *
     * <p>Caller must have already verified {@link #canAcceptResult(MachineRecipe)}.
     *
     * @return the recipe that was processed (for caller convenience)
     */
    protected MachineRecipe processRecipe(MachineRecipe recipe) {
        ItemStack result = recipe.assemble(new SimpleContainer(itemHandler.getStackInSlot(getInputSlot())),
                level != null ? level.registryAccess() : null);
        consumeOneWithRemainder(getInputSlot());
        insertResult(getOutputSlot(), result);
        recordRecipeCompletion(recipe);
        return recipe;
    }

    /**
     * Called when the active recipe changes (including becoming null).
     * Default implementation resets progress.
     */
    protected void onRecipeChanged(@Nullable ResourceLocation oldId,
                                   @Nullable ResourceLocation newId) {
        if (oldId == null && newId == null) return;
        if (oldId != null && oldId.equals(newId)) return;
        resetProgress();
    }

    // ---- LIT state --------------------------------------------------------

    /**
     * Returns {@code true} when the machine should appear lit.
     *
     * <p>Fuel machines rely on {@code burnTime > 0}.  Self-powered
     * machines (CoffeeMachine) may override this to also check
     * {@code canProcess} so the LIT turns off immediately when the
     * output is blocked, rather than waiting for the current cycle
     * to expire.
     */
    protected boolean shouldBeLit(MachineRecipe recipe, boolean canProcess) {
        return hasProcessingPower();
    }

    /**
     * Updates the block's {@link MachineBlock#LIT} property if it differs
     * from the current state.
     */
    protected void updateLitState(boolean lit) {
        if (level == null) return;
        BlockState state = getBlockState();
        if (state.getValue(MachineBlock.LIT) != lit) {
            level.setBlock(worldPosition, state.setValue(MachineBlock.LIT, lit),
                    Block.UPDATE_ALL);
        }
    }

    // ---- Power hooks (for self-powered / non-fuel machines) ----------------

    /**
     * Returns {@code true} when the machine currently has processing
     * power available.  Fueled machines override this to check
     * {@code burnTime > 0}; self-powered machines start their own
     * cycle.
     */
    protected boolean hasProcessingPower() {
        return burnTime > 0;
    }

    /**
     * Attempts to begin a processing-power cycle for the given recipe.
     * Called once per tick when {@link #hasProcessingPower()} is false
     * but a valid recipe is ready.  Subclasses set {@code burnTime}
     * and {@code burnTimeTotal} here.
     */
    protected void startProcessingPower(MachineRecipe recipe) {
        // default: no-op (fueled machines override in their own tick)
    }

    /**
     * Decrements the power timer each tick.  Fueled and self-powered
     * machines both call this at the start of their tick.
     *
     * <p>Calls {@link #setChanged()} when burnTime actually decrements
     * so the chunk is marked dirty for saving.  This is separate from
     * {@link #markChangedAndSync()} which also sends a block update.
     */
    protected void tickProcessingPower() {
        if (burnTime > 0) {
            burnTime--;
            setChanged();
        }
    }

    // ---- Shared tick body --------------------------------------------------

    /**
     * Common server-side tick for <em>all</em> processing machines.
     *
     * <p>The fueled variant ({@link AbstractFueledProcessingBlockEntity})
     * extends this with explicit fuel-slot logic, but the recipe-change
     * detection, progress advance, and craft-completion steps are
     * identical.  Self-powered machines (CoffeeMachine) call this
     * directly and override {@link #startProcessingPower(MachineRecipe)}
     * to initiate a self-cycle.
     */
    protected void tickProcessing(Level level, BlockPos pos, BlockState state) {
        if (level.isClientSide) return;

        // Decrement power timer (calls setChanged if burnTime changed)
        tickProcessingPower();

        // Snapshot the recipe ID *before* resolution, so we can detect changes
        ResourceLocation previousRecipeId = activeRecipeId;
        MachineRecipe recipe = getCurrentRecipe();

        // Detect recipe change
        if (!java.util.Objects.equals(previousRecipeId, activeRecipeId)) {
            onRecipeChanged(previousRecipeId, activeRecipeId);
            if (recipe != null) {
                totalCookTime = recipe.cookingTime();
            } else {
                totalCookTime = 0;
                cookTime = 0;
            }
        }

        // /reload may change cookingTime without changing recipe ID
        if (recipe != null && totalCookTime != recipe.cookingTime()
                && java.util.Objects.equals(previousRecipeId, activeRecipeId)) {
            totalCookTime = recipe.cookingTime();
            cookTime = Math.min(cookTime, totalCookTime - 1);
        }

        boolean canProcess = recipe != null && canAcceptResult(recipe);

        // If idle but work is available, try to start a power cycle
        if (!hasProcessingPower() && canProcess) {
            startProcessingPower(recipe);
        }

        // Advance progress while power is available and recipe is valid
        if (hasProcessingPower() && canProcess) {
            int oldSignal = totalCookTime > 0 ? (cookTime * 15) / totalCookTime : 0;
            cookTime++;
            setChanged(); // persist cookTime progress every tick
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
