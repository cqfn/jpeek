/*
 * The MIT License (MIT)
 *
 * SPDX-FileCopyrightText: Copyright (c) 2017-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.jpeek.skeleton;

import org.cactoos.Scalar;
import org.jpeek.FakeBase;
import org.xembly.Directives;
import org.xembly.Xembler;

/**
 * String XML representation for input class name.
 * @since 0.27
 */
final class ClassAsXml implements Scalar<String> {
    /**
     * Class name for conversion to XML.
     */
    private final String name;

    /**
     * Ctor.
     * @param name Class name for conversion
     */
    ClassAsXml(final String name) {
        this.name = name;
    }

    @Override
    public String value() {
        return new Xembler(
            new Directives().add("class").append(
                new XmlClass(
                    new Classes(
                        new FakeBase(this.name)
                    ).iterator().next()
                )
            )
        ).xmlQuietly();
    }
}
