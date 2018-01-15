/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2017 Yegor Bugayenko
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
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

/**
 * Tests for all metrics.
 * @author Yegor Bugayenko (yegor256@gmail.com)
 * @version $Id$
 * @since 0.23
 * @checkstyle JavadocMethodCheck (500 lines)
 * @checkstyle VisibilityModifierCheck (500 lines)
 * @checkstyle JavadocVariableCheck (500 lines)
 * @checkstyle MagicNumberCheck (500 lines)
 */
@RunWith(Parameterized.class)
@SuppressWarnings("PMD.AvoidDuplicateLiterals")
public final class MetricsTest {

    @Parameterized.Parameter(0)
    public String target;

    @Parameterized.Parameter(1)
    public String metric;

    @Parameterized.Parameter(2)
    public double value;

    @Parameterized.Parameters(name = "{0}:{1}:{2}")
    public static Collection<Object[]> targets() {
        return new CollectionOf<>(
            new Object[] {"Bar", "LCOM", 3.0d},
            new Object[] {"Foo", "LCOM", 0.0d},
            new Object[] {"MethodsWithDiffParamTypes", "LCOM", 9.0d},
            new Object[] {"NoMethods", "LCOM", 0.0d},
            new Object[] {"OneVoidMethodWithoutParams", "LCOM", 0.0d},
            new Object[] {"OverloadMethods", "LCOM", 0.0d},
            new Object[] {"TwoCommonAttributes", "LCOM", 3.0d},
            new Object[] {"WithoutAttributes", "LCOM", 0.0d},
            new Object[] {"OneMethodCreatesLambda", "LCOM", 1.0d},
            new Object[] {"Bar", "MMAC", 1.0d},
            new Object[] {"Foo", "MMAC", 1.0d},
            new Object[] {"MethodsWithDiffParamTypes", "MMAC", 0.037d},
            new Object[] {"NoMethods", "MMAC", 0.0d},
            new Object[] {"OneVoidMethodWithoutParams", "MMAC", 0.0d},
            new Object[] {"OverloadMethods", "MMAC", 0.7222d},
            new Object[] {"TwoCommonAttributes", "MMAC", 0.3333d},
            new Object[] {"WithoutAttributes", "MMAC", 1.0d},
            new Object[] {"OneMethodCreatesLambda", "MMAC", 0.0d}
        );
    }

    @Test
    public void testsTarget() throws IOException {
        final Path output = Files.createTempDirectory("");
        new Report(
            new Skeleton(new FakeBase(this.target)).xml(),
            this.metric
        ).save(output);
        MatcherAssert.assertThat(
            XhtmlMatchers.xhtml(
                new TextOf(
                    output.resolve(String.format("%s.xml", this.metric))
                ).asString()
            ),
            XhtmlMatchers.hasXPaths(
                String.format(
                    "//class[@id='%s' and number(@value)=%.4f]",
                    this.target, this.value
                )
            )
        );
    }

}
