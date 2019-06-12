/**
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
package org.jpeek.web;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import org.hamcrest.core.IsEqual;
import org.junit.Test;
import org.llorllale.cactoos.matchers.Assertion;

/**
 * Test case for {@link Pages}.
 * @author Nikita Puzankov (humb1t@yandex.ru)
 * @version $Id$
 * @since 0.31
 * @checkstyle JavadocMethodCheck (500 lines)
 */
public final class PagesTest {

    /**
     * Simple test.
     */
    @Test
    public void testApply() throws IOException {
        final Path temp = Files.createTempDirectory("");
        temp.resolve("a").toFile().mkdirs();
        final String path = "a/z.class";
        Files.write(
            temp.resolve(path), "".getBytes(),
            StandardOpenOption.CREATE_NEW
        );
        new Assertion<>(
            "There is no byte available in body - should return -1",
            () -> new Pages(temp).apply(path).body().read(),
            new IsEqual<>(-1)
        ).affirm();
    }
}
