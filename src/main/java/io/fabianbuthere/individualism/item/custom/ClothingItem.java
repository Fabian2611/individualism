package io.fabianbuthere.individualism.item.custom;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public class ClothingItem extends ArmorItem {
    private final ResourceLocation wearModel;
    private final ResourceLocation wearTexture;
    private final ResourceLocation itemTexture;
    private final String displayName;

    public ClothingItem(ArmorMaterial material, Type type, Properties properties,
                        ResourceLocation wearModel, ResourceLocation wearTexture,
                        String displayName, ResourceLocation itemTexture) {
        super(material, type, properties);
        this.wearModel = wearModel;
        this.wearTexture = wearTexture;
        this.displayName = displayName;
        this.itemTexture = itemTexture;
    }

    @Override
    public @Nullable String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot, String type) {
        return null;
    }

    @Override
    public Component getName(ItemStack stack) {
        return Component.literal(displayName);
    }

    public ResourceLocation getWearModel() {
        return wearModel;
    }

    public ResourceLocation getWearTexture() {
        return wearTexture;
    }

    public ResourceLocation getItemTexture() {
        return itemTexture;
    }

    public String getDisplayName() {
        return displayName;
    }
}
