package com.kazurayam.unittest;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

public class TestOutputOrganizerTest {

    private static final Logger log = LoggerFactory.getLogger(TestOutputOrganizerTest.class);

    @Test
    public void test_getProjectDir() {
        TestOutputOrganizer too = new TestOutputOrganizer.Builder(this.getClass()).build();
        Path p = too.getProjectDir();
        log.info("[test_getProjectDirViaClasspath] project dir : " + p);
        assertThat(p.getFileName().toString()).isEqualTo("lib");
        assertThat(p.getFileName().toString()).isNotEqualTo("unittest-helper");
    }

    @Test
    public void test_getOutputDir_default() {
        TestOutputOrganizer too = new TestOutputOrganizer.Builder(this.getClass()).build();
        Path p = too.getOutputDirectory();
        log.info("[test_getOutputDir_default] output dir : " + p);
        assertThat(p.getFileName().toString()).isEqualTo("test-output");
    }

    @Test
    public void test_getOutputDir_custom() {
        String dirName = "customDir";
        TestOutputOrganizer too =
                new TestOutputOrganizer.Builder(this.getClass())
                        .outputDirPath(Paths.get(dirName).toString())
                        .build();
        Path p = too.getOutputDirectory();
        log.info("[test_getOutputDir_custom] output dir : " + p);
        assertThat(p.getFileName().toString()).isEqualTo(dirName);
    }

    @Test
    public void test_resolveOutput() throws Exception {
        TestOutputOrganizer too = new TestOutputOrganizer.Builder(this.getClass()).build();
        Path p = too.resolveOutput("hello.json");
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
    public void test_resolveOutput_with_subDirPath() throws Exception {
        TestOutputOrganizer too =
                new TestOutputOrganizer.Builder(this.getClass())
                        .subDirPath(Paths.get(this.getClass().getName()).toString())
                        .build();
        Path p = too.resolveOutput("test_resolveOutput_with_subDir/hello.json");
        Files.write(p, "Hello, world!".getBytes(StandardCharsets.UTF_8));
        assertThat(p.getParent()).exists();
    }

    @Test
    public void test_resolveOutput_into_custom_location() throws Exception {
        TestOutputOrganizer too =
                new TestOutputOrganizer.Builder(this.getClass())
                        .outputDirPath(Paths.get("build/tmp/testOutput").toString())
                        .build();
        Path p = too.resolveOutput("hello.txt");
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
        TestOutputOrganizer too = new TestOutputOrganizer.Builder(this.getClass()).build();
        Path projectDir = too.getProjectDir();
        String homeRelative = TestOutputOrganizer.toHomeRelativeString(projectDir);
        System.out.println("[test_toHomeRelativeString_simple] " + homeRelative);
        assertThat(homeRelative).isEqualTo("~/github/unittest-helper/lib");
    }

    @Test
    public void test_toHomeRelativeString_simple_more() {
        TestOutputOrganizer too = new TestOutputOrganizer.Builder(this.getClass()).build();
        Path p = too.resolveOutput("foo.txt");
        String homeRelative = TestOutputOrganizer.toHomeRelativeString(p);
        assertThat(homeRelative).isEqualTo(
                "~/github/unittest-helper/lib/test-output/foo.txt");
    }

    @Test
    public void test_toHomeRelativeString_HOME_itself() {
        Path p = Paths.get(System.getProperty("user.home"));
        String s = TestOutputOrganizer.toHomeRelativeString(p);
        assertThat(s).isEqualTo("~/");
    }

    @Test
    public void test_toHomeRelativeString_when_not_relative() {
        Path p = Paths.get("/Applications");
        String s = TestOutputOrganizer.toHomeRelativeString(p);
        assertThat(s).isEqualTo("/Applications");
    }

    @Test
    public void test_cleanOutputDirectory() throws IOException {
        // given
        TestOutputOrganizer too = new TestOutputOrganizer.Builder(this.getClass()).build();
        Path p = too.resolveOutput("sub/foo.txt");
        Files.write(p, "Hello, world!".getBytes(StandardCharsets.UTF_8));
        assertThat(p).exists();
        // when
        too.cleanOutputDirectory();
        // then
        Path od = too.getOutputDirectory();
        assertThat(od).exists();
        assertThat(isEmpty(od)).isTrue();
    }


    @Test
    public void test_cleanOutputSubDirectory() throws IOException {
        // given
        TestOutputOrganizer too =
                new TestOutputOrganizer.Builder(this.getClass())
                        .subDirPath(Paths.get(this.getClass().getName()).toString())
                        .build();
        Path p = too.resolveOutput("foo.txt");
        Files.write(p, "Hello, world!".getBytes(StandardCharsets.UTF_8));
        assertThat(p).exists();
        //
        too.cleanOutputSubDirectory();
        assertThat(too.getOutputSubDirectory()).exists();
        assertThat(isEmpty(too.getOutputSubDirectory())).isTrue();
    }

    boolean isEmpty(Path path) throws IOException {
        if (Files.isDirectory(path)) {
            try (DirectoryStream<Path> directory = Files.newDirectoryStream(path)) {
                return !directory.iterator().hasNext();
            }
        }
        return false;
    }

    @Test
    public void test_sublistPattern() throws IOException {
        // given
        TestOutputOrganizer too =
                new TestOutputOrganizer.Builder(this.getClass())

                        .sublistPattern(Arrays.asList("bin", "classes"))
                        // should compile
                        .build();
        Path p = too.resolveOutput("foo.txt");
        Files.write(p, "Hello, world!".getBytes(StandardCharsets.UTF_8));
        assertThat(p).exists();
        //
        too.cleanOutputSubDirectory();
        assertThat(too.getOutputSubDirectory()).exists();
        assertThat(isEmpty(too.getOutputSubDirectory())).isTrue();
    }
}
