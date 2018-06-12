package org.jpeek;

import java.io.IOException;
import java.nio.file.Path;

public interface Target {

	public Path toPath() throws IOException;
}
