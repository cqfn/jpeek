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
import com.jcabi.xml.XSL;
import com.jcabi.xml.XSLDocument;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.cactoos.io.LengthOf;
import org.cactoos.io.ResourceOf;
import org.cactoos.io.TeeInput;
import org.cactoos.list.ListOf;
import org.cactoos.scalar.And;
import org.cactoos.scalar.IoCheckedScalar;
import org.jpeek.metrics.cohesion.CAMC;
import org.jpeek.metrics.cohesion.LCOM;
import org.jpeek.metrics.cohesion.LCOM2;
import org.jpeek.metrics.cohesion.LCOM3;
import org.jpeek.metrics.cohesion.MMAC;
import org.jpeek.metrics.cohesion.NHD;
import org.jpeek.metrics.cohesion.OCC;
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
 */
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
     * Ctor.
     * @param source Source directory
     * @param target Target dir
     */
    public App(final Path source, final Path target) {
        this.input = source;
        this.output = target;
    }

    /**
     * Analyze sources.
     * @throws IOException If fails
     */
    @SuppressWarnings("PMD.ExcessiveMethodLength")
    public void analyze() throws IOException {
        if (Files.exists(this.output)) {
            throw new IllegalStateException(
                String.format(
                    "Directory/file already exists: %s",
                    this.output.normalize().toAbsolutePath()
                )
            );
        }
        final Base base = new DefaultBase(this.input);
        final Iterable<Report> reports = new ListOf<>(
            new Report(new CAMC(base), 0.10d, 0.35d),
            new Report(new LCOM(base), 15.0d, 5.0d),
            new Report(new OCC(base), 0.85d, 0.25d),
            new Report(new NHD(base), 0.55d, 0.15d),
            new Report(new LCOM2(base), 0.75d, 0.25d),
            new Report(new LCOM3(base), 0.85d, 0.25d),
            new Report(new MMAC(base), 0.35d, 0.85d)
        );
        new IoCheckedScalar<>(
            new And(
                reports,
                report -> {
                    report.save(this.output);
                }
            )
        ).value();
        final XML index = App.xsl("index-post-2.xsl").transform(
            App.xsl("index-post-1.xsl").transform(
                new XMLDocument(
                    new Xembler(
                        new Index(this.output).value()
                    ).xmlQuietly()
                )
            )
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
        final XML matrix = App.xsl("matrix-post.xsl").transform(
            new XMLDocument(
                new Xembler(
                    new Matrix(this.output).value()
                ).xmlQuietly()
            )
        );
        this.save(matrix.toString(), "matrix.xml");
        this.save(
            App.xsl("matrix.xsl").transform(matrix).toString(),
            "matrix.html"
        );
        this.copy("jpeek.css");
        new IoCheckedScalar<>(
            new And(
                new ListOf<>("index", "matrix", "jpeek"),
                this::copyXsl
            )
        ).value();
    }

    /**
     * Copy resource.
     * @param name The name of resource
     * @throws IOException If fails
     */
    private void copy(final String name) throws IOException {
        new LengthOf(
            new TeeInput(
                new ResourceOf(String.format("org/jpeek/%s", name)),
                this.output.resolve(name)
            )
        ).value();
    }

    /**
     * Copy XSL.
     * @param name The name of resource
     * @throws IOException If fails
     */
    private void copyXsl(final String name) throws IOException {
        this.copy(String.format("%s.xsl", name));
    }

    /**
     * Save file.
     * @param data Content
     * @param name The name of destination file
     * @throws IOException If fails
     */
    private void save(final String data, final String name) throws IOException {
        new LengthOf(
            new TeeInput(
                data,
                this.output.resolve(name)
            )
        ).value();
    }

    /**
     * Make XSL.
     * @param name The name of XSL file
     * @return XSL document
     */
    private static XSL xsl(final String name) {
        return new XSLDocument(App.class.getResourceAsStream(name));
    }

}
