package net.langball.coffee.compat.jei;

import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableStatic;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.AbstractRecipeCategory;
import net.langball.coffee.CoffeeWork;
import net.langball.coffee.recipes.DrinkTransformRecipe;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

/**
 * JEI category for drink + additive → flavored drink transformation.
 *
 * <p>Shows the source drink + additive (e.g. syrup) → result drink,
 * with a note that remaining cups are preserved.
 */
public class DrinkTransformRecipeCategory extends AbstractRecipeCategory<DrinkTransformRecipe> {

    private static final ResourceLocation JEI_GUI =
            new ResourceLocation(CoffeeWork.MODID, "textures/gui/jei_machine.png");

    private final IDrawableStatic staticArrow;

    public DrinkTransformRecipeCategory(RecipeType<DrinkTransformRecipe> recipeType,
                                        Component title, IGuiHelper guiHelper, ItemStack iconStack) {
        super(recipeType, title,
                guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK, iconStack),
                130, 54);

        this.staticArrow = guiHelper.createDrawable(JEI_GUI, 0, 0, 22, 16);
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, DrinkTransformRecipe recipe, IFocusGroup focuses) {
        // Input 1: source drink
        ItemStack sourceStack = recipe.getResultItem(
                Minecraft.getInstance().level != null
                        ? Minecraft.getInstance().level.registryAccess() : null);
        builder.addSlot(RecipeIngredientRole.INPUT, 1, 19)
                .addItemStack(new ItemStack(recipe.getSource()));

        // Input 2: additive (e.g. syrup)
        builder.addSlot(RecipeIngredientRole.INPUT, 30, 19)
                .addItemStack(new ItemStack(recipe.getAdditive()));

        // Output: result drink (initialised with cup data)
        ItemStack result = recipe.getResultItem(
                Minecraft.getInstance().level != null
                        ? Minecraft.getInstance().level.registryAccess() : null);
        builder.addSlot(RecipeIngredientRole.OUTPUT, 90, 19)
                .addItemStack(result);
    }

    @Override
    public void draw(DrinkTransformRecipe recipe, IRecipeSlotsView slotsView,
                     GuiGraphics graphics, double mouseX, double mouseY) {
        staticArrow.draw(graphics, 58, 20);

        Font font = Minecraft.getInstance().font;
        graphics.drawString(font, "Transform", 55, 4, 0xFF808080, false);
    }
}
