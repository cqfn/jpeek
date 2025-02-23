/*
 * The MIT License (MIT)
 *
 * SPDX-FileCopyrightText: Copyright (c) 2017-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.jpeek;

import java.io.IOException;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Iterator;
import org.xembly.Directive;
import org.xembly.Directives;

/**
 * Xembly header for the report.
 *
 * <p>There is no thread-safety guarantee.
 *
 * @since 0.8
 */
public final class Header implements Iterable<Directive> {

    @Override
    public Iterator<Directive> iterator() {
        try {
            return new Directives()
                .attr(
                    "date",
                    ZonedDateTime.now().format(
                        DateTimeFormatter.ISO_INSTANT
                    )
                )
                .attr("version", new Version().value())
                .iterator();
        } catch (final IOException ex) {
            throw new IllegalStateException(ex);
        }
    }

}
