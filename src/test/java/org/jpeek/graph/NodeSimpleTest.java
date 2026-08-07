/*
 * SPDX-FileCopyrightText: Copyright (c) 2017-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.jpeek.graph;

import java.io.IOException;
import org.hamcrest.core.IsEqual;
import org.junit.jupiter.api.Test;
import org.llorllale.cactoos.matchers.Assertion;

/**
 * Test case for {@link Node.Simple}.
 * @since 0.30.9
 */
final class NodeSimpleTest {

    @Test
    void givesName() throws IOException {
        final String name = "name";
        new Assertion<>(
            "Must returns name",
            new Node.Simple(name).name(),
            new IsEqual<>(name)
        ).affirm();
    }
}
