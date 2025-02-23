/*
 * SPDX-FileCopyrightText: Copyright (c) 2017-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.jpeek;

import java.io.IOException;
import java.nio.file.Path;
import org.cactoos.iterable.Joined;

/**
 * Source code base.
 *
 * <p>There is no thread-safety guarantee.
 *
 * @since 0.1
 * @checkstyle JavadocTagsCheck (500 lines)
 */
public interface Base {

    /**
     * Take all files from the base.
     * @return The iterable of files
     * @throws IOException If fails
     */
    Iterable<Path> files() throws IOException;

    /**
     * Concat.
     */
    final class Concat implements Base {
        /**
         * Left Base.
         */
        private final Base left;

        /**
         * Left Base.
         */
        private final Base right;

        /**
         * Ctor.
         * @param one Left
         * @param two Right
         */
        public Concat(final Base one, final Base two) {
            this.left = one;
            this.right = two;
        }

        @Override
        public Iterable<Path> files() throws IOException {
            return new Joined<Path>(this.left.files(), this.right.files());
        }
    }

}
