package net.langball.coffee.init;

import net.langball.coffee.CoffeeWork;
import net.langball.coffee.world.BlueBerryFeature;
import net.langball.coffee.world.CoffeeTreeFeature;
import net.langball.coffee.world.SodaOreFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModFeatures {
    public static final DeferredRegister<Feature<?>> FEATURES =
            DeferredRegister.create(ForgeRegistries.FEATURES, CoffeeWork.MODID);

    public static final RegistryObject<Feature<NoneFeatureConfiguration>> COFFEE_TREE =
            FEATURES.register("coffee_tree", CoffeeTreeFeature::new);

    public static final RegistryObject<Feature<NoneFeatureConfiguration>> BLUEBERRY_BUSH =
            FEATURES.register("blueberry_bush", BlueBerryFeature::new);

    public static final RegistryObject<Feature<NoneFeatureConfiguration>> SODA_ORE =
            FEATURES.register("soda_ore", SodaOreFeature::new);
}
