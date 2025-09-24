package me.basiqueevangelist.multicam.client;

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

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

public class MultiCam implements ClientModInitializer {
	public static final GlProgram WORLD_VIEW_PROGRAM = new GlProgram(Identifier.of("multicam", "world_view"), VertexFormats.POSITION_COLOR);

    public static final Logger LOGGER = LoggerFactory.getLogger("MultiCam");

	@Override
	public void onInitializeClient() {
		LOGGER.info("\uD83C\uDF0F \uD83D\uDC40");

		ServerData.init();

		ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
			dispatcher.register(
				literal("multicam")
					.requires(x -> ServerData.canUse(x.hasPermissionLevel(2)))
					.executes(context -> {
						new CameraAngleWindow().open();
						return 1;
					})
			);
		});

		ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> {
			closeAllWindows();
		});
	}

	public static void closeAllWindows() {
		OpenWindows.windows().forEach(x -> {
			if (x instanceof CameraAngleWindow) {
				MinecraftClient.getInstance().send(x::close);
			}
		});
	}
}