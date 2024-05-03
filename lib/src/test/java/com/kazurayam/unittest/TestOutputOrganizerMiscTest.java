package com.kazurayam.unittest;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;

public class TestOutputOrganizerMiscTest {


    @Test
    void test_deleteDir() throws IOException {
        TestOutputOrganizer too =
                new TestOutputOrganizer.Builder(this.getClass())
                        .subOutputDirectory(this.getClass().getName())
                        .build();
        String methodName = "test_deleteDir";
        // given
        Path sourceDir = too.resolveSubOutputDirectory().resolve(methodName + "/source");
        Path sourceFile = too.resolveSubOutputDirectory().resolve(methodName + "/source/foo/hello.txt");
        Files.createDirectories(sourceFile.getParent());
        Files.write(sourceFile, "Hello, world!".getBytes(StandardCharsets.UTF_8));
        Path targetDir = too.resolveSubOutputDirectory().resolve(methodName + "/target");
        Path targetFile = too.resolveSubOutputDirectory().resolve(methodName + "/target/foo/hello.txt");
        too.copyDir(sourceDir, targetDir);
        assertThat(targetFile).exists();
        // when
        too.deleteDir(targetDir);
        // then
        assertThat(targetFile).doesNotExist();
    }


    @Test
    void test_copyDir() throws IOException {
        TestOutputOrganizer too =
                new TestOutputOrganizer.Builder(this.getClass())
                        .subOutputDirectory(this.getClass().getName())
                        .build();
        String methodName = "test_copyDir";
        // given
        Path sourceDir = too.resolveSubOutputDirectory().resolve(methodName + "/source");
        Path sourceFile = too.resolveSubOutputDirectory().resolve(methodName + "/source/foo/hello.txt");
        Files.createDirectories(sourceFile.getParent());
        Files.write(sourceFile, "Hello, world!".getBytes(StandardCharsets.UTF_8));
        Path targetDir = too.resolveSubOutputDirectory().resolve(methodName + "/target");
        Path targetFile = too.resolveSubOutputDirectory().resolve(methodName + "/target/foo/hello.txt");
        // when
        too.copyDir(sourceDir, targetDir);
        // then
        assertThat(targetFile).exists();
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



    private boolean isWindows() {
        String OS = System.getProperty("os.name");
        return OS.startsWith("Windows");
    }


}
