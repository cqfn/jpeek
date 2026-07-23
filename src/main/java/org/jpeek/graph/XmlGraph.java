/*
 * SPDX-FileCopyrightText: Copyright (c) 2017-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
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
