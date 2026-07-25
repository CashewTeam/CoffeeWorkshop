package net.langball.coffee.client;

import net.langball.coffee.init.ModMenuTypes;
import net.langball.coffee.gui.GuiCoffeeMachine;
import net.langball.coffee.gui.GuiGrinder;
import net.langball.coffee.gui.GuiIcecreamMachine;
import net.langball.coffee.gui.GuiOven;
import net.langball.coffee.gui.GuiRoller;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(
        modid = net.langball.coffee.CoffeeWork.MODID,
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
        });
    }
}
