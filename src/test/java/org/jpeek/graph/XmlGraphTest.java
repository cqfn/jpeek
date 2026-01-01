/*
 * SPDX-FileCopyrightText: Copyright (c) 2017-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.jpeek.graph;

import java.util.List;
import java.util.Map;
import org.cactoos.list.ListOf;
import org.cactoos.map.MapOf;
import org.hamcrest.collection.IsEmptyCollection;
import org.hamcrest.core.AllOf;
import org.jpeek.FakeBase;
import org.jpeek.skeleton.Skeleton;
import org.junit.jupiter.api.Test;
import org.llorllale.cactoos.matchers.Assertion;
import org.llorllale.cactoos.matchers.HasValues;
import org.llorllale.cactoos.matchers.HasValuesMatching;

/**
 * Test case for {@link XmlGraph}.
 * @since 0.30.9
 */
final class XmlGraphTest {

    /**
     * First method name.
     */
    private static final String METHOD_ONE = "MethodMethodCalls.methodOne.";

    /**
     * Second method name.
     */
    private static final String METHOD_TWO = "MethodMethodCalls.methodTwo.";

    /**
     * Third method name.
     */
    private static final String METHOD_THREE = "MethodMethodCalls.methodThree.";

    /**
     * Fourth method name.
     */
    private static final String METHOD_FOUR = "MethodMethodCalls.methodFour.";

    /**
     * Fifth method name.
     */
    private static final String METHOD_FIVE = "MethodMethodCalls.methodFive.";

    /**
     * Class name.
     */
    private static final String CLASS_NAME = "MethodMethodCalls";

    @SuppressWarnings("unchecked")
    @Test
    void buildsMethodsAsNodes() {
        final List<Node> nodes = new XmlGraph(
            new Skeleton(new FakeBase(XmlGraphTest.CLASS_NAME)),
            "", XmlGraphTest.CLASS_NAME
        ).nodes();
        new Assertion<>(
            "Must build nodes representing methods",
            nodes,
            new AllOf<Iterable<Node>>(
                new ListOf<>(
                    new HasValuesMatching<>(
                        node -> node.name().equals(XmlGraphTest.METHOD_ONE)
                    ),
                    new HasValuesMatching<>(
                        node -> node.name().equals(XmlGraphTest.METHOD_TWO)
                    ),
                    new HasValuesMatching<>(
                        node -> node.name().equals(XmlGraphTest.METHOD_THREE)
                    ),
                    new HasValuesMatching<>(
                        node -> node.name().equals(XmlGraphTest.METHOD_FOUR)
                    ),
                    new HasValuesMatching<>(
                        node -> node.name().equals(XmlGraphTest.METHOD_FIVE)
                    )
                )
            )
        ).affirm();
    }

    @Test
    void buildsConnections() {
        final Map<String, Node> byname = new MapOf<>(
            Node::name,
            node -> node,
            new XmlGraph(
                new Skeleton(new FakeBase(XmlGraphTest.CLASS_NAME)),
                "", XmlGraphTest.CLASS_NAME
            ).nodes()
        );
        final Node one = byname.get(XmlGraphTest.METHOD_ONE);
        final Node two = byname.get(XmlGraphTest.METHOD_TWO);
        final Node three = byname.get(XmlGraphTest.METHOD_THREE);
        final Node four = byname.get(XmlGraphTest.METHOD_FOUR);
        final Node five = byname.get(XmlGraphTest.METHOD_FIVE);
        new Assertion<>(
            "Must build nodes connections when called",
            one.connections(),
            new HasValues<>(two)
        ).affirm();
        new Assertion<>(
            "Must build nodes connections when called or calling",
            two.connections(),
            new HasValues<>(one, four)
        ).affirm();
        new Assertion<>(
            "Must build nodes connections when neither called nor calling",
            three.connections(),
            new IsEmptyCollection<>()
        ).affirm();
        new Assertion<>(
            "Must build nodes connections when calling",
            four.connections(),
            new HasValues<>(two)
        ).affirm();
        new Assertion<>(
            "Must build nodes connections when throwing",
            five.connections(),
            new IsEmptyCollection<>()
        ).affirm();
    }
}
