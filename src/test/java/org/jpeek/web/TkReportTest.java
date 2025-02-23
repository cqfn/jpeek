/*
 * The MIT License (MIT)
 *
 * SPDX-FileCopyrightText: Copyright (c) 2017-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.jpeek.web;

import com.jcabi.matchers.XhtmlMatchers;
import java.io.File;
import java.io.IOException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.llorllale.cactoos.matchers.Assertion;
import org.takes.facets.fork.RqRegex;
import org.takes.rq.RqFake;
import org.takes.rs.RsPrint;

/**
 * Test case for {@link TkReport}.
 * @since 0.23
 * @checkstyle JavadocMethodCheck (500 lines)
 */
final class TkReportTest {

    @Test
    void rendersEmptySvgBadge(@TempDir final File folder)
        throws IOException, InterruptedException {
        final Futures futures = new Futures(
            new Reports(folder.toPath())
        );
        new Assertion<>(
            "Must render the badge",
            XhtmlMatchers.xhtml(
                new RsPrint(
                    new TkReport(
                        new AsyncReports(futures),
                        new Results()
                    ).act(
                        new RqRegex.Fake(
                            new RqFake(),
                            "/([^/]+)/([^/]+)/([^/]+)",
                            "/org.jpeek/jpeek/badge.svg"
                        )
                    )
                ).printBody()
            ),
            XhtmlMatchers.hasXPath("//svg:svg")
        ).affirm();
        futures.shutdown();
    }

}
