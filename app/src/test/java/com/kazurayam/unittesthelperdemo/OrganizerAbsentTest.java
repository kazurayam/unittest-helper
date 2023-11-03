package com.kazurayam.unittesthelperdemo;

import com.kazurayam.unittest.TestOutputOrganizer;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class OrganizerAbsentTest {

    /*
     * will create a file `<projectDir>/sample1.txt`
     */
    @Test
    public void test_write_under_current_working_directory() throws Exception {
        Path p = Paths.get("sample1.txt");
        Files.write(p, "Hello, world!".getBytes(StandardCharsets.UTF_8));
        System.out.println("[test_write_under_current_working_directory] p = " +
                TestOutputOrganizer.toHomeRelativeString(p));
    }

}
