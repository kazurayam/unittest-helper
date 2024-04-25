package com.kazurayam.unittesthelperdemo;

import com.kazurayam.unittest.TestOutputOrganizer;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class Ex06Test {

    Logger log = LoggerFactory.getLogger(Ex06Test.class);

    @Test
    public void test_write_into_subdir_under_the_default_output_directory() throws Exception {
        TestOutputOrganizer too = new TestOutputOrganizer.Builder(this.getClass()).build();
        Path file = too.createOutputDirectory().resolve("sub/sample.txt");
        // you need to make sure that the parent directory exists
        Files.createDirectories(file.getParent());

        Files.write(file, "Hello, world!".getBytes(StandardCharsets.UTF_8));
        log.info("[test_write_into_subdir_under_the_default_output_directory] " +
                too.toHomeRelativeString(file));
        List<String> content = Files.readAllLines(file);
        log.info(content.toString());
    }

}
