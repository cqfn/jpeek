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
package org.jpeek.asm;

import org.cactoos.Scalar;
import org.jpeek.asm.func.Proc4;
import org.jpeek.asm.func.UncheckedProc4;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

/**
 * Method visitor for the ASM library.
 *
 * <p>It encapsulates logic of creation of {@link MethodVisitor}.</p>
 *
 * @author Sergey Karazhenets (sergeykarazhenets@gmail.com)
 * @version $Id$
 * @since 0.13
 */
public final class JpMethodVisitor implements Scalar<MethodVisitor> {

    /**
     * Procedure that will be executed when method visitor will visit
     * field instruction inside a method.
     */
    private final Proc4<Integer, String, String, String> onfieldinsn;

    /**
     * Ctor.
     *
     * @param onfieldinsn Procedure to execute on field instruction
     *  visit event.
     */
    public JpMethodVisitor(
        final Proc4<Integer, String, String, String> onfieldinsn
    ) {
        this.onfieldinsn = onfieldinsn;
    }

    @Override
    public MethodVisitor value() throws Exception {
        return new MethodVisitor(Opcodes.ASM6) {
            @Override
            // @checkstyle ParameterNumberCheck (1 line)
            public void visitFieldInsn(final int opcode, final String owner,
                final String name, final String desc
            ) {
                new UncheckedProc4<>(JpMethodVisitor.this.onfieldinsn)
                    .exec(opcode, owner, name, desc);
            }
        };
    }
}
