/*
 * SPDX-FileCopyrightText: Copyright (c) 2017-2026 Yegor Bugayenko
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
        new Assertion<>(
            "Must returns name",
            new ReportData(name).metric(),
            new IsEqual<>(name)
        ).affirm();
    }

    @Test
    void reportsMean() {
        final double mean = 0;
        new Assertion<>(
            "Must returns mean",
            new ReportData("whats", ReportDataTest.args(), mean, 1).mean(),
            new IsEqual<>(mean)
        ).affirm();
    }

    @Test
    void reportsSigma() {
        final double sigma = 1;
        new Assertion<>(
            "Must returns sigma",
            new ReportData(
                "whatevermetric", ReportDataTest.args(), 0, sigma
            ).sigma(),
            new IsEqual<>(sigma)
        ).affirm();
    }

    @Test
    void reportsParams() {
        final Map<String, Object> sample = ReportDataTest.args();
        new Assertion<>(
            "Must returns args",
            new ReportData("name", sample).params().entrySet(),
            new HasValues<>(sample.entrySet())
        ).affirm();
    }

    @Test
    void remainsImmutableWhenModifyingPassedParams() {
        final Map<String, Object> sample = ReportDataTest.args();
        final Map<String, Object> params = new HashMap<>(sample);
        final ReportData data = new ReportData("metric", params);
        params.clear();
        new Assertion<>(
            "Must be immutable",
            data.params().entrySet().size(),
            new IsEqual<>(sample.size())
        ).affirm();
    }

    @Test
    void throwsExceptionWhenTryingToModifyParams() {
        final ReportData data = new ReportData(
            "metrics", new HashMap<>(ReportDataTest.args())
        );
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
