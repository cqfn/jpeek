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
     * XSL stylesheet.
     */
    private static final XSL STYLESHEET = XSLDocument.make(
        App.class.getResourceAsStream("index.xsl")
    );

    /**
     * XSL stylesheet.
     */
    private static final XSL BADGE = XSLDocument.make(
        App.class.getResourceAsStream("badge.xsl")
    );

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
        final Iterable<Metric> metrics = new ListOf<>(
            new CAMC(base),
            new LCOM(base),
            new OCC(base),
            new NHD(base)
        );
        new IoCheckedScalar<>(
            new And(
                metrics,
                metric -> {
                    new Report(metric).save(this.output);
                }
            )
        ).value();
        new LengthOf(
            new TeeInput(
                new ResourceOf("org/jpeek/jpeek.xsl"),
                this.output.resolve("jpeek.xsl")
            )
        ).value();
        final XML index = new XMLDocument(
            new Xembler(
                new Index(this.output).value()
            ).xmlQuietly()
        );
        new LengthOf(
            new TeeInput(
                index.toString(),
                this.output.resolve("index.xml")
            )
        ).value();
        new LengthOf(
            new TeeInput(
                App.STYLESHEET.transform(index).toString(),
                this.output.resolve("index.html")
            )
        ).value();
        new LengthOf(
            new TeeInput(
                App.BADGE.transform(
                    new XMLDocument(
                        new Xembler(
                            new Directives().add("score").set(
                                String.format(
                                    "%.4f",
                                    Double.parseDouble(
                                        index.xpath(
                                            // @checkstyle LineLength (1 line)
                                            "sum(//metric/score) div count(//metric)"
                                        ).get(0)
                                    )
                                )
                            ).attr("style", "round")
                        ).xmlQuietly()
                    )
                ).toString(),
                this.output.resolve("badge.svg")
            )
        ).value();
    }

}
