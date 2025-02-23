/*
 * SPDX-FileCopyrightText: Copyright (c) 2017-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.jpeek;

import com.beust.jcommander.ParameterException;
import com.jcabi.matchers.XhtmlMatchers;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.cactoos.Scalar;
import org.cactoos.text.TextOf;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.llorllale.cactoos.matchers.Assertion;
import org.llorllale.cactoos.matchers.IsTrue;
import org.llorllale.cactoos.matchers.Throws;

/**
 * Test case for {@link Main}.
 * @since 0.1
 * @checkstyle JavadocMethodCheck (500 lines)
 */
@SuppressWarnings("PMD.AvoidDuplicateLiterals")
final class MainTest {

    @Test
    void printsHelp() throws IOException {
        Main.main("--help");
    }

    @Test
    void createsXmlReports(@TempDir final Path temp) throws IOException {
        final Path output = temp.resolve("x3");
        final Path input = Paths.get(".");
        Main.main("--sources", input.toString(), "--target", output.toString());
        new Assertion<>(
            "Must create LCOM5 report",
            Files.exists(output.resolve("LCOM5.xml")),
            new IsTrue()
        ).affirm();
    }

    @Test
    void crashesIfInvalidInput() {
        new Assertion<>(
            "Must throw an exception if parameter is invalid",
            () -> {
                Main.main("hello");
                return "";
            }, new Throws<>(ParameterException.class)
        ).affirm();
    }

    @Test
    void crashesIfNoOverwriteAndTargetExists(@TempDir final Path target) {
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
    void crashesIfOverwriteAndSourceEqualsToTarget(@TempDir final Path source) {
        new Assertion(
            "Must throw an exception",
            (Scalar<Boolean>) () -> {
                Main.main(
                    "--sources", source.toString(),
                    "--target", source.toString(),
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
    void crashesIfMetricsHaveInvalidNames(@TempDir final Path target) {
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
    void createsXmlReportsIfOverwriteAndTargetExists(@TempDir final Path target)
        throws IOException {
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

    @Test
    void supportsIncludeFilters(@TempDir final Path temp) throws Exception {
        final Path input = Paths.get(".");
        final Path output = temp.resolve("include");
        Main.main(
            "--sources", input.toString(),
            "--target", output.toString(),
            "--include-ctors",
            "--include-static-methods",
            "--include-private-methods"
        );
        final String skeleton = new TextOf(output.resolve("skeleton.xml")).asString();
        new Assertion<>(
            "Must contain included methods",
            XhtmlMatchers.xhtml(skeleton),
            XhtmlMatchers.hasXPaths(
                "//method[@ctor='true']",
                "//method[@static='true']",
                "//method[@visibility='private']"
            )
        ).affirm();
    }
}
