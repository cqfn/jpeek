/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2017-2018 Yegor Bugayenko
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

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;
import org.cactoos.BiFunc;

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
final class StickyFutures implements BiFunc<String, String, Future<Front>> {

    /**
     * Original func.
     */
    private final BiFunc<String, String, Future<Front>> origin;

    /**
     * Cache.
     */
    private final Map<String, Future<Front>> cache;

    /**
     * Max size.
     */
    private final int max;

    /**
     * Ctor.
     * @param func Original bi-function
     * @param size Max size of cache before full clean up
     */
    StickyFutures(final BiFunc<String, String, Future<Front>> func,
        final int size) {
        this.origin = func;
        this.cache = new ConcurrentHashMap<>(0);
        this.max = size;
    }

    @Override
    public Future<Front> apply(final String group, final String artifact)
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
