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
package org.jpeek.graph;

import com.jcabi.xml.XML;

import java.util.*;

import org.cactoos.list.ListOf;
import org.cactoos.map.MapOf;
import org.cactoos.scalar.Sticky;
import org.cactoos.scalar.Unchecked;
import org.cactoos.text.FormattedText;
import org.jpeek.skeleton.Skeleton;

/**
 * Graph implementation built on skeleton.
 * @since 0.30.9
 * @todo #473:30min Find a way to eliminate this
 *  ClassDataAbstractionCouplingCheck. The class probably needs to be split
 *  into smaller ones, perhaps extracting the maps into separate objects
 *  (extending MapEnvelopes), or maybe the list itself.
 */
public final class XmlGraph implements Graph {

    /**
     * List of the nodes of this graph.
     */
    private final Unchecked<List<Node>> nds;

    /**
     * Ctor.
     * @param skeleton XMl representation on whiwh to build the graph
     * @param pname Package of the class this graph is for
     * @param cname Class in the skeleton this graph is for
     */
    public XmlGraph(final Skeleton skeleton, final String pname, final String cname) {
        this.nds = new Unchecked<>(
            new Sticky<>(
                () -> XmlGraph.build(skeleton, pname, cname)
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
     * @param pname Package of the class this graph is for
     * @param cname Class in the skeleton this graph is for
     * @return List of nodes
     */
    private static List<Node> build(final Skeleton skeleton, final String pname, final String cname) throws Exception {
        final Map<XML, Node> byxml = new HashMap<>();
        final Set<String> visitedNames = new HashSet<>();
        for (XML methodXml : skeleton.xml().nodes("//methods/method[@ctor='false' and @abstract='false']")) {
            String methodName = new XmlMethodSignature(skeleton.xml().nodes(new FormattedText("//package[@id='%s']", pname).toString())
                    .get(0)
                    .nodes(new FormattedText("//class[@id='%s']", cname).toString())
                    .get(0), methodXml).asString();
            if (!visitedNames.contains(methodName)) {
                Node.Simple node = new Node.Simple(methodName);
                byxml.put(methodXml, node);
                visitedNames.add(methodName);
            }
        }
        for (Map.Entry<XML, Node> entry : byxml.entrySet()) {
            XML methodXml = entry.getKey();
            Node caller = entry.getValue();
            for (XML call : methodXml.nodes("ops/op[@code='call']")) {
                String calleeName = new XmlMethodCall(call).toString();
                if (visitedNames.contains(calleeName)) {
                    Node callee = byxml.get(call); // assuming they exist in byxml
                    caller.connections().add(callee);
                    callee.connections().add(caller);
                }
            }
        }
        return new ArrayList<>(byxml.values());
    }
}
