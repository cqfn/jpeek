/*
 * SPDX-FileCopyrightText: Copyright (c) 2017-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.jpeek.metrics;

import com.jcabi.xml.XML;
import org.cactoos.text.FormattedText;
import org.cactoos.text.TextOf;
import org.cactoos.text.UncheckedText;
import org.hamcrest.number.IsCloseTo;
import org.llorllale.cactoos.matchers.Assertion;
import org.llorllale.cactoos.matchers.IsText;

/**
 * Assertion helper for xml.
 * @since 0.28
 */
public final class MetricReport {

    /**
     * Copy of the transformation xml.
     */
    private final XML xml;

    /**
     * Class name.
     */
    private final String name;

    /**
     * Ctor.
     * @param name Class name
     * @param xml Resulting xml
     */
    public MetricReport(final String name, final XML xml) {
        this.name = name;
        this.xml = xml;
    }

    /**
     * Asserts the variable produced.
     * @param variable Variable name
     * @param expected Expected value
     * @throws Exception String format exception
     */
    public void assertVariable(final String variable,
        final int expected) throws Exception {
        new Assertion<>(
            new FormattedText(
                "Variable '%s' is not calculated correctly for class '%s'",
                variable,
                this.name
            ).asString(),
            new TextOf(
                this.xml.xpath(
                    new FormattedText(
                        "//class[@id='%s']/vars/var[@id='%s']/text()",
                        this.name,
                        variable
                    ).asString()
                ).get(0)
            ),
            new IsText(
                String.valueOf(
                    expected
                )
            )
        ).affirm();
    }

    /**
     * Asserts the main metric value.
     * @param value Expected value of the metric
     * @param error Rounding tolerance since the metric is float number
     */
    public void assertValue(final double value, final double error) {
        new Assertion<>(
            "The metric value is not calculated properly",
            Double.parseDouble(
                this.xml.xpath(
                    new UncheckedText(
                        new FormattedText(
                            "//class[@id='%s']/@value",
                            this.name
                        )
                    ).asString()
                ).get(0)
            ),
            new IsCloseTo(
                value,
                error
            )
        ).affirm();
    }
}
