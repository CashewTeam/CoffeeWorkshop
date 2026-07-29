package net.langball.coffee.compat.jei;

import net.langball.coffee.CoffeeWork;
import net.langball.coffee.init.ModRecipeTypes;
import net.langball.coffee.recipes.CoffeeBrewingRecipe;
import net.langball.coffee.recipes.CoolingRecipe;
import net.langball.coffee.recipes.DrinkTransformRecipe;
import net.langball.coffee.recipes.MachineRecipe;
import net.langball.coffee.recipes.SodaMachineRecipe;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.item.crafting.RecipeManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Constants for JEI recipe types.
 */
public final class JEIRecipeTypes {

    public static final mezz.jei.api.recipe.RecipeType<MachineRecipe> GRINDING =
            mezz.jei.api.recipe.RecipeType.create(CoffeeWork.MODID, "grinding", MachineRecipe.class);

    public static final mezz.jei.api.recipe.RecipeType<CoffeeBrewingRecipe> COFFEE_BREWING =
            mezz.jei.api.recipe.RecipeType.create(CoffeeWork.MODID, "coffee_brewing", CoffeeBrewingRecipe.class);

    public static final mezz.jei.api.recipe.RecipeType<MachineRecipe> ICECREAM_MAKING =
            mezz.jei.api.recipe.RecipeType.create(CoffeeWork.MODID, "icecream_making", MachineRecipe.class);

    public static final mezz.jei.api.recipe.RecipeType<MachineRecipe> ROLLING =
            mezz.jei.api.recipe.RecipeType.create(CoffeeWork.MODID, "rolling", MachineRecipe.class);

    public static final mezz.jei.api.recipe.RecipeType<MachineRecipe> OVEN_BAKING =
            mezz.jei.api.recipe.RecipeType.create(CoffeeWork.MODID, "oven_baking", MachineRecipe.class);

    public static final mezz.jei.api.recipe.RecipeType<CoolingRecipe> COOLING =
            mezz.jei.api.recipe.RecipeType.create(CoffeeWork.MODID, "cooling", CoolingRecipe.class);

    public static final mezz.jei.api.recipe.RecipeType<DrinkTransformRecipe> DRINK_TRANSFORM =
            mezz.jei.api.recipe.RecipeType.create(CoffeeWork.MODID, "drink_transform", DrinkTransformRecipe.class);

    public static final mezz.jei.api.recipe.RecipeType<SodaMachineRecipe> SODA_MAKING =
            mezz.jei.api.recipe.RecipeType.create(CoffeeWork.MODID, "soda_making", SodaMachineRecipe.class);

    /** Fetch CoolingRecipes from the client-side RecipeManager.
     *  CoolingRecipe extends CustomRecipe and lives under {@link net.minecraft.world.item.crafting.RecipeType#CRAFTING}.
     *  We cannot query by a custom RecipeType because that would require
     *  the CoolingRecipe to override {@code getType()} and break the workbench lookup.
     *  Instead we filter the CRAFTING list by class. */
    public static List<CoolingRecipe> getCoolingRecipes() {
        ClientLevel level = Minecraft.getInstance().level;
        if (level == null) return List.of();
        RecipeManager rm = level.getRecipeManager();
        List<CoolingRecipe> result = new ArrayList<>();
        for (var recipe : rm.getAllRecipesFor(net.minecraft.world.item.crafting.RecipeType.CRAFTING)) {
            if (recipe instanceof CoolingRecipe cr) {
                result.add(cr);
            }
        }
        return result;
    }

    /** Fetch DrinkTransformRecipes from the client-side RecipeManager.
     *  DrinkTransformRecipe extends CustomRecipe and lives under CRAFTING. */
    public static List<DrinkTransformRecipe> getDrinkTransformRecipes() {
        ClientLevel level = Minecraft.getInstance().level;
        if (level == null) return List.of();
        RecipeManager rm = level.getRecipeManager();
        List<DrinkTransformRecipe> result = new ArrayList<>();
        for (var recipe : rm.getAllRecipesFor(net.minecraft.world.item.crafting.RecipeType.CRAFTING)) {
            if (recipe instanceof DrinkTransformRecipe dtr) {
                result.add(dtr);
            }
        }
        return result;
    }

    /** Fetch MachineRecipes from the client-side RecipeManager. */
    public static List<MachineRecipe> getMachineRecipes(net.minecraft.world.item.crafting.RecipeType<MachineRecipe> vanillaType) {
        ClientLevel level = Minecraft.getInstance().level;
        if (level == null) return List.of();
        RecipeManager rm = level.getRecipeManager();
        return new ArrayList<>(rm.getAllRecipesFor(vanillaType));
    }

    /** Fetch CoffeeBrewingRecipes from the client-side RecipeManager. */
    @SuppressWarnings("unchecked")
    public static List<CoffeeBrewingRecipe> getCoffeeRecipes() {
        ClientLevel level = Minecraft.getInstance().level;
        if (level == null) return List.of();
        RecipeManager rm = level.getRecipeManager();
        List<CoffeeBrewingRecipe> result = new ArrayList<>();
        for (var recipe : rm.getAllRecipesFor((net.minecraft.world.item.crafting.RecipeType) ModRecipeTypes.COFFEE_BREWING)) {
            if (recipe instanceof CoffeeBrewingRecipe cbr) {
                result.add(cbr);
            }
        }
        return result;
    }

    /** Fetch SodaMachineRecipes from the client-side RecipeManager. */
    public static List<SodaMachineRecipe> getSodaRecipes() {
        ClientLevel level = Minecraft.getInstance().level;
        if (level == null) return List.of();
        RecipeManager rm = level.getRecipeManager();
        return new ArrayList<>(rm.getAllRecipesFor(ModRecipeTypes.SODA_MAKING));
    }

    private JEIRecipeTypes() {}
}
