package net.langball.coffee.init;

import net.langball.coffee.CoffeeWork;
import net.langball.coffee.recipes.CoffeeBrewingRecipeSerializer;
import net.langball.coffee.recipes.MachineRecipe;
import net.langball.coffee.recipes.MachineRecipeSerializer;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

/**
 * Registration for the five machine recipe types and their serializers.
 *
 * <p>Each processing machine (Grinder, CoffeeMachine, IcecreamMachine,
 * Roller, Oven) has its own distinct {@link RecipeType} and
 * {@link RecipeSerializer}, both backed by the same
 * {@link MachineRecipe} / {@link MachineRecipeSerializer} classes.
 *
 * <p>Recipe type IDs match the {@code "type"} field in the JSON recipes
 * ({@code "coffeework:grinding"}, {@code "coffeework:coffee_brewing"}, …).
 */
public final class ModRecipeTypes {

    // ========================================================================
    // Recipe Types
    // ========================================================================

    /** Grinder — coffee beans → powder, wheat → flour, etc. */
    public static final RecipeType<MachineRecipe> GRINDING =
            RecipeType.simple(CoffeeWork.id("grinding"));

    /** Coffee Machine — brews drinks from coffee powder etc. */
    public static final RecipeType<MachineRecipe> COFFEE_BREWING =
            RecipeType.simple(CoffeeWork.id("coffee_brewing"));

    /** Ice-cream Machine — freezes liquids into ice cream. */
    public static final RecipeType<MachineRecipe> ICECREAM_MAKING =
            RecipeType.simple(CoffeeWork.id("icecream_making"));

    /** Roller — flattens dough, plates iron. */
    public static final RecipeType<MachineRecipe> ROLLING =
            RecipeType.simple(CoffeeWork.id("rolling"));

    /** Oven — bakes dough into bread, cookies, etc. */
    public static final RecipeType<MachineRecipe> OVEN_BAKING =
            RecipeType.simple(CoffeeWork.id("oven_baking"));

    // ========================================================================
    // Recipe Serializers  (registered to Forge's registry)
    // ========================================================================

    public static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS =
            DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, CoffeeWork.MODID);

    public static final RegistryObject<MachineRecipeSerializer> GRINDING_SERIALIZER =
            SERIALIZERS.register("grinding", () -> new MachineRecipeSerializer(GRINDING));

    public static final RegistryObject<CoffeeBrewingRecipeSerializer> COFFEE_BREWING_SERIALIZER =
            SERIALIZERS.register("coffee_brewing", CoffeeBrewingRecipeSerializer::new);

    public static final RegistryObject<MachineRecipeSerializer> ICECREAM_MAKING_SERIALIZER =
            SERIALIZERS.register("icecream_making", () -> new MachineRecipeSerializer(ICECREAM_MAKING));

    public static final RegistryObject<MachineRecipeSerializer> ROLLING_SERIALIZER =
            SERIALIZERS.register("rolling", () -> new MachineRecipeSerializer(ROLLING));

    public static final RegistryObject<MachineRecipeSerializer> OVEN_BAKING_SERIALIZER =
            SERIALIZERS.register("oven_baking", () -> new MachineRecipeSerializer(OVEN_BAKING));

    private ModRecipeTypes() {
    }
}
