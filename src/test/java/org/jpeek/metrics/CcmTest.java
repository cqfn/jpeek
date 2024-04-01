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
package org.jpeek.metrics;

import com.jcabi.xml.XML;
import java.util.HashMap;
import org.jpeek.FakeBase;
import org.jpeek.calculus.java.Ccm;
import org.jpeek.skeleton.Skeleton;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

/**
 * Tests to check CCM metric in different links between attributes and methods.
 *
 * @since 0.29
 */
final class CcmTest {

    /**
     * Class with one method access one attribute have
     * ncc metric = methods count.
     *
     * @throws Exception
     */
    @Test
    void manyComponentInClassTest() throws Exception {
        final XML result = new Ccm().node(
            "ccm", new HashMap<>(0),
            new Skeleton(
                new FakeBase("CcmManyComp")
            ).xml()
        );
        final MetricBase.Report report = new MetricBase.Report("CcmManyComp", result);
        report.assertVariable("methods", 5);
        report.assertVariable("nc", 0);
        report.assertVariable("nmp", 10);
        report.assertVariable("ncc", 5);
        report.assertValue(0.0f, 0.001f);
    }

    /**
     * Class with one method access one attribute and
     * Ctor with all attributes initialization have the same
     * metric as without Ctor.
     *
     * @throws Exception
     */
    @Test
    void manyComponentWithCtorInClassTest() throws Exception {
        final XML result = new Ccm().node(
            "ccm", new HashMap<>(0),
            new Skeleton(
                new FakeBase("CcmManyCompWithCtor")
            ).xml()
        );
        final MetricBase.Report report = new MetricBase.Report("CcmManyCompWithCtor", result);
        report.assertVariable("methods", 5);
        report.assertVariable("nc", 0);
        report.assertVariable("nmp", 10);
        report.assertVariable("ncc", 5);
        report.assertValue(0.0f, 0.001f);
    }

    @Test
    void oneComponentInClassTest() throws Exception {
        final XML result = new Ccm().node(
            "ccm", new HashMap<>(0),
            new Skeleton(
                new FakeBase("CcmOneComp")
            ).xml()
        );
        final MetricBase.Report report = new MetricBase.Report("CcmOneComp", result);
        report.assertVariable("methods", 5);
        report.assertVariable("nc", 10);
        report.assertVariable("nmp", 10);
        report.assertVariable("ncc", 1);
        report.assertValue(1.0f, 0.001f);
    }

    /**
     * Check ccm metric for mixed usage: attribute usage, methods calls.
     *
     * @throws Exception
     * @todo #522:30min there is a 4th step for incorrect calculation: nc
     *  in case of calling one method from another because of
     *  `xsl:if test="$method/ops/op/text()[. = $other/ops/op/text()]"`
     *  method name is not used for creating edge.
     */
    @Test
    @Disabled
    void mixedCallsInClassTest() throws Exception {
        final XML result = new Ccm().node(
            "ccm",
            new HashMap<>(0),
            new Skeleton(
                new FakeBase("CcmMixCallManyComp")
            ).xml()
        );
        final MetricBase.Report report = new MetricBase.Report("CcmMixCallManyComp", result);
        report.assertVariable("methods", 5);
        report.assertVariable("nc", 2);
        report.assertVariable("nmp", 10);
        report.assertVariable("ncc", 3);
        report.assertValue(0.0666f, 0.0001f);
    }
}
