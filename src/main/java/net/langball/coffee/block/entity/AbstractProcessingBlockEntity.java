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

    // ---- Shared tick body --------------------------------------------------

    /**
     * Common server-side tick for non-fueled processing machines.
     *
     * <p>Fueled machines should instead use
     * {@link AbstractFueledProcessingBlockEntity#tickFueledProcessing(Level, BlockPos, BlockState)}.
     *
     * <p>This method handles:
     * <ol>
     *   <li>Recipe resolution</li>
     *   <li>Recipe-change detection (resets progress)</li>
     *   <li>Progress advance when a recipe is available and output is clear</li>
     *   <li>Craft completion (consume input, produce output)</li>
     *   <li>LIT state synchronisation</li>
     * </ol>
     *
     * <p>Subclasses that provide their own power source (e.g. CoffeeMachine)
     * call this as part of their tick and manage burnTime themselves.
     */
    protected void tickProcessing(Level level, BlockPos pos, BlockState state) {
        if (level.isClientSide) return;

        MachineRecipe recipe = getCurrentRecipe();
        ResourceLocation prevId = activeRecipeId;

        boolean canProcess = recipe != null && canAcceptResult(recipe);

        if (!canProcess) {
            // No valid recipe or output blocked — pause but don't reset
            if (cookTime > 0) {
                cookTime = 0;
            }
            updateLitState(false);
            return;
        }

        // Detect recipe change
        if (recipe != null && prevId != null && !prevId.equals(recipe.getId())) {
            onRecipeChanged(prevId, recipe.getId());
        }

        // Advance progress
        cookTime++;
        if (cookTime >= totalCookTime) {
            cookTime = 0;
            totalCookTime = recipe.cookingTime();
            processRecipe(recipe);
            markChangedAndSync();
        }

        updateLitState(cookTime > 0 || burnTime > 0);
    }
}
