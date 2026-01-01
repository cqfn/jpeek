/*
 * SPDX-FileCopyrightText: Copyright (c) 2017-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.jpeek.web;

import org.cactoos.iterable.HeadOf;
import org.cactoos.iterable.IterableOf;
import org.cactoos.iterable.Joined;
import org.takes.Request;
import org.takes.Response;
import org.takes.Take;
import org.takes.rs.xe.XeAppend;
import org.takes.rs.xe.XeDirectives;

/**
 * Mistakes page.
 *
 * <p>There is no thread-safety guarantee.
 *
 * @since 0.14
 */
final class TkMistakes implements Take {

    @Override
    public Response act(final Request req) {
        return new RsPage(
            req, "mistakes",
            () -> new IterableOf<>(
                new XeAppend(
                    "worst",
                    new XeDirectives(
                        new Joined<>(
                            new HeadOf<>(
                                20, new Mistakes().worst()
                            )
                        )
                    )
                )
            )
        );
    }

}
