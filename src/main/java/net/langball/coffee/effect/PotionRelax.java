package net.langball.coffee.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;

public class PotionRelax extends MobEffect {
	public PotionRelax() {
		super(MobEffectCategory.BENEFICIAL, 0x98FB98);
	}

	@Override
	public boolean isDurationEffectTick(int duration, int amplifier) {
		// Matching vanilla Regeneration tick rate: 50 >> amplifier, minimum 1
		int k = 50 >> Math.min(amplifier, 4);
		if (k > 0) {
			return duration % k == 0;
		} else {
			return true;
		}
	}

	@Override
	public void applyEffectTick(LivingEntity entity, int amplifier) {
		if (!entity.level().isClientSide) {
			entity.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 60, amplifier, false, false, false));
		}
	}
}
