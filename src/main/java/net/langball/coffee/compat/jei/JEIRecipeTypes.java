package net.langball.coffee.compat.jei;

import net.langball.coffee.CoffeeWork;
import net.langball.coffee.init.ModRecipeTypes;
import net.langball.coffee.recipes.CoffeeBrewingRecipe;
import net.langball.coffee.recipes.CoolingRecipe;
import net.langball.coffee.recipes.MachineRecipe;
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

    /** Fetch CoolingRecipes from the client-side RecipeManager. */
    @SuppressWarnings("unchecked")
    public static List<CoolingRecipe> getCoolingRecipes() {
        ClientLevel level = Minecraft.getInstance().level;
        if (level == null) return List.of();
        RecipeManager rm = level.getRecipeManager();
        List<CoolingRecipe> result = new ArrayList<>();
        for (var recipe : rm.getAllRecipesFor((net.minecraft.world.item.crafting.RecipeType) ModRecipeTypes.COOLING_SERIALIZER.get())) {
            // Note: COOLING_SERIALIZER doesn't have a RecipeType, so we use the cooling registry directly
            if (recipe instanceof CoolingRecipe cr) result.add(cr);
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

    private JEIRecipeTypes() {}
}
