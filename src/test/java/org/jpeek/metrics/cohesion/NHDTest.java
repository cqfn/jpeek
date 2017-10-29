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
package org.jpeek.metrics.cohesion;

import com.jcabi.matchers.XhtmlMatchers;
import java.io.IOException;
import java.nio.file.Paths;
import org.hamcrest.MatcherAssert;
import org.jpeek.DefaultBase;
import org.jpeek.metrics.FakeBase;
import org.junit.Test;
import org.xembly.Xembler;

/**
 * Test case for {@link NHD}.
 *
 * @author Mehmet Yildirim (memoyil@gmail.com)
 * @version $Id$
 * @since 0.1
 * @checkstyle AbbreviationAsWordInNameCheck (5 lines)
 * @checkstyle JavadocMethodCheck (500 lines)
 */
public final class NHDTest {

    @Test
    public void createsBigXmlReport() throws IOException {
        MatcherAssert.assertThat(
            XhtmlMatchers.xhtml(
                new Xembler(
                    new NHD(
                        new DefaultBase(Paths.get("."))
                    ).xembly()
                ).xmlQuietly()
            ),
            XhtmlMatchers.hasXPaths(
                "/app/package/class[@id='CAMCTest']",
                "//class[@id='DefaultBase' and @value='0.3333']"
            )
        );
    }

    @Test
    public void createsXmlReportForFixtureClassA() throws IOException {
        MatcherAssert.assertThat(
            XhtmlMatchers.xhtml(
                new Xembler(
                    new NHD(
                        new FakeBase("Foo")
                    ).xembly()
                ).xmlQuietly()
            ),
            XhtmlMatchers.hasXPaths(
                "/app/package[@id='']/class[@id='Foo']",
                "//class[@id='Foo' and @value='0.3333']",
                "//class[@id='Foo' and @color='yellow']"
            )
        );
    }

}
