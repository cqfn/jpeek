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

import com.jcabi.http.request.JdkRequest;
import com.jcabi.http.response.RestResponse;
import java.net.HttpURLConnection;
import java.nio.file.Path;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.llorllale.cactoos.matchers.Assertion;
import org.takes.Response;
import org.takes.Take;
import org.takes.facets.hamcrest.HmRsStatus;
import org.takes.http.FtRemote;
import org.takes.rq.RqFake;
import org.takes.rs.RsPrint;

/**
 * Test case for {@link TkApp}.
 * @since 0.5
 * @checkstyle JavadocMethodCheck (500 lines)
 */
final class TkAppTest {

    @Test
    void rendersOneReport(@TempDir final Path temp) throws Exception {
        final Take app = new TkApp(temp);
        new FtRemote(app).exec(
            home -> {
                new JdkRequest(home)
                    .uri().path("org.jpeek")
                    .path("jpeek")
                    .path("index.html").back()
                    .fetch()
                    .as(RestResponse.class)
                    .assertStatus(HttpURLConnection.HTTP_NOT_FOUND);
                new JdkRequest(String.format("%s/org.jpeek/jpeek/", home))
                    .fetch()
                    .as(RestResponse.class)
                    .assertStatus(HttpURLConnection.HTTP_SEE_OTHER);
                new JdkRequest(String.format("%s/org.jpeek/jpeek", home))
                    .fetch()
                    .as(RestResponse.class)
                    .assertStatus(HttpURLConnection.HTTP_SEE_OTHER);
                new JdkRequest(String.format("%s/shutdown", home))
                    .fetch()
                    .as(RestResponse.class)
                    .assertBody(Matchers.equalTo("true"));
            }
        );
    }

    @Test
    @SuppressWarnings("PMD.AvoidInstantiatingObjectsInLoops")
    void pingsSimplePages(@TempDir final Path temp) throws Exception {
        final String[] pages = {
            "/org/jpeek/web/layout.xsl",
            "/org/jpeek/web/index.xsl",
            "/jpeek.css",
            "/",
            "/mistakes",
            "/robots.txt",
        };
        final Take app = new TkApp(temp);
        for (final String page : pages) {
            final Response response = app.act(new RqFake("GET", page));
            new Assertion<>(
                new RsPrint(response).print(),
                response,
                new HmRsStatus(HttpURLConnection.HTTP_OK)
            ).affirm();
        }
        app.act(new RqFake("GET", "/shutdown"));
    }

}
