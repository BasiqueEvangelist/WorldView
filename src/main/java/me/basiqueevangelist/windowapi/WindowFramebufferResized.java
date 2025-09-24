package me.basiqueevangelist.windowapi;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

public interface WindowFramebufferResized {
    void onFramebufferResized(int newWidth, int newHeight);

    static Event<WindowFramebufferResized> newEvent() {
        return EventFactory.createArrayBacked(WindowFramebufferResized.class, subscribers -> (newWidth, newHeight) -> {
            for (var subscriber : subscribers) {
                subscriber.onFramebufferResized(newWidth, newHeight);
            }
        });
    }
}