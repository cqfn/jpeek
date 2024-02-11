/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2017-2024 Yegor Bugayenko
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
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import org.cactoos.BiFunc;
import org.cactoos.Func;
import org.cactoos.Text;
import org.cactoos.io.InputOf;
import org.cactoos.iterable.IterableOf;
import org.cactoos.iterable.Mapped;
import org.cactoos.number.AvgOf;
import org.cactoos.text.Joined;
import org.cactoos.text.TextOf;
import org.cactoos.text.UncheckedText;
import org.takes.Response;
import org.takes.rq.RqFake;
import org.takes.rs.xe.XeAppend;

/**
 * Futures for {@link AsyncReports}.
 *
 * <p>There is no thread-safety guarantee.
 *
 * @since 0.8
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
    private final Map<String, Long> queue;

    /**
     * Long.
     */
    private final Collection<Long> times;

    /**
     * Ctor.
     * @param func Original bi-function
     */
    Futures(final BiFunc<String, String, Func<String, Response>> func) {
        this.origin = func;
        this.service = Executors.newFixedThreadPool(
            Math.min(Runtime.getRuntime().availableProcessors(), 4),
            new VerboseThreads(Futures.class)
        );
        this.queue = new ConcurrentSkipListMap<>();
        this.times = new CopyOnWriteArrayList<>();
    }

    @Override
    @SuppressWarnings("PMD.AvoidCatchingGenericException")
    public Future<Func<String, Response>> apply(final String group,
        final String artifact) {
        final String target = String.format("%s:%s", group, artifact);
        this.queue.put(target, System.currentTimeMillis());
        if (this.times.size() > 1000) {
            this.times.clear();
        }
        return this.service.submit(
            new VerboseCallable<>(
                () -> {
                    Func<String, Response> front;
                    try {
                        Logger.info(
                            this, "Started processing of %s:%s...",
                            group, artifact
                        );
                        front = this.origin.apply(group, artifact);
                        this.times.add(
                            System.currentTimeMillis() - this.queue.remove(target)
                        );
                        Logger.info(
                            this, "Finished processing of %s:%s",
                            artifact, group
                        );
                    // @checkstyle IllegalCatchCheck (4 lines)
                    // @checkstyle AvoidCatchingGenericException (4 lines)
                    } catch (final Exception ex) {
                        Logger.error(
                            this, "Failure in %s:%s: %s",
                            group, artifact, ex.getMessage()
                        );
                        front = input -> new RsPage(
                            new RqFake(),
                            "exception",
                            () -> new IterableOf<>(
                                new XeAppend("group", group),
                                new XeAppend("artifact", artifact),
                                new XeAppend(
                                    "stacktrace",
                                    new UncheckedText(
                                        new TextOf(new InputOf(ex))
                                    ).asString()
                                )
                            )
                        );
                    }
                    return front;
                },
                true, true
            )
        );
    }

    @Override
    public String asString() throws Exception {
        return Logger.format(
            // @checkstyle LineLength (1 line)
            "Artifacts=%d, processors=%d, threads=%d, freeMemory=%dM, maxMemory=%dM, totalMemory=%dM, ETA=%[ms]s:\n%s\n\nThreads: %s",
            this.queue.size(),
            Runtime.getRuntime().availableProcessors(),
            Thread.getAllStackTraces().keySet().size(),
            Runtime.getRuntime().freeMemory() / (1024L << 10),
            Runtime.getRuntime().maxMemory() / (1024L << 10),
            Runtime.getRuntime().totalMemory() / (1024L << 10),
            new AvgOf(
                this.times.toArray(new Long[this.times.size()])
            ).longValue() * (long) this.queue.size(),
            new Joined(", ", this.queue.keySet()).asString(),
            new Joined(
                ", ",
                new Mapped<>(
                    Thread::getName,
                    Thread.getAllStackTraces().keySet()
                )
            ).asString()
        );
    }

    /**
     * Shut it down.
     * @return TRUE if terminated OK
     * @throws InterruptedException If interrupted while waiting
     * @todo #1:1h shutdown can not be completed in 1 minute during testing,
     *  should be checked what happens and may be require some fix
     */
    public boolean shutdown() throws InterruptedException {
        this.service.shutdown();
        return this.service.awaitTermination(2L, TimeUnit.MINUTES);
    }
}
