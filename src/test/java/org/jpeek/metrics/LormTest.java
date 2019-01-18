/**
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

package org.jpeek.metrics;

import com.jcabi.xml.XML;
import com.jcabi.xml.XSLDocument;
import org.cactoos.io.ResourceOf;
import org.cactoos.list.ListOf;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.IsEqual;
import org.jpeek.FakeBase;
import org.jpeek.skeleton.Skeleton;
import org.junit.Test;

/**
 * Test case for LORM.
 * LORM = Logical Relatedness of Methods.
 * @author Ilya Kharlamov (ilya.kharlamov@gmail.com)
 * @version $Id$
 * @since 0.28
 * @checkstyle JavadocMethodCheck (500 lines)
 * @checkstyle ClassDataAbstractionCouplingCheck (500 lines)
 */
public final class LormTest {

    @Test
    public void calculatesVariables() throws Exception {
        final XML xml = new XSLDocument(
            new ResourceOf(
                "org/jpeek/metrics/LORM.xsl"
            ).stream()
        ).transform(
            new Skeleton(
                new FakeBase("TwoCommonMethods")
            ).xml()
        );
        final int methods = 6;
        MatcherAssert.assertThat(
            "N variable 'N' is not calculated correctly",
            xml.xpath(
                "//class[@id='TwoCommonMethods']/vars/var[@id='N']/text()"
            ).get(0),
            new IsEqual<>(
                String.valueOf(methods)
            )
        );
        MatcherAssert.assertThat(
            "variable 'R' is not calculated correctly",
            xml.xpath(
                "//class[@id='TwoCommonMethods']/vars/var[@id='R']/text()"
            ).get(0),
            new IsEqual<>(
                String.valueOf(
                    new ListOf<>(
                        "methodTwo   -> methodOne",
                        "methodThree -> methodOne",
                        "methodFive  -> methodFour",
                        "methodSix   -> methodFour"
                    ).size()
                )
            )
        );
        MatcherAssert.assertThat(
            "variable 'RN' is not calculated correctly",
            xml.xpath(
                "//class[@id='TwoCommonMethods']/vars/var[@id='RN']/text()"
            ).get(0),
            new IsEqual<>(
                String.valueOf(
                    methods * (methods - 1) / 2
                )
            )
        );
    }
}
