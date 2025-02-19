/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2017-2025 Yegor Bugayenko
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
