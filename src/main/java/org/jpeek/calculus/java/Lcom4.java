/*
 * The MIT License (MIT)
 *
 * SPDX-FileCopyrightText: Copyright (c) 2017-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.jpeek.calculus.java;

import com.jcabi.xml.Sources;
import com.jcabi.xml.XML;
import com.jcabi.xml.XSLDocument;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import org.cactoos.io.ResourceOf;
import org.cactoos.text.Joined;
import org.cactoos.text.TextOf;
import org.cactoos.text.UncheckedText;
import org.jpeek.calculus.Calculus;

/**
 * LCOM4 Metrics java calculus.
 * @since 0.30.9
 */
public final class Lcom4 implements Calculus {

    @Override
    public XML node(final String metric, final Map<String, Object> params,
        final XML skeleton) throws IOException {
        final XML result = new XSLDocument(
            new UncheckedText(
                new TextOf(
                    new ResourceOf("org/jpeek/metrics/LCOM4.xsl")
                )
            ).asString(),
            Sources.DUMMY,
            params
        ).transform(skeleton);
        final List<XML> packages = result.nodes("//package");
        for (final XML elt : packages) {
            final String pack = elt.xpath("/@id").get(0);
            final List<XML> classes = elt.nodes("//class");
            for (final XML clazz : classes) {
                this.update(skeleton, pack, clazz);
            }
        }
        return result;
    }

    /**
     * Updates the xml node of the class with the proper pair value and metric values.
     * @param skeleton XML Skeleton
     * @param pack Package name
     * @param clazz Class node in the resulting xml
     * @throws IOException If fails
     */
    private void update(final XML skeleton, final String pack, final XML clazz) throws IOException {
        throw new UnsupportedOperationException(
            new UncheckedText(
                new Joined("", skeleton.toString(), pack, clazz.toString())
            ).asString()
        );
    }

}
