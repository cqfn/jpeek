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
import org.cactoos.scalar.And;
import org.cactoos.scalar.Ternary;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

/**
 * Function that will be called on visiting class method
 * that isn't a constructor.
 *
 * <p>After compilation byte code of each class contains methods using for
 * class instantiating at runtime. These methods we call constructors.
 * Inside byte code all of them have special names {@code <init>} for regular
 * constructors or {@code <clinit>} for any static initialization blocks.
 * Use this one function to filter all methods of the class and to react only
 * on non constructor methods.</p>
 *
 * @author Sergey Karazhenets (sergeykarazhenets@gmail.com)
 * @version $Id$
 * @since 0.13
 */
public final class OnNotCtor implements
    Func5<Integer, String, String, String, String[], Scalar<MethodVisitor>> {

    /**
     * Function that will be called when class visitor will visit
     * non constructor method.
     */
    private final Func5<Integer, String, String, String, String[],
        Scalar<MethodVisitor>> func;

    /**
     * Ctor.
     * @param func Function to call on method visit event
     *  for non constructor method.
     */
    public OnNotCtor(
        final Func5<Integer, String, String, String, String[],
            Scalar<MethodVisitor>> func
    ) {
        this.func = func;
    }

    @Override
    @SuppressWarnings({"PMD.UseVarargs", "PMD.UseObjectForClearerAPI"})
    // @checkstyle ParameterNumberCheck (1 line)
    public Scalar<MethodVisitor> apply(final Integer access, final String name,
        final String desc, final String signature, final String[] exceptions
    ) throws Exception {
        return new Ternary<Scalar<MethodVisitor>>(
            new And(
                () -> !"<init>".equals(name),
                () -> !"<clinit>".equals(name)
            ),
            () -> this.func.apply(access, name, desc, signature, exceptions),
            () -> () -> new MethodVisitor(Opcodes.ASM6) { }
        ).value();
    }
}
