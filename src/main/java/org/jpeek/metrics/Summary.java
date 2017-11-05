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

import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import org.cactoos.iterable.Joined;
import org.cactoos.iterable.Mapped;
import org.cactoos.map.MapEntry;
import org.cactoos.map.StickyMap;
import org.xembly.Directive;
import org.xembly.Directives;

/**
 * Summary of one class.
 *
 * <p>There is no thread-safety guarantee.
 *
 * @author Yegor Bugayenko (yegor256@gmail.com)
 * @version $Id$
 * @see <a href="http://www.pitt.edu/~ckemerer/CK%20research%20papers/MetricForOOD_ChidamberKemerer94.pdf">A metrics suite for object oriented design</a>
 * @since 0.13
 * @checkstyle AbbreviationAsWordInNameCheck (5 lines)
 * @checkstyle ClassDataAbstractionCouplingCheck (500 lines)
 */
public final class Summary implements Iterable<Directive> {

    /**
     * The value.
     */
    private final double value;

    /**
     * Vars.
     */
    private final Map<String, Double> vars;

    /**
     * Ctor.
     */
    public Summary() {
        this(0.0d);
    }

    /**
     * Ctor.
     * @param val The value
     */
    public Summary(final double val) {
        this(val, new HashMap<>(0));
    }

    /**
     * Ctor.
     * @param val The value
     * @param map The map of vars
     */
    private Summary(final double val, final Map<String, Double> map) {
        this.value = val;
        this.vars = map;
    }

    @Override
    public Iterator<Directive> iterator() {
        return new Directives()
            .attr("value", String.format(Locale.ENGLISH, "%.4f", this.value))
            .add("vars")
            .append(
                new Joined<Directive>(
                    new Mapped<>(
                        ent -> new Directives()
                            .add("var")
                            .attr("id", ent.getKey())
                            .set(ent.getValue())
                            .up(),
                        this.vars.entrySet()
                    )
                )
            )
            .up()
            .iterator();
    }

    /**
     * With this var.
     * @param var The var name
     * @param val The value
     * @return Summary
     */
    public Summary with(final String var, final int val) {
        return this.with(var, (double) val);
    }

    /**
     * With this var.
     * @param var The var name
     * @param val The value
     * @return Summary
     */
    public Summary with(final String var, final double val) {
        return new Summary(
            this.value,
            new StickyMap<String, Double>(this.vars, new MapEntry<>(var, val))
        );
    }

}
