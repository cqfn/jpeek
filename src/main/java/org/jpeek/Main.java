/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2017-2019 Yegor Bugayenko
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
        this.metrics = "CCM,LCOM,LCOM2,LCOM3,LCOM4,LCOM5,MMAC,NHD,TCC,LCC,PCC";
    }

    /**
     * Main Java entry point.
     * @param args Command line args
     * @throws IOException If fails
     */
    public static void main(final String... args) throws IOException {
        final Main main = new Main();
        JCommander.newBuilder()
            .addObject(main)
            .build()
            .parse(args);
        main.run();
    }

    /**
     * Run it.
     * @throws IOException If fails
     */
    private void run() throws IOException {
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
