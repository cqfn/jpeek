/*
 * SPDX-FileCopyrightText: Copyright (c) 2017-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.jpeek.web;

import com.jcabi.matchers.XhtmlMatchers;
import java.io.IOException;
import org.junit.jupiter.api.Test;
import org.llorllale.cactoos.matchers.Assertion;
import org.takes.rq.RqFake;
import org.takes.rs.RsPrint;

/**
 * Test case for {@link TkAll}.
 * @since 0.11
 * @checkstyle JavadocMethodCheck (500 lines)
 */
final class TkAllTest {

    @Test
    void rendersAllPage() throws IOException {
        new Assertion<>(
            "Must print body",
            XhtmlMatchers.xhtml(
                new RsPrint(new TkAll().act(new RqFake())).printBody()
            ),
            XhtmlMatchers.hasXPath("//xhtml:body")
        ).affirm();
    }

}
