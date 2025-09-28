package me.basiqueevangelist.multicam.mixin.client.sodium;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import me.basiqueevangelist.multicam.client.WorldViewComponent;
import me.basiqueevangelist.multicam.client.compat.sodium.SodiumRendererFeature;
import net.caffeinemc.mods.sodium.client.render.SodiumWorldRenderer;
import org.joml.Matrix4f;
import org.joml.Vector3d;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(SodiumWorldRenderer.class)
public class SodiumWorldRendererMixin {
    @Shadow private Vector3d lastCameraPos;

    @Shadow private double lastCameraYaw;

    @Shadow private double lastCameraPitch;

    @Shadow private Matrix4f lastProjectionMatrix;

    //region lastCameraPos
    @ModifyExpressionValue(method = "setupTerrain", at = @At(value = "FIELD", target = "Lnet/caffeinemc/mods/sodium/client/render/SodiumWorldRenderer;lastCameraPos:Lorg/joml/Vector3d;", opcode = Opcodes.GETFIELD))
    private Vector3d redirectPos(Vector3d orig) {
        if (WorldViewComponent.CURRENT != null)
            return WorldViewComponent.CURRENT.get(SodiumRendererFeature.KEY).lastCameraPos;

        return orig;
    }

    @Redirect(method = "setupTerrain", at = @At(value = "FIELD", target = "Lnet/caffeinemc/mods/sodium/client/render/SodiumWorldRenderer;lastCameraPos:Lorg/joml/Vector3d;", opcode = Opcodes.PUTFIELD))
    private void redirectPos(SodiumWorldRenderer instance, Vector3d value) {
        if (WorldViewComponent.CURRENT != null)
            WorldViewComponent.CURRENT.get(SodiumRendererFeature.KEY).lastCameraPos = value;

        lastCameraPos = value;
    }
    //endregion

    //region lastCameraYaw
    @ModifyExpressionValue(method = "setupTerrain", at = @At(value = "FIELD", target = "Lnet/caffeinemc/mods/sodium/client/render/SodiumWorldRenderer;lastCameraYaw:D", opcode = Opcodes.GETFIELD))
    private double redirectYaw(double original) {
        if (WorldViewComponent.CURRENT != null)
            return WorldViewComponent.CURRENT.get(SodiumRendererFeature.KEY).lastCameraYaw;

        return original;
    }

    @Redirect(method = "setupTerrain", at = @At(value = "FIELD", target = "Lnet/caffeinemc/mods/sodium/client/render/SodiumWorldRenderer;lastCameraYaw:D", opcode = Opcodes.PUTFIELD))
    private void redirectYaw(SodiumWorldRenderer instance, double value) {
        if (WorldViewComponent.CURRENT != null)
            WorldViewComponent.CURRENT.get(SodiumRendererFeature.KEY).lastCameraYaw = value;

        lastCameraYaw = value;
    }
    //endregion

    //region lastCameraPitch
    @ModifyExpressionValue(method = "setupTerrain", at = @At(value = "FIELD", target = "Lnet/caffeinemc/mods/sodium/client/render/SodiumWorldRenderer;lastCameraPitch:D", opcode = Opcodes.GETFIELD))
    private double redirectPitch(double original) {
        if (WorldViewComponent.CURRENT != null)
            return WorldViewComponent.CURRENT.get(SodiumRendererFeature.KEY).lastCameraPitch;

        return original;
    }

    @Redirect(method = "setupTerrain", at = @At(value = "FIELD", target = "Lnet/caffeinemc/mods/sodium/client/render/SodiumWorldRenderer;lastCameraPitch:D", opcode = Opcodes.PUTFIELD))
    private void redirectPitch(SodiumWorldRenderer instance, double value) {
        if (WorldViewComponent.CURRENT != null)
            WorldViewComponent.CURRENT.get(SodiumRendererFeature.KEY).lastCameraPitch = value;

        lastCameraPitch = value;
    }
    //endregion

    //region lastProjectionMatrix
    @ModifyExpressionValue(method = "setupTerrain", at = @At(value = "FIELD", target = "Lnet/caffeinemc/mods/sodium/client/render/SodiumWorldRenderer;lastProjectionMatrix:Lorg/joml/Matrix4f;", opcode = Opcodes.GETFIELD))
    private Matrix4f redirectProjectionMatrix(Matrix4f original) {
        if (WorldViewComponent.CURRENT != null)
            return WorldViewComponent.CURRENT.get(SodiumRendererFeature.KEY).lastProjectionMatrix;

        return original;
    }

    @Redirect(method = "setupTerrain", at = @At(value = "FIELD", target = "Lnet/caffeinemc/mods/sodium/client/render/SodiumWorldRenderer;lastProjectionMatrix:Lorg/joml/Matrix4f;", opcode = Opcodes.PUTFIELD))
    private void redirectYaw(SodiumWorldRenderer instance, Matrix4f value) {
        if (WorldViewComponent.CURRENT != null)
            WorldViewComponent.CURRENT.get(SodiumRendererFeature.KEY).lastProjectionMatrix = value;

        lastProjectionMatrix = value;
    }
    //endregion
}
