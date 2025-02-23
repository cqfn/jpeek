/*
 * SPDX-FileCopyrightText: Copyright (c) 2017-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.jpeek.web;

import java.io.IOException;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.llorllale.cactoos.matchers.Assertion;
import org.takes.rq.RqFake;
import org.takes.rq.RqWithHeader;
import org.takes.rq.multipart.RqMtFake;
import org.takes.rs.RsPrint;

/**
 * Test case for {@link TkUpload}.
 * @since 0.32
 * @checkstyle JavadocMethodCheck (500 lines)
 */
final class TkUploadTest {

    @Test
    void rendersIndexPage() throws IOException {
        new Assertion<>(
            "Must upload body",
            new RsPrint(
                new TkUpload((artifact, group) -> null).act(
                    new RqMtFake(
                        new RqFake(),
                        new RqWithHeader(
                            new RqFake("POST", "/", "org.jpeek:jpeek"),
                            "Content-Disposition: form-data; name=\"coordinates\""
                        )
                    )
                )
            ).printBody(),
            Matchers.startsWith("Uploaded ")
        ).affirm();
    }

}
