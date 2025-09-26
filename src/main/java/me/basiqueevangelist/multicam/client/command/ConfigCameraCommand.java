package me.basiqueevangelist.multicam.client.command;

import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import me.basiqueevangelist.multicam.client.CameraWindow;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.argument;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

public class ConfigCameraCommand {
    public static ArgumentBuilder<FabricClientCommandSource, ?> build() {
        return literal("config")
            .then(CommandUtil.cameraNode()
                .then(literal("entities")
                    .then(argument("enabled", BoolArgumentType.bool())
                        .executes(ctx -> {
                            CameraWindow camera = CommandUtil.getCamera(ctx);

                            camera.worldView.disableEntities(!BoolArgumentType.getBool(ctx, "enabled"));

                            return 0;
                        })))
                .then(literal("block_entities")
                    .then(argument("enabled", BoolArgumentType.bool())
                        .executes(ctx -> {
                            CameraWindow camera = CommandUtil.getCamera(ctx);

                            camera.worldView.disableBlockEntities(!BoolArgumentType.getBool(ctx, "enabled"));

                            return 0;
                        })))
                .then(literal("particles")
                    .then(argument("enabled", BoolArgumentType.bool())
                        .executes(ctx -> {
                            CameraWindow camera = CommandUtil.getCamera(ctx);

                            camera.worldView.disableParticles(!BoolArgumentType.getBool(ctx, "enabled"));

                            return 0;
                        }))));
    }
}
