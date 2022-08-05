/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2017-2022 Yegor Bugayenko
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
package org.jpeek.graph;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.cactoos.Scalar;

/**
 * Disjoint graph node sets calculus implemented as scalar.
 * @since 0.30.9
 */
public final class Disjoint implements Scalar<List<Set<Node>>> {

    /**
     * Graph.
     */
    private final Graph graph;

    /**
     * Ctor.
     * @param graph Graph
     */
    public Disjoint(final Graph graph) {
        this.graph = graph;
    }

    @Override
    public List<Set<Node>> value() throws Exception {
        final Set<Node> unvisited = new HashSet<>(this.graph.nodes());
        final List<Set<Node>> result = new ArrayList<>(unvisited.size());
        while (!unvisited.isEmpty()) {
            final Node node = unvisited.iterator().next();
            final Set<Node> visiting = new HashSet<>();
            visiting.add(node);
            final Set<Node> current = new HashSet<>();
            while (!visiting.isEmpty()) {
                final Node visit = visiting.iterator().next();
                current.add(visit);
                for (final Node connexion:visit.connections()) {
                    if (!current.contains(connexion)) {
                        visiting.add(connexion);
                    }
                }
                unvisited.remove(visit);
                visiting.remove(visit);
            }
            result.add(current);
        }
        return result;
    }
}
