/*
 * SPDX-FileCopyrightText: Copyright (c) 2017-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.jpeek.metrics;

import com.jcabi.xml.XSLDocument;
import java.io.InputStream;
import org.cactoos.io.ResourceOf;
import org.jpeek.FakeBase;
import org.jpeek.skeleton.Skeleton;

/**
 * Metric test helper.
 * @since 0.28
 */
public final class MetricBase {

    /**
     * XSL document.
     */
    private final XSLDocument xsl;

    /**
     * Ctor.
     * @param path Path to the xsl
     * @throws Exception If file not found.
     */
    public MetricBase(final String path) throws Exception {
        this(new ResourceOf(path).stream());
    }

    /**
     * Ctor.
     * @param input XSL input stream
     */
    private MetricBase(final InputStream input) {
        this.xsl = new XSLDocument(input);
    }

    /**
     * Transform a class to assertable xml.
     * @param name File name (without an extension) of a class to transform
     * @return Xml result of the transformation
     */
    public MetricReport transform(final String name) {
        return new MetricReport(
            name,
            this.xsl.transform(
                new Skeleton(
                    new FakeBase(name)
                ).xml()
            )
        );
    }
}
