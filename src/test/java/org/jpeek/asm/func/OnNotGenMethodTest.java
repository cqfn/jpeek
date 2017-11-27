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
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

/**
 * Test cases for {@link OnNotGenMethod} function.
 *
 * @author Sergey Karazhenets (sergeykarazhenets@gmail.com)
 * @version $Id$
 * @since 0.13
 * @checkstyle JavadocMethodCheck (500 lines)
 */
public final class OnNotGenMethodTest {

    @Test
    public void doNotReactOnGeneratedMethod() throws Exception {
        final MethodVisitor mvis = new MethodVisitor(Opcodes.ASM6) { };
        MatcherAssert.assertThat(
            new OnNotGenMethod(
                (access, name, desc, signature, exceptions) -> () -> mvis
            ).apply(
                // @checkstyle MagicNumberCheck (1 line)
                4098, "lambda$apply$4",
                "()Lorg/cactoos/Scalar;", null,
                new String[]{"java/lang/Exception"}
            ).value(),
            Matchers.allOf(
                Matchers.notNullValue(),
                Matchers.not(Matchers.sameInstance(mvis))
            )
        );
    }

    @Test
    public void reactOnNotGeneratedMethod() throws Exception {
        final MethodVisitor mvis = new MethodVisitor(Opcodes.ASM6) { };
        MatcherAssert.assertThat(
            new OnNotGenMethod(
                (access, name, desc, signature, exceptions) -> () -> mvis
            // @checkstyle MagicNumberCheck (1 line)
            ).apply(
                1, "exec", "(Ljava/lang/Integer;)V",
                null, null
            ).value(),
            Matchers.sameInstance(mvis)
        );
    }
}
