package com.kazurayam.unittesthelperdemo;

import com.kazurayam.unittest.TestHelper;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class HelperlessTest {

    /*
     * will create a file `<projectDir>/sample1.txt`
     */
    @Test
    public void test_write_under_current_working_directory() throws Exception {
        Path p = Paths.get("sample1.txt");
        Files.writeString(p, "Hello, world!");
        System.out.println("[test_write_under_current_working_directory] p = " +
                TestHelper.toHomeRelativeString(p));
    }

}
