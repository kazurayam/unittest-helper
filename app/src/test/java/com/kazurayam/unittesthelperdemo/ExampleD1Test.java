package com.kazurayam.unittesthelperdemo;

import com.kazurayam.unittest.TestOutputOrganizer;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;

public class ExampleD1Test {

    private static final Logger log = LoggerFactory.getLogger(ExampleD1Test.class);
    private static final TestOutputOrganizer too =
            new TestOutputOrganizer.Builder(ExampleD1Test.class)
                    .subOutputDirectory(ExampleD1Test.class)
                    .build();
    @BeforeAll
    public static void beforeAll() throws Exception {
        log.info("projectDir=" + too.toHomeRelativeString(too.getProjectDirectory()));
        too.cleanOutputDirectory();
        log.info("outputDirectory=" + too.toHomeRelativeString(too.createOutputDirectory()));
        too.cleanClassOutputDirectory();
        log.info("classOutputDirectory=" + too.toHomeRelativeString(too.createClassOutputDirectory()));
    }

    @Test
    public void testMethod1() throws Exception {
        too.cleanMethodOutputDirectory("testMethod1");
        Path methodDir = too.createMethodOutputDirectory("testMethod1");
        log.info("methodOutputDirectory=" + too.toHomeRelativeString(methodDir));
    }
}
