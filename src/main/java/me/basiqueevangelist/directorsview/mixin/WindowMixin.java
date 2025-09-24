package me.basiqueevangelist.directorsview.mixin;

import me.basiqueevangelist.directorsview.WorldViewComponent;
import net.minecraft.client.util.Window;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Window.class)
public class WindowMixin {
    @Inject(method = "getFramebufferWidth", at = @At("HEAD"), cancellable = true)
    private void malding(CallbackInfoReturnable<Integer> cir) {
        if (WorldViewComponent.CURRENT != null) {
            cir.setReturnValue(WorldViewComponent.CURRENT.textureWidth);
        }
    }

    @Inject(method = "getFramebufferHeight", at = @At("HEAD"), cancellable = true)
    private void maldnite(CallbackInfoReturnable<Integer> cir) {
        if (WorldViewComponent.CURRENT != null) {
            cir.setReturnValue(WorldViewComponent.CURRENT.textureHeight);
        }
    }
}
