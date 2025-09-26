package me.basiqueevangelist.multicam.client.command;

import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import io.wispforest.owo.ui.core.Animatable;
import io.wispforest.owo.ui.core.AnimatableProperty;
import io.wispforest.owo.ui.core.Easing;
import me.basiqueevangelist.multicam.client.CameraWindow;
import me.basiqueevangelist.multicam.client.command.argument.MsTimeArgumentType;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
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
import java.util.function.Function;
import java.util.stream.Stream;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.argument;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

public class CommandUtil {
    // TODO: translate.
    public static final SimpleCommandExceptionType NO_SUCH_CAMERA = new SimpleCommandExceptionType(Text.literal("No such camera"));

    public static ArgumentBuilder<FabricClientCommandSource, ?> cameraNode() {
        return argument("camera", IntegerArgumentType.integer(1))
            .suggests((ctx, suggestionsBuilder) -> {
                for (int i = 0; i < CameraWindow.CAMERAS.size(); i++) {
                    if (CameraWindow.CAMERAS.get(i) != null) {
                        suggestionsBuilder.suggest(i + 1);
                    }
                }

                return suggestionsBuilder.buildFuture();
            });
    }

    public static CameraWindow getCamera(CommandContext<FabricClientCommandSource> ctx) throws CommandSyntaxException {
        int cameraId = IntegerArgumentType.getInteger(ctx, "camera");

        cameraId -= 1;

        if (cameraId >= CameraWindow.CAMERAS.size()) throw NO_SUCH_CAMERA.create();

        CameraWindow camera = CameraWindow.CAMERAS.get(cameraId);

        if (camera == null) throw NO_SUCH_CAMERA.create();

        return camera;
    }

    public static void addInAt(ArgumentBuilder<FabricClientCommandSource, ?> builder, Function<AnimationConfigurer, ArgumentBuilder<FabricClientCommandSource, ?>> next) {
        // TODO: custom easings.
        builder
            .then(next.apply(new AnimationConfigurer() {
                @Override
                public <A extends Animatable<A>> void configureAnimation(CommandContext<FabricClientCommandSource> ctx, AnimatableProperty<A> property, A target, float distance) {
                    property.set(target);
                }
            }))
            .then(literal("in")
                .then(argument("duration", MsTimeArgumentType.time())
                    .then(next.apply(new AnimationConfigurer() {
                        @Override
                        public <A extends Animatable<A>> void configureAnimation(CommandContext<FabricClientCommandSource> ctx, AnimatableProperty<A> property, A target, float distance) {
                            property.animate(
                                IntegerArgumentType.getInteger(ctx, "duration"),
                                Easing.LINEAR,
                                target
                            )
                                .forwards();
                        }
                    }))))
            .then(literal("at")
                .then(argument("speed", FloatArgumentType.floatArg(0.1f))
                    .then(next.apply(new AnimationConfigurer() {
                        @Override
                        public <A extends Animatable<A>> void configureAnimation(CommandContext<FabricClientCommandSource> ctx, AnimatableProperty<A> property, A target, float distance) {
                            int duration = (int) ((distance / FloatArgumentType.getFloat(ctx, "speed")) * 1000);

                            property.animate(
                                duration,
                                Easing.LINEAR,
                                target
                            )
                                .forwards();
                        }
                    }))));
    }

    static FabricClientCommandSource getSourceForCamera(FabricClientCommandSource delegate, CameraWindow camera) {
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

    public interface AnimationConfigurer {
        <A extends Animatable<A>> void configureAnimation(CommandContext<FabricClientCommandSource> ctx, AnimatableProperty<A> property, A target, float distance);
    }
}
