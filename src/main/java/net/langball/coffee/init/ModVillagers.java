package net.langball.coffee.init;

import com.google.common.collect.ImmutableSet;
import net.langball.coffee.CoffeeWork;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.List;

public class ModVillagers {
    public static final DeferredRegister<PoiType> POI_TYPES =
            DeferredRegister.create(ForgeRegistries.POI_TYPES, CoffeeWork.MODID);
    public static final DeferredRegister<VillagerProfession> PROFESSIONS =
            DeferredRegister.create(ForgeRegistries.VILLAGER_PROFESSIONS, CoffeeWork.MODID);

    // ========================== POI Types ==========================

    /** Coffee machine: workstation for both barista and materials trader */
    public static final RegistryObject<PoiType> COFFEE_POI = POI_TYPES.register("coffee_poi",
            () -> new PoiType(ImmutableSet.of(
                    ModBlocks.COFFEE_MACHINE.get().defaultBlockState(),
                    ModBlocks.COFFEE_MACHINE_ON.get().defaultBlockState()),
                    1, 1));

    /** Oven: workstation for the food trader */
    public static final RegistryObject<PoiType> OVEN_POI = POI_TYPES.register("oven_poi",
            () -> new PoiType(ImmutableSet.of(
                    ModBlocks.OVEN.get().defaultBlockState(),
                    ModBlocks.OVEN_ON.get().defaultBlockState()),
                    1, 1));

    // ========================== Professions ==========================

    /** Sells coffee drinks & coffee-related materials (mapped from old VillagerCoffee) */
    public static final RegistryObject<VillagerProfession> COFFEE_BARISTA = PROFESSIONS.register("coffee_barista",
            () -> new VillagerProfession("coffee_barista",
                    holder -> holder.value() == COFFEE_POI.get(),
                    holder -> holder.value() == COFFEE_POI.get(),
                    ImmutableSet.of(), ImmutableSet.of(),
                    SoundEvents.VILLAGER_WORK_WEAPONSMITH));

    /** Trades instant coffee items (mapped from old VillagerCoffee2) */
    public static final RegistryObject<VillagerProfession> COFFEE_MATERIALS_TRADER = PROFESSIONS.register("coffee_materials_trader",
            () -> new VillagerProfession("coffee_materials_trader",
                    holder -> holder.value() == COFFEE_POI.get(),
                    holder -> holder.value() == COFFEE_POI.get(),
                    ImmutableSet.of(), ImmutableSet.of(),
                    SoundEvents.VILLAGER_WORK_WEAPONSMITH));

    /** Trades food, cakes, and baking supplies (mapped from old VillagerFood) */
    public static final RegistryObject<VillagerProfession> FOOD_TRADER = PROFESSIONS.register("food_trader",
            () -> new VillagerProfession("food_trader",
                    holder -> holder.value() == OVEN_POI.get(),
                    holder -> holder.value() == OVEN_POI.get(),
                    ImmutableSet.of(), ImmutableSet.of(),
                    SoundEvents.VILLAGER_WORK_BUTCHER));

    // ========================== Trade Registration ==========================

