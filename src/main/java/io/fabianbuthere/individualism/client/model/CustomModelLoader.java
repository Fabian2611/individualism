package io.fabianbuthere.individualism.client.model;

import com.google.gson.*;
import com.mojang.math.Axis;
import io.fabianbuthere.individualism.Individualism;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import java.util.HashMap;
import java.util.Map;

/**
 * Loads and parses custom models from JSON files
 */
public class CustomModelLoader extends SimpleJsonResourceReloadListener {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
    private static final CustomModelLoader INSTANCE = new CustomModelLoader();

    private final Map<ResourceLocation, CustomModel> models = new HashMap<>();

    private CustomModelLoader() {
        super(GSON, "models/custom");
    }

    public static CustomModelLoader getInstance() {
        return INSTANCE;
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> resources, @NotNull ResourceManager resourceManager,
                         @NotNull ProfilerFiller profiler) {
        models.clear();

        profiler.push("Loading custom models");

        for (Map.Entry<ResourceLocation, JsonElement> entry : resources.entrySet()) {
            ResourceLocation modelId = entry.getKey();

            try {
                String path = modelId.getPath();
                if (path.endsWith(".json")) {
                    path = path.substring(0, path.length() - 5);
                }
                ResourceLocation cleanModelId = ResourceLocation.fromNamespaceAndPath(modelId.getNamespace(), path);

                JsonObject modelJson = entry.getValue().getAsJsonObject();

                CustomModel model = parseModel(cleanModelId, modelJson);
                models.put(cleanModelId, model);
                Individualism.LOGGER.info("Loaded custom model: {}", cleanModelId);

            } catch (Exception e) {
                Individualism.LOGGER.error("Failed to load custom model: {}", modelId, e);
            }
        }

        profiler.pop();
        Individualism.LOGGER.info("Loaded {} custom models", models.size());
    }

    private float[] parseFloatArray(JsonArray array, int expectedLength) {
        float[] result = new float[expectedLength];
        for (int i = 0; i < expectedLength && i < array.size(); i++) {
            result[i] = array.get(i).getAsFloat();
        }
        return result;
    }

    private CustomModel parseModel(ResourceLocation modelId, JsonObject modelJson) {
        ResourceLocation textureLocation = parseTextureLocation(modelJson);

        final CustomModel model;
        if (modelJson.has("playerTranslation") && modelJson.get("playerTranslation").isJsonArray()) {
            model = new CustomModel(modelId, textureLocation, parseFloatArray(modelJson.getAsJsonArray("playerTranslation"), 3));
        } else {
            model = new CustomModel(modelId, textureLocation);
        }

        if (modelJson.has("elements") && modelJson.get("elements").isJsonArray()) {
            JsonArray elements = modelJson.getAsJsonArray("elements");
            for (JsonElement elementElem : elements) {
                if (elementElem.isJsonObject()) {
                    JsonObject elementJson = elementElem.getAsJsonObject();
                    CustomModel.ModelElement element = parseElement(elementJson);
                    if (element != null) {
                        model.addElement(element);
                    }
                }
            }
        }

        return model;
    }

    private ResourceLocation parseTextureLocation(JsonObject modelJson) {
        if (modelJson.has("textures") && modelJson.get("textures").isJsonObject()) {
            JsonObject textures = modelJson.getAsJsonObject("textures");

            String texturePath = null;
            if (textures.has("0")) {
                texturePath = textures.get("0").getAsString();
            } else if (textures.has("particle")) {
                texturePath = textures.get("particle").getAsString();
            } else if (!textures.entrySet().isEmpty()) {
                texturePath = textures.entrySet().iterator().next().getValue().getAsString();
            }

            if (texturePath != null) {
                String[] parts = texturePath.split(":");
                if (parts.length == 2) {
                    return ResourceLocation.fromNamespaceAndPath(parts[0], "textures/" + parts[1] + ".png");
                }
            }
        }

        return ResourceLocation.fromNamespaceAndPath(Individualism.MOD_ID,"textures/armor/default.png");
    }

