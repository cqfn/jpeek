package org.jpeek;

import java.io.IOException;
import java.nio.file.Path;

/**
 * Single report.
 *
 * <p>There is no thread-safety guarantee.
 *
 * @author Vladyslav Halchenko (valh@tuta.io)
 * @version $Id$
 * @since 0.30.2
 */
public interface Report {
    /**
     * Save report.
     * @param target Target dir
     * @throws IOException If fails
     */
    void save(final Path target) throws IOException;
}
