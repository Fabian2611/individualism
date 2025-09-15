package io.fabianbuthere.individualism.client.model;

import com.google.gson.*;
import io.fabianbuthere.individualism.Individualism;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.item.ArmorItem;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

/**
 * Loads metadata for custom models from JSON files
 */
public class ModelMetadataLoader extends SimpleJsonResourceReloadListener {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
    private static final ModelMetadataLoader INSTANCE = new ModelMetadataLoader();

    private final Map<ResourceLocation, ModelMetadata> metadataMap = new HashMap<>();

    private ModelMetadataLoader() {
        super(GSON, "metadata");
    }

    public static ModelMetadataLoader getInstance() {
        return INSTANCE;
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> resources, @NotNull ResourceManager resourceManager,
                         @NotNull ProfilerFiller profiler) {
        metadataMap.clear();

        profiler.push("Loading model metadata");

        for (Map.Entry<ResourceLocation, JsonElement> entry : resources.entrySet()) {
            ResourceLocation metadataId = entry.getKey();

            try {
                // Create model ID without the .json extension
                String path = metadataId.getPath();
                if (path.endsWith(".json")) {
                    path = path.substring(0, path.length() - 5);
                }
                ResourceLocation modelId = ResourceLocation.fromNamespaceAndPath(
                        metadataId.getNamespace(), path);

                JsonObject metadataJson = entry.getValue().getAsJsonObject();

                // Parse the metadata
                ModelMetadata metadata = parseMetadata(modelId, metadataJson);
                metadataMap.put(modelId, metadata);

                Individualism.LOGGER.info("Loaded metadata for model: {}", modelId);

            } catch (Exception e) {
                Individualism.LOGGER.error("Failed to load metadata: {}", metadataId, e);
            }
        }

        profiler.pop();
        Individualism.LOGGER.info("Loaded metadata for {} models", metadataMap.size());
    }

    private ModelMetadata parseMetadata(ResourceLocation modelId, JsonObject json) {
        String name = getStringOrDefault(json, "name", "Unnamed");
        ArmorItem.Type armorSlot = switch (getStringOrDefault(json, "slot", "_")) {
            case "head" -> ArmorItem.Type.HELMET;
            case "chest" -> ArmorItem.Type.CHESTPLATE;
            case "legs" -> ArmorItem.Type.LEGGINGS;
            case "feet" -> ArmorItem.Type.BOOTS;
            default -> ArmorItem.Type.CHESTPLATE;
        };
        String neededItem = getStringOrDefault(json, "item", "minecraft:air");
        JsonArray offset = json.getAsJsonArray("playerTranslation");
        float offsetX = offset.get(0).getAsFloat();
        float offsetY = offset.get(1).getAsFloat();
        float offsetZ = offset.get(2).getAsFloat();
        float variantKey = getFloatOrDefault(json, "variant", 0f);

        return new ModelMetadata(name, offsetX, offsetY, offsetZ, modelId, variantKey, neededItem, armorSlot);
    }

    private String getStringOrDefault(JsonObject json, String key, String defaultValue) {
        if (json.has(key) && json.get(key).isJsonPrimitive() && json.get(key).getAsJsonPrimitive().isString()) {
            return json.get(key).getAsString();
        }
        return defaultValue;
    }

    private float getFloatOrDefault(JsonObject json, String key, float defaultValue) {
        if (json.has(key) && json.get(key).isJsonPrimitive() && json.get(key).getAsJsonPrimitive().isNumber()) {
            return json.get(key).getAsFloat();
        }
        return defaultValue;
    }

    private boolean getBooleanOrDefault(JsonObject json, String key, boolean defaultValue) {
        if (json.has(key) && json.get(key).isJsonPrimitive() && json.get(key).getAsJsonPrimitive().isBoolean()) {
            return json.get(key).getAsBoolean();
        }
        return defaultValue;
    }

    /**
     * Get metadata for a specific model
     */
    public ModelMetadata getMetadata(ResourceLocation modelId) {
        return metadataMap.getOrDefault(modelId, null);
    }

    /**
     * Get all loaded metadata
     */
    public Map<ResourceLocation, ModelMetadata> getAllMetadata() {
        return new HashMap<>(metadataMap);
    }
}
