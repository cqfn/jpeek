/*
 * SPDX-FileCopyrightText: Copyright (c) 2017-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.jpeek.web;

import org.cactoos.Scalar;
import org.cactoos.iterable.IterableOf;
import org.jpeek.Header;
import org.takes.Request;
import org.takes.Response;
import org.takes.facets.fork.FkTypes;
import org.takes.facets.fork.RsFork;
import org.takes.rs.RsPrettyXml;
import org.takes.rs.RsWithType;
import org.takes.rs.RsWrap;
import org.takes.rs.RsXslt;
import org.takes.rs.xe.RsXembly;
import org.takes.rs.xe.XeAppend;
import org.takes.rs.xe.XeChain;
import org.takes.rs.xe.XeDirectives;
import org.takes.rs.xe.XeMillis;
import org.takes.rs.xe.XeSource;
import org.takes.rs.xe.XeStylesheet;

/**
 * Ping them all.
 *
 * <p>There is no thread-safety guarantee.
 *
 * @since 0.14
 */
final class RsPage extends RsWrap {

    /**
     * Ctor.
     * @param req Request
     * @param xsl XSL stylesheet
     */
    RsPage(final Request req, final String xsl) {
        super(RsPage.make(req, xsl, IterableOf::new));
    }

    /**
     * Ctor.
     * @param req Request
     * @param xsl XSL stylesheet
     * @param src Sources
     */
    RsPage(final Request req, final String xsl,
        final Scalar<Iterable<XeSource>> src) {
        super(RsPage.make(req, xsl, src));
    }

    /**
     * Make it.
     * @param req Request
     * @param xsl XSL stylesheet
     * @param src Sources
     * @return Response
     */
    private static Response make(final Request req, final String xsl,
        final Scalar<Iterable<XeSource>> src) {
        final Response raw = new RsXembly(
            new XeChain(
                new XeStylesheet(
                    String.format("/org/jpeek/web/%s.xsl", xsl)
                ),
                new XeAppend(
                    "page",
                    new XeChain(
                        new XeMillis(),
                        new XeDirectives(new Header()),
                        new XeChain(src),
                        new XeMillis(true)
                    )
                )
            )
        );
        return new RsFork(
            req,
            new FkTypes(
                "text/html",
                new RsXslt(new RsWithType(raw, "text/html"))
            ),
            new FkTypes(
                "application/vnd.jpeek+xml",
                new RsPrettyXml(new RsWithType(raw, "text/xml"))
            ),
            new FkTypes("*/*", raw)
        );
    }

}
