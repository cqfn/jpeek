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
import org.cactoos.text.FormattedText;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.IsEqual;
import org.hamcrest.number.IsCloseTo;
import org.jpeek.FakeBase;
import org.jpeek.skeleton.Skeleton;

/**
 * Metric test helper.
 * @author Ilya Kharlamov (ilya.kharlamov@gmail.com)
 * @version $Id$
 * @since 0.28
 */
public final class MetricBase {
    /**
     * XSL document.
     */
    private final XSLDocument xsl;

    /**
     * Ctor.
     * @param path Path to the xsl
     * @throws Exception If file not found.
     */
    public MetricBase(final String path) throws Exception {
        this.xsl = new XSLDocument(
            new ResourceOf(
                path
            ).stream()
        );
    }

    /**
     * Transform a class to assertable xml.
     * @param name File name (without an extension) of a class to transform
     * @return Xml result of the transformation
     */
    public Report transform(final String name) {
        return new Report(
            name,
            this.xsl.transform(
                new Skeleton(
                    new FakeBase(name)
                ).xml()
            )
        );
    }

    /**
     * Assertion helper for xml.
     */
    public static final class Report {
        /**
         * Copy of the transformation xml.
         */
        private final XML xml;

        /**
         * Class name.
         */
        private final String name;

        /**
         * Ctor.
         * @param name Class name.
         * @param xml Resulting xml.
         */
        public Report(final String name, final XML xml) {
            this.name = name;
            this.xml = xml;
        }

        /**
         * Asserts the variable produced.
         * @param variable Variable name
         * @param expected Expected value
         * @throws Exception String format exception
         */
        public void assertVariable(final String variable,
            final int expected) throws Exception {
            MatcherAssert.assertThat(
                new FormattedText(
                    "Variable '%s' is not calculated correctly for class '%s'",
                    variable,
                    this.name
                ).asString(),
                this.xml.xpath(
                    String.format(
                        "//class[@id='%s']/vars/var[@id='%s']/text()",
                        this.name,
                        variable
                    )
                ).get(0),
                new IsEqual<>(
                    String.valueOf(
                        expected
                    )
                )
            );
        }

        /**
         * Asserts the main metric value.
         * @param value Expected value of the metric
         * @throws Exception String format exception
         */
        public void assertValue(final double value) throws Exception {
            MatcherAssert.assertThat(
                "The metric value is not calculated properly",
                Double.parseDouble(
                    this.xml.xpath(
                        new FormattedText(
                            "//class[@id='%s']/@value",
                            this.name
                        ).asString()
                    ).get(0)
                ),
                new IsCloseTo(
                    value,
                    // @checkstyle MagicNumberCheck (1 line)
                    0.001d
                )
            );
        }
    }
}