    private CustomModel.ModelElement parseElement(JsonObject elementJson) {
        try {
            String name = elementJson.has("name") ? elementJson.get("name").getAsString() : "unnamed";

            Vector3f from = parseVector3f(elementJson.getAsJsonArray("from"));
            Vector3f to = parseVector3f(elementJson.getAsJsonArray("to"));

            Vector3f rotationOrigin = new Vector3f(8, 8, 8);
            float rotationAngle = 0;
            Axis rotationAxis = null;

            if (elementJson.has("rotation") && elementJson.get("rotation").isJsonObject()) {
                JsonObject rotation = elementJson.getAsJsonObject("rotation");

                if (rotation.has("origin") && rotation.get("origin").isJsonArray()) {
                    rotationOrigin = parseVector3f(rotation.getAsJsonArray("origin"));
                }

                if (rotation.has("angle") && rotation.get("angle").isJsonPrimitive()) {
                    rotationAngle = rotation.get("angle").getAsFloat();
                }

                if (rotation.has("axis") && rotation.get("axis").isJsonPrimitive()) {
                    String axis = rotation.get("axis").getAsString();
                    switch (axis.toLowerCase()) {
                        case "x" -> rotationAxis = Axis.XP;
                        case "y" -> rotationAxis = Axis.YP;
                        case "z" -> rotationAxis = Axis.ZP;
                    }
                }
            }

            CustomModel.ModelElement element = new CustomModel.ModelElement(
                    name, from, to, rotationOrigin, rotationAngle, rotationAxis
            );

            if (elementJson.has("faces") && elementJson.get("faces").isJsonObject()) {
                JsonObject faces = elementJson.getAsJsonObject("faces");

                parseFace(faces, "north", CustomModel.ModelFace.Direction.NORTH, element);
                parseFace(faces, "east", CustomModel.ModelFace.Direction.EAST, element);
                parseFace(faces, "south", CustomModel.ModelFace.Direction.SOUTH, element);
                parseFace(faces, "west", CustomModel.ModelFace.Direction.WEST, element);
                parseFace(faces, "up", CustomModel.ModelFace.Direction.UP, element);
                parseFace(faces, "down", CustomModel.ModelFace.Direction.DOWN, element);
            }

            return element;

        } catch (Exception e) {
            Individualism.LOGGER.error("Error parsing model element", e);
            return null;
        }
    }

    private void parseFace(JsonObject faces, String faceName, CustomModel.ModelFace.Direction direction,
                           CustomModel.ModelElement element) {
        if (faces.has(faceName) && faces.get(faceName).isJsonObject()) {
            JsonObject faceJson = faces.getAsJsonObject(faceName);

            if (faceJson.has("uv") && faceJson.get("uv").isJsonArray()) {
                JsonArray uvArray = faceJson.getAsJsonArray("uv");
                if (uvArray.size() >= 4) {
                    float u1 = uvArray.get(0).getAsFloat() / 16f;
                    float v1 = uvArray.get(1).getAsFloat() / 16f;
                    float u2 = uvArray.get(2).getAsFloat() / 16f;
                    float v2 = uvArray.get(3).getAsFloat() / 16f;

                    CustomModel.ModelFace face = new CustomModel.ModelFace(direction, u1, v1, u2, v2);
                    element.addFace(face);
                }
            }
        }
    }

    private Vector3f parseVector3f(JsonArray array) {
        if (array.size() >= 3) {
            return new Vector3f(
                    array.get(0).getAsFloat(),
                    array.get(1).getAsFloat(),
                    array.get(2).getAsFloat()
            );
        }
        return new Vector3f(0, 0, 0);
    }

    public CustomModel getModel(ResourceLocation modelId) {
        return models.get(modelId);
    }

    public Map<ResourceLocation, CustomModel> getAllModels() {
        return models;
    }
}
