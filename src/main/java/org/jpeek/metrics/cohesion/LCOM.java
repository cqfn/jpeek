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
package org.jpeek.metrics.cohesion;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import javassist.CannotCompileException;
import javassist.CtClass;
import org.jpeek.Base;
import org.jpeek.Metric;
import org.jpeek.metrics.Colors;
import org.jpeek.metrics.JavassistClasses;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.xembly.Directive;

/**
 * Lack of Cohesion in Methods (LCOM).
 *
 * <p>LCOM is calculated as the number of pairs of methods
 * operating on disjoint sets of instance variables,
 * reduced by the number of method pairs acting on at
 * least one shared instance variable.</p>
 *
 * <p>Say, there are 5 methods in a class. This means that there are 10
 * pairs of methods ({@code 5 * 4 / 2}). Now, we need to see how many of these
 * pairs are using at least one and the same attribute (Nonempty) and how many
 * of them are not using any similar attributes (Empty). Then, we
 * just do {@code LCOM = Empty - Nonempty}. The metric can be really big,
 * starting from zero and up to any possible number. The bigger the
 * value the least cohesive is the class. A perfect design would have
 * {@code LCOM=0}.</p>
 *
 * <p>There is no thread-safety guarantee.
 *
 * @author Yegor Bugayenko (yegor256@gmail.com)
 * @version $Id$
 * @see <a href="http://www.pitt.edu/~ckemerer/CK%20research%20papers/MetricForOOD_ChidamberKemerer94.pdf">A metrics suite for object oriented design</a>
 * @since 0.2
 * @checkstyle AbbreviationAsWordInNameCheck (5 lines)
 * @checkstyle ClassDataAbstractionCouplingCheck (500 lines)
 */
public final class LCOM implements Metric {

    /**
     * The base.
     */
    private final Base base;

    /**
     * Ctor.
     * @param bse The base
     */
    public LCOM(final Base bse) {
        this.base = bse;
    }

    @Override
    public Iterable<Directive> xembly() throws IOException {
        return new JavassistClasses(
            this.base, LCOM::cohesion,
            // @checkstyle MagicNumberCheck (1 line)
            new Colors(100.0d, 5.0d)
        ).xembly();
    }

    /**
     * Calculate LCOM metric for a single Java class.
     * @param ctc The .class file
     * @return Metrics
     * @checkstyle ParameterNumberCheck (100 lines)
     */
    @SuppressWarnings({ "PMD.UseObjectForClearerAPI", "PMD.UseVarargs" })
    private static double cohesion(final CtClass ctc) {
        final ClassReader reader;
        try {
            reader = new ClassReader(ctc.toBytecode());
        } catch (final IOException | CannotCompileException ex) {
            throw new IllegalStateException(ex);
        }
        final List<Collection<String>> methods = new LinkedList<>();
        reader.accept(
            new ClassVisitor(Opcodes.ASM6) {
                @Override
                public MethodVisitor visitMethod(final int access,
                    final String mtd, final String desc,
                    final String signature, final String[] exceptions) {
                    super.visitMethod(access, mtd, desc, signature, exceptions);
                    final Collection<String> attrs = new HashSet<>(0);
                    methods.add(attrs);
                    return new MethodVisitor(Opcodes.ASM6) {
                        @Override
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
        int empty = 0;
        int nonempty = 0;
        for (int idx = 0; idx < methods.size(); ++idx) {
            for (int jdx = idx + 1; jdx < methods.size(); ++jdx) {
                if (Collections.disjoint(methods.get(idx), methods.get(jdx))) {
                    ++empty;
                } else {
                    ++nonempty;
                }
            }
        }
        return Math.max(0.0d, (double) (empty - nonempty));
    }

}
