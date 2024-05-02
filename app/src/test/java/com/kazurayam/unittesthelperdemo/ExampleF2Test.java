package com.kazurayam.unittesthelperdemo;

import com.kazurayam.unittest.TestOutputOrganizer;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;

public class ExampleF2Test {

    private static final TestOutputOrganizer too =
            new TestOutputOrganizer.Builder(ExampleF2Test.class)
                    .subPathUnderOutputDirectory(ExampleF2Test.class)
                    .build();

    @Test
    void test_copyDir() throws IOException {
        Path methodDir = too.createMethodOutputDirectory("test_copyDir");
        // given
        Path sourceDir = methodDir.resolve("source");
        Path sourceFile = sourceDir.resolve("foo/hello.txt");
        Files.createDirectories(sourceFile.getParent());
        Files.write(sourceFile, "Hello, world!".getBytes(StandardCharsets.UTF_8));
        Path targetDir = methodDir.resolve("target");
        // when
        too.copyDir(sourceDir, targetDir);
        // then
        Path targetFile = targetDir.resolve("foo/hello.txt");
        assertThat(targetFile).exists();
    }

}
