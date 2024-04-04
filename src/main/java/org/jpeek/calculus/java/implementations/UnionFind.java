/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2017-2024 Yegor Bugayenko
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
package org.jpeek.calculus.java.implementations;

import java.util.HashSet;
import java.util.Map;

/**
 * Utility class implementing the Union-Find algorithm.
 * The UnionFind class provides methods to perform the Union-Find algorithm,
 * which is used to calculate the number of components in a given structure.
 * @param <T> The type of elements in the Union-Find structure.
 * @since 0.30.25
 */
public class UnionFind<T> {
    /**
     * The map representing the parent-child relationships of elements.
     */
    private final Map<T, T> parents;

    /**
     * The number of components in the Union-Find structure.
     */
    private int size;

    /**
     * Constructs a Union-Find data structure.
     * This constructor initializes a Union-Find data structure with the given sets.
     * It calculates the size of the structure based on the unique values in the sets.
     * The Union-Find algorithm is used to manage the relationships between elements in the sets.
     * @param sets The initial sets representing the elements and their relationships.
     */
    public UnionFind(final Map<T, T> sets) {
        this.parents = sets;
        this.size = new HashSet<>(sets.values()).size();
    }

    /**
     * Unites two nodes using the Union-Find algorithm.
     * This method unites two nodes using the Union-Find algorithm.
     * @param node The first node.
     * @param son The second node.
     */
    public void unite(final T node, final T son) {
        if (!(this.parents.containsKey(node) && this.parents.containsKey(son))) {
            throw new IllegalStateException("some of the nodes are not from the initial set");
        }
        final T root = this.getParent(node);
        final T attachable = this.getParent(son);
        if (!root.equals(attachable)) {
            this.size -= 1;
            this.parents.put(attachable, root);
        }
    }

    /**
     * Gets the size of the Union-Find structure.
     * This method retrieves the number of components in the Union-Find structure.
     * The size represents the number of disjoint sets or components in the structure.
     * @return The size of the Union-Find structure, which is the number of components.
     */
    public int getSize() {
        return this.size;
    }

    /**
     * Gets the parent of a node using the Union-Find algorithm.
     * This method retrieves the parent of a node using the Union-Find algorithm.
     * @param node The node whose parent is to be found.
     * @return The parent of the node.
     */
    private T getParent(final T node) {
        if (!this.parents.containsKey(node)) {
            throw new IllegalStateException("node is not from the initial set");
        }
        T ancestor = node;
        while (!this.parents.get(ancestor).equals(ancestor)) {
            ancestor = this.parents.get(ancestor);
        }
        T current = node;
        while (!this.parents.get(current).equals(current)) {
            final T temp = this.parents.get(current);
            this.parents.put(current, ancestor);
            current = temp;
        }
        return ancestor;
    }
}
