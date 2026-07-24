package net.langball.coffee.init;

import net.langball.coffee.CoffeeWork;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModSounds {
    public static final DeferredRegister<SoundEvent> SOUNDS =
            DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, CoffeeWork.MODID);

    public static final RegistryObject<SoundEvent> RECORD_KUSA_NOSHI_TO_NE = SOUNDS.register("records.kusa_noshi_to_ne",
            () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(CoffeeWork.MODID, "records.kusa_noshi_to_ne")));

    public static final RegistryObject<SoundEvent> RECORD_LAZY_LADY_KAGUYA = SOUNDS.register("records.lazy_lady_kaguya",
            () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(CoffeeWork.MODID, "records.lazy_lady_kaguya")));

    public static final RegistryObject<SoundEvent> RECORD_THE_GRIMOIRE_OF_MARISA = SOUNDS.register("records.the_grimoire_of_marisa",
            () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(CoffeeWork.MODID, "records.the_grimoire_of_marisa")));
}
