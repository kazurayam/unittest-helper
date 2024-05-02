package com.kazurayam.unittesthelperdemo;


import com.kazurayam.unittest.TestOutputOrganizer;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Path;

public class ExampleE1Test {

    private static final Logger log = LoggerFactory.getLogger(ExampleE1Test.class);
    private static final TestOutputOrganizer too =
            new TestOutputOrganizer.Builder(ExampleE1Test.class)
                    .build();
    @Test
    public void test_smoke() throws IOException {
        Path outputDir = too.createOutputDirectory();
        Path file = outputDir.resolve("sample1.txt");
        log.info("file absolute: " + file);
        log.info("file relative: " + too.toHomeRelativeString(file));
    }
}
