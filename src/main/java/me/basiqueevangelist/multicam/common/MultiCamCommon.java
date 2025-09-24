package me.basiqueevangelist.multicam.common;

import me.lucko.fabric.api.permissions.v0.Permissions;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class MultiCamCommon implements ModInitializer {
    public static Identifier id(String path) {
        return Identifier.of("multicam", path);
    }

    @Override
    public void onInitialize() {
        PayloadTypeRegistry.playS2C().register(MultiCamUsageS2CPacket.ID, MultiCamUsageS2CPacket.PACKET_CODEC);
    }

    public static void sendUsagePacket(ServerPlayerEntity player) {
        boolean canUse = Permissions.check(player, "multicam.use", 2);

        // Always let the singleplayer host use the mod.
        if (player.getServer().isHost(player.getGameProfile()))
            canUse = true;

        ServerPlayNetworking.send(player, new MultiCamUsageS2CPacket(canUse));
    }
}
