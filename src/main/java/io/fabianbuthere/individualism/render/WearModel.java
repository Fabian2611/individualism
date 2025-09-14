package io.fabianbuthere.individualism.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public class WearModel {
    private static final Logger LOGGER = LogManager.getLogger();

    private final String type;
    private final ModelPart[] parts;

    public WearModel(String type, ModelPart[] parts) {
        this.type = type;
        this.parts = parts;
    }

    public void render(PoseStack poseStack, VertexConsumer buffer, Player player,
                       ResourceLocation textureLocation, int light, float partialTicks) {
        if (parts == null || parts.length == 0) {
            return;
        }

        poseStack.pushPose();
        poseStack.scale(1.0f, 1.0f, 1.0f);

        for (ModelPart part : parts) {
            part.render(poseStack, buffer, textureLocation, light);
        }

        poseStack.popPose();
    }

    public String getType() {
        return type;
    }

    public ModelPart[] getParts() {
        return parts;
    }

    public static class ModelPart {
        private final String name;
        private final float[] from;
        private final float[] to;
        private final float[] rotation;
        private final float[] origin;
        private final Face[] faces;

        public ModelPart(String name, float[] from, float[] to, float[] rotation,
                         float[] origin, Face[] faces) {
            this.name = name;
            this.from = from;
            this.to = to;
            this.rotation = rotation;
            this.origin = origin;
            this.faces = faces;
        }

        public void render(PoseStack poseStack, VertexConsumer buffer,
                           ResourceLocation textureLocation, int light) {
            poseStack.pushPose();

            poseStack.translate(origin[0] / 16.0f, origin[1] / 16.0f, origin[2] / 16.0f);

            if (rotation != null && rotation.length >= 3) {
                poseStack.mulPose(new org.joml.Quaternionf().rotateXYZ(
                        (float)Math.toRadians(rotation[0]),
                        (float)Math.toRadians(rotation[1]),
                        (float)Math.toRadians(rotation[2])
                ));
            }

            poseStack.translate(-origin[0] / 16.0f, -origin[1] / 16.0f, -origin[2] / 16.0f);

            RenderType renderType = null;

            try {
                renderType = RenderType.entityCutout(textureLocation);
            } catch (Exception e) {
                LOGGER.error("Failed to get texture for model part: {}", e.getMessage());
                poseStack.popPose();
                return;
            }

            for (Face face : faces) {
                face.render(poseStack, buffer, from, to, light);
            }

            poseStack.popPose();
        }
    }

    public static class Face {
        private final String direction;
        private final float[] uv;

        public Face(String direction, float[] uv) {
            this.direction = direction;
            this.uv = uv;
        }

        public void render(PoseStack poseStack, VertexConsumer buffer,
                           float[] from, float[] to, int light) {
            float x1 = from[0] / 16.0f;
            float y1 = from[1] / 16.0f;
            float z1 = from[2] / 16.0f;
            float x2 = to[0] / 16.0f;
            float y2 = to[1] / 16.0f;
            float z2 = to[2] / 16.0f;

            float u1 = uv != null && uv.length > 0 ? uv[0] / 16.0f : 0;
            float v1 = uv != null && uv.length > 1 ? uv[1] / 16.0f : 0;
            float u2 = uv != null && uv.length > 2 ? uv[2] / 16.0f : 1;
            float v2 = uv != null && uv.length > 3 ? uv[3] / 16.0f : 1;

            Matrix4f matrix = poseStack.last().pose();
            Matrix3f normalMatrix = poseStack.last().normal();

            switch (direction.toLowerCase()) {
                case "north" -> {
                    Vector3f normal = new Vector3f(0, 0, -1);
                    normal.normalize();

                    buffer.vertex(matrix, x1, y2, z1).color(255, 255, 255, 255)
                            .uv(u1, v1).overlayCoords(0, 0).uv2(light)
                            .normal(normalMatrix, normal.x(), normal.y(), normal.z()).endVertex();

                    buffer.vertex(matrix, x1, y1, z1).color(255, 255, 255, 255)
                            .uv(u1, v2).overlayCoords(0, 0).uv2(light)
                            .normal(normalMatrix, normal.x(), normal.y(), normal.z()).endVertex();

                    buffer.vertex(matrix, x2, y1, z1).color(255, 255, 255, 255)
                            .uv(u2, v2).overlayCoords(0, 0).uv2(light)
                            .normal(normalMatrix, normal.x(), normal.y(), normal.z()).endVertex();

                    buffer.vertex(matrix, x2, y2, z1).color(255, 255, 255, 255)
                            .uv(u2, v1).overlayCoords(0, 0).uv2(light)
                            .normal(normalMatrix, normal.x(), normal.y(), normal.z()).endVertex();
                }
                case "south" -> {
                    Vector3f normal = new Vector3f(0, 0, 1);
                    normal.normalize();

                    buffer.vertex(matrix, x2, y2, z2).color(255, 255, 255, 255)
                            .uv(u1, v1).overlayCoords(0, 0).uv2(light)
                            .normal(normalMatrix, normal.x(), normal.y(), normal.z()).endVertex();

                    buffer.vertex(matrix, x2, y1, z2).color(255, 255, 255, 255)
                            .uv(u1, v2).overlayCoords(0, 0).uv2(light)
                            .normal(normalMatrix, normal.x(), normal.y(), normal.z()).endVertex();

                    buffer.vertex(matrix, x1, y1, z2).color(255, 255, 255, 255)
                            .uv(u2, v2).overlayCoords(0, 0).uv2(light)
                            .normal(normalMatrix, normal.x(), normal.y(), normal.z()).endVertex();

                    buffer.vertex(matrix, x1, y2, z2).color(255, 255, 255, 255)
                            .uv(u2, v1).overlayCoords(0, 0).uv2(light)
                            .normal(normalMatrix, normal.x(), normal.y(), normal.z()).endVertex();
                }
                case "east" -> {
                    Vector3f normal = new Vector3f(1, 0, 0);
                    normal.normalize();

                    buffer.vertex(matrix, x2, y2, z1).color(255, 255, 255, 255)
                            .uv(u1, v1).overlayCoords(0, 0).uv2(light)
                            .normal(normalMatrix, normal.x(), normal.y(), normal.z()).endVertex();

                    buffer.vertex(matrix, x2, y1, z1).color(255, 255, 255, 255)
                            .uv(u1, v2).overlayCoords(0, 0).uv2(light)
                            .normal(normalMatrix, normal.x(), normal.y(), normal.z()).endVertex();

                    buffer.vertex(matrix, x2, y1, z2).color(255, 255, 255, 255)
                            .uv(u2, v2).overlayCoords(0, 0).uv2(light)
                            .normal(normalMatrix, normal.x(), normal.y(), normal.z()).endVertex();

                    buffer.vertex(matrix, x2, y2, z2).color(255, 255, 255, 255)
                            .uv(u2, v1).overlayCoords(0, 0).uv2(light)
                            .normal(normalMatrix, normal.x(), normal.y(), normal.z()).endVertex();
                }
                case "west" -> {
                    Vector3f normal = new Vector3f(-1, 0, 0);
                    normal.normalize();

                    buffer.vertex(matrix, x1, y2, z2).color(255, 255, 255, 255)
                            .uv(u1, v1).overlayCoords(0, 0).uv2(light)
                            .normal(normalMatrix, normal.x(), normal.y(), normal.z()).endVertex();

                    buffer.vertex(matrix, x1, y1, z2).color(255, 255, 255, 255)
                            .uv(u1, v2).overlayCoords(0, 0).uv2(light)
                            .normal(normalMatrix, normal.x(), normal.y(), normal.z()).endVertex();

                    buffer.vertex(matrix, x1, y1, z1).color(255, 255, 255, 255)
                            .uv(u2, v2).overlayCoords(0, 0).uv2(light)
                            .normal(normalMatrix, normal.x(), normal.y(), normal.z()).endVertex();

                    buffer.vertex(matrix, x1, y2, z1).color(255, 255, 255, 255)
                            .uv(u2, v1).overlayCoords(0, 0).uv2(light)
                            .normal(normalMatrix, normal.x(), normal.y(), normal.z()).endVertex();
                }
                case "up" -> {
                    Vector3f normal = new Vector3f(0, 1, 0);
                    normal.normalize();

                    buffer.vertex(matrix, x1, y2, z2).color(255, 255, 255, 255)
                            .uv(u1, v1).overlayCoords(0, 0).uv2(light)
                            .normal(normalMatrix, normal.x(), normal.y(), normal.z()).endVertex();

                    buffer.vertex(matrix, x1, y2, z1).color(255, 255, 255, 255)
                            .uv(u1, v2).overlayCoords(0, 0).uv2(light)
                            .normal(normalMatrix, normal.x(), normal.y(), normal.z()).endVertex();

                    buffer.vertex(matrix, x2, y2, z1).color(255, 255, 255, 255)
                            .uv(u2, v2).overlayCoords(0, 0).uv2(light)
                            .normal(normalMatrix, normal.x(), normal.y(), normal.z()).endVertex();

                    buffer.vertex(matrix, x2, y2, z2).color(255, 255, 255, 255)
                            .uv(u2, v1).overlayCoords(0, 0).uv2(light)
                            .normal(normalMatrix, normal.x(), normal.y(), normal.z()).endVertex();
                }
                case "down" -> {
                    Vector3f normal = new Vector3f(0, -1, 0);
                    normal.normalize();

                    buffer.vertex(matrix, x1, y1, z1).color(255, 255, 255, 255)
                            .uv(u1, v1).overlayCoords(0, 0).uv2(light)
                            .normal(normalMatrix, normal.x(), normal.y(), normal.z()).endVertex();

                    buffer.vertex(matrix, x1, y1, z2).color(255, 255, 255, 255)
                            .uv(u1, v2).overlayCoords(0, 0).uv2(light)
                            .normal(normalMatrix, normal.x(), normal.y(), normal.z()).endVertex();

                    buffer.vertex(matrix, x2, y1, z2).color(255, 255, 255, 255)
                            .uv(u2, v2).overlayCoords(0, 0).uv2(light)
                            .normal(normalMatrix, normal.x(), normal.y(), normal.z()).endVertex();

                    buffer.vertex(matrix, x2, y1, z1).color(255, 255, 255, 255)
                            .uv(u2, v1).overlayCoords(0, 0).uv2(light)
                            .normal(normalMatrix, normal.x(), normal.y(), normal.z()).endVertex();
                }
            }
        }
    }
}
