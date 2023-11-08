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
                .outputDirPath("build/tmp/testOutput")
                .subDirPath(CopyDirTest.class.getName()).build();
        too.cleanOutputSubDirectory();
    }

    @Test
    void test_constructor() throws IOException {
        // given
        Path sourceDir = too.resolveOutput("source");
        Path sourceFile = too.resolveOutput("source/foo/hello.txt");
        Files.write(sourceFile, "Hello, world!".getBytes(StandardCharsets.UTF_8));
        Path targetDir = too.resolveOutput("target");
        Path targetFile = too.resolveOutput("target/foo/hello.txt");
        // when
        Files.walkFileTree(sourceDir, new CopyDir(sourceDir, targetDir));
        // then
        assertThat(targetFile).exists();
    }
}
