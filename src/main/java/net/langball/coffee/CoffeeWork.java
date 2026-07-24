package net.langball.coffee;

import net.langball.coffee.gui.GuiCoffeeMachine;
import net.langball.coffee.gui.GuiGrinder;
import net.langball.coffee.gui.GuiIcecreamMachine;
import net.langball.coffee.gui.GuiOven;
import net.langball.coffee.gui.GuiRoller;
import net.langball.coffee.init.*;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(CoffeeWork.MODID)
public class CoffeeWork {
    public static final String MODID = "coffeework";
    public static final String NAME = "Coffee Workshop";

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

        modBus.addListener(this::commonSetup);
        modBus.addListener(this::clientSetup);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            ModVillagers.registerTrades();
        });
    }

    private void clientSetup(final FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            net.minecraft.client.gui.screens.MenuScreens.register(ModMenuTypes.GRINDER.get(), GuiGrinder::new);
            net.minecraft.client.gui.screens.MenuScreens.register(ModMenuTypes.COFFEE_MACHINE.get(), GuiCoffeeMachine::new);
            net.minecraft.client.gui.screens.MenuScreens.register(ModMenuTypes.ICECREAM_MACHINE.get(), GuiIcecreamMachine::new);
            net.minecraft.client.gui.screens.MenuScreens.register(ModMenuTypes.ROLLER.get(), GuiRoller::new);
            net.minecraft.client.gui.screens.MenuScreens.register(ModMenuTypes.OVEN.get(), GuiOven::new);
        });
    }
}
