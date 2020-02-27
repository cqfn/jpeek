/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2017-2019 Yegor Bugayenko
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
package org.jpeek;

import java.util.HashMap;
import java.util.Map;

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
        this.args = new HashMap<>(args);
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
        return new HashMap<>(this.args);
    }
}
