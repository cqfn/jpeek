/*
 * SPDX-FileCopyrightText: Copyright (c) 2017-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
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
