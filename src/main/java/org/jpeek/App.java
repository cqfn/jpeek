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
import com.jcabi.xml.XSDDocument;
import com.jcabi.xml.XSL;
import com.jcabi.xml.XSLChain;
import com.jcabi.xml.XSLDocument;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.cactoos.collection.CollectionOf;
import org.cactoos.io.ResourceOf;
import org.cactoos.io.TeeInput;
import org.cactoos.list.ListOf;
import org.cactoos.map.MapEntry;
import org.cactoos.map.MapOf;
import org.cactoos.scalar.And;
import org.cactoos.scalar.AndInThreads;
import org.cactoos.scalar.IoChecked;
import org.cactoos.scalar.LengthOf;
import org.jpeek.calculus.Calculus;
import org.jpeek.calculus.GeneralCalculation;
import org.jpeek.calculus.xsl.XslCalculus;
import org.jpeek.skeleton.Skeleton;
import org.xembly.Directives;
import org.xembly.Xembler;

/**
 * Application.
 *
 * <p>There is no thread-safety guarantee.
 *
 * @since 0.1
 * @checkstyle ClassDataAbstractionCouplingCheck (500 lines)
 * @checkstyle ClassFanOutComplexityCheck (500 lines)
 * @checkstyle ExecutableStatementCountCheck (500 lines)
 * @checkstyle NPathComplexityCheck (500 lines)
 * @checkstyle MagicNumberCheck (500 lines)
 * @checkstyle CyclomaticComplexityCheck (500 lines)
 * @checkstyle MethodLengthCheck (500 lines)
 * @checkstyle JavaNCSSCheck (500 lines)
 * @checkstyle BooleanExpressionComplexityCheck (500 lines)
 */
