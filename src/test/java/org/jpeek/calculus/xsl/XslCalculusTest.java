/*
 * SPDX-FileCopyrightText: Copyright (c) 2017-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.jpeek.calculus.xsl;

import com.jcabi.matchers.XhtmlMatchers;
import com.jcabi.xml.XML;
import java.io.IOException;
import java.util.HashMap;
import org.jpeek.FakeBase;
import org.jpeek.skeleton.Skeleton;
import org.junit.jupiter.api.Test;
import org.llorllale.cactoos.matchers.Assertion;

/**
 * Test case for {@link XslCalculus}.
 * @since 0.30.9
 */
final class XslCalculusTest {

    @Test
    void createsXmlCalculusWithXpaths() throws IOException {
        final XML result = new XslCalculus().node(
            "LCOM", new HashMap<>(0), new Skeleton(
                new FakeBase(
                    "NoMethods", "Bar", "OverloadMethods",
                    "OnlyOneMethodWithParams", "WithoutAttributes"
                )
            ).xml()
        );
        new Assertion<>(
            "Must create LCOM report",
            result.toString(),
            XhtmlMatchers.hasXPaths(
                "/metric/app/package/class/vars",
                "/metric/app/package/class/vars/var",
                "/metric/app/package/class[@value]"
            )
        ).affirm();
    }

    @Test
    void createsXmlCalculusWithEmptyProject() throws IOException {
        final XML result = new XslCalculus().node(
            "LCOM2", new HashMap<>(0), new Skeleton(new FakeBase()).xml()
        );
        new Assertion<>(
            "Report for empty project created",
            result.toString(),
            XhtmlMatchers.hasXPaths(
                "/metric[title='LCOM2']/app[@id]"
            )
        ).affirm();
    }

}
