/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2017-2025 Yegor Bugayenko
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

import com.jcabi.xml.XML;
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
    void returnsEmptyStringWhenNoArgsSpecificied() throws Exception {
        final XML method = new Skeleton(new FakeBase("MethodMethodCalls")).xml().nodes(
            "//method[@name='methodOne']"
        ).get(0);
        new Assertion<>(
            "Must returns empty string when method has no arguments",
            new XmlMethodArgs(method).asString(),
            new IsEqual<>("")
        ).affirm();
    }

    @Test
    void givesArgsForMultipleArgs() throws Exception {
        final XML method = new Skeleton(new FakeBase("MethodsWithDiffParamTypes")).xml().nodes(
            "//method[@name='methodThree']"
        ).get(0);
        new Assertion<>(
            "Must serialize args when multiple arguments are in the method node",
            new XmlMethodArgs(method).asString(),
            new IsEqual<>(
                "Ljava/lang/String:I"
            )
        ).affirm();
    }
}
