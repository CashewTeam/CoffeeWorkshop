package net.langball.coffee.datagen.recipe;

import com.google.gson.JsonObject;
import net.langball.coffee.CoffeeWork;
import net.langball.coffee.recipes.CoffeeBrewingRecipe;
import net.langball.coffee.recipes.SlotIngredient;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

/**
 * DataGen builder for {@link CoffeeBrewingRecipe} (v2 — 5-slot).
 *
 * <pre>{@code
 * CoffeeBrewingRecipeBuilder.brewing(new ItemStack(ModItems.COFFEE_MOCHACCINO.get()))
 *     .base(Ingredient.of(ModItems.COFFEE_POWDER.get()), 1)
 *     .modifier(Ingredient.of(Items.MILK_BUCKET), 1)
 *     .additive(Ingredient.of(ModItems.COCOA_POWDER.get()), 1)
 *     .container(Ingredient.of(ModItems.CUP.get()), 1)
 *     .experience(0.3F).cookingTime(140)
 *     .save(writer, CoffeeWork.id("coffee_brewing/mochaccino"));
 * }</pre>
 */
public class CoffeeBrewingRecipeBuilder {

    private final ItemStack result;
    private SlotIngredient base;
    @Nullable private SlotIngredient modifier;
    @Nullable private SlotIngredient additive;
    private SlotIngredient container;
    private float experience = 0F;
    private int cookingTime = 200;
    private String group = "";

    private CoffeeBrewingRecipeBuilder(ItemStack result) {
        this.result = result;
    }

    public static CoffeeBrewingRecipeBuilder brewing(ItemStack result) {
        return new CoffeeBrewingRecipeBuilder(result);
    }

    public CoffeeBrewingRecipeBuilder base(Ingredient ingredient, int count) {
        this.base = new SlotIngredient(ingredient, count);
        return this;
    }

    /** Sets the modifier ingredient.  Omit this call for recipes that
     *  require the modifier slot to be empty (e.g. Espresso). */
    public CoffeeBrewingRecipeBuilder modifier(Ingredient ingredient, int count) {
        this.modifier = new SlotIngredient(ingredient, count);
        return this;
    }

    /** Sets the additive ingredient.  Omit this call for recipes that
     *  require the additive slot to be empty (e.g. basic Americano). */
    public CoffeeBrewingRecipeBuilder additive(Ingredient ingredient, int count) {
        this.additive = new SlotIngredient(ingredient, count);
        return this;
    }

    public CoffeeBrewingRecipeBuilder container(Ingredient ingredient, int count) {
        this.container = new SlotIngredient(ingredient, count);
        return this;
    }

    public CoffeeBrewingRecipeBuilder experience(float e) { this.experience = e; return this; }
    public CoffeeBrewingRecipeBuilder cookingTime(int t) { this.cookingTime = t; return this; }
    public CoffeeBrewingRecipeBuilder group(String g) { this.group = g; return this; }

    public void save(Consumer<FinishedRecipe> writer, ResourceLocation id) {
        validate(id);
        writer.accept(new Result(id));
    }

    private void validate(ResourceLocation id) {
        if (base == null) throw new IllegalArgumentException("Coffee recipe '" + id + "' missing base");
        if (container == null) throw new IllegalArgumentException("Coffee recipe '" + id + "' missing container");
        if (result.isEmpty()) throw new IllegalArgumentException("Coffee recipe '" + id + "' has empty result");
        if (cookingTime < 1 || cookingTime > 72000)
            throw new IllegalArgumentException("Coffee recipe '" + id + "' cookingTime out of range");
    }

    private class Result implements FinishedRecipe {
        private final ResourceLocation id;
        Result(ResourceLocation id) { this.id = id; }

        @Override
        public void serializeRecipeData(JsonObject json) {
            if (!group.isEmpty()) json.addProperty("group", group);

            JsonObject baseObj = new JsonObject();
            baseObj.add("ingredient", base.ingredient().toJson());
            baseObj.addProperty("count", base.count());
            json.add("base", baseObj);

            if (modifier != null) {
                JsonObject modObj = new JsonObject();
                modObj.add("ingredient", modifier.ingredient().toJson());
                modObj.addProperty("count", modifier.count());
                json.add("modifier", modObj);
            }

            if (additive != null) {
                JsonObject addObj = new JsonObject();
                addObj.add("ingredient", additive.ingredient().toJson());
                addObj.addProperty("count", additive.count());
                json.add("additive", addObj);
            }

            JsonObject contObj = new JsonObject();
            contObj.add("ingredient", container.ingredient().toJson());
            contObj.addProperty("count", container.count());
            json.add("container", contObj);

            JsonObject resultObj = new JsonObject();
            resultObj.addProperty("item", BuiltInRegistries.ITEM.getKey(result.getItem()).toString());
            if (result.getCount() > 1) resultObj.addProperty("count", result.getCount());
            json.add("result", resultObj);

            if (experience > 0F) json.addProperty("experience", experience);
            json.addProperty("cookingtime", cookingTime);
        }

        @Override public ResourceLocation getId() { return id; }

        @Override
        public RecipeSerializer<?> getType() {
            return net.langball.coffee.init.ModRecipeTypes.COFFEE_BREWING_SERIALIZER.get();
        }

        @Nullable @Override public JsonObject serializeAdvancement() { return null; }
        @Nullable @Override public ResourceLocation getAdvancementId() { return null; }
    }
}
