/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2017-2019 Yegor Bugayenko
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
package org.jpeek.graph.java;

import java.util.ArrayList;
import java.util.List;

/**
 * Find connected components of graph.
 * @since 1.0.0
 */
public class FindConnectedComponents {

    /**
     * Number of vertices.
     */
    private final int vnumber;

    /**
     * List of vertices with connections.
     */
    private final List<List<Integer>> graph;

    /**
     * Initializes instance of class.
     *
     * @param vnumber Number of vertices in graph
     */
    public FindConnectedComponents(final int vnumber) {
        this.vnumber = vnumber;
        this.graph = new ArrayList<>(vnumber);
    }

    /**
     * Initializes graph based ob vertices number.
     */
    public void initGraph() {
        for (int ind = 0; ind < this.vnumber; ind += 1) {
            this.graph.add(ind, new ArrayList<>(this.vnumber));
        }
    }

    /**
     * Searches for connected components.
     *
     * @return Number of connected components
     */
    public int connectedComponents() {
        int ccc = 0;
        final boolean[] visited = new boolean[this.vnumber];
        for (int indv = 0; indv < this.vnumber; indv += 1) {
            if (!visited[indv]) {
                this.dfs(indv, visited);
                ccc += 1;
            }
        }
        return ccc;
    }

    /**
     * Creates direct edges in graph.
     *
     * @param src Vertice where connection starts
     * @param dest Vertice where connection ends
     */
    public void addEdge(final int src, final int dest) {
        this.graph.get(src).add(dest);
        this.graph.get(dest).add(src);
    }

    /**
     * Recurrent method for depth first search.
     *
     * @param vertice Current vertice
     * @param visited Array of visited vertices
     */
    @SuppressWarnings("PMD.UseVarargs")
    void dfs(final int vertice, final boolean[] visited) {
        visited[vertice] = true;
        for (final int ver : this.graph.get(vertice)) {
            if (!visited[ver]) {
                this.dfs(ver, visited);
            }
        }
    }
}

