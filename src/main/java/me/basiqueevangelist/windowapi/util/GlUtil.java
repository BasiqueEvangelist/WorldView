package me.basiqueevangelist.windowapi.util;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.systems.VertexSorter;
import org.joml.Matrix4f;
import org.lwjgl.glfw.GLFW;

public final class GlUtil {
    private GlUtil() {

    }

    public static InfallibleCloseable setContext(long handle) {
        long old = GLFW.glfwGetCurrentContext();
        if (old == handle) return InfallibleCloseable.empty();

        GLFW.glfwMakeContextCurrent(handle);

        return () -> GLFW.glfwMakeContextCurrent(old);
    }

    public static InfallibleCloseable setProjectionMatrix(Matrix4f projectionMatrix, VertexSorter sorter) {
        Matrix4f oldMatrix = RenderSystem.getProjectionMatrix();
        VertexSorter oldSorter = RenderSystem.getVertexSorting();

        RenderSystem.setProjectionMatrix(projectionMatrix, sorter);

        return () -> RenderSystem.setProjectionMatrix(oldMatrix, oldSorter);
    }
}