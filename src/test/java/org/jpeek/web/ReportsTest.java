/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2017-2022 Yegor Bugayenko
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

import com.jcabi.log.Logger;
import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
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
public final class ReportsTest {

    @BeforeEach
    public void weAreOnline() throws Exception {
        try {
            new TextOf(new URL("https://www.jpeek.org/")).asString();
        } catch (final IOException ex) {
            Logger.debug(this, "We are not online: %s", ex.getMessage());
            Assumptions.assumeTrue(false);
        }
    }

    @Test
    public void rendersOneReport(@TempDir final File folder) throws Exception {
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
