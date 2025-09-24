package me.basiqueevangelist.windowapi;

import java.util.function.Consumer;
import java.util.function.Function;

public interface SupportsFeatures<S extends SupportsFeatures<S>> {
    <T> T get(Key<S, T> key);

    final class Key<S extends SupportsFeatures<S>, T> {
        private final Function<S, T> factory;
        private Consumer<T> destructor = unused -> {};

        public Key(Function<S, T> factory) {
            this.factory = factory;
        }

        public Key<S, T> withDestructor(Consumer<T> destructor) {
            this.destructor = destructor;

            return this;
        }

        public Function<S, T> factory() {
            return factory;
        }

        public Consumer<T> destructor() {
            return destructor;
        }
    }
}