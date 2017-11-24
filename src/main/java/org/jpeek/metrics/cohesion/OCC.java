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
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import javassist.CannotCompileException;
import javassist.CtClass;
import javassist.CtField;
import javassist.CtMethod;
import org.jpeek.Base;
import org.jpeek.Metric;
import org.jpeek.metrics.JavassistClasses;
import org.jpeek.metrics.Summary;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.xembly.Directive;

/**
 * Optimistic Class Cohesion (OCC).
 *
 * Maximum value of percentage of methods to be reachable by some method,
 * on the weak-connection graph.
 *
 * <p>The metric value ranges between 0 and 1.0. A value of
 * 1.0 represents minimum cohesion and 0 represents
 * a completely cohesive class.</p>
 *
 * <p>There is no thread-safety guarantee.
 *
 * @author Vseslav Sekorin (vssekorin@gmail.com)
 * @version $Id$
 * @see <a href="https://www.researchgate.net/publication/268046583_A_Proposal_of_Class_Cohesion_Metrics_Using_Sizes_of_Cohesive_Parts">A Proposal of Class Cohesion Metrics Using Sizes of Cohesive Parts</a>
 * @since 0.4
 * @checkstyle AbbreviationAsWordInNameCheck (5 lines)
 * @checkstyle ParameterNumberCheck (500 lines)
 */
@SuppressWarnings("PMD.TooManyMethods")
public final class OCC implements Metric {

    /**
     * The base.
     */
    private final Base base;

    /**
     * Ctor.
     * @param bse The base
     */
    public OCC(final Base bse) {
        this.base = bse;
    }

    @Override
    public Iterable<Directive> xembly() throws IOException {
        return new JavassistClasses(
            this.base, OCC::cohesion
        ).xembly();
    }

    /**
     * Calculate OCC metric for a single Java class.
     *
     * @param ctc The .class file
     * @return Metrics
     */
    private static Iterable<Directive> cohesion(final CtClass ctc) {
        final List<String> ctmethods = Arrays.stream(ctc.getDeclaredMethods())
            .map(CtMethod::getName)
            .collect(Collectors.toList());
        final int number = ctmethods.size();
        final double result;
        if (number == 1) {
            result = 0;
        } else {
            result = maxRw(ctc, ctmethods) / (double) (number - 1);
        }
        return new Summary(result)
            .with("methods", ctmethods.size())
            .with("number", number);
    }

    /**
     * Maximum number of methods which are reachable.
     *
     * @param ctc The .class file
     * @param ctmethods Methods
     * @return The number
     */
    @SuppressWarnings({"PMD.UseVarargs", "PMD.UseObjectForClearerAPI"})
    private static double maxRw(
        final CtClass ctc, final List<String> ctmethods) {
        final ClassReader reader;
        try {
            reader = new ClassReader(ctc.toBytecode());
        } catch (final IOException | CannotCompileException ex) {
            throw new IllegalStateException(ex);
        }
        final Map<String, Set<String>> vars = new HashMap<>();
        final Map<String, Set<String>> methods = new HashMap<>();
        final List<String> ctfields = Arrays.stream(ctc.getDeclaredFields())
            .map(CtField::getName)
            .collect(Collectors.toList());
        reader.accept(
            //@checkstyle AnonInnerLengthCheck (50 lines)
            new ClassVisitor(Opcodes.ASM6) {
                @Override
                public MethodVisitor visitMethod(
                    final int access, final String mtd, final String desc,
                    final String signature, final String[] exceptions) {
                    super.visitMethod(access, mtd, desc, signature, exceptions);
                    final Set<String> attrs = new HashSet<>();
                    final Set<String> names = new HashSet<>();
                    vars.put(mtd, attrs);
                    methods.put(mtd, names);
                    return new MethodVisitor(Opcodes.ASM6) {
                        @Override
                        public void visitFieldInsn(
                            final int opcode, final String owner,
                            final String attr, final String details) {
                            super.visitFieldInsn(opcode, owner, attr, details);
                            if (ctfields.contains(attr)) {
                                attrs.add(attr);
                            }
                        }
                        @Override
                        public void visitMethodInsn(
                            final int opcode, final String owner,
                            final String name, final String desc,
                            final boolean itf) {
                            super.visitMethodInsn(
                                opcode, owner, name, desc, itf
                            );
                            if (!name.equals(mtd) && ctmethods.contains(name)) {
                                names.add(name);
                            }
                        }
                    };
                }
            },
            0
        );
        methods.forEach(
            (key, value) -> value.forEach(
                item -> vars.get(key).addAll(vars.get(item))
            )
        );
        final boolean[][] adjacency = adjacencyMatrix(vars, ctmethods);
        final int number = ctmethods.size();
        final boolean[][] reachabilty = reachabilityMatrix(adjacency, number);
        return maxWays(reachabilty, number);
    }

