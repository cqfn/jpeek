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
 * Test case for {@link Matrix}.
 * @since 0.8
 */
final class MatrixTest {

    @Test
    void createsMatrixXml(@TempDir final Path output) throws Exception {
        new Assertion<>(
            "Must create matrix.xml",
            XhtmlMatchers.xhtml(
                MatrixTest.xml(output)
            ),
            XhtmlMatchers.hasXPaths("/matrix/classes")
        ).affirm();
    }

    @Test
    void xmlHasSchema(@TempDir final Path output) throws Exception {
        new Assertion<>(
            "The XML Schema is invalid",
            XhtmlMatchers.xhtml(
                MatrixTest.xml(output)
            ),
            XhtmlMatchers.hasXPaths(
                "/matrix[@xsi:noNamespaceSchemaLocation='xsd/matrix.xsd']"
            )
        ).affirm();
    }

    private static String xml(final Path output) throws Exception {
        new App(Paths.get("."), output).analyze();
        return new TextOf(output.resolve("matrix.xml")).asString();
    }
}
