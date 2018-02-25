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
import com.jcabi.xml.StrictXML;
import com.jcabi.xml.XML;
import com.jcabi.xml.XMLDocument;
import com.jcabi.xml.XSDDocument;
import com.jcabi.xml.XSL;
import com.jcabi.xml.XSLChain;
import com.jcabi.xml.XSLDocument;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Map;
import org.cactoos.collection.CollectionOf;
import org.cactoos.io.LengthOf;
import org.cactoos.io.ResourceOf;
import org.cactoos.io.TeeInput;
import org.cactoos.list.ListOf;
import org.cactoos.map.MapEntry;
import org.cactoos.map.MapOf;
import org.cactoos.scalar.And;
import org.cactoos.scalar.AndInThreads;
import org.cactoos.scalar.IoCheckedScalar;
import org.jpeek.skeleton.Skeleton;
import org.xembly.Directives;
import org.xembly.Xembler;

/**
 * Application.
 *
 * <p>There is no thread-safety guarantee.
 *
 * @author Yegor Bugayenko (yegor256@gmail.com)
 * @version $Id$
 * @since 0.1
 * @checkstyle ClassDataAbstractionCouplingCheck (500 lines)
 * @checkstyle ClassFanOutComplexityCheck (500 lines)
 * @checkstyle ExecutableStatementCountCheck (500 lines)
 * @checkstyle NPathComplexityCheck (500 lines)
 * @checkstyle MagicNumberCheck (500 lines)
 * @checkstyle CyclomaticComplexityCheck (500 lines)
 * @checkstyle MethodLengthCheck (500 lines)
 * @checkstyle JavaNCSSCheck (500 lines)
 *
 * @todo #9:30min LCC metric has impediments (see puzzles in LCC.xml).
 *  Once they are resolved, cover the metric with autotests and add it
 *  to reports list.
 *  (details on how to test the metrics are to be negotiated here - #107)
 *
 * @todo #17:30min MWE metric has impediments (see puzzles in MWE.xml).
 *  Once they are resolved, cover the metric with autotests and add it
 *  to reports list.
 *  (details on how to test the metrics are to be negotiated here - #107)
 */
@SuppressWarnings
    (
        {
            "PMD.AvoidDuplicateLiterals",
            "PMD.NPathComplexity",
            "PMD.CyclomaticComplexity",
            "PMD.StdCyclomaticComplexity",
            "PMD.ModifiedCyclomaticComplexity"
        }
    )
public final class App {

    /**
     * Location of the project to analyze.
     */
    private final Path input;

    /**
     * Directory to save reports to.
     */
    private final Path output;

    /**
     * XSL params.
     */
    private final Map<String, Object> params;

    /**
     * Ctor.
     * @param source Source directory
     * @param target Target dir
     */
    public App(final Path source, final Path target) {
        this(
            source, target,
            new MapOf<String, Object>(
                new MapEntry<>("LCOM", true),
                new MapEntry<>("LCOM2", true),
                new MapEntry<>("LCOM3", true),
                new MapEntry<>("LCOM4", true),
                new MapEntry<>("LCOM5", true),
                new MapEntry<>("SCOM", true),
                new MapEntry<>("NHD", true),
                new MapEntry<>("MMAC", true),
                new MapEntry<>("OCC", true),
                new MapEntry<>("PCC", true),
                new MapEntry<>("TCC", true)
            )
        );
    }

    /**
     * Ctor.
     * @param source Source directory
     * @param target Target dir
     * @param args XSL params
     */
    public App(final Path source, final Path target,
        final Map<String, Object> args) {
        this.input = source;
        this.output = target;
        this.params = args;
    }

