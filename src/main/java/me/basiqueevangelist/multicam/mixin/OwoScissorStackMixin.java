package me.basiqueevangelist.multicam.mixin;

import io.wispforest.owo.ui.util.ScissorStack;
import me.basiqueevangelist.windowapi.context.CurrentWindowContext;
import net.minecraft.client.util.Window;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ScissorStack.class)
public class OwoScissorStackMixin {
    @Redirect(method = {"pushDirect", "applyState"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/util/Window;getScaleFactor()D"))
    private static double getScaleFactor(Window instance) {
        return CurrentWindowContext.current().scaleFactor();
    }

    @Redirect(method = "pushDirect", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/util/Window;getScaledHeight()I"))
    private static int getScaleHeight(Window instance) {
        return CurrentWindowContext.current().scaledHeight();
    }

    @Redirect(method = "applyState", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/util/Window;getFramebufferWidth()I"))
    private static int getFramebufferWidth(Window instance) {
        return CurrentWindowContext.current().framebufferWidth();
    }

    @Redirect(method = "applyState", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/util/Window;getFramebufferHeight()I"))
    private static int getFramebufferHeight(Window instance) {
        return CurrentWindowContext.current().framebufferHeight();
    }
}
