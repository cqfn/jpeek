/*
 * The MIT License (MIT)
 *
 * SPDX-FileCopyrightText: Copyright (c) 2017-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.jpeek.web;

import com.jcabi.matchers.XhtmlMatchers;
import java.io.IOException;
import org.junit.jupiter.api.Test;
import org.llorllale.cactoos.matchers.Assertion;
import org.takes.rq.RqFake;
import org.takes.rq.RqWithHeaders;
import org.takes.rs.RsPrint;

/**
 * Test case for {@link TkMistakes}.
 * @since 0.14
 * @checkstyle JavadocMethodCheck (500 lines)
 */
final class TkMistakesTest {

    @Test
    void rendersMistakesPage() throws IOException {
        new Assertion<>(
            "Must print body on mistakes page",
            XhtmlMatchers.xhtml(
                new RsPrint(new TkMistakes().act(new RqFake())).printBody()
            ),
            XhtmlMatchers.hasXPath("//xhtml:body")
        ).affirm();
    }

    @Test
    void rendersMistakesPageInXml() throws IOException {
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
