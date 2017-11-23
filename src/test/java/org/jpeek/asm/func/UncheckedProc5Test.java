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
package org.jpeek.asm.func;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;

/**
 * Test cases for {@link UncheckedProc5}.
 *
 * @author Sergey Karazhenets (sergeykarazhenets@gmail.com)
 * @version $Id$
 * @since 0.13
 * @checkstyle JavadocMethodCheck (500 lines)
 */
public final class UncheckedProc5Test {

    @Test
    public void doNotThrowExceptionOnNormalExecution() {
        final List<Object> list = new ArrayList<>(0);
        MatcherAssert.assertThat(list, Matchers.empty());
        new UncheckedProc5<>(
            (fst, snd, trd, frt, fft) -> list.add(fst)
        ).exec("3", "4", "5", "6", "7");
        MatcherAssert.assertThat(list, Matchers.hasSize(1));
        MatcherAssert.assertThat(list, Matchers.hasItem("3"));
    }

    @Test(expected = IllegalStateException.class)
    public void throwIllegalStateExceptionOnInterruptedException() {
        new UncheckedProc5<>(
            (fst, snd, trd, frt, fft) -> { throw new InterruptedException(); }
        ).exec("3", "4", "5", "6", "7");
    }

    @Test(expected = IllegalStateException.class)
    public void throwIllegalStateExceptionOnCheckedException() {
        new UncheckedProc5<>(
            (fst, snd, trd, frt, fft) -> { throw new IOException(); }
        ).exec("3", "4", "5", "6", "7");
    }
}
