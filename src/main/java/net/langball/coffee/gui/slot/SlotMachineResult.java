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

import java.util.Iterator;
import java.util.Map;
import java.util.Optional;

/**
 * Unified output slot for all five processing machines.
 *
 * <p>Replaces the four separate output-slot classes
 * ({@code SlotGrinderOutput}, {@code SlotCoffeeMachineOutput},
 * {@code SlotRollerOutput}, {@code SlotICEMachineOutput}) with a single
 * class that handles:
 *
 * <ul>
 *   <li>Rejecting manual insertion ({@link #mayPlace} → false).</li>
 *   <li>Triggering the {@code crafted} callback on the output item.</li>
 *   <li>Awarding accumulated recipe experience to the player when items
 *       are manually taken (plain click, shift-click, or hotbar swap).</li>
 *   <li>Not generating experience for hopper/automation extraction
 *       (those bypass the slot's {@code onTake} callback).</li>
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

    @Override
    protected void onQuickCraft(@NotNull ItemStack stack, int amount) {
        super.onQuickCraft(stack, amount);
        // The item's own crafted callback
        stack.onCraftedBy(level, null, amount);
    }

    @Override
    public void onTake(@NotNull Player player, @NotNull ItemStack stack) {
        // Fire crafted callback *before* awarding XP so any advancement
        // triggers see the correct inventory state
        stack.onCraftedBy(level, player, stack.getCount());

        // Award accumulated experience
        awardExperience(player);

        super.onTake(player, stack);
    }

    /**
     * Awards all accumulated recipe experience to the player and clears
     * the tracking map.
     *
     * <p>Experience formula: {@code sum(recipe.experience × completionCount)}.
     * Fractional remainder uses the standard furnace probability:
     * if {@code random.nextFloat() < fractionalPart}, award +1 XP.
     */
    private void awardExperience(Player player) {
        if (!(player instanceof ServerPlayer sp)) return;
        if (level == null) return;

        Map<ResourceLocation, Integer> used = machine.getRecipesUsed();
        if (used.isEmpty()) return;

        RecipeManager rm = level.getRecipeManager();
        float totalXp = 0.0F;

        Iterator<Map.Entry<ResourceLocation, Integer>> iter = used.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry<ResourceLocation, Integer> entry = iter.next();
            Optional<? extends net.minecraft.world.item.crafting.Recipe<?>> opt =
                    rm.byKey(entry.getKey());
            if (opt.isPresent() && opt.get() instanceof MachineRecipe recipe) {
                totalXp += recipe.experience() * entry.getValue();
            }
            iter.remove();
        }

        int wholeXp = (int) totalXp;
        float frac = totalXp - wholeXp;
        if (frac > 0.0F && level.random.nextFloat() < frac) {
            wholeXp++;
        }

        if (wholeXp > 0) {
            sp.giveExperiencePoints(wholeXp);
        }
    }

    /**
     * Returns the underlying MachineBlockEntity for external access
     * (e.g. the menu's quick-move logic).
     */
    public MachineBlockEntity getMachine() {
        return machine;
    }
}
