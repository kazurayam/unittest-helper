package com.kazurayam.unittest;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;

public class DeleteDirTest {

    static TestOutputOrganizer too;

    @BeforeAll
    static void setupClass() throws IOException {
        too = new TestOutputOrganizer.Builder(DeleteDirTest.class)
                .outputDirectoryRelativeToProject("build/tmp/testOutput")
                .subOutputDirectory(CopyDirTest.class).build();
        too.cleanSubOutputDirectory();
    }


    /**
     * call the static method cleanDirectoryRecursively(Path) of
     * DeleteDir class to remove a directory recursively
     */
    @Test
    public void test_deleteDirectoryRecursively() throws IOException {
        // given
        Path dir = FileSystems.getDefault().getPath("build/work");
        Files.createDirectories(dir);
        Path file = dir.resolve("foo.txt");
        Files.write(file, "Hello, world!".getBytes(StandardCharsets.UTF_8));
        // when
        DeleteDir.deleteDirectoryRecursively(dir);
        // then
        assertThat(file).doesNotExist();
        assertThat(dir).doesNotExist();
    }
}
