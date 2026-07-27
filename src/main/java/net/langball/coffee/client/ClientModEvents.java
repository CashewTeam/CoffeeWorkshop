package net.langball.coffee.client;

import net.langball.coffee.CoffeeWork;
import net.langball.coffee.client.renderer.DrinkDisplayRenderer;
import net.langball.coffee.init.ModBlockEntities;
import net.langball.coffee.init.ModMenuTypes;
import net.langball.coffee.gui.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.RegisterClientReloadListenersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(
        modid = CoffeeWork.MODID,
        bus = Mod.EventBusSubscriber.Bus.MOD,
        value = Dist.CLIENT
)
public class ClientModEvents {

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            MenuScreens.register(ModMenuTypes.GRINDER.get(), GuiGrinder::new);
            MenuScreens.register(ModMenuTypes.COFFEE_MACHINE.get(), GuiCoffeeMachine::new);
            MenuScreens.register(ModMenuTypes.ICECREAM_MACHINE.get(), GuiIcecreamMachine::new);
            MenuScreens.register(ModMenuTypes.ROLLER.get(), GuiRoller::new);
            MenuScreens.register(ModMenuTypes.OVEN.get(), GuiOven::new);

            ResourceManager rm = Minecraft.getInstance().getResourceManager();
            DrinkDisplayModelRegistry.loadModels(rm);
        });
    }

    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(ModBlockEntities.DRINK_DISPLAY.get(), DrinkDisplayRenderer::new);
    }

    @SubscribeEvent
    public static void onRegisterReloadListeners(RegisterClientReloadListenersEvent event) {
        DrinkDisplayModelRegistry.reset();
        event.registerReloadListener((stage, rm, prep, reload, bg, game) ->
                java.util.concurrent.CompletableFuture.runAsync(() -> {
                    DrinkDisplayModelRegistry.reset();
                    DrinkDisplayModelRegistry.ensureLoaded();
                }, bg).thenCompose(stage::wait));
    }
}
