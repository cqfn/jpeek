/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2017-2018 Yegor Bugayenko
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
import com.jcabi.xml.Sources;
import com.jcabi.xml.StrictXML;
import com.jcabi.xml.XML;
import com.jcabi.xml.XSD;
import com.jcabi.xml.XSDDocument;
import com.jcabi.xml.XSL;
import com.jcabi.xml.XSLChain;
import com.jcabi.xml.XSLDocument;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import org.cactoos.collection.CollectionOf;
import org.cactoos.io.InputOf;
import org.cactoos.io.LengthOf;
import org.cactoos.io.TeeInput;
import org.cactoos.map.MapEntry;
import org.cactoos.map.MapOf;
import org.cactoos.text.TextOf;
import org.cactoos.text.UncheckedText;

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
     * Default mean.
     */
    private static final double DEFAULT_MEAN = 0.5d;
    /**
     * Default sigma.
     */
    private static final double DEFAULT_SIGMA = 0.1d;

    /**
     * Location to the schema file.
     */
    private static final String SCHEMA_FILE = "xsd/metric.xsd";

    /**
     * XSD schema.
     */
    private static final XSD SCHEMA = XSDDocument.make(
        Report.class.getResourceAsStream(Report.SCHEMA_FILE)
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
    private final XSL post;

    /**
     * XSL params.
     */
    private final Map<String, Object> params;

    /**
     * Ctor.
     * @param xml Skeleton
     * @param name Name of the metric
     */
    Report(final XML xml, final String name) {
        this(
            xml, name, new HashMap<>(0),
            Report.DEFAULT_MEAN, Report.DEFAULT_SIGMA
        );
    }

    /**
     * Ctor.
     * @param xml Skeleton
     * @param name Name of metric
     * @param args Params for XSL
     */
    Report(final XML xml, final String name, final Map<String, Object> args) {
        this(
            xml, name, args,
            Report.DEFAULT_MEAN, Report.DEFAULT_SIGMA
        );
    }

    /**
     * Ctor.
     * @param xml Skeleton
     * @param name Name of the metric
     * @param args Params for XSL
     * @param mean Mean
     * @param sigma Sigma
     * @checkstyle ParameterNumberCheck (10 lines)
     * @todo #135:30min The current solution to add the reference to
     *  'metric.xsd' in the generated 'metric.xml' is too complex for
     *  the task at hand. Refactor towards a simpler solution that
     *  ideally would just require one or two Xembly instructions to
     *  add the required attribute.
     */
    @SuppressWarnings("unchecked")
    Report(final XML xml, final String name,
        final Map<String, Object> args,
        final double mean, final double sigma) {
        this.skeleton = xml;
        this.metric = name;
        this.params = new MapOf<>(
            args,
            new MapEntry<>("schemaLocation", Report.SCHEMA_FILE)
        );
        this.post = new XSLChain(
            new CollectionOf<>(
                new XSLDocument(
                    new UncheckedText(
                        new TextOf(
                            new InputOf(
                                Report.class.getResourceAsStream(
                                    "xsl/metric-post-schemaloc.xsl"
                                )
                            )
                        )
                    ).asString(),
                    Sources.DUMMY,
                    this.params
                ),
                new XSLDocument(
                    Report.class.getResourceAsStream(
                        "xsl/metric-post-colors.xsl"
                    )
                ).with("low", mean - sigma).with("high", mean + sigma),
                new XSLDocument(
                    Report.class.getResourceAsStream(
                        "xsl/metric-post-range.xsl"
                    )
                ),
                new XSLDocument(
                    Report.class.getResourceAsStream(
                        "xsl/metric-post-bars.xsl"
                    )
                )
            )
        );
    }

    /**
     * Save report.
     * @param target Target dir
     * @throws IOException If fails
     */
    public void save(final Path target) throws IOException {
        final long start = System.currentTimeMillis();
        final XML xml = new StrictXML(
            new ReportWithStatistics(
                this.post.transform(this.xml())
            ),
            Report.SCHEMA
        );
        new LengthOf(
            new TeeInput(
                xml.toString(),
                target.resolve(
                    String.format("%s.xml", this.metric)
                )
            )
        ).intValue();
        new LengthOf(
            new TeeInput(
                Report.STYLESHEET.transform(xml).toString(),
                target.resolve(
                    String.format("%s.html", this.metric)
                )
            )
        ).intValue();
        Logger.info(
            this, "%s.xml generated in %[ms]s",
            this.metric, System.currentTimeMillis() - start
        );
    }

    /**
     * Make XML.
     * @return XML
     * @throws IOException If fails
     */
    private XML xml() throws IOException {
        final String name = String.format("metrics/%s.xsl", this.metric);
        final URL res = this.getClass().getResource(name);
        if (res == null) {
            throw new IllegalArgumentException(
                String.format("XSL not found: %s", name)
            );
        }
        return new XSLDocument(
            new TextOf(res).asString(),
            Sources.DUMMY,
            this.params
        ).transform(this.skeleton);
    }

}
