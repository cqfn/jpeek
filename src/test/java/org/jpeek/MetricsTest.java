/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2017-2025 Yegor Bugayenko
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

import com.jcabi.matchers.XhtmlMatchers;
import java.nio.file.Path;
import org.cactoos.text.FormattedText;
import org.cactoos.text.TextOf;
import org.jpeek.calculus.xsl.XslCalculus;
import org.jpeek.skeleton.Skeleton;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.llorllale.cactoos.matchers.Assertion;

// @todo #68:30min SCOM has an impediment on issue #103: cannot currently
//  be tested in MetricsTest when the resulting value is "NaN". Affected
//  tests are: NoMethods, OneVoidMethodWithoutParams, WithoutAttributes,
//  OneMethodCreatesLambda.
/**
 * Tests for all metrics.
 * <p>
 * In some cases to run this test in IDE,
 * you have to set up VM options: -Duser.language=en -Duser.country=US
 * @since 0.23
 * @todo #118:30min Add test for LCC with "IndirectlyRelatedPairs" and others.
 *  In "IndirectlyRelatedPairs" all methods exist in one transitive closure, so
 *  the result should be {@code 1d}. Also, all classes without transitive
 *  relations should have the same LCC metric as TCC metric. Before do it we have
 *  to fix puzzles in LCC.xml.
 * @todo #437:30min Skeleton structure has changed in #437, and therefore some xsl metrics calculus
 *  are in error or give bad results. Fix the corresponding xslt files and then reput these expected
 *  results in metricstest-params.csv
 *  MethodsWithDiffParamTypes,CCM,0.0667d
 *  MethodsWithDiffParamTypes,SCOM,0.2381d
 *  OverloadMethods,SCOM,0.75d
 *  TwoCommonAttributes,TLCOM,4.0d
 *  Bar,CCM,0.2222d
 *  TwoCommonMethods,CCM,0.0333d
 *  TwoCommonMethods,LORM,0.26667d
 * @checkstyle JavadocTagsCheck (500 lines)
 * @checkstyle JavadocMethodCheck (500 lines)
 * @checkstyle ParameterNumberCheck (500 lines)
 */
final class MetricsTest {

    @ParameterizedTest
    @CsvFileSource(resources = "/org/jpeek/metricstest-params.csv")
    void testsTarget(final String target, final String metric, final double value,
        @TempDir final Path output)
        throws Exception {
        new XslReport(
            new Skeleton(new FakeBase(target)).xml(), new XslCalculus(),
            new ReportData(metric)
        ).save(output);
        final String xpath;
        if (Double.isNaN(value)) {
            xpath = "//class[@id='%s' and @value='NaN']";
        } else {
            xpath = "//class[@id='%s' and number(@value)=%.4f]";
        }
        new Assertion<>(
            new FormattedText(
                "Must exists with target '%s' and value '%s'",
                target, value
            ).asString(),
            XhtmlMatchers.xhtml(
                new TextOf(
                    output.resolve(String.format("%s.xml", metric))
                ).asString()
            ),
            XhtmlMatchers.hasXPaths(
                String.format(
                    xpath,
                    target, value
                )
            )
        ).affirm();
    }
}
