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
package org.jpeek.web;

import java.io.IOException;
import org.cactoos.BiFunc;
import org.cactoos.Func;
import org.cactoos.func.UncheckedBiFunc;
import org.takes.Request;
import org.takes.Response;
import org.takes.Take;
import org.takes.rq.RqPrint;
import org.takes.rq.multipart.RqMtBase;
import org.takes.rq.multipart.RqMtSmart;
import org.takes.rs.RsText;

/**
 * Upload a list of artifacts (their coordinates).
 *
 * <p>There is no thread-safety guarantee.
 *
 * @author Yegor Bugayenko (yegor256@gmail.com)
 * @version $Id$
 * @since 0.32
 * @checkstyle ClassDataAbstractionCouplingCheck (500 lines)
 * @checkstyle JavadocTagsCheck (500 lines)
 */
final class TkUpload implements Take {

    /**
     * Maker or reports.
     */
    private final UncheckedBiFunc<String, String, Func<String, Response>> reports;

    /**
     * Ctor.
     * @param rpts Reports
     */
    TkUpload(final BiFunc<String, String, Func<String, Response>> rpts) {
        this.reports = new UncheckedBiFunc<>(rpts);
    }

    @Override
    public Response act(final Request req) throws IOException {
        final String[] items = new RqPrint(
            new RqMtSmart(new RqMtBase(req)).single("coordinates")
        ).printBody().trim().split("\n");
        int submitted = 0;
        for (final String item : items) {
            final String[] parts = item.trim().split(":", 2);
            this.reports.apply(parts[0], parts[1]);
            submitted += 1;
        }
        return new RsText(String.format("Uploaded %d artifacts", submitted));
    }

}
