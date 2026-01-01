/*
 * SPDX-FileCopyrightText: Copyright (c) 2017-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.jpeek.web;

import com.jcabi.matchers.XhtmlMatchers;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.cactoos.iterable.Joined;
import org.jpeek.App;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.llorllale.cactoos.matchers.Assertion;
import org.xembly.Directives;
import org.xembly.Xembler;

/**
 * Test case for {@link Results}.
 * @since 0.16
 * @checkstyle JavadocMethodCheck (500 lines)
 */
final class ResultsTest {

    @Test
    @Disabled
    void acceptsAndRenders(@TempDir final Path output) throws Exception {
        final Path input = Paths.get(".");
        new App(input, output).analyze();
        final Results results = new Results();
        results.add("org.takes:takes", output);
        new Assertion<>(
            "Must exists repos tag",
            XhtmlMatchers.xhtml(
                new Xembler(
                    new Directives().add("repos").append(
                        new Joined<>(results.recent())
                    )
                ).xmlQuietly()
            ),
            XhtmlMatchers.hasXPath("/repos")
        ).affirm();
    }

}
