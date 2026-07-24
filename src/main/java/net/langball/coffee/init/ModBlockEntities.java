package net.langball.coffee.init;

import net.langball.coffee.CoffeeWork;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, CoffeeWork.MODID);

    // Stub registrations — actual BlockEntity classes will be created in Sprint 3.
    // These are temporary and will be replaced with proper BlockEntityType.Builder.of(...)
    // referencing the actual BlockEntity subclasses.

    public static final RegistryObject<BlockEntityType<?>> GRINDER = BLOCK_ENTITIES.register("grinder",
            () -> BlockEntityType.Builder.of(
                    (pos, state) -> {
                        throw new UnsupportedOperationException("BlockEntity not yet implemented");
                    },
                    ModBlocks.GRINDER.get(), ModBlocks.GRINDER_ON.get()
            ).build(null));

    public static final RegistryObject<BlockEntityType<?>> COFFEE_MACHINE = BLOCK_ENTITIES.register("coffee_machine",
            () -> BlockEntityType.Builder.of(
                    (pos, state) -> {
                        throw new UnsupportedOperationException("BlockEntity not yet implemented");
                    },
                    ModBlocks.COFFEE_MACHINE.get(), ModBlocks.COFFEE_MACHINE_ON.get()
            ).build(null));

    public static final RegistryObject<BlockEntityType<?>> ICECREAM_MACHINE = BLOCK_ENTITIES.register("icecream_machine",
            () -> BlockEntityType.Builder.of(
                    (pos, state) -> {
                        throw new UnsupportedOperationException("BlockEntity not yet implemented");
                    },
                    ModBlocks.ICECREAM_MACHINE.get(), ModBlocks.ICECREAM_MACHINE_ON.get()
            ).build(null));

    public static final RegistryObject<BlockEntityType<?>> ROLLER = BLOCK_ENTITIES.register("roller",
            () -> BlockEntityType.Builder.of(
                    (pos, state) -> {
                        throw new UnsupportedOperationException("BlockEntity not yet implemented");
                    },
                    ModBlocks.ROLLER.get(), ModBlocks.ROLLER_ON.get()
            ).build(null));

    public static final RegistryObject<BlockEntityType<?>> OVEN = BLOCK_ENTITIES.register("oven",
            () -> BlockEntityType.Builder.of(
                    (pos, state) -> {
                        throw new UnsupportedOperationException("BlockEntity not yet implemented");
                    },
                    ModBlocks.OVEN.get(), ModBlocks.OVEN_ON.get()
            ).build(null));
}
