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

/**
 * Generic JSON serializer for {@link MachineRecipe}.
 *
 * <p>One instance is created per machine type during registration
 * ({@link net.langball.coffee.init.ModRecipeTypes}), each holding a
 * reference to the corresponding {@link net.minecraft.world.item.crafting.RecipeType}
 * so that deserialized recipes carry the correct type.
 *
 * <p>Expected JSON format:
 * <pre>{@code
 * {
 *   "type": "coffeework:grinding",
 *   "group": "optional",
 *   "ingredient": { "item": "..." } | { "tag": "..." },
 *   "result": { "item": "...", "count": 1 },
 *   "experience": 0.2,
 *   "cookingtime": 200
 * }
 * }</pre>
 *
 * <h3>Validation rules</h3>
 * <ul>
 *   <li>{@code ingredient} must be present and non-empty.</li>
 *   <li>{@code result.item} must be a valid item.</li>
 *   <li>{@code result.count} must be between 1 and the item's max stack size.</li>
 *   <li>{@code experience} must be finite and &ge; 0.</li>
 *   <li>{@code cookingtime} must be between 1 and 72000 (1 hour real-time).</li>
 * </ul>
 * Network decoding applies the same defensive checks and rejects invalid data
 * instead of silently correcting it.
 */
public final class MachineRecipeSerializer implements RecipeSerializer<MachineRecipe> {

    /** Maximum allowed cooking time in ticks (1 hour at 20 TPS). */
    private static final int MAX_COOKING_TIME = 72000;

    private final net.minecraft.world.item.crafting.RecipeType<MachineRecipe> recipeType;

    /**
     * @param recipeType the RecipeType that every recipe deserialized by this
     *                   serializer will carry.  Must match the type returned
     *                   by the serialiser's registry name.
     */
    public MachineRecipeSerializer(net.minecraft.world.item.crafting.RecipeType<MachineRecipe> recipeType) {
        this.recipeType = recipeType;
    }

    @Override
    @NotNull
    public MachineRecipe fromJson(@NotNull ResourceLocation id, @NotNull JsonObject json) {
        String group = GsonHelper.getAsString(json, "group", "");

        // --- ingredient validation ---
        if (!json.has("ingredient") || json.get("ingredient").isJsonNull()) {
            throw new JsonParseException("Machine recipe '" + id + "' is missing required field 'ingredient'");
        }
        Ingredient ingredient = Ingredient.fromJson(json.get("ingredient"));

        // --- result validation ---
        if (!json.has("result") || json.get("result").isJsonNull()) {
            throw new JsonParseException("Machine recipe '" + id + "' is missing required field 'result'");
        }
        ItemStack result = ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(json, "result"));

        // --- experience ---
        float experience = GsonHelper.getAsFloat(json, "experience", 0.0F);

        // --- cookingtime ---
        int cookingTime = GsonHelper.getAsInt(json, "cookingtime", 200);

        // Shared validation (reused by fromNetwork as well)
        validateRecipeData(id, ingredient, result, experience, cookingTime);

        return new MachineRecipe(id, group, ingredient, result, experience, cookingTime, recipeType, this);
    }

    @Override
    public MachineRecipe fromNetwork(@NotNull ResourceLocation id, @NotNull FriendlyByteBuf buf) {
        String group = buf.readUtf();
        Ingredient ingredient = Ingredient.fromNetwork(buf);
        ItemStack result = buf.readItem();
        float experience = buf.readFloat();
        int cookingTime = buf.readVarInt();

        // Shared validation (same rules as fromJson)
        validateRecipeData(id, ingredient, result, experience, cookingTime);

        return new MachineRecipe(id, group, ingredient, result, experience, cookingTime, recipeType, this);
    }

    @Override
    public void toNetwork(@NotNull FriendlyByteBuf buf, @NotNull MachineRecipe recipe) {
        buf.writeUtf(recipe.group());
        recipe.ingredient().toNetwork(buf);
        buf.writeItem(recipe.result());
        buf.writeFloat(recipe.experience());
        buf.writeVarInt(recipe.cookingTime());
    }

    // ---- Shared validation -----------------------------------------------

    /**
     * Validates recipe data regardless of source (JSON or network).
     * Throws {@link IllegalArgumentException} on invalid data so both
     * paths fail fast with a clear message that includes the recipe ID.
     */
    static void validateRecipeData(ResourceLocation id, Ingredient ingredient,
                                    ItemStack result, float experience, int cookingTime) {
        if (ingredient.isEmpty()) {
            throw new IllegalArgumentException(
                    "Machine recipe '" + id + "' has an empty ingredient");
        }
        if (result.isEmpty()) {
            throw new IllegalArgumentException(
                    "Machine recipe '" + id + "' result is empty");
        }
        int count = result.getCount();
        int maxStack = result.getMaxStackSize();
        if (count < 1 || count > maxStack) {
            throw new IllegalArgumentException(
                    "Machine recipe '" + id + "' result count " + count
                    + " is out of range [1, " + maxStack + "]");
        }
        if (!Float.isFinite(experience) || experience < 0.0F) {
            throw new IllegalArgumentException(
                    "Machine recipe '" + id + "' experience " + experience
                    + " must be finite and >= 0");
        }
        if (cookingTime < 1 || cookingTime > MAX_COOKING_TIME) {
            throw new IllegalArgumentException(
                    "Machine recipe '" + id + "' cookingtime " + cookingTime
                    + " is out of range [1, " + MAX_COOKING_TIME + "]");
        }
    }
}
