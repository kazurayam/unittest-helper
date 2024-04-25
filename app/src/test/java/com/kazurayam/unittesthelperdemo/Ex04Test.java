package com.kazurayam.unittesthelperdemo;

import com.kazurayam.unittest.TestOutputOrganizer;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Path;

public class Ex04Test {

    Logger log = LoggerFactory.getLogger(Ex04Test.class);

    @Test
    public void test_getOutputDir_custom() throws IOException {
        TestOutputOrganizer too =
                new TestOutputOrganizer.Builder(this.getClass())
                        .outputDirectoryPathRelativeToProject("test-output-another")
                        .build();
        Path outputDir = too.getOutputDirectory();
        log.info("[test_getOutputDir_custom] " +
                too.toHomeRelativeString(outputDir));
    }

    @Test
    public void test_getOutputDir_custom_more() throws IOException {
        TestOutputOrganizer too = new TestOutputOrganizer.Builder(this.getClass())
                .outputDirectoryPathRelativeToProject("build/tmp/testOutput")
                .build();
        Path outputDir = too.getOutputDirectory();
        log.info("[test_getOutputDir_custom_more] " +
                too.toHomeRelativeString(outputDir));
    }
}
