package me.basiqueevangelist.multicam.client;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import me.basiqueevangelist.multicam.client.argument.ClientPosArgument;
import me.basiqueevangelist.multicam.client.argument.ClientRotationArgumentType;
import me.basiqueevangelist.multicam.client.argument.ClientVec3ArgumentType;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.resource.featuretoggle.FeatureSet;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.Collection;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.argument;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

public class TpCameraCommand {
    public static ArgumentBuilder<FabricClientCommandSource, ?> build(CommandRegistryAccess buildCtx) {
        return literal("tp")
            .then(argument("camera", IntegerArgumentType.integer(1))
                .then(argument("position", ClientVec3ArgumentType.vec3(true))
                    .executes(TpCameraCommand::tpWithoutAngle)
                    .then(argument("rotation", ClientRotationArgumentType.rotation())
                        .executes(TpCameraCommand::tpWithAngle))));
    }

    private static int tpWithoutAngle(CommandContext<FabricClientCommandSource> ctx) throws CommandSyntaxException {
        CameraWindow camera = MultiCam.getCamera(ctx);

        FabricClientCommandSource cameraSrc = getSourceForCamera(ctx.getSource(), camera);
        Vec3d pos = ClientVec3ArgumentType.getPosArgument(ctx, "position").toAbsolutePos(cameraSrc);

        camera.worldView.position(pos);

        return 0;
    }

    private static int tpWithAngle(CommandContext<FabricClientCommandSource> ctx) throws CommandSyntaxException {
        CameraWindow camera = MultiCam.getCamera(ctx);

        FabricClientCommandSource cameraSrc = getSourceForCamera(ctx.getSource(), camera);

        Vec3d pos = ClientVec3ArgumentType.getPosArgument(ctx, "position").toAbsolutePos(cameraSrc);
        ClientPosArgument rotArg = ClientRotationArgumentType.getRotation(ctx, "rotation");

        Vec2f rot = rotArg.toAbsoluteRotation(cameraSrc);

        camera.worldView.position(pos);
        camera.worldView.pitch(rot.x);
        camera.worldView.yaw(rot.y);

        return 0;
    }

    private static FabricClientCommandSource getSourceForCamera(FabricClientCommandSource delegate, CameraWindow camera) {
        return new FabricClientCommandSource() {
            @Override
            public void sendFeedback(Text text) {
                delegate.sendFeedback(text);
            }

            @Override
            public void sendError(Text text) {
                delegate.sendError(text);
            }

            @Override
            public MinecraftClient getClient() {
                return delegate.getClient();
            }

            @Override
            public ClientPlayerEntity getPlayer() {
                return delegate.getPlayer();
            }

            @Override
            public ClientWorld getWorld() {
                return delegate.getWorld();
            }

            @Override
            public Collection<String> getPlayerNames() {
                return delegate.getPlayerNames();
            }

            @Override
            public Collection<String> getTeamNames() {
                return delegate.getTeamNames();
            }

            @Override
            public Stream<Identifier> getSoundIds() {
                return delegate.getSoundIds();
            }

            @Override
            public Stream<Identifier> getRecipeIds() {
                return delegate.getRecipeIds();
            }

            @Override
            public CompletableFuture<Suggestions> getCompletions(CommandContext<?> context) {
                return delegate.getCompletions(context);
            }

            @Override
            public Set<RegistryKey<World>> getWorldKeys() {
                return delegate.getWorldKeys();
            }

            @Override
            public DynamicRegistryManager getRegistryManager() {
                return delegate.getRegistryManager();
            }

            @Override
            public FeatureSet getEnabledFeatures() {
                return delegate.getEnabledFeatures();
            }

            @Override
            public CompletableFuture<Suggestions> listIdSuggestions(RegistryKey<? extends Registry<?>> registryRef, SuggestedIdType suggestedIdType, SuggestionsBuilder builder, CommandContext<?> context) {
                return delegate.listIdSuggestions(registryRef, suggestedIdType, builder, context);
            }

            @Override
            public boolean hasPermissionLevel(int level) {
                return delegate.hasPermissionLevel(level);
            }

            @Override
            public Vec3d getPosition() {
                return camera.worldView.position();
            }

            @Override
            public Vec2f getRotation() {
                return new Vec2f(camera.worldView.pitch(), camera.worldView.yaw());
            }
        };
    }
}
