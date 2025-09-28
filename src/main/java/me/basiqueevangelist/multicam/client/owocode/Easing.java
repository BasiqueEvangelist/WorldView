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

/**
 * An easing function which can smoothly move
 * an interpolation value from 0 to 1
 */
public interface Easing {

    Easing LINEAR = x -> x;

    Easing SINE = x -> {
        return (float) (Math.sin(x * Math.PI - Math.PI / 2) * 0.5 + 0.5);
    };

    Easing QUADRATIC = x -> {
        return x < 0.5 ? 2 * x * x : (float) (1 - Math.pow(-2 * x + 2, 2) / 2);
    };

    Easing CUBIC = x -> {
        return x < 0.5 ? 4 * x * x * x : (float) (1 - Math.pow(-2 * x + 2, 3) / 2);
    };

    Easing QUARTIC = x -> {
        return x < 0.5 ? 8 * x * x * x * x : (float) (1 - Math.pow(-2 * x + 2, 4) / 2);
    };

    Easing EXPO = x -> {
        if (x == 0) return 0;
        if (x == 1) return 1;

        return x < 0.5
            ? (float) Math.pow(2, 20 * x - 10) / 2
            : (2 - (float) Math.pow(2, -20 * x + 10)) / 2;
    };

    float apply(float x);

}