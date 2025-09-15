package io.fabianbuthere.individualism.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import io.fabianbuthere.individualism.Individualism;
import io.fabianbuthere.individualism.client.model.CustomModel;
import io.fabianbuthere.individualism.client.model.CustomModelLoader;
import io.fabianbuthere.individualism.client.model.ModelMetadata;
import io.fabianbuthere.individualism.client.model.ModelMetadataLoader;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Map;

public class PlayerModelRenderer {
    private static boolean initialized = false;

    public static void init() {
        if (!initialized) {
            MinecraftForge.EVENT_BUS.register(PlayerModelRenderer.class);
            initialized = true;
            Individualism.LOGGER.info("Player model renderer initialized");
        }
    }

    @SubscribeEvent
    public static void onRenderPlayer(RenderPlayerEvent.Post event) {
        Player player = event.getEntity();
        PoseStack poseStack = event.getPoseStack();
        float partialTick = event.getPartialTick();
        MultiBufferSource bufferSource = event.getMultiBufferSource();

        Map<ResourceLocation, CustomModel> models = CustomModelLoader.getInstance().getAllModels();

        if (models.isEmpty()) {
            return;
        }

        poseStack.pushPose();

        float yaw = player.yBodyRotO + (player.yBodyRot - player.yBodyRotO) * partialTick;
        poseStack.mulPose(Axis.YP.rotationDegrees(-yaw));
        poseStack.translate(0, player.isCrouching() ? -1.5 / 16.0 : -1.0 / 16.0, 0);

        for (Map.Entry<ResourceLocation, CustomModel> modelEntry : models.entrySet()) {
            ResourceLocation modelId = modelEntry.getKey();
            CustomModel model = modelEntry.getValue();

            // Get metadata for this model
            ModelMetadata metadata = ModelMetadataLoader.getInstance().getMetadata(modelId);
            if (metadata == null) {
                Individualism.LOGGER.error("Missing metadata for model: {}", modelId);
                continue;
            }

            final boolean shouldRender = player.getItemBySlot(metadata.armorSlot().getSlot())
                    .is(ForgeRegistries.ITEMS.getValue(ResourceLocation.bySeparator(metadata.onItem(), ':')));

            if (!shouldRender) continue;

            float offsetX = metadata.offsetX();
            float offsetY = metadata.offsetY();
            float offsetZ = metadata.offsetZ();

            poseStack.pushPose();

            poseStack.translate(offsetX / 16.0, offsetY / 16.0, offsetZ / 16.0);

            model.render(poseStack, bufferSource, event.getPackedLight(), OverlayTexture.NO_OVERLAY);

            poseStack.popPose();
        }

        poseStack.popPose();
    }
}
