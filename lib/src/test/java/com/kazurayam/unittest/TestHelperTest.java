package com.kazurayam.unittest;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.assertj.core.api.Assertions.assertThat;

public class TestHelperTest {

    private static final Logger log = LoggerFactory.getLogger(TestHelperTest.class);

    @Test
    public void test_getProjectDirViaClasspath() {
        Path p = new TestHelper(this.getClass()).getProjectDirViaClasspath();
        log.info("project dir : " + p);
        assertThat(p.getFileName().toString()).isEqualTo("lib");
        assertThat(p.getFileName().toString()).isNotEqualTo("unittest-helper");
    }

    @Test
    public void test_resolveOutput() throws Exception {
        Path p = new TestHelper(this.getClass())
                .resolveOutput("hello.json");
        Files.write(p, "Hello, world!".getBytes("utf-8"));
        assertThat(p.getParent()).exists();
        assertThat(p.getParent().getFileName().toString())
                .isEqualTo("test-output");
        assertThat(p.getParent()   // expecting the test-output directory
                .getParent()       // expecting the lib directory
                .getFileName().toString())
                .isEqualTo("lib");
    }

    @Test
    public void test_resolveOutput_into_custom_location() throws Exception {
        TestHelper th =
                new TestHelper(this.getClass())
                        .setOutputDirPath(Paths.get("build/tmp/testOutput"));
        Path p = th.resolveOutput("hello.txt");
        Files.write(p, "Hello, world!".getBytes("utf-8"));
        assertThat(p.getParent()                   // expecting testOutput
                .getFileName().toString())
                .isEqualTo("testOutput");
        assertThat(p.getParent()                   // expecting testOutput
                .getParent()                       // expecting tmp
                .getFileName().toString())
                .isEqualTo("tmp");
        assertThat(p.getParent()                   // expecting testOutput
                .getParent()                       // expecting tmp
                .getParent()                       // expecting build
                .getFileName().toString())
                .isEqualTo("build");
    }
}
