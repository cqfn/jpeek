/*
 * SPDX-FileCopyrightText: Copyright (c) 2017-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.jpeek.metrics;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

/**
 * Tests to check CCM metric in different links between attributes and methods.
 * @since 0.29
 */
@Disabled
final class CcmTest {

    /**
     * Class with one method access one attribute have
     * ncc metric = methods count.
     * @throws Exception
     */
    @Test
    void manyComponentInClassTest() throws Exception {
        final MetricBase.Report report = new MetricBase(
            "org/jpeek/metrics/CCM.xsl"
        ).transform(
            "CcmManyComp"
        );
        report.assertVariable("methods", 5);
        report.assertVariable("nc", 0);
        report.assertVariable("nmp", 10);
        report.assertVariable("ncc", 5);
        report.assertValue(0.0f, 0.001f);
    }

    /**
     * Class with one method access one attribute and
     * Ctor with all attributes initialization have the same
     * metric as without Ctor.
     * @throws Exception
     */
    @Test
    void manyComponentWithCtorInClassTest() throws Exception {
        final MetricBase.Report report = new MetricBase(
            "org/jpeek/metrics/CCM.xsl"
        ).transform(
            "CcmManyCompWithCtor"
        );
        report.assertVariable("methods", 5);
        report.assertVariable("nc", 0);
        report.assertVariable("nmp", 10);
        report.assertVariable("ncc", 5);
        report.assertValue(0.0f, 0.001f);
    }

    @Test
    void oneComponentInClassTest() throws Exception {
        final MetricBase.Report report = new MetricBase(
            "org/jpeek/metrics/CCM.xsl"
        ).transform(
            "CcmOneComp"
        );
        report.assertVariable("methods", 5);
        report.assertVariable("nc", 10);
        report.assertVariable("nmp", 10);
        report.assertVariable("ncc", 1);
        report.assertValue(1.0f, 0.001f);
    }

    /**
     * Check ccm metric for mixed usage: attribute usage, methods calls.
     * @throws Exception
     * @todo #522:30min there is a 4th step for incorrect calculation: nc
     *  in case of calling one method from another because of
     *  `xsl:if test="$method/ops/op/text()[. = $other/ops/op/text()]"`
     *  method name is not used for creating edge.
     */
    @Test
    void mixedCallsInClassTest() throws Exception {
        final MetricBase.Report report = new MetricBase(
            "org/jpeek/metrics/CCM.xsl"
        ).transform(
            "CcmMixCallManyComp"
        );
        report.assertVariable("methods", 5);
        report.assertVariable("nc", 2);
        report.assertVariable("nmp", 10);
        report.assertVariable("ncc", 3);
        report.assertValue(0.0666f, 0.0001f);
    }
}
