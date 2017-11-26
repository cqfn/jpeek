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
package org.jpeek.metrics;

import com.jcabi.matchers.XhtmlMatchers;
import java.util.ArrayList;
import org.cactoos.iterable.IterableOf;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.xembly.Xembler;

/**
 * Test cases for {@link JavassistClasses}.
 *
 * @author Sergey Karazhenets (sergeykarazhenets@gmail.com)
 * @version $Id$
 * @since 0.13
 * @checkstyle JavadocMethodCheck (500 lines)
 */
public final class JavassistClassesTest {

    @Test
    public void doNotConsiderClasses() throws Exception {
        MatcherAssert.assertThat(
            XhtmlMatchers.xhtml(
                new Xembler(
                    new JavassistClasses(
                        new FakeBase(
                            "JustInterface",
                            "JustEnum",
                            "JustAnnotation",
                            "JustGenClass$1",
                            "Just$AjcClosure1"
                        ),
                        ctc -> new IterableOf<>(new ArrayList<>(0))
                    ).xembly()
                ).xmlQuietly()
            ),
            Matchers.allOf(
                Matchers.not(
                    XhtmlMatchers.hasXPath(
                        "/metric/app/package/class[@id='JustInterface']"
                    )
                ),
                Matchers.not(
                    XhtmlMatchers.hasXPath(
                        "/metric/app/package/class[@id='JustEnum']"
                    )
                ),
                Matchers.not(
                    XhtmlMatchers.hasXPath(
                        "/metric/app/package/class[@id='JustAnnotation']"
                    )
                ),
                Matchers.not(
                    XhtmlMatchers.hasXPath(
                        "/metric/app/package/class[@id='JustGenClass$1']"
                    )
                ),
                Matchers.not(
                    XhtmlMatchers.hasXPath(
                        "/metric/app/package/class[@id='Just$AjcClosure1']"
                    )
                )
            )
        );
    }
}
