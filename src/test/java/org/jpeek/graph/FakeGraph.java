/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2017-2023 Yegor Bugayenko
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
