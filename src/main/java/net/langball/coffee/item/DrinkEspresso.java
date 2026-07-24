package net.langball.coffee.item;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.Item;

public class DrinkEspresso extends DrinkCoffee {
    public DrinkEspresso(Item.Properties properties, MobEffectInstance[] effects, int maxCups) {
        super(properties, effects, maxCups);
    }
}
