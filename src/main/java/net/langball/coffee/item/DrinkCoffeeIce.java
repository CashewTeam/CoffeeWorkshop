package net.langball.coffee.item;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class DrinkCoffeeIce extends DrinkCoffee {
    public DrinkCoffeeIce(Item.Properties properties, MobEffectInstance[] effects, int maxCups) {
        super(properties, effects, maxCups);
    }

    public DrinkCoffeeIce(Item.Properties properties, MobEffectInstance[][] effectTable, int maxCups, int[] variantCount) {
        super(properties, effectTable, maxCups, variantCount);
    }
}
