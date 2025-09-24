package me.basiqueevangelist.multicam;

import me.basiqueevangelist.multicam.mixin.PostEffectProcessorAccessor;
import me.basiqueevangelist.multicam.mixin.WorldRendererAccessor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.PostEffectPass;
import net.minecraft.client.gl.PostEffectProcessor;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.WorldRenderer;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;

public final class ResizeHacks {
    private ResizeHacks() {

    }

    public static void resize(GameRenderer renderer, @Nullable WorldViewComponent ctx) {
        if (renderer.getPostProcessor() != null) {
            resize(renderer.getPostProcessor(), ctx);
        }

        resize(renderer.getClient().worldRenderer, ctx);
    }

    public static void resize(WorldRenderer renderer, @Nullable WorldViewComponent ctx) {
        // this.scheduleTerrainUpdate();

        var entityOutlineProcessor = ((WorldRendererAccessor) renderer).getEntityOutlinePostProcessor();
        if (entityOutlineProcessor != null) {
            resize(entityOutlineProcessor, ctx);
        }

        var transparencyProcessor = ((WorldRendererAccessor) renderer).getTransparencyPostProcessor();
        if (transparencyProcessor != null) {
            resize(transparencyProcessor, ctx);
        }
    }

    public static void resize(PostEffectProcessor processor, @Nullable WorldViewComponent ctx) {
        PostEffectProcessorAccessor duck = (PostEffectProcessorAccessor) processor;

        duck.setWidth(width(ctx));
        duck.setHeight(height(ctx));

        var projectionMatrix = new Matrix4f()
            .setOrtho(
                0.0F,
                width(ctx),
                0.0F,
                height(ctx),
                0.1F,
                1000.0F
            );
        duck.setProjectionMatrix(projectionMatrix);

        if (duck.getMainTarget() instanceof DelegatingFramebuffer deleg)
            deleg.switchTo(ctx);

        for (PostEffectPass pass : duck.getPasses()) {
            pass.setProjectionMatrix(projectionMatrix);
        }

        for (var target : duck.getDefaultSizedTargets()) {
            if (target instanceof DelegatingFramebuffer deleg)
                deleg.switchTo(ctx);
        }
    }

    private static int width(@Nullable WorldViewComponent ctx) {
        return ctx == null ? MinecraftClient.getInstance().getWindow().getFramebufferWidth() : ctx.framebuffer.textureWidth;
    }

    private static int height(@Nullable WorldViewComponent ctx) {
        return ctx == null ? MinecraftClient.getInstance().getWindow().getFramebufferHeight() : ctx.framebuffer.textureHeight;
    }
}
