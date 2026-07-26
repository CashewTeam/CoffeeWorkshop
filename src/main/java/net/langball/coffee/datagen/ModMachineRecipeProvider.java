package net.langball.coffee.datagen;

import net.langball.coffee.CoffeeWork;
import net.langball.coffee.datagen.recipe.CoffeeBrewingRecipeBuilder;
import net.langball.coffee.datagen.recipe.MachineRecipeBuilder;
import net.langball.coffee.init.ModItems;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Blocks;

import java.util.function.Consumer;

/**
 * Generates all machine processing recipes via DataGen builders.
 *
 * <p>Called from {@link ModRecipeProvider#buildRecipes(Consumer)}.
 * Not a standalone RecipeProvider to avoid duplicate provider errors.
 */
public final class ModMachineRecipeProvider {

    private ModMachineRecipeProvider() {}

    public static void buildRecipes(Consumer<FinishedRecipe> writer) {
        buildGrindingRecipes(writer);
        buildOvenRecipes(writer);
        buildRollingRecipes(writer);
        buildIcecreamRecipes(writer);
        buildCoffeeRecipes(writer);
    }

    private static void buildGrindingRecipes(Consumer<FinishedRecipe> writer) {
        MachineRecipeBuilder.grinding(Ingredient.of(ModItems.COFFEE_BEAN.get()),
                new ItemStack(ModItems.COFFEE_POWDER.get()))
                .experience(0.2F).cookingTime(200)
                .save(writer, id("grinding/coffee_powder"));

        MachineRecipeBuilder.grinding(Ingredient.of(Items.COCOA_BEANS),
                new ItemStack(ModItems.COCOA_POWDER.get()))
                .experience(0.2F).cookingTime(200)
                .save(writer, id("grinding/cocoa_powder"));

        MachineRecipeBuilder.grinding(Ingredient.of(Items.WHEAT),
                new ItemStack(ModItems.FLOUR.get()))
                .experience(0.1F).cookingTime(200)
                .save(writer, id("grinding/flour"));

        MachineRecipeBuilder.grinding(Ingredient.of(ModItems.COCOA_POWDER.get()),
                new ItemStack(ModItems.COCOA_BATTER.get()))
                .experience(0.15F).cookingTime(200)
                .save(writer, id("grinding/cocoa_batter"));

        MachineRecipeBuilder.grinding(Ingredient.of(Blocks.ICE),
                new ItemStack(ModItems.ICE_SLAG.get()))
                .experience(0.05F).cookingTime(200)
                .save(writer, id("grinding/ice_slag"));

        MachineRecipeBuilder.grinding(Ingredient.of(ModItems.SODA_ORE.get()),
                new ItemStack(ModItems.SODA.get(), 4))
                .experience(0.1F).cookingTime(200)
                .save(writer, id("grinding/soda"));
    }

    private static void buildOvenRecipes(Consumer<FinishedRecipe> writer) {
        MachineRecipeBuilder.ovenBaking(Ingredient.of(ModItems.COFFEE_BEAN_RAW.get()),
                new ItemStack(ModItems.COFFEE_BEAN.get()))
                .experience(0.35F).cookingTime(200)
                .save(writer, id("oven_baking/coffee_bean"));

        MachineRecipeBuilder.ovenBaking(Ingredient.of(ModItems.DOUGH_BREAD.get()),
                new ItemStack(Items.BREAD))
                .experience(0.35F).cookingTime(200)
                .save(writer, id("oven_baking/bread"));

        MachineRecipeBuilder.ovenBaking(Ingredient.of(ModItems.DOUGH_BREAD_ROUND.get()),
                new ItemStack(ModItems.BREAD_ROUND.get()))
                .experience(0.35F).cookingTime(200)
                .save(writer, id("oven_baking/bread_round"));

        MachineRecipeBuilder.ovenBaking(Ingredient.of(ModItems.DOUGH_BAGUETTE.get()),
                new ItemStack(ModItems.BAGUETTE.get()))
                .experience(0.35F).cookingTime(200)
                .save(writer, id("oven_baking/baguette"));

        MachineRecipeBuilder.ovenBaking(Ingredient.of(ModItems.DOUGH_BAGEL.get()),
                new ItemStack(ModItems.BAGEL.get()))
                .experience(0.35F).cookingTime(200)
                .save(writer, id("oven_baking/bagel"));

        MachineRecipeBuilder.ovenBaking(Ingredient.of(ModItems.DOUGH_TOAST.get()),
                new ItemStack(ModItems.TOAST.get()))
                .experience(0.35F).cookingTime(200)
                .save(writer, id("oven_baking/toast"));

        MachineRecipeBuilder.ovenBaking(Ingredient.of(ModItems.DOUGH_COOKIE.get()),
                new ItemStack(Items.COOKIE))
                .experience(0.35F).cookingTime(200)
                .save(writer, id("oven_baking/cookie"));

        // Phase 4: Dough intermediate closures
        MachineRecipeBuilder.ovenBaking(Ingredient.of(ModItems.PLATE_DOUGH.get()),
                new ItemStack(ModItems.BREAD_ROUND.get()))
                .experience(0.35F).cookingTime(200)
                .save(writer, id("oven_baking/plate_dough_to_bread_round"));
    }

