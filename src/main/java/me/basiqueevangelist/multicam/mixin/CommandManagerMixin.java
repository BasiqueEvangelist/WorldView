package me.basiqueevangelist.multicam.mixin;

import me.basiqueevangelist.multicam.common.MultiCamCommon;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CommandManager.class)
public class CommandManagerMixin {
    @Inject(method = "sendCommandTree(Lnet/minecraft/server/network/ServerPlayerEntity;)V", at = @At("HEAD"))
    private void onReloadPermissions(ServerPlayerEntity player, CallbackInfo ci) {
        MultiCamCommon.sendUsagePacket(player);
    }
}
