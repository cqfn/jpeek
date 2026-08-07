/*
 * SPDX-FileCopyrightText: Copyright (c) 2017-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.jpeek;

import com.jcabi.matchers.XhtmlMatchers;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.cactoos.text.TextOf;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.llorllale.cactoos.matchers.Assertion;

/**
 * Test case for {@link Index}.
 * @since 0.6
 */
final class IndexTest {

    @Test
    void createsIndexXml(@TempDir final Path output) throws Exception {
        new Assertion<>(
            "Must create index.xml",
            XhtmlMatchers.xhtml(
                IndexTest.xml(output)
            ),
            XhtmlMatchers.hasXPaths("/index/metric")
        ).affirm();
    }

    @Test
    void xmlHasSchema(@TempDir final Path output) throws Exception {
        new Assertion<>(
            "The XML Schema is invalid",
            XhtmlMatchers.xhtml(
                IndexTest.xml(output)
            ),
            XhtmlMatchers.hasXPaths(
                "/index[@xsi:noNamespaceSchemaLocation='xsd/index.xsd']"
            )
        ).affirm();
    }

    /**
     * Build index.xml content.
     * @param output Output dir
     * @return XML as string
     * @throws Exception If fails
     */
    private static String xml(final Path output) throws Exception {
        final Path input = Paths.get(".");
        new App(input, output).analyze();
        return new TextOf(output.resolve("index.xml")).asString();
    }
}
