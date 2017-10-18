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

package org.jpeek.metrics.cohesion.fixtures;

/**
 * Dummy class for cohesion testing.
 *
 * @author Alonso A. Ortega (alayor3@gmail.com)
 * @version $Id$
 * @since 0.1
 */
public final class TestClassA {

    /**
     * Dummy var.
     */
    private int num;

    /**
     * Dummy method.
     * @param str Dummy Parameter 1.
     * @param boole Dummy Parameter 2.
     */
    public void methodOne(final String str, final Boolean boole) {
        this.num += 1;
        this.methodTwo(str, boole);
    }

    /**
     * Dummy method.
     * @param str Dummy Parameter 1.
     * @param boole Dummy Parameter 2.
     */
    public void methodTwo(final String str, final Boolean boole) {
        this.num += 1;
        this.methodOne(str, boole);
    }
}
