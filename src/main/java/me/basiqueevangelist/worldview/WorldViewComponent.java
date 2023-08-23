package me.basiqueevangelist.worldview;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.systems.VertexSorter;
import io.wispforest.owo.ui.base.BaseComponent;
import io.wispforest.owo.ui.core.OwoUIDrawContext;
import io.wispforest.owo.ui.core.Size;
import io.wispforest.owo.ui.util.GlDebugUtils;
import io.wispforest.owo.ui.util.OwoGlUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.gl.SimpleFramebuffer;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.RotationAxis;
import org.jetbrains.annotations.ApiStatus;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL32;

public class WorldViewComponent extends BaseComponent {
    @ApiStatus.Internal
    public static Framebuffer CURRENT = null;

    private final MinecraftClient client = MinecraftClient.getInstance();
    private Framebuffer framebuffer = null;

    @Override
    public void inflate(Size space) {
        if (this.framebuffer != null)
            this.framebuffer.delete();

        super.inflate(space);

        this.framebuffer = new SimpleFramebuffer(width, height, true, MinecraftClient.IS_SYSTEM_MAC);
        GlDebugUtils.labelObject(GL32.GL_FRAMEBUFFER, this.framebuffer.fbo, "Framebuffer for " + this);
    }

    @Override
    public void dismount(DismountReason reason) {
        if (this.framebuffer != null)
            this.framebuffer.delete();
    }

    @Override
    public void draw(OwoUIDrawContext context, int mouseX, int mouseY, float partialTicks, float delta) {
        try (var ignored = GlDebugUtils.pushGroup("Drawing world into FB for " + this)) {
            int oldFb = GL32.glGetInteger(GL32.GL_DRAW_FRAMEBUFFER_BINDING);

            int viewportX = GlStateManager.Viewport.getX();
            int viewportY = GlStateManager.Viewport.getY();
            int viewportW = GlStateManager.Viewport.getWidth();
            int viewportH = GlStateManager.Viewport.getHeight();

            framebuffer.beginWrite(true);

            CURRENT = framebuffer;
//            client.gameRenderer.onResized(width, height);

            GlStateManager._disableScissorTest();

            Camera camera = client.gameRenderer.getCamera();
            MatrixStack matrixStack = new MatrixStack();
            double fov = this.client.options.getFov().getValue().intValue();
            matrixStack.multiplyPositionMatrix(client.gameRenderer.getBasicProjectionMatrix(fov));

            Matrix4f matrix4f = matrixStack.peek().getPositionMatrix();
            try (var ignored1 = OwoGlUtil.setProjectionMatrix(matrix4f, VertexSorter.BY_DISTANCE)) {
//                camera.update(
//                    this.client.world,
//                    (Entity)(this.client.getCameraEntity() == null ? this.client.player : this.client.getCameraEntity()),
//                    !this.client.options.getPerspective().isFirstPerson(),
//                    this.client.options.getPerspective().isFrontView(),
//                    partialTicks
//                );
                MatrixStack matrices = new MatrixStack();
                matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(camera.getPitch()));
                matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(camera.getYaw() + 180.0F));
                Matrix3f matrix3f = new Matrix3f(matrices.peek().getNormalMatrix()).invert();
                RenderSystem.setInverseViewRotationMatrix(matrix3f);
                this.client
                    .worldRenderer
                    .setupFrustum(matrices, camera.getPos(), client.gameRenderer.getBasicProjectionMatrix(fov));
                client.worldRenderer.render(matrices, partialTicks, 0, false, camera, client.gameRenderer, client.gameRenderer.getLightmapTextureManager(), matrix4f);
            }

            GlStateManager._glBindFramebuffer(GL32.GL_DRAW_FRAMEBUFFER, oldFb);
            GlStateManager._viewport(viewportX, viewportY, viewportW, viewportH);
            GlStateManager._enableScissorTest();
            CURRENT = null;
//            client.gameRenderer.onResized(client.getWindow().getFramebufferWidth(), client.getWindow().getFramebufferHeight());
        }

        WorldView.WORLD_VIEW_PROGRAM.use();
//        RenderSystem.setShader(GameRenderer);
        RenderSystem.disableBlend();
        RenderSystem.setShaderTexture(0, framebuffer.getColorAttachment());
        Matrix4f matrix4f = context.getMatrices().peek().getPositionMatrix();
        BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();
        bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE);
        bufferBuilder.vertex(matrix4f, x, y, 0).texture(0, 1).next();
        bufferBuilder.vertex(matrix4f, x, y + height, 0).texture(0, 0).next();
        bufferBuilder.vertex(matrix4f, x + width, y + height, 0).texture(1, 0).next();
        bufferBuilder.vertex(matrix4f, x + width, y, 0).texture(1, 1).next();
        BufferRenderer.drawWithGlobalProgram(bufferBuilder.end());
    }
}
