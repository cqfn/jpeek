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
package org.jpeek.skeleton;

import com.jcabi.matchers.XhtmlMatchers;
import org.junit.jupiter.api.Test;
import org.llorllale.cactoos.matchers.Assertion;

/**
 * Test case for {@link XmlClass}.
 * @since 0.27
 * @checkstyle JavadocMethodCheck (500 lines)
 */
@SuppressWarnings("PMD.AvoidDuplicateLiterals")
public final class XmlClassTest {

    @Test
    public void parsesClass() {
        new Assertion<>(
            "Must parse class",
            XhtmlMatchers.xhtml(new ClassAsXml("Bar").value()),
            XhtmlMatchers.hasXPaths(
                "/class/methods[count(method) = 5]",
                "/class/attributes[count(attribute) = 4]"
            )
        ).affirm();
    }

    @Test
    public void parsesMethodVisibility() {
        new Assertion<>(
            "Must parse method visibility",
            XhtmlMatchers.xhtml(
                new ClassAsXml("ClassWithDifferentMethodVisibilities").value()
            ),
            XhtmlMatchers.hasXPaths(
                "/class/methods/method[@visibility = 'public']",
                "/class/methods/method[@visibility = 'private']",
                "/class/methods/method[@visibility = 'default']",
                "/class/methods/method[@visibility = 'protected']"
            )
        ).affirm();
    }

    @Test
    public void thereIsNoAttributePublic() {
        new Assertion<>(
            "attribute public does not exists",
            XhtmlMatchers.xhtml(
                new ClassAsXml("ClassWithDifferentMethodVisibilities").value()
            ),
            XhtmlMatchers.hasXPaths(
                "/class/methods/method[not (@public)]"
            )
        ).affirm();
    }
}
