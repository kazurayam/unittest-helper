package com.kazurayam.unittesthelperdemo;

import com.kazurayam.unittest.TestHelper;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class WithHelperTest {

    @Test
    public void test_getProjectDir() {
        TestHelper th = new TestHelper.Builder(this.getClass()).build();
        Path projectDir = th.getProjectDir();
        System.out.println("[test_getProjectDir] projectDir = " +
                TestHelper.toHomeRelativeString(projectDir));
    }

    @Test
    public void test_getOutputDir_as_default() {
        TestHelper th = new TestHelper.Builder(this.getClass()).build();
        Path outputDir = th.getOutputDir();
        System.out.println("[test_getOutputDir_as_default] outputDir = " +
                TestHelper.toHomeRelativeString(outputDir));
    }

    @Test
    public void test_getOutputDir_custom() {
        TestHelper th = new TestHelper.Builder(this.getClass())
                .outputDirPath(Paths.get("test-output-another"))
                .build();
        Path outputDir = th.getOutputDir();
        System.out.println("[test_getOutputDir_as_default] outputDir = " +
                TestHelper.toHomeRelativeString(outputDir));
    }

    /*
     * will create a file `<projectDir>/test-output/sample2.txt`
     */
    @Test
    public void test_write_into_the_default_dir() throws Exception {
        TestHelper th = new TestHelper.Builder(this.getClass()).build();
        Path p = th.resolveOutput("sample4.txt");
        Files.write(p, "Hello, world!".getBytes(StandardCharsets.UTF_8));
        System.out.println("[test_write_into_the_default_dir] p = " +
                TestHelper.toHomeRelativeString(p));
    }

    @Test
    public void test_write_into_subdir_under_the_default_dir() throws Exception {
        TestHelper th = new TestHelper.Builder(this.getClass()).build();
        Path p = th.resolveOutput("sub/sample5.txt");
        Files.write(p, "Hello, world!".getBytes(StandardCharsets.UTF_8));
        System.out.println("[test_write_into_subdir_under_the_default_dir] p = " + TestHelper.toHomeRelativeString(p));
    }

    /*
     * will create a file `<projectDir>/build/tmp/testOutput/sample3.txt`
     */
    @Test
    public void test_write_into_custom_dir() throws Exception {
        TestHelper th =
                new TestHelper.Builder(this.getClass())
                        .outputDirPath(Paths.get("build/tmp/testOutput"))
                        .build();
        Path p = th.resolveOutput("sample6.txt");
        Files.write(p, "Hello, world!".getBytes(StandardCharsets.UTF_8));
        System.out.println("[test_write_into_custom_dir] p = " + TestHelper.toHomeRelativeString(p));
    }

    @Test
    public void test_write_into_another_custom_dir() throws Exception {
        TestHelper th =
                new TestHelper.Builder(this.getClass())
                        .outputDirPath(Paths.get("test-output-another"))
                        .build();
        Path outdir = th.getOutputDir();
        Files.createDirectories(outdir);
        Path p = outdir.resolve("sample7.txt");
        Files.write(p, "Hello, world!".getBytes(StandardCharsets.UTF_8));
        System.out.println("[test_write_into_another_custom_dir] p = " +
                TestHelper.toHomeRelativeString(p));
    }
}
