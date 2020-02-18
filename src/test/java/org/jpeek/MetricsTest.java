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
package org.jpeek;

import com.jcabi.matchers.XhtmlMatchers;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Locale;
import org.cactoos.collection.CollectionOf;
import org.cactoos.text.FormattedText;
import org.cactoos.text.TextOf;
import org.jpeek.skeleton.Skeleton;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.llorllale.cactoos.matchers.Assertion;

// @todo #68:30min SCOM has an impediment on issue #103: cannot currently
//  be tested in MetricsTest when the resulting value is "NaN". Affected
//  tests are: NoMethods, OneVoidMethodWithoutParams, WithoutAttributes,
//  OneMethodCreatesLambda.
/**
 * Tests for all metrics.
 * @author Yegor Bugayenko (yegor256@gmail.com)
 * @version $Id$
 * @since 0.23
 * @checkstyle JavadocMethodCheck (500 lines)
 * @checkstyle VisibilityModifierCheck (500 lines)
 * @checkstyle JavadocVariableCheck (500 lines)
 * @checkstyle MagicNumberCheck (500 lines)
 * @todo #118:30min Add test for LCC with "IndirectlyRelatedPairs" and others.
 *  In "IndirectlyRelatedPairs" all methods exist in one transitive closure, so
 *  the result should be {@code 1d}. Also, all classes without transitive
 *  relations should have the same LCC metric as TCC metric. Before do it we have
 *  to fix puzzles in LCC.xml.
 * @todo #323:30min This test is fully written against JUnit 4 API.
 *  Migrate this parametrized test to junit 5, so it won't import any classes from junit 4 anymore.
 * @checkstyle JavadocTagsCheck (500 lines)
 */
@RunWith(Parameterized.class)
@SuppressWarnings({
    "PMD.AvoidDuplicateLiterals",
    "PMD.ExcessiveMethodLength",
    "PMD.UseUnderscoresInNumericLiterals"
})
public final class MetricsTest {

    @Parameterized.Parameter
    public String target;

    @Parameterized.Parameter(1)
    public String metric;

    @Parameterized.Parameter(2)
    public double value;

