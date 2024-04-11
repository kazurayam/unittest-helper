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

    Logger log = LoggerFactory.getLogger(OutputIntoDedicatedDirectoryTest.class);

    @Test
    public void test_getProjectDir() {
        TestOutputOrganizer too =
                new TestOutputOrganizer.Builder(this.getClass()).build();
        Path projectDir = too.getProjectDir();
        log.info("[test_getProjectDir] " + projectDir);
        log.info("[test_getProjectDir] " +
                too.toHomeRelativeString(projectDir));
    }

    @Test
    public void test_getOutputDir_as_default() throws IOException {
        TestOutputOrganizer too = new TestOutputOrganizer.Builder(this.getClass()).build();
        Path outputDir = too.getOutputDirectory();
        log.info("[test_getOutputDir_as_default] " +
                too.toHomeRelativeString(outputDir));
    }

    @Test
    public void test_getOutputDir_custom() throws IOException {
        TestOutputOrganizer too = new TestOutputOrganizer.Builder(this.getClass())
                .outputDirPath("test-output-another")
                .build();
        Path outputDir = too.getOutputDirectory();
        log.info("[test_getOutputDir_custom] " +
                too.toHomeRelativeString(outputDir));
    }

    /*
     * will create a file `<projectDir>/test-output/sample.txt`
     */
    @Test
    public void test_write_a_file_into_the_default_output_directory() throws Exception {
        TestOutputOrganizer too = new TestOutputOrganizer.Builder(this.getClass()).build();
        Path file = too.getOutputDirectory().resolve("sample.txt");
        Files.write(file, "Hello, world!".getBytes(StandardCharsets.UTF_8));
        System.out.println("[test_write_into_the_default_output_directory] " +
                too.toHomeRelativeString(file));
    }

    @Test
    public void test_write_into_subdir_under_the_default_output_directory() throws Exception {
        TestOutputOrganizer too = new TestOutputOrganizer.Builder(this.getClass()).build();
        Path file = too.getOutputDirectory().resolve("sub/sample.txt");
        Files.createDirectories(file.getParent());
        Files.write(file, "Hello, world!".getBytes(StandardCharsets.UTF_8));
        log.info("[test_write_into_subdir_under_the_default_output_directory] " +
                too.toHomeRelativeString(file));
    }

    /*
     * will create a file `<projectDir>/build/tmp/testOutput/sample3.txt`
     */
    @Test
    public void test_write_into_custom_directory() throws Exception {
        TestOutputOrganizer too =
                new TestOutputOrganizer.Builder(this.getClass())
                        .outputDirPath("build/tmp/testOutput")
                        .build();
        Path file = too.getOutputDirectory().resolve("sample.txt");
        Files.write(file, "Hello, world!".getBytes(StandardCharsets.UTF_8));
        System.out.println("[test_write_into_custom_directory] " +
                too.toHomeRelativeString(file));
    }

    @Test
    public void test_write_into_another_custom_directory() throws Exception {
        TestOutputOrganizer too =
                new TestOutputOrganizer.Builder(this.getClass())
                        .outputDirPath("test-output-another")
                        .build();
        Path outputDir = too.getOutputDirectory();
        Files.createDirectories(outputDir);
        Path file = outputDir.resolve("sample.txt");
        Files.write(file, "Hello, world!".getBytes(StandardCharsets.UTF_8));
        System.out.println("[test_write_into_another_custom_dir] " +
                too.toHomeRelativeString(file));
    }
}
