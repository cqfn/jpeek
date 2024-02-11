/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2017-2024 Yegor Bugayenko
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
package org.jpeek.web;

import org.hamcrest.core.IsEqual;
import org.hamcrest.core.IsNot;
import org.hamcrest.core.IsNull;
import org.junit.jupiter.api.Test;
import org.llorllale.cactoos.matchers.Assertion;

/**
 * Test case for {@link DyNum}.
 * @since 0.31
 * @checkstyle JavadocMethodCheck (500 lines)
 */
final class DyNumTest {

    @Test
    void testConstructDyNumFromString() {
        final double number = 2.0d;
        new Assertion<>(
            "Double values should be equal",
            new DyNum(
                String.valueOf(number)
            ).doubleValue(),
            new IsEqual<>(number)
        ).affirm();
    }

    @Test
    void testConstructDyNumFromLong() {
        final long number = 2L;
        new Assertion<>(
            "Long values should be equal",
            new DyNum(number).longValue(),
            new IsEqual<>(number)
        ).affirm();
    }

    @Test
    void testConstructDyNumFromLongCompareToInt() {
        final Double number = 2.0d;
        new Assertion<>(
            "Integer values should be equal",
            new DyNum(number).intValue(),
            new IsEqual<>(number.intValue())
        ).affirm();
    }

    @Test
    void testConstructDyNumFromLongCompareToFloat() {
        final float number = 2.0f;
        new Assertion<>(
            "Float values should be equal",
            new DyNum(number).floatValue(),
            new IsEqual<>(number)
        ).affirm();
    }

    @Test
    void testConstructDyNumFromDouble() {
        final double number = 2.0d;
        new Assertion<>(
            "Double values should be equal.",
            new DyNum(number).doubleValue(),
            new IsEqual<>(number)
        ).affirm();
    }

    @Test
    void testUpdateDyNum() {
        new Assertion<>(
            "Result should not be null",
            new DyNum(2.0d).update(),
            new IsNot<>(
                new IsNull<>()
            )
        ).affirm();
    }
}
