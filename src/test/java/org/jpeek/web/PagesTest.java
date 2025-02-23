/*
 * SPDX-FileCopyrightText: Copyright (c) 2017-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.jpeek.web;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import org.hamcrest.core.IsEqual;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.llorllale.cactoos.matchers.Assertion;

/**
 * Test case for {@link Pages}.
 * @since 0.31
 * @checkstyle JavadocMethodCheck (500 lines)
 */
final class PagesTest {

    /**
     * Simple test.
     * @param temp Temp directory
     */
    @Test
    void testApply(@TempDir final Path temp) throws IOException {
        temp.resolve("a").toFile().mkdirs();
        final String path = "a/z.class";
        Files.write(
            temp.resolve(path), "".getBytes(),
            StandardOpenOption.CREATE_NEW
        );
        new Assertion<>(
            "There is no byte available in body - should return -1",
            new Pages(temp).apply(path).body().read(),
            new IsEqual<>(-1)
        ).affirm();
    }
}
