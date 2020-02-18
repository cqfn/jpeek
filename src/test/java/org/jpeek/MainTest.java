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

import com.beust.jcommander.ParameterException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.cactoos.Scalar;
import org.junit.jupiter.api.Test;
import org.llorllale.cactoos.matchers.Assertion;
import org.llorllale.cactoos.matchers.IsTrue;
import org.llorllale.cactoos.matchers.Throws;

/**
 * Test case for {@link Main}.
 * @author Yegor Bugayenko (yegor256@gmail.com)
 * @version $Id$
 * @since 0.1
 * @checkstyle JavadocMethodCheck (500 lines)
 * @checkstyle JavadocTagsCheck (500 lines)
 */
@SuppressWarnings("PMD.AvoidDuplicateLiterals")
public final class MainTest {

    @Test
    public void createsXmlReports() throws IOException {
        final Path output = Files.createTempDirectory("").resolve("x3");
        final Path input = Paths.get(".");
        Main.main("--sources", input.toString(), "--target", output.toString());
        new Assertion<>(
            "Must create LCOM5 report",
            Files.exists(output.resolve("LCOM5.xml")),
            new IsTrue()
        ).affirm();
    }

    @Test
    public void crashesIfInvalidInput() {
        new Assertion<>(
            "Must throw an exception if parameter is invalid",
            () -> {
                Main.main("hello");
                return "";
            }, new Throws<>(ParameterException.class)
        ).affirm();
    }

    @Test
    public void crashesIfNoOverwriteAndTargetExists() throws IOException {
        final Path target = Files.createTempDirectory("");
        new Assertion<>(
            "Must throw an exception if target exists and no overwrite",
            () -> {
                Main.main(
                    "--sources", Paths.get(".").toString(),
                    "--target", target.toString()
                );
                return "";
            }, new Throws<>(IllegalStateException.class)
        ).affirm();
    }

    @Test
    @SuppressWarnings({"unchecked", "rawtypes"})
    public void crashesIfOverwriteAndSourceEqualsToTarget() throws IOException {
        final Path source = Files.createTempDirectory("sourceequalstarget");
        final Path target = source;
        new Assertion(
            "Must throw an exception",
            (Scalar<Boolean>) () -> {
                Main.main(
                    "--sources", source.toString(),
                    "--target", target.toString(),
                    "--overwrite"
                );
                return true;
            },
            new Throws(
                "Invalid paths - can't be equal if overwrite option is set.",
                IllegalArgumentException.class
            )
        ).affirm();
    }

    @Test
    @SuppressWarnings({"unchecked", "rawtypes"})
    public void crashesIfMetricsHaveInvalidNames() throws IOException {
        final Path target = Files.createTempDirectory("");
        new Assertion(
            "Must throw an exception",
            (Scalar<Boolean>) () -> {
                Main.main(
                    "--sources", Paths.get(".").toString(),
                    "--target", target.toString(),
                    "--metrics", "#%$!"
                );
                return true;
            },
            new Throws(
                "Invalid metric name: '#%$!'",
                IllegalArgumentException.class
            )
        ).affirm();
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
        new Assertion<>(
            "Must exists LCOM5.xml",
            Files.exists(target.resolve("LCOM5.xml")),
            new IsTrue()
        ).affirm();
    }
}
