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
import javassist.CtClass;
import javassist.CtConstructor;
import javassist.CtMethod;
import javassist.Modifier;
import javassist.NotFoundException;
import org.cactoos.collection.Joined;
import org.cactoos.iterator.Mapped;
import org.jpeek.Base;
import org.jpeek.Metric;
import org.jpeek.metrics.Colors;
import org.jpeek.metrics.JavassistClasses;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.signature.SignatureReader;
import org.objectweb.asm.signature.SignatureVisitor;
import org.xembly.Directive;

/**
 * Cohesion Among Methods of Classes (CAMC).
 *
 * <p>In the CAMC metric, the cohesion in the methods of
 * a class is determined by the types of objects (parameter
 * access pattern of methods) that method’s take as input parameters.
 * The metric determines the overlap in the object types of
 * the methods parameter lists. The amount of overlap in object
 * types used by the methods of a class can be used to predict
 * the cohesion of the class.</p>
 *
 * <p>The metric value ranges between 0 and 1.0. A value of
 * 1.0 represents maximum cohesion and 0 represents
 * a completely un-cohesive class.</p>
 *
 * <p>There is no thread-safety guarantee.
 *
 * @author Yegor Bugayenko (yegor256@gmail.com)
 * @version $Id$
 * @see <a href="https://pdfs.semanticscholar.org/2709/1005bacefaee0242cf2643ba5efa20fa7c47.pdf">A class cohesion metric for object-oriented designs</a>
 * @since 0.1
 * @checkstyle AbbreviationAsWordInNameCheck (5 lines)
 * @checkstyle ClassDataAbstractionCouplingCheck (500 lines)
 */
public final class CAMC implements Metric {

    /**
     * The base.
     */
    private final Base base;

    /**
     * Ctor.
     * @param bse The base
     */
    public CAMC(final Base bse) {
        this.base = bse;
    }

    @Override
    public Iterable<Directive> xembly() throws IOException {
        return new JavassistClasses(
            this.base, CAMC::cohesion,
            // @checkstyle MagicNumberCheck (1 line)
            new Colors(0.15d, 0.35d, false)
        ).xembly();
    }

    /**
     * Calculate CAMC metric for a single Java class.
     * @param ctc The .class file
     * @return Metrics
     * @throws NotFoundException If fails
     */
    private static double cohesion(final CtClass ctc) throws NotFoundException {
        final Collection<Collection<String>> methods = CAMC.methods(ctc);
        final Collection<String> types = new HashSet<>(
            new Joined<>(
                () -> new Mapped<>(
                    methods.iterator(),
                    strings -> strings
                )
            )
        );
        int sum = 0;
        for (final String type : types) {
            int mine = 0;
            for (final Collection<String> mtd : methods) {
                if (mtd.contains(type)) {
                    ++mine;
                }
            }
            sum += mine;
        }
        final double cohesion;
        if (types.isEmpty() || methods.isEmpty()) {
            cohesion = 1.0d;
        } else {
            cohesion = (double) sum / (double) (types.size() * methods.size());
        }
        return cohesion;
    }

    /**
     * Get all method signatures.
     * @param ctc The .class file
     * @return Method signatures
     * @throws NotFoundException If fails
     */
    @SuppressWarnings("PMD.AvoidInstantiatingObjectsInLoops")
    private static Collection<Collection<String>> methods(final CtClass ctc)
        throws NotFoundException {
        final Collection<Collection<String>> methods = new LinkedList<>();
        for (final CtMethod mtd : ctc.getDeclaredMethods()) {
            if (Modifier.isPrivate(mtd.getModifiers())) {
                continue;
            }
            final Collection<String> args = new LinkedList<>();
            for (final String arg : CAMC.types(mtd.getSignature())) {
                args.add(arg);
            }
            methods.add(args);
        }
        for (final CtConstructor ctor : ctc.getConstructors()) {
            if (Modifier.isPrivate(ctor.getModifiers())) {
                continue;
            }
            final Collection<String> args = new LinkedList<>();
            for (final String arg : CAMC.types(ctor.getSignature())) {
                args.add(arg);
            }
            methods.add(args);
        }
        return methods;
    }

    /**
     * Get parameter types from a method/ctor signature.
     * @param sig Signature
     * @return Types of params
     */
    private static Iterable<String> types(final String sig) {
        final Collection<String> types = new LinkedList<>();
        new SignatureReader(sig).accept(
            new SignatureVisitor(Opcodes.ASM6) {
                @Override
                public void visitClassType(final String name) {
                    super.visitClassType(name);
                    types.add(name);
                }
                @Override
                public void visitBaseType(final char name) {
                    super.visitBaseType(name);
                    if ('V' != name) {
                        types.add(String.format(".%s", name));
                    }
                }
            }
        );
        return types;
    }

}
