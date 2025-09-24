package me.basiqueevangelist.windowapi;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public abstract class SupportsFeaturesImpl<S extends SupportsFeatures<S>> implements SupportsFeatures<S> {
    private final Map<Key<S, ?>, Object> features = new HashMap<>();

    @SuppressWarnings("unchecked")
    @Override
    public <T> T get(Key<S, T> key) {
        T value = (T) features.get(key);

        if (value == null) {
            value = key.factory().apply((S) this);
            features.put(key, value);
        }

        return value;
    }

    @SuppressWarnings("unchecked")
    protected void destroyFeatures() {
        for (var feature : features.entrySet()) {
            ((Consumer<Object>) feature.getKey().destructor()).accept(feature.getValue());
        }
    }
}
