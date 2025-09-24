package me.basiqueevangelist.windowapi.util;

public interface InfallibleCloseable extends AutoCloseable {
    static InfallibleCloseable empty() {
        return () -> {};
    }

    @Override
    void close();
}