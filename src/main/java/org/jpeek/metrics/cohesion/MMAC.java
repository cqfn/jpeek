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
import java.util.List;
import java.util.Set;
import javassist.CtBehavior;
import javassist.CtClass;
import org.cactoos.iterable.Filtered;
import org.cactoos.iterable.IterableOf;
import org.cactoos.iterable.Mapped;
import org.cactoos.list.ListOf;
import org.jpeek.Base;
import org.jpeek.Metric;
import org.jpeek.metrics.Colors;
import org.jpeek.metrics.JavassistClasses;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.signature.SignatureReader;
import org.objectweb.asm.signature.SignatureVisitor;
import org.xembly.Directive;

/**
 * Method-Method through Attributes Cohesion (MMAC).
 *
 * <p>The MMAC is the average cohesion of all pairs of methods.
 * In simple words this metric shows how much methods have the
 * same parameters or return types. When class has some number
 * of methods and most of them operate the same parameters it
 * assumes better. It looks like class contains overloaded
 * methods. Preferably when class has only one method with
 * parameters and/or return type and it assumes that class
 * do only one thing. Value of MMAC metric is better for these
 * one classes.</p>
 *
 * <p>Metric value is in interval [0, 1].
 * Value closer to 1 is better.</p>
 *
 * @author Sergey Karazhenets (sergeykarazhenets@gmail.com)
 * @version $Id$
 * @see <a href="http://www.math.md/files/csjm/v25-n1/v25-n1-(pp44-74).pdf">
 *    Class Cohesion Metrics for Software Engineering: A Critical Review</a>
 * @since 0.2
 * @checkstyle AbbreviationAsWordInNameCheck (5 lines)
 * @checkstyle ClassDataAbstractionCouplingCheck (500 lines)
 */
public final class MMAC implements Metric {

    /**
     * Source code base.
     */
    private final Base base;

    /**
     * Ctor.
     * @param bse The base.
     */
    public MMAC(final Base bse) {
        this.base = bse;
    }

    @Override
    public Iterable<Directive> xembly() throws IOException {
        return new JavassistClasses(
            this.base, MMAC::cohesion,
            // @checkstyle MagicNumberCheck (1 line)
            new Colors(0.15d, 0.85d)
        ).xembly();
    }

    /**
     * Calculates MMAC metric for a single Java class.
     *
     * @param ctc The .class file
     * @return MMAC metric
     * @checkstyle ReturnCountCheck (10 lines). This assert is suppressed
     *  because we have non one line formula for metric calculation.
     * @checkstyle TrailingCommentCheck (50 lines)
     * @checkstyle MethodBodyCommentsCheck (50 lines)
     */
    @SuppressWarnings(
        // These PMD asserts are suppressed because we have non one line
        // formula for metric calculation.
        {
            "PMD.OnlyOneReturn", "PMD.CyclomaticComplexity",
            "PMD.StdCyclomaticComplexity", "PMD.ModifiedCyclomaticComplexity"
        })
    private static double cohesion(final CtClass ctc) {
        final List<Collection<String>> methods = new ListOf<>(
            new Mapped<>(
                new Mapped<>(
                    new Filtered<>(
                        new IterableOf<>(ctc.getDeclaredMethods()),
                        // isn't lambda or other generated methods by compiler
                        m -> !m.getName().contains("$")
                    ),
                    CtBehavior::getSignature
                ),
                signature -> {
                    final Set<String> types = new HashSet<>();
                    new SignatureReader(signature).accept(
                        new SignatureVisitor(Opcodes.ASM6) {
                            @Override
                            public void visitClassType(final String name) {
                                super.visitClassType(name);
                                types.add(name);
                            }
                            @Override
                            public void visitBaseType(final char name) {
                                super.visitBaseType(name);
                                if ('V' != name) { // isn't void type
                                    types.add(String.valueOf(name));
                                }
                            }
                        }
                    );
                    return types;
                }
            )
        );
        if (methods.isEmpty()) {
            return 0.0d;
        }
        if (methods.size() == 1) {
            return 1.0d;
        }
        final Set<String> types = new HashSet<>();
        for (final Collection<String> method : methods) {
            types.addAll(method);
        }
        if (types.isEmpty()) {
            return 0.0d;
        }
        double sum = 0.0d;
        for (final String type : types) {
            int mcnt = 0;
            for (final Collection<String> method : methods) {
                if (method.contains(type)) {
                    ++mcnt;
                }
            }
            sum += mcnt * (mcnt - 1);
        }
        return sum
            / (types.size() * methods.size() * (methods.size() - 1));
    }
}
