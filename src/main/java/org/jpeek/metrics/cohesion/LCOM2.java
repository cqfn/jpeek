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
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
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
 * Lack of Cohesion in Methods 2 (LCOM2).
 *
 * <p>Consider a class C with methods M1, M2,….., Mn. Let {Ii} = set of
 * instance variables used by method Mi. There are n such sets, i.e., {1i},
 * {I2},….., {In}. Let  P = { (Ii, Ij ) | Ii ∩ Ij = Ø} and Q = {(Ii, Ij ) |
 * Ii ∩ Ij ≠ Ø}. If all n sets {1i}, {I2},….., {In} are Ø then let P = Ø.
 * </p>
 *
 * <p>There is no thread-safety guarantee.</p>
 *
 * @author Mehmet Yildirim (memoyil@gmail.com)
 * @version $Id$
 * @see <a href="http://www.pitt.edu/~ckemerer/CK%20research%20papers/MetricForOOD_ChidamberKemerer94.pdf">A metrics suite for object oriented design</a>
 * @since 0.2
 * @checkstyle AbbreviationAsWordInNameCheck (5 lines)
 * @checkstyle ClassDataAbstractionCouplingCheck (500 lines)
 */
public final class LCOM2 implements Metric {

    /**
     * The base.
     */
    private final Base base;

    /**
     * Ctor.
     *
     * @param bse The base
     */
    public LCOM2(final Base bse) {
        this.base = bse;
    }

    @Override
    public Iterable<Directive> xembly() throws IOException {
        return new JavassistClasses(
            this.base, LCOM2::cohesion,
            // @checkstyle MagicNumberCheck (1 line)
            new Colors(1.0d, 0.0d)
        ).xembly();
    }

    /**
     * Calculate LCOM2 metric for a single Java class.
     *
     * @param ctc The .class file
     * @return Metrics
     * @checkstyle ParameterNumberCheck (100 lines)
     * @checkstyle AnonInnerLengthCheck (25 lines)
     */
    @SuppressWarnings({"PMD.UseObjectForClearerAPI", "PMD.UseVarargs"})
    private static double cohesion(final CtClass ctc) {
        final ClassReader reader;
        try {
            reader = new ClassReader(ctc.toBytecode());
        } catch (final IOException | CannotCompileException ex) {
            throw new IllegalStateException(ex);
        }
        final List<Collection<String>> methods = new LinkedList<>();
        final Set<String> attrs = new HashSet<>();
        reader.accept(
            new ClassVisitor(Opcodes.ASM6) {
                @Override
                public MethodVisitor visitMethod(final int access, final
                    String mtd, final String desc, final String signature, final
                    String[] exceptions) {
                    super.visitMethod(access, mtd, desc, signature, exceptions);
                    final Collection<String> methodattrs = new HashSet<>(0);
                    methods.add(methodattrs);
                    return new MethodVisitor(Opcodes.ASM6) {
                        @Override
                        public void visitFieldInsn(final int opcode,
                            final String owner, final String attr,
                            final String details) {
                            super.visitFieldInsn(opcode, owner, attr, details);
                            attrs.add(attr);
                            methodattrs.add(attr);
                        }
                    };
                }
            },
            0
        );
        int sum = 0;
        int result = 0;
        for (final String attr : attrs) {
            for (final Collection<String> methodattrs : methods) {
                if (methodattrs.contains(attr)) {
                    ++sum;
                }
            }
        }
        if (!attrs.isEmpty() && !methods.isEmpty()) {
            result = 1 - sum / (attrs.size() * methods.size());
        }
        return result;
    }

}
