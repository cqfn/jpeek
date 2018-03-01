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
package org.jpeek;

import com.jcabi.matchers.XhtmlMatchers;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import org.cactoos.collection.CollectionOf;
import org.cactoos.text.TextOf;
import org.hamcrest.MatcherAssert;
import org.jpeek.skeleton.Skeleton;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

// @todo #93:30min NHD needs to be tested against the following after #103 is
//  fixed: NoMethods, OneVoidMethodWithoutParams, WithoutAttributes,
//  OneMethodCreatesLambda. NHD score for all these is "NaN".
// @todo #93:30min NHD calculation needs to take into account the method's
//  visibility, which should be configurable and implemented after #101 is
//  fixed.
// @todo #68:30min SCOM has an impediment on issue #103: cannot currently
//  be tested in MetricsTest when the resulting value is "NaN". Affected
//  tests are: NoMethods, OneVoidMethodWithoutParams, WithoutAttributes,
//  OneMethodCreatesLambda.
// @todo #103:30min NaN-based assertions introduced in #103 made complexity
//  of `testsTarget` higher. Potentially, if more possible invariants will be
//  introduced, enlarging complexity may become real problem for this method.
//  That's why parametrized tests as a generic way of testing all metrics is
//  proposed to be refactored. Possible alternatives are either classical
//  JUnit modules, one per test, or wrapping parameters to reusable test case
//  objects, like described here - https://github.com/yegor256/cactoos-test
/**
 * Tests for all metrics.
 * @author Yegor Bugayenko (yegor256@gmail.com)
 * @version $Id$
 * @since 0.23
 * @checkstyle JavadocMethodCheck (500 lines)
 * @checkstyle VisibilityModifierCheck (500 lines)
 * @checkstyle JavadocVariableCheck (500 lines)
 * @checkstyle MagicNumberCheck (500 lines)
 * @todo #67:30min PCC: add the rest of the test cases for this metric. Could
 *  only fit test case for MethodsWithDiffParamTypes within budget.
 * @todo #90:30min OCC metric: need to implement the rest of the test cases.
 *  Could only fit test for sample class "Foo" within budget in this one.
 * @todo #106:30min Adding a new 'op' for calls to methods broke some tests
 *  and hence they were removed. Need to do the math for those tests and then
 *  add them back: SCOM with "Foo", SCOM with "MethodsWithDiffParamTypes",
 *  and SCOM with "OverloadMethods".
 */
@RunWith(Parameterized.class)
@SuppressWarnings("PMD.AvoidDuplicateLiterals")
public final class MetricsTest {

    @Parameterized.Parameter
    public String target;

    @Parameterized.Parameter(1)
    public String metric;

    @Parameterized.Parameter(2)
    public double value;

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
            new Object[] {"OneMethodCreatesLambda", "LCOM", 3.0d},
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
            new Object[] {"OneMethodCreatesLambda", "LCOM5", 1.5d},
            new Object[] {"Bar", "NHD", 0.4d},
            new Object[] {"Foo", "NHD", 0.3333d},
            new Object[] {"MethodsWithDiffParamTypes", "NHD", 0.7143d},
            new Object[] {"OverloadMethods", "NHD", 0.5333d},
            new Object[] {"TwoCommonAttributes", "NHD", 0.3333d},
            new Object[] {"MethodsWithDiffParamTypes", "CCM", 0.0476d},
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
            new Object[] {"Foo", "LCOM3", 0.5d},
            new Object[] {"MethodsWithDiffParamTypes", "LCOM3", 0.6667d},
            new Object[] {"NoMethods", "LCOM3", 0.0d},
            new Object[] {"OneVoidMethodWithoutParams", "LCOM3", 1.0d},
            new Object[] {"OverloadMethods", "LCOM3", 0.25d},
            new Object[] {"TwoCommonAttributes", "LCOM3", 1.0d},
            new Object[] {"WithoutAttributes", "LCOM3", 0.0d},
            new Object[] {"MethodsWithDiffParamTypes", "PCC", 0.3333d},
            new Object[] {"Foo", "OCC", 0.5d},
            new Object[] {"Bar", "TCC", 0.0d},
            new Object[] {"Foo", "TCC", 1.0d},
            new Object[] {"MethodsWithDiffParamTypes", "TCC", 0.2d},
            new Object[] {"OverloadMethods", "TCC", 1.0d},
            new Object[] {"TwoCommonAttributes", "TCC", 0.0d},
            new Object[] {"WithoutAttributes", "TCC", 0.0d},
            new Object[] {"Foo", "TLCOM", 1.0d},
            new Object[] {"MethodsWithDiffParamTypes", "TLCOM", 15.0d},
            new Object[] {"NoMethods", "TLCOM", 0.0d},
            new Object[] {"OneVoidMethodWithoutParams", "TLCOM", 1.0d},
            new Object[] {"OnlyOneMethodWithParams", "TLCOM", 0.0d},
            new Object[] {"OverloadMethods", "TLCOM", 0.0d},
            new Object[] {"TwoCommonAttributes", "TLCOM", 4.0d},
            new Object[] {"WithoutAttributes", "TLCOM", 1.0d},
            new Object[] {"MethodMethodCalls", "LCOM4", 0.6d}
        );
    }

    @Test
    public void testsTarget() throws IOException {
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
        MatcherAssert.assertThat(
            XhtmlMatchers.xhtml(
                new TextOf(
                    output.resolve(String.format("%s.xml", this.metric))
                ).asString()
            ),
            XhtmlMatchers.hasXPaths(
                String.format(
                    xpath,
                    this.target, this.value
                )
            )
        );
    }
}
