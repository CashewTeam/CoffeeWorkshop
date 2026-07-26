package net.langball.coffee.recipes;

import net.minecraft.world.item.ItemStack;

/**
 * One entry in a {@link ConsumptionPlan}, describing what happens
 * to a single slot during recipe processing.
 *
 * @param slot       the inventory slot index
 * @param count      how many items to consume from this slot
 * @param remainder  the ItemStack left behind (e.g. empty bucket),
 *                   or {@link ItemStack#EMPTY} if nothing is returned
 */
public record ConsumptionEntry(int slot, int count, ItemStack remainder) {

    public ConsumptionEntry {
        if (count <= 0) throw new IllegalArgumentException("count must be > 0");
    }
}
