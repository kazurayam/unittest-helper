package com.kazurayam.unittesthelperdemo;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

public class OutputIntoCurrentWorkingDirectoryTest {

    Logger log = LoggerFactory.getLogger(OutputIntoCurrentWorkingDirectoryTest.class);

    @Test
    public void test_show_CWD() {
        Path p = Paths.get(".");
        log.info("[test_show_CWD] " + p.toAbsolutePath().normalize());
    }

    /*
     * will create a file `<projectDir>/sample1.txt`
     */
    @Disabled
    @Test
    public void test_write_under_current_working_directory() throws Exception {
        Path p = Paths.get("sample1.txt");
        Files.write(p, "Hello, world!".getBytes(StandardCharsets.UTF_8));
        System.out.println("[test_write_under_current_working_directory] p = " + p.toAbsolutePath());
    }

}
