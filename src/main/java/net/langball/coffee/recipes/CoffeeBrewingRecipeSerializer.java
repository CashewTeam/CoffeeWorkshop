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
 * JSON serializer for {@link CoffeeBrewingRecipe}.
 *
 * <h3>JSON format</h3>
 * <pre>{@code
 * {
 *   "type": "coffeework:coffee_brewing",
 *   "base": { "ingredient": {"item": "coffeework:coffee_powder"}, "count": 1 },
 *   "modifier": { "ingredient": {"item": "minecraft:water_bucket"}, "count": 1 },
 *   "container": { "ingredient": {"item": "coffeework:cup"}, "count": 1 },
 *   "result": { "item": "coffeework:coffee_americano", "count": 1 },
 *   "experience": 0.2,
 *   "cookingtime": 120
 * }
 * }</pre>
 *
 * <p>When {@code "modifier"} is absent, the modifier slot MUST be
 * empty (e.g. Espresso).
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

        return new CoffeeBrewingRecipe(id, group, base, modifier, container, result, experience, cookingTime);
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
        SlotIngredient container = readSlotIngredient(buf);
        ItemStack result = buf.readItem();
        float experience = buf.readFloat();
        int cookingTime = buf.readVarInt();

        MachineRecipeSerializer.validateRecipeData(id, base.ingredient(), result, experience, cookingTime);

        return new CoffeeBrewingRecipe(id, group, base, modifier, container, result, experience, cookingTime);
    }

    @Override
    public void toNetwork(@NotNull FriendlyByteBuf buf, @NotNull CoffeeBrewingRecipe recipe) {
        buf.writeUtf(recipe.group());
        writeSlotIngredient(buf, recipe.base());
        buf.writeBoolean(recipe.modifier() != null);
        if (recipe.modifier() != null) writeSlotIngredient(buf, recipe.modifier());
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
