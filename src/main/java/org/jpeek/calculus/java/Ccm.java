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

import com.jcabi.xml.XML;
import com.jcabi.xml.XSL;
import com.jcabi.xml.XSLChain;
import com.jcabi.xml.XSLDocument;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.cactoos.io.ResourceOf;
import org.cactoos.io.UncheckedInput;
import org.cactoos.text.FormattedText;
import org.jpeek.XslReport;
import org.jpeek.calculus.Calculus;
import org.jpeek.graph.Disjoint;
import org.jpeek.graph.XmlGraph;

/**
 * CCM metric Java calculus.
 * This class implements the Calculus interface to provide functionality
 * for computing the CCM metric for Java code.
 * @since 0.30.25
 */
public final class Ccm implements Calculus {
    @Override
    public XML node(
        final String metric,
        final Map<String, Object> params,
        final XML skeleton
    ) {
        if (!"ccm".equalsIgnoreCase(metric)) {
            throw new IllegalArgumentException(
                new FormattedText(
                    "This metric is CCM, not %s.", metric
                ).toString()
            );
        }
        List<XSL> layers = new ArrayList<>(0);
        if (!params.containsKey("include-static-methods")) {
            layers = addXslFilter(layers, "no-static-methods.xsl");
        }
        if (!params.containsKey("include-ctors")) {
            layers = addXslFilter(layers, "no-ctors.xsl");
        }
        if (!params.containsKey("include-private-methods")) {
            layers = addXslFilter(layers, "no-private-methods.xsl");
        }
        final XSLChain chain = new XSLChain(layers);
        final XML meta = new XSLDocument(
            XslReport.class.getResourceAsStream(
                "xsl/meta/meta-creater.xsl"
            )
        ).transform(chain.transform(skeleton));
        final XML modified = findNcc(skeleton, meta);
        return new XSLDocument(
            new UncheckedInput(
                new ResourceOf("org/jpeek/metrics/CCM.xsl")
            ).stream()
        ).transform(modified);
    }

    /**
     * Find NCC.
     *
     * @param skeleton The XML skeleton to operate on
     * @param meta The XML containing metadata
     * @return The modified XML
     */
    private static XML findNcc(final XML skeleton, final XML meta) {
        XML result = meta;
        final XSL ncc = new XSLDocument(
            XslReport.class.getResourceAsStream(
                "xsl/meta/meta-ncc.xsl"
            )
        );
        for (final XML clazz : meta.nodes("//class")) {
            final XML pack = clazz.nodes("..").get(0);
            final XmlGraph graph = new XmlGraph(
                skeleton,
                pack.xpath("@id").get(0),
                clazz.xpath("@id").get(0)
            );
            final String size = String.valueOf(new Disjoint(graph).value().size());
            result = ncc
                .with("package", pack.xpath("@id").get(0))
                .with("class", clazz.xpath("@id").get(0))
                .with("value", size)
                .transform(result);
        }
        return new XSLDocument(
            XslReport.class.getResourceAsStream(
                "xsl/meta/skeleton-appender.xsl"
            )
        ).with("meta", result.node()).transform(skeleton);
    }

    /**
     * Adds an XSL filter to the list of layers.
     *
     * @param layers The list of XSL filters
     * @param name The name of the XSL file to add
     * @return The updated list of XSL filters
     */
    private static List<XSL> addXslFilter(final List<XSL> layers, final String name) {
        final XSLDocument doc = new XSLDocument(
            XslReport.class.getResourceAsStream(String.format("xsl/layers/%s", name))
        );
        layers.add(doc);
        return layers;
    }
}
