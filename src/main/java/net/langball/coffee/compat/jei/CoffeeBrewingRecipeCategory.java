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
import net.langball.coffee.recipes.CoffeeBrewingRecipe;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public class CoffeeBrewingRecipeCategory extends AbstractRecipeCategory<CoffeeBrewingRecipe> {

    private static final ResourceLocation JEI_GUI = new ResourceLocation(CoffeeWork.MODID, "textures/gui/jei_machine.png");

    private final IDrawableAnimated animatedArrow;

    public CoffeeBrewingRecipeCategory(RecipeType<CoffeeBrewingRecipe> recipeType,
                                        Component title, IGuiHelper guiHelper, ItemStack iconStack) {
        super(recipeType, title,
                guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK, iconStack),
                130, 54);

        IDrawableStatic arrow = guiHelper.createDrawable(JEI_GUI, 0, 0, 22, 16);
        this.animatedArrow = guiHelper.createAnimatedDrawable(arrow, 200,
                IDrawableAnimated.StartDirection.LEFT, false);
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, CoffeeBrewingRecipe recipe, IFocusGroup focuses) {
        // Slot 0: base (coffee powder) — left
        builder.addSlot(RecipeIngredientRole.INPUT, 1, 1)
                .addIngredients(recipe.base().ingredient());

        // Slot 1: modifier (water/milk bucket) — middle, or empty
        if (recipe.modifier() != null) {
            builder.addSlot(RecipeIngredientRole.INPUT, 1, 19)
                    .addIngredients(recipe.modifier().ingredient());
        }

        // Slot 2: container (cup) — middle-bottom
        builder.addSlot(RecipeIngredientRole.INPUT, 1, 37)
                .addIngredients(recipe.container().ingredient());

        // Slot 3: output — right
        ItemStack result = recipe.getResultItem(Minecraft.getInstance().level != null
                ? Minecraft.getInstance().level.registryAccess() : null);
        builder.addSlot(RecipeIngredientRole.OUTPUT, 104, 19)
                .addItemStack(result);
    }

    @Override
    public void draw(CoffeeBrewingRecipe recipe, IRecipeSlotsView slotsView,
                     GuiGraphics graphics, double mouseX, double mouseY) {
        animatedArrow.draw(graphics, 55, 19);

        Font font = Minecraft.getInstance().font;
        String timeText = recipe.cookingTime() / 20.0F + "s";
        graphics.drawString(font, timeText, 62, 42, 0xFF808080, false);

        if (recipe.experience() > 0) {
            String xpText = recipe.experience() + " XP";
            graphics.drawString(font, xpText, 20, 42, 0xFF808080, false);
        }
    }
}
