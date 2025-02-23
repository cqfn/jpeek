/*
 * The MIT License (MIT)
 *
 * SPDX-FileCopyrightText: Copyright (c) 2017-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.jpeek.graph;

import com.jcabi.xml.XMLDocument;
import org.cactoos.text.Joined;
import org.hamcrest.core.IsEqual;
import org.junit.jupiter.api.Test;
import org.llorllale.cactoos.matchers.Assertion;

/**
 * Test case for {@link XmlMethodCall}.
 * @since 0.44
 */
final class XmlMethodCallTest {

    @Test
    void hasClassMethodAndArgs() throws Exception {
        new Assertion<>(
            "Must have class name, method name and args.",
            new XmlMethodCall(
                new XMLDocument(
                    new Joined(
                        "",
                        "<op code=\"call\">",
                        "  <name>OverloadMethods.methodOne</name>",
                        "  <args>",
                        "    <arg type=\"Ljava/lang/String\">?</arg>",
                        "    <arg type=\"Z\">?</arg>",
                        "  </args>",
                        "</op>"
                    ).asString()
                ).nodes("//op").get(0)
            ).asString(),
            new IsEqual<>(
                "OverloadMethods.methodOne.Ljava/lang/String:Z"
            )
        ).affirm();
    }
}
