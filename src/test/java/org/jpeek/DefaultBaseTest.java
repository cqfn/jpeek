/*
 * SPDX-FileCopyrightText: Copyright (c) 2017-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.jpeek;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.llorllale.cactoos.matchers.Assertion;

/**
 * Test case for {@link DefaultBase}.
 * @since 0.1
 * @checkstyle JavadocMethodCheck (500 lines)
 */
final class DefaultBaseTest {

    @Test
    void listsFiles(@TempDir final Path temp) throws IOException {
        temp.resolve("a/b/c").toFile().mkdirs();
        Files.write(
            temp.resolve("a/b/c/x.java"), "Hello".getBytes(),
            StandardOpenOption.CREATE_NEW
        );
        Files.write(
            temp.resolve("a/z.class"), "".getBytes(),
            StandardOpenOption.CREATE_NEW
        );
        new Assertion<>(
            "Must be more the 2 files",
            new DefaultBase(temp).files(),
            Matchers.iterableWithSize(Matchers.greaterThan(2))
        ).affirm();
    }

}
