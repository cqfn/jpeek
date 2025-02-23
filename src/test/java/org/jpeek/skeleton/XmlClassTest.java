/*
 * The MIT License (MIT)
 *
 * SPDX-FileCopyrightText: Copyright (c) 2017-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.jpeek.skeleton;

import com.jcabi.matchers.XhtmlMatchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledForJreRange;
import org.junit.jupiter.api.condition.JRE;
import org.llorllale.cactoos.matchers.Assertion;

/**
 * Test case for {@link XmlClass}.
 * @since 0.27
 * @checkstyle JavadocMethodCheck (500 lines)
 */
final class XmlClassTest {

    @Test
    void parsesClass() {
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
    void parsesDeprecatedClass() {
        new Assertion<>(
            "Must parse deprecated class",
            XhtmlMatchers.xhtml(new ClassAsXml("BarDeprecated").value()),
            XhtmlMatchers.hasXPaths(
                "/class/methods[count(method) = 5]",
                "/class/attributes[count(attribute) = 4]"
            )
        ).affirm();
    }

    @Test
    @DisabledForJreRange(min = JRE.JAVA_8, max = JRE.JAVA_13)
    void parsesRecordClass() {
        new Assertion<>(
            "Must parse record class",
            XhtmlMatchers.xhtml(new ClassAsXml("BarRecord").value()),
            XhtmlMatchers.hasXPaths(
                "/class/methods[count(method) = 5]",
                "/class/attributes[count(attribute) = 1]"
            )
        ).affirm();
    }

    @Test
    void parsesMethodVisibility() {
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
    void thereIsNoAttributePublic() {
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
