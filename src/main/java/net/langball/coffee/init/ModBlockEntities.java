package net.langball.coffee.init;

import net.langball.coffee.CoffeeWork;
import net.langball.coffee.block.entity.CoffeeMachineBlockEntity;
import net.langball.coffee.block.entity.CoffeePotBlockEntity;
import net.langball.coffee.block.entity.DrinkDisplayBlockEntity;
import net.langball.coffee.block.entity.GrinderBlockEntity;
import net.langball.coffee.block.entity.IcecreamMachineBlockEntity;
import net.langball.coffee.block.entity.MokaPotBlockEntity;
import net.langball.coffee.block.entity.OvenBlockEntity;
import net.langball.coffee.block.entity.RollerBlockEntity;
import net.langball.coffee.block.entity.SodaMachineBlockEntity;
import net.langball.coffee.block.entity.TurkishCoffeePotBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, CoffeeWork.MODID);

    /** Each machine is now backed by ONE Block (lit state lives on it),
     *  so each BlockEntityType only accepts that single Block. */
    public static final RegistryObject<BlockEntityType<GrinderBlockEntity>> GRINDER = BLOCK_ENTITIES.register("grinder",
            () -> BlockEntityType.Builder.of(
                    GrinderBlockEntity::new,
                    ModBlocks.GRINDER.get()
            ).build(null));

    public static final RegistryObject<BlockEntityType<CoffeeMachineBlockEntity>> COFFEE_MACHINE = BLOCK_ENTITIES.register("coffee_machine",
            () -> BlockEntityType.Builder.of(
                    CoffeeMachineBlockEntity::new,
                    ModBlocks.COFFEE_MACHINE.get()
            ).build(null));

    public static final RegistryObject<BlockEntityType<IcecreamMachineBlockEntity>> ICECREAM_MACHINE = BLOCK_ENTITIES.register("icecream_machine",
            () -> BlockEntityType.Builder.of(
                    IcecreamMachineBlockEntity::new,
                    ModBlocks.ICECREAM_MACHINE.get()
            ).build(null));

    public static final RegistryObject<BlockEntityType<RollerBlockEntity>> ROLLER = BLOCK_ENTITIES.register("roller",
            () -> BlockEntityType.Builder.of(
                    RollerBlockEntity::new,
                    ModBlocks.ROLLER.get()
            ).build(null));

    public static final RegistryObject<BlockEntityType<OvenBlockEntity>> OVEN = BLOCK_ENTITIES.register("oven",
            () -> BlockEntityType.Builder.of(
                    OvenBlockEntity::new,
                    ModBlocks.OVEN.get()
            ).build(null));

    public static final RegistryObject<BlockEntityType<DrinkDisplayBlockEntity>> DRINK_DISPLAY = BLOCK_ENTITIES.register("drink_display",
            () -> BlockEntityType.Builder.of(
                    DrinkDisplayBlockEntity::new,
                    ModBlocks.DRINK_DISPLAY.get()
            ).build(null));

    public static final RegistryObject<BlockEntityType<MokaPotBlockEntity>> MOKA_POT = BLOCK_ENTITIES.register("moka_pot",
            () -> BlockEntityType.Builder.of(
                    MokaPotBlockEntity::new,
                    ModBlocks.MOKA_POT.get()
            ).build(null));

    public static final RegistryObject<BlockEntityType<TurkishCoffeePotBlockEntity>> TURKISH_COFFEE_POT = BLOCK_ENTITIES.register("turkish_coffee_pot",
            () -> BlockEntityType.Builder.of(
                    TurkishCoffeePotBlockEntity::new,
                    ModBlocks.TURKISH_COFFEE_POT.get()
            ).build(null));

    public static final RegistryObject<BlockEntityType<CoffeePotBlockEntity>> COFFEE_POT = BLOCK_ENTITIES.register("coffee_pot",
            () -> BlockEntityType.Builder.of(
                    CoffeePotBlockEntity::new,
                    ModBlocks.COFFEE_POT.get()
            ).build(null));

    public static final RegistryObject<BlockEntityType<SodaMachineBlockEntity>> SODA_MACHINE = BLOCK_ENTITIES.register("soda_machine",
            () -> BlockEntityType.Builder.of(
                    SodaMachineBlockEntity::new,
                    ModBlocks.SODA_MACHINE.get()
            ).build(null));
}
