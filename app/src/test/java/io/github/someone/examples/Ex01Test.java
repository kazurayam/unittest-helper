package io.github.someone.examples;

import com.kazurayam.unittest.TestOutputOrganizer;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;

public class Ex01Test {

    Logger log = LoggerFactory.getLogger(Ex01Test.class);

    private static TestOutputOrganizer too;

    @BeforeAll
    public static void beforeAll() {
        too = new TestOutputOrganizer.Builder(Ex01Test.class).build();
    }

    @Test
    public void test_showProjectDir() {
        Path projectDir = too.getProjectDir();
        log.info("projectDir=" + projectDir);
    }
}
