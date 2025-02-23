/*
 * The MIT License (MIT)
 *
 * SPDX-FileCopyrightText: Copyright (c) 2017-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
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
