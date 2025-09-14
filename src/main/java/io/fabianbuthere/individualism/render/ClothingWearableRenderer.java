package io.fabianbuthere.individualism.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import io.fabianbuthere.individualism.data.ModelParser;
import io.fabianbuthere.individualism.item.custom.ClothingItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

public class ClothingWearableRenderer {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Map<ResourceLocation, WearModel> modelCache = new HashMap<>();

    public static void init() {
        LOGGER.info("Initializing ClothingWearableRenderer");
        MinecraftForge.EVENT_BUS.register(ClothingWearableRenderer.class);
    }

    @SubscribeEvent
    public static void onRenderPlayer(RenderPlayerEvent.Post event) {
        Player player = event.getEntity();
        PoseStack poseStack = event.getPoseStack();
        float partialTick = event.getPartialTick();
        MultiBufferSource buffers = event.getMultiBufferSource();

        for (EquipmentSlot slot : EquipmentSlot.values()) {
            if (slot.getType() != EquipmentSlot.Type.ARMOR) continue;

            ItemStack stack = player.getItemBySlot(slot);
            if (stack.getItem() instanceof ClothingItem clothingItem) {
                renderClothingItem(player, clothingItem, poseStack, buffers, partialTick);
            }
        }
    }

    private static void renderClothingItem(Player player, ClothingItem clothingItem,
                                           PoseStack poseStack, MultiBufferSource buffers,
                                           float partialTick) {
        ResourceLocation modelLoc = clothingItem.getWearModel();
        ResourceLocation textureLoc = clothingItem.getWearTexture();

        if (modelLoc == null || textureLoc == null) {
            LOGGER.warn("Clothing item missing model or texture: {}", clothingItem);
            return;
        }

        WearModel model = getOrLoadModel(modelLoc);
        if (model == null) {
            return;
        }

        poseStack.pushPose();
        applyPlayerTransforms(player, poseStack, clothingItem, partialTick);

        VertexConsumer buffer = buffers.getBuffer(RenderType.entityCutout(textureLoc));

        int light = getEntityLight(player);
        model.render(poseStack, buffer, player, textureLoc, light, partialTick);

        poseStack.popPose();
    }

    private static void applyPlayerTransforms(Player player, PoseStack poseStack,
                                              ClothingItem clothingItem, float partialTick) {
        ArmorItem.Type slotType = clothingItem.getType();

        switch (slotType) {
            case HELMET -> {
                poseStack.translate(0, 0, 0);
                if (player.isCrouching()) {
                    poseStack.translate(0, -0.25, 0);
                }
            }
            case CHESTPLATE -> {
                poseStack.translate(0, 0.7, 0);
                if (player.isCrouching()) {
                    poseStack.translate(0, -0.25, 0.1);
                }
            }
            case LEGGINGS -> {
                poseStack.translate(0, 0.5, 0);
                if (player.isCrouching()) {
                    poseStack.translate(0, -0.15, 0.1);
                }
            }
            case BOOTS -> {
                poseStack.translate(0, 0.25, 0);
                if (player.isCrouching()) {
                    poseStack.translate(0, 0, 0.1);
                }
            }
        }
    }

    private static WearModel getOrLoadModel(ResourceLocation modelLoc) {
        if (modelCache.containsKey(modelLoc)) {
            return modelCache.get(modelLoc);
        }

        try {
            ResourceManager resourceManager = Minecraft.getInstance().getResourceManager();
            WearModel model = ModelParser.parseModel(resourceManager, modelLoc);

            if (model != null) {
                modelCache.put(modelLoc, model);
                return model;
            }
        } catch (Exception e) {
            LOGGER.error("Failed to load model {}: {}", modelLoc, e.getMessage());
        }

        return null;
    }

    private static int getEntityLight(Player player) {
        return Minecraft.getInstance().getEntityRenderDispatcher().getPackedLightCoords(player, 0);
    }

    public static void clearCache() {
        modelCache.clear();
    }
}
