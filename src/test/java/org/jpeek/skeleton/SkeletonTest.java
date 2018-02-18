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
package org.jpeek.skeleton;

import com.jcabi.matchers.XhtmlMatchers;
import java.io.IOException;
import org.hamcrest.MatcherAssert;
import org.jpeek.FakeBase;
import org.junit.Test;

/**
 * Test case for {@link Skeleton}.
 * @author Yegor Bugayenko (yegor256@gmail.com)
 * @version $Id$
 * @since 0.23
 * @checkstyle JavadocMethodCheck (500 lines)
 */
@SuppressWarnings("PMD.AvoidDuplicateLiterals")
public final class SkeletonTest {

    @Test
    public void createsXml() {
        MatcherAssert.assertThat(
            XhtmlMatchers.xhtml(
                new Skeleton(
                    new FakeBase("OverloadMethods", "Bar")
                ).xml().toString()
            ),
            XhtmlMatchers.hasXPaths(
                // @checkstyle LineLength (10 lines)
                "/skeleton/app/package[count(class)=2]",
                "//class[@id='Bar']/methods[count(method)=5]",
                "//class[@id='OverloadMethods']/methods[count(method)=5]",
                "//method[@name='<init>' and @ctor='true']",
                "//class[@id='Bar']//method[@name='getKey']/ops[count(op)=3]",
                "//class[@id='Bar']//method[@name='getKey']/ops/op[@code='put_static' and .='singleton']",
                "//class[@id='Bar']//method[@name='getKey']/ops/op[@code='call' and .='java.lang.String.length']",
                "//class[@id='Bar']//method[@name='getKey']/ops/op[@code='get' and .='key']",
                "//class[@id='Bar']//method[@name='<init>']/ops[count(op)=4]"
            )
        );
    }

    @Test
    public void findsMethodsAndArgs() throws IOException {
        MatcherAssert.assertThat(
            XhtmlMatchers.xhtml(
                new Skeleton(
                    new FakeBase("MethodsWithDiffParamTypes")
                ).xml().toString()
            ),
            XhtmlMatchers.hasXPaths(
                // @checkstyle LineLength (10 lines)
                "//class/methods[count(method)=7]",
                "//method[@name='methodSix']/args[count(arg)=1]",
                "//method[@name='methodSix']/args/arg[@type='Ljava/sql/Timestamp']",
                "//method[@name='methodSix' and return='Ljava/util/Date']",
                "//method[@name='methodTwo' and return='V']",
                "//method[@name='methodOne']/args/arg[@type='Ljava/lang/Object']"
            )
        );
    }

    @Test
    public void findsMethodCalls() throws IOException {
        MatcherAssert.assertThat(
            XhtmlMatchers.xhtml(
                new Skeleton(
                    new FakeBase("Bar", "Foo")
                ).xml().toString()
            ),
            XhtmlMatchers.hasXPaths(
                // @checkstyle LineLength (10 lines)
                "//class[@id='Bar']/methods/method[@name='<init>' and @ctor='true']/ops[op = 'java.lang.Object.<init>']",
                "//class[@id='Bar']/methods/method[@name='getKey']/ops[op = 'java.lang.String.length']",
                "//class[@id='Bar']/methods/method[@name='getValue']/ops[op = 'java.lang.String.length']",
                "//class[@id='Bar']/methods/method[@name='setValue']/ops[op ='java.lang.UnsupportedOperationException.<init>']",
                "//class[@id='Foo']/methods/method[@name='methodOne']/ops[op = 'Foo.methodTwo']",
                "//class[@id='Foo']/methods/method[@name='methodTwo']/ops[op = 'Foo.methodOne']"
            )
        );
    }

}
