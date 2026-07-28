package net.langball.coffee;

import net.langball.coffee.advancement.PhonographPlayTrigger;
import net.langball.coffee.init.*;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(CoffeeWork.MODID)
public class CoffeeWork {
    public static final String MODID = "coffeework";
    public static final String NAME = "Coffee Workshop";

    public static ResourceLocation id(String path) {
        return new ResourceLocation(MODID, path);
    }

    public CoffeeWork() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, net.langball.coffee.ModConfig.SPEC);

        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();

        ModBlocks.BLOCKS.register(modBus);
        ModItems.ITEMS.register(modBus);
        ModEffects.EFFECTS.register(modBus);
        ModSounds.SOUNDS.register(modBus);
        ModBlockEntities.BLOCK_ENTITIES.register(modBus);
        ModMenuTypes.MENUS.register(modBus);
        ModCreativeTabs.TABS.register(modBus);
        ModFeatures.FEATURES.register(modBus);
        ModVillagers.POI_TYPES.register(modBus);
        ModVillagers.PROFESSIONS.register(modBus);
        ModRecipeTypes.SERIALIZERS.register(modBus);

        modBus.addListener(this::commonSetup);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            ModVillagers.registerTrades();
            registerIceFuels();
            // Phase 9 Fix7 P1-4: register the custom phonograph
            // trigger.  The advancement JSON references this
            // criterion by ID, so it must be registered before any
            // world loads.
            CriteriaTriggers.register(PhonographPlayTrigger.INSTANCE);
        });
    }

    /** Populate the ice-cream machine's custom cooling-fuel registry. */
    private static void registerIceFuels() {
        var reg = net.langball.coffee.block.entity.IcecreamMachineBlockEntity.COOLING_FUEL;
        reg.put(net.minecraft.world.item.Items.SNOWBALL, 100);
        reg.put(net.minecraft.world.item.Items.ICE, 200);
        reg.put(net.minecraft.world.item.Items.PACKED_ICE, 800);
        reg.put(net.minecraft.world.item.Items.BLUE_ICE, 3600);
    }
}
