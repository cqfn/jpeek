/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2017-2022 Yegor Bugayenko
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
