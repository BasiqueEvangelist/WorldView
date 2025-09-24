package me.basiqueevangelist.multicam.common;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;

public record MultiCamUsageS2CPacket(boolean canUse) implements CustomPayload {
    public static final CustomPayload.Id<MultiCamUsageS2CPacket> ID = new Id<>(MultiCamCommon.id("usage"));
    public static final PacketCodec<ByteBuf, MultiCamUsageS2CPacket> PACKET_CODEC =
        PacketCodecs.BOOL.xmap(MultiCamUsageS2CPacket::new, MultiCamUsageS2CPacket::canUse);

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
