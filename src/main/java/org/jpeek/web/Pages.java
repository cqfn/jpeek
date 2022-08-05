/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2017-2022 Yegor Bugayenko
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NON-INFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
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
