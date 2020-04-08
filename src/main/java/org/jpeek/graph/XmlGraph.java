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
import java.util.Map;
import org.cactoos.Text;
import org.cactoos.list.ListOf;
import org.cactoos.map.MapOf;
import org.cactoos.scalar.Sticky;
import org.cactoos.scalar.Unchecked;
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
    private final Unchecked<List<Node>> nds;

    /**
     * Ctor.
     * @param skeleton XMl representation on whiwh to build the graph
     * @throws IOException If fails
     */
    public XmlGraph(final Skeleton skeleton) throws IOException {
        this.nds = new Unchecked<>(
            new Sticky<>(
                () -> XmlGraph.build(skeleton)
            )
        );
    }

    @Override
    public List<Node> nodes() {
        return this.nds.value();
    }

    /**
     * Builds the graph from the skeleton.
     * @param skeleton XML representation on whiwh to build the graph
     * @return List of nodes
     * @throws IOException If fails
     */
    private static List<Node> build(final Skeleton skeleton) throws IOException {
        final Map<XML, Node> byxml = new org.cactoos.map.Sticky<>(
            method -> method,
            method -> new Node.Simple(
                new Joined(
                    "", skeleton.xml().xpath("//class/@id").get(0),
                    ".", method.xpath("@name").get(0),
                    ".", new XmlMethodArgs(method).asString()
                ).asString()
            ), skeleton.xml().nodes(
                "//methods/method[@ctor='false' and @abstract='false']"
            )
        );
        final Map<String, Node> byname = new MapOf<>(
            node -> node.name(),
            node -> node,
            byxml.values()
        );
        for (final XML method : byxml.keySet()) {
            final List<XML> calls = method.nodes("ops/op[@code='call']");
            final Node caller = byxml.get(method);
            for (final XML call : calls) {
                final String name = new XmlCall(call).asString();
                if (byname.containsKey(name)) {
                    final Node callee = byname.get(name);
                    caller.connections().add(callee);
                    callee.connections().add(caller);
                }
            }
        }
        return new ListOf<>(byxml.values());
    }

    /**
     * Serialize method call to a string.
     *
     * @since 1.0
     * @todo #440:30min This class XmlCall and the following one
     *  XmlMethodArgs should be made public and tests should be added
     *  to validate both their behaviours.
     */
    private static final class XmlCall implements Text {

        /**
         * XML Call operation.
         */
        private final XML call;

        /**
         * Ctor.
         *
         * @param call Call operation as XML.
         */
        XmlCall(final XML call) {
            this.call = call;
        }

        @Override
        public String asString() throws IOException {
            return new Joined(
                "", this.call.xpath("name/text()").get(0),
                ".", new XmlMethodArgs(call).asString()
            ).asString();
        }
    }

    /**
     * Serialize method arguments to a string.
     *
     * @since 1.0
     */
    private static final class XmlMethodArgs implements Text {

        /**
         * XML Method.
         */
        private final XML method;

        /**
         * Ctor.
         *
         * @param method Method as XML
         */
        XmlMethodArgs(final XML method) {
            this.method = method;
        }

        @Override
        public String asString() throws IOException {
            return new Joined(":", this.method.xpath("args/arg/@type")).asString();
        }
    }
}
