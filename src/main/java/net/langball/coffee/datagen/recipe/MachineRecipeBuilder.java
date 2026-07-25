package net.langball.coffee.datagen.recipe;

import com.google.gson.JsonObject;
import net.langball.coffee.CoffeeWork;
import net.langball.coffee.init.ModRecipeTypes;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

/**
 * DataGen builder for {@code MachineRecipe} (single-input/single-output
 * processing recipes for Grinder, Oven, Roller, and Icecream Machine).
 *
 * <h3>Usage</h3>
 * <pre>{@code
 * MachineRecipeBuilder.grinding(Ingredient.of(Items.WHEAT), new ItemStack(ModItems.FLOUR.get()))
 *         .experience(0.1F)
 *         .cookingTime(200)
 *         .save(writer, CoffeeWork.id("grinding/flour"));
 * }</pre>
 *
 * <p>The {@code "type"} field in the output JSON is determined by the
 * factory method used (e.g. {@code grinding(...)} sets
 * {@code "coffeework:grinding"}).
 */
public class MachineRecipeBuilder {

    private final RecipeSerializer<?> serializer;
    private final Ingredient ingredient;
    private final ItemStack result;
    private float experience = 0.0F;
    private int cookingTime = 200;
    private String group = "";

    private MachineRecipeBuilder(RecipeSerializer<?> serializer, Ingredient ingredient, ItemStack result) {
        this.serializer = serializer;
        this.ingredient = ingredient;
        this.result = result;
    }

    // ── Factory methods ─────────────────────────────────────────────────

    public static MachineRecipeBuilder grinding(Ingredient ingredient, ItemStack result) {
        return new MachineRecipeBuilder(ModRecipeTypes.GRINDING_SERIALIZER.get(), ingredient, result);
    }

    public static MachineRecipeBuilder ovenBaking(Ingredient ingredient, ItemStack result) {
        return new MachineRecipeBuilder(ModRecipeTypes.OVEN_BAKING_SERIALIZER.get(), ingredient, result);
    }

    public static MachineRecipeBuilder rolling(Ingredient ingredient, ItemStack result) {
        return new MachineRecipeBuilder(ModRecipeTypes.ROLLING_SERIALIZER.get(), ingredient, result);
    }

    public static MachineRecipeBuilder icecreamMaking(Ingredient ingredient, ItemStack result) {
        return new MachineRecipeBuilder(ModRecipeTypes.ICECREAM_MAKING_SERIALIZER.get(), ingredient, result);
    }

    // ── Setters ─────────────────────────────────────────────────────────

    public MachineRecipeBuilder experience(float experience) {
        this.experience = experience;
        return this;
    }

    public MachineRecipeBuilder cookingTime(int cookingTime) {
        this.cookingTime = cookingTime;
        return this;
    }

    public MachineRecipeBuilder group(String group) {
        this.group = group;
        return this;
    }

    // ── Build & save ────────────────────────────────────────────────────

    /**
     * Validates and saves the recipe via {@code writer}.
     *
     * @throws IllegalArgumentException if the recipe data is invalid
     */
    public void save(Consumer<FinishedRecipe> writer, ResourceLocation id) {
        validate(id);
        writer.accept(new Result(id));
    }

    private void validate(ResourceLocation id) {
        if (id == null) throw new IllegalArgumentException("Recipe ID must not be null");
        if (ingredient.isEmpty()) throw new IllegalArgumentException("Recipe '" + id + "' has empty ingredient");
        if (result.isEmpty()) throw new IllegalArgumentException("Recipe '" + id + "' has empty result");
        int count = result.getCount();
        int max = result.getMaxStackSize();
        if (count < 1 || count > max)
            throw new IllegalArgumentException("Recipe '" + id + "' result count " + count + " out of [1," + max + "]");
        if (!Float.isFinite(experience) || experience < 0F)
            throw new IllegalArgumentException("Recipe '" + id + "' experience " + experience + " must be >= 0");
        if (cookingTime < 1 || cookingTime > 72000)
            throw new IllegalArgumentException("Recipe '" + id + "' cookingTime " + cookingTime + " out of [1,72000]");
    }

    // ── FinishedRecipe impl ─────────────────────────────────────────────

    private class Result implements FinishedRecipe {
        private final ResourceLocation id;

        Result(ResourceLocation id) { this.id = id; }

        @Override public void serializeRecipeData(JsonObject json) {
            if (!group.isEmpty()) json.addProperty("group", group);
            json.add("ingredient", ingredient.toJson());
            JsonObject resultObj = new JsonObject();
            resultObj.addProperty("item", net.minecraft.core.registries.BuiltInRegistries.ITEM.getKey(result.getItem()).toString());
            if (result.getCount() > 1) resultObj.addProperty("count", result.getCount());
            json.add("result", resultObj);
            if (experience > 0F) json.addProperty("experience", experience);
            json.addProperty("cookingtime", cookingTime);
        }

        @Override public ResourceLocation getId() { return id; }

        @Override public RecipeSerializer<?> getType() { return serializer; }

        @Nullable @Override public JsonObject serializeAdvancement() { return null; }

        @Nullable @Override public ResourceLocation getAdvancementId() { return null; }
    }
}
