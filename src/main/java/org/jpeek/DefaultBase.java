/*
 * The MIT License (MIT)
 *
 * SPDX-FileCopyrightText: Copyright (c) 2017-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.jpeek;

import com.jcabi.log.Logger;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Default base.
 *
 * <p>There is no thread-safety guarantee.
 *
 * @since 0.1
 */
public final class DefaultBase implements Base {

    /**
     * Directory.
     */
    private final Path dir;

    /**
     * Ctor.
     * @param path Path of the directory with files
     */
    public DefaultBase(final Path path) {
        this.dir = path;
    }

    @Override
    public String toString() {
        return this.dir.normalize().toAbsolutePath().toString();
    }

    @Override
    public Iterable<Path> files() throws IOException {
        try (Stream<Path> stream = Files.walk(this.dir)) {
            final List<Path> files = stream.collect(Collectors.toList());
            Logger.debug(this, "Found %d files in %s", files.size(), this.dir);
            return files;
        }
    }

}
