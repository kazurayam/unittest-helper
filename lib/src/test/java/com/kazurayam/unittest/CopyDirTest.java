package com.kazurayam.unittest;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;

public class CopyDirTest {

    static TestOutputOrganizer too;

    @BeforeAll
    static void setupClass() throws IOException {
        too = new TestOutputOrganizer.Builder(CopyDirTest.class)
                .outputDirectoryRelativeToProject("build/tmp/testOutput")
                .subOutputDirectory(CopyDirTest.class).build();
        too.cleanSubOutputDirectory();
    }

    @Test
    void test_visitorConstructor() throws IOException {
        // given
        Path sourceDir = too.resolveSubOutputDirectory().resolve("source");
        Path sourceFile = too.resolveSubOutputDirectory().resolve("source/foo/hello.txt");
        Files.createDirectories(sourceFile.getParent());
        Files.write(sourceFile, "Hello, world!".getBytes(StandardCharsets.UTF_8));
        Path targetDir = too.resolveSubOutputDirectory().resolve("target");
        Path targetFile = too.resolveSubOutputDirectory().resolve("target/foo/hello.txt");
        // when
        Files.walkFileTree(sourceDir, new CopyDir(sourceDir, targetDir));
        // then
        assertThat(targetFile).exists();
    }
}
