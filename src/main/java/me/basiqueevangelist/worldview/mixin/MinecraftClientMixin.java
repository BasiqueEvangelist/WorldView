package me.basiqueevangelist.worldview.mixin;

import me.basiqueevangelist.worldview.WorldViewComponent;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.Framebuffer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MinecraftClient.class)
public class MinecraftClientMixin {
    @Inject(method = "getFramebuffer", at = @At("HEAD"), cancellable = true)
    private void malding(CallbackInfoReturnable<Framebuffer> cir) {
        if (WorldViewComponent.CURRENT != null) {
            cir.setReturnValue(WorldViewComponent.CURRENT);
        }
    }
}
