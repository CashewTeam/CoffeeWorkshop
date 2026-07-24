package net.langball.coffee.init;

import net.langball.coffee.CoffeeWork;
import net.langball.coffee.gui.ContainerCoffeeMachine;
import net.langball.coffee.gui.ContainerGrinder;
import net.langball.coffee.gui.ContainerIcecreamMachine;
import net.langball.coffee.gui.ContainerOven;
import net.langball.coffee.gui.ContainerRoller;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModMenuTypes {
    public static final DeferredRegister<MenuType<?>> MENUS =
            DeferredRegister.create(ForgeRegistries.MENU_TYPES, CoffeeWork.MODID);

    public static final RegistryObject<MenuType<ContainerGrinder>> GRINDER = MENUS.register("grinder",
            () -> IForgeMenuType.create((id, inv, data) -> new ContainerGrinder(id, inv, data.readBlockPos())));

    public static final RegistryObject<MenuType<ContainerCoffeeMachine>> COFFEE_MACHINE = MENUS.register("coffee_machine",
            () -> IForgeMenuType.create((id, inv, data) -> new ContainerCoffeeMachine(id, inv, data.readBlockPos())));

    public static final RegistryObject<MenuType<ContainerIcecreamMachine>> ICECREAM_MACHINE = MENUS.register("icecream_machine",
            () -> IForgeMenuType.create((id, inv, data) -> new ContainerIcecreamMachine(id, inv, data.readBlockPos())));

    public static final RegistryObject<MenuType<ContainerRoller>> ROLLER = MENUS.register("roller",
            () -> IForgeMenuType.create((id, inv, data) -> new ContainerRoller(id, inv, data.readBlockPos())));

    public static final RegistryObject<MenuType<ContainerOven>> OVEN = MENUS.register("oven",
            () -> IForgeMenuType.create((id, inv, data) -> new ContainerOven(id, inv, data.readBlockPos())));
}
