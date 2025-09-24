package me.basiqueevangelist.directorsview;

import io.wispforest.owo.ui.core.OwoUIAdapter;
import io.wispforest.owo.ui.core.ParentComponent;
import me.basiqueevangelist.windowapi.AltWindow;
import net.minecraft.client.gui.DrawContext;

public abstract class OwoWindow<R extends ParentComponent> extends AltWindow {
    protected OwoUIAdapter<R> uiAdapter = null;

    protected abstract OwoUIAdapter<R> createAdapter();

    protected abstract void build(R rootComponent);

    @Override
    protected void build() {
        this.uiAdapter = createAdapter();
        build(this.uiAdapter.rootComponent);
        this.uiAdapter.inflateAndMount();
    }

    @Override
    protected void resize(int newWidth, int newHeight) {
        uiAdapter.moveAndResize(0, 0, newWidth, newHeight);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        uiAdapter.render(context, mouseX, mouseY, delta);
    }

    @Override
    public void mouseMoved(double mouseX, double mouseY) {
        this.uiAdapter.mouseMoved(mouseX, mouseY);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        return this.uiAdapter.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        return this.uiAdapter.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        return this.uiAdapter.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        return this.uiAdapter.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        return this.uiAdapter.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        return this.uiAdapter.keyReleased(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean charTyped(char chr, int modifiers) {
        return this.uiAdapter.charTyped(chr, modifiers);
    }

    @Override
    public void close() {
        this.uiAdapter.dispose();
        super.close();
    }

    @Override
    public void setFocused(boolean focused) {

    }

    @Override
    public boolean isFocused() {
        return false;
    }
}
