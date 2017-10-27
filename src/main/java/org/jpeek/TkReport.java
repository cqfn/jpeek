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

import com.jcabi.xml.XMLDocument;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.util.regex.Matcher;
import org.cactoos.func.IoCheckedBiFunc;
import org.cactoos.func.StickyBiFunc;
import org.cactoos.func.SyncBiFunc;
import org.cactoos.io.LengthOf;
import org.cactoos.io.TeeInput;
import org.cactoos.text.TextOf;
import org.takes.Response;
import org.takes.facets.fork.RqRegex;
import org.takes.facets.fork.TkRegex;
import org.takes.rs.RsText;

/**
 * Report page.
 *
 * <p>There is no thread-safety guarantee.
 *
 * @author Yegor Bugayenko (yegor256@gmail.com)
 * @version $Id$
 * @since 0.5
 * @checkstyle ClassDataAbstractionCouplingCheck (500 lines)
 */
public final class TkReport implements TkRegex {

    /**
     * Directory with sources.
     */
    private final Path sources;

    /**
     * Directory with reports.
     */
    private final Path target;

    /**
     * Maker or reports.
     */
    private final IoCheckedBiFunc<String, String, Path> reports;

    /**
     * Ctor.
     * @param home Home dir
     */
    public TkReport(final Path home) {
        this(home.resolve("sources"), home.resolve("target"));
    }

    /**
     * Ctor.
     * @param input Dir with sources
     * @param output Dir with reports
     */
    public TkReport(final Path input, final Path output) {
        this.sources = input;
        this.target = output;
        this.reports = new IoCheckedBiFunc<>(
            new StickyBiFunc<>(
                new SyncBiFunc<>(
                    this::home
                )
            )
        );
    }

    @Override
    public Response act(final RqRegex req) throws IOException {
        final Matcher matcher = req.matcher();
        // @checkstyle MagicNumber (1 line)
        String path = matcher.group(3);
        if (path.isEmpty()) {
            path = "index.html";
        } else {
            path = path.substring(1);
        }
        return new RsText(
            new TextOf(
                this.reports.apply(
                    matcher.group(1),
                    matcher.group(2)
                ).resolve(path)
            ).asString()
        );
    }

    /**
     * Make report and return its path.
     * @param group Maven group
     * @param artifact Maven artiface
     * @return Path to the report files
     * @throws IOException If fails
     */
    private Path home(final String group, final String artifact)
        throws IOException {
        final String grp = group.replace(".", "/");
        final String version = new XMLDocument(
            new TextOf(
                new URL(
                    String.format(
                        // @checkstyle LineLength (1 line)
                        "http://repo1.maven.org/maven2/%s/%s/maven-metadata.xml",
                        grp, artifact
                    )
                )
            ).asString()
        ).xpath("/metadata/versioning/latest/text()").get(0);
        final Path input = this.sources.resolve(grp).resolve(artifact);
        new LengthOf(
            new TeeInput(
                new URL(
                    String.format(
                        "http://repo1.maven.org/maven2/%s/%s/%s/%2$s-%3$s.jar",
                        grp, artifact, version
                    )
                ),
                input
            )
        ).value();
        final Path output = this.target.resolve(grp).resolve(artifact);
        new App(input, output).analyze();
        return output;
    }

}
