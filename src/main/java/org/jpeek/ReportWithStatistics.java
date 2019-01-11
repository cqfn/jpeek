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
package org.jpeek;

import com.jcabi.xml.XML;
import com.jcabi.xml.XMLDocument;
import java.util.Collection;
import java.util.List;
import javax.xml.namespace.NamespaceContext;
import org.cactoos.collection.Mapped;
import org.cactoos.scalar.SolidScalar;
import org.cactoos.scalar.UncheckedScalar;
import org.w3c.dom.Node;
import org.xembly.Directives;
import org.xembly.Xembler;

/**
 * Statistics.
 *
 * <p>There is no thread-safety guarantee.
 *
 * @author Yegor Bugayenko (yegor256@gmail.com)
 * @version $Id$
 * @since 0.16
 * @checkstyle ClassDataAbstractionCouplingCheck (500 lines)
 */
final class ReportWithStatistics implements XML {

    /**
     * The XML with statistics.
     */
    private final UncheckedScalar<XML> output;

    /**
     * Ctor.
     * @param xml The XML
     */
    ReportWithStatistics(final XML xml) {
        this.output = new UncheckedScalar<>(
            new SolidScalar<XML>(
                () -> {
                    final Collection<Double> values = new Mapped<>(
                        Double::parseDouble,
                        xml.xpath(
                            // @checkstyle LineLength (1 line)
                            "//class[@value<=/metric/max and @value>=/metric/min]/@value"
                        )
                    );
                    final double total = (double) values.size();
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
