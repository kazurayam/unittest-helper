package com.kazurayam.unittesthelperdemo;

import com.kazurayam.unittest.TestOutputOrganizer;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Path;

public class ExampleB2Test {

    Logger log = LoggerFactory.getLogger(ExampleB2Test.class);

    @Test
    public void test_getOutputDir_custom() throws IOException {
        TestOutputOrganizer too =
                new TestOutputOrganizer.Builder(this.getClass())
                        .outputDirectoryRelativeToProject("test-output-another")
                        .build();
        Path outputDir = too.createOutputDirectory();
        log.info("[test_getOutputDir_custom] " +
                too.toHomeRelativeString(outputDir));
    }

    @Test
    public void test_getOutputDir_custom_more() throws IOException {
        TestOutputOrganizer too = new TestOutputOrganizer.Builder(this.getClass())
                .outputDirectoryRelativeToProject("build/tmp/testOutput")
                .build();
        Path outputDir = too.createOutputDirectory();
        log.info("[test_getOutputDir_custom_more] " +
                too.toHomeRelativeString(outputDir));
    }
}
