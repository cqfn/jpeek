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
package org.jpeek.graph;

import com.jcabi.xml.XML;
import java.io.IOException;
import java.util.List;
import org.cactoos.list.Mapped;
import org.cactoos.text.Joined;
import org.jpeek.skeleton.Skeleton;

/**
 * Graph implementation built on skeleton.
 * @since 0.30.9
 */
public final class XmlGraph implements Graph {

    /**
     * List of the nodes of this graph.
     */
    private final List<Node> nds;

    /**
     * Ctor.
     * @param skeleton XMl representation on whiwh to build the graph
     * @throws IOException If fails
     */
    public XmlGraph(final Skeleton skeleton) throws IOException {
        this.nds = XmlGraph.build(skeleton);
    }

    @Override
    public List<Node> nodes() {
        return this.nds;
    }

    /**
     * Builds the graph from the skeleton.
     * @param skeleton XML representation on whiwh to build the graph
     * @return List of nodes
     * @throws IOException If fails
     */
    private static List<Node> build(final Skeleton skeleton) throws IOException {
        return new Mapped<XML, Node>(
            method -> new Node.Simple(
                new Joined(
                    "", method.xpath("@name").get(0), method.xpath("@desc").get(0)
                ).asString()
            ), skeleton.xml().nodes(
                "//methods/method[@ctor='false' and @abstract='false']"
            )
        );
    }
}
