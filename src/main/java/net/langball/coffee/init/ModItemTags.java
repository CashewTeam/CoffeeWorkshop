package net.langball.coffee.init;

import net.langball.coffee.CoffeeWork;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

/**
 * Mod-specific item tags for fruit/berry abstraction and other
 * data-driven ingredient grouping.
 */
public final class ModItemTags {

    /** Berries: sweet_berries, glow_berries, coffeework:blueberry */
    public static final TagKey<Item> BERRIES = create("fruits/berries");

    /** General fruits: #coffeework:fruits/berries + apple, melon, lemon */
    public static final TagKey<Item> FRUITS = create("fruits");

    private static TagKey<Item> create(String path) {
        return TagKey.create(Registries.ITEM, CoffeeWork.id(path));
    }

    private ModItemTags() {}
}