    private static void buildRollingRecipes(Consumer<FinishedRecipe> writer) {
        MachineRecipeBuilder.rolling(Ingredient.of(Items.IRON_INGOT),
                new ItemStack(ModItems.PLATE_IRON.get()))
                .experience(0.1F).cookingTime(200)
                .save(writer, id("rolling/plate_iron"));

        MachineRecipeBuilder.rolling(Ingredient.of(ModItems.DOUGH.get()),
                new ItemStack(ModItems.PLATE_DOUGH.get()))
                .experience(0.1F).cookingTime(200)
                .save(writer, id("rolling/plate_dough"));

        MachineRecipeBuilder.rolling(Ingredient.of(ModItems.DOUGH_PASTRY.get()),
                new ItemStack(ModItems.PLATE_DOUGH_PASTRY.get()))
                .experience(0.1F).cookingTime(200)
                .save(writer, id("rolling/plate_dough_pastry"));

        MachineRecipeBuilder.rolling(Ingredient.of(ModItems.DOUGH_GINGER.get()),
                new ItemStack(ModItems.PLATE_DOUGH_GINGER.get()))
                .experience(0.1F).cookingTime(200)
                .save(writer, id("rolling/plate_dough_ginger"));
    }

    private static void buildIcecreamRecipes(Consumer<FinishedRecipe> writer) {
        MachineRecipeBuilder.icecreamMaking(
                Ingredient.of(ModItems.ICECREAM_MIX_VANILLA.get()),
                new ItemStack(ModItems.ICECREAM_VANILLA.get()))
                .experience(0.2F).cookingTime(400)
                .save(writer, id("icecream_making/vanilla"));
    }

