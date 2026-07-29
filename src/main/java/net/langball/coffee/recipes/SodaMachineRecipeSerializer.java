package net.langball.coffee.recipes;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.registries.ForgeRegistries;

public class SodaMachineRecipeSerializer implements RecipeSerializer<SodaMachineRecipe> {
    public static final SodaMachineRecipeSerializer INSTANCE = new SodaMachineRecipeSerializer();

    @Override
    public SodaMachineRecipe fromJson(ResourceLocation id, JsonObject json) {
        String group = GsonHelper.getAsString(json, "group", "");

        Ingredient container = Ingredient.fromJson(json.get("container"));
        Ingredient base = Ingredient.fromJson(json.get("base"));
        Ingredient flavor = Ingredient.fromJson(json.get("flavor"));

        JsonObject resultJson = GsonHelper.getAsJsonObject(json, "result");
        ItemStack result = new ItemStack(
                ForgeRegistries.ITEMS.getValue(new ResourceLocation(GsonHelper.getAsString(resultJson, "item"))),
                GsonHelper.getAsInt(resultJson, "count", 1)
        );

        float experience = GsonHelper.getAsFloat(json, "experience", 0.0F);
        int cookingTime = GsonHelper.getAsInt(json, "processing_time", 200);

        return new SodaMachineRecipe(id, group, container, base, flavor, result, experience, cookingTime);
    }

    @Override
    public SodaMachineRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buf) {
        String group = buf.readUtf();
        Ingredient container = Ingredient.fromNetwork(buf);
        Ingredient base = Ingredient.fromNetwork(buf);
        Ingredient flavor = Ingredient.fromNetwork(buf);
        ItemStack result = buf.readItem();
        float experience = buf.readFloat();
        int cookingTime = buf.readVarInt();
        return new SodaMachineRecipe(id, group, container, base, flavor, result, experience, cookingTime);
    }

    @Override
    public void toNetwork(FriendlyByteBuf buf, SodaMachineRecipe recipe) {
        buf.writeUtf(recipe.group());
        recipe.container().toNetwork(buf);
        recipe.base().toNetwork(buf);
        recipe.flavor().toNetwork(buf);
        buf.writeItem(recipe.result());
        buf.writeFloat(recipe.experience());
        buf.writeVarInt(recipe.cookingTime());
    }
}
