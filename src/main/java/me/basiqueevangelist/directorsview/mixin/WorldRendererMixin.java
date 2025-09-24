package me.basiqueevangelist.directorsview.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import me.basiqueevangelist.directorsview.WorldViewComponent;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(WorldRenderer.class)
public class WorldRendererMixin {
    @Shadow @Final private MinecraftClient client;

    // Taken from https://github.com/maruohon/tweakeroo/blob/pre-rewrite/fabric/1.19.x/src/main/java/fi/dy/masa/tweakeroo/mixin/MixinWorldRenderer.java#L67-L78.
    @ModifyExpressionValue(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/Camera;getFocusedEntity()Lnet/minecraft/entity/Entity;", ordinal = 3))
    private Entity makePlayerRender(Entity old) {
        if (WorldViewComponent.CURRENT != null)
            return client.player;

        return old;
    }

    @ModifyExpressionValue(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/Camera;isThirdPerson()Z"))
    private boolean makePlayerRender(boolean old) {
        if (WorldViewComponent.CURRENT != null)
            return true;

        return old;
    }
}
