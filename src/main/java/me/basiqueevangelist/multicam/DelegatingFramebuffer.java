package me.basiqueevangelist.multicam;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.gl.SimpleFramebuffer;
import org.jetbrains.annotations.Nullable;

public class DelegatingFramebuffer extends Framebuffer {
    private final Framebuffer original;
    private final LoadingCache<WorldViewComponent, Framebuffer> subFramebuffers;
    private Framebuffer tracking;


    public DelegatingFramebuffer(Framebuffer tracking) {
        super(tracking.useDepthAttachment);

        switchTo(tracking);
        this.original = tracking;

        this.subFramebuffers = CacheBuilder.newBuilder()
            .<WorldViewComponent, Framebuffer>removalListener(r -> {
                if (r.getValue() == null) return;

                r.getValue().delete();
            })
            .weakKeys()
            .build(CacheLoader.from(ctx -> {
                SimpleFramebuffer sub = new SimpleFramebuffer(ctx.framebuffer.textureWidth, ctx.framebuffer.textureHeight, original.useDepthAttachment, MinecraftClient.IS_SYSTEM_MAC);
                sub.setClearColor(0.0F, 0.0F, 0.0F, 0.0F);

                ctx.whenResized((newWidth, newHeight) -> sub.resize(newWidth, newHeight, MinecraftClient.IS_SYSTEM_MAC));

                return sub;
            }));
    }

    public void switchTo(@Nullable WorldViewComponent component) {
        if (component == null) {
            switchTo(this.original);
        } else {
            switchTo(subFramebuffers.getUnchecked(component));
        }
    }

    public void switchTo(Framebuffer tracking) {
        this.tracking = tracking;
        this.fbo = tracking.fbo;
        this.colorAttachment = tracking.getColorAttachment();
        this.depthAttachment = tracking.getDepthAttachment();
        this.textureHeight = tracking.textureHeight;
        this.textureWidth = tracking.textureWidth;
        this.viewportHeight = tracking.viewportHeight;
        this.viewportWidth = tracking.viewportWidth;
        this.texFilter = tracking.texFilter;
    }

    @Override
    public void resize(int width, int height, boolean getError) {
        tracking.resize(width, height, getError);
    }

    @Override
    public void delete() {
        tracking.delete();
    }

    @Override
    public void copyDepthFrom(Framebuffer framebuffer) {
        tracking.copyDepthFrom(framebuffer);
    }

    @Override
    public void initFbo(int width, int height, boolean getError) {
        tracking.initFbo(width, height, getError);
    }

    @Override
    public void setTexFilter(int texFilter) {
        tracking.setTexFilter(texFilter);
    }

    @Override
    public void checkFramebufferStatus() {
        tracking.checkFramebufferStatus();
    }

    @Override
    public void beginRead() {
        tracking.beginRead();
    }

    @Override
    public void endRead() {
        tracking.endRead();
    }

    @Override
    public void beginWrite(boolean setViewport) {
        tracking.beginWrite(setViewport);
    }

    @Override
    public void endWrite() {
        tracking.endWrite();
    }

    @Override
    public void setClearColor(float r, float g, float b, float a) {
        tracking.setClearColor(r, g, b, a);
    }

    @Override
    public void draw(int width, int height) {
        tracking.draw(width, height);
    }

    @Override
    public void draw(int width, int height, boolean disableBlend) {
        tracking.draw(width, height, disableBlend);
    }

    @Override
    public void clear(boolean getError) {
        tracking.clear(getError);
    }

    @Override
    public int getColorAttachment() {
        return tracking.getColorAttachment();
    }

    @Override
    public int getDepthAttachment() {
        return tracking.getDepthAttachment();
    }
}
