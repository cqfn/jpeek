/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2017 Yegor Bugayenko
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
package org.jpeek.metrics.cohesion;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.cactoos.collection.Joined;
import org.cactoos.io.LengthOf;
import org.cactoos.io.ResourceOf;
import org.cactoos.io.TeeInput;
import org.cactoos.iterable.Mapped;
import org.cactoos.list.ListOf;
import org.cactoos.scalar.And;
import org.cactoos.scalar.IoCheckedScalar;
import org.jpeek.Base;
import org.jpeek.DefaultBase;

/**
 * Fake base for tests.
 * @author Yegor Bugayenko (yegor256@gmail.com)
 * @version $Id$
 * @since 0.2
 * @checkstyle AbbreviationAsWordInNameCheck (5 lines)
 * @checkstyle JavadocMethodCheck (500 lines)
 * @checkstyle ClassDataAbstractionCouplingCheck (500 lines)
 */
public final class FakeBase implements Base {

    /**
     * Classes to use.
     */
    private final Iterable<String> classes;

    /**
     * Ctor.
     * @param list List of file names
     */
    public FakeBase(final String... list) {
        this(new ListOf<>(list));
    }

    /**
     * Ctor.
     * @param list List of file names
     */
    public FakeBase(final Iterable<String> list) {
        this.classes = list;
    }

    @Override
    public Iterable<Path> files() throws IOException {
        final Path temp = Files.createTempDirectory("jpeek");
        final Iterable<String> sources = new Mapped<>(
            this.classes,
            cls -> String.format("%s.java", cls)
        );
        new IoCheckedScalar<>(
            new And(
                sources,
                java -> {
                    new LengthOf(
                        new TeeInput(
                            new ResourceOf(String.format("org/jpeek/%s", java)),
                            temp.resolve(java)
                        )
                    ).value();
                }
            )
        ).value();
        final Process process = new ProcessBuilder()
            .redirectOutput(ProcessBuilder.Redirect.INHERIT)
            .redirectInput(ProcessBuilder.Redirect.INHERIT)
            .redirectError(ProcessBuilder.Redirect.INHERIT)
            .directory(temp.toFile())
            .command(
                new ListOf<>(
                    new Joined<String>(
                        new ListOf<>("javac"),
                        sources
                    )
                )
            )
            .start();
        try {
            assert process.waitFor() == 0;
        } catch (final InterruptedException ex) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException(ex);
        }
        return new DefaultBase(temp).files();
    }

}
