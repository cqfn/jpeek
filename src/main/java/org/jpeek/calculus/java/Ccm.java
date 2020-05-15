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
package org.jpeek.calculus.java;

import com.jcabi.xml.XML;
import com.jcabi.xml.XSLDocument;
import java.util.List;
import java.util.Map;
import org.cactoos.io.ResourceOf;
import org.cactoos.scalar.Unchecked;
import org.cactoos.text.FormattedText;
import org.cactoos.text.Joined;
import org.jpeek.calculus.Calculus;

/**
 * CCM metric Java calculus.
 * @since 0.30.25
 */
public final class Ccm implements Calculus {

    @Override
    public XML node(
        final String metric,
        final Map<String, Object> params,
        final XML skeleton
    ) {
        if (!"ccm".equalsIgnoreCase(metric)) {
            throw new IllegalArgumentException(
                new FormattedText(
                    "This metric is CCM, not %s.", metric
                ).toString()
            );
        }
        return Ccm.withFixedNcc(
            new Unchecked<>(
                () -> new XSLDocument(
                    new ResourceOf("org/jpeek/metrics/CCM.xsl").stream()
                ).transform(skeleton)
            ).value(),
            skeleton
        );
    }

    /**
     * Updates the transformed xml with proper NCC value.
     * @param transformed The transformed XML skeleton.
     * @param skeleton XML Skeleton
     * @return XML with fixed NCC.
     */
    private static XML withFixedNcc(final XML transformed, final XML skeleton) {
        final List<XML> packages = transformed.nodes("//package");
        for (final XML elt : packages) {
            final String pack = elt.xpath("/@id").get(0);
            final List<XML> classes = elt.nodes("//class");
            for (final XML clazz : classes) {
                Ccm.updateNcc(skeleton, pack, clazz);
            }
        }
        return transformed;
    }

    /**
     * Updates the xml node of the class with proper NCC value.
     * @param skeleton XML Skeleton
     * @param pack Package name
     * @param clazz Class node in the resulting xml
     * @todo #449:30min Implement NCC calculation with `XmlGraph` and use this
     *  class to fix CCM metric (see issue #449). To do this, this class, once
     *  it works correctly, should be integrated with XSL based calculuses in
     *  `XslReport` (see `todo #449` in Calculus). Also, decide whether the
     *  whole CCM metric should be implemented in Java, or only the NCC part.
     *  Update this `todo` accordingly.
     */
    private static void updateNcc(
        final XML skeleton, final String pack, final XML clazz
    ) {
        throw new UnsupportedOperationException(
            new Joined(
                "",
                skeleton.toString(),
                pack,
                clazz.toString()
            ).toString()
        );
    }
}
