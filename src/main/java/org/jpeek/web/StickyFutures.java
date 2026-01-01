/*
 * SPDX-FileCopyrightText: Copyright (c) 2017-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.jpeek.web;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;
import org.cactoos.BiFunc;
import org.cactoos.Func;
import org.takes.Response;

/**
 * Futures for {@link AsyncReports}.
 *
 * <p>There is no thread-safety guarantee.
 *
 * @since 0.8
 */
final class StickyFutures
    implements BiFunc<String, String, Future<Func<String, Response>>> {

    /**
     * Original func.
     */
    private final BiFunc<String, String, Future<Func<String, Response>>> origin;

    /**
     * Cache.
     */
    private final Map<String, Future<Func<String, Response>>> cache;

    /**
     * Max size.
     */
    private final int max;

    /**
     * Ctor.
     * @param func Original bi-function
     * @param size Max size of cache before full clean up
     */
    StickyFutures(final BiFunc<String, String,
        Future<Func<String, Response>>> func, final int size) {
        this.origin = func;
        this.cache = new ConcurrentHashMap<>(0);
        this.max = size;
    }

    @Override
    public Future<Func<String, Response>> apply(
        final String group, final String artifact)
        throws Exception {
        synchronized (this.cache) {
            if (this.cache.size() > this.max) {
                this.cache.clear();
            }
            final String target = String.format("%s:%s", group, artifact);
            if (!this.cache.containsKey(target)
                || this.cache.get(target).isCancelled()) {
                this.cache.put(target, this.origin.apply(group, artifact));
            }
            return this.cache.get(target);
        }
    }

}
