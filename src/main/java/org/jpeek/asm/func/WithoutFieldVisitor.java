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

import org.cactoos.Scalar;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.Opcodes;

/**
 * Function that will be called on visiting class field.
 * All functions calling on visiting class field event must
 * return instance of {@link FieldVisitor}. This one function
 * provides possibility to respond on class visiting event,
 * but returns an empty {@link FieldVisitor}.
 *
 * @author Sergey Karazhenets (sergeykarazhenets@gmail.com)
 * @version $Id$
 * @see OnNotGenField
 * @since 0.13
 */
public final class WithoutFieldVisitor implements
    Func5<Integer, String, String, String, Object, Scalar<FieldVisitor>> {

    /**
     * Procedure that will be executed when class visitor will visit
     * some field in the class.
     */
    private final Proc5<Integer, String, String, String, Object> proc;

    /**
     * Ctor.
     * @param proc Procedure to execute on field visit event.
     */
    public WithoutFieldVisitor(
        final Proc5<Integer, String, String, String, Object> proc
    ) {
        this.proc = proc;
    }

    @Override
    // @checkstyle ParameterNumberCheck (1 line)
    public Scalar<FieldVisitor> apply(final Integer access, final String name,
        final String desc, final String signature, final Object value
    ) throws Exception {
        this.proc.exec(access, name, desc, signature, value);
        return () -> new FieldVisitor(Opcodes.ASM6) { };
    }
}
