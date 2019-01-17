/**
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

import com.jcabi.log.Logger;
import com.jcabi.log.VerboseCallable;
import com.jcabi.log.VerboseThreads;
import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import org.cactoos.BiFunc;
import org.cactoos.Text;
import org.cactoos.collection.Mapped;
import org.cactoos.scalar.AvgOf;
import org.cactoos.text.JoinedText;

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
    BiFunc<String, String, Future<Front>>, Text {

    /**
     * Original func.
     */
    private final BiFunc<String, String, Front> origin;

    /**
     * Service.
     */
    private final ExecutorService service;

    /**
     * Queue.
     */
    private final Map<String, Long> queue;

    /**
     * Long.
     */
    private final Collection<Long> times;

    /**
     * Ctor.
     * @param func Original bi-function
     */
    Futures(final BiFunc<String, String, Front> func) {
        this.origin = func;
        this.service = Executors.newFixedThreadPool(
            Runtime.getRuntime().availableProcessors(),
            new VerboseThreads(Futures.class)
        );
        this.queue = new ConcurrentSkipListMap<>();
        this.times = new CopyOnWriteArrayList<>();
    }

    @Override
    public Future<Front> apply(final String group,
        final String artifact) {
        final String target = String.format("%s:%s", group, artifact);
        this.queue.put(target, System.currentTimeMillis());
        // @checkstyle MagicNumber (1 line)
        if (this.times.size() > 1000) {
            this.times.clear();
        }
        return this.service.submit(
            new VerboseCallable<>(
                () -> {
                    final Front func =
                        this.origin.apply(group, artifact);
                    this.times.add(
                        System.currentTimeMillis() - this.queue.remove(target)
                    );
                    return func;
                },
                true, true
            )
        );
    }

    @Override
    public String asString() throws IOException {
        return Logger.format(
            // @checkstyle LineLength (1 line)
            "Artifacts=%d, processors=%d, threads=%d, freeMemory=%dM, maxMemory=%dM, totalMemory=%dM, ETA=%[ms]s:\n%s\n\nThreads: %s",
            this.queue.size(),
            Runtime.getRuntime().availableProcessors(),
            Thread.getAllStackTraces().keySet().size(),
            // @checkstyle MagicNumber (3 lines)
            Runtime.getRuntime().freeMemory() / (1024L << 10),
            Runtime.getRuntime().maxMemory() / (1024L << 10),
            Runtime.getRuntime().totalMemory() / (1024L << 10),
            new AvgOf(
                this.times.toArray(new Long[this.times.size()])
            ).longValue() * (long) this.queue.size(),
            new JoinedText(", ", this.queue.keySet()).asString(),
            new JoinedText(
                ", ",
                new Mapped<>(
                    Thread::getName,
                    Thread.getAllStackTraces().keySet()
                )
            ).asString()
        );
    }
}