@SuppressWarnings({
    "PMD.AvoidDuplicateLiterals",
    "PMD.NPathComplexity",
    "PMD.CyclomaticComplexity",
    "PMD.StdCyclomaticComplexity",
    "PMD.ModifiedCyclomaticComplexity"
})
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
                new MapEntry<>("TCC", true),
                new MapEntry<>("LCC", true),
                new MapEntry<>("CCM", true),
                new MapEntry<>("MWE", true)
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
     * @todo #452:30min Extract report building
     *  Analyze method is too big. We need to extract report building from
     *  here and use a map instead of if statements se we can make
     *  easier to add and remove metrics from execution.
     */
    @SuppressWarnings({
        "PMD.ExcessiveMethodLength",
        "PMD.NcssCount",
        "PMD.GuardLogStatement"
    })
    public void analyze() throws IOException {
        final long start = System.currentTimeMillis();
        final Base base = new DefaultBase(this.input);
        final XML skeleton = new Skeleton(base).xml();
        final Collection<XSL> layers = new LinkedList<>();
        if (this.params.containsKey("include-ctors")) {
            Logger.debug(this, "Constructors will be included");
        } else {
            layers.add(App.xsl("layers/no-ctors.xsl"));
            Logger.debug(this, "Constructors will be ignored");
        }
        if (this.params.containsKey("include-static-methods")) {
            Logger.debug(this, "Static methods will be included");
        } else {
            layers.add(App.xsl("layers/no-static-methods.xsl"));
            Logger.debug(this, "Static methods will be ignored");
        }
        if (this.params.containsKey("include-private-methods")) {
            Logger.debug(this, "Private methods will be included");
        } else {
            layers.add(App.xsl("layers/no-private-methods.xsl"));
            Logger.debug(this, "Private methods will be ignored");
        }
        final XSL chain = new XSLChain(layers);
        this.save(skeleton.toString(), "skeleton.xml");
        final Collection<Report> reports = new LinkedList<>();
        final Calculus xsl = new XslCalculus();
        final List<String> metrics = new ArrayList<>(this.params.size());
        if (this.params.containsKey("LCOM")) {
            reports.add(
                new XslReport(
                    chain.transform(skeleton), xsl,
                    new ReportData("LCOM", this.params, 10.0d, -5.0d)
                )
            );
        }
        if (this.params.containsKey("CAMC")) {
            reports.add(
                new XslReport(
                    chain.transform(skeleton), xsl,
                    new ReportData("CAMC", this.params)
                )
            );
        }
        if (this.params.containsKey("MMAC")) {
            metrics.add("MMAC");
            reports.add(
                new XslReport(
                    chain.transform(skeleton), xsl,
                    new ReportData("MMAC", this.params, 0.5d, 0.1d)
                )
            );
        }
        if (this.params.containsKey("LCOM5")) {
            metrics.add("LCOM5");
            reports.add(
                new XslReport(
                    chain.transform(skeleton), xsl,
                    new ReportData("LCOM5", this.params, 0.5d, -0.1d)
                )
            );
        }
        if (this.params.containsKey("LCOM4")) {
            reports.add(
                new XslReport(
                    chain.transform(skeleton), xsl,
                    new ReportData("LCOM4", this.params, 0.5d, -0.1d)
                )
            );
        }
        if (this.params.containsKey("NHD")) {
            metrics.add("NHD");
            reports.add(
                new XslReport(
                    chain.transform(skeleton), xsl,
                    new ReportData("NHD")
                )
            );
        }
        if (this.params.containsKey("LCOM2")) {
            metrics.add("LCOM2");
            reports.add(
                new XslReport(
                    chain.transform(skeleton), xsl,
                    new ReportData("LCOM2", this.params)
                )
            );
        }
        if (this.params.containsKey("LCOM3")) {
            reports.add(
                new XslReport(
                    chain.transform(skeleton), xsl,
                    new ReportData("LCOM3", this.params)
                )
            );
        }
        if (this.params.containsKey("SCOM")) {
            reports.add(
                new XslReport(
                    chain.transform(skeleton), xsl,
                    new ReportData("SCOM", this.params)
                )
            );
        }
        if (this.params.containsKey("OCC")) {
            reports.add(
                new XslReport(
                    chain.transform(skeleton), xsl,
                    new ReportData("OCC", this.params)
                )
            );
        }
        if (this.params.containsKey("PCC")) {
            metrics.add("PCC");
            reports.add(
                new XslReport(
                    chain.transform(skeleton), xsl,
                    new ReportData("PCC")
                )
            );
        }
        if (this.params.containsKey("TCC")) {
            metrics.add("TCC");
            reports.add(
                new XslReport(
                    chain.transform(skeleton), xsl,
                    new ReportData("TCC")
                )
            );
        }
        if (this.params.containsKey("LCC")) {
            metrics.add("LCC");
            reports.add(
                new XslReport(
                    chain.transform(skeleton), xsl,
                    new ReportData("LCC")
                )
            );
        }
        if (this.params.containsKey("CCM")) {
            metrics.add("CCM");
            reports.add(
                new XslReport(
                    chain.transform(skeleton), xsl,
                    new ReportData("CCM")
                )
            );
        }
        if (this.params.containsKey("MWE")) {
            reports.add(
                new XslReport(
                    chain.transform(skeleton), xsl,
                    new ReportData("MWE")
                )
            );
        }
        new IoChecked<>(
            new AndInThreads(
                report -> {
                    report.save(this.output);
                },
                reports
            )
        ).value();
        Logger.debug(
            this, "%d XML reports created in %[ms]s",
            reports.size(), System.currentTimeMillis() - start
        );
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
        Logger.info(this, "Matrix generated with %d metrics", reports.size());
        this.save(matrix.toString(), "matrix.xml");
        this.save(
            App.xsl("matrix.xsl").transform(matrix).toString(),
            "matrix.html"
        );
        if (this.params.containsKey("LCOM2") && this.params.containsKey("LCOM5")
            && this.params.containsKey("CCM") && this.params.containsKey("NHD")
            && this.params.containsKey("MMAC") && this.params.containsKey("LCC")
            && this.params.containsKey("TCC") && this.params.containsKey("PCC")) {
            GeneralCalculation.createReport(this.output, metrics);
        }
        this.copy("jpeek.css");
        new IoChecked<>(
            new And(
                this::copyXsl,
                new ListOf<>("index", "matrix", "metric", "skeleton")
            )
        ).value();
        new IoChecked<>(
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
        new IoChecked<>(
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
        new IoChecked<>(
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
