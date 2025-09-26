package me.basiqueevangelist.multicam.client.command;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import me.basiqueevangelist.multicam.client.AnimatableFloat;
import me.basiqueevangelist.multicam.client.AnimatableVec3d;
import me.basiqueevangelist.multicam.client.CameraWindow;
import me.basiqueevangelist.multicam.client.command.argument.ClientPosArgument;
import me.basiqueevangelist.multicam.client.command.argument.ClientRotationArgumentType;
import me.basiqueevangelist.multicam.client.command.argument.ClientVec3ArgumentType;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.argument;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

public class MoveCameraCommand {
    public static ArgumentBuilder<FabricClientCommandSource, ?> build() {
        var cameraNode = CommandUtil.cameraNode();

        CommandUtil.addInAt(cameraNode, configurer -> literal("to")
            .then(argument("position", ClientVec3ArgumentType.vec3(true))
                .executes(ctx -> {
                    CameraWindow camera = CommandUtil.getCamera(ctx);

                    FabricClientCommandSource cameraSrc = CommandUtil.getSourceForCamera(ctx.getSource(), camera);
                    Vec3d pos = ClientVec3ArgumentType.getPosArgument(ctx, "position").toAbsolutePos(cameraSrc);

                    configurer.configureAnimation(
                        ctx,
                        camera.worldView.position,
                        new AnimatableVec3d(pos),
                        (float) pos.distanceTo(camera.worldView.position())
                    );

                    return 0;
                })
                .then(argument("rotation", ClientRotationArgumentType.rotation())
                    .executes(ctx -> {
                        CameraWindow camera = CommandUtil.getCamera(ctx);

                        FabricClientCommandSource cameraSrc = CommandUtil.getSourceForCamera(ctx.getSource(), camera);

                        Vec3d pos = ClientVec3ArgumentType.getPosArgument(ctx, "position").toAbsolutePos(cameraSrc);
                        ClientPosArgument rotArg = ClientRotationArgumentType.getRotation(ctx, "rotation");

                        Vec2f rot = rotArg.toAbsoluteRotation(cameraSrc);

                        configurer.configureAnimation(
                            ctx,
                            camera.worldView.position,
                            new AnimatableVec3d(pos),
                            (float) pos.distanceTo(camera.worldView.position())
                        );

                        configurer.configureAnimation(
                            ctx,
                            camera.worldView.pitch,
                            new AnimatableFloat(rot.x),
                            Math.abs(rot.x - camera.worldView.pitch())
                        );

                        configurer.configureAnimation(
                            ctx,
                            camera.worldView.yaw,
                            new AnimatableFloat(rot.y),
                            Math.abs(rot.y - camera.worldView.yaw())
                        );

                        return 0;
                    }))));

        return literal("move").then(cameraNode);
    }

}
