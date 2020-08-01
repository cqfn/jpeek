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
package org.jpeek;

import com.jcabi.matchers.XhtmlMatchers;
import com.jcabi.xml.XMLDocument;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.cactoos.text.TextOf;
import org.jpeek.calculus.xsl.XslCalculus;
import org.jpeek.skeleton.Skeleton;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.llorllale.cactoos.matchers.Assertion;
import org.llorllale.cactoos.matchers.IsTrue;
import org.xembly.Directives;
import org.xembly.Xembler;

/**
 * Test case for {@link XslReport}.
 * @since 0.4
 * @checkstyle JavadocMethodCheck (500 lines)
 * @checkstyle ClassDataAbstractionCouplingCheck (500 lines)
 */
@SuppressWarnings("PMD.AvoidDuplicateLiterals")
public final class XslReportTest {

    @Test
    public void createsXmlReport(@TempDir final Path output) throws IOException {
        new XslReport(
            new Skeleton(new FakeBase()).xml(),
            new XslCalculus("LCOM"),
            new ReportData("LCOM")
        ).save(output);
        new Assertion<>(
            "Must LCOM.xml file exists",
            Files.exists(output.resolve("LCOM.xml")),
            new IsTrue()
        ).affirm();
        new Assertion<>(
            "Must LCOM.html file exists",
            Files.exists(output.resolve("LCOM.html")),
            new IsTrue()
        ).affirm();
    }

    @Test
    public void createsXmlReportWithXpaths(@TempDir final Path output) throws IOException {
        new XslReport(
            new Skeleton(
                new FakeBase(
                    "NoMethods", "Bar", "OverloadMethods",
                    "OnlyOneMethodWithParams", "WithoutAttributes"
                )
            ).xml(), new XslCalculus("LCOM"), new ReportData("LCOM")
        ).save(output);
        new Assertion<>(
            "Must create LCOM report",
            XhtmlMatchers.xhtml(
                new TextOf(output.resolve("LCOM.xml")).asString()
            ),
            XhtmlMatchers.hasXPaths(
                "/metric/statistics/mean",
                "/metric/bars/bar[@x='0' and .='0' and @color='yellow']"
            )
        ).affirm();
    }

    @Test
    public void createsXmlReportWithEmptyProject(@TempDir final Path output) throws IOException {
        new XslReport(
            new Skeleton(new FakeBase()).xml(),
            new XslCalculus("LCOM"),
            new ReportData("LCOM")
        ).save(output);
        new Assertion<>(
            "Report for empty project created",
            XhtmlMatchers.xhtml(
                new TextOf(output.resolve("LCOM.xml")).asString()
            ),
            XhtmlMatchers.hasXPaths(
                "/metric[title='LCOM']/bars/bar"
            )
        ).affirm();
    }

    @Test
    public void createsFullXmlReport(@TempDir final Path output) throws IOException {
        new XslReport(
            new XMLDocument(
                new Xembler(
                    new Directives()
                        .add("skeleton")
                        .append(new Header())
                        .add("app").attr("id", ".")
                        .add("package").attr("id", ".")
                        .add("class").attr("id", "A").attr("value", "0.1").up()
                        .add("class").attr("id", "B").attr("value", "0.5").up()
                        .add("class").attr("id", "C").attr("value", "0.6").up()
                        .add("class").attr("id", "D").attr("value", "0.7").up()
                        .add("class").attr("id", "E").attr("value", "NaN").up()
                ).xmlQuietly()
            ), new XslCalculus("LCOM"), new ReportData("LCOM")
        ).save(output);
        new Assertion<>(
            "Must create full report",
            XhtmlMatchers.xhtml(
                new TextOf(output.resolve("LCOM.xml")).asString()
            ),
            XhtmlMatchers.hasXPaths(
                "/metric[min='0.5' and max='0.6']",
                "//class[@id='B' and @element='true']",
                "//class[@id='D' and @element='false']",
                "//class[@id='E' and @element='false']",
                "//statistics[total='5']",
                "//statistics[elements='2']",
                "//statistics[mean='0.55']"
            )
        ).affirm();
    }

    @Test
    public void setsCorrectSchemaLocation(@TempDir final Path output) throws IOException {
        new XslReport(
            new Skeleton(new FakeBase()).xml(),
            new XslCalculus("LCOM"),
            new ReportData("LCOM")
        ).save(output);
        new Assertion<>(
            "Must have correct schema location",
            XhtmlMatchers.xhtml(
                new TextOf(output.resolve("LCOM.xml")).asString()
            ),
            XhtmlMatchers.hasXPaths(
                // @checkstyle LineLength (1 line)
                "/metric[@xsi:noNamespaceSchemaLocation = 'xsd/metric.xsd']"
            )
        ).affirm();
    }
}
