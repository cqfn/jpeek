/*
 * SPDX-FileCopyrightText: Copyright (c) 2017-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.jpeek;

import com.jcabi.xml.XML;
import com.jcabi.xml.XSL;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import org.jpeek.calculus.Calculus;

/**
 * Builds a {@link Report} for a single metric.
 *
 * <p>There is no thread-safety guarantee.
 *
 * @since 0.72.0
 */
final class ReportBuilder {

    /**
     * XSL chain to apply to the skeleton.
     */
    private final XSL chain;

    /**
     * Calculus.
     */
    private final Calculus xsl;

    /**
     * Skeleton XML.
     */
    private final XML skeleton;

    /**
     * XSL params.
     */
    private final Map<String, Object> params;

    /**
     * Ctor.
     * @param chn XSL chain to apply to the skeleton
     * @param clc Calculus
     * @param skl Skeleton XML
     * @param prms XSL params
     */
    ReportBuilder(final XSL chn, final Calculus clc, final XML skl,
        final Map<String, Object> prms) {
        this.chain = chn;
        this.xsl = clc;
        this.skeleton = skl;
        this.params = prms;
    }

    /**
     * Build a report for one metric and add it to the collection.
     * @param metric Metric
     * @param reports Resulting report
     */
    void add(final Metrics metric, final Collection<Report> reports) {
        if (Objects.nonNull(metric.getSigma())) {
            reports.add(
                new XslReport(
                    this.chain.transform(this.skeleton), this.xsl,
                    new ReportData(
                        metric.name(),
                        this.params,
                        metric.getMean(),
                        metric.getSigma()
                    )
                )
            );
        } else if (metric.isIncludeParams()) {
            reports.add(
                new XslReport(
                    this.chain.transform(this.skeleton), this.xsl,
                    new ReportData(metric.name(), this.params)
                )
            );
        } else {
            reports.add(
                new XslReport(
                    this.chain.transform(this.skeleton), this.xsl,
                    new ReportData(metric.name())
                )
            );
        }
    }
}
