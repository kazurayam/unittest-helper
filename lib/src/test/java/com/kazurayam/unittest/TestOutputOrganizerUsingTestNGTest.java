package com.kazurayam.unittest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;

import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Using TestNG
 */
public class TestOutputOrganizerUsingTestNGTest {

    private static final Logger log = LoggerFactory.getLogger(TestOutputOrganizerUsingTestNGTest.class);

    private final TestOutputOrganizer too =
            new TestOutputOrganizer.Builder(this.getClass())
                    .subPathUnderOutputDirectory(this.getClass()).build();

    @Test
    public void test_resolveMethodOutputDirectory(Method method) {
        Path p = too.resolveMethodOutputDirectory(method);
        log.debug("[" + method.getName() + "] " + p);
        assertThat(p.getFileName().toString()).isEqualTo(method.getName());
        assertThat(p.getParent().getFileName().toString()).isEqualTo(this.getClass().getName());
    }

    @Test
    public void test_createMethodOutputDirectory(Method method) throws IOException {
        Path p = too.createMethodOutputDirectory(method);
        log.debug("[" + method.getName() + "] " + p);
        assertThat(p).exists();
    }

    @Test
    public void test_cleanMethodOutputDirectory(Method method) throws IOException {
        Path p = too.createMethodOutputDirectory(method);
        log.debug("[" + method.getName() + "] " + p);
        Path f = p.resolve("Hello.txt");
        Files.write(f, "Hello, world!".getBytes());
        // when
        p = too.cleanMethodOutputDirectory(method);
        assertThat(Files.list(p)).isEmpty();
    }
}
