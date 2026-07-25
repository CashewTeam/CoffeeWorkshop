package net.langball.coffee.compat.jei;

import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableAnimated;
import mezz.jei.api.gui.drawable.IDrawableStatic;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.AbstractRecipeCategory;
import net.langball.coffee.CoffeeWork;
import net.langball.coffee.recipes.MachineRecipe;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

/**
 * Shared JEI recipe category for all five processing machines.
 * Each machine gets its own instance configured with its unique
 * {@link RecipeType}, title, icon, and GUI texture.
 */
public class MachineRecipeCategory extends AbstractRecipeCategory<MachineRecipe> {

    private static final ResourceLocation JEI_GUI = new ResourceLocation(CoffeeWork.MODID, "textures/gui/jei_machine.png");

    private final IDrawableStatic staticArrow;
    private final IDrawableAnimated animatedArrow;

    /**
     * @param recipeType  JEI recipe type for this machine
     * @param title       localised category title
     * @param guiHelper   JEI GUI helper
     * @param iconStack   ItemStack used as the category icon (e.g. the machine block)
     */
    public MachineRecipeCategory(RecipeType<MachineRecipe> recipeType,
                                  Component title,
                                  IGuiHelper guiHelper,
                                  ItemStack iconStack) {
        super(recipeType, title, guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK, iconStack), 100, 54);

        IDrawableStatic arrow = guiHelper.createDrawable(JEI_GUI, 0, 0, 22, 16);
        this.staticArrow = arrow;
        this.animatedArrow = guiHelper.createAnimatedDrawable(arrow, 200, IDrawableAnimated.StartDirection.LEFT, false);
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, MachineRecipe recipe, IFocusGroup focuses) {
        // Input slot (left)
        builder.addSlot(RecipeIngredientRole.INPUT, 8, 19)
                .addIngredients(recipe.ingredient());

        // Output slot (right)
        builder.addSlot(RecipeIngredientRole.OUTPUT, 76, 19)
                .addItemStack(recipe.result());
    }

    @Override
    public void draw(MachineRecipe recipe, IRecipeSlotsView slotsView, GuiGraphics graphics, double mouseX, double mouseY) {
        // Draw animated arrow (progress indicator)
        animatedArrow.draw(graphics, 33, 19);

        Font font = Minecraft.getInstance().font;

        // Draw cooking time text below the arrow
        String timeText = recipe.cookingTime() / 20.0F + "s";
        graphics.drawString(font, timeText, 42, 42, 0xFF808080, false);

        // Draw experience text
        if (recipe.experience() > 0) {
            String xpText = recipe.experience() + " XP";
            graphics.drawString(font, xpText, 8, 44, 0xFF808080, false);
        }
    }
}
