package com.kazurayam.unittesthelperdemo;

import com.kazurayam.unittest.TestOutputOrganizer;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class OrganizerPresentTest {

    @Test
    public void test_getProjectDir() {
        TestOutputOrganizer too = new TestOutputOrganizer.Builder(this.getClass()).build();
        Path projectDir = too.getProjectDir();
        System.out.println("[test_getProjectDir] projectDir = " +
                TestOutputOrganizer.toHomeRelativeString(projectDir));
    }

    @Test
    public void test_getOutputDir_as_default() {
        TestOutputOrganizer too = new TestOutputOrganizer.Builder(this.getClass()).build();
        Path outputDir = too.getOutputDirectory();
        System.out.println("[test_getOutputDir_as_default] outputDir = " +
                TestOutputOrganizer.toHomeRelativeString(outputDir));
    }

    @Test
    public void test_getOutputDir_custom() {
        TestOutputOrganizer too = new TestOutputOrganizer.Builder(this.getClass())
                .outputDirPath("test-output-another")
                .build();
        Path outputDir = too.getOutputDirectory();
        System.out.println("[test_getOutputDir_as_default] outputDir = " +
                TestOutputOrganizer.toHomeRelativeString(outputDir));
    }

    /*
     * will create a file `<projectDir>/test-output/sample2.txt`
     */
    @Test
    public void test_write_into_the_default_dir() throws Exception {
        TestOutputOrganizer too = new TestOutputOrganizer.Builder(this.getClass()).build();
        Path p = too.resolveOutput("sample4.txt");
        Files.write(p, "Hello, world!".getBytes(StandardCharsets.UTF_8));
        System.out.println("[test_write_into_the_default_dir] p = " +
                TestOutputOrganizer.toHomeRelativeString(p));
    }

    @Test
    public void test_write_into_subdir_under_the_default_dir() throws Exception {
        TestOutputOrganizer too = new TestOutputOrganizer.Builder(this.getClass()).build();
        Path p = too.resolveOutput("sub/sample5.txt");
        Files.write(p, "Hello, world!".getBytes(StandardCharsets.UTF_8));
        System.out.println("[test_write_into_subdir_under_the_default_dir] p = " + TestOutputOrganizer.toHomeRelativeString(p));
    }

    /*
     * will create a file `<projectDir>/build/tmp/testOutput/sample3.txt`
     */
    @Test
    public void test_write_into_custom_dir() throws Exception {
        TestOutputOrganizer too =
                new TestOutputOrganizer.Builder(this.getClass())
                        .outputDirPath("build/tmp/testOutput")
                        .build();
        Path p = too.resolveOutput("sample6.txt");
        Files.write(p, "Hello, world!".getBytes(StandardCharsets.UTF_8));
        System.out.println("[test_write_into_custom_dir] p = " + TestOutputOrganizer.toHomeRelativeString(p));
    }

    @Test
    public void test_write_into_another_custom_dir() throws Exception {
        TestOutputOrganizer too =
                new TestOutputOrganizer.Builder(this.getClass())
                        .outputDirPath("test-output-another")
                        .build();
        Path outdir = too.getOutputDirectory();
        Files.createDirectories(outdir);
        Path p = outdir.resolve("sample7.txt");
        Files.write(p, "Hello, world!".getBytes(StandardCharsets.UTF_8));
        System.out.println("[test_write_into_another_custom_dir] p = " +
                TestOutputOrganizer.toHomeRelativeString(p));
    }
}
