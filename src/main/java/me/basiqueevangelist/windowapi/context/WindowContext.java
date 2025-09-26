package me.basiqueevangelist.windowapi.context;

import me.basiqueevangelist.windowapi.SupportsFeatures;
import me.basiqueevangelist.windowapi.WindowFramebufferResized;
import net.fabricmc.fabric.api.event.Event;
import net.minecraft.client.gl.Framebuffer;

public interface WindowContext extends SupportsFeatures<WindowContext> {
    int framebufferWidth();
    int framebufferHeight();
    Event<WindowFramebufferResized> framebufferResized();
//    Framebuffer framebuffer();

    int scaledWidth();
    int scaledHeight();
    double scaleFactor();

    long handle();
}