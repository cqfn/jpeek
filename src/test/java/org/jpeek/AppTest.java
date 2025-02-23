/*
 * The MIT License (MIT)
 *
 * SPDX-FileCopyrightText: Copyright (c) 2017-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
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
import org.junit.jupiter.api.io.TempDir;
import org.llorllale.cactoos.matchers.Assertion;
import org.llorllale.cactoos.matchers.IsTrue;

/**
 * Test case for {@link App}.
 * @since 0.1
 * @checkstyle JavadocMethodCheck (500 lines)
 */
final class AppTest {
    @Test
    void createsXmlReports(@TempDir final Path output) throws IOException {
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
    void canIncludePrivateMethods(@TempDir final Path output) throws Exception {
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
    void canIncludeConstructors(@TempDir final Path output) throws Exception {
        final Path input = Paths.get(".");
        final Map<String, Object> args = new HashMap<>();
        args.put("include-ctors", 1);
        new App(input, output, args).analyze();
        new Assertion<>(
            "Must contain constructor",
            XhtmlMatchers.xhtml(
                new TextOf(output.resolve("skeleton.xml")).asString()
            ),
            XhtmlMatchers.hasXPaths(
                "//method[@ctor='true']"
            )
        ).affirm();
    }

    @Test
    void canIncludeStaticMethods(@TempDir final Path output) throws Exception {
        final Path input = Paths.get(".");
        final Map<String, Object> args = new HashMap<>();
        args.put("include-static-methods", 1);
        new App(input, output, args).analyze();
        new Assertion<>(
            "Must contain static method",
            XhtmlMatchers.xhtml(
                new TextOf(output.resolve("skeleton.xml")).asString()
            ),
            XhtmlMatchers.hasXPaths(
                "//method[@static='true']"
            )
        ).affirm();
    }

    @Test
    void createsIndexHtml(@TempDir final Path output) throws IOException {
        final Path input = Paths.get(".");
        new App(input, output).analyze();
        new Assertion<>(
            "Must index.html file exists",
            Files.exists(output.resolve("index.html")),
            new IsTrue()
        ).affirm();
    }

    @Test
    void createsIndexXml(@TempDir final Path output) throws Exception {
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
    void isXsdDocumented() throws IOException {
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
