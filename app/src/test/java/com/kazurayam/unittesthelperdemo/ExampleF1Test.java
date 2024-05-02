package com.kazurayam.unittesthelperdemo;

import com.kazurayam.unittest.DeleteDir;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.assertj.core.api.Assertions.assertThat;

public class ExampleF1Test {

    @Test
    public void test_deleteDirectoryRecursively() throws IOException {
        // given
        Path dir = Paths.get("build/work");
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
