/**
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

package org.jpeek.metrics;

import org.cactoos.list.ListOf;
import org.junit.Ignore;
import org.junit.Test;

/**
 * Test case for LCOM4
 * LCOM4 = Logical Relatedness of Methods.
 * Basically it's a graph disjoint set.
 * I.e. it answers the the following question:
 * "Into how many independent classes can you split this class?"
 * @author Ilya Kharlamov (ilya.kharlamov@gmail.com)
 * @version $Id$
 * @since 0.28
 * @checkstyle JavadocMethodCheck (500 lines)
 * @checkstyle ClassDataAbstractionCouplingCheck (500 lines)
 */
public final class Lcom4Test {

    /**
     * Variable from the report.
     */
    public static final String PAIRS = "pairs";

    /**
     * Variable from the report.
     */
    public static final String ATTRIBUTES = "attributes";

    /**
     * Variable from the report.
     */
    public static final String METHODS = "methods";

    /**
     * Path to XSL.
     */
    private static final String XSL = "org/jpeek/metrics/LCOM4.xsl";

    /**
     * Tests the deep dependencies methodFour() -> methodTwo() -> methodOne().
     *  MethodMethodCalls.java
     *  - methodOne uses 'num' directly
     *  - methodTwo uses 'num' indirectly via methodOne
     *  - methodThree uses 'num' directly
     *  - methodFour uses 'num' indirectly via methodTwo and methodOne
     *  - methodFive does not use 'num' (this is an orphan method, ignored)
     *  Therefore the number of disjoint sets (LCOM4) should be 1
     *  since all the methods use the same num field.
     *  @todo #215:30min LCOM4: Graph algorithm to determine disjoint sets.
     *  Currently we can only identify the dependencies via only one graph hop
     *  so we can't trace methodFour that uses 'num' indirectly
     *  via methodTwo and methodOne.
     *  Calculating Disjoint sets (also known as Connected Components),
     *  requires a graph traversal algorithm like depth-first search.
     *  This one will be tricky to implement in XSLT.
     *  After implementing, remove the @Ignore.
     */
    @Test
    @Ignore
    public void methodMethodCalls() throws Exception {
        final MetricBase.Report report = new MetricBase(
            Lcom4Test.XSL
        ).transform(
            "MethodMethodCalls"
        );
        report.assertVariable(
            Lcom4Test.METHODS,
            new ListOf<>(
                "methodOne()",
                "methodTwo()",
                "methodThree()",
                "methodFour()",
                "methodFive()"
            ).size()
        );
        report.assertVariable(Lcom4Test.ATTRIBUTES, 1);
        report.assertValue(1.0F);
    }

    /**
     * In OneCommonAttribute.java.
     * - methodOne uses variable 'num'
     * - methodTwo uses variable 'num'
     * So this class only has a single disjoint set AKA (LCOM4)
     * This is an ideal LCOM4 value = 1
     */
    @Test
    public void oneCommonAttribute() throws Exception {
        final MetricBase.Report report = new MetricBase(
            Lcom4Test.XSL
        ).transform(
            "OneCommonAttribute"
        );
        report.assertVariable(
            Lcom4Test.METHODS,
            new ListOf<>(
                "methodOne",
                "methodTwo"
            ).size()
        );
        report.assertVariable(Lcom4Test.ATTRIBUTES, 1);
        report.assertVariable(Lcom4Test.PAIRS, 1);
        report.assertValue(1.0F);
    }

    /**
     * In NotCommonAttributes.java
     * - methodOne only uses 'num' variable
     * - methodTwo only uses 'anotherNum' variable
     * So basically there are two separate disjoint sets,
     * or two separate classes under the same umbrella.
     * So the value is 2.0
     */
    @Test
    public void notCommonAttributes() throws Exception {
        final MetricBase.Report report = new MetricBase(
            Lcom4Test.XSL
        ).transform(
            "NotCommonAttributes"
        );
        report.assertVariable(Lcom4Test.ATTRIBUTES, 2);
        report.assertVariable(Lcom4Test.PAIRS, 0);
        report.assertValue(2.0F);
    }

    /**
     * Should be the same as NotCommonAttributes.
     * since constructors are not methods and should be ignored
     */
    @Test
    public void notCommonAttributesWithAllArgsConstructor() throws Exception {
        final MetricBase.Report report = new MetricBase(
            Lcom4Test.XSL
        ).transform(
            "NotCommonAttributesWithAllArgsConstructor"
        );
        report.assertVariable(Lcom4Test.ATTRIBUTES, 2);
        report.assertVariable(Lcom4Test.PAIRS, 0);
        report.assertValue(2.0F);
    }
}
