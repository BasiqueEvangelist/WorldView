package me.basiqueevangelist.multicam.client;

import me.basiqueevangelist.multicam.common.MultiCamUsageS2CPacket;
import net.fabricmc.fabric.api.client.networking.v1.ClientLoginConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import org.jetbrains.annotations.Nullable;

public class ServerData {
    private static @Nullable MultiCamUsageS2CPacket PACKET;

    public static void init() {
        ClientPlayNetworking.registerGlobalReceiver(MultiCamUsageS2CPacket.ID, (packet, context) -> {
            ServerData.PACKET = packet;

            if (!packet.canUse()) {
                MultiCam.closeAllWindows();
            }
        });

        ClientPlayConnectionEvents.DISCONNECT.register(
            (handler, client) -> PACKET = null);

        ClientLoginConnectionEvents.DISCONNECT.register(
            (handler, client) -> PACKET = null);
    }

    public static boolean canUse(boolean defaultValue) {
        return PACKET != null ? PACKET.canUse() : defaultValue;
    }
}
