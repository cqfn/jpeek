/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2017-2019 Yegor Bugayenko
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
 * @checkstyle JavadocTagsCheck (500 lines)
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
