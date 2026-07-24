package net.langball.coffee.init;

import net.langball.coffee.CoffeeWork;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModMenuTypes {
    public static final DeferredRegister<MenuType<?>> MENUS =
            DeferredRegister.create(ForgeRegistries.MENU_TYPES, CoffeeWork.MODID);

    // Stub registrations — actual Menu classes will be created in Sprint 3.
    // These use IForgeMenuType.create with a factory that throws for now.

    public static final RegistryObject<MenuType<?>> GRINDER = MENUS.register("grinder",
            () -> IForgeMenuType.create((id, inv, data) -> {
                throw new UnsupportedOperationException("Menu not yet implemented");
            }));

    public static final RegistryObject<MenuType<?>> COFFEE_MACHINE = MENUS.register("coffee_machine",
            () -> IForgeMenuType.create((id, inv, data) -> {
                throw new UnsupportedOperationException("Menu not yet implemented");
            }));

    public static final RegistryObject<MenuType<?>> ICECREAM_MACHINE = MENUS.register("icecream_machine",
            () -> IForgeMenuType.create((id, inv, data) -> {
                throw new UnsupportedOperationException("Menu not yet implemented");
            }));

    public static final RegistryObject<MenuType<?>> ROLLER = MENUS.register("roller",
            () -> IForgeMenuType.create((id, inv, data) -> {
                throw new UnsupportedOperationException("Menu not yet implemented");
            }));

    public static final RegistryObject<MenuType<?>> OVEN = MENUS.register("oven",
            () -> IForgeMenuType.create((id, inv, data) -> {
                throw new UnsupportedOperationException("Menu not yet implemented");
            }));
}
