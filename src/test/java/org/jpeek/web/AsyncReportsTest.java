/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2017-2024 Yegor Bugayenko
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
import java.net.HttpURLConnection;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import org.cactoos.BiFunc;
import org.cactoos.Func;
import org.cactoos.func.SolidBiFunc;
import org.junit.jupiter.api.Test;
import org.llorllale.cactoos.matchers.Assertion;
import org.takes.Response;
import org.takes.facets.hamcrest.HmRsStatus;
import org.takes.rs.RsPrint;
import org.takes.rs.RsText;

/**
 * Test case for {@link AsyncReports}.
 * @since 0.8
 * @checkstyle JavadocMethodCheck (500 lines)
 */
final class AsyncReportsTest {

    @Test
    void rendersOneReport() throws Exception {
        final ExecutorService service = Executors.newSingleThreadExecutor();
        final BiFunc<String, String, Func<String, Response>> bifunc = new AsyncReports(
            new SolidBiFunc<>(
                (first, second) -> service.submit(
                    () -> input -> {
                        TimeUnit.DAYS.sleep(1L);
                        return new RsText("done!");
                    }
                )
            )
        );
        final Response response = bifunc.apply("org.jpeek", "jpeek").apply(
            "index.html"
        );
        new Assertion<>(
            "Must return HTTP NOT FOUND status",
            response,
            new HmRsStatus(HttpURLConnection.HTTP_NOT_FOUND)
        ).affirm();
        new Assertion<>(
            "Must have body in response",
            XhtmlMatchers.xhtml(new RsPrint(response).printBody()),
            XhtmlMatchers.hasXPath("//xhtml:body")
        ).affirm();
        service.shutdownNow();
    }

}
