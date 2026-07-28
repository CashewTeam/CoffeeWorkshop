package net.langball.coffee.advancement;

import com.google.gson.JsonObject;
import net.langball.coffee.CoffeeWork;
import net.minecraft.advancements.critereon.AbstractCriterionTriggerInstance;
import net.minecraft.advancements.critereon.ContextAwarePredicate;
import net.minecraft.advancements.critereon.DeserializationContext;
import net.minecraft.advancements.critereon.SimpleCriterionTrigger;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

/**
 * Phase 9 Fix7 P1-4: custom criterion that fires exactly when a
 * record is successfully inserted into a Phonograph.  Vanilla
 * {@code item_used_on_block} fires on every right-click and cannot
 * distinguish an insert from an eject, so the generic trigger would
 * award the advancement to a player who merely popped an existing
 * record out.  This criterion is only triggered from
 * {@link net.langball.coffee.block.entity.PhonographBlockEntity#insertRecord}
 * after the record signature confirms a successful insert.
 *
 * The criterion matches any record (no item predicate required) — the
 * advancement JSON does not need a {@code conditions} block.  Future
 * fields can be added without a schema break.
 */
public class PhonographPlayTrigger extends SimpleCriterionTrigger<PhonographPlayTrigger.TriggerInstance> {
    private static final ResourceLocation ID = CoffeeWork.id("phonograph_play");

    public static final PhonographPlayTrigger INSTANCE = new PhonographPlayTrigger();

    public ResourceLocation getId() { return ID; }

    public void trigger(ServerPlayer player) {
        trigger(player, t -> true);
    }

    @Override
    protected TriggerInstance createInstance(JsonObject json, ContextAwarePredicate player,
                                              DeserializationContext ctx) {
        // No item check on the criterion side — the advancement only
        // fires from the code path that successfully inserted a
        // record, so the item-gating is implicit.
        return new TriggerInstance(player);
    }

    public static class TriggerInstance extends AbstractCriterionTriggerInstance {
        public TriggerInstance(ContextAwarePredicate player) {
            super(ID, player);
        }
    }
}
