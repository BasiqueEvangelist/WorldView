package me.basiqueevangelist.worldview;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.systems.VertexSorter;
import io.wispforest.owo.ui.base.BaseComponent;
import io.wispforest.owo.ui.core.OwoUIDrawContext;
import io.wispforest.owo.ui.core.Size;
import io.wispforest.owo.ui.util.GlDebugUtils;
import io.wispforest.owo.ui.util.OwoGlUtil;
import io.wispforest.owo.ui.window.context.CurrentWindowContext;
import me.basiqueevangelist.worldview.mixin.CameraAccessor;
import me.basiqueevangelist.worldview.mixin.MinecraftClientAccessor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.gl.SimpleFramebuffer;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.ApiStatus;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL32;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

public class WorldViewComponent extends BaseComponent {
    @ApiStatus.Internal
    public static Framebuffer CURRENT = null;

    private final MinecraftClient client = MinecraftClient.getInstance();
    Framebuffer framebuffer = null;
    private final List<BiConsumer<Integer, Integer>> resizeListeners = new ArrayList<>();

    private Vec3d position = client.gameRenderer.getCamera().getPos();
    private float yaw = client.gameRenderer.getCamera().getYaw();
    private float pitch = client.gameRenderer.getCamera().getPitch();

    public Vec3d position() {
        return position;
    }

    public float yaw() {
        return yaw;
    }

    public float pitch() {
        return pitch;
    }

    public WorldViewComponent position(Vec3d position) {
        this.position = position;
        return this;
    }

    public WorldViewComponent yaw(float yaw) {
        this.yaw = yaw;
        return this;
    }

    public WorldViewComponent pitch(float pitch) {
        this.pitch = pitch;
        return this;
    }

    @Override
    public void inflate(Size space) {
        if (this.framebuffer != null)
            this.framebuffer.delete();

        super.inflate(space);

        int realWidth = (int) (CurrentWindowContext.current().scaleFactor() * width);
        int realHeight = (int) (CurrentWindowContext.current().scaleFactor() * height);

        this.framebuffer = new SimpleFramebuffer(realWidth, realHeight, true, MinecraftClient.IS_SYSTEM_MAC);
        resizeListeners.forEach(x -> x.accept(realWidth, realHeight));
        GlDebugUtils.labelObject(GL32.GL_FRAMEBUFFER, this.framebuffer.fbo, "Framebuffer for " + this);
    }

    @Override
    public void dismount(DismountReason reason) {
        if (this.framebuffer != null)
            this.framebuffer.delete();
    }

    @Override
    public void draw(OwoUIDrawContext context, int mouseX, int mouseY, float partialTicks, float delta) {
        float tickDelta = client.isPaused() ? ((MinecraftClientAccessor) client).getPausedTickDelta() : partialTicks;

        try (var ignored = GlDebugUtils.pushGroup("Drawing world into FB for " + this)) {
            int oldFb = GL32.glGetInteger(GL32.GL_DRAW_FRAMEBUFFER_BINDING);

            int viewportX = GlStateManager.Viewport.getX();
            int viewportY = GlStateManager.Viewport.getY();
            int viewportW = GlStateManager.Viewport.getWidth();
            int viewportH = GlStateManager.Viewport.getHeight();

            framebuffer.beginWrite(true);

            CURRENT = framebuffer;
            ResizeHacks.resize(client.gameRenderer, this);

            GlStateManager._disableScissorTest();

            RenderSystem.getModelViewStack().push();
            RenderSystem.getModelViewStack().loadIdentity();
            RenderSystem.applyModelViewMatrix();

            Camera camera = client.gameRenderer.getCamera();
            MatrixStack matrixStack = new MatrixStack();
            double fov = this.client.options.getFov().getValue();
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

                Vec3d oldPos = camera.getPos();
                float oldYaw = camera.getYaw();
                float oldPitch = camera.getPitch();
                ((CameraAccessor) camera).invokeSetPos(this.position);
                ((CameraAccessor) camera).invokeSetRotation(this.yaw, this.pitch);

                MatrixStack matrices = new MatrixStack();
                matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(camera.getPitch()));
                matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(camera.getYaw() + 180.0F));
                Matrix3f matrix3f = new Matrix3f(matrices.peek().getNormalMatrix()).invert();
                RenderSystem.setInverseViewRotationMatrix(matrix3f);
                this.client
                    .worldRenderer
                    .setupFrustum(matrices, camera.getPos(), client.gameRenderer.getBasicProjectionMatrix(fov));
                client.worldRenderer.render(matrices, tickDelta, 0, false, camera, client.gameRenderer, client.gameRenderer.getLightmapTextureManager(), matrix4f);
                ((CameraAccessor) camera).invokeSetPos(oldPos);
                ((CameraAccessor) camera).invokeSetRotation(oldYaw, oldPitch);
            }

            RenderSystem.getModelViewStack().pop();
            RenderSystem.applyModelViewMatrix();
            GlStateManager._glBindFramebuffer(GL32.GL_DRAW_FRAMEBUFFER, oldFb);
            GlStateManager._viewport(viewportX, viewportY, viewportW, viewportH);
            GlStateManager._enableScissorTest();
            CURRENT = null;
            ResizeHacks.resize(client.gameRenderer, null);
        }

        WorldView.WORLD_VIEW_PROGRAM.use();
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

    void whenResized(BiConsumer<Integer, Integer> onResized) {
        resizeListeners.add(onResized);
    }
}
