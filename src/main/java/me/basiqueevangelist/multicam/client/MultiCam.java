package me.basiqueevangelist.multicam.client;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import me.basiqueevangelist.multicam.client.command.ConfigCameraCommand;
import me.basiqueevangelist.multicam.client.command.MoveCameraCommand;
import me.basiqueevangelist.multicam.client.command.OrbitCameraCommand;
import me.basiqueevangelist.multicam.client.command.ZoomCameraCommand;
import me.basiqueevangelist.windowapi.OpenWindows;
import net.fabricmc.api.ClientModInitializer;

import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.rendering.v1.CoreShaderRegistrationCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.ShaderProgram;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.util.Identifier;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.argument;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

public class MultiCam implements ClientModInitializer {
	public static ShaderProgram WORLD_VIEW_PROGRAM = null;

	public static int FPS_TARGET = 60;

	@Override
	public void onInitializeClient() {
		ServerData.init();

		CoreShaderRegistrationCallback.EVENT.register(context -> {
			context.register(Identifier.of("multicam", "world_view"), VertexFormats.POSITION_COLOR, program -> WORLD_VIEW_PROGRAM = program);
		});

		ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
			dispatcher.register(
				literal("multicam")
					.requires(x -> ServerData.canUse(x.hasPermissionLevel(2)))
					.executes(context -> {
						new CameraWindow().open();
						return 1;
					})
					.then(literal("fps_target")
						.then(argument("target", IntegerArgumentType.integer(1))
							.executes(ctx -> {
								FPS_TARGET = IntegerArgumentType.getInteger(ctx, "target");
								return 0;
							})))
					.then(MoveCameraCommand.build())
					.then(ZoomCameraCommand.build())
					.then(OrbitCameraCommand.build())
					.then(ConfigCameraCommand.build())
			);
		});

		ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> {
			closeAllWindows();
		});
	}

	public static void closeAllWindows() {
		OpenWindows.windows().forEach(x -> {
			if (x instanceof CameraWindow) {
				MinecraftClient.getInstance().send(x::close);
			}
		});
	}
}