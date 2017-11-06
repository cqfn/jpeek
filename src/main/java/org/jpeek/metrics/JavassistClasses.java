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

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

import com.google.common.io.Closer;
import javassist.ClassPool;
import javassist.CtClass;
import org.cactoos.Func;
import org.cactoos.func.IoCheckedFunc;
import org.cactoos.iterable.Filtered;
import org.cactoos.iterable.Joined;
import org.cactoos.iterable.Mapped;
import org.cactoos.map.MapEntry;
import org.jpeek.Base;
import org.jpeek.Metric;
import org.xembly.Directive;
import org.xembly.Directives;

/**
 * Classes parsed by Javassist.
 *
 * <p>We take into account only classes. Interfaces are ignored.</p>
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
public final class JavassistClasses implements Metric {

    /**
     * The base.
     */
    private final Base base;

    /**
     * Func.
     */
    private final IoCheckedFunc<CtClass, Iterable<Directive>> func;

    /**
     * Javassist pool.
     */
    private final ClassPool pool;

    /**
     * Ctor.
     * @param bse The base
     * @param fnc Func
     */
    public JavassistClasses(final Base bse,
        final Func<CtClass, Iterable<Directive>> fnc) {
        this.base = bse;
        this.pool = ClassPool.getDefault();
        this.func = new IoCheckedFunc<>(fnc);
    }

    @Override
    public Iterable<Directive> xembly() throws IOException {
        return new Directives()
            .add("metric")
            .add("app")
            .attr("id", this.base)
            .append(
                new Joined<Directive>(
                    new Mapped<>(
                        ent -> new Directives()
                            .add("package")
                            .attr("id", ent.getKey())
                            .append(ent.getValue())
                            .up(),
                        this.metrics()
                    )
                )
            );
    }

    /**
     * Calculate metrics for all classes.
     * @return Metrics
     * @throws IOException If fails
     */
    @SuppressWarnings("PMD.AvoidInstantiatingObjectsInLoops")
    private Iterable<Map.Entry<String, Directives>> metrics()
        throws IOException {
        final Map<String, Directives> map = new HashMap<>(0);
        final Iterable<Map.Entry<String, Directives>> all = new Mapped<>(
            this::metric,
            new Filtered<>(
                // @checkstyle BooleanExpressionComplexityCheck (10 lines)
                ctClass -> !ctClass.isInterface()
                    && !ctClass.isEnum()
                    && !ctClass.isAnnotation()
                    && !ctClass.getName().matches("^.+\\$[0-9]+$")
                    && !ctClass.getName().matches("^.+\\$AjcClosure[0-9]+$"),
                new Mapped<>(
                    path -> {
                        final Closer closer = Closer.create();
                        try {
                            return this.pool.makeClassIfNew(
                                closer.register(
                                    new FileInputStream(path.toFile())));
                        } catch (Throwable e) {
                            throw closer.rethrow(e);
                        } finally {
                            closer.close();
                        }
                    },
                    new Filtered<>(
                        path -> Files.isRegularFile(path)
                            && path.toString().endsWith(".class"),
                        this.base.files()
                    )
                )
            )
        );
        for (final Map.Entry<String, Directives> ent : all) {
            map.putIfAbsent(ent.getKey(), new Directives());
            map.get(ent.getKey()).append(ent.getValue());
        }
        return map.entrySet();
    }

    /**
     * Calculate metrics for a single .class file.
     * @param ctc The class
     * @return Metrics
     * @throws IOException If fails
     */
    private Map.Entry<String, Directives> metric(
        final CtClass ctc) throws IOException {
        final Iterable<Directive> cohesion = this.func.apply(ctc);
        ctc.defrost();
        String pkg = ctc.getPackageName();
        if (pkg == null) {
            pkg = "";
        }
        return new MapEntry<>(
            pkg,
            new Directives()
                .add("class")
                .attr("id", ctc.getSimpleName())
                .append(cohesion)
                .up()
        );
    }

}
