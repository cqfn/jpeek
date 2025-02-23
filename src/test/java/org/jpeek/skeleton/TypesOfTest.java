/*
 * SPDX-FileCopyrightText: Copyright (c) 2017-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.jpeek.skeleton;

import com.jcabi.matchers.XhtmlMatchers;
import org.junit.jupiter.api.Test;
import org.llorllale.cactoos.matchers.Assertion;
import org.xembly.Directives;
import org.xembly.Xembler;

/**
 * Test case for {@link TypesOf}.
 * @since 0.27
 * @checkstyle JavadocMethodCheck (500 lines)
 */
final class TypesOfTest {

    @Test
    void parsesSignature() {
        new Assertion<>(
            "Must parse signature",
            XhtmlMatchers.xhtml(
                new Xembler(
                    new Directives().add("method").append(
                        new TypesOf("(Ljava/lang/String;Lorg/jpeek/Test;)Z")
                    )
                ).xmlQuietly()
            ),
            XhtmlMatchers.hasXPaths(
                "/method/args[count(arg) = 2]",
                "/method/args/arg[@type = 'Ljava/lang/String']",
                "/method[child::return='Z']"
            )
        ).affirm();
    }

}
