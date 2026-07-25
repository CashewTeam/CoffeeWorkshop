package net.langball.coffee.item;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.function.Supplier;

public class DrinkCoffeeInstant extends DrinkCoffee {
    private final boolean isOpened;

    public DrinkCoffeeInstant(Item.Properties properties, MobEffectInstance[] effects, int maxCups, boolean isOpened) {
        super(properties, effects, maxCups);
        this.isOpened = isOpened;
    }

    public DrinkCoffeeInstant(Item.Properties properties, MobEffectInstance[] effects, int maxCups, boolean isOpened, Supplier<Item> emptyCupItem) {
        super(properties, effects, maxCups, emptyCupItem);
        this.isOpened = isOpened;
    }

    public boolean isOpened() {
        return isOpened;
    }
}
