package me.basiqueevangelist.worldview;

import io.wispforest.owo.ui.container.Containers;
import io.wispforest.owo.ui.container.FlowLayout;
import io.wispforest.owo.ui.core.OwoUIAdapter;
import io.wispforest.owo.ui.core.Sizing;
import io.wispforest.owo.ui.window.OwoWindow;
import net.minecraft.client.MinecraftClient;

public class WorldViewWindow extends OwoWindow<FlowLayout> {
    public WorldViewWindow() {
        super(640, 480, "World View", MinecraftClient.getInstance().getWindow().getHandle());
    }

    @Override
    protected OwoUIAdapter<FlowLayout> createAdapter() {
        return OwoUIAdapter.create(this, Containers::verticalFlow);
    }

    @Override
    protected void build(FlowLayout rootComponent) {
        rootComponent.child(new WorldViewComponent()
            .sizing(Sizing.fill(100), Sizing.fill(100)));
    }
}
