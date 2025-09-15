package io.fabianbuthere.individualism.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a parsed Blockbench model that can be rendered
 */
public class CustomModel {
    private final ResourceLocation modelId;
    private final ResourceLocation textureLocation;
    private final List<ModelElement> elements = new ArrayList<>();
    private final float[] renderOffset; // Rendering translation in pixels

    public CustomModel(ResourceLocation modelId, ResourceLocation textureLocation, float[] renderOffset) {
        this.modelId = modelId;
        this.textureLocation = textureLocation;
        this.renderOffset = renderOffset;
    }

    public CustomModel(ResourceLocation modelId, ResourceLocation textureLocation) {
        this(modelId, textureLocation, new float[]{0f, 0f, 0f});
    }

    public ResourceLocation getModelId() {
        return modelId;
    }

    public ResourceLocation getTextureLocation() {
        return textureLocation;
    }

    public void addElement(ModelElement element) {
        elements.add(element);
    }

    public void render(PoseStack poseStack, MultiBufferSource buffer, int packedLight, int packedOverlay) {
        VertexConsumer vertexConsumer = buffer.getBuffer(RenderType.entityTranslucentCull(textureLocation));

        poseStack.pushPose();

        poseStack.scale(1/16f, 1/16f, 1/16f);
        poseStack.translate(renderOffset[0], renderOffset[1], renderOffset[2]);

        for (ModelElement element : elements) {
            element.render(poseStack, vertexConsumer, packedLight, packedOverlay);
        }

        poseStack.popPose();
    }

    /**
     * Represents a single cube in the model
     */
    public static class ModelElement {
        private final String name;
        private final Vector3f from;
        private final Vector3f to;
        private final Vector3f rotationOrigin;
        private final float rotationAngle;
        private final Axis rotationAxis;
        private final List<ModelFace> faces = new ArrayList<>();

        public ModelElement(String name, Vector3f from, Vector3f to,
                            Vector3f rotationOrigin, float rotationAngle, Axis rotationAxis) {
            this.name = name;
            this.from = from;
            this.to = to;
            this.rotationOrigin = rotationOrigin;
            this.rotationAngle = rotationAngle;
            this.rotationAxis = rotationAxis;
        }

        public void addFace(ModelFace face) {
            faces.add(face);
        }

        public void render(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay) {
            poseStack.pushPose();

            if (rotationAxis != null && rotationAngle != 0) {
                poseStack.translate(rotationOrigin.x(), rotationOrigin.y(), rotationOrigin.z());
                poseStack.mulPose(rotationAxis.rotationDegrees(rotationAngle));
                poseStack.translate(-rotationOrigin.x(), -rotationOrigin.y(), -rotationOrigin.z());
            }

            Matrix4f pose = poseStack.last().pose();
            Matrix3f normal = poseStack.last().normal();

            for (ModelFace face : faces) {
                face.render(buffer, pose, normal, packedLight, packedOverlay, from, to);
            }

            poseStack.popPose();
        }
    }

    /**
     * Represents a single face of an element
     */
    public static class ModelFace {
        public enum Direction {
            NORTH, EAST, SOUTH, WEST, UP, DOWN
        }

        private final Direction direction;
        private final float u1, v1, u2, v2;

        public ModelFace(Direction direction, float u1, float v1, float u2, float v2) {
            this.direction = direction;
            this.u1 = u1;
            this.v1 = v1;
            this.u2 = u2;
            this.v2 = v2;
        }

