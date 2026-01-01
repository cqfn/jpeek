/*
 * SPDX-FileCopyrightText: Copyright (c) 2017-2026 Yegor Bugayenko
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
 * Test case for {@link Matrix}.
 * @since 0.8
 * @checkstyle JavadocMethodCheck (500 lines)
 */
final class MatrixTest {
    /**
     * Xml file content as a string.
     */
    private String xml;

    @BeforeEach
    void setUp(@TempDir final Path output) throws Exception {
        final Path input = Paths.get(".");
        new App(input, output).analyze();
        this.xml = new TextOf(output.resolve("matrix.xml")).asString();
    }

    @Test
    void createsMatrixXml() {
        new Assertion<>(
            "Must create matrix.xml",
            XhtmlMatchers.xhtml(
                this.xml
            ),
            XhtmlMatchers.hasXPaths("/matrix/classes")
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
                "/matrix[@xsi:noNamespaceSchemaLocation='xsd/matrix.xsd']"
            )
        ).affirm();
    }

}
