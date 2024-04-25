package com.kazurayam.unittesthelperdemo;

import com.kazurayam.unittest.TestOutputOrganizer;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class OutputIntoDedicatedDirectoryTest {

    private static Logger log = LoggerFactory.getLogger(OutputIntoDedicatedDirectoryTest.class);

    private static TestOutputOrganizer too =
            new TestOutputOrganizer.Builder(OutputIntoDedicatedDirectoryTest.class).build();

    @Test
    public void test_getProjectDir() {
        Path projectDir = too.getProjectDirectory();
        log.info("[test_getProjectDir] " + projectDir);
        log.info("[test_getProjectDir] " +
                too.toHomeRelativeString(projectDir));
    }

    @Test
    public void test_getOutputDir_as_default() throws IOException {
        Path outputDir = too.createOutputDirectory();
        log.info("[test_getOutputDir_as_default] " +
                too.toHomeRelativeString(outputDir));
    }


    /*
     * will create a file `<projectDir>/test-output/sample.txt`
     */
    @Test
    public void test_write_a_file_into_the_default_output_directory() throws Exception {
        Path file = too.createOutputDirectory().resolve("sample.txt");
        Files.write(file, "Hello, world!".getBytes(StandardCharsets.UTF_8));
        System.out.println("[test_write_into_the_default_output_directory] " +
                too.toHomeRelativeString(file));
    }

    @Test
    public void test_write_into_subdir_under_the_default_output_directory() throws Exception {
        Path file = too.createOutputDirectory().resolve("sub/sample.txt");
        Files.createDirectories(file.getParent());
        Files.write(file, "Hello, world!".getBytes(StandardCharsets.UTF_8));
        log.info("[test_write_into_subdir_under_the_default_output_directory] " +
                too.toHomeRelativeString(file));
    }

    @Test
    public void test_getOutputDir_custom() throws IOException {
        TestOutputOrganizer too2 = new TestOutputOrganizer.Builder(this.getClass())
                .outputDirectoryPathRelativeToProject("test-output-another")
                .build();
        Path outputDir = too2.createOutputDirectory();
        log.info("[test_getOutputDir_custom] " +
                too2.toHomeRelativeString(outputDir));
    }

    /*
     * will create a file `<projectDir>/build/tmp/testOutput/sample3.txt`
     */
    @Test
    public void test_write_into_custom_directory() throws Exception {
        TestOutputOrganizer too3 =
                new TestOutputOrganizer.Builder(this.getClass())
                        .outputDirectoryPathRelativeToProject("build/tmp/testOutput")
                        .build();
        Path file = too3.createOutputDirectory().resolve("sample.txt");
        Files.write(file, "Hello, world!".getBytes(StandardCharsets.UTF_8));
        System.out.println("[test_write_into_custom_directory] " +
                too3.toHomeRelativeString(file));
    }

    @Test
    public void test_write_into_another_custom_directory() throws Exception {
        TestOutputOrganizer too4 =
                new TestOutputOrganizer.Builder(this.getClass())
                        .outputDirectoryPathRelativeToProject("test-output-another")
                        .build();
        Path outputDir = too4.createOutputDirectory();
        Files.createDirectories(outputDir);
        Path file = outputDir.resolve("sample.txt");
        Files.write(file, "Hello, world!".getBytes(StandardCharsets.UTF_8));
        System.out.println("[test_write_into_another_custom_dir] " +
                too4.toHomeRelativeString(file));
    }
}
