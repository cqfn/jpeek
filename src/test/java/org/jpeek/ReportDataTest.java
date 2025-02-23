/*
 * SPDX-FileCopyrightText: Copyright (c) 2017-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.jpeek;

import java.util.HashMap;
import java.util.Map;
import org.cactoos.map.MapEntry;
import org.cactoos.map.MapOf;
import org.hamcrest.core.IsEqual;
import org.junit.jupiter.api.Test;
import org.llorllale.cactoos.matchers.Assertion;
import org.llorllale.cactoos.matchers.HasValues;
import org.llorllale.cactoos.matchers.Throws;

/**
 * Tests for {@link ReportData}.
 * @since 0.30.9
 */
final class ReportDataTest {

    @Test
    void reportsName() {
        final String name = "whatever";
        final ReportData data = new ReportData(name);
        new Assertion<>(
            "Must returns name",
            data.metric(),
            new IsEqual<>(name)
        ).affirm();
    }

    @Test
    void reportsMean() {
        final String name = "whats";
        final double mean = 0;
        final double sigma = 1;
        final ReportData data = new ReportData(name, ReportDataTest.args(), mean, sigma);
        new Assertion<>(
            "Must returns mean",
            data.mean(),
            new IsEqual<>(mean)
        ).affirm();
    }

    @Test
    void reportsSigma() {
        final String name = "whatevermetric";
        final double mean = 0;
        final double sigma = 1;
        final ReportData data = new ReportData(name, ReportDataTest.args(), mean, sigma);
        new Assertion<>(
            "Must returns sigma",
            data.sigma(),
            new IsEqual<>(sigma)
        ).affirm();
    }

    @Test
    void reportsParams() {
        final String name = "name";
        final Map<String, Object> sample = ReportDataTest.args();
        final ReportData data = new ReportData(name, sample);
        new Assertion<>(
            "Must returns args",
            data.params().entrySet(),
            new HasValues<>(sample.entrySet())
        ).affirm();
    }

    @Test
    void shouldBeImmutableWhenModifyingPassedParams() {
        final String name = "metric";
        final Map<String, Object> sample = ReportDataTest.args();
        final Map<String, Object> params = new HashMap<>(sample);
        final ReportData data = new ReportData(name, params);
        params.clear();
        new Assertion<>(
            "Must be immutable",
            data.params().entrySet().size(),
            new IsEqual<>(sample.size())
        ).affirm();
    }

    @Test
    void throwsExceptionWhenTryingToModifyParams() {
        final String name = "metrics";
        final ReportData data = new ReportData(name, new HashMap<>(ReportDataTest.args()));
        new Assertion<>(
            "Must throw an exception if retrieved is modified",
            () -> {
                data.params().clear();
                return "";
            }, new Throws<>(UnsupportedOperationException.class)
        ).affirm();
    }

    private static Map<String, Object> args() {
        return new MapOf<String, Object>(
            new MapEntry<>("a", 1),
            new MapEntry<>("b", 2)
        );
    }
}
