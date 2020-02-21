/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2017-2019 Yegor Bugayenko
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NON-INFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
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
 * @checkstyle ClassDataAbstractionCouplingCheck (500 lines)
 */
public final class TkReportTest {

    /**
     * Temp folder for all tests.
     */
    @TempDir
    public File folder;

    @Test
    public void rendersEmptySvgBadge() throws IOException {
        new Assertion<>(
            "Must render the badge",
            XhtmlMatchers.xhtml(
                new RsPrint(
                    new TkReport(
                        new AsyncReports(
                            new Futures(
                                new Reports(this.folder.toPath())
                            )
                        ),
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
    }

}
