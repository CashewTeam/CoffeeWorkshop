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
        return ItemStack.isSameItemSameTags(drink, getStoredDrink())
                && getServings() < getCapacity();
    }

    default void fill(ItemStack drink, int count) {
        if (count <= 0) return;
        if (getStoredDrink().isEmpty()) {
            setStoredDrink(drink.copy());
            getStoredDrink().setCount(1);
            setServings(Math.min(count, getCapacity()));
        } else if (ItemStack.isSameItemSameTags(drink, getStoredDrink())) {
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
            if (!loaded.isEmpty() && loaded.getCount() <= loaded.getMaxStackSize()) {
                setStoredDrink(loaded);
                getStoredDrink().setCount(1);
            }
        }
        int s = tag.getInt("Servings");
        int c = tag.contains("Capacity") ? tag.getInt("Capacity") : getCapacity();
        if (c > 0) setCapacity(c);
        setServings(Math.max(0, Math.min(s, getCapacity())));
    }
}
