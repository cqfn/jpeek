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
import com.jcabi.xml.ClasspathSources;
import com.jcabi.xml.XML;
import com.jcabi.xml.XMLDocument;
import com.jcabi.xml.XSLDocument;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.cactoos.text.TextOf;
import org.hamcrest.collection.IsEmptyIterable;
import org.hamcrest.core.IsNot;
import org.junit.jupiter.api.Test;
import org.llorllale.cactoos.matchers.Assertion;
import org.llorllale.cactoos.matchers.IsTrue;

/**
 * Test case for {@link App}.
 * @author Yegor Bugayenko (yegor256@gmail.com)
 * @version $Id$
 * @since 0.1
 * @checkstyle JavadocMethodCheck (500 lines)
 * @checkstyle JavadocTagsCheck (500 lines)
 * @checkstyle ClassDataAbstractionCouplingCheck (500 lines)
 */
@SuppressWarnings("PMD.AvoidDuplicateLiterals")
public final class AppTest {
    @Test
    public void createsXmlReports() throws IOException {
        final Path output = Files.createTempDirectory("").resolve("x1");
        final Path input = Paths.get(".");
        new App(input, output).analyze();
        new Assertion<>(
            "Must LCOM.xml file exists",
            Files.exists(output.resolve("LCOM.xml")),
            new IsTrue()
        ).affirm();
        new Assertion<>(
            "Must create LCOM report",
            XSLDocument
                .make(
                    AppTest.class.getResourceAsStream("xsl/metric.xsl")
                )
                .with(new ClasspathSources())
                .applyTo(new XMLDocument(output.resolve("LCOM.xml").toFile())),
            XhtmlMatchers.hasXPath("//xhtml:body")
        ).affirm();
    }

    @Test
    public void canIncludePrivateMethods() throws IOException {
        final Path output = Files.createTempDirectory("").resolve("x2");
        final Path input = Paths.get(".");
        final Map<String, Object> args = new HashMap<>();
        args.put("include-private-methods", 1);
        new App(input, output, args).analyze();
        new Assertion<>(
            "Must contain private method",
            XhtmlMatchers.xhtml(
                new TextOf(output.resolve("skeleton.xml")).asString()
            ),
            XhtmlMatchers.hasXPaths(
                "//method[@visibility='private']"
            )
        ).affirm();
    }

    @Test
    public void createsIndexHtml() throws IOException {
        final Path output = Files.createTempDirectory("").resolve("x2");
        final Path input = Paths.get(".");
        new App(input, output).analyze();
        new Assertion<>(
            "Must index.html file exists",
            Files.exists(output.resolve("index.html")),
            new IsTrue()
        ).affirm();
    }

    @Test
    public void createsIndexXml() throws IOException {
        final Path output = Files.createTempDirectory("").resolve("x7");
        final Path input = Paths.get(".");
        new App(input, output).analyze();
        new Assertion<>(
            "Must have some metrics",
            XhtmlMatchers.xhtml(
                new TextOf(output.resolve("index.xml")).asString()
            ),
            XhtmlMatchers.hasXPaths(
                "/index[@score!='0.0000']",
                "/index[@score!='NaN']",
                "/index[@diff!='NaN']",
                "/index[count(metric)>0]"
            )
        ).affirm();
    }

    @Test
    @SuppressWarnings("PMD.AvoidInstantiatingObjectsInLoops")
    public void isXsdDocumented() throws IOException {
        final List<XML> elements = new XMLDocument(
            AppTest.class.getResourceAsStream("xsd/metric.xsd")
        ).nodes("//node()[@name]");
        final IsNot<? super List<?>> populated = new IsNot<>(
            new IsEmptyIterable<>()
        );
        new Assertion<>(
            "Nodes must not be empty",
            elements,
            populated
        ).affirm();
        for (final XML element : elements) {
            new Assertion<>(
                String.format(
                    "element '%s' must have a documentation",
                    element.xpath("@name").get(0)
                ),
                element.xpath("xs:annotation/xs:documentation/text()"),
                populated
            ).affirm();
        }
    }
}
