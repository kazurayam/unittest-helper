package com.kazurayam.unittesthelperdemo;

import org.junit.jupiter.api.Test;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import com.kazurayam.unittest.TestOutputOrganizer;

import static org.assertj.core.api.Assertions.assertThat;

public class ExampleA4Test {

    @Test
    public void test_specify_project_directory() throws Exception {
        // setup
        Path userHome = Paths.get(System.getProperty("user.home"));
        Path projDir = userHome.resolve("ExampleA4Test");
        Files.createDirectories(projDir);
        // when
        TestOutputOrganizer too =
                new TestOutputOrganizer.Builder(this.getClass())
                        .projectDirectory(projDir) // specify the project dir
                        .build();
        Path outputDir = too.cleanOutputDirectory();
        // then
        assertThat(outputDir).exists();
        assertThat(outputDir.getParent()).isEqualByComparingTo(projDir);
        assertThat(outputDir.getFileName().toString()).isEqualTo("test-output");
    }
}
