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

public class Ex08Test {

    private static final Logger log = LoggerFactory.getLogger(Ex08Test.class);
    private static final TestOutputOrganizer too =
            new TestOutputOrganizer.Builder(Ex08Test.class)
                    .outputDirPath("build/tmp/testOutput")
                    .subDirPath(Ex08Test.class)
                    .build();

    @BeforeAll
    public static void beforeAll() throws IOException {
        too.cleanClassOutputDirectory();
    }

    @Test
    public void test_write_a_file() throws Exception {
        // when
        Path classOutputDir = too.getClassOutputDirectory();
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
