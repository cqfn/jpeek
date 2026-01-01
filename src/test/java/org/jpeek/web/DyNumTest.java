/*
 * SPDX-FileCopyrightText: Copyright (c) 2017-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
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
