package net.langball.coffee.compat.jei;

import net.langball.coffee.CoffeeWork;
import net.langball.coffee.init.ModRecipeTypes;
import net.langball.coffee.recipes.MachineRecipe;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Constants for JEI recipe types for all five machines.
 */
public final class JEIRecipeTypes {

    public static final mezz.jei.api.recipe.RecipeType<MachineRecipe> GRINDING =
            mezz.jei.api.recipe.RecipeType.create(CoffeeWork.MODID, "grinding", MachineRecipe.class);

    public static final mezz.jei.api.recipe.RecipeType<MachineRecipe> COFFEE_BREWING =
            mezz.jei.api.recipe.RecipeType.create(CoffeeWork.MODID, "coffee_brewing", MachineRecipe.class);

    public static final mezz.jei.api.recipe.RecipeType<MachineRecipe> ICECREAM_MAKING =
            mezz.jei.api.recipe.RecipeType.create(CoffeeWork.MODID, "icecream_making", MachineRecipe.class);

    public static final mezz.jei.api.recipe.RecipeType<MachineRecipe> ROLLING =
            mezz.jei.api.recipe.RecipeType.create(CoffeeWork.MODID, "rolling", MachineRecipe.class);

    public static final mezz.jei.api.recipe.RecipeType<MachineRecipe> OVEN_BAKING =
            mezz.jei.api.recipe.RecipeType.create(CoffeeWork.MODID, "oven_baking", MachineRecipe.class);

    /**
     * Fetch all recipes of the given type from the server's RecipeManager.
     */
    public static List<MachineRecipe> getRecipes(mezz.jei.api.recipe.RecipeType<MachineRecipe> recipeType,
                                                  net.minecraft.world.item.crafting.RecipeType<MachineRecipe> vanillaType) {
        ClientLevel level = Minecraft.getInstance().level;
        if (level == null) return List.of();

        RecipeManager recipeManager = level.getRecipeManager();
        return new ArrayList<>(recipeManager.getAllRecipesFor(vanillaType));
    }

    private JEIRecipeTypes() {}
}
