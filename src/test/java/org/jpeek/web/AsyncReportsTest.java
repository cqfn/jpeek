/*
 * SPDX-FileCopyrightText: Copyright (c) 2017-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
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
    @SuppressWarnings("PMD.CloseResource")
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
