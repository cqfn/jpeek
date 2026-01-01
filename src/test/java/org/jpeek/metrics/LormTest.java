/*
 * SPDX-FileCopyrightText: Copyright (c) 2017-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */

package org.jpeek.metrics;

import org.cactoos.list.ListOf;
import org.junit.jupiter.api.Test;

/**
 * Test case for LORM.
 * LORM = Logical Relatedness of Methods.
 * @since 0.28
 * @checkstyle JavadocMethodCheck (500 lines)
 */
final class LormTest {

    @Test
    void calculatesVariables() throws Exception {
        final MetricBase.Report report = new MetricBase(
            "org/jpeek/metrics/LORM.xsl"
        ).transform(
            "TwoCommonMethods"
        );
        final int methods = 6;
        report.assertVariable("N", methods);
        report.assertVariable(
            "R",
            new ListOf<>(
                "methodTwo   -> methodOne",
                "methodThree -> methodOne",
                "methodFive  -> methodFour",
                "methodSix   -> methodFour"
            ).size()
        );
        report.assertVariable(
            "RN",
            methods * (methods - 1) / 2
        );
    }
}
