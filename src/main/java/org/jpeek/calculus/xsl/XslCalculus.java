/*
 * SPDX-FileCopyrightText: Copyright (c) 2017-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.jpeek.calculus.xsl;

import com.jcabi.xml.Sources;
import com.jcabi.xml.XML;
import com.jcabi.xml.XSLDocument;
import java.io.IOException;
import java.util.Map;
import org.cactoos.io.ResourceOf;
import org.cactoos.text.FormattedText;
import org.cactoos.text.TextOf;
import org.cactoos.text.UncheckedText;
import org.jpeek.calculus.Calculus;

/**
 * Metrics xsl calculus. Use an xsl sheet to transform the input skeleton into
 * the xml containing the calculation.
 * @since 0.30.9
 */
public final class XslCalculus implements Calculus {

    @Override
    public XML node(final String metric, final Map<String, Object> params,
        final XML skeleton) throws IOException {
        return new XSLDocument(
            new UncheckedText(
                new TextOf(
                    new ResourceOf(
                        new FormattedText("org/jpeek/metrics/%s.xsl", metric)
                    )
                )
            ).asString(),
            Sources.DUMMY,
            params
        ).transform(skeleton);
    }

}
