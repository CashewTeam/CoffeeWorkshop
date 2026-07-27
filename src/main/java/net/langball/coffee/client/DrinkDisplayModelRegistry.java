package net.langball.coffee.client;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import net.langball.coffee.CoffeeWork;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ModelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@OnlyIn(Dist.CLIENT)
@Mod.EventBusSubscriber(modid = CoffeeWork.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class DrinkDisplayModelRegistry {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Gson GSON = new GsonBuilder().create();
    private static final Map<ResourceLocation, ResourceLocation> DRINK_TO_MODEL = new ConcurrentHashMap<>();
    private static volatile boolean loaded = false;

    @SubscribeEvent
    public static void registerAdditionalModels(ModelEvent.RegisterAdditional event) {
        ensureLoaded();
        for (ResourceLocation modelLoc : getRegisteredModels()) {
            event.register(modelLoc);
        }
        LOGGER.info("DrinkDisplayModelRegistry: registered {} extra models", getRegisteredModels().size());
    }

    private DrinkDisplayModelRegistry() {}

    public static synchronized void ensureLoaded() {
        if (loaded) return;
        loaded = true;
        DRINK_TO_MODEL.clear();

        String path = "/data/coffeework/drink_display_models.json";
        try (InputStream is = DrinkDisplayModelRegistry.class.getResourceAsStream(path)) {
            if (is == null) {
                LOGGER.error("DrinkDisplayModelRegistry: resource not found at {}", path);
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
            LOGGER.error("DrinkDisplayModelRegistry: failed to load mappings", e);
        }
        LOGGER.info("DrinkDisplayModelRegistry: {} drink→model mappings loaded", DRINK_TO_MODEL.size());
    }

    public static void loadModels(ResourceManager rm) {
        ensureLoaded();
    }

    @Nullable
    public static ResourceLocation getModelLocation(ResourceLocation drinkId) {
        ensureLoaded();
        return DRINK_TO_MODEL.get(drinkId);
    }

    @Nullable
    public static BakedModel getModel(ResourceLocation drinkId) {
        ResourceLocation modelLoc = getModelLocation(drinkId);
        if (modelLoc == null) return null;
        Minecraft mc = Minecraft.getInstance();
        if (mc == null || mc.getModelManager() == null) return null;
        return mc.getModelManager().getModel(modelLoc);
    }

    public static boolean canDisplay(ResourceLocation drinkId) {
        ensureLoaded();
        return DRINK_TO_MODEL.containsKey(drinkId);
    }

    public static Set<ResourceLocation> getRegisteredDrinks() {
        ensureLoaded();
        return Set.copyOf(DRINK_TO_MODEL.keySet());
    }

    public static Set<ResourceLocation> getRegisteredModels() {
        ensureLoaded();
        return DRINK_TO_MODEL.values().stream().collect(Collectors.toUnmodifiableSet());
    }

    /** Reset and reload on resource reload (data pack refresh / F3+T). */
    public static synchronized void reset() {
        loaded = false;
        DRINK_TO_MODEL.clear();
    }
}
