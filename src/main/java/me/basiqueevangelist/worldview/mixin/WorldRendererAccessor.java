package me.basiqueevangelist.worldview.mixin;

import net.minecraft.client.gl.PostEffectProcessor;
import net.minecraft.client.render.WorldRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(WorldRenderer.class)
public interface WorldRendererAccessor {
    @Accessor
    PostEffectProcessor getEntityOutlinePostProcessor();

    @Accessor
    PostEffectProcessor getTransparencyPostProcessor();
}
