package net.langball.coffee.block;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import net.langball.coffee.CoffeeWork;
import net.minecraft.resources.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Server-safe drink-to-model ID registry.  Reads
 * {@code assets/coffeework/drink_display_models.json} from the classpath
 * so that {@link #canDisplay(ResourceLocation)} works everywhere
 * (dedicated server, client, DataGen).
 */
public final class DrinkDisplayRegistry {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Gson GSON = new GsonBuilder().create();
    private static final Map<ResourceLocation, ResourceLocation> DRINK_TO_MODEL = new HashMap<>();
    private static volatile boolean loaded = false;

    private DrinkDisplayRegistry() {}

    public static synchronized void ensureLoaded() {
        if (loaded) return;
        loaded = true;
        DRINK_TO_MODEL.clear();

        String path = "/data/coffeework/drink_display_models.json";
        try (InputStream is = DrinkDisplayRegistry.class.getResourceAsStream(path)) {
            if (is == null) {
                LOGGER.error("DrinkDisplayRegistry: resource not found at {}", path);
                return;
            }
            try (Reader reader = new InputStreamReader(is, StandardCharsets.UTF_8)) {
                @SuppressWarnings("unchecked")
                Map<String, Object> root = GSON.fromJson(reader,
                        new TypeToken<Map<String, Object>>() {}.getType());
                @SuppressWarnings("unchecked")
                Map<String, String> drinks = (Map<String, String>) root.get("drinks");
                if (drinks != null) {
                    for (var entry : drinks.entrySet()) {
                        ResourceLocation drinkId = new ResourceLocation(entry.getKey());
                        ResourceLocation modelLoc = new ResourceLocation(entry.getValue());
                        DRINK_TO_MODEL.put(drinkId, modelLoc);
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.error("DrinkDisplayRegistry: failed to load", e);
        }
        LOGGER.info("DrinkDisplayRegistry: {} drink→model mappings loaded", DRINK_TO_MODEL.size());
    }

    public static boolean canDisplay(ResourceLocation drinkId) {
        ensureLoaded();
        return DRINK_TO_MODEL.containsKey(drinkId);
    }

    @Nullable
    public static ResourceLocation getModelLocation(ResourceLocation drinkId) {
        ensureLoaded();
        return DRINK_TO_MODEL.get(drinkId);
    }

    public static Set<ResourceLocation> getRegisteredDrinks() {
        ensureLoaded();
        return Collections.unmodifiableSet(DRINK_TO_MODEL.keySet());
    }

    public static int size() {
        ensureLoaded();
        return DRINK_TO_MODEL.size();
    }

    /** Reset and reload on /reload (data pack refresh). */
    public static synchronized void reset() {
        loaded = false;
        DRINK_TO_MODEL.clear();
    }
}
