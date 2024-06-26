package com.kazurayam.unittesthelperdemo;

import com.kazurayam.unittest.TestOutputOrganizer;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Collectors;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class ExampleC4Test {

    private static final Logger log = LoggerFactory.getLogger(ExampleC4Test.class);
    private static final TestOutputOrganizer too =
            new TestOutputOrganizer.Builder(ExampleC4Test.class)
                    .outputDirectoryRelativeToProject("build/tmp/testOutput")
                    .subOutputDirectory(ExampleC4Test.class)
                    .build();

    @BeforeAll
    public static void beforeAll() throws IOException {
        too.cleanClassOutputDirectory();
    }

    @Test
    public void test_write_a_file() throws Exception {
        // when
        Path classOutputDir = too.createClassOutputDirectory();
        log.info("[test_write_a_file] classOutputDir: " +
                too.toHomeRelativeString(classOutputDir));

        Path file = classOutputDir.resolve("sample.txt");
        Files.write(file, "Hello, world!".getBytes(StandardCharsets.UTF_8));
        log.info("[test_write_a_file] created a file " +
                too.toHomeRelativeString(file));
        // then
        assertThat(numberOfChildren(classOutputDir)).isEqualTo(1);
    }

    int numberOfChildren(Path dir) throws IOException {
        return Files.list(dir).collect(Collectors.toList()).size();
    }

}
