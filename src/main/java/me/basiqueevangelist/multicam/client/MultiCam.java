package me.basiqueevangelist.multicam.client;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import io.wispforest.owo.shader.GlProgram;
import me.basiqueevangelist.windowapi.OpenWindows;
import net.fabricmc.api.ClientModInitializer;

import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.argument;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

public class MultiCam implements ClientModInitializer {
	public static final GlProgram WORLD_VIEW_PROGRAM = new GlProgram(Identifier.of("multicam", "world_view"), VertexFormats.POSITION_COLOR);

	public static int FPS_TARGET = 60;

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