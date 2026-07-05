package net.datalib;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;

import java.io.BufferedReader;
import java.util.Map;
import java.util.HashMap;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class Main implements ModInitializer {

    public static final String MOD_ID = "datalib";
    private static final Gson GSON = new Gson();

    public static final Map<ResourceLocation, Boolean> REGISTERED_FLAGS = new HashMap<>();

    @Override
    public void onInitialize() {

        ResourceManagerHelper.get(PackType.SERVER_DATA)
                .registerReloadListener(new FlagReloadListener());
    }

    public static class FlagReloadListener implements IdentifiableResourceReloadListener {

        @Override
        public ResourceLocation getFabricId() {
            return ResourceLocation.fromNamespaceAndPath(
                    MOD_ID,
                    "feature_flag_reload"
            );
        }

        @Override
        public CompletableFuture<Void> reload(
                PreparationBarrier barrier,
                ResourceManager resourceManager,
                ProfilerFiller preparationsProfiler,
                ProfilerFiller reloadProfiler,
                Executor backgroundExecutor,
                Executor gameExecutor
        ) {

            return CompletableFuture
                    .runAsync(() -> {

                        REGISTERED_FLAGS.clear();

                        Map<ResourceLocation, Resource> resources =
                                resourceManager.listResources(
                                        "data_packs",
                                        location -> location.getPath().endsWith(".json")
                                );

                        for (Map.Entry<ResourceLocation, Resource> entry : resources.entrySet()) {

                            try (BufferedReader reader = entry.getValue().openAsReader()) {

                                JsonObject json =
                                        GSON.fromJson(reader, JsonObject.class);

                                if (json == null)
                                    continue;

                                if (!json.has("flag_name"))
                                    continue;

                                if (!json.has("enabled"))
                                    continue;

                                String namespace =
                                        entry.getKey().getNamespace();

                                String flag =
                                        json.get("flag_name").getAsString();

                                boolean enabled =
                                        json.get("enabled").getAsBoolean();

                                ResourceLocation id =
                                        ResourceLocation.fromNamespaceAndPath(
                                                namespace,
                                                flag
                                        );

                                REGISTERED_FLAGS.put(id, enabled);

                                System.out.println(
                                        "[FeatureFlags] Registered "
                                                + id
                                                + " = "
                                                + enabled
                                );

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                    }, backgroundExecutor)
                    .thenCompose(barrier::wait);
        }
    }
}