    /**
     * Called from {@link CoffeeWork#commonSetup(FMLCommonSetupEvent)} inside an
     * {@code event.enqueueWork()} wrapper.
     */
    public static void registerTrades() {
        // ---- COFFEE BARISTA (old VillagerCoffee + coffee drinks) ----
        //   Level 1: sells coffee beans/powders          (old SimpleBuy: materials[0/1/2])
        //   Level 2: buys powders in bulk, sells syrup    (old SimpleSell + SimpleBuy)
        //   Level 3: sells spices, buys cocoa powder      (old materials[7], materials[2])
        List<VillagerTrades.ItemListing>[] baristaTrades = new List[3];
        baristaTrades[0] = List.of(
                // Coffee drinks for emeralds
                new VillagerTrades.ItemsForEmeralds(ModItems.COFFEE_AMERICANO.get(), 2, 1, 12, 2),
                new VillagerTrades.ItemsForEmeralds(ModItems.ESPRESSO.get(), 3, 1, 12, 2),
                // Materials for emeralds  (old: SimpleBuy materials[0] x2 for 3-7e)
                new VillagerTrades.ItemsForEmeralds(ModItems.COFFEE_BEAN.get(), 4, 2, 16, 1),
                // Materials for emeralds  (old: SimpleBuy materials[1] x4 for 4-9e)
                new VillagerTrades.ItemsForEmeralds(ModItems.COFFEE_POWDER.get(), 6, 4, 16, 1),
                // Materials for emeralds  (old: SimpleBuy materials[2] x4 for 6-11e)
                new VillagerTrades.ItemsForEmeralds(ModItems.COCOA_POWDER.get(), 8, 4, 16, 1)
        );
        baristaTrades[1] = List.of(
                // Buy powders in bulk  (old: SimpleSell materials[1] x16 for 2-6e)
                new VillagerTrades.EmeraldsForItems(ModItems.COFFEE_POWDER.get(), 4, 16, 12, 5),
                // Buy cocoa powder in bulk  (old: SimpleSell materials[2] x16 for 2-6e)
                new VillagerTrades.EmeraldsForItems(ModItems.COCOA_POWDER.get(), 4, 16, 12, 5),
                // Sell syrup  (old: SimpleBuy syrup x16 for 2-6e)
                new VillagerTrades.ItemsForEmeralds(ModItems.SYRUP_EMPTY.get(), 4, 16, 12, 5)
        );
        baristaTrades[2] = List.of(
                // Sell spices  (old: SimpleBuy materials[7] x16 for 2-6e)
                new VillagerTrades.ItemsForEmeralds(ModItems.SPICES.get(), 4, 16, 12, 10),
                // Buy cocoa powder  (old: SimpleSell materials[2] x8 for 2-6e)
                new VillagerTrades.EmeraldsForItems(ModItems.COCOA_POWDER.get(), 2, 8, 12, 10)
        );
        VillagerTrades.TRADES.put(ModVillagers.COFFEE_BARISTA.get(), baristaTrades);

        // ---- COFFEE MATERIALS TRADER (old VillagerCoffee2) ----
        //   Level 1: sells instant sticks, buys beans
        //   Level 2: buys/sells instant boxes, sells cups
        //   Level 3: buys instant boxes in bulk
        //   (old: coffee_instant_stick, coffee_instant_box, coffee_instant_cup_unopen)
        List<VillagerTrades.ItemListing>[] materialsTrades = new List[3];
        materialsTrades[0] = List.of(
                new VillagerTrades.ItemsForEmeralds(ModItems.COFFEE_INSTANT_STICK.get(), 2, 3, 12, 2),
                new VillagerTrades.EmeraldsForItems(ModItems.COFFEE_BEAN.get(), 1, 4, 16, 1)
        );
        materialsTrades[1] = List.of(
                // Player sells instant box for emeralds  (old: SimpleSell box x1 for 4-6e)
                new VillagerTrades.EmeraldsForItems(ModItems.COFFEE_INSTANT_BOX.get(), 5, 1, 8, 5),
                // Player buys instant box with emeralds  (old: SimpleBuy box x1 for 7-9e)
                new VillagerTrades.ItemsForEmeralds(ModItems.COFFEE_INSTANT_BOX.get(), 8, 1, 8, 5),
                // Player buys instant drink cup  (old: SimpleBuy cup x1 for 2-3e)
                new VillagerTrades.ItemsForEmeralds(ModItems.COFFEE_INSTANT.get(), 2, 1, 12, 5)
        );
        materialsTrades[2] = List.of(
                // Player sells 16 instant boxes  (old: SimpleSell box x16 for 24-48e)
                new VillagerTrades.EmeraldsForItems(ModItems.COFFEE_INSTANT_BOX.get(), 32, 16, 3, 15)
        );
        VillagerTrades.TRADES.put(ModVillagers.COFFEE_MATERIALS_TRADER.get(), materialsTrades);

        // ---- FOOD TRADER (old VillagerFood) ----
        //   Level 1: buys cake slices, sells flour/blueberries/cocoa beans
        //   Level 2: sells brownies, buys dough/cheese
        //   Level 3: sells cake molds
        List<VillagerTrades.ItemListing>[] foodTrades = new List[3];
        foodTrades[0] = List.of(
                // Player sells cake sponge slice  (old: SimpleSell dessert_1[2] x8 for 4-12e)
                new VillagerTrades.EmeraldsForItems(ModItems.CAKE_SPONGE_SLICE.get(), 1, 8, 12, 2),
                // Player buys flour  (old: SimpleBuy materials[11] x16 for 3-5e)
                new VillagerTrades.ItemsForEmeralds(ModItems.FLOUR.get(), 1, 8, 16, 1),
                // Player buys blueberries  (old: SimpleBuy materialFood[5] x4 for 6-8e)
                new VillagerTrades.ItemsForEmeralds(ModItems.BLUEBERRY.get(), 2, 4, 16, 1),
                // Player buys cocoa beans  (old: SimpleBuy materials[4] x4 for 3-5e)
                new VillagerTrades.ItemsForEmeralds(ModItems.COCOA_BEAN.get(), 1, 4, 16, 1)
        );
        foodTrades[1] = List.of(
                // Player sells brownie  (old: SimpleSell muffin[9] x8 for 8-14e)
                new VillagerTrades.EmeraldsForItems(ModItems.BROWNIE.get(), 2, 8, 12, 5),
                // Player buys dough  (old: SimpleBuy materials[10] x8 for 6-7e)
                new VillagerTrades.ItemsForEmeralds(ModItems.DOUGH.get(), 1, 8, 16, 5),
                // Player buys cheese  (old: SimpleBuy materialFood[9] x8 for 8-10e)
                new VillagerTrades.ItemsForEmeralds(ModItems.CHEESE.get(), 1, 8, 12, 5)
        );
        foodTrades[2] = List.of(
                // Cake molds  (old: SimpleBuy cake_model x1 for 4-8e)
                new VillagerTrades.ItemsForEmeralds(ModItems.CAKE_MODEL.get(), 5, 1, 8, 10),
                new VillagerTrades.ItemsForEmeralds(ModItems.CAKE_MODEL_SQUARE.get(), 5, 1, 8, 10),
                new VillagerTrades.ItemsForEmeralds(ModItems.CAKE_MODEL_PLATE.get(), 5, 1, 8, 10)
        );
        VillagerTrades.TRADES.put(ModVillagers.FOOD_TRADER.get(), foodTrades);
    }
}