    private static void buildCoffeeRecipes(Consumer<FinishedRecipe> writer) {
        // ── Phase 3 base recipes (v2 compatible) ─────────────────────────

        // Espresso: 2 coffee_powder + 1 cup (no modifier, no additive)
        CoffeeBrewingRecipeBuilder.brewing(new ItemStack(ModItems.ESPRESSO.get()))
                .base(Ingredient.of(ModItems.COFFEE_POWDER.get()), 2)
                .container(Ingredient.of(ModItems.CUP.get()), 1)
                .experience(0.2F).cookingTime(80)
                .save(writer, id("coffee_brewing/espresso"));

        // Americano: 1 coffee_powder + water_bucket + 1 cup
        CoffeeBrewingRecipeBuilder.brewing(new ItemStack(ModItems.COFFEE_AMERICANO.get()))
                .base(Ingredient.of(ModItems.COFFEE_POWDER.get()), 1)
                .modifier(Ingredient.of(Items.WATER_BUCKET), 1)
                .container(Ingredient.of(ModItems.CUP.get()), 1)
                .experience(0.2F).cookingTime(120)
                .save(writer, id("coffee_brewing/americano"));

        // Latte: 1 coffee_powder + milk_bucket + 1 cup
        CoffeeBrewingRecipeBuilder.brewing(new ItemStack(ModItems.COFFEE_LATTE.get()))
                .base(Ingredient.of(ModItems.COFFEE_POWDER.get()), 1)
                .modifier(Ingredient.of(Items.MILK_BUCKET), 1)
                .container(Ingredient.of(ModItems.CUP.get()), 1)
                .experience(0.25F).cookingTime(120)
                .save(writer, id("coffee_brewing/latte"));

        // ── Phase 4: Coffee & Cocoa drinks ──────────────────────────────

        // Cappuccino: coffee_powder + milk_bucket + sugar + cup
        CoffeeBrewingRecipeBuilder.brewing(new ItemStack(ModItems.COFFEE_CAPPUCCINO.get()))
                .base(Ingredient.of(ModItems.COFFEE_POWDER.get()), 1)
                .modifier(Ingredient.of(Items.MILK_BUCKET), 1)
                .additive(Ingredient.of(Items.SUGAR), 1)
                .container(Ingredient.of(ModItems.CUP.get()), 1)
                .experience(0.3F).cookingTime(140)
                .save(writer, id("coffee_brewing/cappuccino"));

        // Macchiato: 2×coffee_powder + milk_bucket + cup
        CoffeeBrewingRecipeBuilder.brewing(new ItemStack(ModItems.COFFEE_MACCHIATO.get()))
                .base(Ingredient.of(ModItems.COFFEE_POWDER.get()), 2)
                .modifier(Ingredient.of(Items.MILK_BUCKET), 1)
                .container(Ingredient.of(ModItems.CUP.get()), 1)
                .experience(0.3F).cookingTime(140)
                .save(writer, id("coffee_brewing/macchiato"));

        // Mochaccino: coffee_powder + milk_bucket + cocoa_powder + cup
        CoffeeBrewingRecipeBuilder.brewing(new ItemStack(ModItems.COFFEE_MOCHACCINO.get()))
                .base(Ingredient.of(ModItems.COFFEE_POWDER.get()), 1)
                .modifier(Ingredient.of(Items.MILK_BUCKET), 1)
                .additive(Ingredient.of(ModItems.COCOA_POWDER.get()), 1)
                .container(Ingredient.of(ModItems.CUP.get()), 1)
                .experience(0.3F).cookingTime(140)
                .save(writer, id("coffee_brewing/mochaccino"));

        // Cocoa: 2×cocoa_powder + milk_bucket + cup
        CoffeeBrewingRecipeBuilder.brewing(new ItemStack(ModItems.COCOA.get()))
                .base(Ingredient.of(ModItems.COCOA_POWDER.get()), 2)
                .modifier(Ingredient.of(Items.MILK_BUCKET), 1)
                .container(Ingredient.of(ModItems.CUP.get()), 1)
                .experience(0.25F).cookingTime(120)
                .save(writer, id("coffee_brewing/cocoa"));

        // Cocoa Strong: 2×cocoa_powder + milk_bucket + chocolate_chip + cup
        CoffeeBrewingRecipeBuilder.brewing(new ItemStack(ModItems.COCOA_STRONG.get()))
                .base(Ingredient.of(ModItems.COCOA_POWDER.get()), 2)
                .modifier(Ingredient.of(Items.MILK_BUCKET), 1)
                .additive(Ingredient.of(ModItems.CHOCOLATE_CHIP.get()), 1)
                .container(Ingredient.of(ModItems.CUP.get()), 1)
                .experience(0.35F).cookingTime(160)
                .save(writer, id("coffee_brewing/cocoa_strong"));

        // ── Phase 4: Tea drinks ──────────────────────────────────────────

        // Green Tea: tea_leaf + water_bucket + cup_glass
        CoffeeBrewingRecipeBuilder.brewing(new ItemStack(ModItems.COFFEE_GREEN_TEA.get()))
                .base(Ingredient.of(ModItems.TEA_LEAF.get()), 1)
                .modifier(Ingredient.of(Items.WATER_BUCKET), 1)
                .container(Ingredient.of(ModItems.CUP_GLASS.get()), 1)
                .experience(0.15F).cookingTime(100)
                .save(writer, id("coffee_brewing/green_tea"));

        // Black Tea: black_tea_leaf + water_bucket + cup_glass
        CoffeeBrewingRecipeBuilder.brewing(new ItemStack(ModItems.COFFEE_BLACK_TEA.get()))
                .base(Ingredient.of(ModItems.BLACK_TEA_LEAF.get()), 1)
                .modifier(Ingredient.of(Items.WATER_BUCKET), 1)
                .container(Ingredient.of(ModItems.CUP_GLASS.get()), 1)
                .experience(0.2F).cookingTime(100)
                .save(writer, id("coffee_brewing/black_tea"));

        // Milk Tea: black_tea_leaf + milk_bucket + sugar + cup_glass
        CoffeeBrewingRecipeBuilder.brewing(new ItemStack(ModItems.COFFEE_MILK_TEA.get()))
                .base(Ingredient.of(ModItems.BLACK_TEA_LEAF.get()), 1)
                .modifier(Ingredient.of(Items.MILK_BUCKET), 1)
                .additive(Ingredient.of(Items.SUGAR), 1)
                .container(Ingredient.of(ModItems.CUP_GLASS.get()), 1)
                .experience(0.25F).cookingTime(120)
                .save(writer, id("coffee_brewing/milk_tea"));

        // Mandarin Drink: coffee_powder + milk_bucket + tea_leaf + cup_glass
        CoffeeBrewingRecipeBuilder.brewing(new ItemStack(ModItems.COFFEE_MANDARIN_DRINK.get()))
                .base(Ingredient.of(ModItems.COFFEE_POWDER.get()), 1)
                .modifier(Ingredient.of(Items.MILK_BUCKET), 1)
                .additive(Ingredient.of(ModItems.TEA_LEAF.get()), 1)
                .container(Ingredient.of(ModItems.CUP_GLASS.get()), 1)
                .experience(0.3F).cookingTime(140)
                .save(writer, id("coffee_brewing/mandarin_drink"));

        // ── Phase 4: Cold Brew ───────────────────────────────────────────

        // Coldbrew: coldbrew_bottle + cup_glass
        CoffeeBrewingRecipeBuilder.brewing(new ItemStack(ModItems.COFFEE_COLDBREW.get()))
                .base(Ingredient.of(ModItems.COLDBREW_BOTTLE.get()), 1)
                .container(Ingredient.of(ModItems.CUP_GLASS.get()), 1)
                .experience(0.3F).cookingTime(160)
                .save(writer, id("coffee_brewing/coldbrew"));

        // ── Phase 4: Iced drinks ─────────────────────────────────────────

        // Iced Americano: coffee_powder + water_bucket + ice_slag + cup_glass
        CoffeeBrewingRecipeBuilder.brewing(new ItemStack(ModItems.COFFEE_AMERICANO_ICE.get()))
                .base(Ingredient.of(ModItems.COFFEE_POWDER.get()), 1)
                .modifier(Ingredient.of(Items.WATER_BUCKET), 1)
                .additive(Ingredient.of(ModItems.ICE_SLAG.get()), 1)
                .container(Ingredient.of(ModItems.CUP_GLASS.get()), 1)
                .experience(0.25F).cookingTime(140)
                .save(writer, id("coffee_brewing/iced_americano"));

        // Iced Latte: coffee_powder + milk_bucket + ice_slag + cup_glass
        CoffeeBrewingRecipeBuilder.brewing(new ItemStack(ModItems.COFFEE_LATTE_ICE.get()))
                .base(Ingredient.of(ModItems.COFFEE_POWDER.get()), 1)
                .modifier(Ingredient.of(Items.MILK_BUCKET), 1)
                .additive(Ingredient.of(ModItems.ICE_SLAG.get()), 1)
                .container(Ingredient.of(ModItems.CUP_GLASS.get()), 1)
                .experience(0.25F).cookingTime(140)
                .save(writer, id("coffee_brewing/iced_latte"));

        // Iced Coldbrew: coldbrew_bottle + ice_slag + cup_glass
        CoffeeBrewingRecipeBuilder.brewing(new ItemStack(ModItems.COFFEE_COLDBREW_ICE.get()))
                .base(Ingredient.of(ModItems.COLDBREW_BOTTLE.get()), 1)
                .additive(Ingredient.of(ModItems.ICE_SLAG.get()), 1)
                .container(Ingredient.of(ModItems.CUP_GLASS.get()), 1)
                .experience(0.3F).cookingTime(180)
                .save(writer, id("coffee_brewing/iced_coldbrew"));
    }

    private static ResourceLocation id(String path) {
        return CoffeeWork.id(path);
    }
}
