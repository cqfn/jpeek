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

import org.cactoos.iterable.IterableOf;
import org.hamcrest.core.IsEqual;
import org.hamcrest.core.IsNot;
import org.junit.Test;
import org.llorllale.cactoos.matchers.Assertion;
import org.takes.rq.RqFake;
import org.takes.rs.xe.XeAppend;

/**
 * Test case for {@link Futures}.
 * @since 0.32
 * @checkstyle JavadocMethodCheck (500 lines)
 * @checkstyle JavadocTagsCheck (500 lines)
 * @checkstyle ClassDataAbstractionCouplingCheck (500 lines)
 */
public final class FuturesTest {

    @Test
    public void testSimpleScenario() throws Exception {
        new Assertion<>(
            "Futures returns Response",
            new Futures(
                (artifact, group) -> input -> new RsPage(
                    new RqFake(),
                    "wait",
                    () -> new IterableOf<>(
                        new XeAppend("group", group),
                        new XeAppend("artifact", artifact)
                    )
                )
            ).apply("a", "g").get().apply("test"),
            new IsNot<>(new IsEqual<>(null))
        ).affirm();
    }

    @Test
    public void testIgnoresCrashes() throws Exception {
        new Assertion<>(
            "Futures don't crash",
            new Futures(
                (artifact, group) -> {
                    throw new UnsupportedOperationException("intended");
                }
            ).apply("a1", "g1").get().apply("test-2"),
            new IsNot<>(new IsEqual<>(null))
        ).affirm();
    }

}
