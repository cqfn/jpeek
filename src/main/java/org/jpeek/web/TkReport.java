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

import java.io.IOException;
import java.util.regex.Matcher;
import org.cactoos.BiFunc;
import org.cactoos.Func;
import org.cactoos.func.IoCheckedBiFunc;
import org.cactoos.func.IoCheckedFunc;
import org.takes.Response;
import org.takes.facets.fork.RqRegex;
import org.takes.facets.fork.TkRegex;
import org.takes.facets.forward.RsForward;

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
final class TkReport implements TkRegex {

    /**
     * Maker or reports.
     */
    private final BiFunc<String, String, Func<String, Response>> reports;

    /**
     * Ctor.
     * @param rpts Reports
     */
    TkReport(final BiFunc<String, String, Func<String, Response>> rpts) {
        this.reports = rpts;
    }

    @Override
    public Response act(final RqRegex req) throws IOException {
        final Matcher matcher = req.matcher();
        // @checkstyle MagicNumber (1 line)
        final String path = matcher.group(3);
        if (path.isEmpty()) {
            throw new RsForward(
                String.format("%s/index.html", matcher.group(0))
            );
        }
        return new IoCheckedFunc<>(
            new IoCheckedBiFunc<>(this.reports).apply(
                matcher.group(1),
                matcher.group(2)
            )
        ).apply(path.substring(1));
    }

}
