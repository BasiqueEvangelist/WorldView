/*
The MIT License (MIT)

Copyright (c) 2021 glisco

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in
all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
THE SOFTWARE.
 */

package me.basiqueevangelist.multicam.client.owocode;

import org.jetbrains.annotations.Nullable;

/**
 * A container which holds an animatable object,
 * used to manage to properties of UI components. Extends
 * the {@link Observable} container so that changes in its value
 * can be propagated to the holder of the property
 *
 * @param <A> The type of animatable object this property describes
 */
public class AnimatableProperty<A extends Animatable<A>> extends Observable<A> {

    protected @Nullable Animation<A> animation;

    protected AnimatableProperty(A initial) {
        super(initial);
    }

    /**
     * Creates a new animatable property with
     * the given initial value
     */
    public static <A extends Animatable<A>> AnimatableProperty<A> of(A initial) {
        return new AnimatableProperty<>(initial);
    }

    /**
     * Create an animation object which interpolates the state of this
     * property from the current one to {@code to} in {@code duration}
     * milliseconds, applying the given easing
     * <p>
     * This method replaces the current animation object of
     * this property - it will not be updated anymore
     *
     * @param duration The duration of the animation to create, in milliseconds
     * @param easing   The easing method to use
     * @param to       The target state of this property
     * @return The new animation of this property.
     */
    public Animation<A> animate(int duration, Easing easing, A to) {
        this.animation = new Animation<>(duration, this::set, easing, this.value, to);
        return this.animation;
    }

    /**
     * @return The current animation object of this property,
     * potentially {@code null} if {@link #animate(int, Easing, Animatable)}
     * was never called
     */
    public @Nullable Animation<A> animation() {
        return this.animation;
    }

    /**
     * Update the currently stored animation
     * object of this property
     *
     * @param delta The duration of the last frame, in partial ticks
     */
    public void update(float delta) {
        if (this.animation == null) return;
        this.animation.update(delta);
    }
}
