/*
 * SPDX-FileCopyrightText: Copyright (c) 2017-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.jpeek;

import com.jcabi.matchers.XhtmlMatchers;
import com.jcabi.xml.XML;
import com.jcabi.xml.XMLDocument;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.junit.jupiter.api.Test;
import org.llorllale.cactoos.matchers.Assertion;

/**
 * Test case for {@link ReportWithStatistics}.
 * @since 0.19
 * @checkstyle JavadocMethodCheck (500 lines)
 */
final class ReportWithStatisticsTest {

    @Test
    void createsXml() {
        final XML xml = new ReportWithStatistics(
            new XMLDocument("<metric/>")
        );
        final int threads = 10;
        final ExecutorService service = Executors.newFixedThreadPool(threads);
        final CountDownLatch latch = new CountDownLatch(1);
        for (int thread = 0; thread < threads; ++thread) {
            service.submit(
                () -> {
                    latch.await();
                    xml.toString();
                    return null;
                }
            );
        }
        latch.countDown();
        service.shutdown();
        new Assertion<>(
            "Must create report with statistics",
            XhtmlMatchers.xhtml(
                xml.toString()
            ),
            XhtmlMatchers.hasXPaths("/metric/statistics[total='0']")
        ).affirm();
    }

}
