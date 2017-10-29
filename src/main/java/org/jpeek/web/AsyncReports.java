/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2017 Yegor Bugayenko
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
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import org.cactoos.BiFunc;
import org.cactoos.Func;
import org.cactoos.func.IoCheckedBiFunc;
import org.cactoos.func.StickyBiFunc;
import org.cactoos.func.SyncBiFunc;
import org.takes.Response;
import org.takes.rs.RsHtml;

/**
 * Async reports.
 *
 * <p>There is no thread-safety guarantee.
 *
 * @author Yegor Bugayenko (yegor256@gmail.com)
 * @version $Id$
 * @since 0.8
 * @checkstyle ClassDataAbstractionCouplingCheck (500 lines)
 */
final class AsyncReports implements
    BiFunc<String, String, Func<String, Response>> {

    /**
     * Cache.
     */
    private final BiFunc<String, String, Future<Func<String, Response>>> cache;

    /**
     * Ctor.
     * @param func Original bi-function
     */
    AsyncReports(final BiFunc<String, String, Func<String, Response>> func) {
        this.cache = new SyncBiFunc<>(new StickyBiFunc<>(new Futures(func)));
    }

    @Override
    public Func<String, Response> apply(final String group,
        final String artifact) throws IOException {
        final Future<Func<String, Response>> future =
            new IoCheckedBiFunc<>(this.cache).apply(group, artifact);
        final Func<String, Response> output;
        if (future.isDone()) {
            try {
                output = future.get();
            } catch (final InterruptedException | ExecutionException ex) {
                throw new IllegalStateException(ex);
            }
        } else {
            output = input -> new RsHtml(
                "<html>We're still building this report...</html>"
            );
        }
        return output;
    }

}
