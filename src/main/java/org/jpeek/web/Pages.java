/*
 * The MIT License (MIT)
 *
 * SPDX-FileCopyrightText: Copyright (c) 2017-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.jpeek.web;

import java.io.IOException;
import java.nio.file.Path;
import org.cactoos.Func;
import org.cactoos.text.TextOf;
import org.cactoos.text.UncheckedText;
import org.takes.Response;
import org.takes.rs.RsWithBody;

/**
 * Pages in one report.
 *
 * <p>There is no thread-safety guarantee.
 *
 * @since 0.8
 */
final class Pages implements Func<String, Response> {

    /**
     * Directory with files.
     */
    private final Path home;

    /**
     * Ctor.
     * @param dir Home dir
     */
    Pages(final Path dir) {
        this.home = dir;
    }

    @Override
    public Response apply(final String path) throws IOException {
        return new RsWithBody(
            new UncheckedText(
                new TextOf(
                    this.home.resolve(path)
                )
            ).asString()
        );
    }

}
