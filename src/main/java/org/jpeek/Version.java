/*
 * SPDX-FileCopyrightText: Copyright (c) 2017-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.jpeek;

import java.io.IOException;
import org.cactoos.Scalar;
import org.cactoos.io.ResourceOf;
import org.cactoos.scalar.PropertiesOf;

/**
 * Version.
 *
 * <p>There is no thread-safety guarantee.
 *
 * @since 0.11
 */
public final class Version implements Scalar<String> {

    @Override
    public String value() throws IOException {
        return new PropertiesOf(
            new ResourceOf(
                "org/jpeek/jpeek.properties"
            )
        ).value().getProperty("org.jpeek.version");
    }

}
