/*
 * SPDX-FileCopyrightText: Copyright (c) 2017-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.jpeek.graph;

import java.util.Set;
import org.cactoos.list.ListOf;
import org.hamcrest.core.AllOf;
import org.junit.jupiter.api.Test;
import org.llorllale.cactoos.matchers.Assertion;
import org.llorllale.cactoos.matchers.HasValuesMatching;

/**
 * Test case for {@link Disjoint}.
 * @since 0.30.9
 */
final class DisjointTest {

    @Test
    @SuppressWarnings("unchecked")
    void calculatesDisjointSets() throws Exception {
        final Node one = new Node.Simple("one");
        final Node two = new Node.Simple("two");
        final Node three = new Node.Simple("three");
        final Node four = new Node.Simple("four");
        final Node five = new Node.Simple("five");
        final Node six = new Node.Simple("six");
        one.connections().add(two);
        two.connections().addAll(new ListOf<>(one, three));
        three.connections().add(two);
        four.connections().add(five);
        five.connections().add(four);
        final Graph graph = new FakeGraph(
            new ListOf<>(one, two, three, four, five, six)
        );
        new Assertion<>(
            "Must build disjoint sets correctly",
            new Disjoint(graph).value(),
            new AllOf<Iterable<Set<Node>>>(
                new ListOf<>(
                    new HasValuesMatching<>(
                        set -> set.contains(one) && set.contains(two) && set.contains(three)
                            && set.size() == 3
                    ),
                    new HasValuesMatching<>(
                        set -> set.contains(five) && set.contains(four) && set.size() == 2
                    ),
                    new HasValuesMatching<>(
                        set -> set.contains(six) && set.size() == 1
                    )
                )
            )
        ).affirm();
    }

    @Test
    @SuppressWarnings("unchecked")
    void calculatesDisjointSetsForUnconnected() throws Exception {
        final Node one = new Node.Simple("1");
        final Node two = new Node.Simple("2");
        final Node three = new Node.Simple("3");
        final Node four = new Node.Simple("4");
        final Graph graph = new FakeGraph(
            new ListOf<>(one, two, three, four)
        );
        new Assertion<>(
            "Must build disjoint sets for unconnected graph",
            new Disjoint(graph).value(),
            new AllOf<Iterable<Set<Node>>>(
                new ListOf<>(
                    new HasValuesMatching<>(
                        set -> set.contains(one) && set.size() == 1
                    ),
                    new HasValuesMatching<>(
                        set -> set.contains(two) && set.size() == 1
                    ),
                    new HasValuesMatching<>(
                        set -> set.contains(three) && set.size() == 1
                    ),
                    new HasValuesMatching<>(
                        set -> set.contains(four) && set.size() == 1
                    )
                )
            )
        ).affirm();
    }

}
