/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2017 Yegor Bugayenko
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
package org.jpeek;

import com.jcabi.matchers.XhtmlMatchers;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.cactoos.text.TextOf;
import org.hamcrest.MatcherAssert;
import org.junit.Test;

/**
 *
 * @author George Aristy (george.aristy@gmail.com)
 * @since 1.0
 */
public class NhdTest {
    private static final String METRIC = "NHD";

    @Test
    public void noDisagreements() throws IOException {
        final Path output = Files.createTempDirectory("");
        new Report(
            new Skeleton(new FakeBase("nhd/NoDisagreements")).xml(),
            METRIC
        ).save(output);
        MatcherAssert.assertThat(
            XhtmlMatchers.xhtml(
                new TextOf(
                    output.resolve(String.format("%s.xml", METRIC))
                ).asString()
            ),
            XhtmlMatchers.hasXPaths(
                String.format(
                    "//class[@id='%s' and number(@value)=%s]",
                    "NoDisagreements", 1
                )
            )
        );
    }

    @Test
    public void noTypes() throws IOException {
        final Path output = Files.createTempDirectory("");
        new Report(
            new Skeleton(new FakeBase("nhd/NoTypes")).xml(),
            METRIC
        ).save(output);
        MatcherAssert.assertThat(
            XhtmlMatchers.xhtml(
                new TextOf(
                    output.resolve(String.format("%s.xml", METRIC))
                ).asString()
            ),
            XhtmlMatchers.hasXPaths(
                String.format(
                    "//class[@id='%s' and number(@value)=%s]",
                    "NoTypes", 1
                )
            )
        );
    }

    @Test
    public void bar() throws IOException {
        final Path output = Files.createTempDirectory("");
        new Report(
            new Skeleton(new FakeBase("nhd/Bar")).xml(),
            METRIC
        ).save(output);
        MatcherAssert.assertThat(
            XhtmlMatchers.xhtml(
                new TextOf(
                    output.resolve(String.format("%s.xml", METRIC))
                ).asString()
            ),
            XhtmlMatchers.hasXPaths(
                String.format(
                    "//class[@id='%s' and number(@value)=%s]",
                    "Bar", 0.33
                )
            )
        );
    }
}
