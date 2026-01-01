/*
 * SPDX-FileCopyrightText: Copyright (c) 2017-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.jpeek.graph;

import java.util.List;

/**
 * Graph containing list of nodes.
 * @since 0.30.9
 */
public interface Graph {

    /**
     * Nodes composing this graph.
     * @return List of the nodes belonging to this graph
     */
    List<Node> nodes();
}