        public void render(VertexConsumer buffer, Matrix4f pose, Matrix3f normal,
                           int packedLight, int packedOverlay, Vector3f from, Vector3f to) {
            float x1 = from.x();
            float y1 = from.y();
            float z1 = from.z();
            float x2 = to.x();
            float y2 = to.y();
            float z2 = to.z();

            float nx = 0, ny = 0, nz = 0;

            // CW winding
            switch (direction) {
                case NORTH -> {
                    // -Z face
                    nz = -1;
                    addVertex(buffer, pose, normal, x1, y2, z1, u1, v1, nx, ny, nz, packedLight, packedOverlay);
                    addVertex(buffer, pose, normal, x2, y2, z1, u2, v1, nx, ny, nz, packedLight, packedOverlay);
                    addVertex(buffer, pose, normal, x2, y1, z1, u2, v2, nx, ny, nz, packedLight, packedOverlay);
                    addVertex(buffer, pose, normal, x1, y1, z1, u1, v2, nx, ny, nz, packedLight, packedOverlay);
                }
                case SOUTH -> {
                    // +Z face
                    nz = 1;
                    addVertex(buffer, pose, normal, x2, y1, z2, u2, v2, nx, ny, nz, packedLight, packedOverlay);
                    addVertex(buffer, pose, normal, x2, y2, z2, u2, v1, nx, ny, nz, packedLight, packedOverlay);
                    addVertex(buffer, pose, normal, x1, y2, z2, u1, v1, nx, ny, nz, packedLight, packedOverlay);
                    addVertex(buffer, pose, normal, x1, y1, z2, u1, v2, nx, ny, nz, packedLight, packedOverlay);
                }
                case WEST -> {
                    // -X face
                    nx = -1;
                    addVertex(buffer, pose, normal, x1, y1, z2, u2, v2, nx, ny, nz, packedLight, packedOverlay);
                    addVertex(buffer, pose, normal, x1, y2, z2, u2, v1, nx, ny, nz, packedLight, packedOverlay);
                    addVertex(buffer, pose, normal, x1, y2, z1, u1, v1, nx, ny, nz, packedLight, packedOverlay);
                    addVertex(buffer, pose, normal, x1, y1, z1, u1, v2, nx, ny, nz, packedLight, packedOverlay);
                }
                case EAST -> {
                    // +X face
                    nx = 1;
                    addVertex(buffer, pose, normal, x2, y1, z1, u2, v2, nx, ny, nz, packedLight, packedOverlay);
                    addVertex(buffer, pose, normal, x2, y2, z1, u2, v1, nx, ny, nz, packedLight, packedOverlay);
                    addVertex(buffer, pose, normal, x2, y2, z2, u1, v1, nx, ny, nz, packedLight, packedOverlay);
                    addVertex(buffer, pose, normal, x2, y1, z2, u1, v2, nx, ny, nz, packedLight, packedOverlay);
                }
                case UP -> {
                    // +Y face
                    ny = 1;
                    addVertex(buffer, pose, normal, x1, y2, z2, u1, v2, nx, ny, nz, packedLight, packedOverlay);
                    addVertex(buffer, pose, normal, x2, y2, z2, u2, v2, nx, ny, nz, packedLight, packedOverlay);
                    addVertex(buffer, pose, normal, x2, y2, z1, u2, v1, nx, ny, nz, packedLight, packedOverlay);
                    addVertex(buffer, pose, normal, x1, y2, z1, u1, v1, nx, ny, nz, packedLight, packedOverlay);
                }
                case DOWN -> {
                    // -Y face
                    ny = -1;
                    addVertex(buffer, pose, normal, x1, y1, z1, u1, v1, nx, ny, nz, packedLight, packedOverlay);
                    addVertex(buffer, pose, normal, x2, y1, z1, u2, v1, nx, ny, nz, packedLight, packedOverlay);
                    addVertex(buffer, pose, normal, x2, y1, z2, u2, v2, nx, ny, nz, packedLight, packedOverlay);
                    addVertex(buffer, pose, normal, x1, y1, z2, u1, v2, nx, ny, nz, packedLight, packedOverlay);
                }
            }
        }

        private void addVertex(VertexConsumer buffer, Matrix4f pose, Matrix3f normal,
                               float x, float y, float z, float u, float v,
                               float nx, float ny, float nz, int packedLight, int packedOverlay) {
            buffer.vertex(pose, x, y, z)
                    .color(255, 255, 255, 255)
                    .uv(u, v)
                    .overlayCoords(packedOverlay)
                    .uv2(packedLight)
                    .normal(normal, nx, ny, nz)
                    .endVertex();
        }
    }
}
