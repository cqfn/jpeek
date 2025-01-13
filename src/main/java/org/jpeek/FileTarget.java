/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2017-2025 Yegor Bugayenko
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
            if (this.overwrite) {
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
