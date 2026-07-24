package net.langball.coffee.init;

import net.langball.coffee.CoffeeWork;
import net.langball.coffee.effect.PotionCaffeine;
import net.langball.coffee.effect.PotionGoldenHeart;
import net.langball.coffee.effect.PotionRelax;
import net.minecraft.world.effect.MobEffect;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModEffects {
    public static final DeferredRegister<MobEffect> EFFECTS =
            DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, CoffeeWork.MODID);

    public static final RegistryObject<MobEffect> CAFFEINE = EFFECTS.register("caffeine", PotionCaffeine::new);

    public static final RegistryObject<MobEffect> RELAX = EFFECTS.register("relax", PotionRelax::new);

    public static final RegistryObject<MobEffect> GOLDEN_HEART = EFFECTS.register("golden_heart", PotionGoldenHeart::new);
}
