/*
 * SPDX-FileCopyrightText: Copyright (c) 2017-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.jpeek;

import com.jcabi.matchers.XhtmlMatchers;
import com.jcabi.xml.XML;
import com.jcabi.xml.XMLDocument;
import java.util.Collection;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import org.hamcrest.core.IsEqual;
import org.junit.jupiter.api.Test;
import org.llorllale.cactoos.matchers.Assertion;

/**
 * Test case for {@link ReportWithStatistics}.
 * @since 0.19
 */
final class ReportWithStatisticsTest {

    @Test
    @SuppressWarnings("PMD.CloseResource")
    void createsXml() throws InterruptedException {
        final XML xml = new ReportWithStatistics(
            new XMLDocument("<metric/>")
        );
        final int threads = 10;
        final ExecutorService service = Executors.newFixedThreadPool(threads);
        final CountDownLatch latch = new CountDownLatch(1);
        final Collection<String> results = new ConcurrentSkipListSet<>();
        for (int thread = 0; thread < threads; ++thread) {
            service.submit(
                () -> {
                    latch.await();
                    results.add(xml.toString());
                    return null;
                }
            );
        }
        latch.countDown();
        service.shutdown();
        service.awaitTermination(1L, TimeUnit.MINUTES);
        new Assertion<>(
            "Must produce the same xml in every thread",
            results.size(),
            new IsEqual<>(1)
        ).affirm();
        new Assertion<>(
            "Must create report with statistics",
            XhtmlMatchers.xhtml(
                xml.toString()
            ),
            XhtmlMatchers.hasXPaths("/metric/statistics[total='0']")
        ).affirm();
    }
}