    /**
     * Analyze sources.
     * @throws IOException If fails
     * @todo #66:15min Add the CCM metric report here when all the puzzles
     *  about it are resolved. It requires 'params' to be passed in in order
     *  to properly include or exclude ctors.
     */
    @SuppressWarnings("PMD.ExcessiveMethodLength")
    public void analyze() throws IOException {
        final Base base = new DefaultBase(this.input);
        final XML skeleton = new Skeleton(base).xml();
        final Collection<XSL> layers = new LinkedList<>();
        if (this.params.containsKey("include-ctors")) {
            Logger.info(this, "Constructors will be included");
        } else {
            layers.add(App.xsl("layers/no-ctors.xsl"));
            Logger.info(this, "Constructors will be ignored");
        }
        if (this.params.containsKey("include-static-methods")) {
            Logger.info(this, "Static methods will be included");
        } else {
            layers.add(App.xsl("layers/no-static-methods.xsl"));
            Logger.info(this, "Static methods will be ignored");
        }
        final XSL chain = new XSLChain(layers);
        this.save(skeleton.toString(), "skeleton.xml");
        final Collection<Report> reports = new LinkedList<>();
        if (this.params.containsKey("LCOM")) {
            reports.add(
                new Report(
                    chain.transform(skeleton),
                    "LCOM", this.params, 10.0d, -5.0d
                )
            );
        }
        if (this.params.containsKey("CAMC")) {
            reports.add(
                new Report(
                    chain.transform(skeleton),
                    "CAMC", this.params
                )
            );
        }
        if (this.params.containsKey("MMAC")) {
            reports.add(
                new Report(
                    chain.transform(skeleton),
                    "MMAC", this.params, 0.5d, 0.1d
                )
            );
        }
        if (this.params.containsKey("LCOM5")) {
            reports.add(
                new Report(
                    chain.transform(skeleton),
                    "LCOM5", this.params, 0.5d, -0.1d
                )
            );
        }
        if (this.params.containsKey("NHD")) {
            reports.add(
                new Report(
                    chain.transform(skeleton),
                    "NHD"
                )
            );
        }
        if (this.params.containsKey("LCOM2")) {
            reports.add(
                new Report(
                    chain.transform(skeleton),
                    "LCOM2", this.params
                )
            );
        }
        if (this.params.containsKey("LCOM3")) {
            reports.add(
                new Report(
                    chain.transform(skeleton),
                    "LCOM3", this.params
                )
            );
        }
        if (this.params.containsKey("SCOM")) {
            reports.add(
                new Report(
                    chain.transform(skeleton),
                    "SCOM", this.params
                )
            );
        }
        if (this.params.containsKey("OCC")) {
            reports.add(
                new Report(
                    chain.transform(skeleton),
                    "OCC", this.params
                )
            );
        }
        if (this.params.containsKey("PCC")) {
            reports.add(
                new Report(
                    chain.transform(skeleton),
                    "PCC"
                )
            );
        }
        if (this.params.containsKey("TCC")) {
            reports.add(
                new Report(
                    chain.transform(skeleton),
                    "TCC"
                )
            );
        }
        new IoCheckedScalar<>(
            new AndInThreads(
                report -> {
                    report.save(this.output);
                },
                reports
            )
        ).value();
        Logger.info(this, "%d XML reports created", reports.size());
        final XML index = new StrictXML(
            new XSLChain(
                new CollectionOf<>(
                    App.xsl("index-post-metric-diff.xsl"),
                    App.xsl("index-post-diff-and-defects.xsl")
                )
            ).transform(
                new XMLDocument(
                    new Xembler(new Index(this.output)).xmlQuietly()
                )
            ),
            new XSDDocument(App.class.getResourceAsStream("xsd/index.xsd"))
        );
        this.save(index.toString(), "index.xml");
        this.save(
            App.xsl("badge.xsl").transform(
                new XMLDocument(
                    new Xembler(
                        new Directives().add("badge").set(
                            index.xpath("/index/@score").get(0)
                        ).attr("style", "round")
                    ).xmlQuietly()
                )
            ).toString(),
            "badge.svg"
        );
        this.save(
            App.xsl("index.xsl").transform(index).toString(),
            "index.html"
        );
        final XML matrix = new StrictXML(
            App.xsl("matrix-post.xsl").transform(
                new XMLDocument(
                    new Xembler(new Matrix(this.output)).xmlQuietly()
                )
            ),
            new XSDDocument(App.class.getResourceAsStream("xsd/matrix.xsd"))
        );
        Logger.info(this, "Matrix generated");
        this.save(matrix.toString(), "matrix.xml");
        this.save(
            App.xsl("matrix.xsl").transform(matrix).toString(),
            "matrix.html"
        );
        this.copy("jpeek.css");
        new IoCheckedScalar<>(
            new And(
                this::copyXsl,
                new ListOf<>("index", "matrix", "metric", "skeleton")
            )
        ).value();
        new IoCheckedScalar<>(
            new And(
                this::copyXsd,
                new ListOf<>("index", "matrix", "metric", "skeleton")
            )
        ).value();
    }

    /**
     * Copy resource.
     * @param name The name of resource
     * @throws IOException If fails
     */
    private void copy(final String name) throws IOException {
        new IoCheckedScalar<>(
            new LengthOf(
                new TeeInput(
                    new ResourceOf(String.format("org/jpeek/%s", name)),
                    this.output.resolve(name)
                )
            )
        ).value();
    }

    /**
     * Copy XSL.
     * @param name The name of resource
     * @throws IOException If fails
     */
    private void copyXsl(final String name) throws IOException {
        this.copy(String.format("xsl/%s.xsl", name));
    }

    /**
     * Copy XSL.
     * @param name The name of resource
     * @throws IOException If fails
     */
    private void copyXsd(final String name) throws IOException {
        this.copy(String.format("xsd/%s.xsd", name));
    }

    /**
     * Save file.
     * @param data Content
     * @param name The name of destination file
     * @throws IOException If fails
     */
    private void save(final String data, final String name) throws IOException {
        new IoCheckedScalar<>(
            new LengthOf(
                new TeeInput(
                    data,
                    this.output.resolve(name)
                )
            )
        ).value();
    }

    /**
     * Make XSL.
     * @param name The name of XSL file
     * @return XSL document
     */
    private static XSL xsl(final String name) {
        return new XSLDocument(
            App.class.getResourceAsStream(String.format("xsl/%s", name))
        ).with(new ClasspathSources());
    }

}
