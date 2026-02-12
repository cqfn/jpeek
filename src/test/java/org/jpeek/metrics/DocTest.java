/*
 * SPDX-FileCopyrightText: Copyright (c) 2017-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.jpeek.metrics;

import org.junit.jupiter.api.Test;

/**
 * Tests for DOC metric.
 * @since 0.30
 */
final class DocTest {

    /**
     * Ensures DOC counts local variable stores.
     * @throws Exception If fails
     */
    @Test
    void countsVariableStores() throws Exception {
        final MetricBase.Report report = new MetricBase(
            "org/jpeek/metrics/DOC.xsl"
        ).transform(
            "DocDistance"
        );
        report.assertVariable("methods", 1);
        report.assertVariable("stores", 3);
        report.assertValue(3f, 0.001f);
    }
}
