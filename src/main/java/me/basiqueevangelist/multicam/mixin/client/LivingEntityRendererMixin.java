package me.basiqueevangelist.multicam.mixin.client;

import me.basiqueevangelist.multicam.client.WorldViewComponent;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntityRenderer.class)
public class LivingEntityRendererMixin {
    @Inject(method = "hasLabel(Lnet/minecraft/entity/LivingEntity;)Z", at = @At("HEAD"), cancellable = true)
    private void makeNameRender(LivingEntity livingEntity, CallbackInfoReturnable<Boolean> cir) {
        if (livingEntity instanceof ClientPlayerEntity && WorldViewComponent.CURRENT != null)
            cir.setReturnValue(true);
    }
}
