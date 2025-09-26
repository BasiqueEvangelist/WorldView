package me.basiqueevangelist.multicam.client.command;

import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import me.basiqueevangelist.multicam.client.CameraWindow;
import me.basiqueevangelist.multicam.client.command.argument.ClientVec3ArgumentType;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.util.math.Vec3d;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.argument;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

public class OrbitCameraCommand {
    public static ArgumentBuilder<FabricClientCommandSource, ?> build() {
        return literal("orbit")
            .then(CommandUtil.cameraNode()
                .then(argument("position", ClientVec3ArgumentType.vec3(true))
                    .then(argument("period", FloatArgumentType.floatArg(0))
                        .executes(ctx -> {
                            CameraWindow camera = CommandUtil.getCamera(ctx);

                            FabricClientCommandSource cameraSrc = CommandUtil.getSourceForCamera(ctx.getSource(), camera);
                            Vec3d pos = ClientVec3ArgumentType.getPosArgument(ctx, "position").toAbsolutePos(cameraSrc);

                            float period = FloatArgumentType.getFloat(ctx, "period");

                            float rad2d = (float) rad2d(camera.worldView.position(), pos);

                            camera.beginOrbit(pos, period, (float) camera.worldView.position().y, rad2d);

                            return 0;
                        }))));
    }

    private static double rad2d(Vec3d a, Vec3d b) {
        double x = b.x - a.x;
        double z = b.z - a.z;

        return Math.sqrt(x * x + z * z);
    }
}
