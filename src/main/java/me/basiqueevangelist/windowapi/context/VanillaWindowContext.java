package me.basiqueevangelist.windowapi.context;

import me.basiqueevangelist.windowapi.SupportsFeaturesImpl;
import me.basiqueevangelist.windowapi.WindowFramebufferResized;
import net.fabricmc.fabric.api.event.Event;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.util.Window;
import org.jetbrains.annotations.ApiStatus;

import java.util.WeakHashMap;

public class VanillaWindowContext extends SupportsFeaturesImpl<WindowContext> implements WindowContext {
    private static final WeakHashMap<Window, VanillaWindowContext> MAP = new WeakHashMap<>();

    public static final VanillaWindowContext MAIN = new VanillaWindowContext(MinecraftClient.getInstance().getWindow(), MinecraftClient.getInstance().getFramebuffer());

    private final Window window;
    private final Framebuffer framebuffer;

    private final Event<WindowFramebufferResized> framebufferResizedEvents = WindowFramebufferResized.newEvent();

    private VanillaWindowContext(Window window, Framebuffer framebuffer) {
        this.window = window;
        this.framebuffer = framebuffer;

        MAP.put(window, this);
    }

    @ApiStatus.Internal
    public static void onWindowResized(Window window) {
        var ctx = MAP.get(window);
        if (ctx == null) return;
        ctx.framebufferResizedEvents.invoker().onFramebufferResized(window.getFramebufferWidth(), window.getFramebufferHeight());
    }

    @Override
    public int framebufferWidth() {
        return window.getFramebufferWidth();
    }

    @Override
    public int framebufferHeight() {
        return window.getFramebufferHeight();
    }

    @Override
    public Event<WindowFramebufferResized> framebufferResized() {
        return framebufferResizedEvents;
    }

    @Override
    public int scaledWidth() {
        return window.getScaledWidth();
    }

    @Override
    public int scaledHeight() {
        return window.getScaledHeight();
    }

    @Override
    public double scaleFactor() {
        return window.getScaleFactor();
    }

    @Override
    public long handle() {
        return window.getHandle();
    }

    @Override
    public String toString() {
        return "VanillaWindowContext[" + (this == MAIN ? "MAIN" : window) + "]";
    }
}
