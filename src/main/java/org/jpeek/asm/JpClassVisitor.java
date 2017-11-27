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
import org.cactoos.scalar.UncheckedScalar;
import org.jpeek.asm.func.Func5;
import org.jpeek.asm.func.UncheckedFunc5;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

/**
 * Class visitor for the ASM library.
 *
 * <p>It encapsulates logic of creation of {@link ClassVisitor}.</p>
 *
 * @author Sergey Karazhenets (sergeykarazhenets@gmail.com)
 * @version $Id$
 * @since 0.13
 */
public final class JpClassVisitor implements Scalar<ClassVisitor> {

    /**
     * Function that will be called when class visitor will visit
     * some field of class.
     */
    private final Func5<Integer, String, String, String, Object,
        Scalar<FieldVisitor>> onfield;

    /**
     * Function that will be called when class visitor will visit
     * some method of class.
     */
    private final Func5<Integer, String, String, String, String[],
        Scalar<MethodVisitor>> onmethod;

    /**
     * Ctor.
     *
     * @param onfield Function to call on field visit event.
     * @param onmethod Function to call on method visit event.
     */
    public JpClassVisitor(
        final Func5<Integer, String, String, String, Object,
            Scalar<FieldVisitor>> onfield,
        final Func5<Integer, String, String, String, String[],
            Scalar<MethodVisitor>> onmethod
    ) {
        this.onfield = onfield;
        this.onmethod = onmethod;
    }

    @Override
    public ClassVisitor value() throws Exception {
        // @checkstyle AnonInnerLengthCheck (21 lines)
        return new ClassVisitor(Opcodes.ASM6) {
            @Override
            // @checkstyle ParameterNumberCheck (1 line)
            public FieldVisitor visitField(final int access, final String name,
                final String desc, final String signature, final Object value
            ) {
                return new UncheckedScalar<>(
                    new UncheckedFunc5<>(JpClassVisitor.this.onfield)
                        .apply(access, name, desc, signature, value)
                ).value();
            }
            @Override
            @SuppressWarnings({"PMD.UseVarargs", "PMD.UseObjectForClearerAPI"})
            // @checkstyle ParameterNumberCheck (1 line)
            public MethodVisitor visitMethod(final int access,
                final String name, final String desc, final String signature,
                final String[] exceptions
            ) {
                return new UncheckedScalar<>(
                    new UncheckedFunc5<>(JpClassVisitor.this.onmethod)
                        .apply(access, name, desc, signature, exceptions)
                ).value();
            }
        };
    }
}
