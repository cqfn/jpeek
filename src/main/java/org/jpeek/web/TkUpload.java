/*
 * SPDX-FileCopyrightText: Copyright (c) 2017-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
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
 * @since 0.32
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
