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

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.Opcodes;

/**
 * Test cases for {@link OnNotGenField} function.
 *
 * @author Sergey Karazhenets (sergeykarazhenets@gmail.com)
 * @version $Id$
 * @since 0.13
 * @checkstyle JavadocMethodCheck (500 lines)
 */
public final class OnNotGenFieldTest {

    @Test
    public void doNotReactOnGeneratedField() throws Exception {
        final FieldVisitor fvis = new FieldVisitor(Opcodes.ASM6) { };
        MatcherAssert.assertThat(
            new OnNotGenField(
                (access, name, desc, signature, value) -> () -> fvis
            // @checkstyle MagicNumberCheck (1 line)
            ).apply(4112, "this$0", "LClassUsesNotOwnAttr;", null, null)
            .value(),
            Matchers.allOf(
                Matchers.notNullValue(),
                Matchers.not(Matchers.sameInstance(fvis))
            )
        );
    }

    @Test
    public void reactOnNotGeneratedField() throws Exception {
        final FieldVisitor fvis = new FieldVisitor(Opcodes.ASM6) { };
        MatcherAssert.assertThat(
            new OnNotGenField(
                (access, name, desc, signature, value) -> () -> fvis
            // @checkstyle MagicNumberCheck (1 line)
            ).apply(18, "input", "Ljava/nio/file/Path;", null, null)
            .value(),
            Matchers.sameInstance(fvis)
        );
    }
}
