/*
 * SPDX-FileCopyrightText: Copyright (c) 2017-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.jpeek.calculus.java;

import com.jcabi.matchers.XhtmlMatchers;
import com.jcabi.xml.XML;
import java.io.IOException;
import java.util.HashMap;
import org.cactoos.scalar.ItemAt;
import org.jpeek.FakeBase;
import org.jpeek.skeleton.Skeleton;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.llorllale.cactoos.matchers.Assertion;
import org.llorllale.cactoos.matchers.HasValue;

/**
 * Test case for {@link Lcom4}.
 * @since 0.30.9
 */
final class Lcom4Test {

    @Test
    @Disabled
    void createsXmlCalculusWithXpaths() throws IOException {
        final XML result = new Lcom4().node(
            "LCOM", new HashMap<>(0), new Skeleton(
                new FakeBase(
                    "NoMethods", "Bar", "OverloadMethods",
                    "OnlyOneMethodWithParams", "WithoutAttributes"
                )
            ).xml()
        );
        new Assertion<>(
            "Must create LCOM report",
            result.toString(),
            XhtmlMatchers.hasXPaths(
                "/metric/app/package/class/vars",
                "/metric/app/package/class/vars/var",
                "/metric/app/package/class[@value]"
            )
        ).affirm();
    }

    @ParameterizedTest
    @Disabled
    @CsvFileSource(resources = "/org/jpeek/calculus/java/lcom4-params.csv")
    void calculatesValue(final String file, final String value) throws Exception {
        final XML result = new Lcom4().node(
            "", new HashMap<>(0), new Skeleton(
                new FakeBase(file)
            ).xml()
        );
        new Assertion<>(
            "Must create LCOM4 value",
            new ItemAt<>(0, result.xpath("/metric/app/package/class/@value")),
            new HasValue<>(value)
        ).affirm();
    }
}
