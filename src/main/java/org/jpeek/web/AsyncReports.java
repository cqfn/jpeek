/*
 * The MIT License (MIT)
 *
 * SPDX-FileCopyrightText: Copyright (c) 2017-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.jpeek.web;

import com.jcabi.log.Logger;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import org.cactoos.BiFunc;
import org.cactoos.Func;
import org.cactoos.func.IoCheckedBiFunc;
import org.cactoos.iterable.IterableOf;
import org.takes.Response;
import org.takes.rq.RqFake;
import org.takes.rs.RsWithStatus;
import org.takes.rs.xe.XeAppend;

/**
 * Async reports.
 *
 * <p>There is no thread-safety guarantee.
 *
 * @since 0.8
 */
final class AsyncReports implements
    BiFunc<String, String, Func<String, Response>> {

    /**
     * Cache.
     */
    private final BiFunc<String, String, Future<Func<String, Response>>> cache;

    /**
     * Starts.
     */
    private final Map<String, Long> starts;

    /**
     * Ctor.
     * @param func Original bi-function
     */
    AsyncReports(
        final BiFunc<String, String, Future<Func<String, Response>>> func) {
        this.cache = func;
        this.starts = new ConcurrentHashMap<>(0);
    }

    @Override
    public Func<String, Response> apply(final String group,
        final String artifact) throws IOException {
        final Future<Func<String, Response>> future = new IoCheckedBiFunc<>(
            this.cache
        ).apply(group, artifact);
        final Func<String, Response> output;
        if (future.isCancelled()) {
            output = input -> new RsPage(
                new RqFake(),
                "error",
                () -> new IterableOf<>(
                    new XeAppend("group", group),
                    new XeAppend("artifact", artifact),
                    new XeAppend("future", future.toString())
                )
            );
        } else if (future.isDone()) {
            try {
                output = future.get();
            } catch (final InterruptedException | ExecutionException ex) {
                throw new IllegalStateException(ex);
            }
        } else {
            final long msec = System.currentTimeMillis()
                - this.starts.computeIfAbsent(
                    String.format("%s:%s", group, artifact),
                    s -> System.currentTimeMillis()
                );
            output = input -> new RsWithStatus(
                new RsPage(
                    new RqFake(),
                    "wait",
                    () -> new IterableOf<>(
                        new XeAppend("group", group),
                        new XeAppend("artifact", artifact),
                        new XeAppend("future", future.toString()),
                        new XeAppend("msec", Long.toString(msec)),
                        new XeAppend(
                            "spent",
                            Logger.format("%[ms]s", msec)
                        )
                    )
                ),
                HttpURLConnection.HTTP_NOT_FOUND
            );
        }
        return output;
    }

}
