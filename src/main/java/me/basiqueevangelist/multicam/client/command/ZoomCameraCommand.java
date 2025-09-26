package me.basiqueevangelist.multicam.client.command;

import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import io.wispforest.owo.ui.core.Easing;
import me.basiqueevangelist.multicam.client.AnimatableFloat;
import me.basiqueevangelist.multicam.client.CameraWindow;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.argument;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

public class ZoomCameraCommand {
    public static ArgumentBuilder<FabricClientCommandSource, ?> build() {
        var cameraNode = CommandUtil.cameraNode();

        CommandUtil.addInAt(cameraNode, configurer ->
            literal("to")
                .then(argument("fov", FloatArgumentType.floatArg(0))
                    .executes(ctx -> {
                        CameraWindow camera = CommandUtil.getCamera(ctx);
                        float fov = FloatArgumentType.getFloat(ctx, "fov");

                        configurer.configureAnimation(
                            ctx,
                            camera.worldView.fov,
                            new AnimatableFloat(fov),
                            Math.abs(fov - camera.worldView.fov())
                        );

                        return 0;
                    })));

        return literal("zoom").then(cameraNode);
    }
}
