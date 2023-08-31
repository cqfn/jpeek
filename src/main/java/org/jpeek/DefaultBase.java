/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2017-2023 Yegor Bugayenko
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
