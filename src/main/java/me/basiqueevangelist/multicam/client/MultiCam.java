package me.basiqueevangelist.multicam.client;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import io.wispforest.owo.shader.GlProgram;
import me.basiqueevangelist.windowapi.OpenWindows;
import net.fabricmc.api.ClientModInitializer;

import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.argument;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

public class MultiCam implements ClientModInitializer {
	public static final GlProgram WORLD_VIEW_PROGRAM = new GlProgram(Identifier.of("multicam", "world_view"), VertexFormats.POSITION_COLOR);
	// TODO: translate.
	public static final SimpleCommandExceptionType NO_SUCH_CAMERA = new SimpleCommandExceptionType(Text.literal("No such camera"));

	public static int FPS_TARGET = 60;

	public static CameraWindow getCamera(CommandContext<FabricClientCommandSource> ctx) throws CommandSyntaxException {
		int cameraId = IntegerArgumentType.getInteger(ctx, "camera");

		cameraId -= 1;

		if (cameraId >= CameraWindow.CAMERAS.size()) throw NO_SUCH_CAMERA.create();

		CameraWindow camera = CameraWindow.CAMERAS.get(cameraId);

		if (camera == null) throw NO_SUCH_CAMERA.create();

		return camera;
	}

	@Override
	public void onInitializeClient() {
		ServerData.init();

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
					.then(TpCameraCommand.build(registryAccess))
					.then(ZoomCameraCommand.build(registryAccess))
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