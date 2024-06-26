package com.kazurayam.unittesthelperdemo;

import com.kazurayam.unittest.TestOutputOrganizer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ExampleC5Test {

    private static final Logger log = LoggerFactory.getLogger(ExampleC5Test.class);
    private static final TestOutputOrganizer too =
            new TestOutputOrganizer.Builder(ExampleC5Test.class)
                    .subOutputDirectory(ExampleC5Test.class)
                    .build();
    private static LocalDateTime timestamp;

    @BeforeAll
    public static void beforeAll() throws Exception {
        timestamp = LocalDateTime.now();
    }

    @Test
    public void testMethod1() throws Exception {
        too.cleanMethodOutputDirectory("testMethod1");
        Path methodDir = too.createMethodOutputDirectory("testMethod1");
        Path file = methodDir.resolve(DateTimeFormatter.ISO_DATE_TIME.format(timestamp) + ".txt");
        Files.write(file, "Hello, world!".getBytes(StandardCharsets.UTF_8));
    }

    @Test
    public void testMethod2() throws Exception {
        too.cleanMethodOutputDirectory("testMethod2");
        Path methodDir = too.createMethodOutputDirectory("testMethod2");
        Path file = methodDir.resolve(DateTimeFormatter.ISO_DATE_TIME.format(timestamp) + ".txt");
        Files.write(file, "Hello, world!".getBytes(StandardCharsets.UTF_8));
    }

    @Test
    public void testMethod3() throws Exception {
        too.cleanMethodOutputDirectory("testMethod3");
        Path methodDir = too.createMethodOutputDirectory("testMethod3");
        Path file = methodDir.resolve(DateTimeFormatter.ISO_DATE_TIME.format(timestamp) + ".txt");
        Files.write(file, "Hello, world!".getBytes(StandardCharsets.UTF_8));
    }

    @AfterAll
    public static void afterAll() throws IOException {
        Files.find(too.createClassOutputDirectory(), 999, (p, bfa) -> bfa.isRegularFile())
                .sorted()
                .forEach(p -> log.info(too.toHomeRelativeString(p)));
    }
}
