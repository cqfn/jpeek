/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2017 Yegor Bugayenko
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
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import org.cactoos.Scalar;
import org.cactoos.collection.Filtered;
import org.cactoos.collection.Joined;
import org.cactoos.iterable.Mapped;
import org.cactoos.list.Sorted;
import org.xembly.Directive;
import org.xembly.Directives;

/**
 * Index.
 *
 * <p>There is no thread-safety guarantee.
 *
 * @author Yegor Bugayenko (yegor256@gmail.com)
 * @version $Id$
 * @since 0.6
 * @checkstyle ClassDataAbstractionCouplingCheck (500 lines)
 */
final class Index implements Scalar<Iterable<Directive>> {

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
    public Iterable<Directive> value() throws IOException {
        return new Directives()
            .add("metrics")
            .append(new Header())
            .append(
                new Joined<>(
                    new Mapped<Path, Iterable<Directive>>(
                        new Filtered<Path>(
                            Files.list(this.output)
                                .collect(Collectors.toList()),
                            path -> path.getFileName()
                                .toString()
                                .matches("^[A-Z].+\\.xml$")
                        ),
                        Index::metric
                    )
                )
            );
    }

    /**
     * Metric to Xembly.
     * @param file The XML file with metric report
     * @return Xembly
     * @throws IOException If fails
     */
    private static Iterable<Directive> metric(final Path file)
        throws IOException {
        final String name = file.getFileName()
            .toString().replaceAll("\\.xml$", "");
        final XML xml = new XMLDocument(file.toFile());
        final List<Double> values = new Sorted<>(
            new org.cactoos.list.Mapped<>(
                xml.xpath("//class/@value"),
                Double::parseDouble
            )
        );
        final double green = (double) xml.nodes("//*[@color='green']").size();
        final double yellow = (double) xml.nodes("//*[@color='yellow']").size();
        final double red = (double) xml.nodes("//*[@color='red']").size();
        final double score = 10.0d
            * (green + yellow * 0.25d + red * 0.05d)
            / (green + yellow + red);
        return new Directives()
            .add("metric")
            .attr("name", name)
            .add("html").set(String.format("%s.html", name)).up()
            .add("xml").set(String.format("%s.xml", name)).up()
            .add("classes").set(values.size()).up()
            .add("average").set(Index.avg(values)).up()
            .add("min").set(values.get(0)).up()
            .add("max").set(values.get(values.size() - 1)).up()
            .add("green").set((int) green).up()
            .add("yellow").set((int) yellow).up()
            .add("red").set((int) red).up()
            .add("score").set(score).up()
            .up();
    }

    /**
     * Calculate average.
     * @param values Values
     * @return Average
     */
    private static double avg(final Collection<Double> values) {
        double sum = 0.0d;
        for (final double val : values) {
            sum += val;
        }
        double avg = 0.0d;
        if (!values.isEmpty()) {
            avg = sum / (double) values.size();
        }
        return avg;
    }
}
