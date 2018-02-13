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

import com.beust.jcommander.ParameterException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;

/**
 * Test case for {@link Main}.
 * @author Yegor Bugayenko (yegor256@gmail.com)
 * @version $Id$
 * @since 0.1
 * @checkstyle JavadocMethodCheck (500 lines)
 */
@SuppressWarnings("PMD.AvoidDuplicateLiterals")
public final class MainTest {

    @Test
    public void createsXmlReports() throws IOException {
        final Path output = Files.createTempDirectory("").resolve("x3");
        final Path input = Paths.get(".");
        Main.main("--sources", input.toString(), "--target", output.toString());
        MatcherAssert.assertThat(
            Files.exists(output.resolve("LCOM5.xml")),
            Matchers.equalTo(true)
        );
    }

    @Test(expected = ParameterException.class)
    public void crashesIfInvalidInput() throws IOException {
        Main.main("hello");
    }

    @Test(expected = IllegalStateException.class)
    public void crashesIfNoOverwriteAndTargetExists() throws IOException {
        final Path target = Files.createTempDirectory("");
        Main.main(
            "--sources", Paths.get(".").toString(),
            "--target", target.toString()
        );
    }

    @Test
    public void createsXmlReportsIfOverwriteAndTargetExists()
        throws IOException {
        final Path target = Files.createTempDirectory("");
        Main.main(
            "--sources", Paths.get(".").toString(),
            "--target", target.toString(),
            "--overwrite"
        );
        MatcherAssert.assertThat(
            Files.exists(target.resolve("LCOM5.xml")),
            Matchers.equalTo(true)
        );
    }
}
