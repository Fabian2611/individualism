package io.fabianbuthere.individualism.client.model;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ArmorItem;

public record ModelMetadata(
        String name,
        float offsetX,
        float offsetY,
        float offsetZ,
        ResourceLocation modelId,
        float variantKey,
        String onItem,
        ArmorItem.Type armorSlot
) {

}
