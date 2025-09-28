package me.basiqueevangelist.multicam.client;

import com.mojang.blaze3d.systems.RenderSystem;
import me.basiqueevangelist.multicam.mixin.client.KeyBindingAccessor;
import me.basiqueevangelist.windowapi.AltWindow;
import me.basiqueevangelist.windowapi.WindowIcon;
import me.basiqueevangelist.windowapi.context.CurrentWindowContext;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL20;
import org.lwjgl.system.NativeResource;

import java.util.ArrayList;
import java.util.List;

public class CameraWindow extends AltWindow {
    public final WorldViewComponent worldView = new WorldViewComponent();

    public static final List<CameraWindow> CAMERAS = new ArrayList<>();

    private NativeResource focusCb;
    private final int cameraIndex;

    private long prevDrawNanos = 0;
    private long prevFrameDurationNanos = 0;

    private @Nullable Vec3d orbitPoint = null;
    private float orbitY = 0;
    private float orbitRadius = 0;
    private float orbitPeriod = 0;
    private float orbitAngle = 0;

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

        icon(WindowIcon.fromResources(Identifier.of("multicam", "icon.png")));
    }

    @Override
    protected void build() {
        focusCb = GLFW.glfwSetWindowFocusCallback(handle(), (window, focused) -> {
            if (!focused) {
                unlockCursor();
            }
        });

        worldView.resize(scaledWidth(), scaledHeight());
    }

    @Override
    protected void resize(int newWidth, int newHeight) {
        worldView.resize(newWidth, newHeight);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (!cursorLocked()) {
            lockCursor();

            orbitPoint = null;
        }

        return true;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == GLFW.GLFW_KEY_ESCAPE && cursorLocked()) {
            unlockCursor();
        }

        return true;
    }

    @Override
    public void setFocused(boolean focused) {

    }

    @Override
    public boolean isFocused() {
        return false;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        worldView.fov(worldView.fov() + -(float)verticalAmount * 5);
        return true;
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
        float prevFrameDuration = MinecraftClient.getInstance().getRenderTickCounter().getLastFrameDuration();

        worldView.update(prevFrameDuration);

        if (cursorLocked()) {
            float multiplier = 1;

            if (hasKeyDown(MinecraftClient.getInstance().options.sprintKey)) {
                multiplier = 2;
            }

            if (hasKeyDown(MinecraftClient.getInstance().options.forwardKey)) {
                worldView.moveBy(multiplier * 0.5f * prevFrameDuration, 0, 0, true);
            }

            if (hasKeyDown(MinecraftClient.getInstance().options.backKey)) {
                worldView.moveBy(multiplier * -0.5f * prevFrameDuration, 0, 0, true);
            }

            if (hasKeyDown(MinecraftClient.getInstance().options.leftKey)) {
                worldView.moveBy(0, 0, multiplier * -0.5f * prevFrameDuration, true);
            }

            if (hasKeyDown(MinecraftClient.getInstance().options.rightKey)) {
                worldView.moveBy(0, 0, multiplier * 0.5f * prevFrameDuration, true);
            }

            if (hasKeyDown(MinecraftClient.getInstance().options.jumpKey)) {
                worldView.moveBy(0, multiplier * 0.5f * prevFrameDuration, 0, false);
            }

            if (hasKeyDown(MinecraftClient.getInstance().options.sneakKey)) {
                worldView.moveBy(0, multiplier * -0.5f * prevFrameDuration, 0, false);
            }
        }

        if (orbitPoint != null) {
            orbitAngle += (float) (Math.PI * 2 * (prevFrameDuration / 20 / orbitPeriod));

            float x = (float) (orbitPoint.x + Math.cos(orbitAngle) * orbitRadius);
            float z = (float) (orbitPoint.z + Math.sin(orbitAngle) * orbitRadius);

            worldView.position(new Vec3d(x, orbitY, z));
            worldView.lookAt(orbitPoint);
        }

        if (prevDrawNanos + 1_000_000_000 / MultiCam.FPS_TARGET - prevFrameDurationNanos > System.nanoTime()) return;

        long start = System.nanoTime();
        super.draw();
        this.prevFrameDurationNanos = System.nanoTime() - start;

        prevDrawNanos = System.nanoTime();
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        RenderSystem.disableScissor();
        GL20.glScissor(0, 0, CurrentWindowContext.current().framebufferWidth(), CurrentWindowContext.current().framebufferHeight());

        worldView.draw(context, 0, 0);

        RenderSystem.disableScissor();
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

    public void beginOrbit(Vec3d pos, float period, float y, float radius) {
        this.orbitPoint = pos;
        this.orbitPeriod = period;
        this.orbitY = y;
        this.orbitRadius = radius;
        this.orbitAngle = 0;
    }
}
