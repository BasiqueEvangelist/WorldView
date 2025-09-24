package me.basiqueevangelist.multicam.mixin;

import me.basiqueevangelist.multicam.DelegatingFramebuffer;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.gl.PostEffectProcessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(PostEffectProcessor.class)
public class PostEffectProcessorMixin {
    @ModifyVariable(method = "<init>", at = @At("HEAD"), argsOnly = true)
    private static Framebuffer trackifyFramebuffer(Framebuffer framebuffer) {
        if (framebuffer instanceof DelegatingFramebuffer)
            return framebuffer;
        else
            return new DelegatingFramebuffer(framebuffer);
    }

    @ModifyVariable(method = "addTarget", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gl/Framebuffer;setClearColor(FFFF)V"))
    private Framebuffer trackify(Framebuffer old) {
        return new DelegatingFramebuffer(old);
    }
}
