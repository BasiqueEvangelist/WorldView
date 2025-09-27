package me.basiqueevangelist.multicam.mixin.client;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import io.wispforest.owo.ui.core.OwoUIAdapter;
import me.basiqueevangelist.multicam.client.CameraWindow;
import me.basiqueevangelist.windowapi.context.CurrentWindowContext;
import net.minecraft.client.util.Window;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(OwoUIAdapter.class)
public class OwoUIAdapterMixin {
    @ModifyExpressionValue(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/RenderTickCounter;getLastFrameDuration()F"))
    private float setLastFrameDuration(float orig) {
        if (CameraWindow.PREV_FRAME_DURATION != 0)
            return CameraWindow.PREV_FRAME_DURATION;

        return orig;
    }

    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/util/Window;getFramebufferWidth()I"))
    private int getFramebufferWidth(Window instance) {
        return CurrentWindowContext.current().framebufferWidth();
    }

    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/util/Window;getFramebufferHeight()I"))
    private int getFramebufferHeight(Window instance) {
        return CurrentWindowContext.current().framebufferHeight();
    }
}
