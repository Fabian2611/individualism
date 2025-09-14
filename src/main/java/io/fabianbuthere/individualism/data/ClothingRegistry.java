package io.fabianbuthere.individualism.data;

import io.fabianbuthere.individualism.Individualism;
import io.fabianbuthere.individualism.item.custom.ClothingItem;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

public class ClothingRegistry {
    private static final Logger LOGGER = LogManager.getLogger();
    private final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Individualism.MOD_ID);
    private final Map<String, RegistryObject<ClothingItem>> registeredClothing = new HashMap<>();
    private final ArmorMaterial CLOTHING_MATERIAL;

    public ClothingRegistry(ArmorMaterial clothingMaterial) {
        this.CLOTHING_MATERIAL = clothingMaterial;
    }

    public void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }

    public void registerClothing(String id, ClothingLoader.ClothingData data) {
        String registryName = id.toLowerCase().replace(' ', '_');

        RegistryObject<ClothingItem> registryObject = ITEMS.register(registryName, () -> {
            return new ClothingItem(
                    CLOTHING_MATERIAL,
                    data.getSlotType(),
                    new Item.Properties(),
                    data.getWearModel(),
                    data.getWearTexture(),
                    data.getName(),
                    data.getItemTexture()
            );
        });

        registeredClothing.put(id, registryObject);
        LOGGER.info("Registered clothing item: {}", id);
    }

    public void clearDynamicItems() {
        registeredClothing.clear();
    }

    public Map<String, RegistryObject<ClothingItem>> getRegisteredClothing() {
        return registeredClothing;
    }

    public RegistryObject<ClothingItem> getClothing(String id) {
        return registeredClothing.get(id);
    }
}
