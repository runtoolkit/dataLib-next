package net.datalib;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleResourceReloadListener;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.ResourceManager;
import java.io.BufferedReader;
import java.util.HashMap;
import java.util.Map;
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

    private static class FlagReloadListener
            implements SimpleResourceReloadListener<Map<ResourceLocation, Boolean>> {

        @Override
        public ResourceLocation getFabricId() {
            return ResourceLocation.fromNamespaceAndPath(MOD_ID, "feature_flag_reload");
        }

        @Override
        public CompletableFuture<Map<ResourceLocation, Boolean>> load(
                ResourceManager manager, Executor executor
        ) {
            return CompletableFuture.supplyAsync(() -> {
                Map<ResourceLocation, Boolean> loaded = new HashMap<>();
                // Gömülü flag'ler — modun kendi JAR'ına dahil, kullanıcı datapack'ine bağımlı değil
                loaded.put(ResourceLocation.fromNamespaceAndPath(MOD_ID, "my_new_flag"), true);

                manager.listResources(
                        "data_packs",
                        id -> id.getPath().endsWith(".json")
                ).forEach((id, resource) -> {
                    try (BufferedReader reader = resource.openAsReader()) {
                        JsonObject json = GSON.fromJson(reader, JsonObject.class);
                        if (json == null || !json.has("flag_name") || !json.has("enabled")) return;
                        ResourceLocation flagId = ResourceLocation.fromNamespaceAndPath(
                                id.getNamespace(), json.get("flag_name").getAsString());
                        loaded.put(flagId, json.get("enabled").getAsBoolean());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
                return loaded;
            }, executor);
        }

        @Override
        public CompletableFuture<Void> apply(
                Map<ResourceLocation, Boolean> data, ResourceManager manager, Executor executor
        ) {
            return CompletableFuture.runAsync(() -> {
                REGISTERED_FLAGS.clear();
                REGISTERED_FLAGS.putAll(data);
            }, executor);
        }
    }
}