/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2017-2025 Yegor Bugayenko
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
package org.jpeek.skeleton;

import com.jcabi.log.Logger;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.TreeSet;
import javassist.ClassPool;
import javassist.CtClass;
import org.cactoos.iterable.Filtered;
import org.cactoos.iterable.Mapped;
import org.cactoos.list.ListOf;
import org.jpeek.Base;

/**
 * List of Javassist classes.
 *
 * <p>We take into account only classes. Interfaces are ignored.</p>
 *
 * <p>There is no thread-safety guarantee.</p>
 *
 * @see <a href="http://www.pitt.edu/~ckemerer/CK%20research%20papers/MetricForOOD_ChidamberKemerer94.pdf">A packages suite for object oriented design</a>
 * @since 0.27
 */
final class Classes implements Iterable<CtClass> {

    /**
     * The base.
     */
    private final Base base;

    /**
     * Javassist pool.
     */
    private final ClassPool pool;

    /**
     * Ctor.
     * @param bse The base
     */
    Classes(final Base bse) {
        this.base = bse;
        this.pool = new ClassPool();
    }

    @Override
    @SuppressWarnings({
        "PMD.PrematureDeclaration",
        "PMD.GuardLogStatement"
    })
    public Iterator<CtClass> iterator() {
        final Iterable<CtClass> classes;
        final long start = System.currentTimeMillis();
        try {
            classes = new Filtered<CtClass>(
                // @checkstyle BooleanExpressionComplexityCheck (10 lines)
                ctClass -> !ctClass.isInterface()
                    && !ctClass.isEnum()
                    && !ctClass.isAnnotation()
                    && !ctClass.getName().matches("^.+\\$[0-9]+$")
                    && !ctClass.getName().matches("^.+\\$AjcClosure[0-9]+$"),
                new Mapped<>(
                    path -> {
                        try (InputStream stream = Files.newInputStream(path)) {
                            return this.pool.makeClassIfNew(stream);
                        }
                    },
                    new Filtered<>(
                        path -> Files.isRegularFile(path)
                            && path.toString().endsWith(".class"),
                        new ListOf<>(this.base.files())
                    )
                )
            );
        } catch (final IOException ex) {
            throw new IllegalStateException(ex);
        }
        final Collection<CtClass> unique = new TreeSet<>(
            Comparator.comparing(CtClass::getName)
        );
        unique.addAll(new ListOf<>(classes));
        Logger.debug(
            this, "%d classes found and parsed via Javassist in %[ms]s",
            unique.size(), System.currentTimeMillis() - start
        );
        return unique.iterator();
    }

}
