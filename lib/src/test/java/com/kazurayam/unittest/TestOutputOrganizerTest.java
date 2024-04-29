package com.kazurayam.unittest;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

public class TestOutputOrganizerTest {

    private static final Logger log = LoggerFactory.getLogger(TestOutputOrganizerTest.class);

    @Test
    public void test_getProjectDirectory() {
        TestOutputOrganizer too = new TestOutputOrganizer.Builder(this.getClass()).build();
        Path p = too.getProjectDirectory();
        log.info("[test_getProjectDir] projectDirectory : " + p);
        assertThat(p.getFileName().toString()).isEqualTo("lib");
        assertThat(p.getFileName().toString()).isNotEqualTo("unittest-helper");
    }

    @Test
    public void test_resolveOutputDirectory_default() {
        TestOutputOrganizer too = new TestOutputOrganizer.Builder(this.getClass()).build();
        Path p = too.resolveOutputDirectory();
        log.info("[test_resolveOutputDir_default] output dir : " + p);
        assertThat(p.getFileName().toString()).isEqualTo("test-output");
    }

    @Test
    public void test_resolveOutputDirectory_custom() {
        String dirName = "customDir";
        TestOutputOrganizer too =
                new TestOutputOrganizer.Builder(this.getClass())
                        .outputDirectoryPathRelativeToProject(dirName)
                        .build();
        Path p = too.resolveOutputDirectory();
        log.info("[test_resolveOutputDirectory_custom] output dir : " + p);
        assertThat(p.getFileName().toString()).isEqualTo(dirName);
    }

    @Test
    public void test_resolveClassOutputDirectory() {
        TestOutputOrganizer too = new TestOutputOrganizer.Builder(this.getClass())
                .subPathUnderOutputDirectory(this.getClass()).build();
        Path p = too.resolveClassOutputDirectory();
        log.info("[test_resolveClassOutputDirectory] " + p);
        assertThat(p.getFileName().toString()).isEqualTo(this.getClass().getName());
    }

    @Test
    public void test_resolveMethodOutputDirectory() {
        TestOutputOrganizer too =
                new TestOutputOrganizer.Builder(this.getClass())
                        .subPathUnderOutputDirectory(this.getClass()).build();
        String testMethodName = new Object(){}.getClass().getEnclosingMethod().getName();
        assert testMethodName.equals("test_resolveMethodOutputDirectory");
        Path p = too.resolveMethodOutputDirectory(testMethodName);
        log.info("[" + testMethodName + "] p = " + p);
        assertThat(p.getFileName().toString()).isEqualTo(testMethodName);
        assertThat(p.getParent().getFileName().toString()).isEqualTo(this.getClass().getName());
    }

    @Test
    public void test_createOutputSubDirectory() throws Exception {
        // given
        TestOutputOrganizer too = new TestOutputOrganizer.Builder(this.getClass()).build();
        // when
        Path p = too.createOutputSubDirectory().resolve("hello.json");
        Files.write(p, "Hello, world!".getBytes(StandardCharsets.UTF_8));
        // then
        assertThat(p.getParent()).exists();
        assertThat(p.getParent().getFileName().toString())
                .isEqualTo("test-output");
        assertThat(p.getParent()   // expecting the test-output directory
                .getParent()       // expecting the lib directory
                .getFileName().toString())
                .isEqualTo("lib");
    }

    @Test
    public void test_resolveOutputSubDirectory() throws Exception {
        TestOutputOrganizer too =
                new TestOutputOrganizer.Builder(this.getClass())
                        .subPathUnderOutputDirectory(this.getClass())
                        .build();
        Path p = too.resolveOutputSubDirectory().resolve("test_resolveOutput_with_subDir/hello.json");
        Files.createDirectories(p.getParent());
        Files.write(p, "Hello, world!".getBytes(StandardCharsets.UTF_8));
        assertThat(p.getParent()).exists();
    }

