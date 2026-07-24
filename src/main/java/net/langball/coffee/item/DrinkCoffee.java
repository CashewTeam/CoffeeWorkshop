package net.langball.coffee.item;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;

public class DrinkCoffee extends Item {
    private final MobEffectInstance[][] effectTable;
    private final int maxCups;
    private final int[] variantCount;

    public DrinkCoffee(Properties properties, MobEffectInstance[][] effectTable, int maxCups, int[] variantCount) {
        super(properties.stacksTo(1));
        this.effectTable = effectTable;
        this.maxCups = maxCups;
        this.variantCount = variantCount;
    }

    public DrinkCoffee(Properties properties, MobEffectInstance[] effects, int maxCups) {
        super(properties.stacksTo(1));
        this.effectTable = new MobEffectInstance[][]{effects};
        this.maxCups = maxCups;
        this.variantCount = new int[]{0};
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity livingEntity) {
        super.finishUsingItem(stack, level, livingEntity);
        if (livingEntity instanceof ServerPlayer serverPlayer) {
            CriteriaTriggers.CONSUME_ITEM.trigger(serverPlayer, stack);
            serverPlayer.awardStat(Stats.ITEM_USED.get(this));
        }

        if (!level.isClientSide && effectTable != null) {
            for (MobEffectInstance[] variants : effectTable) {
                for (MobEffectInstance effect : variants) {
                    if (effect != null && livingEntity.getRandom().nextFloat() < 1.0F) {
                        livingEntity.addEffect(new MobEffectInstance(effect));
                    }
                }
            }
        }

        if (stack.isEmpty()) {
            return ItemStack.EMPTY;
        }
        return stack;
    }

    @Override
    public int getUseDuration(ItemStack stack) {
        return 32;
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.DRINK;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        return ItemUtils.startUsingInstantly(level, player, hand);
    }

    public int getMaxCups() {
        return maxCups;
    }
}
