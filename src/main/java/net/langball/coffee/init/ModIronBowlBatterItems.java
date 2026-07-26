package net.langball.coffee.init;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.DeferredRegister;

/**
 * Iron Bowl Batter items — 1.12.2 metadata-based batter system,
 * restored as individual items for 1.20.1.
 *
 * <p>In 1.12.2, {@code iron_bowl_batter} used metadata (0–10) to
 * represent 11 variants. Each variant had {@code setContainerItem(iron_bowl)},
 * returning an empty Iron Bowl when consumed in crafting.
 *
 * <p>Production chain:
 * <pre>{@code
 *   Iron Bowl + ingredients → Batter in Iron Bowl
 *   Batter + Mold → Cake Raw → Oven → Cake Model → Finished Cake
 * }</pre>
 *
 * @see ModIngredientItems for the Iron Bowl and molds
 */
public class ModIronBowlBatterItems {

    static void registerAll(DeferredRegister<Item> items) {
        // Generic base batter (1.12.2 meta 2)
        ModItems.IRON_BOWL_BATTER = items.register("iron_bowl_batter",
                () -> new BatterItem(new Item.Properties().stacksTo(16)));
        // Egg batter (1.12.2 meta 0): 3 eggs in iron bowl
        ModItems.IRON_BOWL_EGG = items.register("iron_bowl_egg",
                () -> new BatterItem(new Item.Properties().stacksTo(16)));
        // Cheese batter (1.12.2 meta 1): cheese + cream in iron bowl
        ModItems.IRON_BOWL_CHEESE = items.register("iron_bowl_cheese",
                () -> new BatterItem(new Item.Properties().stacksTo(16)));

        // Flavored batters (1.12.2 meta 3–10): base batter + flavor
        ModItems.IRON_BOWL_BATTER_BERRY = items.register("iron_bowl_batter_berry",
                () -> new BatterItem(new Item.Properties().stacksTo(16)));
        ModItems.IRON_BOWL_BATTER_CARROT = items.register("iron_bowl_batter_carrot",
                () -> new BatterItem(new Item.Properties().stacksTo(16)));
        ModItems.IRON_BOWL_BATTER_CHOCOLATE = items.register("iron_bowl_batter_chocolate",
                () -> new BatterItem(new Item.Properties().stacksTo(16)));
        ModItems.IRON_BOWL_BATTER_COFFEE = items.register("iron_bowl_batter_coffee",
                () -> new BatterItem(new Item.Properties().stacksTo(16)));
        ModItems.IRON_BOWL_BATTER_LEMON = items.register("iron_bowl_batter_lemon",
                () -> new BatterItem(new Item.Properties().stacksTo(16)));
        ModItems.IRON_BOWL_BATTER_PUMPKIN = items.register("iron_bowl_batter_pumpkin",
                () -> new BatterItem(new Item.Properties().stacksTo(16)));
        ModItems.IRON_BOWL_BATTER_RED = items.register("iron_bowl_batter_red",
                () -> new BatterItem(new Item.Properties().stacksTo(16)));
        ModItems.IRON_BOWL_BATTER_TEA = items.register("iron_bowl_batter_tea",
                () -> new BatterItem(new Item.Properties().stacksTo(16)));
    }

    /**
     * Item that returns an empty Iron Bowl as its crafting remainder.
     *
     * <p>Matches 1.12.2 {@code setContainerItem(iron_bowl)}:
     * when batter is consumed in a recipe, the player gets back
     * the iron bowl.
     */
    private static class BatterItem extends Item {
        BatterItem(Properties props) {
            super(props);
        }

        @Override
        public boolean hasCraftingRemainingItem() {
            return true;
        }

        @Override
        public ItemStack getCraftingRemainingItem(ItemStack stack) {
            return new ItemStack(ModItems.IRON_BOWL.get());
        }
    }
}
