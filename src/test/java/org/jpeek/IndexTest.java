/*
 * The MIT License (MIT)
 *
 * SPDX-FileCopyrightText: Copyright (c) 2017-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.jpeek;

import com.jcabi.matchers.XhtmlMatchers;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.cactoos.text.TextOf;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.llorllale.cactoos.matchers.Assertion;

/**
 * Test case for {@link Index}.
 * @since 0.6
 * @checkstyle JavadocMethodCheck (500 lines)
 */
final class IndexTest {
    /**
     * Xml file content as a string.
     */
    private String xml;

    @BeforeEach
    void setUp(@TempDir final Path output) throws Exception {
        final Path input = Paths.get(".");
        new App(input, output).analyze();
        this.xml = new TextOf(output.resolve("index.xml")).asString();
    }

    @Test
    void createsIndexXml() {
        new Assertion<>(
            "Must create index.xml",
            XhtmlMatchers.xhtml(
                this.xml
            ),
            XhtmlMatchers.hasXPaths("/index/metric")
        ).affirm();
    }

    @Test
    void xmlHasSchema() {
        new Assertion<>(
            "The XML Schema is invalid",
            XhtmlMatchers.xhtml(
                this.xml
            ),
            XhtmlMatchers.hasXPaths(
                "/index[@xsi:noNamespaceSchemaLocation='xsd/index.xsd']"
            )
        ).affirm();
    }

}
