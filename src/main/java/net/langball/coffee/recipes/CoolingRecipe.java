package net.langball.coffee.recipes;

import com.google.gson.JsonObject;
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
                if (hasHot) return false;
                hasHot = true;
            } else if (s.is(net.langball.coffee.init.ModItems.ICE_SLAG.get())) {
                if (hasIce) return false;
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
        if (inputDrink != null && inputDrink.hasTag()) {
            output.setTag(inputDrink.getTag().copy());
        }
        return output;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return true;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return net.langball.coffee.init.ModRecipeTypes.COOLING_SERIALIZER.get();
    }

    public static class Serializer implements RecipeSerializer<CoolingRecipe> {
        @Override
        public CoolingRecipe fromJson(ResourceLocation id, JsonObject json) {
            Item hot = BuiltInRegistries.ITEM.get(new ResourceLocation(json.get("hot").getAsString()));
            Item iced = BuiltInRegistries.ITEM.get(new ResourceLocation(json.get("iced").getAsString()));
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
