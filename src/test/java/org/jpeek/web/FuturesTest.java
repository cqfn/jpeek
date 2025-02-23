/*
 * SPDX-FileCopyrightText: Copyright (c) 2017-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.jpeek.web;

import org.cactoos.iterable.IterableOf;
import org.hamcrest.core.IsEqual;
import org.hamcrest.core.IsNot;
import org.junit.jupiter.api.Test;
import org.llorllale.cactoos.matchers.Assertion;
import org.llorllale.cactoos.matchers.IsBlank;
import org.takes.rq.RqFake;
import org.takes.rs.xe.XeAppend;

/**
 * Test case for {@link Futures}.
 * @since 0.32
 * @checkstyle JavadocMethodCheck (500 lines)
 */
final class FuturesTest {

    @Test
    void testSimpleScenario() throws Exception {
        new Assertion<>(
            "Futures returns Response",
            new Futures(
                (group, artifact) -> input -> new RsPage(
                    new RqFake(),
                    "wait",
                    () -> new IterableOf<>(
                        new XeAppend("group", group),
                        new XeAppend("artifact", artifact)
                    )
                )
            ).apply("g", "a").get().apply("test"),
            new IsNot<>(new IsEqual<>(null))
        ).affirm();
    }

    @Test
    void testIgnoresCrashes() throws Exception {
        new Assertion<>(
            "Futures don't crash",
            new Futures(
                (group, artifact) -> {
                    throw new UnsupportedOperationException("intended");
                }
            ).apply("g1", "a1").get().apply("test-2"),
            new IsNot<>(new IsEqual<>(null))
        ).affirm();
    }

    @Test
    void testAsString() throws Exception {
        final Futures futures = new Futures(
            (group, artifact) -> input -> new RsPage(
                new RqFake(),
                "",
                () -> new IterableOf<>(
                    new XeAppend("group", group),
                    new XeAppend("artifact", artifact)
                )
            )
        );
        futures.apply("g", "a").get();
        new Assertion<>(
            "Resulting string shouldn't be blank",
            futures.asString(),
            new IsNot<>(new IsBlank())
        ).affirm();
    }
}
