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
package org.jpeek.metrics;

import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import javassist.CannotCompileException;
import javassist.CtClass;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

/**
 * Methods of a class.
 *
 * <p>There is no thread-safety guarantee.
 *
 * @author Yegor Bugayenko (yegor256@gmail.com)
 * @version $Id$
 * @see <a href="http://www.pitt.edu/~ckemerer/CK%20research%20papers/MetricForOOD_ChidamberKemerer94.pdf">A metrics suite for object oriented design</a>
 * @since 0.18
 * @checkstyle AbbreviationAsWordInNameCheck (5 lines)
 * @checkstyle ClassDataAbstractionCouplingCheck (500 lines)
 */
public final class Methods implements Iterable<Collection<String>> {

    /**
     * The class.
     */
    private final CtClass source;

    /**
     * Ctor.
     * @param cls The class
     */
    public Methods(final CtClass cls) {
        this.source = cls;
    }

    @Override
    @SuppressWarnings({ "PMD.UseObjectForClearerAPI", "PMD.UseVarargs" })
    public Iterator<Collection<String>> iterator() {
        final ClassReader reader;
        try {
            reader = new ClassReader(this.source.toBytecode());
        } catch (final IOException | CannotCompileException ex) {
            throw new IllegalStateException(ex);
        }
        final Collection<Collection<String>> methods = new LinkedList<>();
        reader.accept(
            // @checkstyle AnonInnerLengthCheck (50 lines)
            new ClassVisitor(Opcodes.ASM6) {
                @Override
                // @checkstyle ParameterNumberCheck (5 lines)
                public MethodVisitor visitMethod(final int access,
                    final String mtd, final String desc,
                    final String signature, final String[] exceptions) {
                    super.visitMethod(access, mtd, desc, signature, exceptions);
                    final Collection<String> attrs = new HashSet<>(0);
                    methods.add(attrs);
                    return new MethodVisitor(Opcodes.ASM6) {
                        @Override
                        // @checkstyle ParameterNumberCheck (5 lines)
                        public void visitFieldInsn(final int opcode,
                            final String owner, final String attr,
                            final String details) {
                            super.visitFieldInsn(opcode, owner, attr, details);
                            attrs.add(attr);
                        }
                    };
                }
            },
            0
        );
        return methods.iterator();
    }

}
