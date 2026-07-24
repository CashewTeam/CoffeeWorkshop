package net.langball.coffee.init;

import net.langball.coffee.CoffeeWork;
import net.langball.coffee.block.entity.CoffeeMachineBlockEntity;
import net.langball.coffee.block.entity.GrinderBlockEntity;
import net.langball.coffee.block.entity.IcecreamMachineBlockEntity;
import net.langball.coffee.block.entity.OvenBlockEntity;
import net.langball.coffee.block.entity.RollerBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, CoffeeWork.MODID);

    public static final RegistryObject<BlockEntityType<GrinderBlockEntity>> GRINDER = BLOCK_ENTITIES.register("grinder",
            () -> BlockEntityType.Builder.of(
                    GrinderBlockEntity::new,
                    ModBlocks.GRINDER.get(), ModBlocks.GRINDER_ON.get()
            ).build(null));

    public static final RegistryObject<BlockEntityType<CoffeeMachineBlockEntity>> COFFEE_MACHINE = BLOCK_ENTITIES.register("coffee_machine",
            () -> BlockEntityType.Builder.of(
                    CoffeeMachineBlockEntity::new,
                    ModBlocks.COFFEE_MACHINE.get(), ModBlocks.COFFEE_MACHINE_ON.get()
            ).build(null));

    public static final RegistryObject<BlockEntityType<IcecreamMachineBlockEntity>> ICECREAM_MACHINE = BLOCK_ENTITIES.register("icecream_machine",
            () -> BlockEntityType.Builder.of(
                    IcecreamMachineBlockEntity::new,
                    ModBlocks.ICECREAM_MACHINE.get(), ModBlocks.ICECREAM_MACHINE_ON.get()
            ).build(null));

    public static final RegistryObject<BlockEntityType<RollerBlockEntity>> ROLLER = BLOCK_ENTITIES.register("roller",
            () -> BlockEntityType.Builder.of(
                    RollerBlockEntity::new,
                    ModBlocks.ROLLER.get(), ModBlocks.ROLLER_ON.get()
            ).build(null));

    public static final RegistryObject<BlockEntityType<OvenBlockEntity>> OVEN = BLOCK_ENTITIES.register("oven",
            () -> BlockEntityType.Builder.of(
                    OvenBlockEntity::new,
                    ModBlocks.OVEN.get(), ModBlocks.OVEN_ON.get()
            ).build(null));
}
