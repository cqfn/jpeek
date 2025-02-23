/*
 * The MIT License (MIT)
 *
 * SPDX-FileCopyrightText: Copyright (c) 2017-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.jpeek.graph;

import com.jcabi.xml.XML;
import org.hamcrest.core.IsEqual;
import org.jpeek.FakeBase;
import org.jpeek.skeleton.Skeleton;
import org.junit.jupiter.api.Test;
import org.llorllale.cactoos.matchers.Assertion;

/**
 * Test case for {@link XmlMethodSignature}.
 * @since 0.30.9
 */
final class XmlMethodSignatureTest {

    @Test
    void givesArgsForMultipleArgs() throws Exception {
        final XML skeleton = new Skeleton(
            new FakeBase("MethodsWithDiffParamTypes")
        ).xml();
        new Assertion<>(
            "Must create method signature with multiple arguments.",
            new XmlMethodSignature(
                skeleton.nodes("//class").get(0),
                skeleton.nodes("//method[@name='methodThree']").get(0)
            ).asString(),
            new IsEqual<>(
                "MethodsWithDiffParamTypes.methodThree.Ljava/lang/String:I"
            )
        ).affirm();
    }
}
