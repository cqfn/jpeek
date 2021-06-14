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
package org.jpeek.calculus;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.jpeek.FakeBase;
import org.jpeek.Main;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.llorllale.cactoos.matchers.Assertion;
import org.llorllale.cactoos.matchers.IsTrue;

/**
 * Test case for {@link GeneralCalculation}.
 *
 * @since 1.0.0
 * @checkstyle JavadocMethodCheck (500 lines)
 * @checkstyle ParameterNumberCheck (500 lines)
 */
@SuppressWarnings("PMD.AvoidDuplicateLiterals")
class GeneralCalculationTest {

    @Test
    void createReport(@TempDir final Path output) throws IOException {
        final Path input = Paths.get(".");
        final String metrics = "LCOM2,LCOM5,NHD,CCM,MMAC,PCC,TCC,LCC";
        final List<String> metricslist = Arrays.asList(metrics.split(","));
        Main.main(
            "--sources",
            input.toString(),
            "--target",
            output.toString(),
            "--metrics",
            metrics,
            "--overwrite"
        );
        new Assertion<>(
            "Must total.html file exists",
            Files.exists(output.resolve("total.html")),
            new IsTrue()
        ).affirm();
        GeneralCalculation.createReport(output, metricslist);
        new Assertion<>(
            "Must total.html file exists",
            Files.exists(output.resolve("total.html")),
            new IsTrue()
        ).affirm();
    }

    @ParameterizedTest
    @CsvFileSource(resources = "/org/jpeek/generalcalctest-params.csv")
    void getResult(final String target, final Double mainres, final Double typeres,
        final Double refactorres, @TempDir final Path output) throws IOException {
        final String metrics = "LCOM2,LCOM5,NHD,CCM,MMAC,PCC,TCC,LCC";
        final List<String> metricslist = Arrays.asList(metrics.split(","));
        Main.main(
            "--sources",
            new FakeBase(target).files().iterator().next().toString(),
            "--target",
            output.toString(),
            "--overwrite",
            "--metrics",
            metrics
        );
        final Map<String, MetricPresentation> mres = new HashMap<>();
        for (final String mtc : metricslist) {
            mres.put(
                mtc,
                MetricPresentation.initialize(
                    String.format(
                        "%s/%s.xml",
                        output,
                        mtc
                    )
                )
            );
        }
        final Map<String, String[]> results = GeneralCalculation.getResult(mres);
        Assertions.assertArrayEquals(
            new double[]{mainres, typeres, refactorres},
            Arrays.stream(results.get(target)).mapToDouble(Double::parseDouble).toArray()
        );
    }
}

