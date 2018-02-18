/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2017-2018 Yegor Bugayenko
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
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
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
 * @author Yegor Bugayenko (yegor256@gmail.com)
 * @version $Id$
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
        JCommander.newBuilder()
            .addObject(main)
            .build()
            .parse(args);
        main.run();
    }

    /**
     * Run it.
     * @throws IOException If fails
     * @todo #104:30min The logic for overwriting the target directory is very
     *  procedural and needs to be refactored. Suggestion: 'target' should be
     *  its own animated object where the logic is triggered in its 'toPath'
     *  method.
     */
    private void run() throws IOException {
        final ConsoleAppender console = new ConsoleAppender();
        if (!this.quiet) {
            console.setLayout(new PatternLayout("%m%n"));
            console.activateOptions();
            Logger.getRootLogger().addAppender(console);
        }
        if (this.target.exists()) {
            if (this.overwrite) {
                Main.deleteDir(this.target);
            } else {
                throw new IllegalStateException(
                    String.format(
                        "Directory/file already exists: %s",
                        this.target.getAbsolutePath()
                    )
                );
            }
        }
        final Map<String, Object> params = new HashMap<>(0);
        if (this.ctors) {
            params.put("include-ctors", 1);
        }
        if (this.statics) {
            params.put("include-static-methods", 1);
        }
        for (final String metric : this.metrics.split(",")) {
            if (!metric.matches("[A-Z]+[0-9]?")) {
                throw new IllegalArgumentException(
                    String.format("Invalid metric name: '%s'", metric)
                );
            }
            params.put(metric, true);
        }
        new App(this.sources.toPath(), this.target.toPath(), params).analyze();
        if (!this.quiet) {
            Logger.getRootLogger().removeAppender(console);
        }
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
