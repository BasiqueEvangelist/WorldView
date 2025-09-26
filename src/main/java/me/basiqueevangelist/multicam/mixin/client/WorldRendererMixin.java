package me.basiqueevangelist.multicam.mixin.client;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import me.basiqueevangelist.multicam.client.WorldViewComponent;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Collections;
import java.util.List;
import java.util.Set;

@Mixin(WorldRenderer.class)
public class WorldRendererMixin {
    @Shadow @Final private MinecraftClient client;

    // Taken from https://github.com/maruohon/tweakeroo/blob/pre-rewrite/fabric/1.19.x/src/main/java/fi/dy/masa/tweakeroo/mixin/MixinWorldRenderer.java#L67-L78.
    @ModifyExpressionValue(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/Camera;getFocusedEntity()Lnet/minecraft/entity/Entity;", ordinal = 3))
    private Entity makePlayerRender(Entity old) {
        if (WorldViewComponent.CURRENT_BUFFER != null)
            return client.player;

        return old;
    }

    @ModifyExpressionValue(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/Camera;isThirdPerson()Z"))
    private boolean makePlayerRender(boolean old) {
        if (WorldViewComponent.CURRENT_BUFFER != null)
            return true;

        return old;
    }

    @ModifyExpressionValue(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/world/ClientWorld;getEntities()Ljava/lang/Iterable;"))
    private Iterable<Entity> disableEntitiesIfNeeded(Iterable<Entity> old) {
        if (WorldViewComponent.CURRENT != null && WorldViewComponent.CURRENT.disableEntities())
            return Collections.emptyList();

        return old;
    }

    @WrapWithCondition(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/particle/ParticleManager;renderParticles(Lnet/minecraft/client/render/LightmapTextureManager;Lnet/minecraft/client/render/Camera;F)V"))
    private boolean disableParticlesIfNeeded(ParticleManager instance, LightmapTextureManager lightmapTextureManager, Camera camera, float tickDelta) {
        return WorldViewComponent.CURRENT == null || !WorldViewComponent.CURRENT.disableParticles();
    }
}
