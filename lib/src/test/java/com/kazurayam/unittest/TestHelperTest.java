package com.kazurayam.unittest;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.assertj.core.api.Assertions.assertThat;

public class TestHelperTest {

    private static final Logger log = LoggerFactory.getLogger(TestHelperTest.class);

    @Test
    public void test_getProjectDir() {
        Path p = new TestHelper(this.getClass()).getProjectDir();
        log.info("[test_getProjectDirViaClasspath] project dir : " + p);
        assertThat(p.getFileName().toString()).isEqualTo("lib");
        assertThat(p.getFileName().toString()).isNotEqualTo("unittest-helper");
    }

    @Test
    public void test_getOutputDir_default() {
        Path p = new TestHelper(this.getClass()).getOutputDir();
        log.info("[test_getOutputDir_default] output dir : " + p);
        assertThat(p.getFileName().toString()).isEqualTo("test-output");
    }

    @Test
    public void test_getOutputDir_custom() {
        String dirName = "customDir";
        TestHelper th = new TestHelper(this.getClass()).setOutputDirPath(Paths.get(dirName));
        Path p = th.getOutputDir();
        log.info("[test_getOutputDir_custom] output dir : " + p);
        assertThat(p.getFileName().toString()).isEqualTo(dirName);
    }

    @Test
    public void test_resolveOutput() throws Exception {
        Path p = new TestHelper(this.getClass())
                .resolveOutput("hello.json");
        Files.write(p, "Hello, world!".getBytes(StandardCharsets.UTF_8));
        assertThat(p.getParent()).exists();
        assertThat(p.getParent().getFileName().toString())
                .isEqualTo("test-output");
        assertThat(p.getParent()   // expecting the test-output directory
                .getParent()       // expecting the lib directory
                .getFileName().toString())
                .isEqualTo("lib");
    }

    @Test
    public void test_resolveOutput_with_subDir() throws Exception {
        Path p = new TestHelper(this.getClass())
                .setSubDir(Paths.get(this.getClass().getName()))
                .resolveOutput("test_resolveOutput_with_subDir/hello.json");
        Files.write(p, "Hello, world!".getBytes(StandardCharsets.UTF_8));
        assertThat(p.getParent()).exists();
    }

    @Test
    public void test_resolveOutput_into_custom_location() throws Exception {
        TestHelper th = new TestHelper(this.getClass())
                .setOutputDirPath(Paths.get("build/tmp/testOutput"));
        Path p = th.resolveOutput("hello.txt");
        Files.write(p, "Hello, world!".getBytes(StandardCharsets.UTF_8));
        assertThat(p.getParent()                   // expecting testOutput
                .getFileName().toString())
                .isEqualTo("testOutput");
        assertThat(p.getParent()                   // expecting testOutput
                .getParent()                       // expecting tmp
                .getFileName().toString())
                .isEqualTo("tmp");
        assertThat(p.getParent()                   // expecting testOutput
                .getParent()                       // expecting tmp
                .getParent()                       // expecting build
                .getFileName().toString())
                .isEqualTo("build");
    }

    @Test
    public void test_toHomeRelativeString_simple() {
        Path p = new TestHelper(this.getClass()).getProjectDir();
        String s = TestHelper.toHomeRelativeString(p);
        System.out.println("[test_toHomeRelativeString_simple] s = " + s);
        assertThat(s).isEqualTo("~/github/unittest-helper/lib");
    }

    @Test
    public void test_toHomeRelativeString_simple_more() {
        Path p = new TestHelper(this.getClass()).resolveOutput("foo.txt");
        String s = TestHelper.toHomeRelativeString(p);
        assertThat(s).isEqualTo(
                "~/github/unittest-helper/lib/test-output/foo.txt");
    }

    @Test
    public void test_toHomeRelativeString_HOME_itself() {
        Path p = Paths.get(System.getProperty("user.home"));
        String s = TestHelper.toHomeRelativeString(p);
        assertThat(s).isEqualTo("~/");
    }

    @Test
    public void test_toHomeRelativeString_when_not_relative() {
        Path p = Paths.get("/Applications");
        String s = TestHelper.toHomeRelativeString(p);
        assertThat(s).isEqualTo("/Applications");
    }

    @Test
    public void test_cleanOutputDirectory() throws IOException {
        // given
        TestHelper th = new TestHelper(this.getClass());
        Path p = th.resolveOutput("sub/foo.txt");
        Files.write(p, "Hello, world!".getBytes(StandardCharsets.UTF_8));
        assertThat(p).exists();
        // when
        th.cleanOutputDirectory();
        // then
        Path od = th.getOutputDir();
        assertThat(od).doesNotExist();
    }
}
