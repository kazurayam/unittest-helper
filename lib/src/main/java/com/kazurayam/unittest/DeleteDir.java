package com.kazurayam.unittest;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.Objects;

/**
 * A utility class that implements deleteDirectoryRecursively method
 */
public class DeleteDir {

    /**
     * delete the target directory and its content files/directories recursively.
     *
     * @param dir directory to delete
     * @throws IOException any error while deletion
     */
    public static void deleteDirectoryRecursively(Path dir) throws IOException {
        Objects.requireNonNull(dir);
        if (!Files.exists(dir)) {
            throw new IOException(dir.toString() + " does not exist");
        }
        Files.walk(dir)
                .sorted(Comparator.reverseOrder())
                .forEach(p -> {
                    try {
                        if (Files.exists(p)) {
                            Files.delete(p);
                        }
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
    }
}