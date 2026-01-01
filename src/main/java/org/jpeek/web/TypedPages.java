/*
 * SPDX-FileCopyrightText: Copyright (c) 2017-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.jpeek.web;

import java.io.IOException;
import org.cactoos.Func;
import org.cactoos.func.IoCheckedFunc;
import org.takes.Response;
import org.takes.rs.RsWithType;

/**
 * Typed pages.
 *
 * <p>There is no thread-safety guarantee.
 *
 * @since 0.8
 */
final class TypedPages implements Func<String, Response> {

    /**
     * Origin.
     */
    private final Func<String, Response> origin;

    /**
     * Ctor.
     * @param func The func
     */
    TypedPages(final Func<String, Response> func) {
        this.origin = func;
    }

    @Override
    public Response apply(final String path) throws IOException {
        String type = "text/plain";
        if (path.endsWith(".html")) {
            type = "text/html";
        } else if (path.endsWith(".xml")) {
            type = "application/xml";
        } else if (path.endsWith(".svg")) {
            type = "image/svg+xml";
        }
        return new RsWithType(
            new IoCheckedFunc<>(this.origin).apply(path), type
        );
    }

}
