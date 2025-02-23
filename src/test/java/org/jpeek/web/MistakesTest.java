/*
 * The MIT License (MIT)
 *
 * SPDX-FileCopyrightText: Copyright (c) 2017-2025 Yegor Bugayenko
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
 * Test case for {@link Mistakes}.
 * @since 0.16
 * @checkstyle JavadocMethodCheck (500 lines)
 */
final class MistakesTest {

    @Test
    @Disabled
    void acceptsAndRenders(@TempDir final Path output) throws Exception {
        final Path input = Paths.get(".");
        new App(input, output).analyze();
        final Mistakes mistakes = new Mistakes();
        mistakes.add(output);
        new Assertion<>(
            "Must accept and render",
            XhtmlMatchers.xhtml(
                new Xembler(
                    new Directives().add("metrics").append(
                        new Joined<>(mistakes.worst())
                    )
                ).xmlQuietly()
            ),
            XhtmlMatchers.hasXPath("/metrics/metric[@id='LCOM']/avg")
        ).affirm();
    }

}
