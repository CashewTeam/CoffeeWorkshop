package net.langball.coffee.brewing;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;

public interface ServingContainer {

    ItemStack getStoredDrink();
    void setStoredDrink(ItemStack drink);
    int getServings();
    void setServings(int servings);
    int getCapacity();
    void setCapacity(int capacity);

    default void clear() {
        setStoredDrink(ItemStack.EMPTY);
        setServings(0);
    }

    default boolean isEmpty() {
        return getStoredDrink().isEmpty() || getServings() <= 0;
    }

    default boolean isFull() {
        return !getStoredDrink().isEmpty() && getServings() >= getCapacity();
    }

    default ItemStack pourOneServing() {
        if (isEmpty()) return ItemStack.EMPTY;
        ItemStack result = getStoredDrink().copy();
        result.setCount(1);
        int remaining = getServings() - 1;
        if (remaining <= 0) {
            clear();
        } else {
            setServings(remaining);
        }
        return result;
    }

    default boolean canAccept(ItemStack drink) {
        if (drink.isEmpty()) return false;
        if (getStoredDrink().isEmpty()) return true;
        return ItemStack.isSameItem(drink, getStoredDrink())
                && getServings() < getCapacity();
    }

    /**
     * Phase 9 Fix5 P2: legacy fill API — kept for backwards compatibility
     * but routes through the single-serving normalisation logic so callers
     * can no longer accidentally paste a multi-cup stack into the pot.
     * Prefer {@link #fillFrom(ItemStack, int)} which already normalises.
     */
    default void fill(ItemStack drink, int count) {
        if (count <= 0) return;
        if (!(drink.getItem() instanceof net.langball.coffee.item.DrinkCoffee)) return;
        if (getStoredDrink().isEmpty()) {
            ItemStack template = drink.copy();
            template.setCount(1);
            net.langball.coffee.item.DrinkCoffee.setRemainingCups(template, 1);
            template.getOrCreateTag().putInt("max_cups", 1);
            setStoredDrink(template);
            setServings(Math.min(count, getCapacity()));
        } else if (ItemStack.isSameItem(drink, getStoredDrink())) {
            setServings(Math.min(getServings() + count, getCapacity()));
        }
    }

    default void saveToTag(CompoundTag tag) {
        if (!getStoredDrink().isEmpty()) {
            CompoundTag drinkTag = new CompoundTag();
            getStoredDrink().save(drinkTag);
            tag.put("StoredDrink", drinkTag);
        }
        tag.putInt("Servings", getServings());
        tag.putInt("Capacity", getCapacity());
    }

    default void loadFromTag(CompoundTag tag) {
        if (tag.contains("StoredDrink")) {
            ItemStack loaded = ItemStack.of(tag.getCompound("StoredDrink"));
            if (loaded.isEmpty() || loaded.getCount() > loaded.getMaxStackSize()) {
                clear();
                return;
            }
            if (!(loaded.getItem() instanceof net.langball.coffee.item.DrinkCoffee)) {
                clear();
                return;
            }
            loaded.setCount(1);
            // Phase 9 Fix4 P2-1: normalise stored drink so the template
            // always represents a single serving.  An old / corrupted stack
            // may carry remaining_cups > 1, which would let a single
            // pourServing() copy the entire multi-cup payload.
            int rem = net.langball.coffee.item.DrinkCoffee.getRemainingCups(loaded);
            if (rem <= 0) {
                clear();
                return;
            }
            if (rem > 1) {
                net.langball.coffee.item.DrinkCoffee.setRemainingCups(loaded, 1);
            }
            CompoundTag nd = loaded.getOrCreateTag();
            if (!nd.contains("max_cups") || nd.getInt("max_cups") > 1) {
                nd.putInt("max_cups", 1);
            }
            setStoredDrink(loaded);
        } else if (tag.getInt("Servings") > 0) {
            clear();
            return;
        }
        int s = tag.getInt("Servings");
        // Phase 9 Fix4 P2-1: never trust a Capacity value from NBT for fixed
        // containers.  The BE constructor initialises the design capacity;
        // we only need to clamp servings to that value.
        int cap = getCapacity();
        if (cap <= 0) cap = 4; // safety net
        setServings(Math.max(0, Math.min(s, cap)));
        if (getServings() <= 0) {
            setStoredDrink(ItemStack.EMPTY);
        }
    }
}
