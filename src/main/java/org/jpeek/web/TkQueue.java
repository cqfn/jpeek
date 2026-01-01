/*
 * SPDX-FileCopyrightText: Copyright (c) 2017-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.jpeek.web;

import java.io.IOException;
import org.cactoos.Text;
import org.takes.Request;
import org.takes.Response;
import org.takes.Take;
import org.takes.rs.RsText;

/**
 * All of them.
 *
 * <p>There is no thread-safety guarantee.
 *
 * @since 0.18
 */
final class TkQueue implements Take {

    /**
     * The queue.
     */
    private final Text futures;

    /**
     * Ctor.
     * @param frs Futures
     */
    TkQueue(final Text frs) {
        this.futures = frs;
    }

    // @checkstyle IllegalCatchCheck (10 lines)
    @SuppressWarnings("PMD.AvoidCatchingGenericException")
    @Override
    public Response act(final Request req) throws IOException {
        try {
            return new RsText(
                this.futures.asString()
            );
        } catch (final Exception exception) {
            throw new IOException(exception);
        }
    }

}
