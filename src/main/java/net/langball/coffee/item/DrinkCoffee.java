package net.langball.coffee.item;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
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
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Supplier;

public class DrinkCoffee extends Item {
    private static final String TAG_REMAINING_CUPS = "remaining_cups";
    private static final String TAG_MAX_CUPS = "max_cups";

    private final MobEffectInstance[][] effectTable;
    private final int maxCups;
    private final int[] variantCount;
    private final Supplier<Item> emptyCupItem;

    /**
     * Full constructor with multi-variant effect table and empty cup support.
     * For Plan A (each drink = independent item) the table should contain
     * a single variant entry.
     */
    public DrinkCoffee(Properties properties, MobEffectInstance[][] effectTable,
                       int maxCups, int[] variantCount, Supplier<Item> emptyCupItem) {
        super(properties.stacksTo(1));
        this.effectTable = effectTable;
        this.maxCups = maxCups;
        this.variantCount = variantCount;
        this.emptyCupItem = emptyCupItem;
    }

    /** Simplified constructor: single effect set, no empty cup. */
    public DrinkCoffee(Properties properties, MobEffectInstance[] effects, int maxCups) {
        this(properties, new MobEffectInstance[][]{effects}, maxCups, new int[]{0}, () -> null);
    }

    /** Simplified constructor: single effect set with empty cup. */
    public DrinkCoffee(Properties properties, MobEffectInstance[] effects, int maxCups, Supplier<Item> emptyCupItem) {
        this(properties, new MobEffectInstance[][]{effects}, maxCups, new int[]{0}, emptyCupItem);
    }

    /** Legacy full constructor (no empty cup). */
    public DrinkCoffee(Properties properties, MobEffectInstance[][] effectTable, int maxCups, int[] variantCount) {
        this(properties, effectTable, maxCups, variantCount, () -> null);
    }

    // ========================================================================
    // NBT Helpers
    // ========================================================================

    /** Initialise NBT cup counters on a newly crafted stack. */
    public static ItemStack initCupCount(ItemStack stack, int maxCups) {
        CompoundTag tag = stack.getOrCreateTag();
        tag.putInt(TAG_REMAINING_CUPS, maxCups);
        tag.putInt(TAG_MAX_CUPS, maxCups);
        return stack;
    }

    public static int getRemainingCups(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        if (tag != null && tag.contains(TAG_REMAINING_CUPS)) {
            return tag.getInt(TAG_REMAINING_CUPS);
        }
        // Legacy stack without NBT: treat as single cup
        return 1;
    }

    public static int getMaxCups(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        if (tag != null && tag.contains(TAG_MAX_CUPS)) {
            return tag.getInt(TAG_MAX_CUPS);
        }
        return 1;
    }

    public static void setRemainingCups(ItemStack stack, int remaining) {
        stack.getOrCreateTag().putInt(TAG_REMAINING_CUPS, Math.max(0, remaining));
    }

    /** Whether this drink stack participates in multi-cup logic. */
    public boolean hasMultiCup() {
        return maxCups > 1 && net.langball.coffee.ModConfig.ENABLE_MULTI_CUP.get();
    }

    // ========================================================================
    // Item overrides
    // ========================================================================

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity livingEntity) {
        super.finishUsingItem(stack, level, livingEntity);

        if (livingEntity instanceof ServerPlayer serverPlayer) {
            CriteriaTriggers.CONSUME_ITEM.trigger(serverPlayer, stack);
            serverPlayer.awardStat(Stats.ITEM_USED.get(this));
        }

        // Apply effects — only the first (or only) variant in the table.
        // With Plan A each drink item has exactly one variant.
        if (!level.isClientSide && effectTable != null && effectTable.length > 0) {
            MobEffectInstance[] variant = effectTable[0];
            if (variant != null) {
                for (MobEffectInstance effect : variant) {
                    if (effect != null) {
                        livingEntity.addEffect(new MobEffectInstance(effect));
                    }
                }
            }
        }

        // Handle multi-cup logic
        if (livingEntity instanceof Player player && !player.getAbilities().instabuild) {
            boolean multiCup = net.langball.coffee.ModConfig.ENABLE_MULTI_CUP.get() && hasMultiCup();

            if (multiCup) {
                int remaining = getRemainingCups(stack);
                remaining--;

                if (remaining <= 0) {
                    // Last cup consumed — return empty cup if configured
                    if (net.langball.coffee.ModConfig.ENABLE_EMPTY_CUP_RETURN.get()
                            && emptyCupItem != null && emptyCupItem.get() != null) {
                        return new ItemStack(emptyCupItem.get());
                    }
                    return ItemStack.EMPTY;
                } else {
                    // Still has remaining servings
                    setRemainingCups(stack, remaining);
                    return stack;
                }
            } else {
                // Single-cup behaviour
                if (net.langball.coffee.ModConfig.ENABLE_EMPTY_CUP_RETURN.get()
                        && emptyCupItem != null && emptyCupItem.get() != null) {
                    return new ItemStack(emptyCupItem.get());
                }
            }
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

    // ========================================================================
    // Durability bar — shows remaining cups
    // ========================================================================

    @Override
    public boolean isBarVisible(ItemStack stack) {
        return hasMultiCup() && getRemainingCups(stack) < getMaxCups(stack);
    }

    @Override
    public int getBarWidth(ItemStack stack) {
        int max = getMaxCups(stack);
        int rem = getRemainingCups(stack);
        return max > 0 ? (rem * 13) / max : 0;
    }

    @Override
    public int getBarColor(ItemStack stack) {
        // Orange-ish colour for coffee bar
        return 0xD4A06A;
    }

    // ========================================================================
    // Tooltip: show cup count
    // ========================================================================

    @Override
    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(ItemStack stack, @Nullable Level level,
                                List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);
        if (hasMultiCup()) {
            int rem = getRemainingCups(stack);
            int max = getMaxCups(stack);
            tooltip.add(Component.translatable("tooltip.coffeework.cups_remaining", rem, max));
        }
    }
}
