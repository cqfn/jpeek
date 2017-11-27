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
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;

/**
 * Class reader for the ASM library.
 *
 * <p>It encapsulates logic of creation of {@link ClassReader}
 * and doesn't throw checked {@link Exception}.</p>
 *
 * @author Sergey Karazhenets (sergeykarazhenets@gmail.com)
 * @version $Id$
 * @since 0.13
 */
public final class JpClassReader implements Scalar<ClassReader> {

    /**
     * Byte code of the given class.
     */
    private final Scalar<byte[]> clbytecode;

    /**
     * {@link ClassVisitor} for the class byte code
     * {@link JpClassReader#clbytecode}.
     */
    private final Scalar<ClassVisitor> clvisitor;

    /**
     * Ctor.
     *
     * @param clbytecode Byte code of the class.
     * @param clvisitor Class visitor for the given class byte code.
     */
    public JpClassReader(
        final Scalar<byte[]> clbytecode,
        final Scalar<ClassVisitor> clvisitor
    ) {
        this.clbytecode = clbytecode;
        this.clvisitor = clvisitor;
    }

    @Override
    public ClassReader value() {
        return new UncheckedScalar<>(
            () -> {
                final ClassReader clrdr = new ClassReader(
                    this.clbytecode.value()
                );
                clrdr.accept(this.clvisitor.value(), 0);
                return clrdr;
            }).value();
    }
}
