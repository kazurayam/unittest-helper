package com.kazurayam.unittest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Objects;

/**
 * original: https://www.codejava.net/java-se/file-io/java-nio-copy-file-or-directory-examples
 */
public class CopyDir extends SimpleFileVisitor<Path> {
    private static final Logger logger = LoggerFactory.getLogger(CopyDir.class);
    private final Path sourceDir;
    private final Path targetDir;

    private final Option option;

    /**
     * copy the source directory and its content files/directories to the target directory
     * recursively.
     * If the targetDir is not existing, will create it.
     *
     * @param sourceDir source directory to copy from
     * @param targetDir target directory to copy into
     * @throws IOException any error while i/o
     */
    public CopyDir(Path sourceDir, Path targetDir) throws IOException {
        this(sourceDir, targetDir, Option.REPLACE_EXISTING);
    }

    /**
     * copy the source directory and its content files/directories to the target directory
     * recursively.
     * If the targetDir is not existing, will create it.
     *
     * @param sourceDir source directory to copy from
     * @param targetDir target directory to copy into
     * @param option specifies what to do when the target file is found existing
     * @throws IOException any error while i/o
     */
    public CopyDir(Path sourceDir, Path targetDir, Option option) throws IOException {
        Objects.requireNonNull(sourceDir, "sourceDir must not be null");
        Objects.requireNonNull(option, "option must not be null");
        this.sourceDir = sourceDir;
        this.targetDir = targetDir;
        if ( ! Files.exists(targetDir)) {
            Files.createDirectories(targetDir);
        }
        this.option = option;
    }

    @Override
    public FileVisitResult visitFile(Path file, BasicFileAttributes attributes) {
        try {
            Path targetFile = targetDir.resolve(sourceDir.relativize(file));
            if (Files.exists(targetFile)) {
                if (option == Option.REPLACE_EXISTING) {
                    Files.copy(file, targetFile,
                            StandardCopyOption.REPLACE_EXISTING,
                            StandardCopyOption.COPY_ATTRIBUTES);
                } else {
                    logger.debug(targetFile + " is found; skipped overwriting it.");
                }
            } else {
                Files.copy(file, targetFile,
                        StandardCopyOption.COPY_ATTRIBUTES);
            }

        } catch (IOException e) {
            logger.warn(e.getMessage());
        }
        return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult preVisitDirectory(Path dir,
                                             BasicFileAttributes attributes) {
        try {
            Path newDir = targetDir.resolve(sourceDir.relativize(dir));
            if (!Files.exists(newDir)) {
                Files.createDirectory(newDir);
            }
        } catch (IOException ex) {
            logger.warn(ex.getMessage());
        }
        return FileVisitResult.CONTINUE;
    }

    /**
     * Parameter to the CopyDir constructor
     */
    public enum Option {
        /**
         * replace the existing target file with the source file
         */
        REPLACE_EXISTING,
        /**
         * skip copying the file when the target file exists
         */
        SKIP_IF_EXISTING;
    }

    /**
     * copy the source directory and its content files/directories to the target directory
     * recursively.
     * If the targetDir is not existing, will create it.
     *
     * @param sourceDir source directory to copy from
     * @param targetDir target directory to copy into
     * @throws IOException any error while i/o
     */
    public static void copyDir(Path sourceDir, Path targetDir) throws IOException {
        Files.walkFileTree(sourceDir, new CopyDir(sourceDir, targetDir));
    }

    /**
     * usage: "java com.kazurayam.unittest.CopyDir sourceDirPath targetDirPath
     * @param args [sourceDir,targetDir]
     * @throws IOException any error while i/o
     */
    public static void main(String[] args) throws IOException {
        Path sourceDir = Paths.get(args[0]);
        Path targetDir = Paths.get(args[1]);
        Files.walkFileTree(sourceDir, new CopyDir(sourceDir, targetDir));
    }
}
