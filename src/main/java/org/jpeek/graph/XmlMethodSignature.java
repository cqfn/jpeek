/*
 * The MIT License (MIT)
 *
 * SPDX-FileCopyrightText: Copyright (c) 2017-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.jpeek.graph;

import com.jcabi.xml.XML;
import org.cactoos.text.Joined;
import org.cactoos.text.TextEnvelope;
import org.cactoos.text.TextOf;

/**
 * Text signature of a class method, extracted from XML Skeleton.
 * @since 0.30.9
 */
public final class XmlMethodSignature extends TextEnvelope {

    /**
     * Ctor.
     * @param clazz The class element of XML skeleton.
     * @param method The method element of XML skeleton.
     */
    public XmlMethodSignature(final XML clazz, final XML method) {
        super(
            new Joined(
                new TextOf("."),
                new TextOf(clazz.xpath("./@id").get(0)),
                new TextOf(method.xpath("@name").get(0)),
                new XmlMethodArgs(method)
            )
        );
    }
}
