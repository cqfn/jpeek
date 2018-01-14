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
package org.jpeek;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.converters.FileConverter;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

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

    /**
     * Ctor.
     */
    private Main() {
        // intentionally
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
        final Map<String, Object> params = new HashMap<>(0);
        if (this.ctors) {
            params.put("ctors", 1);
        }
        new App(this.sources.toPath(), this.target.toPath(), params).analyze();
    }

}
