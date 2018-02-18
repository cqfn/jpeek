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
package org.jpeek.web;

import com.jcabi.matchers.XhtmlMatchers;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.cactoos.collection.Joined;
import org.hamcrest.MatcherAssert;
import org.jpeek.App;
import org.junit.Ignore;
import org.junit.Test;
import org.xembly.Directive;
import org.xembly.Directives;
import org.xembly.Xembler;

/**
 * Test case for {@link Mistakes}.
 * @author Yegor Bugayenko (yegor256@gmail.com)
 * @version $Id$
 * @since 0.16
 * @checkstyle JavadocMethodCheck (500 lines)
 */
public final class MistakesTest {

    @Test
    @Ignore
    public void acceptsAndRenders() throws Exception {
        final Path output = Files.createTempDirectory("").resolve("x2");
        final Path input = Paths.get(".");
        new App(input, output).analyze();
        final Mistakes mistakes = new Mistakes();
        mistakes.add(output);
        MatcherAssert.assertThat(
            XhtmlMatchers.xhtml(
                new Xembler(
                    new Directives().add("metrics").append(
                        new Joined<Directive>(mistakes.worst())
                    )
                ).xmlQuietly()
            ),
            XhtmlMatchers.hasXPath("/metrics/metric[@id='LCOM']/avg")
        );
    }

}
