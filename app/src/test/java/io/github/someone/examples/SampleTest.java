package io.github.someone.examples;

import com.kazurayam.unittest.TestOutputOrganizer;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.assertj.core.api.Assertions.assertThat;

public class SampleTest {

    private static TestOutputOrganizer too =
            new TestOutputOrganizer.Builder(SampleTest.class)
                    .subPathUnderOutputDirectory(SampleTest.class).build();

    private static DateTimeFormatter dtf;
    private static final Logger log = LoggerFactory.getLogger(SampleTest.class);

    @BeforeAll
    public static void beforeAll() throws IOException {
        log.info("project directory: " + too.toHomeRelativeString(too.getProjectDirectory()));
        // remove the "test-output/io.github.someone.somestuff.SampleTest" directory recursively
        too.cleanClassOutputDirectory();
        dtf = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
    }

    @Test
    public void test_write_file() throws IOException {
        Path methodOutputDir = too.createMethodOutputDirectory("test_write_file");
        LocalDateTime ldt = LocalDateTime.now();
        Path p = methodOutputDir.resolve(String.format("sample_%s.txt", dtf.format(ldt)));
        Files.write(p, "Hello, world!".getBytes(StandardCharsets.UTF_8));
        assertThat(p).isNotNull().exists();
        assertThat(p.toFile().length()).isGreaterThan(0);
        log.info("output is found at " + too.toHomeRelativeString(p));
    }

    @Test
    public void test_write_file_once_more() throws IOException {
        Path methodOutputDir = too.createMethodOutputDirectory("test_write_file_once_more");
        LocalDateTime ldt = LocalDateTime.now();
        Path p = methodOutputDir.resolve(String.format("sample_%s.txt", dtf.format(ldt)));
        Files.write(p, "Hello, world!".getBytes(StandardCharsets.UTF_8));
        assertThat(p).isNotNull().exists();
        assertThat(p.toFile().length()).isGreaterThan(0);
        log.info("output is found at " + too.toHomeRelativeString(p));
    }
}
