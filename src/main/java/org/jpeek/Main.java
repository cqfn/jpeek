/*
 * The MIT License (MIT)
 *
 * SPDX-FileCopyrightText: Copyright (c) 2017-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.jpeek;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.converters.FileConverter;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;

/**
 * Main entry point.
 *
 * <p>There is no thread-safety guarantee.
 *
 * @since 0.1
 * @checkstyle JavadocVariableCheck (500 lines)
 */
public final class Main {

    @Parameter(
        names = "--help",
        help = true,
        description = "Print usage options"
    )
    private boolean help;

    @Parameter(
        names = { "--sources", "-s" },
        converter = FileConverter.class,
        required = true,
        description = "Directory with .class files"
    )
    private File sources;

    @Parameter(
        names = { "--target", "-t" },
        converter = FileConverter.class,
        required = true,
        description = "Output directory"
    )
    private File target;

    @Parameter(
        names = "--include-ctors",
        description = "Include constructors into all formulas"
    )
    private boolean ctors;

    @Parameter(
        names = "--include-static-methods",
        description = "Include static methods into all formulas"
    )
    private boolean statics;

    @Parameter(
        names = "--include-private-methods",
        description = "Include private methods into all formulas")
    private boolean privates;

    @SuppressWarnings("PMD.ImmutableField")
    @Parameter(
        names = "--metrics",
        description = "Comma-separated list of metrics to include"
    )
    private String metrics;

    @Parameter(
        names = "--overwrite",
        // @checkstyle LineLength (1 line)
        description = "Overwrite the target directory if it exists (otherwise an error is raised)"
    )
    private boolean overwrite;

    @Parameter(
        names = "--quiet",
        description = "Turn logging off"
    )
    private boolean quiet;

    /**
     * Ctor.
     */
    private Main() {
        this.metrics = "LCOM5,NHD,MMAC,SCOM,CAMC";
    }

    /**
     * Main Java entry point.
     * @param args Command line args
     * @throws IOException If fails
     */
    public static void main(final String... args) throws IOException {
        final Main main = new Main();
        final JCommander jcmd = JCommander.newBuilder()
            .addObject(main)
            .build();
        jcmd.parse(args);
        main.run(jcmd);
    }

    /**
     * Run it.
     * @param jcmd The command line opts
     * @throws IOException If fails
     */
    private void run(final JCommander jcmd) throws IOException {
        if (this.help) {
            jcmd.usage();
            return;
        }
        if (this.overwrite && this.sources.equals(this.target)) {
            throw new IllegalArgumentException(
                "Invalid paths - can't be equal if overwrite option is set."
            );
        }
        final ConsoleAppender console = this.buildConsoleAppender();
        final Map<String, Object> params = this.buildParameters();
        new App(
            this.sources.toPath(),
            new FileTarget(
                this.target,
                this.overwrite
            ).toPath(),
            params
        ).analyze();
        if (!this.quiet) {
            Logger.getRootLogger().removeAppender(console);
        }
    }

    /**
     * Prepare application parameters based on configuration and metrics.
     * @return A {@link Map} filled with parameters.
     */
    private Map<String, Object> buildParameters() {
        final Map<String, Object> params = new HashMap<>(0);
        if (this.ctors) {
            params.put("include-ctors", 1);
        }
        if (this.statics) {
            params.put("include-static-methods", 1);
        }
        if (this.privates) {
            params.put("include-private-methods", 1);
        }
        for (final String metric : this.metrics.split(",")) {
            if (!metric.matches("[A-Z]+[0-9]?")) {
                throw new IllegalArgumentException(
                    String.format("Invalid metric name: '%s'", metric)
                );
            }
            params.put(metric, true);
        }
        return params;
    }

    /**
     * Prepare {@link ConsoleAppender} based on configuration.
     * @return Configured {@link ConsoleAppender}.
     */
    private ConsoleAppender buildConsoleAppender() {
        final ConsoleAppender console = new ConsoleAppender();
        if (!this.quiet) {
            console.setLayout(new PatternLayout("%m%n"));
            console.activateOptions();
            Logger.getRootLogger().addAppender(console);
        }
        return console;
    }
}
