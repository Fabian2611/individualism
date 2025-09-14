package io.fabianbuthere.individualism.data;

import com.google.gson.*;
import io.fabianbuthere.individualism.render.WearModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.GsonHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ModelParser {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Gson GSON = new GsonBuilder().create();

    public static WearModel parseModel(ResourceManager resourceManager, ResourceLocation modelLocation) {
        try {
            Optional<Resource> resourceOptional = resourceManager.getResource(modelLocation);
            if (resourceOptional.isEmpty()) {
                LOGGER.error("Failed to load model: {}", modelLocation);
                return null;
            }

            try (
                    InputStream inputStream = resourceOptional.get().open();
                    Reader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))
            ) {
                JsonObject json = GsonHelper.parse(reader);
                return deserializeModel(json);
            }
        } catch (Exception e) {
            LOGGER.error("Error parsing model {}: {}", modelLocation, e.getMessage());
            return null;
        }
    }

    private static WearModel deserializeModel(JsonObject json) {
        String type = GsonHelper.getAsString(json, "type", "custom");
        List<WearModel.ModelPart> parts = new ArrayList<>();

        if (json.has("elements") && json.get("elements").isJsonArray()) {
            JsonArray elements = json.getAsJsonArray("elements");
            for (JsonElement element : elements) {
                if (element.isJsonObject()) {
                    JsonObject partJson = element.getAsJsonObject();

                    String name = GsonHelper.getAsString(partJson, "name", "part");
                    float[] from = parseFloatArray(partJson, "from");
                    float[] to = parseFloatArray(partJson, "to");

                    float[] rotation = new float[3];
                    if (partJson.has("rotation")) {
                        if (partJson.get("rotation").isJsonArray()) {
                            rotation = parseFloatArray(partJson, "rotation", new float[]{0, 0, 0});
                        } else if (partJson.get("rotation").isJsonObject()) {
                            JsonObject rotObj = partJson.getAsJsonObject("rotation");
                            float angle = GsonHelper.getAsFloat(rotObj, "angle", 0f);
                            String axis = GsonHelper.getAsString(rotObj, "axis", "y");

                            switch (axis) {
                                case "x" -> rotation[0] = angle;
                                case "y" -> rotation[1] = angle;
                                case "z" -> rotation[2] = angle;
                            }
                        }
                    }

                    float[] origin = new float[]{0, 0, 0};
                    if (partJson.has("origin")) {
                        origin = parseFloatArray(partJson, "origin", new float[]{0, 0, 0});
                    } else if (partJson.has("rotation") && partJson.get("rotation").isJsonObject()) {
                        JsonObject rotObj = partJson.getAsJsonObject("rotation");
                        if (rotObj.has("origin")) {
                            origin = parseFloatArray(rotObj, "origin", new float[]{0, 0, 0});
                        }
                    }

                    JsonObject facesJson = GsonHelper.getAsJsonObject(partJson, "faces", new JsonObject());
                    WearModel.Face[] faces = parseFaces(facesJson);

                    parts.add(new WearModel.ModelPart(name, from, to, rotation, origin, faces));
                }
            }
        }

        return new WearModel(type, parts.toArray(new WearModel.ModelPart[0]));
    }

    private static WearModel.Face[] parseFaces(JsonObject facesJson) {
        List<WearModel.Face> faces = new ArrayList<>();
        String[] directions = {"north", "east", "south", "west", "up", "down"};

        for (String direction : directions) {
            if (facesJson.has(direction)) {
                JsonObject faceJson = facesJson.getAsJsonObject(direction);
                float[] uv = parseFloatArray(faceJson, "uv", new float[]{0, 0, 16, 16});

                faces.add(new WearModel.Face(direction, uv));
            }
        }

        return faces.toArray(new WearModel.Face[0]);
    }

    private static float[] parseFloatArray(JsonObject json, String key) {
        return parseFloatArray(json, key, null);
    }

    private static float[] parseFloatArray(JsonObject json, String key, float[] defaultValue) {
        if (!json.has(key) || !json.get(key).isJsonArray()) {
            return defaultValue;
        }

        JsonArray array = json.getAsJsonArray(key);
        float[] result = new float[array.size()];

        for (int i = 0; i < array.size(); i++) {
            result[i] = array.get(i).getAsFloat();
        }

        return result;
    }
}
