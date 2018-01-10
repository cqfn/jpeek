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

import com.jcabi.xml.ClasspathSources;
import com.jcabi.xml.Sources;
import com.jcabi.xml.StrictXML;
import com.jcabi.xml.XML;
import com.jcabi.xml.XSD;
import com.jcabi.xml.XSDDocument;
import com.jcabi.xml.XSL;
import com.jcabi.xml.XSLDocument;
import java.io.IOException;
import java.nio.file.Path;
import org.cactoos.io.LengthOf;
import org.cactoos.io.TeeInput;
import org.cactoos.iterable.IterableOf;
import org.cactoos.map.MapEntry;
import org.cactoos.map.MapOf;
import org.cactoos.scalar.IoCheckedScalar;
import org.cactoos.scalar.Reduced;
import org.cactoos.text.TextOf;
import org.cactoos.time.DateAsText;

/**
 * Single report.
 *
 * <p>There is no thread-safety guarantee.
 *
 * @author Yegor Bugayenko (yegor256@gmail.com)
 * @version $Id$
 * @since 0.1
 * @checkstyle ClassDataAbstractionCouplingCheck (500 lines)
 */
final class Report {

    /**
     * XSD schema.
     */
    private static final XSD SCHEMA = XSDDocument.make(
        Report.class.getResourceAsStream("xsd/metric.xsd")
    );

    /**
     * XSL stylesheet.
     */
    private static final XSL STYLESHEET = XSLDocument.make(
        Report.class.getResourceAsStream("xsl/metric.xsl")
    ).with(new ClasspathSources());

    /**
     * The skeleton.
     */
    private final XML skeleton;

    /**
     * The metric.
     */
    private final String metric;

    /**
     * Post processing XSLs.
     */
    private final Iterable<XSL> post;

    /**
     * Ctor.
     * @param xml Skeleton
     * @param name Name of the metric
     */
    Report(final XML xml, final String name) {
        this(xml, name, 0.5d, 0.1d);
    }

    /**
     * Ctor.
     * @param xml Skeleton
     * @param name Name of the metric
     * @param mean Mean
     * @param sigma Sigma
     */
    Report(final XML xml, final String name,
        final double mean, final double sigma) {
        this.skeleton = xml;
        this.metric = name;
        this.post = new IterableOf<>(
            new XSLDocument(
                Report.class.getResourceAsStream("xsl/metric-post-colors.xsl")
            ).with("low", mean - sigma).with("high", mean + sigma),
            new XSLDocument(
                Report.class.getResourceAsStream("xsl/metric-post-range.xsl")
            ),
            new XSLDocument(
                Report.class.getResourceAsStream("xsl/metric-post-bars.xsl")
            )
        );
    }

    /**
     * Save report.
     * @param target Target dir
     * @throws IOException If fails
     */
    public void save(final Path target) throws IOException {
        final XML xml = new StrictXML(
            new ReportWithStatistics(
                new IoCheckedScalar<>(
                    new Reduced<>(
                        this.xml(),
                        (doc, xsl) -> xsl.transform(doc),
                        this.post
                    )
                ).value()
            ),
            Report.SCHEMA
        );
        new LengthOf(
            new TeeInput(
                xml.toString(),
                target.resolve(
                    String.format(
                        "%s.xml",
                        this.metric.getClass().getSimpleName()
                    )
                )
            )
        ).intValue();
        new LengthOf(
            new TeeInput(
                Report.STYLESHEET.transform(xml).toString(),
                target.resolve(
                    String.format(
                        "%s.html",
                        this.metric.getClass().getSimpleName()
                    )
                )
            )
        ).intValue();
    }

    /**
     * Make XML.
     * @return XML
     * @throws IOException If fails
     */
    private XML xml() throws IOException {
        return new XSLDocument(
            new TextOf(
                this.getClass().getResource(
                    String.format("metrics/%s.xsl", this.metric)
                )
            ).asString(),
            Sources.DUMMY,
            new MapOf<>(
                new MapEntry<>("version", new Version().value()),
                new MapEntry<>("date", new DateAsText())
            )
        ).transform(this.skeleton);
    }

}
