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
import java.io.IOException;
import org.junit.Test;
import org.llorllale.cactoos.matchers.Assertion;
import org.takes.rq.RqFake;
import org.takes.rq.RqWithHeaders;
import org.takes.rs.RsPrint;

/**
 * Test case for {@link TkMistakes}.
 * @since 0.14
 * @checkstyle JavadocMethodCheck (500 lines)
 */
public final class TkMistakesTest {

    @Test
    public void rendersMistakesPage() throws IOException {
        new Assertion<>(
            "Must print body on mistakes page",
            XhtmlMatchers.xhtml(
                new RsPrint(new TkMistakes().act(new RqFake())).printBody()
            ),
            XhtmlMatchers.hasXPath("//xhtml:body")
        ).affirm();
    }

    @Test
    public void rendersMistakesPageInXml() throws IOException {
        new Assertion<>(
            "Must render mistake page in xml",
            XhtmlMatchers.xhtml(
                new RsPrint(
                    new TkMistakes().act(
                        new RqWithHeaders(
                            new RqFake(),
                            "Accept: application/vnd.jpeek+xml"
                        )
                    )
                ).printBody()
            ),
            XhtmlMatchers.hasXPath("/page/worst")
        ).affirm();
    }

}
