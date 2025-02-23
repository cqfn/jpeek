/*
 * The MIT License (MIT)
 *
 * SPDX-FileCopyrightText: Copyright (c) 2017-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.jpeek;

import com.jcabi.xml.XML;
import com.jcabi.xml.XMLDocument;
import java.util.List;
import javax.xml.namespace.NamespaceContext;
import org.cactoos.iterable.Mapped;
import org.cactoos.list.ListOf;
import org.cactoos.scalar.Solid;
import org.cactoos.scalar.Unchecked;
import org.w3c.dom.Node;
import org.xembly.Directives;
import org.xembly.Xembler;

/**
 * Statistics.
 *
 * <p>There is no thread-safety guarantee.
 *
 * @since 0.16
 */
final class ReportWithStatistics implements XML {

    /**
     * The XML with statistics.
     */
    private final Unchecked<XML> output;

    /**
     * Ctor.
     * @param xml The XML
     */
    ReportWithStatistics(final XML xml) {
        this.output = new Unchecked<>(
            new Solid<>(
                () -> {
                    final Iterable<Double> values = new Mapped<>(
                        Double::parseDouble,
                        xml.xpath(
                            // @checkstyle LineLength (1 line)
                            "//class[@value<=/metric/max and @value>=/metric/min]/@value"
                        )
                    );
                    final double total = (double) new ListOf<>(values).size();
                    double sum = 0.0d;
                    for (final Double value : values) {
                        sum += value;
                    }
                    final double mean = sum / total;
                    double squares = 0.0d;
                    for (final Double value : values) {
                        squares += Math.pow(value - mean, 2.0d);
                    }
                    final double variance = squares / total;
                    final double sigma = Math.sqrt(variance);
                    double defects = 0.0d;
                    for (final Double value : values) {
                        if (value < mean - sigma || value > mean + sigma) {
                            ++defects;
                        }
                    }
                    return new XMLDocument(
                        new Xembler(
                            new Directives()
                                .xpath("/metric")
                                .add("statistics")
                                .add("total").set(xml.nodes("//class").size())
                                .up()
                                .add("elements").set((long) total).up()
                                .add("mean").set(Double.toString(mean)).up()
                                .add("sigma").set(Double.toString(sigma)).up()
                                .add("variance").set(Double.toString(variance))
                                .up()
                                .add("defects")
                                .set(Double.toString(defects / total)).up()
                        ).applyQuietly(xml.node())
                    );
                }
            )
        );
    }

    @Override
    public String toString() {
        return this.output.value().toString();
    }

    @Override
    public List<String> xpath(final String query) {
        return this.output.value().xpath(query);
    }

    @Override
    public List<XML> nodes(final String query) {
        return this.output.value().nodes(query);
    }

    @Override
    public XML registerNs(final String prefix, final Object uri) {
        return this.output.value().registerNs(prefix, uri);
    }

    @Override
    public XML merge(final NamespaceContext context) {
        return this.output.value().merge(context);
    }

    @Override
    public Node node() {
        return this.output.value().node();
    }
}
