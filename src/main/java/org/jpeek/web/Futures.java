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

import com.jcabi.log.VerboseThreads;
import java.io.IOException;
import java.util.Collection;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import org.cactoos.BiFunc;
import org.cactoos.Func;
import org.cactoos.Text;
import org.cactoos.text.JoinedText;
import org.takes.Response;

/**
 * Futures for {@link AsyncReports}.
 *
 * <p>There is no thread-safety guarantee.
 *
 * @author Yegor Bugayenko (yegor256@gmail.com)
 * @version $Id$
 * @since 0.8
 * @checkstyle ClassDataAbstractionCouplingCheck (500 lines)
 */
final class Futures implements
    BiFunc<String, String, Future<Func<String, Response>>>, Text {

    /**
     * Original func.
     */
    private final BiFunc<String, String, Func<String, Response>> origin;

    /**
     * Service.
     */
    private final ExecutorService service;

    /**
     * Queue.
     */
    private final Collection<String> queue;

    /**
     * Ctor.
     * @param func Original bi-function
     */
    Futures(final BiFunc<String, String, Func<String, Response>> func) {
        this.origin = func;
        this.service = Executors.newFixedThreadPool(
            Runtime.getRuntime().availableProcessors(),
            new VerboseThreads(Futures.class)
        );
        this.queue = new CopyOnWriteArrayList<>();
    }

    @Override
    public Future<Func<String, Response>> apply(final String group,
        final String artifact) {
        final String target = String.format("%s:%s", group, artifact);
        this.queue.add(target);
        return this.service.submit(
            () -> {
                final Func<String, Response> func =
                    this.origin.apply(group, artifact);
                this.queue.remove(target);
                return func;
            }
        );
    }

    @Override
    public String asString() throws IOException {
        return String.format(
            "%d artifacts, %d threads:\n%s",
            this.queue.size(),
            Runtime.getRuntime().availableProcessors(),
            new JoinedText(", ", this.queue).asString()
        );
    }

    @Override
    public int compareTo(final Text txt) {
        throw new UnsupportedOperationException("#compareTo()");
    }
}