    @Test
    public void test_Builder_outputDirectoryPathRelativeToProject_custom() throws Exception {
        TestOutputOrganizer too =
                new TestOutputOrganizer.Builder(this.getClass())
                        .outputDirectoryPathRelativeToProject("build/tmp/testOutput")
                        .build();
        Path p = too.createOutputSubDirectory().resolve("hello.txt");
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

    private boolean isWindows() {
        String OS = System.getProperty("os.name");
        return OS.startsWith("Windows");
    }

    @Test
    public void test_toHomeRelativeString_simple() {
        TestOutputOrganizer too = new TestOutputOrganizer.Builder(this.getClass()).build();
        Path projectDir = too.getProjectDirectory();
        String homeRelative = too.toHomeRelativeString(projectDir);
        System.out.println("[test_toHomeRelativeString_simple] " + homeRelative);
        if (isWindows()) {
            assertThat(homeRelative).isEqualTo("~\\github\\unittest-helper\\lib");

        } else {
            assertThat(homeRelative).isEqualTo("~/github/unittest-helper/lib");
        }
    }

    @Test
    public void test_toHomeRelativeString_simple_more() throws IOException {
        TestOutputOrganizer too = new TestOutputOrganizer.Builder(this.getClass()).build();
        Path p = too.createOutputDirectory().resolve("foo.txt");
        String homeRelative = too.toHomeRelativeString(p);
        if (isWindows()) {
            assertThat(homeRelative).isEqualTo(
                "~\\github\\unittest-helper\\lib\\test-output\\foo.txt");
        } else {
            assertThat(homeRelative).isEqualTo(
                    "~/github/unittest-helper/lib/test-output/foo.txt");
        }
    }

    @Test
    public void test_toHomeRelativeString_HOME_itself() {
        TestOutputOrganizer too = new TestOutputOrganizer.Builder(this.getClass()).build();
        Path p = FileSystems.getDefault().getPath(System.getProperty("user.home"));
        String s = too.toHomeRelativeString(p);
        if (isWindows()) {
            assertThat(s).isEqualTo("~\\");
        } else {
            assertThat(s).isEqualTo("~/");
        }
    }

    @Test
    public void test_toHomeRelativeString_when_not_relative() {
        TestOutputOrganizer too = new TestOutputOrganizer.Builder(this.getClass()).build();
        Path p = FileSystems.getDefault().getPath("/Applications");
        String s = too.toHomeRelativeString(p);
        if (isWindows()) {
            assertThat(s).isEqualTo("C:\\Applications");
        } else {
            assertThat(s).isEqualTo("/Applications");
        }
    }

    @Test
    public void test_cleanOutputDirectory() throws IOException {
        // given
        TestOutputOrganizer too = new TestOutputOrganizer.Builder(this.getClass()).build();
        Path p = too.createOutputSubDirectory().resolve("sub/foo.txt");
        Files.createDirectories(p.getParent());
        Files.write(p, "Hello, world!".getBytes(StandardCharsets.UTF_8));
        assertThat(p).exists();
        // when
        p = too.cleanOutputDirectory();
        // then
        Path od = too.createOutputDirectory();
        assertThat(od).exists();
        assertThat(isEmpty(od)).isTrue();
    }


    @Test
    public void test_cleanOutputSubDirectory() throws IOException {
        // given
        TestOutputOrganizer too =
                new TestOutputOrganizer.Builder(this.getClass())
                        .subPathUnderOutputDirectory(this.getClass())
                        .build();
        Path p = too.createOutputSubDirectory().resolve("foo.txt");
        Files.write(p, "Hello, world!".getBytes(StandardCharsets.UTF_8));
        assertThat(p).exists();
        // when
        p = too.cleanOutputSubDirectory();
        // then
        assertThat(too.createOutputSubDirectory()).exists();
        assertThat(isEmpty(too.createOutputSubDirectory())).isTrue();
    }

    @Test
    public void test_cleanClassOutputDirectory() throws IOException {
        // given
        TestOutputOrganizer too =
                new TestOutputOrganizer.Builder(this.getClass())
                        .subPathUnderOutputDirectory(this.getClass())
                        .build();
        Path p = too.createOutputSubDirectory().resolve("foo.txt");
        Files.write(p, "Hello, world!".getBytes(StandardCharsets.UTF_8));
        assertThat(p).exists();
        // when
        p = too.cleanOutputSubDirectory();
        // then
        assertThat(too.createOutputSubDirectory()).exists();
        assertThat(isEmpty(too.createOutputSubDirectory())).isTrue();
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
    void test_copyDir() throws IOException {
        TestOutputOrganizer too =
                new TestOutputOrganizer.Builder(this.getClass())
                        .subPathUnderOutputDirectory(this.getClass().getName())
                        .build();
        String methodName = "test_copyDir";
        // given
        Path sourceDir = too.resolveOutputSubDirectory().resolve(methodName + "/source");
        Path sourceFile = too.resolveOutputSubDirectory().resolve(methodName + "/source/foo/hello.txt");
        Files.createDirectories(sourceFile.getParent());
        Files.write(sourceFile, "Hello, world!".getBytes(StandardCharsets.UTF_8));
        Path targetDir = too.resolveOutputSubDirectory().resolve(methodName + "/target");
        Path targetFile = too.resolveOutputSubDirectory().resolve(methodName + "/target/foo/hello.txt");
        // when
        too.copyDir(sourceDir, targetDir);
        // then
        assertThat(targetFile).exists();
    }

    @Test
    void test_deleteDir() throws IOException {
        TestOutputOrganizer too =
                new TestOutputOrganizer.Builder(this.getClass())
                        .subPathUnderOutputDirectory(this.getClass().getName())
                        .build();
        String methodName = "test_deleteDir";
        // given
        Path sourceDir = too.resolveOutputSubDirectory().resolve(methodName + "/source");
        Path sourceFile = too.resolveOutputSubDirectory().resolve(methodName + "/source/foo/hello.txt");
        Files.createDirectories(sourceFile.getParent());
        Files.write(sourceFile, "Hello, world!".getBytes(StandardCharsets.UTF_8));
        Path targetDir = too.resolveOutputSubDirectory().resolve(methodName + "/target");
        Path targetFile = too.resolveOutputSubDirectory().resolve(methodName + "/target/foo/hello.txt");
        too.copyDir(sourceDir, targetDir);
        assertThat(targetFile).exists();
        // when
        too.deleteDir(targetDir);
        // then
        assertThat(targetFile).doesNotExist();
    }

    @Test
    public void test_Builder_pathElementsAsClasspathComponent() throws IOException {
        // given
        TestOutputOrganizer too =
                new TestOutputOrganizer.Builder(this.getClass())
                        .addCodeSourcePathElementsUnderProjectDirectory(
                                new CodeSourcePathElementsUnderProjectDirectory("bin", "classes"))
                        .build();
        Path p = too.createOutputSubDirectory().resolve("foo.txt");
        Files.write(p, "Hello, world!".getBytes(StandardCharsets.UTF_8));
        assertThat(p).exists();
        //
        too.cleanOutputSubDirectory();
        assertThat(too.resolveOutputSubDirectory()).exists();
        assertThat(isEmpty(too.resolveOutputSubDirectory())).isTrue();
    }

    /**
     * call the static method cleanDirectoryRecursively(Path) of
     * TestOutputOrganizer class to remove a directory recursively
     */
    @Test
    public void test_DeleteDir_deleteDirectoryRecursively() throws IOException {
        // given
        Path dir = FileSystems.getDefault().getPath("build/work");
        Files.createDirectories(dir);
        Path file = dir.resolve("foo.txt");
        Files.write(file, "Hello, world!".getBytes(StandardCharsets.UTF_8));
        // when
        DeleteDir.deleteDirectoryRecursively(dir);
        // then
        assertThat(file).doesNotExist();
        assertThat(dir).doesNotExist();
    }
}
