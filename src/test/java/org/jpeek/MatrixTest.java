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

import com.jcabi.matchers.XhtmlMatchers;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.cactoos.text.TextOf;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.llorllale.cactoos.matchers.Assertion;

/**
 * Test case for {@link Matrix}.
 * @author Yegor Bugayenko (yegor256@gmail.com)
 * @version $Id$
 * @since 0.8
 * @checkstyle JavadocMethodCheck (500 lines)
 * @checkstyle JavadocTagsCheck (500 lines)
 */
public final class MatrixTest {
    /**
     * Xml file content as a string.
     */
    private String xml;

    @BeforeEach
    public void setUp() throws IOException {
        final Path output = Files.createTempDirectory("").resolve("x2");
        final Path input = Paths.get(".");
        new App(input, output).analyze();
        this.xml = new TextOf(output.resolve("matrix.xml")).asString();
    }

    @Test
    public void createsMatrixXml() {
        new Assertion<>(
            "Must create matrix.xml",
            XhtmlMatchers.xhtml(
                this.xml
            ),
            XhtmlMatchers.hasXPaths("/matrix/classes")
        ).affirm();
    }

    @Test
    public void xmlHasSchema() {
        new Assertion<>(
            "The XML Schema is invalid",
            XhtmlMatchers.xhtml(
                this.xml
            ),
            XhtmlMatchers.hasXPaths(
                "/matrix[@xsi:noNamespaceSchemaLocation='xsd/matrix.xsd']"
            )
        ).affirm();
    }

}
