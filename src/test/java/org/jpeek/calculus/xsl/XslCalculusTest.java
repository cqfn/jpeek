/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2017-2019 Yegor Bugayenko
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
public final class XslCalculusTest {

    @Test
    public void createsXmlCalculusWithXpaths() throws IOException {
        final XML result = new XslCalculus("LCOM").node(
            new HashMap<>(0), new Skeleton(
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
    public void createsXmlCalculusWithEmptyProject() throws IOException {
        final XML result = new XslCalculus("LCOM2").node(
            new HashMap<>(0), new Skeleton(new FakeBase()).xml()
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
