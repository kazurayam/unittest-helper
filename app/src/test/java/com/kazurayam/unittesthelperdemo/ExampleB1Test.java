package com.kazurayam.unittesthelperdemo;

import com.kazurayam.unittest.TestOutputOrganizer;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Path;

public class ExampleB1Test {

    Logger log = LoggerFactory.getLogger(ExampleB1Test.class);

    @Test
    public void test_getOutputDir_as_default() throws IOException {
        TestOutputOrganizer too = new TestOutputOrganizer.Builder(this.getClass()).build();
        Path outputDir = too.createOutputDirectory();
        log.info("[test_getOutputDir_as_default] " + outputDir);
        log.info("[test_getOutputDir_as_default] " +
                too.toHomeRelativeString(outputDir));
    }

}
