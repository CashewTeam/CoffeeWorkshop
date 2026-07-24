package net.langball.coffee.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;

public class PotionGoldenHeart extends MobEffect {
	public PotionGoldenHeart() {
		super(MobEffectCategory.BENEFICIAL, 0xFFD700);
	}

	@Override
	public boolean isDurationEffectTick(int duration, int amplifier) {
		// Tick every 2 seconds (40 ticks) to keep Absorption refreshed
		return duration > 0 && duration % 40 == 0;
	}

	@Override
	public void applyEffectTick(LivingEntity entity, int amplifier) {
		if (!entity.level().isClientSide) {
			entity.addEffect(new MobEffectInstance(MobEffects.ABSORPTION, 80, amplifier, false, false, false));
		}
	}
}
