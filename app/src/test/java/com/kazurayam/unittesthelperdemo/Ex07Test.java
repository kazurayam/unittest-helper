package com.kazurayam.unittesthelperdemo;

import com.kazurayam.unittest.TestOutputOrganizer;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class Ex07Test {

    Logger log = LoggerFactory.getLogger(Ex07Test.class);

    @Test
    public void test_write_into_subdir_under_the_custom_output_directory() throws Exception {
        TestOutputOrganizer too =
                new TestOutputOrganizer.Builder(this.getClass())
                        .outputDirPath("build/tmp/testOutput").build();
        Path file = too.getOutputDirectory().resolve("sample.txt");
        // you do not have to make sure that the parent directory exists
        // Files.createDirectories(file.getParent());

        Files.write(file, "Hello, world!".getBytes(StandardCharsets.UTF_8));
        log.info("[test_write_into_subdir_under_the_custom_output_directory] " +
                too.toHomeRelativeString(file));
        List<String> content = Files.readAllLines(file);
        log.info(content.toString());

        assertThat(too.getOutputDirectory().getFileName().toString()).isEqualTo("testOutput");
    }

}
