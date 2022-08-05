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

import com.jcabi.log.Logger;
import com.jcabi.xml.ClasspathSources;
import com.jcabi.xml.StrictXML;
import com.jcabi.xml.XML;
import com.jcabi.xml.XMLDocument;
import com.jcabi.xml.XSD;
import com.jcabi.xml.XSDDocument;
import com.jcabi.xml.XSL;
import com.jcabi.xml.XSLChain;
import com.jcabi.xml.XSLDocument;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;
import org.cactoos.io.TeeInput;
import org.cactoos.list.ListOf;
import org.cactoos.scalar.LengthOf;
import org.cactoos.scalar.Unchecked;
import org.jpeek.calculus.Calculus;
import org.xembly.Directives;
import org.xembly.Xembler;

/**
 * Single report.
 *
 * <p>There is no thread-safety guarantee.
 *
 * @since 0.1
 * @checkstyle ClassDataAbstractionCouplingCheck (500 lines)
 */
final class XslReport implements Report {

    /**
     * Location to the schema file.
     */
    private static final String SCHEMA_FILE = "xsd/metric.xsd";

    /**
     * XSD schema.
     */
    private static final XSD SCHEMA = XSDDocument.make(
        XslReport.class.getResourceAsStream(XslReport.SCHEMA_FILE)
    );

    /**
     * XSL stylesheet.
     */
    private static final XSL STYLESHEET = XSLDocument.make(
        XslReport.class.getResourceAsStream("xsl/metric.xsl")
    ).with(new ClasspathSources());

    /**
     * XSL params.
     */
    private final Map<String, Object> params;

    /**
     * The skeleton.
     */
    private final XML skeleton;

    /**
     * The metric.
     */
    private final String metric;

    /**
     * Calculus.
     */
    private final Calculus calculus;

    /**
     * Post processing XSLs.
     */
    private final XSL post;

    /**
     * Ctor.
     * @param xml Skeleton
     * @param calc Calculus
     * @param data Report data
     * @checkstyle ParameterNumberCheck (10 lines)
     */
    XslReport(final XML xml, final Calculus calc, final ReportData data) {
        this.skeleton = xml;
        this.metric = data.metric();
        this.params = data.params();
        this.calculus = calc;
        this.post = new XSLChain(
            new ListOf<>(
                new XSLDocument(
                    XslReport.class.getResourceAsStream(
                        "xsl/metric-post-colors.xsl"
                    )
                ).with("low", data.mean() - data.sigma())
                .with("high", data.mean() + data.sigma()),
                new XSLDocument(
                    XslReport.class.getResourceAsStream(
                        "xsl/metric-post-range.xsl"
                    )
                ),
                new XSLDocument(
                    XslReport.class.getResourceAsStream(
                        "xsl/metric-post-bars.xsl"
                    )
                )
            )
        );
    }

    /**
     * Save report.
     * @param target Target dir
     * @return TRUE if saved
     * @throws IOException If fails
     */
    @SuppressWarnings("PMD.GuardLogStatement")
    public boolean save(final Path target) throws IOException {
        final long start = System.currentTimeMillis();
        final XML xml = new StrictXML(
            new ReportWithStatistics(
                this.post.transform(this.xml())
            ),
            XslReport.SCHEMA
        );
        new Unchecked<>(
            new LengthOf(
                new TeeInput(
                    xml.toString(),
                    target.resolve(
                        String.format("%s.xml", this.metric)
                    )
                )
            )
        ).value();
        new Unchecked<>(
            new LengthOf(
                new TeeInput(
                    XslReport.STYLESHEET.transform(xml).toString(),
                    target.resolve(
                        String.format("%s.html", this.metric)
                    )
                )
            )
        ).value();
        Logger.debug(
            this, "%s.xml generated in %[ms]s",
            this.metric, System.currentTimeMillis() - start
        );
        return true;
    }

    /**
     * Make XML.
     * @return XML
     * @throws IOException If fails
     * @todo #227:30min Add a test to check whether passing params to
     *  XSLDocument really works. Currently only C3 metric template
     *  is known to use parameter named 'ctors'. However C3.xsl is a
     *  work in progress and has impediments, see #175. In case the
     *  parameter becomes obsolete, consider simplifying construction
     *  of XSLDocument without params (see reviews to #326).
     */
    private XML xml() throws IOException {
        return new XMLDocument(
            new Xembler(
                new Directives()
                    .xpath("/metric")
                    .attr(
                        "xmlns:xsi",
                        "http://www.w3.org/2001/XMLSchema-instance"
                    )
                    .attr(
                        "xsi:noNamespaceSchemaLocation",
                        XslReport.SCHEMA_FILE
                    )
            ).applyQuietly(
                this.calculus.node(
                    this.metric, this.params, this.skeleton
                ).node()
            )
        );
    }

}
