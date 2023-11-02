package com.kazurayam.unittesthelperdemo;

import com.kazurayam.unittest.TestHelper;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class WithHelperTest {

    @Test
    public void test_getProjectDir() {
        Path projectDir = new TestHelper(this.getClass()).getProjectDir();
        System.out.println("[test_getProjectDir] projectDir = " +
                TestHelper.toHomeRelativeString(projectDir));
    }

    @Test
    public void test_getOutputDir_as_default() {
        Path outputDir = new TestHelper(this.getClass()).getOutputDir();
        System.out.println("[test_getOutputDir_as_default] outputDir = " +
                TestHelper.toHomeRelativeString(outputDir));
    }

    @Test
    public void test_getOutputDir_custom() {
        Path outputDir = new TestHelper(this.getClass())
                .setOutputDirPath(Paths.get("test-output-another"))
                .getOutputDir();
        System.out.println("[test_getOutputDir_as_default] outputDir = " +
                TestHelper.toHomeRelativeString(outputDir));
    }

    /*
     * will create a file `<projectDir>/test-output/sample2.txt`
     */
    @Test
    public void test_write_into_the_default_dir() throws Exception {
        Path p = new TestHelper(this.getClass())
                .resolveOutput("sample2.txt");
        Files.writeString(p, "Hello, world!");
        System.out.println("[test_write_into_the_default_dir] p = " +
                TestHelper.toHomeRelativeString(p));
    }

    @Test
    public void test_write_into_subdir_under_the_default_dir() throws Exception {
        Path p = new TestHelper(this.getClass())
                .resolveOutput("sub/sample4.txt");
        Files.writeString(p, "Hello, world!");
        System.out.println("[test_write_into_subdir_under_the_default_dir] p = " + TestHelper.toHomeRelativeString(p));
    }

    /*
     * will create a file `<projectDir>/build/tmp/testOutput/sample3.txt`
     */
    @Test
    public void test_write_into_custom_dir() throws Exception {
        Path p = new TestHelper(this.getClass())
                .setOutputDirPath(Paths.get("build/tmp/testOutput"))
                .resolveOutput("sample6.txt");
        Files.writeString(p, "Hello, world!");
        System.out.println("[test_write_into_custom_dir] p = " + TestHelper.toHomeRelativeString(p));
    }


    @Test
    public void test_write_into_another_custom_dir() throws Exception {
        Path outdir = new TestHelper(this.getClass())
                .setOutputDirPath(Paths.get("test-output-another"))
                .getOutputDir();
        Files.createDirectories(outdir);
        Path p = outdir.resolve("sample7.txt");
        Files.writeString(p, "Hello, world!");
        System.out.println("[test_write_into_another_custom_dir] p = " +
                TestHelper.toHomeRelativeString(p));
    }


}
