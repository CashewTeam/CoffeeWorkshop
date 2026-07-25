package net.langball.coffee;

import net.langball.coffee.init.*;
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
        });
    }

    /** Populate the ice-cream machine's custom fuel registry. */
    private static void registerIceFuels() {
        var reg = net.langball.coffee.block.entity.IcecreamMachineBlockEntity.ICE_FUEL_REGISTRY;
        reg.put(new net.minecraft.world.item.ItemStack(net.minecraft.world.item.Items.SNOWBALL), 100);
        reg.put(new net.minecraft.world.item.ItemStack(net.minecraft.world.level.block.Blocks.ICE), 200);
        reg.put(new net.minecraft.world.item.ItemStack(net.minecraft.world.level.block.Blocks.PACKED_ICE), 200);
        reg.put(new net.minecraft.world.item.ItemStack(net.minecraft.world.level.block.Blocks.BLUE_ICE), 400);
    }
}
