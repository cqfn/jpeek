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
package org.jpeek.calculus.java;

import com.jcabi.matchers.XhtmlMatchers;
import com.jcabi.xml.XML;
import java.io.IOException;
import java.util.HashMap;
import org.hamcrest.core.IsEqual;
import org.jpeek.FakeBase;
import org.jpeek.skeleton.Skeleton;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.llorllale.cactoos.matchers.Assertion;

/**
 * Test case for {@link Lcom4}.
 * @since 0.30.9
 */
public final class Lcom4Test {

    @Test
    @Disabled
    public void createsXmlCalculusWithXpaths() throws IOException {
        final XML result = new Lcom4().node(
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

    @ParameterizedTest
    @Disabled
    @CsvFileSource(resources = "/org/jpeek/calculus/java/lcom4-params.csv")
    public void methodMethodCalls(final String file, final String value) throws Exception {
        final XML result = new Lcom4().node(
            "", new HashMap<>(0), new Skeleton(
                new FakeBase(file)
            ).xml()
        );
        new Assertion<>(
            "Must create LCOM4 value",
            result.xpath("/metric/app/package/class/@value").get(0),
            new IsEqual<>(value)
        ).affirm();
    }
}
