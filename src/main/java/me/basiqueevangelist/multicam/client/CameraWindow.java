package me.basiqueevangelist.multicam.client;

import io.wispforest.owo.ui.container.Containers;
import io.wispforest.owo.ui.container.FlowLayout;
import io.wispforest.owo.ui.core.OwoUIAdapter;
import io.wispforest.owo.ui.core.Sizing;
import me.basiqueevangelist.multicam.mixin.client.KeyBindingAccessor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.system.NativeResource;

import java.util.ArrayList;
import java.util.List;

public class CameraWindow extends OwoWindow<FlowLayout> {
    final WorldViewComponent worldView = new WorldViewComponent();

    public static final List<CameraWindow> CAMERAS = new ArrayList<>();

    private NativeResource focusCb;
    private final int cameraIndex;

    private long prevDrawNanos = 0;

    public CameraWindow() {
        size(640, 480);

        int i = 0;
        boolean found = false;
        for (i = 0; i < CAMERAS.size(); i++) {
            if (CAMERAS.get(i) == null) {
                found = true;
                CAMERAS.set(i, this);
                break;
            }
        }

        if (!found) {
            CAMERAS.add(this);
        }

        this.cameraIndex = i;

        title("Camera #" + (i + 1));
    }

    @Override
    protected OwoUIAdapter<FlowLayout> createAdapter() {
        return OwoUIAdapter.createWithoutScreen(0, 0, scaledWidth(), scaledHeight(), Containers::verticalFlow);
    }

    @Override
    protected void build(FlowLayout rootComponent) {
        rootComponent.child(worldView
            .sizing(Sizing.fill(100), Sizing.fill(100)));

        rootComponent.mouseUp().subscribe((mouseX, mouseY, button) -> {
            if (!cursorLocked()) {
                lockCursor();
            }

            return true;
        });

        rootComponent.keyPress().subscribe((keyCode, scanCode, modifiers) -> {
            if (keyCode == GLFW.GLFW_KEY_ESCAPE && cursorLocked()) {
                unlockCursor();
            }

            return true;
        });

        rootComponent.mouseScroll().subscribe((mouseX, mouseY, amount) -> {
            worldView.fov(worldView.fov() + -amount * 5);
            return true;
        });

        focusCb = GLFW.glfwSetWindowFocusCallback(handle(), (window, focused) -> {
            if (!focused) {
                unlockCursor();
            }
        });
    }

    @Override
    public void lockedMouseMoved(double xDelta, double yDelta) {
        worldView.yaw((float) (worldView.yaw() + xDelta*0.3f));
        worldView.pitch(MathHelper.clamp((float) (worldView.pitch() + yDelta*0.3f), -90.0F, 90.0F));
    }

    private boolean hasKeyDown(KeyBinding key) {
        return InputUtil.isKeyPressed(handle(), ((KeyBindingAccessor) key).getBoundKey().getCode());
    }

    @Override
    public void draw() {
        if (prevDrawNanos + 1_000_000_000 / MultiCam.FPS_TARGET > System.nanoTime()) return;

        super.draw();
        prevDrawNanos = System.nanoTime();
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        float prevFrameDuration = ((float) System.nanoTime() - prevDrawNanos) / 1_000_000_000f * 20;

        if (cursorLocked()) {
            if (hasKeyDown(MinecraftClient.getInstance().options.forwardKey)) {
                worldView.moveBy(0.5f * prevFrameDuration, 0, 0);
            }

            if (hasKeyDown(MinecraftClient.getInstance().options.backKey)) {
                worldView.moveBy(-0.5f * prevFrameDuration, 0, 0);
            }

            if (hasKeyDown(MinecraftClient.getInstance().options.leftKey)) {
                worldView.moveBy(0, 0, -0.5f * prevFrameDuration);
            }

            if (hasKeyDown(MinecraftClient.getInstance().options.rightKey)) {
                worldView.moveBy(0, 0, 0.5f * prevFrameDuration);
            }
        }


        super.render(context, mouseX, mouseY, delta);
    }

    @Override
    public void close() {
        super.close();

        CAMERAS.set(cameraIndex, null);

        if (focusCb != null) {
            focusCb.close();
            focusCb = null;
        }
    }
}
