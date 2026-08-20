/*
 * SPDX-FileCopyrightText: Copyright (c) 2017-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.jpeek.calculus.java;

import com.jcabi.xml.XML;
import com.jcabi.xml.XSLDocument;
import java.util.List;
import java.util.Map;
import org.cactoos.io.ResourceOf;
import org.cactoos.io.UncheckedInput;
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
            new XSLDocument(
                new UncheckedInput(
                    new ResourceOf("org/jpeek/metrics/CCM.xsl")
                ).stream()
            ).transform(skeleton),
            skeleton
        );
    }

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
