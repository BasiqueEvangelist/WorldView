package me.basiqueevangelist.multicam.mixin.client.sodium;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import me.basiqueevangelist.multicam.client.WorldViewComponent;
import me.basiqueevangelist.multicam.client.compat.sodium.SodiumRendererFeature;
import net.caffeinemc.mods.sodium.client.render.chunk.ChunkUpdateType;
import net.caffeinemc.mods.sodium.client.render.chunk.RenderSection;
import net.caffeinemc.mods.sodium.client.render.chunk.RenderSectionManager;
import net.caffeinemc.mods.sodium.client.render.chunk.lists.SortedRenderLists;
import org.jetbrains.annotations.NotNull;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.ArrayDeque;
import java.util.Map;

@Mixin(value = RenderSectionManager.class, remap = false)
public class RenderSectionManagerMixin {
    @Shadow private @NotNull SortedRenderLists renderLists;

    @Shadow private @NotNull Map<ChunkUpdateType, ArrayDeque<RenderSection>> taskLists;

    @ModifyExpressionValue(method = {"renderLayer", "tickVisibleRenders", "getVisibleChunkCount", "getRenderLists"}, at = @At(value = "FIELD", target = "Lnet/caffeinemc/mods/sodium/client/render/chunk/RenderSectionManager;renderLists:Lnet/caffeinemc/mods/sodium/client/render/chunk/lists/SortedRenderLists;"))
    private SortedRenderLists getRenderLists(SortedRenderLists orig) {
        if (WorldViewComponent.CURRENT != null)
            return WorldViewComponent.CURRENT.get(SodiumRendererFeature.KEY).renderLists;

        return orig;
    }

    @Redirect(method = {"createTerrainRenderList", "resetRenderLists"}, at = @At(value = "FIELD", target = "Lnet/caffeinemc/mods/sodium/client/render/chunk/RenderSectionManager;renderLists:Lnet/caffeinemc/mods/sodium/client/render/chunk/lists/SortedRenderLists;", opcode = Opcodes.PUTFIELD))
    private void setRenderLists(RenderSectionManager self, SortedRenderLists orig) {
        if (WorldViewComponent.CURRENT != null)
            WorldViewComponent.CURRENT.get(SodiumRendererFeature.KEY).renderLists = orig;

        renderLists = orig;
    }

    @ModifyExpressionValue(method = {"resetRenderLists", "submitSectionTasks(Lnet/caffeinemc/mods/sodium/client/render/chunk/compile/executor/ChunkJobCollector;Lnet/caffeinemc/mods/sodium/client/render/chunk/ChunkUpdateType;Z)V", "getDebugStrings"}, at = @At(value = "FIELD", target = "Lnet/caffeinemc/mods/sodium/client/render/chunk/RenderSectionManager;taskLists:Ljava/util/Map;", opcode = Opcodes.GETFIELD))
    private @NotNull Map<ChunkUpdateType, ArrayDeque<RenderSection>> getTaskLists(@NotNull Map<ChunkUpdateType, ArrayDeque<RenderSection>> original) {
        if (WorldViewComponent.CURRENT != null)
            return WorldViewComponent.CURRENT.get(SodiumRendererFeature.KEY).taskLists;

        return original;
    }

    @Redirect(method = {"createTerrainRenderList"}, at = @At(value = "FIELD", target = "Lnet/caffeinemc/mods/sodium/client/render/chunk/RenderSectionManager;taskLists:Ljava/util/Map;", opcode = Opcodes.PUTFIELD))
    private void setTaskLists(RenderSectionManager instance, @NotNull Map<ChunkUpdateType, ArrayDeque<RenderSection>> value) {
        if (WorldViewComponent.CURRENT != null)
            WorldViewComponent.CURRENT.get(SodiumRendererFeature.KEY).taskLists = value;

        taskLists = value;
    }
}
