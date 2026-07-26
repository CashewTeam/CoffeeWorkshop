package net.langball.coffee.recipes;

import net.minecraft.world.item.ItemStack;

import java.util.List;

/**
 * Describes a complete plan for atomically consuming inputs and placing
 * remainders for one machine recipe cycle.
 *
 * <p>The plan is built by pre-checking every slot and remainder
 * destination before any mutation occurs.  Once built, it is
 * applied in a single pass — if any step would fail, processing is
 * halted and nothing is consumed.
 */
public record ConsumptionPlan(List<ConsumptionEntry> entries, ItemStack result) {

    public static ConsumptionPlan of(List<ConsumptionEntry> entries, ItemStack result) {
        return new ConsumptionPlan(entries, result);
    }
}
