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

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * A container which allows observing changes to its value.
 * Every time the value is <i>changed</i>, i.e.
 * {@code Objects.equals(value, newValue)} evaluates to {@code false},
 * all observers added via {@link #observe(Consumer)} will be notified
 * and passed the new value
 *
 * @param <T> The type of object this observable holds
 * @see #observeAll(Runnable, Observable[])
 */
public class Observable<T> {

    protected T value;
    protected final List<Consumer<T>> observers;

    protected Observable(T initial) {
        this.value = initial;
        this.observers = new ArrayList<>();
    }

    /**
     * Creates a new observable container with
     * the given initial value
     */
    public static <T> Observable<T> of(T initial) {
        return new Observable<>(initial);
    }

    /**
     * Notify the given observer whenever <i>any</i> of the given observables
     * are updated. Context-less version {@link #observeAll(Consumer, Observable[])} which
     * allows observing multiple observables of different types
     *
     * @param observer    The observer to notify
     * @param observables The list of observable to observe
     */
    public static void observeAll(Runnable observer, Observable<?>... observables) {
        for (var observable : observables) {
            observable.observe(o -> observer.run());
        }
    }

    /**
     * Notify the given observer whenever <i>any</i> of the given observables
     * are updated
     *
     * @param observer    The observer to notify
     * @param observables The list of observable to observe
     */
    @SafeVarargs
    public static <T> void observeAll(Consumer<T> observer, Observable<T>... observables) {
        for (var observable : observables) {
            observable.observe(observer);
        }
    }

    /**
     * @return The current value stored in this container
     */
    public T get() {
        return this.value;
    }

    /**
     * Change the value stored in this container to {@code newValue}.
     * Observers will only be notified if {@code Objects.equals(value, newValue)}
     * evaluates to {@code false}
     *
     * @param newValue The new value to store
     */
    public void set(T newValue) {
        var oldValue = this.value;
        this.value = newValue;

        if (!Objects.equals(this.value, oldValue)) {
            this.notifyObservers(newValue);
        }
    }

    /**
     * Add an observer function to be run every time
     * the value stored in this container changes
     */
    public void observe(Consumer<T> observer) {
        this.observers.add(observer);
    }

    protected void notifyObservers(T value) {
        for (var observer : this.observers) {
            observer.accept(value);
        }
    }

}