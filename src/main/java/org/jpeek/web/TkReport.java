/*
 * SPDX-FileCopyrightText: Copyright (c) 2017-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.jpeek.web;

import com.jcabi.xml.ClasspathSources;
import com.jcabi.xml.XMLDocument;
import com.jcabi.xml.XSLDocument;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.regex.Matcher;
import org.cactoos.BiFunc;
import org.cactoos.Func;
import org.cactoos.func.IoCheckedBiFunc;
import org.cactoos.func.IoCheckedFunc;
import org.jpeek.App;
import org.takes.Response;
import org.takes.facets.fork.RqRegex;
import org.takes.facets.fork.TkRegex;
import org.takes.facets.forward.RsForward;
import org.takes.rs.RsStatus;
import org.takes.rs.RsText;
import org.takes.rs.RsWithType;
import org.xembly.Directives;
import org.xembly.Xembler;

/**
 * Report page.
 *
 * <p>There is no thread-safety guarantee.
 *
 * @since 0.5
 */
final class TkReport implements TkRegex {

    /**
     * Maker or reports.
     */
    private final BiFunc<String, String, Func<String, Response>> reports;

    /**
     * Results.
     */
    private final Results results;

    /**
     * Ctor.
     * @param rpts Reports
     * @param rslts Results
     */
    TkReport(final BiFunc<String, String, Func<String, Response>> rpts,
        final Results rslts) {
        this.reports = rpts;
        this.results = rslts;
    }

    @Override
    public Response act(final RqRegex req) throws IOException {
        final Matcher matcher = req.matcher();
        final String path = matcher.group(3);
        if (path.isEmpty()) {
            throw new RsForward(
                String.format("%s/index.html", matcher.group(0))
            );
        }
        Response response = new IoCheckedFunc<>(
            new IoCheckedBiFunc<>(this.reports).apply(
                matcher.group(1), matcher.group(2)
            )
        ).apply(path.substring(1));
        if (new RsStatus.Base(response).status() == HttpURLConnection.HTTP_NOT_FOUND
            && "badge.svg".equals(matcher.group(3))) {
            final String artifact = String.format(
                "%s:%s", matcher.group(1), matcher.group(2)
            );
            final Directives dirs = new Directives().add("badge")
                .attr("style", "round");
            if (this.results.exists(artifact)) {
                dirs.set(this.results.score(artifact));
            } else {
                dirs.set(0).attr("unknown", "true");
            }
            response = new RsWithType(
                new RsText(
                    new XSLDocument(
                        App.class.getResourceAsStream("xsl/badge.xsl")
                    ).with(new ClasspathSources()).transform(
                        new XMLDocument(new Xembler(dirs).xmlQuietly())
                    ).toString()
                ),
                "image/svg+xml"
            );
        }
        return response;
    }

}
