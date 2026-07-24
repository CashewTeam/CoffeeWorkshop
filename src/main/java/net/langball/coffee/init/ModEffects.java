package net.langball.coffee.init;

import net.langball.coffee.CoffeeWork;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModEffects {
    public static final DeferredRegister<MobEffect> EFFECTS =
            DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, CoffeeWork.MODID);

    // Caffeine effect - gives speed/haste
    public static final RegistryObject<MobEffect> CAFFEINE = EFFECTS.register("caffeine",
            () -> new MobEffect(MobEffectCategory.BENEFICIAL, 0x8B4513) {
                // PotionCaffeine logic will be added in Sprint 4
            });

    // Relax effect - calming
    public static final RegistryObject<MobEffect> RELAX = EFFECTS.register("relax",
            () -> new MobEffect(MobEffectCategory.BENEFICIAL, 0x98FB98) {
                // PotionRelax logic will be added in Sprint 4
            });

    // Golden Heart effect - extra health
    public static final RegistryObject<MobEffect> GOLDEN_HEART = EFFECTS.register("golden_heart",
            () -> new MobEffect(MobEffectCategory.BENEFICIAL, 0xFFD700) {
                // PotionGoldenHeart logic will be added in Sprint 4
            });
}
