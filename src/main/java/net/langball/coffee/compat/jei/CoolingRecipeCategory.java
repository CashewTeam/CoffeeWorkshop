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
import net.langball.coffee.init.ModItems;
import net.langball.coffee.recipes.CoolingRecipe;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

/**
 * JEI category for hot drink → iced drink cooling recipes.
 *
 * <p>Shows the hot drink + ice_slag → iced drink transformation with
 * frozen cups NBT hint and Arrow.
 */
public class CoolingRecipeCategory extends AbstractRecipeCategory<CoolingRecipe> {

    private static final ResourceLocation JEI_GUI =
            new ResourceLocation(CoffeeWork.MODID, "textures/gui/jei_machine.png");

    private final IDrawableStatic staticArrow;
    private final IDrawableAnimatedCooling animatedArrow;

    public CoolingRecipeCategory(RecipeType<CoolingRecipe> recipeType,
                                  Component title, IGuiHelper guiHelper, ItemStack iconStack) {
        super(recipeType, title,
                guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK, iconStack),
                130, 54);

        IDrawableStatic arrow = guiHelper.createDrawable(JEI_GUI, 0, 0, 22, 16);
        this.staticArrow = arrow;
        this.animatedArrow = new IDrawableAnimatedCooling(arrow);
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, CoolingRecipe recipe, IFocusGroup focuses) {
        // Input 1: hot drink
        builder.addSlot(RecipeIngredientRole.INPUT, 1, 19)
                .addItemStack(new ItemStack(recipe.getHotDrink()));

        // Input 2: ice_slag
        builder.addSlot(RecipeIngredientRole.INPUT, 30, 19)
                .addItemStack(new ItemStack(ModItems.ICE_SLAG.get()));

        // Output: iced drink
        ItemStack result = recipe.getResultItem(Minecraft.getInstance().level != null
                ? Minecraft.getInstance().level.registryAccess() : null);
        if (result.isEmpty()) {
            result = new ItemStack(recipe.getIcedDrink());
            if (result.getItem() instanceof net.langball.coffee.item.DrinkCoffee d) {
                d.initializeFreshStack(result);
            }
        }
        builder.addSlot(RecipeIngredientRole.OUTPUT, 90, 19)
                .addItemStack(result);
    }

    @Override
    public void draw(CoolingRecipe recipe, IRecipeSlotsView slotsView,
                     GuiGraphics graphics, double mouseX, double mouseY) {
        animatedArrow.draw(graphics, 58, 20);

        Font font = Minecraft.getInstance().font;
        graphics.drawString(font, "Cooling", 65, 4, 0xFF808080, false);
    }

    /** Custom IDrawableAnimated wrapper to avoid Forge module dependencies. */
    private static class IDrawableAnimatedCooling implements IDrawable {
        private final IDrawableStatic delegate;
        IDrawableAnimatedCooling(IDrawableStatic delegate) { this.delegate = delegate; }

        @Override public void draw(GuiGraphics gui, int x, int y) { delegate.draw(gui, x, y); }
        @Override public int getWidth() { return delegate.getWidth(); }
        @Override public int getHeight() { return delegate.getHeight(); }
    }
}
