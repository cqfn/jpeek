/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2017 Yegor Bugayenko
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NON-INFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package org.jpeek.metrics;

import org.cactoos.Func;

/**
 * Colors.
 *
 * <p>There is no thread-safety guarantee.
 *
 * @author Yegor Bugayenko (yegor256@gmail.com)
 * @version $Id$
 * @since 0.3
 */
public final class Colors implements Func<Double, String> {

    /**
     * Low border.
     */
    private final double low;

    /**
     * High border.
     */
    private final double high;

    /**
     * Ctor.
     * @param left Low border
     * @param right High border
     */
    public Colors(final double left, final double right) {
        this.low = left;
        this.high = right;
    }

    @Override
    public String toString() {
        final String text;
        if (this.low < this.high) {
            text = String.format("(%.2f .. %.2f]", this.low, this.high);
        } else {
            text = String.format("[%.2f .. %.2f)", this.high, this.low);
        }
        return text;
    }

    @Override
    public String apply(final Double cohesion) {
        final boolean reverse = this.high < this.low;
        final String color;
        if (cohesion < this.low && !reverse
            || cohesion > this.low && reverse) {
            color = "red";
        } else if (cohesion > this.high && !reverse
            || cohesion < this.high && reverse) {
            color = "green";
        } else {
            color = "yellow";
        }
        return color;
    }

}
