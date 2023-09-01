package me.basiqueevangelist.worldview.mixin;

import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.gl.PostEffectPass;
import net.minecraft.client.gl.PostEffectProcessor;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Mixin(PostEffectProcessor.class)
public interface PostEffectProcessorAccessor {
    @Accessor
    List<PostEffectPass> getPasses();

    @Accessor
    List<Framebuffer> getDefaultSizedTargets();

    @Accessor
    Framebuffer getMainTarget();

    @Accessor
    void setProjectionMatrix(Matrix4f projectionMatrix);

    @Accessor
    void setWidth(int width);

    @Accessor
    void setHeight(int height);
}
