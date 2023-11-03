package io.github.someone.somestuff;

import com.kazurayam.unittest.TestOutputOrganizer;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.assertj.core.api.Assertions.assertThat;

public class SampleTest {

    private static TestOutputOrganizer too;

    private DateTimeFormatter dtf;

    @BeforeAll
    public static void beforeAll() throws IOException {
        too = TestOutputOrganizerFactory.create(SampleTest.class);
        too.cleanOutputDirectory();   // remove the test-output dir recursively
    }

    @BeforeEach
    public void setup() {
        dtf = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
    }

    @Test
    public void test_write_file() throws IOException {
        LocalDateTime ldt = LocalDateTime.now();
        Path p = too.resolveOutput(
                String.format("test_write_file/sample_%s.txt", dtf.format(ldt)));
        Files.write(p, "Hello, world!".getBytes(StandardCharsets.UTF_8));
        assertThat(p).isNotNull().exists();
        assertThat(p.toFile().length()).isGreaterThan(0);
        System.out.println("[test_write_file] output is found at " +
                TestOutputOrganizer.toHomeRelativeString(p));
    }

    @Test
    public void test_write_file_once_more() throws IOException {
        LocalDateTime ldt = LocalDateTime.now();
        Path p = too.resolveOutput(
                String.format("test_write_file_once_more/sample_%s.txt", dtf.format(ldt)));
        Files.write(p, "Hello, world!".getBytes(StandardCharsets.UTF_8));
        assertThat(p).isNotNull().exists();
        assertThat(p.toFile().length()).isGreaterThan(0);
        System.out.println("[test_write_file_once_more] output is found at " +
                TestOutputOrganizer.toHomeRelativeString(p));
    }
}
