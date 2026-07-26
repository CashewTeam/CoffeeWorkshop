package net.langball.coffee.recipes;

import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import net.minecraft.core.registries.BuiltInRegistries;

import javax.annotation.Nullable;

/**
 * Custom recipe: hot drink + ice_slag → iced drink (NBT-preserving).
 *
 * <p>Used by the workbench to convert a brewed hot drink into its iced
 * counterpart when both items exist in the registry.  Preserves the
 * {@code remaining_cups} NBT to prevent the cups-duplication exploit.
 *
 * <h3>JSON format</h3>
 * <pre>{@code
 * {
 *   "type": "coffeework:cooling",
 *   "hot":  "coffeework:coffee_latte",
 *   "iced": "coffeework:coffee_latte_ice"
 * }
 * }</pre>
 *
 * <p>Validity: both items must be registered {@code DrinkCoffee}s and
 * must not be the same item.
 */
public class CoolingRecipe extends CustomRecipe {

    private final Item hotDrink;
    private final Item icedDrink;

    public CoolingRecipe(ResourceLocation id, Item hotDrink, Item icedDrink) {
        super(id, CraftingBookCategory.MISC);
        this.hotDrink = hotDrink;
        this.icedDrink = icedDrink;
    }

    public Item getHotDrink() { return hotDrink; }
    public Item getIcedDrink() { return icedDrink; }

    @Override
    public boolean matches(CraftingContainer container, Level level) {
        boolean hasHot = false;
        boolean hasIce = false;
        for (int i = 0; i < container.getContainerSize(); i++) {
            ItemStack s = container.getItem(i);
            if (s.isEmpty()) continue;
            if (s.getItem() == hotDrink) {
                if (hasHot) return false; // only one hot drink allowed
                hasHot = true;
            } else if (s.is(net.langball.coffee.init.ModItems.ICE_SLAG.get())) {
                if (hasIce) return false; // only one ice slag allowed
                hasIce = true;
            } else {
                return false;
            }
        }
        return hasHot && hasIce;
    }

    @Override
    public ItemStack assemble(CraftingContainer container, RegistryAccess registryAccess) {
        ItemStack inputDrink = null;
        for (int i = 0; i < container.getContainerSize(); i++) {
            ItemStack s = container.getItem(i);
            if (!s.isEmpty() && s.getItem() == hotDrink) inputDrink = s;
        }
        ItemStack output = new ItemStack(icedDrink);
        // Preserve remaining_cups / NBT from input drink
        if (inputDrink != null && inputDrink.hasTag()) {
            output.setTag(inputDrink.getTag().copy());
        }
        return output;
    }

    /** Initialize the displayed result (used by JEI) with fresh cups NBT. */
    @Override
    public ItemStack getResultItem(@Nullable RegistryAccess registryAccess) {
        ItemStack result = new ItemStack(icedDrink);
        if (result.getItem() instanceof net.langball.coffee.item.DrinkCoffee drink) {
            drink.initializeFreshStack(result);
        }
        return result;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width * height >= 2;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return net.langball.coffee.init.ModRecipeTypes.COOLING_SERIALIZER.get();
    }

    // ── Serializer ───────────────────────────────────────────────

    public static class Serializer implements RecipeSerializer<CoolingRecipe> {
        @Override
        public CoolingRecipe fromJson(ResourceLocation id, JsonObject json) {
            if (!json.has("hot") || !json.has("iced"))
                throw new JsonSyntaxException("Cooling recipe '" + id + "' missing 'hot' or 'iced' field");
            ResourceLocation hotId = ResourceLocation.tryParse(json.get("hot").getAsString());
            ResourceLocation icedId = ResourceLocation.tryParse(json.get("iced").getAsString());
            if (hotId == null || icedId == null)
                throw new JsonSyntaxException("Cooling recipe '" + id + "' has invalid item IDs");
            if (!BuiltInRegistries.ITEM.containsKey(hotId))
                throw new JsonSyntaxException("Cooling recipe '" + id + "' hot item not registered: " + hotId);
            if (!BuiltInRegistries.ITEM.containsKey(icedId))
                throw new JsonSyntaxException("Cooling recipe '" + id + "' iced item not registered: " + icedId);
            Item hot = BuiltInRegistries.ITEM.get(hotId);
            Item iced = BuiltInRegistries.ITEM.get(icedId);
            if (hot == iced)
                throw new JsonSyntaxException("Cooling recipe '" + id + "' hot and iced are the same item");
            if (!(hot instanceof net.langball.coffee.item.DrinkCoffee))
                throw new JsonSyntaxException("Cooling recipe '" + id + "' hot item is not DrinkCoffee: " + hotId);
            if (!(iced instanceof net.langball.coffee.item.DrinkCoffee))
                throw new JsonSyntaxException("Cooling recipe '" + id + "' iced item is not DrinkCoffee: " + icedId);
            return new CoolingRecipe(id, hot, iced);
        }

        @Override
        @Nullable
        public CoolingRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buf) {
            Item hot = buf.readById(BuiltInRegistries.ITEM);
            Item iced = buf.readById(BuiltInRegistries.ITEM);
            return new CoolingRecipe(id, hot, iced);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buf, CoolingRecipe recipe) {
            buf.writeId(BuiltInRegistries.ITEM, recipe.hotDrink);
            buf.writeId(BuiltInRegistries.ITEM, recipe.icedDrink);
        }
    }
}
