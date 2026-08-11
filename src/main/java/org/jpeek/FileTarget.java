/*
 * SPDX-FileCopyrightText: Copyright (c) 2017-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.jpeek;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

/**
 * File Target.
 * @since 0.26.4
 */
public final class FileTarget implements Target {

    /**
     * The file target.
     */
    private final File target;

    /**
     * Overwrites if true.
     */
    private final Boolean overwrite;

    /**
     * Ctor.
     * @param target Target dir
     * @param overwrite Overwrite if exists
     */
    public FileTarget(final File target, final Boolean overwrite) {
        this.target = target;
        this.overwrite = overwrite;
    }

    @Override
    public Path toPath() throws IOException {
        if (this.target.exists()) {
            if (Boolean.TRUE.equals(this.overwrite)) {
                deleteDir(this.target);
            } else {
                throw new IllegalStateException(
                    String.format(
                        "Overwrite disabled. Directory/file already exists: %s",
                        this.target.getAbsolutePath()
                    )
                );
            }
        }
        return this.target.toPath();
    }

    /**
     * Deletes the directory recursively.
     * @param dir The directory
     * @throws IOException If an I/O error occurs
     */
    private static void deleteDir(final File dir) throws IOException {
        Files.walkFileTree(
            dir.toPath(),
            new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(
                    final Path file,
                    final BasicFileAttributes attrs) throws IOException {
                    Files.delete(file);
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult postVisitDirectory(
                    final Path dir,
                    final IOException error) throws IOException {
                    Files.delete(dir);
                    return FileVisitResult.CONTINUE;
                }
            }
        );
        com.jcabi.log.Logger.info(Main.class, "Directory %s deleted", dir);
    }
}
