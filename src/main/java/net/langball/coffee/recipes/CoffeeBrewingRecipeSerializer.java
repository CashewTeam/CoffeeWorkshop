package net.langball.coffee.recipes;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapedRecipe;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * JSON serializer for {@link CoffeeBrewingRecipe} (v2 — 5-slot).
 *
 * <h3>JSON format</h3>
 * <pre>{@code
 * {
 *   "type": "coffeework:coffee_brewing",
 *   "base": { "ingredient": {"item": "coffeework:coffee_powder"}, "count": 1 },
 *   "modifier": { "ingredient": {"item": "minecraft:water_bucket"}, "count": 1 },
 *   "additive": { "ingredient": {"item": "coffeework:cocoa_powder"}, "count": 1 },
 *   "container": { "ingredient": {"item": "coffeework:cup"}, "count": 1 },
 *   "result": { "item": "coffeework:coffee_mochaccino", "count": 1 },
 *   "experience": 0.3,
 *   "cookingtime": 140
 * }
 * }</pre>
 *
 * <p>Both {@code "modifier"} and {@code "additive"} are optional.
 * When absent, the corresponding slot MUST be empty.
 */
public final class CoffeeBrewingRecipeSerializer implements RecipeSerializer<CoffeeBrewingRecipe> {

    @Override
    @NotNull
    public CoffeeBrewingRecipe fromJson(@NotNull ResourceLocation id, @NotNull JsonObject json) {
        String group = GsonHelper.getAsString(json, "group", "");

        // Base (required)
        if (!json.has("base")) throw new JsonParseException("Coffee recipe '" + id + "' missing 'base'");
        SlotIngredient base = parseSlotIngredient(GsonHelper.getAsJsonObject(json, "base"));

        // Modifier (optional — absent means "must be empty")
        SlotIngredient modifier = null;
        if (json.has("modifier") && !json.get("modifier").isJsonNull()) {
            modifier = parseSlotIngredient(GsonHelper.getAsJsonObject(json, "modifier"));
        }

        // Additive (optional — absent means "must be empty")
        SlotIngredient additive = null;
        if (json.has("additive") && !json.get("additive").isJsonNull()) {
            additive = parseSlotIngredient(GsonHelper.getAsJsonObject(json, "additive"));
        }

        // Container (required)
        if (!json.has("container")) throw new JsonParseException("Coffee recipe '" + id + "' missing 'container'");
        SlotIngredient container = parseSlotIngredient(GsonHelper.getAsJsonObject(json, "container"));

        // Result
        ItemStack result = ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(json, "result"));
        if (result.isEmpty()) throw new JsonParseException("Coffee recipe '" + id + "' has empty result");

        float experience = GsonHelper.getAsFloat(json, "experience", 0F);
        int cookingTime = GsonHelper.getAsInt(json, "cookingtime", 200);

        // Shared validation
        MachineRecipeSerializer.validateRecipeData(id, base.ingredient(), result, experience, cookingTime);

        return new CoffeeBrewingRecipe(id, group, base, modifier, additive, container, result, experience, cookingTime);
    }

    private static SlotIngredient parseSlotIngredient(JsonObject json) {
        Ingredient ingredient = Ingredient.fromJson(json.get("ingredient"));
        int count = GsonHelper.getAsInt(json, "count", 1);
        return new SlotIngredient(ingredient, count);
    }

    @Override
    public CoffeeBrewingRecipe fromNetwork(@NotNull ResourceLocation id, @NotNull FriendlyByteBuf buf) {
        String group = buf.readUtf();
        SlotIngredient base = readSlotIngredient(buf);
        boolean hasModifier = buf.readBoolean();
        SlotIngredient modifier = hasModifier ? readSlotIngredient(buf) : null;
        boolean hasAdditive = buf.readBoolean();
        SlotIngredient additive = hasAdditive ? readSlotIngredient(buf) : null;
        SlotIngredient container = readSlotIngredient(buf);
        ItemStack result = buf.readItem();
        float experience = buf.readFloat();
        int cookingTime = buf.readVarInt();

        MachineRecipeSerializer.validateRecipeData(id, base.ingredient(), result, experience, cookingTime);

        return new CoffeeBrewingRecipe(id, group, base, modifier, additive, container, result, experience, cookingTime);
    }

    @Override
    public void toNetwork(@NotNull FriendlyByteBuf buf, @NotNull CoffeeBrewingRecipe recipe) {
        buf.writeUtf(recipe.group());
        writeSlotIngredient(buf, recipe.base());
        buf.writeBoolean(recipe.modifier() != null);
        if (recipe.modifier() != null) writeSlotIngredient(buf, recipe.modifier());
        buf.writeBoolean(recipe.additive() != null);
        if (recipe.additive() != null) writeSlotIngredient(buf, recipe.additive());
        writeSlotIngredient(buf, recipe.container());
        buf.writeItem(recipe.result());
        buf.writeFloat(recipe.experience());
        buf.writeVarInt(recipe.cookingTime());
    }

    private static SlotIngredient readSlotIngredient(FriendlyByteBuf buf) {
        Ingredient ingredient = Ingredient.fromNetwork(buf);
        int count = buf.readVarInt();
        return new SlotIngredient(ingredient, count);
    }

    private static void writeSlotIngredient(FriendlyByteBuf buf, SlotIngredient si) {
        si.ingredient().toNetwork(buf);
        buf.writeVarInt(si.count());
    }
}
