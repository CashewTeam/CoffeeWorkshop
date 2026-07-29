package net.langball.coffee.compat.jei;

import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawableAnimated;
import mezz.jei.api.gui.drawable.IDrawableStatic;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.category.AbstractRecipeCategory;
import net.langball.coffee.CoffeeWork;
import net.langball.coffee.recipes.SodaMachineRecipe;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

public final class SodaMachineRecipeCategory extends AbstractRecipeCategory<SodaMachineRecipe> {

    private final IDrawableAnimated arrow;

    public SodaMachineRecipeCategory(IGuiHelper guiHelper, ItemStack icon) {
        super(
                JEIRecipeTypes.SODA_MAKING,
                Component.translatable("jei.coffeework.category.soda_machine"),
                guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK, icon),
                122,
                58);

        IDrawableStatic staticArrow = guiHelper.createDrawable(
                CoffeeWork.id("textures/gui/jei_machine.png"),
                0, 0, 22, 16);

        arrow = guiHelper.createAnimatedDrawable(
                staticArrow,
                200,
                IDrawableAnimated.StartDirection.LEFT,
                false);
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, SodaMachineRecipe recipe, IFocusGroup focuses) {
        builder.addSlot(RecipeIngredientRole.INPUT, 4, 7)
                .addIngredients(recipe.container());

        builder.addSlot(RecipeIngredientRole.INPUT, 26, 7)
                .addIngredients(recipe.base());

        builder.addSlot(RecipeIngredientRole.INPUT, 48, 7)
                .addIngredients(recipe.flavor());

        builder.addSlot(RecipeIngredientRole.OUTPUT, 100, 21)
                .addItemStack(recipe.result());
    }

    @Override
    public void draw(SodaMachineRecipe recipe, IRecipeSlotsView slots, GuiGraphics graphics,
                     double mouseX, double mouseY) {
        arrow.draw(graphics, 72, 21);

        String seconds = recipe.cookingTime() / 20.0F + "s";
        graphics.drawString(
                Minecraft.getInstance().font,
                seconds,
                76,
                43,
                0xFF808080,
                false);
    }
}
