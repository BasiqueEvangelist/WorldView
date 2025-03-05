package me.basiqueevangelist.worldview;

import io.wispforest.owo.shader.GlProgram;
import io.wispforest.owo.ui.window.OpenWindows;
import net.fabricmc.api.ClientModInitializer;

import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

public class WorldView implements ClientModInitializer {
	public static final GlProgram WORLD_VIEW_PROGRAM = new GlProgram(Identifier.of("worldview", "world_view"), VertexFormats.POSITION_COLOR);

    public static final Logger LOGGER = LoggerFactory.getLogger("worldview");

	@Override
	public void onInitializeClient() {
		LOGGER.info("\uD83C\uDF0F \uD83D\uDC40");

		ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
			dispatcher.register(
				literal("uwu")
					.then(literal("freeze_camera")
						.executes(context -> {
							new WorldViewWindow().open();
							return 1;
						}))
			);
		});

		ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> {
			OpenWindows.windows().forEach(x -> {
				if (x instanceof WorldViewWindow) {
					MinecraftClient.getInstance().send(x::close);
				}
			});
		});
	}
}