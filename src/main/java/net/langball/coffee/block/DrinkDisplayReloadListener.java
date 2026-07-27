package net.langball.coffee.block;

import net.langball.coffee.CoffeeWork;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@Mod.EventBusSubscriber(modid = CoffeeWork.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class DrinkDisplayReloadListener {
    private DrinkDisplayReloadListener() {}

    @SubscribeEvent
    public static void onAddReloadListeners(AddReloadListenerEvent event) {
        event.addListener((PreparableReloadListener) (stage, rm, prep, reload, bg, game) ->
                CompletableFuture.runAsync(() -> {
                    DrinkDisplayRegistry.reset();
                    DrinkDisplayRegistry.ensureLoaded();
                }, bg).thenCompose(stage::wait));
    }
}
