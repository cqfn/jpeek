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

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtConstructor;
import javassist.CtMethod;
import javassist.Modifier;
import javassist.NotFoundException;
import org.cactoos.iterable.Filtered;
import org.cactoos.iterable.Joined;
import org.cactoos.iterable.Mapped;
import org.cactoos.map.MapEntry;
import org.jpeek.Base;
import org.jpeek.Metric;
import org.xembly.Directive;
import org.xembly.Directives;

/**
 * Cohesion Among Methods of Classes (CAMC).
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
@SuppressWarnings("PMD.AvoidInstantiatingObjectsInLoops")
public final class CAMC implements Metric {

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
    public CAMC(final Base bse) {
        this.base = bse;
        this.pool = ClassPool.getDefault();
    }

    @Override
    public Iterable<Directive> xembly() throws IOException {
        return new Directives()
            .add("app")
            .attr("id", this.base.toString())
            .append(
                new Joined<>(
                    new Mapped<>(
                        this.metrics(),
                        ent -> new Directives()
                            .add("package")
                            .attr("id", ent.getKey())
                            .append(ent.getValue())
                            .up()
                    )
                )
            );
    }

    /**
     * Calculate metrics for all classes.
     * @return Metrics
     * @throws IOException If fails
     */
    private Iterable<Map.Entry<String, Directives>> metrics()
        throws IOException {
        final Map<String, Directives> map = new HashMap<>(0);
        final Iterable<Map.Entry<String, Directives>> all = new Mapped<>(
            new Filtered<>(
                this.base.files(),
                path -> Files.isRegularFile(path)
                    && path.toString().endsWith(".class")
            ),
            this::metric
        );
        for (final Map.Entry<String, Directives> ent : all) {
            map.putIfAbsent(ent.getKey(), new Directives());
            map.get(ent.getKey()).append(ent.getValue());
        }
        return map.entrySet();
    }

    /**
     * Calculate metrics for a single .class file.
     * @param file The .class file
     * @return Metrics
     * @throws IOException If fails
     */
    private Map.Entry<String, Directives> metric(
        final Path file) throws IOException {
        final CtClass ctc = this.pool.makeClass(
            new FileInputStream(file.toFile())
        );
        try {
            return new MapEntry<>(
                ctc.getPackageName(),
                new Directives()
                    .add("class")
                    .attr("id", ctc.getSimpleName())
                    .attr("value", String.format("%.4f", CAMC.cohesion(ctc)))
                    .up()
            );
        } catch (final NotFoundException ex) {
            throw new IllegalStateException(ex);
        }
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
            new org.cactoos.collection.Joined<String>(
                () -> new org.cactoos.iterator.Mapped<>(
                    methods.iterator(),
                    strings -> strings
                )
            )
        );
        int sum = 0;
        for (final Collection<String> mtd : methods) {
            int mine = 0;
            for (final String arg : mtd) {
                if (types.contains(arg)) {
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
    private static Collection<Collection<String>> methods(final CtClass ctc)
        throws NotFoundException {
        final Collection<Collection<String>> methods = new LinkedList<>();
        for (final CtMethod mtd : ctc.getDeclaredMethods()) {
            if (Modifier.isPrivate(mtd.getModifiers())) {
                continue;
            }
            final Collection<String> args = new LinkedList<>();
            for (final CtClass arg : mtd.getParameterTypes()) {
                args.add(arg.getName());
            }
            methods.add(args);
        }
        for (final CtConstructor ctor : ctc.getConstructors()) {
            if (Modifier.isPrivate(ctor.getModifiers())) {
                continue;
            }
            final Collection<String> args = new LinkedList<>();
            for (final CtClass arg : ctor.getParameterTypes()) {
                args.add(arg.getName());
            }
            methods.add(args);
        }
        return methods;
    }
}
