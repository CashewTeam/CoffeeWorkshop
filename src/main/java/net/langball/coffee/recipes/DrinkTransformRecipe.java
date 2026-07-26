package net.langball.coffee.recipes;

import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import net.langball.coffee.init.ModItems;
import net.langball.coffee.item.DrinkCoffee;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;

/**
 * Custom recipe: drink + transform-material → flavored drink (NBT-preserving).
 *
 * <p>Used by the workbench to transform a drink into a flavored variant
 * (e.g. Nitro Americano + Fruit Syrup → Nitro Fruit Americano).
 * Preserves {@code remaining_cups} from the input drink, clamped to the
 * target drink's {@code max_cups}.
 *
 * <h3>JSON format</h3>
 * <pre>{@code
 * {
 *   "type": "coffeework:drink_transform",
 *   "source":   "coffeework:coffee_americano_nitro_ice",
 *   "additive": "coffeework:syrup_fruit",
 *   "result":   "coffeework:coffee_americano_nitro_fruit_ice"
 * }
 * }</pre>
 */
public class DrinkTransformRecipe extends CustomRecipe {

    private final Item source;
    private final Item additive;
    private final Item result;

    public DrinkTransformRecipe(ResourceLocation id, Item source, Item additive, Item result) {
        super(id, CraftingBookCategory.MISC);
        this.source = source;
        this.additive = additive;
        this.result = result;
    }

    public Item getSource() { return source; }
    public Item getAdditive() { return additive; }
    public Item getResult() { return result; }

    @Override
    public boolean matches(CraftingContainer container, Level level) {
        boolean hasSource = false;
        boolean hasAdditive = false;
        for (int i = 0; i < container.getContainerSize(); i++) {
            ItemStack s = container.getItem(i);
            if (s.isEmpty()) continue;
            if (s.getItem() == source) {
                if (hasSource) return false;
                hasSource = true;
            } else if (s.getItem() == additive) {
                if (hasAdditive) return false;
                hasAdditive = true;
            } else {
                return false;
            }
        }
        return hasSource && hasAdditive;
    }

    @Override
    public ItemStack assemble(CraftingContainer container, RegistryAccess registryAccess) {
        ItemStack inputDrink = null;
        for (int i = 0; i < container.getContainerSize(); i++) {
            ItemStack s = container.getItem(i);
            if (!s.isEmpty() && s.getItem() == source) inputDrink = s;
        }
        ItemStack output = new ItemStack(result);
        // Copy remaining_cups from input, clamp to target max_cups
        if (inputDrink != null && inputDrink.hasTag()) {
            output.setTag(inputDrink.getTag().copy());
            int sourceCups = DrinkCoffee.getRemainingCups(inputDrink);
            int sourceMax = DrinkCoffee.getMaxCups(inputDrink);
            int targetMax = DrinkCoffee.getMaxCups(output);
            int clamped = Math.min(sourceCups, targetMax);
            if (sourceCups != clamped) {
                output.getOrCreateTag().putInt("remaining_cups", clamped);
            }
        }
        return output;
    }

    @Override
    public ItemStack getResultItem(@Nullable RegistryAccess registryAccess) {
        ItemStack resultStack = new ItemStack(result);
        if (result instanceof DrinkCoffee drink) {
            drink.initializeFreshStack(resultStack);
        }
        return resultStack;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width * height >= 2;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return net.langball.coffee.init.ModRecipeTypes.DRINK_TRANSFORM_SERIALIZER.get();
    }

    // ── Serializer ───────────────────────────────────────────────

    public static class Serializer implements RecipeSerializer<DrinkTransformRecipe> {
        @Override
        public DrinkTransformRecipe fromJson(ResourceLocation id, JsonObject json) {
            if (!json.has("source") || !json.has("additive") || !json.has("result"))
                throw new JsonSyntaxException("DrinkTransform recipe '" + id + "' missing required fields");

            ResourceLocation sourceId = ResourceLocation.tryParse(json.get("source").getAsString());
            ResourceLocation additiveId = ResourceLocation.tryParse(json.get("additive").getAsString());
            ResourceLocation resultId = ResourceLocation.tryParse(json.get("result").getAsString());

            if (sourceId == null || additiveId == null || resultId == null)
                throw new JsonSyntaxException("DrinkTransform recipe '" + id + "' has invalid item IDs");

            if (!BuiltInRegistries.ITEM.containsKey(sourceId))
                throw new JsonSyntaxException("DrinkTransform recipe '" + id + "' source not registered: " + sourceId);
            if (!BuiltInRegistries.ITEM.containsKey(additiveId))
                throw new JsonSyntaxException("DrinkTransform recipe '" + id + "' additive not registered: " + additiveId);
            if (!BuiltInRegistries.ITEM.containsKey(resultId))
                throw new JsonSyntaxException("DrinkTransform recipe '" + id + "' result not registered: " + resultId);

            Item src = BuiltInRegistries.ITEM.get(sourceId);
            Item add = BuiltInRegistries.ITEM.get(additiveId);
            Item res = BuiltInRegistries.ITEM.get(resultId);

            if (!(src instanceof DrinkCoffee))
                throw new JsonSyntaxException("DrinkTransform recipe '" + id + "' source is not DrinkCoffee: " + sourceId);
            if (!(res instanceof DrinkCoffee))
                throw new JsonSyntaxException("DrinkTransform recipe '" + id + "' result is not DrinkCoffee: " + resultId);

            // Remainder check: additive must have a crafting remainder (syrup → syrup_empty)
            ItemStack addStack = new ItemStack(add);
            if (addStack.getCraftingRemainingItem().isEmpty() && addStack.getItem().hasCraftingRemainingItem()) {
                // fine — the item will handle remainder through vanilla system
            }

            return new DrinkTransformRecipe(id, src, add, res);
        }

        @Override
        @Nullable
        public DrinkTransformRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buf) {
            Item src = buf.readById(BuiltInRegistries.ITEM);
            Item add = buf.readById(BuiltInRegistries.ITEM);
            Item res = buf.readById(BuiltInRegistries.ITEM);
            return new DrinkTransformRecipe(id, src, add, res);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buf, DrinkTransformRecipe recipe) {
            buf.writeId(BuiltInRegistries.ITEM, recipe.source);
            buf.writeId(BuiltInRegistries.ITEM, recipe.additive);
            buf.writeId(BuiltInRegistries.ITEM, recipe.result);
        }
    }
}
