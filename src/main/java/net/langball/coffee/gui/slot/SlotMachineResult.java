package net.langball.coffee.gui.slot;

import net.langball.coffee.block.entity.MachineBlockEntity;
import net.langball.coffee.recipes.MachineRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.Level;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Unified output slot for all five processing machines.
 *
 * <p>Replaces the four old output-slot classes with a single slot that:
 * <ul>
 *   <li>Rejects manual insertion ({@link #mayPlace} → false).</li>
 *   <li>Triggers the item's {@code crafted} callback exactly once per
 *       player interaction (plain click, shift-click, hotbar swap).</li>
 *   <li>Awards accumulated recipe experience.</li>
 *   <li>Grants Recipe Award / Advancement trigger for each recipe
 *       completed since the last manual extraction.</li>
 *   <li>Does <strong>not</strong> generate experience or trigger awards
 *       for hopper/automation extraction (those bypass the slot's
 *       {@code onTake} callback).</li>
 * </ul>
 */
public class SlotMachineResult extends SlotItemHandler {

    private final MachineBlockEntity machine;
    private final Level level;

    public SlotMachineResult(IItemHandler handler, int slot, int x, int y,
                             MachineBlockEntity machine, Level level) {
        super(handler, slot, x, y);
        this.machine = machine;
        this.level = level;
    }

    @Override
    public boolean mayPlace(@NotNull ItemStack stack) {
        return false;
    }

    /**
     * Called during shift-click (quick-move).  We do NOT fire
     * {@code onCraftedBy} here — vanilla's slot framework will call
     * {@link #onTake} afterwards, where we handle everything once.
     */
    @Override
    protected void onQuickCraft(@NotNull ItemStack stack, int amount) {
        // Intentionally empty — crafted callback + XP + awards all
        // happen in onTake() to avoid double-firing.
    }

    /**
     * Called when a player manually removes an item from this slot
     * (click, shift-click, or hotbar swap).  Fires:
     * <ol>
     *   <li>Crafted callback on the item (once per interaction).</li>
     *   <li>Accumulated recipe experience (with fractional probability).</li>
     *   <li>Recipe Awards / Advancements for all recipes completed.</li>
     * </ol>
     */
    @Override
    public void onTake(@NotNull Player player, @NotNull ItemStack stack) {
        // 1. Crafted callback (vanilla-compatible: Player is non-null)
        if (player instanceof ServerPlayer sp) {
            int count = stack.getCount();
            // Trigger the item's own crafted callback
            stack.onCraftedBy(level, player, count);

            // 2. Award experience
            awardExperience(sp);

            // 3. Trigger Recipe Awards / Advancements
            awardRecipes(sp);
        }

        super.onTake(player, stack);
    }

    /**
     * Awards accumulated recipe experience to the player.
     */
    private void awardExperience(ServerPlayer player) {
        Map<ResourceLocation, Integer> used = machine.getRecipesUsed();
        if (used.isEmpty()) return;

        RecipeManager rm = level.getRecipeManager();
        float totalXp = 0.0F;

        for (var entry : used.entrySet()) {
            Optional<? extends net.minecraft.world.item.crafting.Recipe<?>> opt =
                    rm.byKey(entry.getKey());
            if (opt.isPresent() && opt.get() instanceof MachineRecipe recipe) {
                totalXp += recipe.experience() * entry.getValue();
            }
        }

        int wholeXp = (int) totalXp;
        float frac = totalXp - wholeXp;
        if (frac > 0.0F && level.random.nextFloat() < frac) {
            wholeXp++;
        }

        if (wholeXp > 0) {
            player.giveExperiencePoints(wholeXp);
        }
    }

    /**
     * Triggers Recipe Award / Advancement for every recipe the machine
     * completed since the last manual extraction, then clears the
     * tracking map.
     */
    private void awardRecipes(ServerPlayer player) {
        Map<ResourceLocation, Integer> used = machine.getRecipesUsed();
        if (used.isEmpty()) return;

        RecipeManager rm = level.getRecipeManager();
        List<net.minecraft.world.item.crafting.Recipe<?>> toAward = new ArrayList<>();

        Iterator<Map.Entry<ResourceLocation, Integer>> iter = used.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry<ResourceLocation, Integer> entry = iter.next();
            Optional<? extends net.minecraft.world.item.crafting.Recipe<?>> opt =
                    rm.byKey(entry.getKey());
            if (opt.isPresent()) {
                for (int i = 0; i < entry.getValue(); i++) {
                    toAward.add(opt.get());
                }
            }
            iter.remove();
        }

        if (!toAward.isEmpty()) {
            player.awardRecipes(toAward);
        }
    }

    /**
     * Returns the underlying MachineBlockEntity.
     */
    public MachineBlockEntity getMachine() {
        return machine;
    }
}
