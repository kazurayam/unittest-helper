package com.kazurayam.unittesthelperdemo;

import com.kazurayam.unittest.TestOutputOrganizer;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.assertj.core.api.Assertions.assertThat;

public class Ex12Test {

    @Test
    public void test_cleanDirectoryRecursively() throws IOException {
        // given
        Path dir = Paths.get("build/work");
        Files.createDirectories(dir);
        Path file = dir.resolve("foo.txt");
        Files.write(file, "Hello, world!".getBytes(StandardCharsets.UTF_8));
        // when
        TestOutputOrganizer.cleanDirectoryRecursively(dir);
        // then
        assertThat(file).doesNotExist();
        assertThat(dir).doesNotExist();
    }
}
