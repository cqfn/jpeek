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
import javassist.CtClass;
import org.cactoos.iterable.Filtered;
import org.cactoos.iterable.LengthOf;
import org.cactoos.iterable.Mapped;
import org.cactoos.iterable.Reduced;
import org.cactoos.scalar.UncheckedScalar;
import org.jpeek.Base;
import org.jpeek.Metric;
import org.jpeek.asm.CtClassByteCode;
import org.jpeek.asm.JpClassReader;
import org.jpeek.asm.JpClassVisitor;
import org.jpeek.asm.JpMethodVisitor;
import org.jpeek.asm.func.OnFieldInsnOwnedByClass;
import org.jpeek.asm.func.OnNotCtor;
import org.jpeek.asm.func.OnNotGenField;
import org.jpeek.asm.func.OnNotGenFieldInsn;
import org.jpeek.asm.func.OnNotGenMethod;
import org.jpeek.asm.func.WithoutFieldVisitor;
import org.jpeek.metrics.JavassistClasses;
import org.jpeek.metrics.Summary;
import org.xembly.Directive;

/**
 * Lack of Cohesion Of Methods [Henderson & Sellers] (LCOM5).
 *
 * <p>This metric shows a ratio between number of methods which
 * use fields of a class and number of all methods in a class.
 * Better when all methods use all fields or when class has
 * number of methods greater than number of fields and all of
 * these methods use all fields. These one classes have higher
 * cohesion against classes where each method use only one
 * field of a class or doesn't use any field.</p>
 *
 * <p>The metric value ranges between 0 and any possible number.
 * The closer to 0 value represents higher cohesion.</p>
 *
 * @author Sergey Karazhenets (sergeykarazhenets@gmail.com)
 * @version $Id$
 * @see <a href="http://waset.org/publications/5239/a-design-based-cohesion-metric-for-object-oriented-classes">
 *   A Design-Based Cohesion Metric for Object-Oriented Classes</a>
 * @see <a href="http://www.math.md/files/csjm/v25-n1/v25-n1-(pp44-74).pdf">
 *   Class Cohesion Metrics for Software Engineering: A Critical Review</a>
 * @see <a href="https://www.google.by/url?sa=t&rct=j&q=&esrc=s&source=web&cd=3&ved=0ahUKEwjh9PK4nZ7XAhVlEpoKHRMTAS0QFgg2MAI&url=http%3A%2F%2Fwww.ece.rutgers.edu%2F~marsic%2Fbooks%2FSE%2Finstructor%2Fslides%2Flec-16%2520Metrics-Cohesion.ppt&usg=AOvVaw3967dvApLWjTkj6-MBEnkS">
 *   Lecture 16: Class Cohesion Metrics</a>
 * @since 0.13
 * @checkstyle AbbreviationAsWordInNameCheck (5 lines)
 * @checkstyle ClassDataAbstractionCouplingCheck (500 lines)
 */
public final class LCOM5 implements Metric {

    /**
     * The base.
     */
    private final Base base;

    /**
     * Ctor.
     * @param bse The base
     */
    public LCOM5(final Base bse) {
        this.base = bse;
    }

    @Override
    public Iterable<Directive> xembly() throws IOException {
        return new JavassistClasses(
            this.base, LCOM5::cohesion
        ).xembly();
    }

    /**
     * Calculates LCOM5 metric for a single Java class.
     *
     * @param ctc The .class file.
     * @return Metrics.
     */
    private static Iterable<Directive> cohesion(final CtClass ctc) {
        final List<String> fields = new LinkedList<>();
        final List<Collection<String>> methods = new LinkedList<>();
        new JpClassReader(
            new CtClassByteCode(ctc),
            new JpClassVisitor(
                new OnNotGenField(
                    new WithoutFieldVisitor(
                        (access, name, desc, signature, value)
                            -> fields.add(name)
                    )
                ),
                new OnNotGenMethod(
                    new OnNotCtor(
                        (access, name, desc, signature, exceptions) -> {
                            final Set<String> flds = new HashSet<>(0);
                            methods.add(flds);
                            return new JpMethodVisitor(
                                new OnNotGenFieldInsn(
                                    new OnFieldInsnOwnedByClass(
                                        ctc.getName(),
                                        (opcode, owner, fname, fdesc)
                                            -> flds.add(fname)
                                    )
                                )
                            );
                        }
                    )
                )
        )).value();
        final int fieldsum = new UncheckedScalar<>(
            new Reduced<>(
                0, Integer::sum,
                new Mapped<>(
                    f -> new LengthOf(
                        new Filtered<>(m -> m.contains(f), methods)
                    ).value(),
                    fields
                )
            )
        ).value();
        final double cohesion;
        if (fields.isEmpty() || methods.size() == 1 && fieldsum == 0) {
            cohesion = 1.0d;
        } else if (methods.isEmpty() || methods.size() == 1 && fieldsum > 0) {
            cohesion = 0.0d;
        } else {
            cohesion = 0.0d + (fieldsum - methods.size() * fields.size())
                / (fields.size() * (1.0d - methods.size()));
        }
        return new Summary(cohesion)
            .with("fieldsum", fieldsum)
            .with("fields", fields.size())
            .with("methods", methods.size());
    }
}
