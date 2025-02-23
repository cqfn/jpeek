/*
 * SPDX-FileCopyrightText: Copyright (c) 2017-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.jpeek;

import java.io.IOException;
import java.nio.file.Path;

/**
 * File Target.
 *
 * @since 0.26.4
 */
public interface Target {

    /**
     * Returns the Path of Target.
     * @return The Path of Target
     * @throws IOException If something goes wrong
     */
    Path toPath() throws IOException;
}
