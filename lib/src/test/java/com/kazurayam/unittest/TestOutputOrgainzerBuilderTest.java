package com.kazurayam.unittest;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;

public class TestOutputOrgainzerBuilderTest {

    @Test
    public void test_Builder_addCodeSourcePathElementsUnderProjectDirectory() throws IOException {
        // given
        TestOutputOrganizer too =
                new TestOutputOrganizer.Builder(this.getClass())
                        .addCodeSourcePathElementsUnderProjectDirectory(
                                new CodeSourcePathElementsUnderProjectDirectory("bin", "classes"))
                        .build();
        Path p = too.createSubOutputDirectory().resolve("foo.txt");
        Files.write(p, "Hello, world!".getBytes(StandardCharsets.UTF_8));
        assertThat(p).exists();
        //
        too.cleanSubOutputDirectory();
        assertThat(too.resolveSubOutputDirectory()).exists();
        assertThat(isEmpty(too.resolveSubOutputDirectory())).isTrue();
    }

    @Test
    public void test_Builder_outputDirectoryRelativeToProject_custom() throws Exception {
        TestOutputOrganizer too =
                new TestOutputOrganizer.Builder(this.getClass())
                        .outputDirectoryRelativeToProject("build/tmp/testOutput")
                        .build();
        Path p = too.createSubOutputDirectory().resolve("hello.txt");
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


    boolean isEmpty(Path path) throws IOException {
        if (Files.isDirectory(path)) {
            try (DirectoryStream<Path> directory = Files.newDirectoryStream(path)) {
                return !directory.iterator().hasNext();
            }
        }
        return false;
    }

}
