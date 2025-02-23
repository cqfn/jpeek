/*
 * The MIT License (MIT)
 *
 * SPDX-FileCopyrightText: Copyright (c) 2017-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.jpeek.graph;

import com.jcabi.xml.XML;
import java.util.List;
import java.util.Map;
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
    private static List<Node> build(final Skeleton skeleton, final String pname,
        final String cname) {
        final Map<XML, Node> byxml = new MapOf<>(
            method -> method,
            method -> new Node.Simple(
                new XmlMethodSignature(
                    skeleton.xml()
                        .nodes(
                            new FormattedText(
                                "//package[@id='%s']", pname
                            ).toString()
                        ).get(0)
                        .nodes(
                            new FormattedText(
                                "//class[@id='%s']", cname
                            ).toString()
                        ).get(0),
                    method
                ).asString()
            ),
            skeleton.xml().nodes(
                "//methods/method[@ctor='false' and @abstract='false']"
            )
        );
        final Map<String, Node> byname = new MapOf<>(
            Node::name,
            node -> node,
            byxml.values()
        );
        for (final Map.Entry<XML, Node> entry : byxml.entrySet()) {
            final List<XML> calls = entry.getKey().nodes("ops/op[@code='call']");
            final Node caller = entry.getValue();
            for (final XML call : calls) {
                final String name = new XmlMethodCall(call).toString();
                if (byname.containsKey(name)) {
                    final Node callee = byname.get(name);
                    caller.connections().add(callee);
                    callee.connections().add(caller);
                }
            }
        }
        return new ListOf<>(byxml.values());
    }
}
