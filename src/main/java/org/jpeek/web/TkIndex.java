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
package org.jpeek.web;

import java.io.IOException;
import org.cactoos.iterable.Limited;
import org.jpeek.Header;
import org.takes.Request;
import org.takes.Response;
import org.takes.Take;
import org.takes.rs.RsXslt;
import org.takes.rs.xe.RsXembly;
import org.takes.rs.xe.XeAppend;
import org.takes.rs.xe.XeChain;
import org.takes.rs.xe.XeDirectives;
import org.takes.rs.xe.XeMillis;
import org.takes.rs.xe.XeStylesheet;
import org.takes.rs.xe.XeTransform;

/**
 * Index page.
 *
 * <p>There is no thread-safety guarantee.
 *
 * @author Yegor Bugayenko (yegor256@gmail.com)
 * @version $Id$
 * @since 0.10
 * @checkstyle ClassDataAbstractionCouplingCheck (500 lines)
 */
@SuppressWarnings("PMD.AvoidDuplicateLiterals")
final class TkIndex implements Take {

    @Override
    public Response act(final Request req) throws IOException {
        return new RsXslt(
            new RsXembly(
                new XeChain(
                    new XeStylesheet("/org/jpeek/web/index.xsl"),
                    new XeAppend(
                        "index",
                        new XeChain(
                            new XeMillis(),
                            new XeDirectives(new Header()),
                            new XeAppend(
                                "best",
                                new XeTransform<>(
                                    // @checkstyle MagicNumber (1 line)
                                    new Limited<>(new Results().best(), 20),
                                    ent -> {
                                        final String[] parts =
                                            ent.getKey().split(":");
                                        return new XeAppend(
                                            "repo",
                                            new XeChain(
                                                new XeAppend(
                                                    "group",
                                                    parts[0]
                                                ),
                                                new XeAppend(
                                                    "artifact",
                                                    parts[1]
                                                ),
                                                new XeAppend(
                                                    "score",
                                                    Double.toString(
                                                        ent.getValue()
                                                    )
                                                )
                                            )
                                        );
                                    }
                                )
                            ),
                            new XeAppend(
                                "recent",
                                new XeTransform<>(
                                    // @checkstyle MagicNumber (1 line)
                                    new Limited<>(new Results().recent(), 50),
                                    coords -> {
                                        final String[] parts =
                                            coords.split(":");
                                        return new XeAppend(
                                            "repo",
                                            new XeChain(
                                                new XeAppend(
                                                    "group",
                                                    parts[0]
                                                ),
                                                new XeAppend(
                                                    "artifact",
                                                    parts[1]
                                                )
                                            )
                                        );
                                    }
                                )
                            ),
                            new XeMillis(true)
                        )
                    )
                )
            )
        );
    }

}
