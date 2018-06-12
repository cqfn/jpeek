package org.jpeek;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

public final class FileTarget implements Target {

	private final File target;
	private final Boolean overwrite;

	public FileTarget(File target, Boolean overwrite) {
		this.target = target;
		this.overwrite = overwrite;
	}

	public Path toPath() throws IOException {
		if (this.target.exists()) {
            if (this.overwrite) {
                deleteDir(this.target);
            } else {
                throw new IllegalStateException(
                    String.format(
                        "Directory/file already exists: %s",
                        this.target.getAbsolutePath()
                    )
                );
            }
        }
		return target.toPath();
	}
	
	/**
     * Deletes the directory recursively.
     * @param dir The directory
     * @throws IOException If an I/O error occurs
     */
    private static void deleteDir(final File dir) throws IOException {
        Files.walkFileTree(
            dir.toPath(),
            new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(
                    final Path file,
                    final BasicFileAttributes attrs) throws IOException {
                    Files.delete(file);
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult postVisitDirectory(
                    final Path dir,
                    final IOException error) throws IOException {
                    Files.delete(dir);
                    return FileVisitResult.CONTINUE;
                }
            }
        );
        com.jcabi.log.Logger.info(Main.class, "Directory %s deleted", dir);
    }
	
	
}
