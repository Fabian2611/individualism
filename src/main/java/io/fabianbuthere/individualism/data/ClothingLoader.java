package io.fabianbuthere.individualism.data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import io.fabianbuthere.individualism.Individualism;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.item.ArmorItem;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class ClothingLoader extends SimplePreparableReloadListener<Map<String, ClothingLoader.ClothingData>> {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Gson GSON = new GsonBuilder().create();
    private static final String BASE_PATH = "data/" + Individualism.MOD_ID + "/clothing";
    private static final String ITEM_JSON = "item.json";
    private final ClothingRegistry registry;

    public ClothingLoader(ClothingRegistry registry) {
        this.registry = registry;
    }

    @Override
    protected Map<String, ClothingData> prepare(ResourceManager resourceManager, ProfilerFiller profiler) {
        profiler.push("Loading clothing data");
        Map<String, ClothingData> clothingDataMap = new HashMap<>();
        Set<String> clothingDirs = getClothingDirectories(resourceManager);

        for (String dir : clothingDirs) {
            try {
                ClothingData data = loadClothingData(resourceManager, dir);
                if (data != null) {
                    clothingDataMap.put(dir, data);
                    LOGGER.info("Loaded clothing data for: {}", dir);
                }
            } catch (Exception e) {
                LOGGER.error("Failed to load clothing data for {}: {}", dir, e.getMessage());
            }
        }

        profiler.pop();
        return clothingDataMap;
    }

    @Override
    protected void apply(Map<String, ClothingData> clothingDataMap, ResourceManager resourceManager, ProfilerFiller profiler) {
        profiler.push("Registering clothing items");
        registry.clearDynamicItems();

        for (Map.Entry<String, ClothingData> entry : clothingDataMap.entrySet()) {
            String id = entry.getKey();
            ClothingData data = entry.getValue();
            registry.registerClothing(id, data);
        }

        profiler.pop();
    }

    private Set<String> getClothingDirectories(ResourceManager resourceManager) {
        Set<String> dirs = new HashSet<>();

        resourceManager.listResources(BASE_PATH, loc -> true).forEach((location, resource) -> {
            String path = location.getPath();
            if (path.startsWith(BASE_PATH + "/")) {
                String relativePath = path.substring(BASE_PATH.length() + 1);
                int firstSlash = relativePath.indexOf('/');
                if (firstSlash > 0) {
                    dirs.add(relativePath.substring(0, firstSlash));
                }
            }
        });

        return dirs;
    }

    @SuppressWarnings("removal")
    private ClothingData loadClothingData(ResourceManager resourceManager, String clothingId) {
        ResourceLocation itemJsonLoc = new ResourceLocation(Individualism.MOD_ID,
                BASE_PATH + "/" + clothingId + "/" + ITEM_JSON);

        JsonObject itemJson = loadJsonResource(resourceManager, itemJsonLoc);
        if (itemJson == null) {
            LOGGER.error("Missing item.json for clothing: {}", clothingId);
            return null;
        }

        String name = GsonHelper.getAsString(itemJson, "name", clothingId);
        String slotStr = GsonHelper.getAsString(itemJson, "slot", "chest");
        ArmorItem.Type slotType = parseSlotType(slotStr);

        String itemTexturePath = GsonHelper.getAsString(itemJson, "item", "individualism:clothing/" + clothingId + "/item.png");
        String modelPath = GsonHelper.getAsString(itemJson, "model", "individualism:clothing/" + clothingId + "/model.json");
        String texturePath = GsonHelper.getAsString(itemJson, "texture", "individualism:clothing/" + clothingId + "/model.png");

        ResourceLocation itemTextureLoc = new ResourceLocation(itemTexturePath);
        ResourceLocation modelLoc = new ResourceLocation(modelPath);
        ResourceLocation textureLoc = new ResourceLocation(texturePath);

        return new ClothingData(name, slotType, itemTextureLoc, modelLoc, textureLoc);
    }

    private JsonObject loadJsonResource(ResourceManager resourceManager, ResourceLocation location) {
        try {
            Optional<Resource> resourceOptional = resourceManager.getResource(location);
            if (resourceOptional.isEmpty()) {
                return null;
            }

            try (
                    InputStream inputStream = resourceOptional.get().open();
                    Reader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))
            ) {
                return GsonHelper.parse(reader);
            }
        } catch (Exception e) {
            LOGGER.error("Error loading JSON resource {}: {}", location, e.getMessage());
            return null;
        }
    }

    private ArmorItem.Type parseSlotType(String slotStr) {
        return switch (slotStr.toLowerCase()) {
            case "head", "helmet" -> ArmorItem.Type.HELMET;
            case "chest", "chestplate" -> ArmorItem.Type.CHESTPLATE;
            case "legs", "leggings" -> ArmorItem.Type.LEGGINGS;
            case "feet", "boots" -> ArmorItem.Type.BOOTS;
            default -> ArmorItem.Type.CHESTPLATE;
        };
    }

    public static class ClothingData {
        private final String name;
        private final ArmorItem.Type slotType;
        private final ResourceLocation itemTexture;
        private final ResourceLocation wearModel;
        private final ResourceLocation wearTexture;

        public ClothingData(String name, ArmorItem.Type slotType,
                            ResourceLocation itemTexture,
                            ResourceLocation wearModel,
                            ResourceLocation wearTexture) {
            this.name = name;
            this.slotType = slotType;
            this.itemTexture = itemTexture;
            this.wearModel = wearModel;
            this.wearTexture = wearTexture;
        }

        public String getName() {
            return name;
        }

        public ArmorItem.Type getSlotType() {
            return slotType;
        }

        public ResourceLocation getItemTexture() {
            return itemTexture;
        }

        public ResourceLocation getWearModel() {
            return wearModel;
        }

        public ResourceLocation getWearTexture() {
            return wearTexture;
        }
    }
}
