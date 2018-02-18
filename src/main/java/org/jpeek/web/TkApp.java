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
package org.jpeek.web;

import io.sentry.Sentry;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import org.cactoos.BiFunc;
import org.cactoos.func.SolidBiFunc;
import org.cactoos.io.ResourceOf;
import org.cactoos.iterable.PropertiesOf;
import org.cactoos.text.TextOf;
import org.takes.Take;
import org.takes.facets.fallback.Fallback;
import org.takes.facets.fallback.FbChain;
import org.takes.facets.fallback.FbStatus;
import org.takes.facets.fallback.TkFallback;
import org.takes.facets.fork.FkRegex;
import org.takes.facets.fork.TkFork;
import org.takes.facets.forward.TkForward;
import org.takes.http.Exit;
import org.takes.http.FtCli;
import org.takes.misc.Opt;
import org.takes.rs.RsText;
import org.takes.rs.RsWithStatus;
import org.takes.tk.TkClasspath;
import org.takes.tk.TkText;
import org.takes.tk.TkWithType;
import org.takes.tk.TkWrap;

/**
 * Web application.
 *
 * <p>There is no thread-safety guarantee.
 *
 * @author Yegor Bugayenko (yegor256@gmail.com)
 * @version $Id$
 * @since 0.5
 * @checkstyle ClassDataAbstractionCouplingCheck (500 lines)
 * @checkstyle ClassFanOutComplexityCheck (500 lines)
 */
@SuppressWarnings("PMD.UseUtilityClass")
public final class TkApp extends TkWrap {

    /**
     * Ctor.
     * @param home Home directory
     * @throws IOException If fails
     */
    public TkApp(final Path home) throws IOException {
        super(TkApp.make(home));
    }

    /**
     * Main Java entry point.
     * @param args Command line args
     * @throws IOException If fails
     */
    public static void main(final String... args) throws IOException {
        Sentry.init(
            new PropertiesOf(
                new ResourceOf(
                    "org/jpeek/jpeek.properties"
                )
            ).value().getProperty("org.jpeek.sentry")
        );
        new FtCli(
            new TkApp(Files.createTempDirectory("jpeek")),
            args
        ).start(Exit.NEVER);
    }

    /**
     * Ctor.
     * @param home Home directory
     * @return The take
     * @throws IOException If fails
     */
    private static Take make(final Path home) throws IOException {
        final Futures futures = new Futures(new Reports(home));
        return new TkFallback(
            new TkForward(
                new TkFork(
                    new FkRegex("/", new TkIndex()),
                    new FkRegex("/robots.txt", new TkText("")),
                    new FkRegex("/mistakes", new TkMistakes()),
                    new FkRegex(
                        "/flush",
                        (Take) req -> new RsText(
                            String.format("%d flushed", new Results().flush())
                        )
                    ),
                    new FkRegex("/all", new TkAll()),
                    new FkRegex("/queue", new TkQueue(futures)),
                    new FkRegex(
                        ".+\\.xsl",
                        new TkWithType(
                            new TkClasspath(),
                            "text/xsl"
                        )
                    ),
                    new FkRegex(
                        "/jpeek\\.css",
                        new TkWithType(
                            new TkText(
                                new TextOf(
                                    new ResourceOf("org/jpeek/jpeek.css")
                                ).asString()
                            ),
                            "text/css"
                        )
                    ),
                    new FkRegex(
                        "/([^/]+)/([^/]+)(.*)",
                        new TkReport(
                            new BiFunc.NoNulls<>(
                                new AsyncReports(
                                    new BiFunc.NoNulls<>(
                                        // @checkstyle MagicNumber (1 line)
                                        new SolidBiFunc<>(futures, 100)
                                    )
                                )
                            )
                        )
                    )
                )
            ),
            new FbChain(
                new FbStatus(
                    HttpURLConnection.HTTP_NOT_FOUND,
                    (Fallback) req -> new Opt.Single<>(
                        new RsWithStatus(
                            new RsText(req.throwable().getMessage()),
                            req.code()
                        )
                    )
                ),
                req -> {
                    Sentry.capture(req.throwable());
                    return new Opt.Single<>(
                        new RsWithStatus(
                            new RsText(
                                new TextOf(req.throwable()).asString()
                            ),
                            HttpURLConnection.HTTP_INTERNAL_ERROR
                        )
                    );
                }
            )
        );
    }

}
