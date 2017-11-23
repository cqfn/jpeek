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
import org.hamcrest.Matchers;
import org.jpeek.metrics.FakeBase;
import org.jpeek.metrics.cohesion.LCOM;
import org.jpeek.metrics.cohesion.MMAC;
import org.jpeek.metrics.cohesion.NHD;
import org.junit.Test;

/**
 * Test case for {@link Report}.
 * @author Yegor Bugayenko (yegor256@gmail.com)
 * @version $Id$
 * @since 0.4
 * @checkstyle JavadocMethodCheck (500 lines)
 */
public final class ReportTest {

    @Test
    public void createsXmlReport() throws IOException {
        final Path output = Files.createTempDirectory("");
        new Report(new LCOM(new FakeBase("Foo"))).save(output);
        MatcherAssert.assertThat(
            Files.exists(output.resolve("LCOM.xml")),
            Matchers.equalTo(true)
        );
        MatcherAssert.assertThat(
            Files.exists(output.resolve("LCOM.html")),
            Matchers.equalTo(true)
        );
    }

    @Test
    public void createsXmlReportWithXpaths() throws IOException {
        final Path output = Files.createTempDirectory("");
        new Report(
            new MMAC(
                new FakeBase(
                    "NoMethods", "Bar", "OverloadMethods",
                    "OnlyOneMethodWithParams", "WithoutAttributes"
                )
            )
        ).save(output);
        MatcherAssert.assertThat(
            XhtmlMatchers.xhtml(
                new TextOf(output.resolve("MMAC.xml")).asString()
            ),
            XhtmlMatchers.hasXPaths(
                "/metric/app/package/class/vars",
                "/metric/statistics/mean",
                "/metric/bars/bar[@x='0' and .='1' and @color='red']"
            )
        );
    }

    @Test
    public void createsXmlReportWithEmptyProject() throws IOException {
        final Path output = Files.createTempDirectory("");
        new Report(new NHD(new FakeBase())).save(output);
        MatcherAssert.assertThat(
            XhtmlMatchers.xhtml(
                new TextOf(output.resolve("NHD.xml")).asString()
            ),
            XhtmlMatchers.hasXPaths(
                "/metric[title='NHD']/bars/bar"
            )
        );
    }

}
