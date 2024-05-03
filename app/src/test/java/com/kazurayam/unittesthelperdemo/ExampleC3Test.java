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

public class ExampleC3Test {

    Logger log = LoggerFactory.getLogger(ExampleC3Test.class);

    @Test
    public void test_write_into_the_custom_output_directory() throws Exception {
        TestOutputOrganizer too =
                new TestOutputOrganizer.Builder(this.getClass())
                        .outputDirectoryRelativeToProject("build/tmp/testOutput").build();
        Path file = too.createOutputDirectory().resolve("sample.txt");
        // you do not have to make sure that the parent directory exists
        // Files.createDirectories(file.getParent());

        Files.write(file, "Hello, world!".getBytes(StandardCharsets.UTF_8));
        String methodName = new Object(){}.getClass().getEnclosingMethod().getName();
        log.info("[" + methodName + "] " +
                too.toHomeRelativeString(file));
        List<String> content = Files.readAllLines(file);
        log.info(content.toString());

        assertThat(too.createOutputDirectory().getFileName().toString()).isEqualTo("testOutput");
    }

}
