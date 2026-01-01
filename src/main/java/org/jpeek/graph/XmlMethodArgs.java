/*
 * SPDX-FileCopyrightText: Copyright (c) 2017-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.jpeek.graph;

import com.jcabi.xml.XML;
import org.cactoos.text.Joined;
import org.cactoos.text.TextEnvelope;

/**
 * Serialize method arguments to a string.
 *
 * @since 1.0
 */
public final class XmlMethodArgs extends TextEnvelope {

    /**
     * Ctor.
     *
     * @param method Method as XML
     */
    XmlMethodArgs(final XML method) {
        super(new Joined(":", method.xpath("args/arg/@type")));
    }
}
