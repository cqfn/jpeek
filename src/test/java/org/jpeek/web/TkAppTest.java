/*
 * SPDX-FileCopyrightText: Copyright (c) 2017-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.jpeek.web;

import com.jcabi.http.request.JdkRequest;
import com.jcabi.http.response.RestResponse;
import java.net.HttpURLConnection;
import java.nio.file.Path;
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
 * @todo #535:1h renderOneReport is not stable on checking /shutdown endpoint
 *  Increasing timeout does not help, problem should be investigated and
 *  response validation returned back
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
            }
        );
        app.act(new RqFake("GET", "/shutdown"));
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
