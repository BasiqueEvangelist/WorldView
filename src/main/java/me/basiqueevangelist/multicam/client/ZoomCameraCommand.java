package me.basiqueevangelist.multicam.client;

import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.command.CommandRegistryAccess;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.argument;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

public class ZoomCameraCommand {
    public static ArgumentBuilder<FabricClientCommandSource, ?> build(CommandRegistryAccess buildCtx) {
        return literal("zoom")
            .then(argument("camera", IntegerArgumentType.integer(1))
                .then(argument("fov", FloatArgumentType.floatArg(0))
                    .executes(ZoomCameraCommand::zoom)));
    }

    private static int zoom(CommandContext<FabricClientCommandSource> ctx) throws CommandSyntaxException {
        CameraWindow camera = MultiCam.getCamera(ctx);
        float fov = FloatArgumentType.getFloat(ctx, "fov");

        camera.worldView.fov(fov);

        return 0;
    }
}
