/*
 * The MIT License (MIT)
 *
 * SPDX-FileCopyrightText: Copyright (c) 2017-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.jpeek.skeleton;

import org.cactoos.text.Joined;
import org.cactoos.text.TextEnvelope;
import org.cactoos.text.UncheckedText;

/**
 * A fully qualified name of a field, an unambiguous name
 * that specifies field without regard
 * to the context of the call.
 * @since 0.29
 */
public final class QualifiedName extends TextEnvelope {
    /**
     * Ctor.
     * @param owner The class the attribute belongs to
     * @param attr The name of the field
     */
    public QualifiedName(final String owner, final String attr) {
        super(
            new UncheckedText(
                new Joined(
                    ".",
                    owner.replace('/', '.'),
                    attr
                )
            )
        );
    }
}
