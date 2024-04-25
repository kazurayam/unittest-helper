package com.kazurayam.unittest;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;

public class DeleteDirTest {

    static TestOutputOrganizer too;

    @BeforeAll
    static void setupClass() throws IOException {
        too = new TestOutputOrganizer.Builder(DeleteDirTest.class)
                .outputDirectoryPathRelativeToProject("build/tmp/testOutput")
                .subPathUnderOutputDirectory(CopyDirTest.class).build();
        too.cleanOutputSubDirectory();
    }

    @Test
    void test_deleteDirectoryRecursively() throws IOException {
        // given
        Path sourceDir = too.resolveOutputSubDirectory().resolve("source");
        Path sourceFile = too.resolveOutputSubDirectory().resolve("source/foo/hello.txt");
        Files.createDirectories(sourceFile.getParent());
        Files.write(sourceFile, "Hello, world!".getBytes(StandardCharsets.UTF_8));
        Path targetDir = too.resolveOutputSubDirectory().resolve("target");
        Path targetFile = too.resolveOutputSubDirectory().resolve("target/foo/hello.txt");
        Files.walkFileTree(sourceDir, new CopyDir(sourceDir, targetDir));
        assertThat(targetFile).exists();
        // when
        DeleteDir.deleteDirectoryRecursively(targetDir);
        // then
        assertThat(targetFile).doesNotExist();
    }

}
