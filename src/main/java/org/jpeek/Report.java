/*
 * SPDX-FileCopyrightText: Copyright (c) 2017-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.jpeek;

import java.io.IOException;
import java.nio.file.Path;

/**
 * Report interface.
 * @since 0.1
 */
public interface Report {

    /**
     * Save report.
     * @param target Target dir
     * @return TRUE if everything was good
     * @throws IOException If fails
     */
    boolean save(Path target) throws IOException;

}
