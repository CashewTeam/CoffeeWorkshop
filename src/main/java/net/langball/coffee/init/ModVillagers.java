package net.langball.coffee.init;

import com.google.common.collect.ImmutableSet;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.langball.coffee.CoffeeWork;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModVillagers {
    public static final DeferredRegister<PoiType> POI_TYPES =
            DeferredRegister.create(ForgeRegistries.POI_TYPES, CoffeeWork.MODID);
    public static final DeferredRegister<VillagerProfession> PROFESSIONS =
            DeferredRegister.create(ForgeRegistries.VILLAGER_PROFESSIONS, CoffeeWork.MODID);

    // ========================== POI Types ==========================

    /** Coffee machine: workstation for both barista and materials trader */
    public static final RegistryObject<PoiType> COFFEE_POI = POI_TYPES.register("coffee_poi",
            () -> new PoiType(
                    ImmutableSet.copyOf(
                            ModBlocks.COFFEE_MACHINE.get().getStateDefinition().getPossibleStates()),
                    1, 1));

    /** Oven: workstation for the food trader */
    public static final RegistryObject<PoiType> OVEN_POI = POI_TYPES.register("oven_poi",
            () -> new PoiType(
                    ImmutableSet.copyOf(
                            ModBlocks.OVEN.get().getStateDefinition().getPossibleStates()),
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

    // ========================================================================
    // Custom VillagerTrades.ItemListing implementations
    // (Vanilla classes are package-private, so we provide our own.)
    // ========================================================================

    /**
     * Sells an item for emeralds. Player pays emeralds, receives the item.
     * Equivalent to vanilla {@code VillagerTrades.ItemsForEmeralds}.
     */
    public static class ItemsForEmeralds implements VillagerTrades.ItemListing {
        private final ItemStack itemStack;
        private final int emeraldCost;
        private final int maxUses;
        private final int villagerXp;
        private final float priceMultiplier;

        public ItemsForEmeralds(Item item, int emeraldCost, int resultCount, int maxUses, int villagerXp) {
            this(new ItemStack(item, resultCount), emeraldCost, maxUses, villagerXp, 0.05F);
        }

        public ItemsForEmeralds(ItemStack stack, int emeraldCost, int maxUses, int villagerXp) {
            this(stack, emeraldCost, maxUses, villagerXp, 0.05F);
        }

        public ItemsForEmeralds(ItemStack stack, int emeraldCost, int maxUses, int villagerXp, float priceMultiplier) {
            this.itemStack = stack;
            this.emeraldCost = emeraldCost;
            this.maxUses = maxUses;
            this.villagerXp = villagerXp;
            this.priceMultiplier = priceMultiplier;
        }

        @Override
        public MerchantOffer getOffer(Entity entity, RandomSource random) {
            return new MerchantOffer(new ItemStack(Items.EMERALD, emeraldCost), itemStack, maxUses, villagerXp, priceMultiplier);
        }
    }

    /**
     * Buys an item for emeralds. Player gives items, receives emeralds.
     * Equivalent to vanilla {@code VillagerTrades.EmeraldForItems}.
     */
    public static class EmeraldsForItems implements VillagerTrades.ItemListing {
        private final ItemStack buyItem;
        private final int emeraldCount;
        private final int maxUses;
        private final int villagerXp;
        private final float priceMultiplier;

        public EmeraldsForItems(ItemLike item, int itemCount, int emeraldCount, int maxUses, int villagerXp) {
            this.buyItem = new ItemStack(item, itemCount);
            this.emeraldCount = emeraldCount;
            this.maxUses = maxUses;
            this.villagerXp = villagerXp;
            this.priceMultiplier = 0.05F;
        }

        @Override
        public MerchantOffer getOffer(Entity entity, RandomSource random) {
            return new MerchantOffer(buyItem, new ItemStack(Items.EMERALD, emeraldCount), maxUses, villagerXp, priceMultiplier);
        }
    }

    // ========================== Trade Registration ==========================

    /**
     * Called from {@link CoffeeWork#commonSetup(FMLCommonSetupEvent)} inside an
     * {@code event.enqueueWork()} wrapper.
     */
    public static void registerTrades() {
        // ---- COFFEE BARISTA (sells coffee drinks & related materials) ----
        Int2ObjectMap<VillagerTrades.ItemListing[]> baristaTrades = new Int2ObjectOpenHashMap<>();
        baristaTrades.put(1, new VillagerTrades.ItemListing[]{
                new ItemsForEmeralds(ModItems.COFFEE_AMERICANO.get(), 2, 1, 12, 2),
                new ItemsForEmeralds(ModItems.ESPRESSO.get(), 3, 1, 12, 2),
                new ItemsForEmeralds(ModItems.COFFEE_LATTE.get(), 2, 1, 12, 2),
                new ItemsForEmeralds(ModItems.COFFEE_CAPPUCCINO.get(), 3, 1, 12, 2),
                new ItemsForEmeralds(ModItems.COFFEE_BEAN.get(), 8, 2, 16, 1),
                new ItemsForEmeralds(ModItems.COFFEE_POWDER.get(), 6, 4, 16, 1),
                new ItemsForEmeralds(ModItems.COCOA_POWDER.get(), 8, 4, 16, 1)
        });
        baristaTrades.put(2, new VillagerTrades.ItemListing[]{
                new EmeraldsForItems(ModItems.COFFEE_POWDER.get(), 4, 4, 12, 5),
                new EmeraldsForItems(ModItems.COCOA_POWDER.get(), 4, 6, 12, 5),
                new ItemsForEmeralds(ModItems.COFFEE_MACCHIATO.get(), 3, 1, 12, 5),
                new ItemsForEmeralds(ModItems.COFFEE_MOCHACCINO.get(), 3, 1, 12, 5),
                new ItemsForEmeralds(ModItems.COFFEE_GREEN_TEA.get(), 2, 1, 12, 5),
                new ItemsForEmeralds(ModItems.COFFEE_BLACK_TEA.get(), 2, 1, 12, 5),
                new ItemsForEmeralds(ModItems.COFFEE_MILK_TEA.get(), 2, 1, 12, 5),
                new ItemsForEmeralds(ModItems.SYRUP_EMPTY.get(), 4, 16, 12, 5)
        });
        baristaTrades.put(3, new VillagerTrades.ItemListing[]{
                new ItemsForEmeralds(ModItems.COFFEE_LATTE_CARAMEL.get(), 4, 1, 8, 10),
                new ItemsForEmeralds(ModItems.COFFEE_LATTE_VANILLA.get(), 4, 1, 8, 10),
                new ItemsForEmeralds(ModItems.COFFEE_COLDBREW.get(), 3, 1, 8, 10),
                new ItemsForEmeralds(ModItems.COCOA.get(), 2, 1, 8, 10),
                new ItemsForEmeralds(ModItems.SPICES.get(), 4, 16, 12, 10),
                new EmeraldsForItems(ModItems.COCOA_POWDER.get(), 2, 4, 12, 10)
        });
        VillagerTrades.TRADES.put(ModVillagers.COFFEE_BARISTA.get(), baristaTrades);

        // ---- COFFEE MATERIALS TRADER (old VillagerCoffee2) ----
        Int2ObjectMap<VillagerTrades.ItemListing[]> materialsTrades = new Int2ObjectOpenHashMap<>();
        materialsTrades.put(1, new VillagerTrades.ItemListing[]{
                new ItemsForEmeralds(ModItems.COFFEE_INSTANT_STICK.get(), 2, 3, 12, 2),
                new EmeraldsForItems(ModItems.COFFEE_BEAN.get(), 1, 2, 16, 1),
                new ItemsForEmeralds(ModItems.LEMON.get(), 1, 2, 12, 1)
        });
        materialsTrades.put(2, new VillagerTrades.ItemListing[]{
                new EmeraldsForItems(ModItems.COFFEE_INSTANT_BOX.get(), 5, 1, 8, 5),
                new ItemsForEmeralds(ModItems.COFFEE_INSTANT_BOX.get(), 8, 1, 8, 5),
                new ItemsForEmeralds(ModItems.COFFEE_INSTANT.get(), 2, 1, 12, 5)
        });
        materialsTrades.put(3, new VillagerTrades.ItemListing[]{
                new EmeraldsForItems(ModItems.COFFEE_INSTANT_BOX.get(), 32, 16, 3, 15)
        });
        VillagerTrades.TRADES.put(ModVillagers.COFFEE_MATERIALS_TRADER.get(), materialsTrades);

        // ---- FOOD TRADER (old VillagerFood) ----
        Int2ObjectMap<VillagerTrades.ItemListing[]> foodTrades = new Int2ObjectOpenHashMap<>();
        foodTrades.put(1, new VillagerTrades.ItemListing[]{
                new EmeraldsForItems(ModItems.CAKE_SPONGE_SLICE.get(), 1, 8, 12, 2),
                new ItemsForEmeralds(ModItems.FLOUR.get(), 1, 8, 16, 1),
                new ItemsForEmeralds(ModItems.BLUEBERRY.get(), 2, 4, 16, 1),
                new ItemsForEmeralds(ModItems.COCOA_BEAN.get(), 1, 4, 16, 1)
        });
        foodTrades.put(2, new VillagerTrades.ItemListing[]{
                new EmeraldsForItems(ModItems.BROWNIE.get(), 2, 8, 12, 5),
                new ItemsForEmeralds(ModItems.DOUGH.get(), 1, 8, 16, 5),
                new ItemsForEmeralds(ModItems.CHEESE.get(), 1, 8, 12, 5)
        });
        foodTrades.put(3, new VillagerTrades.ItemListing[]{
                new ItemsForEmeralds(ModItems.CAKE_MODEL.get(), 5, 1, 8, 10),
                new ItemsForEmeralds(ModItems.CAKE_MODEL_SQUARE.get(), 5, 1, 8, 10),
                new ItemsForEmeralds(ModItems.CAKE_MODEL_PLATE.get(), 5, 1, 8, 10)
        });
        VillagerTrades.TRADES.put(ModVillagers.FOOD_TRADER.get(), foodTrades);
    }
}
