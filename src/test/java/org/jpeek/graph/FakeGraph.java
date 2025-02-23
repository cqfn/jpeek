/*
 * SPDX-FileCopyrightText: Copyright (c) 2017-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.jpeek.graph;

import java.util.ArrayList;
import java.util.List;

/**
 * Fake Graph implementation for tests.
 * @since 0.30.9
 */
public final class FakeGraph implements Graph {

    /**
     * List of the nodes of this graph.
     */
    private final List<Node> nds;

    /**
     * Ctor.
     * @param nodes Nodes
     */
    public FakeGraph(final List<Node> nodes) {
        this.nds = new ArrayList<>(nodes);
    }

    @Override
    public List<Node> nodes() {
        return this.nds;
    }

}
