package net.langball.coffee.recipes;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
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
 */
public final class MachineRecipeSerializer implements RecipeSerializer<MachineRecipe> {

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
        Ingredient ingredient = Ingredient.fromJson(json.get("ingredient"));
        ItemStack result = ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(json, "result"));
        float experience = GsonHelper.getAsFloat(json, "experience", 0.0F);
        int cookingTime = GsonHelper.getAsInt(json, "cookingtime", 200);
        return new MachineRecipe(id, group, ingredient, result, experience, cookingTime, recipeType, this);
    }

    @Override
    public MachineRecipe fromNetwork(@NotNull ResourceLocation id, @NotNull FriendlyByteBuf buf) {
        String group = buf.readUtf();
        Ingredient ingredient = Ingredient.fromNetwork(buf);
        ItemStack result = buf.readItem();
        float experience = buf.readFloat();
        int cookingTime = buf.readVarInt();
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
}
