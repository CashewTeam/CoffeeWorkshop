package net.langball.coffee.compat.jei;

import net.langball.coffee.CoffeeWork;
import net.langball.coffee.init.ModBlocks;
import net.langball.coffee.init.ModRecipeTypes;
import net.langball.coffee.recipes.MachineRecipe;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.List;

@mezz.jei.api.JeiPlugin
public class JEICompat implements mezz.jei.api.IModPlugin {

    private static final ResourceLocation UID = new ResourceLocation(CoffeeWork.MODID, "jei_plugin");

    @Override
    public ResourceLocation getPluginUid() {
        return UID;
    }

    @Override
    public void registerCategories(mezz.jei.api.registration.IRecipeCategoryRegistration registration) {
        var guiHelper = registration.getJeiHelpers().getGuiHelper();

        registration.addRecipeCategories(
                new MachineRecipeCategory(
                        JEIRecipeTypes.GRINDING,
                        Component.translatable("jei.coffeework.category.grinder"),
                        guiHelper,
                        new ItemStack(ModBlocks.GRINDER.get())),
                new MachineRecipeCategory(
                        JEIRecipeTypes.COFFEE_BREWING,
                        Component.translatable("jei.coffeework.category.coffee_machine"),
                        guiHelper,
                        new ItemStack(ModBlocks.COFFEE_MACHINE.get())),
                new MachineRecipeCategory(
                        JEIRecipeTypes.ICECREAM_MAKING,
                        Component.translatable("jei.coffeework.category.icecream_machine"),
                        guiHelper,
                        new ItemStack(ModBlocks.ICECREAM_MACHINE.get())),
                new MachineRecipeCategory(
                        JEIRecipeTypes.ROLLING,
                        Component.translatable("jei.coffeework.category.roller"),
                        guiHelper,
                        new ItemStack(ModBlocks.ROLLER.get())),
                new MachineRecipeCategory(
                        JEIRecipeTypes.OVEN_BAKING,
                        Component.translatable("jei.coffeework.category.oven"),
                        guiHelper,
                        new ItemStack(ModBlocks.OVEN.get()))
        );
    }

    @Override
    public void registerRecipes(mezz.jei.api.registration.IRecipeRegistration registration) {
        registration.addRecipes(JEIRecipeTypes.GRINDING,
                JEIRecipeTypes.getRecipes(JEIRecipeTypes.GRINDING, ModRecipeTypes.GRINDING));

        registration.addRecipes(JEIRecipeTypes.COFFEE_BREWING,
                JEIRecipeTypes.getRecipes(JEIRecipeTypes.COFFEE_BREWING, ModRecipeTypes.COFFEE_BREWING));

        registration.addRecipes(JEIRecipeTypes.ICECREAM_MAKING,
                JEIRecipeTypes.getRecipes(JEIRecipeTypes.ICECREAM_MAKING, ModRecipeTypes.ICECREAM_MAKING));

        registration.addRecipes(JEIRecipeTypes.ROLLING,
                JEIRecipeTypes.getRecipes(JEIRecipeTypes.ROLLING, ModRecipeTypes.ROLLING));

        registration.addRecipes(JEIRecipeTypes.OVEN_BAKING,
                JEIRecipeTypes.getRecipes(JEIRecipeTypes.OVEN_BAKING, ModRecipeTypes.OVEN_BAKING));
    }

    @Override
    public void registerRecipeCatalysts(mezz.jei.api.registration.IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalyst(new ItemStack(ModBlocks.GRINDER.get()), JEIRecipeTypes.GRINDING);
        registration.addRecipeCatalyst(new ItemStack(ModBlocks.COFFEE_MACHINE.get()), JEIRecipeTypes.COFFEE_BREWING);
        registration.addRecipeCatalyst(new ItemStack(ModBlocks.ICECREAM_MACHINE.get()), JEIRecipeTypes.ICECREAM_MAKING);
        registration.addRecipeCatalyst(new ItemStack(ModBlocks.ROLLER.get()), JEIRecipeTypes.ROLLING);
        registration.addRecipeCatalyst(new ItemStack(ModBlocks.OVEN.get()), JEIRecipeTypes.OVEN_BAKING);
    }
}
