package me.basiqueevangelist.directorsview;

import io.wispforest.owo.ui.container.Containers;
import io.wispforest.owo.ui.container.FlowLayout;
import io.wispforest.owo.ui.core.OwoUIAdapter;
import io.wispforest.owo.ui.core.Sizing;
import me.basiqueevangelist.directorsview.mixin.KeyBindingAccessor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.system.NativeResource;

public class CameraAngleWindow extends OwoWindow<FlowLayout> {
    private final WorldViewComponent worldView = new WorldViewComponent();

    private NativeResource focusCb;

    public CameraAngleWindow() {
        size(640, 480);
        title("Camera Angle");
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
        worldView.pitch((float) (worldView.pitch() + yDelta*0.3f));
    }

    private boolean hasKeyDown(KeyBinding key) {
        return InputUtil.isKeyPressed(handle(), ((KeyBindingAccessor) key).getBoundKey().getCode());
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        if (hasKeyDown(MinecraftClient.getInstance().options.forwardKey)) {
            worldView.moveBy(0.5f * MinecraftClient.getInstance().getRenderTickCounter().getLastFrameDuration(), 0, 0);
        }

        if (hasKeyDown(MinecraftClient.getInstance().options.backKey)) {
            worldView.moveBy(-0.5f * MinecraftClient.getInstance().getRenderTickCounter().getLastFrameDuration(), 0, 0);
        }

        if (hasKeyDown(MinecraftClient.getInstance().options.leftKey)) {
            worldView.moveBy(0, 0, -0.5f * MinecraftClient.getInstance().getRenderTickCounter().getLastFrameDuration());
        }

        if (hasKeyDown(MinecraftClient.getInstance().options.rightKey)) {
            worldView.moveBy(0, 0, 0.5f * MinecraftClient.getInstance().getRenderTickCounter().getLastFrameDuration());
        }


        super.render(context, mouseX, mouseY, delta);
    }

    @Override
    public void close() {
        super.close();

        if (focusCb != null) {
            focusCb.close();
            focusCb = null;
        }
    }
}
