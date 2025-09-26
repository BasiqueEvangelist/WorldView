package me.basiqueevangelist.windowapi;

import net.minecraft.client.MinecraftClient;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.UnmodifiableView;
import org.lwjgl.glfw.GLFW;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public final class OpenWindows {
    private static final List<AltWindow> WINDOWS = new CopyOnWriteArrayList<>();
    private static final List<AltWindow> WINDOWS_VIEW = Collections.unmodifiableList(WINDOWS);

    private OpenWindows() {

    }

    static void add(AltWindow window) {
        WINDOWS.add(window);
    }

    static void remove(AltWindow window) {
        WINDOWS.remove(window);
    }

    public static @UnmodifiableView List<AltWindow> windows() {
        return WINDOWS_VIEW;
    }

    @ApiStatus.Internal
    public static void renderAll() {
        for (AltWindow window : WINDOWS) {
            window.draw();
        }
    }
}