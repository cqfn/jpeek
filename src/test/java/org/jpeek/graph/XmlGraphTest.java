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

import java.io.IOException;
import java.util.List;
import org.cactoos.list.ListOf;
import org.hamcrest.core.AllOf;
import org.jpeek.FakeBase;
import org.jpeek.skeleton.Skeleton;
import org.junit.jupiter.api.Test;
import org.llorllale.cactoos.matchers.Assertion;
import org.llorllale.cactoos.matchers.HasValuesMatching;

/**
 * Test case for {@link XmlGraph}.
 * @since 0.30.9
 */
public final class XmlGraphTest {

    @Test
    @SuppressWarnings("unchecked")
    public void buildsMethodsAsNodes() throws IOException {
        final List<Node> nodes = new XmlGraph(
            new Skeleton(new FakeBase("MethodMethodCalls"))
        ).nodes();
        new Assertion<>(
            "Must build nodes representing methods",
            nodes,
            new AllOf<Iterable<Node>>(
                new ListOf<>(
                    new HasValuesMatching<>(
                        node -> {
                            return node.name().equals("methodOne()I");
                        }
                    ),
                    new HasValuesMatching<>(
                        node -> {
                            return node.name().equals("methodTwo()I");
                        }
                    ),
                    new HasValuesMatching<>(
                        node -> {
                            return node.name().equals("methodThree()I");
                        }
                    ),
                    new HasValuesMatching<>(
                        node -> {
                            return node.name().equals("methodFour()I");
                        }
                    ),
                    new HasValuesMatching<>(
                        node -> {
                            return node.name().equals("methodFive()I");
                        }
                    )
                )
            )
        ).affirm();
    }

}
