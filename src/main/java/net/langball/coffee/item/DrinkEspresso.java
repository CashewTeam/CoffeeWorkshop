package net.langball.coffee.item;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.function.Supplier;

public class DrinkEspresso extends DrinkCoffee {
    public DrinkEspresso(Item.Properties properties, MobEffectInstance[] effects, int maxCups) {
        super(properties, effects, maxCups);
    }

    public DrinkEspresso(Item.Properties properties, MobEffectInstance[] effects, int maxCups, Supplier<Item> emptyCupItem) {
        super(properties, effects, maxCups, emptyCupItem);
    }
}
