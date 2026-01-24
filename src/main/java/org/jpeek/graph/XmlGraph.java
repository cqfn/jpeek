/*
 * SPDX-FileCopyrightText: Copyright (c) 2017-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.jpeek.graph;

import java.util.List;
import org.cactoos.scalar.Sticky;
import org.cactoos.scalar.Unchecked;
import org.jpeek.skeleton.Skeleton;
import org.cactoos.list.ListOf;


public final class XmlGraph implements Graph {
    private final Unchecked<List<Node>> nds;

    public XmlGraph(final Skeleton skeleton, final String pname, final String cname) {
        this.nds = new Unchecked<>(
            new Sticky<>(() -> {
                XmlMethods xmlMethods = new XmlMethods(skeleton, pname, cname);
                NodeConnections connections = new NodeConnections(xmlMethods);
                connections.establishConnections();
                return new ListOf<>(xmlMethods.getByXml().values());
            })
        );
    }

    @Override
    public List<Node> nodes() {
        return this.nds.value();
    }
}