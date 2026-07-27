package net.langball.coffee.init;

import net.langball.coffee.CoffeeWork;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class ModCreativeTabs {
    public static final DeferredRegister<CreativeModeTab> TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, CoffeeWork.MODID);

    public static final RegistryObject<CreativeModeTab> COFFEE_TAB = TABS.register("coffee_workshop",
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.coffee_workshop"))
                    .icon(() -> new ItemStack(ModItems.COLD_BREW_POT.get()))
                    .displayItems((params, output) -> {
                        // Machines
                        output.accept(ModItems.GRINDER.get());
                        output.accept(ModItems.COFFEE_MACHINE.get());
                        output.accept(ModItems.ICECREAM_MACHINE.get());
                        output.accept(ModItems.ROLLER.get());
                        output.accept(ModItems.OVEN.get());
                        output.accept(ModItems.COLD_BREW_POT.get());
                        output.accept(ModItems.MOKA_POT_ITEM.get());
                        output.accept(ModItems.MOKA_BOTTOM.get());
                        output.accept(ModItems.MOKA_TOP.get());
                        output.accept(ModItems.TURKISH_COFFEE_POT_ITEM.get());
                        output.accept(ModItems.COFFEE_POT_ITEM.get());
                        output.accept(ModItems.SODA_MACHINE_ITEM.get());
                        output.accept(ModItems.PHONOGRAPH_ITEM.get());
                        output.accept(ModItems.STONE_BAR_COUNTER.get());
                        output.accept(ModItems.WOODEN_BAR_COUNTER.get());

                        // Plants
                        output.accept(ModItems.COFFEE_TREE_ITEM.get());
                        output.accept(ModItems.BLUEBERRY_BUSH_ITEM.get());
                        output.accept(ModItems.COFFEE_SEEDS.get());
                        output.accept(ModItems.VANILLA_SEEDS.get());

                        // Ores
                        output.accept(ModItems.SODA_ORE.get());

                        // Ingredients
                        output.accept(ModItems.COFFEE_BEAN_RAW.get());
                        output.accept(ModItems.COFFEE_BEAN.get());
                        output.accept(ModItems.COFFEE_POWDER.get());
                        output.accept(ModItems.COCOA_BEAN.get());
                        output.accept(ModItems.COCOA_POWDER.get());
                        output.accept(ModItems.COCOA_BATTER.get());
                        output.accept(ModItems.BAG_CLOTH.get());
                        output.accept(ModItems.ICE_SLAG.get());
                        output.accept(ModItems.YEAST.get());
                        output.accept(ModItems.PLATE_IRON.get());
                        output.accept(ModItems.SPICES.get());
                        output.accept(ModItems.GELATIN.get());
                        output.accept(ModItems.SODA.get());
                        output.accept(ModItems.FLOUR.get());
                        output.accept(ModItems.DOUGH.get());
                        output.accept(ModItems.DOUGH_PASTRY.get());
                        output.accept(ModItems.DOUGH_COOKIE.get());
                        output.accept(ModItems.DOUGH_GINGER.get());
                        output.accept(ModItems.DOUGH_BREAD.get());
                        output.accept(ModItems.DOUGH_BREAD_ROUND.get());
                        output.accept(ModItems.DOUGH_BAGUETTE.get());
                        output.accept(ModItems.DOUGH_BAGEL.get());
                        output.accept(ModItems.DOUGH_TOAST.get());
                        output.accept(ModItems.PLATE_DOUGH.get());
                        output.accept(ModItems.PLATE_DOUGH_PASTRY.get());
                        output.accept(ModItems.PLATE_DOUGH_GINGER.get());
                        output.accept(ModItems.EMPTY_COLDBREW_POT.get());

                        // Foods
                        output.accept(ModItems.BREAD_ROUND.get());
                        output.accept(ModItems.BAGUETTE.get());
                        output.accept(ModItems.BAGEL.get());
                        output.accept(ModItems.TOAST.get());
                        output.accept(ModItems.BUTTER.get());
                        output.accept(ModItems.CHEESE.get());
                        output.accept(ModItems.BLUEBERRY.get());
                        output.accept(ModItems.CHOCOLATE_BAR.get());
                        output.accept(ModItems.CHOCOLATE_CHIP.get());
                        output.accept(ModItems.BROWNIE_RAW.get());
                        output.accept(ModItems.BROWNIE_MODEL.get());
                        output.accept(ModItems.BROWNIE.get());
                        output.accept(ModItems.FIELD_RATION.get());
                        output.accept(ModItems.CARAMEL_APPLE.get());
                        output.accept(ModItems.HARDTACK.get());
                        output.accept(ModItems.COOKIE_BLACK.get());
                        output.accept(ModItems.COOKIE_OREO.get());
                        output.accept(ModItems.MARSHMALLOW.get());
                        output.accept(ModItems.MARSHMALLOW_ROAST.get());
                        output.accept(ModItems.MARSHMALLOW_CHOCOLATE.get());
                        output.accept(ModItems.SMORE.get());

                        // Bags
                        output.accept(ModItems.BAG_COFFEE.get());
                        output.accept(ModItems.BAG_COFFEE_RAW.get());
                        output.accept(ModItems.BAG_COCOA.get());
                        output.accept(ModItems.BAG_COCOA_POWDER.get());
                        output.accept(ModItems.BAG_FLOUR.get());
                        output.accept(ModItems.BAG_COFFEE_POWDER.get());
                        output.accept(ModItems.BAG_SUGAR.get());
                        output.accept(ModItems.DOUBLE_BAG_COFFEE.get());
                        output.accept(ModItems.DOUBLE_BAG_COFFEE_RAW.get());
                        output.accept(ModItems.DOUBLE_BAG_COCOA.get());
                        output.accept(ModItems.DOUBLE_BAG_COCOA_POWDER.get());
                        output.accept(ModItems.DOUBLE_BAG_FLOUR.get());
                        output.accept(ModItems.DOUBLE_BAG_COFFEE_POWDER.get());
                        output.accept(ModItems.DOUBLE_BAG_SUGAR.get());

                        // Cakes
                        output.accept(ModItems.CAKE_SPONGE.get());
                        output.accept(ModItems.CAKE_SPONGE_CHOCOLATE.get());
                        output.accept(ModItems.CAKE_SPONGE_COFFEE.get());
                        output.accept(ModItems.CAKE_SPONGE_PUMPKIN.get());
                        output.accept(ModItems.CAKE_SPONGE_CARROT.get());
                        output.accept(ModItems.CAKE_SPONGE_REDVELVET.get());
                        output.accept(ModItems.CAKE_SPONGE_LEMON.get());
                        output.accept(ModItems.CAKE_SPONGE_TEA.get());
                        output.accept(ModItems.CAKE_SPONGE_BERRY.get());
                        output.accept(ModItems.CAKE_COFFEE.get());
                        output.accept(ModItems.CAKE_HARVEST.get());
                        output.accept(ModItems.CAKE_LEMON.get());
                        output.accept(ModItems.CAKE_TEA.get());
                        output.accept(ModItems.CAKE_BERRY.get());
                        output.accept(ModItems.CAKE_CHEESE.get());
                        output.accept(ModItems.CAKE_SCHWARZWALD.get());
                        output.accept(ModItems.CAKE_REDVELVET.get());
                        output.accept(ModItems.TIRAMISU.get());
                        output.accept(ModItems.MOUSSE_BERRY.get());
                        output.accept(ModItems.MOUSSE_LEMON.get());
                        output.accept(ModItems.MOUSSE_CHOCOLATE.get());
                        output.accept(ModItems.MOUSSE_COFFEE.get());
                        output.accept(ModItems.CAKE_CARROT.get());

                        // Phase 5.4: Cake intermediates
                        output.accept(ModItems.CAKE_SPONGE_RAW.get());
                        output.accept(ModItems.CAKE_SPONGE_MODEL.get());
                        output.accept(ModItems.CAKE_CHEESE_RAW.get());
                        output.accept(ModItems.CAKE_CHEESE_MODEL.get());
                        output.accept(ModItems.CAKE_SPONGE_BERRY_RAW.get());
                        output.accept(ModItems.CAKE_SPONGE_BERRY_MODEL.get());
                        output.accept(ModItems.CAKE_SPONGE_CHOCOLATE_RAW.get());
                        output.accept(ModItems.CAKE_SPONGE_CHOCOLATE_MODEL.get());
                        output.accept(ModItems.CAKE_SPONGE_COFFEE_RAW.get());
                        output.accept(ModItems.CAKE_SPONGE_COFFEE_MODEL.get());
                        output.accept(ModItems.CAKE_SPONGE_PUMPKIN_RAW.get());
                        output.accept(ModItems.CAKE_SPONGE_PUMPKIN_MODEL.get());
                        output.accept(ModItems.CAKE_SPONGE_CARROT_RAW.get());
                        output.accept(ModItems.CAKE_SPONGE_CARROT_MODEL.get());
                        output.accept(ModItems.CAKE_SPONGE_REDVELVET_RAW.get());
                        output.accept(ModItems.CAKE_SPONGE_REDVELVET_MODEL.get());
                        output.accept(ModItems.CAKE_SPONGE_LEMON_RAW.get());
                        output.accept(ModItems.CAKE_SPONGE_LEMON_MODEL.get());
                        output.accept(ModItems.CAKE_SPONGE_TEA_RAW.get());
                        output.accept(ModItems.CAKE_SPONGE_TEA_MODEL.get());
                        output.accept(ModItems.MOUSSE_BERRY_RAW.get());
                        output.accept(ModItems.MOUSSE_CHOCOLATE_RAW.get());
                        output.accept(ModItems.MOUSSE_COFFEE_RAW.get());
                        output.accept(ModItems.MOUSSE_LEMON_RAW.get());

                        // Phase 5.4: Cake plate intermediates
                        output.accept(ModItems.CAKE_SPONGE_PLATE_RAW.get());
                        output.accept(ModItems.CAKE_SPONGE_PLATE_MODEL.get());
                        output.accept(ModItems.CAKE_SPONGE_BASE.get());
                        output.accept(ModItems.CAKE_SPONGE_BERRY_PLATE_RAW.get());
                        output.accept(ModItems.CAKE_SPONGE_BERRY_PLATE_MODEL.get());
                        output.accept(ModItems.CAKE_SPONGE_BERRY_BASE.get());
                        output.accept(ModItems.CAKE_SPONGE_CARROT_PLATE_RAW.get());
                        output.accept(ModItems.CAKE_SPONGE_CARROT_PLATE_MODEL.get());
                        output.accept(ModItems.CAKE_SPONGE_CARROT_BASE.get());
                        output.accept(ModItems.CAKE_SPONGE_CHOCOLATE_PLATE_RAW.get());
                        output.accept(ModItems.CAKE_SPONGE_CHOCOLATE_PLATE_MODEL.get());
                        output.accept(ModItems.CAKE_SPONGE_CHOCOLATE_BASE.get());
                        output.accept(ModItems.CAKE_SPONGE_COFFEE_PLATE_RAW.get());
                        output.accept(ModItems.CAKE_SPONGE_COFFEE_PLATE_MODEL.get());
                        output.accept(ModItems.CAKE_SPONGE_COFFEE_BASE.get());
                        output.accept(ModItems.CAKE_SPONGE_LEMON_PLATE_RAW.get());
                        output.accept(ModItems.CAKE_SPONGE_LEMON_PLATE_MODEL.get());
                        output.accept(ModItems.CAKE_SPONGE_LEMON_BASE.get());
                        output.accept(ModItems.CAKE_SPONGE_PUMPKIN_PLATE_RAW.get());
                        output.accept(ModItems.CAKE_SPONGE_PUMPKIN_PLATE_MODEL.get());
                        output.accept(ModItems.CAKE_SPONGE_PUMPKIN_BASE.get());
                        output.accept(ModItems.CAKE_SPONGE_REDVELVET_PLATE_RAW.get());
                        output.accept(ModItems.CAKE_SPONGE_REDVELVET_PLATE_MODEL.get());
                        output.accept(ModItems.CAKE_SPONGE_REDVELVET_BASE.get());
                        output.accept(ModItems.CAKE_SPONGE_TEA_PLATE_RAW.get());
                        output.accept(ModItems.CAKE_SPONGE_TEA_PLATE_MODEL.get());
                        output.accept(ModItems.CAKE_SPONGE_TEA_BASE.get());

                        // Phase 5.4: Jiggy intermediates (generic + 8 flavored)
                        output.accept(ModItems.JIGGY_CAKE_RAW.get());
                        output.accept(ModItems.JIGGY_CAKE_MODEL.get());
                        output.accept(ModItems.JIGGY_CAKE_BERRY_RAW.get());
                        output.accept(ModItems.JIGGY_CAKE_BERRY_MODEL.get());
                        output.accept(ModItems.JIGGY_CAKE_CARROT_RAW.get());
                        output.accept(ModItems.JIGGY_CAKE_CARROT_MODEL.get());
                        output.accept(ModItems.JIGGY_CAKE_CHOCOLATE_RAW.get());
                        output.accept(ModItems.JIGGY_CAKE_CHOCOLATE_MODEL.get());
                        output.accept(ModItems.JIGGY_CAKE_COFFEE_RAW.get());
                        output.accept(ModItems.JIGGY_CAKE_COFFEE_MODEL.get());
                        output.accept(ModItems.JIGGY_CAKE_LEMON_RAW.get());
                        output.accept(ModItems.JIGGY_CAKE_LEMON_MODEL.get());
                        output.accept(ModItems.JIGGY_CAKE_PUMPKIN_RAW.get());
                        output.accept(ModItems.JIGGY_CAKE_PUMPKIN_MODEL.get());
                        output.accept(ModItems.JIGGY_CAKE_REDVELVET_RAW.get());
                        output.accept(ModItems.JIGGY_CAKE_REDVELVET_MODEL.get());
                        output.accept(ModItems.JIGGY_CAKE_TEA_RAW.get());
                        output.accept(ModItems.JIGGY_CAKE_TEA_MODEL.get());

                        // Phase 5.4: Mousse model intermediates
                        output.accept(ModItems.MOUSSE_BERRY_MODEL.get());
                        output.accept(ModItems.MOUSSE_CHOCOLATE_MODEL.get());
                        output.accept(ModItems.MOUSSE_COFFEE_MODEL.get());
                        output.accept(ModItems.MOUSSE_LEMON_MODEL.get());

                        // Phase 5.4: Tiramisu intermediates
                        output.accept(ModItems.TIRAMISU_RAW.get());
                        output.accept(ModItems.TIRAMISU_MODEL.get());

                        // Phase 5.4: Cake rolls
                        output.accept(ModItems.CAKE_ROLL.get());
                        output.accept(ModItems.CAKE_BERRY_ROLL.get());
                        output.accept(ModItems.CAKE_CARROT_ROLL.get());
                        output.accept(ModItems.CAKE_CHOCOLATE_ROLL.get());
                        output.accept(ModItems.CAKE_COFFEE_ROLL.get());
                        output.accept(ModItems.CAKE_LEMON_ROLL.get());
                        output.accept(ModItems.CAKE_PUMPKIN_ROLL.get());
                        output.accept(ModItems.CAKE_REDVELVET_ROLL.get());
                        output.accept(ModItems.CAKE_TEA_ROLL.get());

                        // Phase 5.4: Cake slices
                        output.accept(ModItems.CAKE_SLICES.get());
                        output.accept(ModItems.CAKE_BERRY_SLICES.get());
                        output.accept(ModItems.CAKE_CHEESE_SLICES.get());
                        output.accept(ModItems.CAKE_COFFEE_SLICES.get());
                        output.accept(ModItems.CAKE_HARVEST_SLICES.get());
                        output.accept(ModItems.CAKE_LEMON_SLICES.get());
                        output.accept(ModItems.CAKE_REDVELVET_SLICES.get());
                        output.accept(ModItems.CAKE_SCHWARZWALD_SLICES.get());
                        output.accept(ModItems.CAKE_TEA_SLICES.get());
                        output.accept(ModItems.CAKE_SPONGE_BERRY_SLICES.get());
                        output.accept(ModItems.CAKE_SPONGE_CARROT_SLICES.get());
                        output.accept(ModItems.CAKE_SPONGE_CHOCOLATE_SLICES.get());
                        output.accept(ModItems.CAKE_SPONGE_COFFEE_SLICES.get());
                        output.accept(ModItems.CAKE_SPONGE_LEMON_SLICES.get());
                        output.accept(ModItems.CAKE_SPONGE_PUMPKIN_SLICES.get());
                        output.accept(ModItems.CAKE_SPONGE_REDVELVET_SLICES.get());
                        output.accept(ModItems.CAKE_SPONGE_TEA_SLICES.get());
                        output.accept(ModItems.TIRAMISU_SLICE.get());

                        // Phase 6: Creams & Cookie Ice Creams
                        output.accept(ModItems.CREAM_MILK.get());
                        output.accept(ModItems.CREAM_APPLE.get());
                        output.accept(ModItems.CREAM_BERRY.get());
                        output.accept(ModItems.CREAM_CHOCOLATE.get());
                        output.accept(ModItems.CREAM_COFFEE.get());
                        output.accept(ModItems.CREAM_LEMON.get());
                        output.accept(ModItems.CREAM_MELON.get());
                        output.accept(ModItems.COOKIE_ICECREAM_VANILLA.get());
                        output.accept(ModItems.COOKIE_ICECREAM_APPLE.get());
                        output.accept(ModItems.COOKIE_ICECREAM_BERRY.get());
                        output.accept(ModItems.COOKIE_ICECREAM_CHOCOLATE.get());
                        output.accept(ModItems.COOKIE_ICECREAM_COFFEE.get());
                        output.accept(ModItems.COOKIE_ICECREAM_LEMON.get());
                        output.accept(ModItems.COOKIE_ICECREAM_MELON.get());

                        // Phase 7: Pies
                        output.accept(ModItems.PIE_APPLE.get());
                        output.accept(ModItems.PIE_BERRY.get());
                        output.accept(ModItems.PIE_CARAMEL.get());
                        output.accept(ModItems.PIE_CHOCOLATE.get());
                        output.accept(ModItems.PIE_COFFEE.get());
                        output.accept(ModItems.PIE_LEMON.get());
                        output.accept(ModItems.PIE_MELON.get());
                        output.accept(ModItems.PIE_TEA.get());

                        // Phase 7: Muffins
                        output.accept(ModItems.MUFFIN.get());
                        output.accept(ModItems.MUFFIN_BERRY.get());
                        output.accept(ModItems.MUFFIN_CARROT.get());
                        output.accept(ModItems.MUFFIN_CHOCOLATE.get());
                        output.accept(ModItems.MUFFIN_COFFEE.get());
                        output.accept(ModItems.MUFFIN_LEMON.get());
                        output.accept(ModItems.MUFFIN_PUMPKIN.get());
                        output.accept(ModItems.MUFFIN_REDVELVET.get());
                        output.accept(ModItems.MUFFIN_TEA.get());
                        output.accept(ModItems.MUFFIN_RAW.get());
                        output.accept(ModItems.MUFFIN_BERRY_RAW.get());
                        output.accept(ModItems.MUFFIN_CARROT_RAW.get());
                        output.accept(ModItems.MUFFIN_CHOCOLATE_RAW.get());
                        output.accept(ModItems.MUFFIN_COFFEE_RAW.get());
                        output.accept(ModItems.MUFFIN_LEMON_RAW.get());
                        output.accept(ModItems.MUFFIN_PUMPKIN_RAW.get());
                        output.accept(ModItems.MUFFIN_REDVELVET_RAW.get());
                        output.accept(ModItems.MUFFIN_TEA_RAW.get());

                        // Phase 7: Pastries
                        output.accept(ModItems.CROISSANT.get());
                        output.accept(ModItems.CROISSANT_CHOCOLATE.get());
                        output.accept(ModItems.GINGER_BREAD.get());
                        output.accept(ModItems.GINGER_BREAD_MAN.get());
                        output.accept(ModItems.GINGER_BREAD_MAN_RAW.get());
                        output.accept(ModItems.PUFF.get());
                        output.accept(ModItems.MILLE_FEUILLE.get());
                        output.accept(ModItems.CROISSANT_RAW.get());
                        output.accept(ModItems.CROISSANT_CHOCOLATE_RAW.get());
                        output.accept(ModItems.PUFF_RAW.get());

                        // Phase 7: Jiggy Cakes
                        output.accept(ModItems.JIGGY_CAKE.get());
                        output.accept(ModItems.JIGGY_CAKE_BERRY.get());
                        output.accept(ModItems.JIGGY_CAKE_CARROT.get());
                        output.accept(ModItems.JIGGY_CAKE_CHOCOLATE.get());
                        output.accept(ModItems.JIGGY_CAKE_COFFEE.get());
                        output.accept(ModItems.JIGGY_CAKE_LEMON.get());
                        output.accept(ModItems.JIGGY_CAKE_PUMPKIN.get());
                        output.accept(ModItems.JIGGY_CAKE_REDVELVET.get());
                        output.accept(ModItems.JIGGY_CAKE_TEA.get());

                        // Phase 7: Mooncakes
                        output.accept(ModItems.MOONCAKE.get());
                        output.accept(ModItems.MOONCAKE_EGG.get());
                        output.accept(ModItems.MOONCAKE_FRUIT.get());
                        output.accept(ModItems.MOONCAKE_HAM.get());
                        output.accept(ModItems.MOONCAKE_RAW.get());
                        output.accept(ModItems.MOONCAKE_EGG_RAW.get());
                        output.accept(ModItems.MOONCAKE_FRUIT_RAW.get());
                        output.accept(ModItems.MOONCAKE_HAM_RAW.get());

                        // Phase 7: Soufflés
                        output.accept(ModItems.SOUFFLE.get());
                        output.accept(ModItems.SOUFFLE_CHOCOLATE.get());
                        output.accept(ModItems.SOUFFLE_RAW.get());
                        output.accept(ModItems.SOUFFLE_CHOCOLATE_RAW.get());

                        // Decor
                        output.accept(ModItems.PLATE.get());
                        output.accept(ModItems.XMAS_TREE.get());
                        output.accept(ModItems.GINGER_HOUSE.get());

                        // Tools
                        output.accept(ModItems.IRON_BOWL.get());
                        output.accept(ModItems.IRON_BOWL_BATTER.get());
                        output.accept(ModItems.IRON_BOWL_EGG.get());
                        output.accept(ModItems.IRON_BOWL_CHEESE.get());
                        output.accept(ModItems.IRON_BOWL_BATTER_BERRY.get());
                        output.accept(ModItems.IRON_BOWL_BATTER_CARROT.get());
                        output.accept(ModItems.IRON_BOWL_BATTER_CHOCOLATE.get());
                        output.accept(ModItems.IRON_BOWL_BATTER_COFFEE.get());
                        output.accept(ModItems.IRON_BOWL_BATTER_LEMON.get());
                        output.accept(ModItems.IRON_BOWL_BATTER_PUMPKIN.get());
                        output.accept(ModItems.IRON_BOWL_BATTER_RED.get());
                        output.accept(ModItems.IRON_BOWL_BATTER_TEA.get());
                        output.accept(ModItems.CAKE_MODEL.get());
                        output.accept(ModItems.CAKE_MODEL_SQUARE.get());
                        output.accept(ModItems.CAKE_MODEL_PLATE.get());
                        output.accept(ModItems.SMALL_MODEL.get());
                        output.accept(ModItems.MOONCAKE_MODEL.get());
                        output.accept(ModItems.MIXING_BOWL.get());

                        // Other items
                        output.accept(ModItems.VANILLA.get());
                        output.accept(ModItems.BAG.get());
                        output.accept(ModItems.SYRUP_EMPTY.get());
                        output.accept(ModItems.SYRUP_CARAMEL.get());
                        output.accept(ModItems.SYRUP_CHOCOLATE.get());
                        output.accept(ModItems.SYRUP_FRUIT.get());
                        output.accept(ModItems.SYRUP_MINT.get());
                        output.accept(ModItems.SYRUP_VANILLA.get());
                        output.accept(ModItems.SYRUP_SAKURA.get());
                        output.accept(ModItems.TEA_LEAF.get());
                        output.accept(ModItems.BLACK_TEA_LEAF.get());
                        output.accept(ModItems.LEMON.get());
                        output.accept(ModItems.CARAMEL.get());
                        output.accept(ModItems.CUSTARD.get());
                        output.accept(ModItems.MILK_FORM.get());
                        output.accept(ModItems.POT.get());
                        output.accept(ModItems.COLDBREW_BOTTLE.get());

                        // Empty cups
                        output.accept(ModItems.CUP.get());
                        output.accept(ModItems.CUP_GLASS.get());

                        // Cake slices & extra foods
                        output.accept(ModItems.CAKE_SPONGE_SLICE.get());
                        output.accept(ModItems.PIE_CREAM.get());
                        output.accept(ModItems.SANDWICH_BLT.get());
                        output.accept(ModItems.SANDWICH_BACON_EGG.get());
                        output.accept(ModItems.SANDWICH_BEEF_CHEESE.get());
                        output.accept(ModItems.SANDWICH_BLT_LARGE.get());
                        output.accept(ModItems.SANDWICH_CLUB.get());
                        output.accept(ModItems.SANDWICH_CLUB_LARGE.get());
                        output.accept(ModItems.SANDWICH_HAM_CHEESE.get());
                        output.accept(ModItems.ICECREAM_VANILLA.get());
                        output.accept(ModItems.ICECREAM_APPLE.get());
                        output.accept(ModItems.ICECREAM_BERRY.get());
                        output.accept(ModItems.ICECREAM_CHOCOLATE.get());
                        output.accept(ModItems.ICECREAM_COFFEE.get());
                        output.accept(ModItems.ICECREAM_LEMON.get());
                        output.accept(ModItems.ICECREAM_MELON.get());
                        output.accept(ModItems.ICECREAM_MIX_VANILLA.get());

                        // Drinks
                        output.accept(ModItems.COFFEE_INSTANT_STICK.get());
                        output.accept(ModItems.COFFEE_INSTANT_BOX.get());
                        output.accept(ModItems.COFFEE_INSTANT_CUP_UNOPEN.get());
                        output.accept(ModItems.COFFEE_INSTANT_CUP.get());
                        output.accept(ModItems.COFFEE_INSTANT.get());
                        output.accept(ModItems.ESPRESSO.get());
                        output.accept(ModItems.COFFEE_TURKISH.get());
                        output.accept(ModItems.COFFEE_AMERICANO.get());
                        output.accept(ModItems.COFFEE_AMERICANO_ICE.get());
                        output.accept(ModItems.COFFEE_AMERICANO_FRUIT.get());
                        output.accept(ModItems.COFFEE_AMERICANO_FRUIT_ICE.get());
                        output.accept(ModItems.COFFEE_AMERICANO_NITRO_ICE.get());
                        output.accept(ModItems.COFFEE_AMERICANO_NITRO_FRUIT_ICE.get());
                        output.accept(ModItems.COFFEE_LATTE.get());
                        output.accept(ModItems.COFFEE_LATTE_ICE.get());
                        output.accept(ModItems.COFFEE_LATTE_CARAMEL.get());
                        output.accept(ModItems.COFFEE_LATTE_CARAMEL_ICE.get());
                        output.accept(ModItems.COFFEE_LATTE_CHOCOLATE.get());
                        output.accept(ModItems.COFFEE_LATTE_CHOCOLATE_ICE.get());
                        output.accept(ModItems.COFFEE_LATTE_FRUIT.get());
                        output.accept(ModItems.COFFEE_LATTE_FRUIT_ICE.get());
                        output.accept(ModItems.COFFEE_LATTE_MINT.get());
                        output.accept(ModItems.COFFEE_LATTE_MINT_ICE.get());
                        output.accept(ModItems.COFFEE_LATTE_VANILLA.get());
                        output.accept(ModItems.COFFEE_LATTE_VANILLA_ICE.get());
                        output.accept(ModItems.COFFEE_LATTE_SAKURA.get());
                        output.accept(ModItems.COFFEE_LATTE_SAKURA_ICE.get());
                        output.accept(ModItems.COFFEE_CAPPUCCINO.get());
                        output.accept(ModItems.COFFEE_CAPPUCCINO_ICE.get());
                        output.accept(ModItems.COFFEE_MACCHIATO.get());
                        output.accept(ModItems.COFFEE_MACCHIATO_ICE.get());
                        output.accept(ModItems.COFFEE_MOCHACCINO.get());
                        output.accept(ModItems.COFFEE_MOCHACCINO_ICE.get());
                        output.accept(ModItems.COFFEE_GREEN_TEA.get());
                        output.accept(ModItems.COFFEE_GREEN_TEA_ICE.get());
                        output.accept(ModItems.COFFEE_BLACK_TEA.get());
                        output.accept(ModItems.COFFEE_BLACK_TEA_ICE.get());
                        output.accept(ModItems.COFFEE_MILK_TEA.get());
                        output.accept(ModItems.COFFEE_MILK_TEA_ICE.get());
                        output.accept(ModItems.COFFEE_MANDARIN_DRINK.get());
                        output.accept(ModItems.COFFEE_MANDARIN_DRINK_ICE.get());
                        output.accept(ModItems.COFFEE_COLDBREW.get());
                        output.accept(ModItems.COFFEE_COLDBREW_ICE.get());
                        output.accept(ModItems.COFFEE_COLDBREW_FRUIT.get());
                        output.accept(ModItems.COFFEE_COLDBREW_FRUIT_ICE.get());
                        output.accept(ModItems.COFFEE_COLDBREW_LATTE.get());
                        output.accept(ModItems.COFFEE_COLDBREW_LATTE_ICE.get());
                        output.accept(ModItems.COFFEE_COLDBREW_LATTE_CARAMEL.get());
                        output.accept(ModItems.COFFEE_COLDBREW_LATTE_CARAMEL_ICE.get());
                        output.accept(ModItems.COFFEE_COLDBREW_LATTE_CHOCOLATE.get());
                        output.accept(ModItems.COFFEE_COLDBREW_LATTE_CHOCOLATE_ICE.get());
                        output.accept(ModItems.COFFEE_COLDBREW_LATTE_FRUIT.get());
                        output.accept(ModItems.COFFEE_COLDBREW_LATTE_FRUIT_ICE.get());
                        output.accept(ModItems.COFFEE_COLDBREW_LATTE_MINT.get());
                        output.accept(ModItems.COFFEE_COLDBREW_LATTE_MINT_ICE.get());
                        output.accept(ModItems.COFFEE_COLDBREW_LATTE_VANILLA.get());
                        output.accept(ModItems.COFFEE_COLDBREW_LATTE_VANILLA_ICE.get());
                        output.accept(ModItems.COCOA.get());
                        output.accept(ModItems.COCOA_ICE.get());
                        output.accept(ModItems.COCOA_STRONG.get());
                        output.accept(ModItems.COCOA_STRONG_ICE.get());

                        // Records
                        output.accept(ModItems.RECORD_BLANK.get());
                        output.accept(ModItems.RECORD_KUSA_NOSHI_TO_NE.get());
                        output.accept(ModItems.RECORD_LAZY_LADY_KAGUYA.get());
                        output.accept(ModItems.RECORD_THE_GRIMOIRE_OF_MARISA.get());
                    })
                    .build());
}
