/*
 * SPDX-FileCopyrightText: Copyright (c) 2017-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.jpeek.graph;

import java.util.HashSet;
import java.util.Set;

/**
 * Graph node description. It should at least provide its name and its neighbors.
 * @since 0.30.9
 */
public interface Node {

    /**
     * Node name.
     * @return A identifier for the node
     */
    String name();

    /**
     * Calculates ingoing and outgoing connected nodes.
     * @return List of nodes connected to this node.
     */
    Set<Node> connections();

    /**
     * Simple implementation.
     * @since 0.30.9
     */
    final class Simple implements Node {
        /**
         * Node name.
         */
        private final String name;

        /**
         * Nodes connected to this node.
         */
        private final Set<Node> connect;

        /**
         * Ctor.
         * @param name Node name
         */
        public Simple(final String name) {
            this.name = name;
            this.connect = new HashSet<Node>(1);
        }

        @Override
        public String name() {
            return this.name;
        }

        @Override
        public Set<Node> connections() {
            return this.connect;
        }

        @Override
        public String toString() {
            return this.name;
        }
    }
}