    // @checkstyle MethodLengthCheck (200 lines)
    @Parameterized.Parameters(name = "{0}:{1}:{2}")
    public static Collection<Object[]> targets() {
        return new CollectionOf<>(
            new Object[] {"NoMethods", "NHD", Double.NaN},
            new Object[] {"Bar", "LCOM", 6.0d},
            new Object[] {"Foo", "LCOM", 1.0d},
            new Object[] {"MethodsWithDiffParamTypes", "LCOM", 15.0d},
            new Object[] {"NoMethods", "LCOM", 0.0d},
            new Object[] {"OneVoidMethodWithoutParams", "LCOM", 1.0d},
            new Object[] {"OverloadMethods", "LCOM", 0.0d},
            new Object[] {"TwoCommonAttributes", "LCOM", 6.0d},
            new Object[] {"WithoutAttributes", "LCOM", 1.0d},
            new Object[] {"OneMethodCreatesLambda", "LCOM", 1.0d},
            new Object[] {"Bar", "CAMC", 0.4d},
            new Object[] {"Foo", "CAMC", 0.6667d},
            new Object[] {"Bar", "MMAC", 0.1d},
            new Object[] {"Foo", "MMAC", 0.3333d},
            new Object[] {"MethodsWithDiffParamTypes", "MMAC", 0.0d},
            new Object[] {"NoMethods", "MMAC", Double.NaN},
            new Object[] {"OneVoidMethodWithoutParams", "MMAC", Double.NaN},
            new Object[] {"OverloadMethods", "MMAC", 0.2333d},
            new Object[] {"TwoCommonAttributes", "MMAC", 0.1667d},
            new Object[] {"WithoutAttributes", "MMAC", 0.0d},
            new Object[] {"OneMethodCreatesLambda", "MMAC", 0.0d},
            new Object[] {"Foo", "LCOM5", 0.5d},
            new Object[] {"Bar", "LCOM5", 0.8125d},
            new Object[] {"MethodsWithDiffParamTypes", "LCOM5", 0.6667d},
            new Object[] {"OverloadMethods", "LCOM5", 0.25d},
            new Object[] {"TwoCommonAttributes", "LCOM5", 1.0d},
            new Object[] {"NoMethods", "LCOM5", Double.NaN},
            new Object[] {"WithoutAttributes", "LCOM5", Double.NaN},
            new Object[] {"OneVoidMethodWithoutParams", "LCOM5", 1.0d},
            new Object[] {"OneMethodCreatesLambda", "LCOM5", 2.0d},
            new Object[] {"Bar", "NHD", 0.4d},
            new Object[] {"Foo", "NHD", 0.3333d},
            new Object[] {"MethodsWithDiffParamTypes", "NHD", 0.7143d},
            new Object[] {"OverloadMethods", "NHD", 0.5333d},
            new Object[] {"TwoCommonAttributes", "NHD", 0.3333d},
            new Object[] {"NoMethods", "NHD", Double.NaN},
            new Object[] {"OneVoidMethodWithoutParams", "NHD", Double.NaN},
            new Object[] {"WithoutAttributes", "NHD", 0.0d},
            new Object[] {"OneMethodCreatesLambda", "NHD", 0.0d},
            new Object[] {"MethodsWithDiffParamTypes", "CCM", 0.0476d},
            new Object[] {"Foo", "SCOM", 0.5d},
            new Object[] {"MethodsWithDiffParamTypes", "SCOM", 0.2381d},
            new Object[] {"OverloadMethods", "SCOM", 0.75d},
            new Object[] {"TwoCommonAttributes", "SCOM", 0.0d},
            new Object[] {"NoMethods", "SCOM", Double.NaN},
            new Object[] {"OneVoidMethodWithoutParams", "SCOM", 0.0d},
            new Object[] {"WithoutAttributes", "SCOM", Double.NaN},
            new Object[] {"OneMethodCreatesLambda", "SCOM", 0.0d},
            new Object[] {"Foo", "LCOM2", 0.3333d},
            new Object[] {"MethodsWithDiffParamTypes", "LCOM2", 0.5714d},
            new Object[] {"NoMethods", "LCOM2", 1.0d},
            new Object[] {"OneVoidMethodWithoutParams", "LCOM2", 0.5d},
            new Object[] {"OverloadMethods", "LCOM2", 0.2d},
            new Object[] {"TwoCommonAttributes", "LCOM2", 0.75d},
            new Object[] {"WithoutAttributes", "LCOM2", 0.0d},
            new Object[] {"OneMethodCreatesLambda", "LCOM2", 1.0d},
            new Object[] {"BridgeMethod", "LCOM2", 0.0d},
            new Object[] {"Foo", "LCOM3", 0.5d},
            new Object[] {"MethodsWithDiffParamTypes", "LCOM3", 0.6667d},
            new Object[] {"NoMethods", "LCOM3", 0.0d},
            new Object[] {"OneVoidMethodWithoutParams", "LCOM3", 1.0d},
            new Object[] {"OverloadMethods", "LCOM3", 0.25d},
            new Object[] {"TwoCommonAttributes", "LCOM3", 1.0d},
            new Object[] {"WithoutAttributes", "LCOM3", 0.0d},
            new Object[] {"Bar", "PCC", 0.5d},
            new Object[] {"BridgeMethod", "PCC", Double.NaN},
            new Object[] {
                "ClassWithDifferentMethodVisibilities", "PCC", Double.NaN,
            },
            new Object[] {"ClassWithPublicField", "PCC", Double.NaN},
            new Object[] {"Foo", "PCC", 0.5d},
            new Object[] {"IndirectlyRelatedPairs", "PCC", Double.NaN},
            new Object[] {"MethodMethodCalls", "PCC", 0.2d},
            new Object[] {"MethodsWithDiffParamTypes", "PCC", 0.3333d},
            new Object[] {"NoMethods", "PCC", Double.NaN},
            new Object[] {"NotCommonAttributes", "PCC", Double.NaN},
            new Object[] {
                "NotCommonAttributesWithAllArgsConstructor", "PCC", 1.0d,
            },
            new Object[] {"OneCommonAttribute", "PCC", 0.5d},
            new Object[] {"OneMethodCreatesLambda", "PCC", Double.NaN},
            new Object[] {"OneVoidMethodWithoutParams", "PCC", Double.NaN},
            new Object[] {"OnlyOneMethodWithParams", "PCC", 1.0d},
            new Object[] {"OverloadMethods", "PCC", 3.0d},
            new Object[] {"TwoCommonAttributes", "PCC", Double.NaN},
            new Object[] {"TwoCommonMethods", "PCC", Double.NaN},
            new Object[] {"WithoutAttributes", "PCC", Double.NaN},
            new Object[] {"Foo", "OCC", 0.5d},
            new Object[] {"Bar", "TCC", 0.0d},
            new Object[] {"Foo", "TCC", 1.0d},
            new Object[] {"MethodsWithDiffParamTypes", "TCC", 0.2d},
            new Object[] {"OverloadMethods", "TCC", 1.0d},
            new Object[] {"TwoCommonAttributes", "TCC", 0.0d},
            new Object[] {"WithoutAttributes", "TCC", 0.0d},
            new Object[] {"IndirectlyRelatedPairs", "TCC", 0.6667},
            new Object[] {"Foo", "TLCOM", 1.0d},
            new Object[] {"MethodsWithDiffParamTypes", "TLCOM", 15.0d},
            new Object[] {"NoMethods", "TLCOM", 0.0d},
            new Object[] {"OneVoidMethodWithoutParams", "TLCOM", 1.0d},
            new Object[] {"OnlyOneMethodWithParams", "TLCOM", 0.0d},
            new Object[] {"OverloadMethods", "TLCOM", 0.0d},
            new Object[] {"TwoCommonAttributes", "TLCOM", 4.0d},
            new Object[] {"WithoutAttributes", "TLCOM", 1.0d},
            new Object[] {"Bar", "LCC", 0.0d},
            new Object[] {"Foo", "LCC", 1.0d},
            new Object[] {"MethodMethodCalls", "LCC", 0.1d},
            new Object[] {"MethodsWithDiffParamTypes", "LCC", 0.2d},
            new Object[] {"NoMethods", "LCC", 0.0d},
            new Object[] {"OneMethodCreatesLambda", "LCC", 0.0d},
            new Object[] {"OneVoidMethodWithoutParams", "LCC", 0.0d},
            new Object[] {"OnlyOneMethodWithParams", "LCC", 0.0d},
            new Object[] {"OverloadMethods", "LCC", 1.0d},
            new Object[] {"TwoCommonAttributes", "LCC", 0.0d},
            new Object[] {"WithoutAttributes", "LCC", 0.0d},
            new Object[] {"NoMethods", "CCM", Double.NaN},
            new Object[] {"WithoutAttributes", "CCM", Double.NaN},
            new Object[] {"OneMethodCreatesLambda", "CCM", Double.NaN},
            new Object[] {"OneVoidMethodWithoutParams", "CCM", Double.NaN},
            new Object[] {"Bar", "CCM", 0.125d},
            new Object[] {"Foo", "CCM", 0.1667d},
            new Object[] {"OverloadMethods", "CCM", 0.6d},
            new Object[] {"TwoCommonAttributes", "CCM", Double.NaN},
            new Object[] {"TwoCommonMethods", "CCM", 0.0238d},
            new Object[] {"Bar", "MWE", 1.0d},
            new Object[] {"Foo", "MWE", 1.0d},
            new Object[] {"MethodMethodCalls", "MWE", 1.0d},
            new Object[] {"MethodsWithDiffParamTypes", "MWE", 1.0d},
            new Object[] {"NoMethods", "MWE", 1.0d},
            new Object[] {"OneMethodCreatesLambda", "MWE", 1.0d},
            new Object[] {"OneVoidMethodWithoutParams", "MWE", 1.0d},
            new Object[] {"OnlyOneMethodWithParams", "MWE", 1.0d},
            new Object[] {"OverloadMethods", "MWE", 1.0d},
            new Object[] {"TwoCommonAttributes", "MWE", 1.0d},
            new Object[] {"WithoutAttributes", "MWE", 1.0d},
            new Object[] {"Foo", "CCM", 0.1667d},
            new Object[] {"OverloadMethods", "CCM", 0.6d},
            new Object[] {"TwoCommonAttributes", "CCM", Double.NaN},
            new Object[] {"TwoCommonMethods", "CCM", 0.0238d},
            new Object[] {"Bar", "OCC", 0.75d},
            new Object[] {"BridgeMethod", "OCC", 0.0d},
            new Object[] {"ClassWithPublicField", "OCC", 0.5d},
            new Object[] {
                "IndirectlyRelatedPairs",
                "OCC",
                0.6666666666666666d,
            },
            new Object[] {"MethodMethodCalls", "OCC", 0.2d},
            new Object[] {
                "MethodsWithDiffParamTypes",
                "OCC",
                0.3333333333333333d,
            },
            new Object[] {"NoMethods", "OCC", 0.0d},
            new Object[] {"OneMethodCreatesLambda", "OCC", 0.0d},
            new Object[] {"OneVoidMethodWithoutParams", "OCC", 0.0d},
            new Object[] {"OnlyOneMethodWithParams", "OCC", 1d},
            new Object[] {"OverloadMethods", "OCC", 0.75d},
            new Object[] {"TwoCommonAttributes", "OCC", 0.0d},
            new Object[] {"TwoCommonMethods", "OCC", 0.0d},
            new Object[] {"WithoutAttributes", "OCC", 0.0d},
            new Object[] {"TwoCommonMethods", "LORM", 0.26_667d}
        );
    }

    @Test
    public void testsTarget() throws Exception {
        final Path output = Files.createTempDirectory("");
        new Report(
            new Skeleton(new FakeBase(this.target)).xml(),
            this.metric
        ).save(output);
        final String xpath;
        if (Double.isNaN(this.value)) {
            xpath = "//class[@id='%s' and @value='NaN']";
        } else {
            xpath = "//class[@id='%s' and number(@value)=%.4f]";
        }
        new Assertion<>(
            new FormattedText(
                "Must exists with target '%s' and value '%s'",
                this.target, this.value
            ).asString(),
            XhtmlMatchers.xhtml(
                new TextOf(
                    output.resolve(String.format("%s.xml", this.metric))
                ).asString()
            ),
            XhtmlMatchers.hasXPaths(
                String.format(
                    Locale.US,
                    xpath,
                    this.target, this.value
                )
            )
        ).affirm();
    }
}
