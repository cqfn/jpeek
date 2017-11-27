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
import com.jcabi.xml.StrictXML;
import com.jcabi.xml.XML;
import com.jcabi.xml.XMLDocument;
import com.jcabi.xml.XSD;
import com.jcabi.xml.XSDDocument;
import com.jcabi.xml.XSL;
import com.jcabi.xml.XSLDocument;
import java.io.IOException;
import java.nio.file.Path;
import org.cactoos.io.LengthOf;
import org.cactoos.io.TeeInput;
import org.cactoos.iterable.IterableOf;
import org.cactoos.scalar.IoCheckedScalar;
import org.cactoos.scalar.Reduced;
import org.xembly.Directives;
import org.xembly.Xembler;

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
        Report.class.getResourceAsStream("jpeek.xsd")
    );

    /**
     * XSL stylesheet.
     */
    private static final XSL STYLESHEET = XSLDocument.make(
        Report.class.getResourceAsStream("jpeek.xsl")
    ).with(new ClasspathSources());

    /**
     * The metric.
     */
    private final Metric metric;

    /**
     * Post processing XSLs.
     */
    private final Iterable<XSL> post;

    /**
     * Ctor.
     * @param mtc Metric
     */
    Report(final Metric mtc) {
        // @checkstyle MagicNumber (1 line)
        this(mtc, 0.5d, 0.25d);
    }

    /**
     * Ctor.
     * @param mtc Metric
     * @param mean Mean
     * @param sigma Sigma
     */
    Report(final Metric mtc, final double mean, final double sigma) {
        this.metric = mtc;
        this.post = new IterableOf<>(
            new XSLDocument(
                Report.class.getResourceAsStream("jpeek-post-colors.xsl")
            ).with("low", mean - sigma).with("high", mean + sigma),
            new XSLDocument(
                Report.class.getResourceAsStream("jpeek-post-bars.xsl")
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
            new IoCheckedScalar<>(
                new Statistics(
                    new IoCheckedScalar<>(
                        new Reduced<>(
                            this.xml(),
                            (doc, xsl) -> xsl.transform(doc),
                            this.post
                        )
                    ).value()
                )
            ).value(),
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
        ).value();
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
        ).value();
    }

    /**
     * Make XML.
     * @return XML
     * @throws IOException If fails
     */
    private XML xml() throws IOException {
        return new XMLDocument(
            new Xembler(
                new Directives()
                    .pi("xml-stylesheet", "href='jpeek.xsl' type='text/xsl'")
                    .append(this.metric.xembly())
                    .xpath("/metric")
                    .append(new Header())
                    .add("title")
                    .set(this.metric.getClass().getSimpleName())
                    .up()
            ).xmlQuietly()
        );
    }

}
