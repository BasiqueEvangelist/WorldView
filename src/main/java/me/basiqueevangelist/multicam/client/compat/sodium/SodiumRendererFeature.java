package me.basiqueevangelist.multicam.client.compat.sodium;

import me.basiqueevangelist.multicam.client.WorldViewComponent;
import me.basiqueevangelist.windowapi.SupportsFeatures;
import net.caffeinemc.mods.sodium.client.render.chunk.ChunkUpdateType;
import net.caffeinemc.mods.sodium.client.render.chunk.RenderSection;
import net.caffeinemc.mods.sodium.client.render.chunk.lists.SortedRenderLists;
import org.joml.Matrix4f;
import org.joml.Vector3d;

import java.util.ArrayDeque;
import java.util.EnumMap;
import java.util.Map;

public class SodiumRendererFeature {
    public static SupportsFeatures.Key<WorldViewComponent, SodiumRendererFeature> KEY = new SupportsFeatures.Key<>(ignored -> new SodiumRendererFeature());

    public Vector3d lastCameraPos;
    public double lastCameraPitch;
    public double lastCameraYaw;
    public Matrix4f lastProjectionMatrix;

    public SortedRenderLists renderLists = SortedRenderLists.empty();
    public Map<ChunkUpdateType, ArrayDeque<RenderSection>> taskLists = new EnumMap<>(ChunkUpdateType.class);

    public SodiumRendererFeature() {
        for (int i = 0; i < ChunkUpdateType.values().length; ++i) {
            ChunkUpdateType type = ChunkUpdateType.values()[i];
            taskLists.put(type, new ArrayDeque<>());
        }
    }
}
