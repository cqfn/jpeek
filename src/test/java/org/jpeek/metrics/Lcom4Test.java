/*
 * The MIT License (MIT)
 *
 * SPDX-FileCopyrightText: Copyright (c) 2017-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */

package org.jpeek.metrics;

import org.cactoos.list.ListOf;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

/**
 * Test case for LCOM4
 * LCOM4 = Logical Relatedness of Methods.
 * Basically it's a graph disjoint set.
 * I.e. it answers the the following question:
 * "Into how many independent classes can you split this class?"
 * @since 0.28
 * @checkstyle JavadocMethodCheck (500 lines)
 */
final class Lcom4Test {

    /**
     * Variable from the report.
     */
    public static final String PAIRS = "pairs";

    /**
     * Variable from the report.
     */
    public static final String ATTRIBUTES = "attributes";

    /**
     * Variable from the report.
     */
    public static final String METHODS = "methods";

    /**
     * Path to XSL.
     */
    private static final String XSL = "org/jpeek/metrics/LCOM4.xsl";

    /**
     * Tests the deep dependencies methodFour() -> methodTwo() -> methodOne().
     *  MethodMethodCalls.java
     *  - methodOne uses 'num' directly
     *  - methodTwo uses 'num' indirectly via methodOne
     *  - methodThree uses 'num' directly
     *  - methodFour uses 'num' indirectly via methodTwo and methodOne
     *  - methodFive does not use 'num' (this is an orphan method, ignored)
     *  Therefore the number of disjoint sets (LCOM4) should be 1
     *  since all the methods use the same num field.
     * @todo #415:30min LCOM4: Graph algorithm to determine disjoint sets.
     *  Disjoint sets calculus is now implemented. We should continue calculating
     *  LCOM4 metrics (probably you should wait for #413, #403.
     *  After implementing, remove the @Disabled from the below test too.
     */
    @Test
    @Disabled
    void methodMethodCalls() throws Exception {
        final MetricBase.Report report = new MetricBase(
            Lcom4Test.XSL
        ).transform(
            "MethodMethodCalls"
        );
        report.assertVariable(
            Lcom4Test.METHODS,
            new ListOf<>(
                "methodOne()",
                "methodTwo()",
                "methodThree()",
                "methodFour()",
                "methodFive()"
            ).size()
        );
        report.assertVariable(Lcom4Test.ATTRIBUTES, 1);
        report.assertValue(1.0f, 0.001f);
    }

    /**
     * In OneCommonAttribute.java.
     * - methodOne uses variable 'num'
     * - methodTwo uses variable 'num'
     * So this class only has a single disjoint set AKA (LCOM4)
     * This is an ideal LCOM4 value = 1
     */
    @Test
    void oneCommonAttribute() throws Exception {
        final MetricBase.Report report = new MetricBase(
            Lcom4Test.XSL
        ).transform(
            "OneCommonAttribute"
        );
        report.assertVariable(
            Lcom4Test.METHODS,
            new ListOf<>(
                "methodOne",
                "methodTwo"
            ).size()
        );
        report.assertVariable(Lcom4Test.ATTRIBUTES, 1);
        report.assertVariable(Lcom4Test.PAIRS, 1);
        report.assertValue(1.0f, 0.001f);
    }

    /**
     * In NotCommonAttributes.java
     * - methodOne only uses 'num' variable
     * - methodTwo only uses 'anotherNum' variable
     * So basically there are two separate disjoint sets,
     * or two separate classes under the same umbrella.
     * So the value is 2.0
     */
    @Test
    void notCommonAttributes() throws Exception {
        final MetricBase.Report report = new MetricBase(
            Lcom4Test.XSL
        ).transform(
            "NotCommonAttributes"
        );
        report.assertVariable(Lcom4Test.ATTRIBUTES, 2);
        report.assertVariable(Lcom4Test.PAIRS, 0);
        report.assertValue(2.0f, 0.001f);
    }

    /**
     * Should be the same as NotCommonAttributes.
     * since constructors are not methods and should be ignored
     */
    @Test
    void notCommonAttributesWithAllArgsConstructor() throws Exception {
        final MetricBase.Report report = new MetricBase(
            Lcom4Test.XSL
        ).transform(
            "NotCommonAttributesWithAllArgsConstructor"
        );
        report.assertVariable(Lcom4Test.ATTRIBUTES, 2);
        report.assertVariable(Lcom4Test.PAIRS, 0);
        report.assertValue(2.0f, 0.001f);
    }
}
