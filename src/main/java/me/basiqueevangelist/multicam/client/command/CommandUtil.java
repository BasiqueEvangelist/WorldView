package me.basiqueevangelist.multicam.client.command;

import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import io.wispforest.owo.ui.core.Animatable;
import io.wispforest.owo.ui.core.AnimatableProperty;
import io.wispforest.owo.ui.core.Easing;
import me.basiqueevangelist.multicam.client.CameraWindow;
import me.basiqueevangelist.multicam.client.command.argument.MsTimeArgumentType;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.text.Text;

import java.util.function.Function;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.argument;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

public class CommandUtil {
    // TODO: translate.
    public static final SimpleCommandExceptionType NO_SUCH_CAMERA = new SimpleCommandExceptionType(Text.literal("No such camera"));

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

    public interface AnimationConfigurer {
        <A extends Animatable<A>> void configureAnimation(CommandContext<FabricClientCommandSource> ctx, AnimatableProperty<A> property, A target, float distance);
    }
}
