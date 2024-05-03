package com.kazurayam.unittest;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;

public class TestOutputOrganizerTest {

    private static final Logger log = LoggerFactory.getLogger(TestOutputOrganizerTest.class);

    @Test
    public void test_getProjectDirectory() {
        TestOutputOrganizer too =
                new TestOutputOrganizer.Builder(this.getClass())
                        .build();
        Path p = too.getProjectDirectory();
        String methodName = new Object(){}.getClass().getEnclosingMethod().getName();
        log.info("[" + methodName + "] projectDirectory : " + p);
        assertThat(p.getFileName().toString()).isEqualTo("lib");
        assertThat(p.getFileName().toString()).isNotEqualTo("unittest-helper");
    }

    @Test
    public void test_resolveOutputDirectory_default() {
        // given
        TestOutputOrganizer too =
                new TestOutputOrganizer.Builder(this.getClass())
                        .build();
        // when
        Path p = too.resolveOutputDirectory();
        String methodName = new Object(){}.getClass().getEnclosingMethod().getName();
        log.info("[" + methodName + "] output dir : " + p);
        // then
        assertThat(p.getFileName().toString()).isEqualTo("test-output");
    }

    @Test
    public void test_resolveOutputDirectory_custom() {
        // given
        String dirName = "customDir";
        TestOutputOrganizer too =
                new TestOutputOrganizer.Builder(this.getClass())
                        .outputDirectoryRelativeToProject(dirName)
                        .build();
        // when
        Path p = too.resolveOutputDirectory();
        String methodName = new Object(){}.getClass().getEnclosingMethod().getName();
        log.info("[" + methodName + "] output dir : " + p);
        // then
        assertThat(p.getFileName().toString()).isEqualTo(dirName);
    }

    @Test
    public void test_resolveClassOutputDirectory() {
        // given
        TestOutputOrganizer too =
                new TestOutputOrganizer.Builder(this.getClass())
                        .subOutputDirectory(this.getClass())
                        .build();
        // when
        Path p = too.resolveClassOutputDirectory();
        String methodName = new Object(){}.getClass().getEnclosingMethod().getName();
        log.info("[" + methodName + "] " + p);
        // then
        assertThat(p.getFileName().toString()).isEqualTo(this.getClass().getName());
    }

    @Test
    public void test_resolveMethodOutputDirectory() {
        // given
        TestOutputOrganizer too =
                new TestOutputOrganizer.Builder(this.getClass())
                        .subOutputDirectory(this.getClass()).build();
        // when
        String testMethodName = new Object(){}.getClass().getEnclosingMethod().getName();
        Path p = too.resolveMethodOutputDirectory(testMethodName);
        log.info("[" + testMethodName + "] p = " + p);
        // then
        assertThat(p.getFileName().toString()).isEqualTo(testMethodName);
        assertThat(p.getParent().getFileName().toString()).isEqualTo(this.getClass().getName());
    }

    @Test
    public void test_createSubOutputDirectory() throws Exception {
        // given
        TestOutputOrganizer too = new TestOutputOrganizer.Builder(this.getClass()).build();
        // when
        Path p = too.createSubOutputDirectory().resolve("hello.json");
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
    public void test_resolveSubOutputDirectory() throws Exception {
        // when
        TestOutputOrganizer too =
                new TestOutputOrganizer.Builder(this.getClass())
                        .subOutputDirectory(this.getClass())
                        .build();
        //
        Path p = too.resolveSubOutputDirectory().resolve("test_resolveSubOutputDirectory/hello.json");
        Files.createDirectories(p.getParent());
        Files.write(p, "Hello, world!".getBytes(StandardCharsets.UTF_8));
        assertThat(p.getParent()).exists();
    }

    @Test
    public void test_cleanOutputDirectory() throws IOException {
        // given
        TestOutputOrganizer too = new TestOutputOrganizer.Builder(this.getClass()).build();
        Path p = too.createSubOutputDirectory().resolve("sub/foo.txt");
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
    public void test_cleanSubOutputDirectory() throws IOException {
        // given
        TestOutputOrganizer too =
                new TestOutputOrganizer.Builder(this.getClass())
                        .subOutputDirectory(this.getClass())
                        .build();
        Path p = too.createSubOutputDirectory().resolve("foo.txt");
        Files.write(p, "Hello, world!".getBytes(StandardCharsets.UTF_8));
        assertThat(p).exists();
        // when
        p = too.cleanSubOutputDirectory();
        // then
        assertThat(too.createSubOutputDirectory()).exists();
        assertThat(isEmpty(too.createSubOutputDirectory())).isTrue();
    }

    @Test
    public void test_cleanClassOutputDirectory() throws IOException {
        // given
        TestOutputOrganizer too =
                new TestOutputOrganizer.Builder(this.getClass())
                        .subOutputDirectory(this.getClass())
                        .build();
        Path p = too.createSubOutputDirectory().resolve("foo.txt");
        Files.write(p, "Hello, world!".getBytes(StandardCharsets.UTF_8));
        assertThat(p).exists();
        // when
        p = too.cleanSubOutputDirectory();
        // then
        assertThat(too.createSubOutputDirectory()).exists();
        assertThat(isEmpty(too.createSubOutputDirectory())).isTrue();
    }

    boolean isEmpty(Path path) throws IOException {
        if (Files.isDirectory(path)) {
            try (DirectoryStream<Path> directory = Files.newDirectoryStream(path)) {
                return !directory.iterator().hasNext();
            }
        }
        return false;
    }
}
