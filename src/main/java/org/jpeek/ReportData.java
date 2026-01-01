/*
 * SPDX-FileCopyrightText: Copyright (c) 2017-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.jpeek;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.cactoos.map.MapOf;

/**
 * Report data holder.
 *
 * @since 0.30.9
 */
final class ReportData {
    /**
     * Default mean.
     */
    private static final double DEFAULT_MEAN = 0.5d;

    /**
     * Default sigma.
     */
    private static final double DEFAULT_SIGMA = 0.1d;

    /**
     * The metric.
     */
    private final String metr;

    /**
     * Mean.
     */
    private final double man;

    /**
     * Sigma.
     */
    private final double sig;

    /**
     * XSL params.
     */
    private final Map<String, Object> args;

    /**
     * Ctor.
     * @param name Name of the metric
     */
    ReportData(final String name) {
        this(
            name, new HashMap<>(0), ReportData.DEFAULT_MEAN, ReportData.DEFAULT_SIGMA
        );
    }

    /**
     * Ctor.
     * @param name Name of metric
     * @param args Params for XSL
     */
    ReportData(final String name, final Map<String, Object> args) {
        this(
            name, args, ReportData.DEFAULT_MEAN, ReportData.DEFAULT_SIGMA
        );
    }

    /**
     * Ctor.
     * @param name Name of the metric
     * @param args Params for XSL
     * @param mean Mean
     * @param sigma Sigma
     * @checkstyle ParameterNumberCheck (10 lines)
     */
    ReportData(final String name, final Map<String, Object> args, final double mean,
        final double sigma) {
        this.metr = name;
        this.args = new MapOf<String, Object>(new HashMap<>(args));
        this.man = mean;
        this.sig = sigma;
    }

    /**
     * Metric name accessor.
     * @return The metric
     */
    public String metric() {
        return this.metr;
    }

    /**
     * Mean accessor.
     * @return The mean
     */
    public double mean() {
        return this.man;
    }

    /**
     * Sigma accessor.
     * @return Sigma
     */
    public double sigma() {
        return this.sig;
    }

    /**
     * Params accessor.
     * @return Params
     */
    public Map<String, Object> params() {
        return Collections.unmodifiableMap(this.args);
    }
}
