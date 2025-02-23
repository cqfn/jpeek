/*
 * The MIT License (MIT)
 *
 * SPDX-FileCopyrightText: Copyright (c) 2017-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.jpeek.web;

import com.jcabi.log.Logger;
import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import org.cactoos.BiFunc;
import org.cactoos.Func;
import org.cactoos.text.TextOf;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.llorllale.cactoos.matchers.Assertion;
import org.takes.Response;
import org.takes.facets.hamcrest.HmRsStatus;

/**
 * Test case for {@link Reports}.
 * @since 0.8
 * @checkstyle JavadocMethodCheck (500 lines)
 */
final class ReportsTest {

    @BeforeEach
    void weAreOnline() throws Exception {
        try {
            new TextOf(new URI("https://www.jpeek.org/").toURL()).asString();
        } catch (final IOException ex) {
            Logger.debug(this, "We are not online: %s", ex.getMessage());
            Assumptions.assumeTrue(false);
        }
    }

    @Test
    void rendersOneReport(@TempDir final File folder) throws Exception {
        final BiFunc<String, String, Func<String, Response>> reports = new Reports(folder.toPath());
        new Assertion<>(
            "Must return HTTP 200 OK status",
            reports.apply("com.jcabi", "jcabi-urn").apply("index.html"),
            new HmRsStatus(HttpURLConnection.HTTP_OK)
        ).affirm();
        new Assertion<>(
            "Must return HTTP 200 OK status",
            reports.apply("com.jcabi", "jcabi-urn").apply("index.html"),
            new HmRsStatus(HttpURLConnection.HTTP_OK)
        ).affirm();
    }
}
