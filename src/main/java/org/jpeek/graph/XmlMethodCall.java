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

/**
 * Serialize method call to a string.
 *
 * @since 1.0
 */
public final class XmlMethodCall extends TextEnvelope {

    /**
     * Ctor.
     *
     * @param call Call operation as XML.
     */
    XmlMethodCall(final XML call) {
        super(
            new Joined(
                "", call.xpath("name/text()").get(0),
                ".", new XmlMethodArgs(call).toString()
            )
        );
    }
}
