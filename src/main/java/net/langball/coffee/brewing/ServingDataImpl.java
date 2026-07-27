package net.langball.coffee.brewing;

import net.minecraft.world.item.ItemStack;

public class ServingDataImpl implements ServingContainer {
    private ItemStack storedDrink = ItemStack.EMPTY;
    private int servings;
    private final int capacity;

    public ServingDataImpl(int capacity) { this.capacity = capacity; }

    @Override public ItemStack getStoredDrink() { return storedDrink; }
    @Override public void setStoredDrink(ItemStack d) { storedDrink = d; }
    @Override public int getServings() { return servings; }
    @Override public void setServings(int s) { servings = s; }
    @Override public int getCapacity() { return capacity; }
    @Override public void setCapacity(int c) {}
}
