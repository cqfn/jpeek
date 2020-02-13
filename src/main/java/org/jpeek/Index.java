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

import com.jcabi.xml.XML;
import com.jcabi.xml.XMLDocument;
import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.List;
import org.cactoos.collection.Filtered;
import org.cactoos.collection.Joined;
import org.cactoos.io.Directory;
import org.cactoos.iterable.Mapped;
import org.cactoos.list.Sorted;
import org.xembly.Directive;
import org.xembly.Directives;

/**
 * Index.
 *
 * <p>There is no thread-safety guarantee.
 *
 * @since 0.6
 * @checkstyle ClassDataAbstractionCouplingCheck (500 lines)
 * @checkstyle JavadocTagsCheck (500 lines)
 */
final class Index implements Iterable<Directive> {

    /**
     * Directory to save index to.
     */
    private final Path output;

    /**
     * Ctor.
     * @param target Target dir
     */
    Index(final Path target) {
        this.output = target;
    }

    @Override
    public Iterator<Directive> iterator() {
        return new Directives()
            .add("index")
            .attr("artifact", "unknown")
            .append(new Header())
            .append(
                () -> new Directives()
                    .attr(
                        "xmlns:xsi",
                        "http://www.w3.org/2001/XMLSchema-instance"
                        )
                    .attr(
                    "xsi:noNamespaceSchemaLocation",
                    "xsd/index.xsd"
                    )
                    .iterator()
            )
            .append(
                new Joined<>(
                    new Mapped<>(
                        Index::metric,
                        new Filtered<>(
                            path -> path.getFileName().toString().matches(
                                "^[A-Z].+\\.xml$"
                            ),
                            new Directory(this.output)
                        )
                    )
                )
            )
            .iterator();
    }

    /**
     * Metric to Xembly.
     * @param file The XML file with metric report
     * @return Xembly
     * @throws FileNotFoundException If fails
     */
    private static Iterable<Directive> metric(final Path file)
        throws FileNotFoundException {
        final String name = file.getFileName()
            .toString().replaceAll("\\.xml$", "");
        final XML xml = new XMLDocument(file.toFile());
        final List<Double> values = new Sorted<>(
            new org.cactoos.list.Mapped<>(
                Double::parseDouble,
                xml.xpath("//class[@element='true' and @value!='NaN']/@value")
            )
        );
        final double green = (double) xml.nodes(
            "//*[@element='true' and @color='green']"
        ).size();
        final double yellow = (double) xml.nodes(
            "//*[@element='true' and @color='yellow']"
        ).size();
        final double red = (double) xml.nodes(
            "//*[@element='true' and @color='red']"
        ).size();
        double all = green + yellow + red;
        if (all == 0.0d) {
            all = 1.0d;
        }
        final double score = 10.0d
            * (green + yellow * 0.25d + red * 0.05d) / all;
        final Directives dirs = new Directives()
            .add("metric")
            .attr("name", name)
            .add("html").set(String.format("%s.html", name)).up()
            .add("xml").set(String.format("%s.xml", name)).up()
            .add("elements").set(values.size()).up()
            .add("classes").set(xml.nodes("//class").size()).up()
            .add("green").set((int) green).up()
            .add("yellow").set((int) yellow).up()
            .add("red").set((int) red).up()
            .add("score").set(score).up()
            .add("reverse")
            .set(
                Boolean.toString(
                    Double.parseDouble(xml.xpath("/metric/colors/@high").get(0))
                    > Double.parseDouble(
                        xml.xpath("/metric/colors/@low").get(0)
                    )
                )
            )
            .up();
        if (!values.isEmpty()) {
            dirs.add("min").set(values.get(0)).up()
                .add("max").set(values.get(values.size() - 1)).up();
        }
        final Iterator<XML> bars = xml.nodes("/metric/bars").iterator();
        if (bars.hasNext()) {
            dirs.add("bars").append(Directives.copyOf(bars.next().node())).up();
        }
        final Iterator<XML> stats = xml.nodes("/metric/statistics").iterator();
        if (stats.hasNext()) {
            final XML stat = stats.next();
            dirs.add("defects").set(stat.xpath("defects/text()").get(0)).up()
                .add("sigma").set(stat.xpath("sigma/text()").get(0)).up()
                .add("mean").set(stat.xpath("mean/text()").get(0)).up();
        }
        return dirs.up();
    }

}
