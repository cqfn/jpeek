/*
 * The MIT License (MIT)
 *
 * SPDX-FileCopyrightText: Copyright (c) 2017-2025 Yegor Bugayenko
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
 * Index page.
 *
 * <p>There is no thread-safety guarantee.
 *
 * @since 0.10
 */
final class TkIndex implements Take {

    @Override
    public Response act(final Request req) {
        return new RsPage(
            req, "index",
            () -> new IterableOf<>(
                new XeAppend(
                    "best",
                    new XeDirectives(
                        new Joined<>(
                            new HeadOf<>(
                                20, new Results().best()
                            )
                        )
                    )
                ),
                new XeAppend(
                    "recent",
                    new XeDirectives(
                        new Joined<>(
                            new HeadOf<>(
                                25, new Results().recent()
                            )
                        )
                    )
                )
            )
        );
    }

}
