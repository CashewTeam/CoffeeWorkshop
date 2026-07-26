package net.langball.coffee.recipes;

import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import net.langball.coffee.init.ModItems;
import net.langball.coffee.item.DrinkCoffee;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
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
 * target drink's configured {@code maxCups}.
 *
 * <p>Only explicitly allowed NBT keys are copied from source to result
 * (currently: {@code remaining_cups}).  The target max_cups and variant
 * identity always come from the result item, never from the source NBT.
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

        if (inputDrink != null && source instanceof DrinkCoffee srcDrink
                && result instanceof DrinkCoffee tgtDrink) {
            // Read source cups from source item's runtime NBT.
            int sourceCups = inputDrink.hasTag()
                    ? DrinkCoffee.getRemainingCups(inputDrink)
                    : srcDrink.getConfiguredMaxCups();

            // Target max cups from the RESULT item's configuration, NOT from
            // copied NBT.  This prevents a 4-cup source writing max_cups=4
            // into a 3-cup target.
            int targetMax = tgtDrink.getConfiguredMaxCups();
            int remaining = Math.min(sourceCups, targetMax);

            // Only copy explicitly allowed NBT keys — do NOT blindly copy the
            // entire source compound tag, which could leak variant fields,
            // internal state, or stale max_cups into the result.
            CompoundTag tag = new CompoundTag();
            tag.putInt("remaining_cups", remaining);
            tag.putInt("max_cups", targetMax);
            output.setTag(tag);
        } else {
            // No source input (should not happen if matches() passed) or
            // source/result not DrinkCoffee — initialise fresh.
            if (result instanceof DrinkCoffee drink) {
                drink.initializeFreshStack(output);
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

            // Additive MUST have a crafting remainder (e.g. syrup → syrup_empty).
            // This ensures the transform is not a free conversion and the
            // additive container is properly returned.
            if (!add.hasCraftingRemainingItem()) {
                throw new JsonSyntaxException(
                        "DrinkTransform recipe '" + id + "' additive has no crafting remainder: " + additiveId
                        + ".  Additives must return a container (e.g. syrup → syrup_empty).");
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
