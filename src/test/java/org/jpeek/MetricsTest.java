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
import org.cactoos.collection.CollectionOf;
import org.cactoos.text.FormattedText;
import org.cactoos.text.TextOf;
import org.jpeek.skeleton.Skeleton;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
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
 * @checkstyle JavadocMethodCheck (500 lines)
 * @checkstyle VisibilityModifierCheck (500 lines)
 * @checkstyle JavadocVariableCheck (500 lines)
 * @checkstyle MagicNumberCheck (500 lines)
 * @todo #118:30min Add test for LCC with "IndirectlyRelatedPairs" and others.
 *  In "IndirectlyRelatedPairs" all methods exist in one transitive closure, so
 *  the result should be {@code 1d}. Also, all classes without transitive
 *  relations should have the same LCC metric as TCC metric. Before do it we have
 *  to fix puzzles in LCC.xml.
 * @checkstyle JavadocTagsCheck (500 lines)
 * @checkstyle ClassDataAbstractionCouplingCheck (500 lines)
 */
@RunWith(Parameterized.class)
@SuppressWarnings({
    "PMD.AvoidDuplicateLiterals",
    "PMD.ExcessiveMethodLength",
    "PMD.UseUnderscoresInNumericLiterals"
})
public final class MetricsTest {

    @ParameterizedTest
    @MethodSource("targets")
    public void testsTarget(final String target, final String metric, final double value)
        throws Exception {
        final Path output = Files.createTempDirectory("");
        new XslReport(
            new Skeleton(new FakeBase(target)).xml(),
            metric, new XslCalculus()
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

    // @checkstyle MethodLengthCheck (200 lines)
    private static Collection<Arguments> targets() {
        return new CollectionOf<>(
            Arguments.of("NoMethods", "NHD", Double.NaN),
            Arguments.of("Bar", "LCOM", 6.0d),
            Arguments.of("Foo", "LCOM", 1.0d),
            Arguments.of("MethodsWithDiffParamTypes", "LCOM", 15.0d),
            Arguments.of("NoMethods", "LCOM", 0.0d),
            Arguments.of("OneVoidMethodWithoutParams", "LCOM", 1.0d),
            Arguments.of("OverloadMethods", "LCOM", 0.0d),
            Arguments.of("TwoCommonAttributes", "LCOM", 6.0d),
            Arguments.of("WithoutAttributes", "LCOM", 1.0d),
            Arguments.of("OneMethodCreatesLambda", "LCOM", 1.0d),
            Arguments.of("Bar", "CAMC", 0.4d),
            Arguments.of("Foo", "CAMC", 0.6667d),
            Arguments.of("Bar", "MMAC", 0.1d),
            Arguments.of("Foo", "MMAC", 0.3333d),
            Arguments.of("MethodsWithDiffParamTypes", "MMAC", 0.0d),
            Arguments.of("NoMethods", "MMAC", Double.NaN),
            Arguments.of("OneVoidMethodWithoutParams", "MMAC", Double.NaN),
            Arguments.of("OverloadMethods", "MMAC", 0.2333d),
            Arguments.of("TwoCommonAttributes", "MMAC", 0.1667d),
            Arguments.of("WithoutAttributes", "MMAC", 0.0d),
            Arguments.of("OneMethodCreatesLambda", "MMAC", 0.0d),
            Arguments.of("Foo", "LCOM5", 0.5d),
            Arguments.of("Bar", "LCOM5", 0.8125d),
            Arguments.of("MethodsWithDiffParamTypes", "LCOM5", 0.6667d),
            Arguments.of("OverloadMethods", "LCOM5", 0.25d),
            Arguments.of("TwoCommonAttributes", "LCOM5", 1.0d),
            Arguments.of("NoMethods", "LCOM5", Double.NaN),
            Arguments.of("WithoutAttributes", "LCOM5", Double.NaN),
            Arguments.of("OneVoidMethodWithoutParams", "LCOM5", 1.0d),
            Arguments.of("OneMethodCreatesLambda", "LCOM5", 2.0d),
            Arguments.of("Bar", "NHD", 0.4d),
            Arguments.of("Foo", "NHD", 0.3333d),
            Arguments.of("MethodsWithDiffParamTypes", "NHD", 0.7143d),
            Arguments.of("OverloadMethods", "NHD", 0.5333d),
            Arguments.of("TwoCommonAttributes", "NHD", 0.3333d),
            Arguments.of("NoMethods", "NHD", Double.NaN),
            Arguments.of("OneVoidMethodWithoutParams", "NHD", Double.NaN),
            Arguments.of("WithoutAttributes", "NHD", 0.0d),
            Arguments.of("OneMethodCreatesLambda", "NHD", 0.0d),
            Arguments.of("MethodsWithDiffParamTypes", "CCM", 0.0476d),
            Arguments.of("Foo", "SCOM", 0.5d),
            Arguments.of("MethodsWithDiffParamTypes", "SCOM", 0.2381d),
            Arguments.of("OverloadMethods", "SCOM", 0.75d),
            Arguments.of("TwoCommonAttributes", "SCOM", 0.0d),
            Arguments.of("NoMethods", "SCOM", Double.NaN),
            Arguments.of("OneVoidMethodWithoutParams", "SCOM", 0.0d),
            Arguments.of("WithoutAttributes", "SCOM", Double.NaN),
            Arguments.of("OneMethodCreatesLambda", "SCOM", 0.0d),
            Arguments.of("Foo", "LCOM2", 0.3333d),
            Arguments.of("MethodsWithDiffParamTypes", "LCOM2", 0.5714d),
            Arguments.of("NoMethods", "LCOM2", 1.0d),
            Arguments.of("OneVoidMethodWithoutParams", "LCOM2", 0.5d),
            Arguments.of("OverloadMethods", "LCOM2", 0.2d),
            Arguments.of("TwoCommonAttributes", "LCOM2", 0.75d),
            Arguments.of("WithoutAttributes", "LCOM2", 0.0d),
            Arguments.of("OneMethodCreatesLambda", "LCOM2", 1.0d),
            Arguments.of("BridgeMethod", "LCOM2", 0.0d),
            Arguments.of("Foo", "LCOM3", 0.5d),
            Arguments.of("MethodsWithDiffParamTypes", "LCOM3", 0.6667d),
            Arguments.of("NoMethods", "LCOM3", 0.0d),
            Arguments.of("OneVoidMethodWithoutParams", "LCOM3", 1.0d),
            Arguments.of("OverloadMethods", "LCOM3", 0.25d),
            Arguments.of("TwoCommonAttributes", "LCOM3", 1.0d),
            Arguments.of("WithoutAttributes", "LCOM3", 0.0d),
            Arguments.of("Bar", "PCC", 0.5d),
            Arguments.of("BridgeMethod", "PCC", Double.NaN),
            Arguments.of(
                "ClassWithDifferentMethodVisibilities", "PCC", Double.NaN
            ),
            Arguments.of("ClassWithPublicField", "PCC", Double.NaN),
            Arguments.of("Foo", "PCC", 0.5d),
            Arguments.of("IndirectlyRelatedPairs", "PCC", Double.NaN),
            Arguments.of("MethodMethodCalls", "PCC", 0.2d),
            Arguments.of("MethodsWithDiffParamTypes", "PCC", 0.3333d),
            Arguments.of("NoMethods", "PCC", Double.NaN),
            Arguments.of("NotCommonAttributes", "PCC", Double.NaN),
            Arguments.of(
                "NotCommonAttributesWithAllArgsConstructor", "PCC", 1.0d
            ),
            Arguments.of("OneCommonAttribute", "PCC", 0.5d),
            Arguments.of("OneMethodCreatesLambda", "PCC", Double.NaN),
            Arguments.of("OneVoidMethodWithoutParams", "PCC", Double.NaN),
            Arguments.of("OnlyOneMethodWithParams", "PCC", 1.0d),
            Arguments.of("OverloadMethods", "PCC", 3.0d),
            Arguments.of("TwoCommonAttributes", "PCC", Double.NaN),
            Arguments.of("TwoCommonMethods", "PCC", Double.NaN),
            Arguments.of("WithoutAttributes", "PCC", Double.NaN),
            Arguments.of("Foo", "OCC", 0.5d),
            Arguments.of("Bar", "TCC", 0.0d),
            Arguments.of("Foo", "TCC", 1.0d),
            Arguments.of("ClassSameAsAnotherPublicField", "TCC", 0.0d),
            Arguments.of("MethodsWithDiffParamTypes", "TCC", 0.2d),
            Arguments.of("OverloadMethods", "TCC", 1.0d),
            Arguments.of("TwoCommonAttributes", "TCC", 0.0d),
            Arguments.of("WithoutAttributes", "TCC", 0.0d),
            Arguments.of("IndirectlyRelatedPairs", "TCC", 0.6667),
            Arguments.of("Foo", "TLCOM", 1.0d),
            Arguments.of("MethodsWithDiffParamTypes", "TLCOM", 15.0d),
            Arguments.of("NoMethods", "TLCOM", 0.0d),
            Arguments.of("OneVoidMethodWithoutParams", "TLCOM", 1.0d),
            Arguments.of("OnlyOneMethodWithParams", "TLCOM", 0.0d),
            Arguments.of("OverloadMethods", "TLCOM", 0.0d),
            Arguments.of("TwoCommonAttributes", "TLCOM", 4.0d),
            Arguments.of("WithoutAttributes", "TLCOM", 1.0d),
            Arguments.of("Bar", "LCC", 0.0d),
            Arguments.of("Foo", "LCC", 1.0d),
            Arguments.of("MethodMethodCalls", "LCC", 0.1d),
            Arguments.of("MethodsWithDiffParamTypes", "LCC", 0.2d),
            Arguments.of("NoMethods", "LCC", 0.0d),
            Arguments.of("OneMethodCreatesLambda", "LCC", 0.0d),
            Arguments.of("OneVoidMethodWithoutParams", "LCC", 0.0d),
            Arguments.of("OnlyOneMethodWithParams", "LCC", 0.0d),
            Arguments.of("OverloadMethods", "LCC", 1.0d),
            Arguments.of("TwoCommonAttributes", "LCC", 0.0d),
            Arguments.of("WithoutAttributes", "LCC", 0.0d),
            Arguments.of("NoMethods", "CCM", Double.NaN),
            Arguments.of("WithoutAttributes", "CCM", Double.NaN),
            Arguments.of("OneMethodCreatesLambda", "CCM", Double.NaN),
            Arguments.of("OneVoidMethodWithoutParams", "CCM", Double.NaN),
            Arguments.of("Bar", "CCM", 0.125d),
            Arguments.of("Foo", "CCM", 0.1667d),
            Arguments.of("OverloadMethods", "CCM", 0.6d),
            Arguments.of("TwoCommonAttributes", "CCM", Double.NaN),
            Arguments.of("TwoCommonMethods", "CCM", 0.0238d),
            Arguments.of("Bar", "MWE", 1.0d),
            Arguments.of("Foo", "MWE", 1.0d),
            Arguments.of("MethodMethodCalls", "MWE", 1.0d),
            Arguments.of("MethodsWithDiffParamTypes", "MWE", 1.0d),
            Arguments.of("NoMethods", "MWE", 1.0d),
            Arguments.of("OneMethodCreatesLambda", "MWE", 1.0d),
            Arguments.of("OneVoidMethodWithoutParams", "MWE", 1.0d),
            Arguments.of("OnlyOneMethodWithParams", "MWE", 1.0d),
            Arguments.of("OverloadMethods", "MWE", 1.0d),
            Arguments.of("TwoCommonAttributes", "MWE", 1.0d),
            Arguments.of("WithoutAttributes", "MWE", 1.0d),
            Arguments.of("Foo", "CCM", 0.1667d),
            Arguments.of("OverloadMethods", "CCM", 0.6d),
            Arguments.of("TwoCommonAttributes", "CCM", Double.NaN),
            Arguments.of("TwoCommonMethods", "CCM", 0.0238d),
            Arguments.of("Bar", "OCC", 0.75d),
            Arguments.of("BridgeMethod", "OCC", 0.0d),
            Arguments.of("ClassWithPublicField", "OCC", 0.5d),
            Arguments.of(
                "IndirectlyRelatedPairs",
                "OCC",
                0.6666666666666666d
            ),
            Arguments.of("MethodMethodCalls", "OCC", 0.2d),
            Arguments.of(
                "MethodsWithDiffParamTypes",
                "OCC",
                0.3333333333333333d
            ),
            Arguments.of("NoMethods", "OCC", 0.0d),
            Arguments.of("OneMethodCreatesLambda", "OCC", 0.0d),
            Arguments.of("OneVoidMethodWithoutParams", "OCC", 0.0d),
            Arguments.of("OnlyOneMethodWithParams", "OCC", 1d),
            Arguments.of("OverloadMethods", "OCC", 0.75d),
            Arguments.of("TwoCommonAttributes", "OCC", 0.0d),
            Arguments.of("TwoCommonMethods", "OCC", 0.0d),
            Arguments.of("WithoutAttributes", "OCC", 0.0d),
            Arguments.of("TwoCommonMethods", "LORM", 0.26_667d)
        );
    }
}
