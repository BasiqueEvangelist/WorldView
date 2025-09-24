package me.basiqueevangelist.windowapi.mixin;

import me.basiqueevangelist.windowapi.OpenWindows;
import me.basiqueevangelist.windowapi.context.VanillaWindowContext;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.Window;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public class MinecraftClientMixin {
    @Shadow @Final private Window window;

    @Inject(method = "render", at = @At(value = "INVOKE_STRING", args = "ldc=yield", target = "Lnet/minecraft/util/profiler/Profiler;swap(Ljava/lang/String;)V"))
    private void renderAllWindows(CallbackInfo ci) {
        OpenWindows.renderAll();
    }

    @Inject(method = "onResolutionChanged", at = @At("TAIL"))
    private void captureResize(CallbackInfo ci) {
        VanillaWindowContext.onWindowResized(this.window);
    }

}