    /**
     * Adjacency matrix.
     *
     * @param vars Variables
     * @param methods Methods
     * @return Result
     */
    private static boolean[][] adjacencyMatrix(
        final Map<String, Set<String>> vars, final List<String> methods) {
        final int number = methods.size();
        final boolean[][] result = new boolean[number][number];
        for (int row = 0; row < number; ++row) {
            for (int col = 0; col < number; ++col) {
                result[row][col] = hasWeakConnection(
                    vars.get(methods.get(row)),
                    vars.get(methods.get(col))
                );
            }
        }
        return result;
    }

    /**
     * Reachability matrix.
     *
     * @param adjacency Adjacency matrix
     * @param number Number of methods
     * @return Result
     */
    private static boolean[][] reachabilityMatrix(
        final boolean[][] adjacency, final int number) {
        boolean[][] result = new boolean[number][number];
        boolean[][] current = new boolean[number][number];
        for (int ind = 0; ind < number; ++ind) {
            System.arraycopy(adjacency[ind], 0, result[ind], 0, number);
            System.arraycopy(adjacency[ind], 0, current[ind], 0, number);
        }
        for (int ind = 2; ind <= number; ++ind) {
            current = multiply(current, adjacency, number);
            result = disjunction(current, result, number);
        }
        return result;
    }

    /**
     * Check weak-connection of two method.
     *
     * @param first First method's vars
     * @param second Second method's vars
     * @return Result
     */
    private static boolean hasWeakConnection(
        final Set<?> first, final Set<?> second) {
        boolean result = false;
        for (final Object item : first) {
            if (second.contains(item)) {
                result = true;
                break;
            }
        }
        return result;
    }

    /**
     * Multiply of two boolean matrix.
     *
     * @param first The first matrix
     * @param second The second matrix
     * @param size Matrix size
     * @return Result matrix
     */
    private static boolean[][] multiply(
        final boolean[][] first, final boolean[][] second, final int size) {
        final boolean[][] result = new boolean[size][size];
        //@checkstyle NestedForDepthCheck (10 lines)
        for (int row = 0; row < size; ++row) {
            for (int col = 0; col < size; ++col) {
                for (int inner = 0; inner < size; ++inner) {
                    result[row][col] = result[row][col]
                        || first[row][inner] && second[inner][col];
                }
            }
        }
        return result;
    }

    /**
     * Disjunction of two boolean matrix.
     *
     * @param first The first matrix
     * @param second The second matrix
     * @param size Matrix size
     * @return Result matrix
     */
    private static boolean[][] disjunction(
        final boolean[][] first, final boolean[][] second, final int size) {
        final boolean[][] result = new boolean[size][size];
        for (int row = 0; row < size; ++row) {
            for (int col = 0; col < size; ++col) {
                result[row][col] = first[row][col] || second[row][col];
            }
        }
        return result;
    }

    /**
     * The maximum number of paths in the reachability matrix.
     *
     * @param array Matrix
     * @param size Size of matrix
     * @return The number
     */
    private static int maxWays(final boolean[][] array, final int size) {
        int result = 0;
        for (int row = 0; row < size; ++row) {
            int current = 0;
            for (int col = 0; col < size; ++col) {
                if (array[row][col] && row != col) {
                    ++current;
                }
            }
            if (result < current) {
                result = current;
            }
        }
        return result;
    }
}
