/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2017-2024 Yegor Bugayenko
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
import org.jpeek.FakeBase;
import org.jpeek.skeleton.Skeleton;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.llorllale.cactoos.matchers.Assertion;
import org.llorllale.cactoos.matchers.IsTrue;

/**
 * Test class for CCM calculus.
 * @since 0.30.9
 * @todo #522:15m/DEV Implement test with one CC it should find ncc as 1, and use it in further
 *  calculation of ccm metric
 * @todo #522:15m/DEV Implement test with several CC here more complicated graph: several methods
 *  with references is such way that we would get edges between methods
 * @todo #522:15m/DEV Implement test with only constructor test would be parametrized, depending
 *  on parameter constructor may be ingored
 * @todo #522:15m/DEV Implement test with constructor and several CC. Parametrized on constructor,
 *  explore behavior when we have constructor and regular methods
 * @todo #522:15m/DEV Implement test with variable shadowing. Whether calculus notices that no
 *  edge between methods needed or it should just ignore it.
 * @todo #522:15m/DEV Implement test with static methods check. Whether calculus notices that
 *  method is static or not. I guess there is not big difference between static and non-static
 *  methods, but i guess it is better to add such tests anyway
 */
final class CcmTest {
    @Test
    @Disabled
    void createsSkeletonWithMeta() throws IOException {
        final XML result = new Ccm().node(
            "CCM", new HashMap<>(0), new Skeleton(
                new FakeBase(
                    "Foo", "Bar"
                )
            ).xml()
        );
        new Assertion<>(
            "Must create CCM report",
            result.toString(),
            XhtmlMatchers.hasXPath(
                "/metric/app/package/class/vars/var"
            )
        ).affirm();
        new Assertion<>(
            "Must have 2 ncc vars",
            result.xpath("/metric/app/package/class/vars/var[@id=\'ncc\']/text()").size() == 2,
            new IsTrue()
        ).affirm();
    }
}
