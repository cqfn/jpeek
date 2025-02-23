/*
 * The MIT License (MIT)
 *
 * SPDX-FileCopyrightText: Copyright (c) 2017-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.jpeek;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.cactoos.io.ResourceOf;
import org.cactoos.io.TeeInput;
import org.cactoos.iterable.Joined;
import org.cactoos.iterable.Mapped;
import org.cactoos.list.ListOf;
import org.cactoos.scalar.And;
import org.cactoos.scalar.IoChecked;
import org.cactoos.scalar.LengthOf;

/**
 * Fake base for tests.
 * @since 0.2
 * @checkstyle JavadocMethodCheck (500 lines)
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
            cls -> String.format("%s.java", cls),
            this.classes
        );
        new IoChecked<>(
            new And(
                java -> {
                    new LengthOf(
                        new TeeInput(
                            new ResourceOf(
                                String.format("org/jpeek/samples/%s", java)
                            ),
                            temp.resolve(java)
                        )
                    ).value();
                    return true;
                },
                sources
            )
        ).value();
        if (sources.iterator().hasNext()) {
            final int exit;
            try {
                exit = new ProcessBuilder()
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
                    .start()
                    .waitFor();
            } catch (final InterruptedException ex) {
                Thread.currentThread().interrupt();
                throw new IllegalStateException(ex);
            }
            if (exit != 0) {
                throw new IllegalStateException(
                    String.format("javac failed with exit code %d", exit)
                );
            }
        }
        return new DefaultBase(temp).files();
    }

}
