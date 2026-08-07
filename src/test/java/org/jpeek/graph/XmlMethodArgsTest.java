/*
 * SPDX-FileCopyrightText: Copyright (c) 2017-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.jpeek.graph;

import org.hamcrest.core.IsEqual;
import org.jpeek.FakeBase;
import org.jpeek.skeleton.Skeleton;
import org.junit.jupiter.api.Test;
import org.llorllale.cactoos.matchers.Assertion;

/**
 * Test case for {@link XmlMethodArgs}.
 * @since 0.30.9
 */
final class XmlMethodArgsTest {

    @Test
    void returnsEmptyStringWhenNoArgsSpecified() throws Exception {
        new Assertion<>(
            "Must returns empty string when method has no arguments",
            new XmlMethodArgs(
                new Skeleton(new FakeBase("MethodMethodCalls")).xml().nodes(
                    "//method[@name='methodOne']"
                ).get(0)
            ).asString(),
            new IsEqual<>("")
        ).affirm();
    }

    @Test
    void givesArgsForMultipleArgs() throws Exception {
        new Assertion<>(
            "Must serialize args when multiple arguments are in the method node",
            new XmlMethodArgs(
                new Skeleton(new FakeBase("MethodsWithDiffParamTypes")).xml().nodes(
                    "//method[@name='methodThree']"
                ).get(0)
            ).asString(),
            new IsEqual<>(
                "Ljava/lang/String:I"
            )
        ).affirm();
    }
}
