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

    /** @return the configured max cups for this drink item. */
    public int getConfiguredMaxCups() {
        return maxCups;
    }

    /** Initialises cup NBT on a freshly crafted stack. */
    public ItemStack initializeFreshStack(ItemStack stack) {
        return initCupCount(stack, maxCups);
    }

    /**
     * Lazy initialisation: if NBT cup data is missing (e.g. from /give or
     * legacy stacks), set it to this item's configured maxCups so the drink
     * behaves correctly rather than defaulting to 1.
     */
    public void ensureCupData(ItemStack stack) {
        CompoundTag tag = stack.getOrCreateTag();
        if (!tag.contains(TAG_MAX_CUPS)) {
            tag.putInt(TAG_MAX_CUPS, maxCups);
        }
        if (!tag.contains(TAG_REMAINING_CUPS)) {
            tag.putInt(TAG_REMAINING_CUPS, maxCups);
        }
    }

    public static int getRemainingCups(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        if (tag != null && tag.contains(TAG_REMAINING_CUPS)) {
            return tag.getInt(TAG_REMAINING_CUPS);
        }
        // Legacy stack without NBT: return 1 (safe default before ensureCupData is called)
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

    @Nullable
    public Item getEmptyCupItem() {
        return emptyCupItem != null ? emptyCupItem.get() : null;
    }

    public MobEffectInstance[][] getEffectTable() {
        return effectTable;
    }

    /**
     * Apply one serving of this drink: food, effects, stats, criteria,
     * and cup-count logic.  Modifies the stack's {@code remaining_cups}
     * in-place.
     *
     * @param consumeResource if false, skips cup decrement and empty-container
     *        return (used for creative-mode drinking where effects/stats
     *        should still apply but the item is not consumed)
     * @return the empty container to give back (empty if still has cups,
     *         or the configured empty-cup / bottle item)
     */
    public ItemStack consumeOneServing(ItemStack stack, LivingEntity entity, Level level, boolean consumeResource) {
        ensureCupData(stack);

        if (!level.isClientSide) {
            FoodProperties food = stack.getFoodProperties(entity);
            if (food != null && entity instanceof Player player) {
                player.getFoodData().eat(food.getNutrition(), food.getSaturationModifier());
            }
        }

        if (entity instanceof ServerPlayer sp) {
            CriteriaTriggers.CONSUME_ITEM.trigger(sp, stack);
            sp.awardStat(Stats.ITEM_USED.get(this));
        }

        if (!level.isClientSide && effectTable != null && effectTable.length > 0) {
            MobEffectInstance[] variant = effectTable[0];
            if (variant != null) {
                for (MobEffectInstance effect : variant) {
                    if (effect != null) {
                        entity.addEffect(new MobEffectInstance(effect));
                    }
                }
            }
        }

        if (!consumeResource) {
            return ItemStack.EMPTY;
        }

        boolean multiCup = net.langball.coffee.ModConfig.ENABLE_MULTI_CUP.get() && hasMultiCup();
        int remaining = getRemainingCups(stack);
        if (multiCup) {
            remaining = Math.max(0, remaining - 1);
        } else {
            remaining = 0;
        }
        setRemainingCups(stack, remaining);

        if (remaining <= 0) {
            boolean cupReturn = net.langball.coffee.ModConfig.ENABLE_EMPTY_CUP_RETURN.get()
                    && emptyCupItem != null && emptyCupItem.get() != null;
            return cupReturn ? new ItemStack(emptyCupItem.get()) : ItemStack.EMPTY;
        }
        return ItemStack.EMPTY;
    }

    // ========================================================================
    // Item overrides
    // ========================================================================

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity livingEntity) {
        if (livingEntity instanceof Player player && !player.getAbilities().instabuild) {
            ItemStack empty = consumeOneServing(stack, livingEntity, level, true);
            if (getRemainingCups(stack) > 0) {
                return stack;
            }
            return empty;
        }
        consumeOneServing(stack, livingEntity, level, false);
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
        ItemStack stack = player.getItemInHand(hand);
        ensureCupData(stack);
        return ItemUtils.startUsingInstantly(level, player, hand);
    }

    // ========================================================================
    // Durability bar — shows remaining cups
    // ========================================================================

    @Override
    public boolean isBarVisible(ItemStack stack) {
        ensureCupData(stack);
        return hasMultiCup() && getRemainingCups(stack) < getMaxCups(stack);
    }

    @Override
    public int getBarWidth(ItemStack stack) {
        ensureCupData(stack);
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
        ensureCupData(stack);
        if (hasMultiCup()) {
            int rem = getRemainingCups(stack);
            int max = getMaxCups(stack);
            tooltip.add(Component.translatable("tooltip.coffeework.cups_remaining", rem, max));
        }
    }
}
