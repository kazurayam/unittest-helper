package com.kazurayam.unittest;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.util.List;
import java.util.regex.Matcher;

import static org.assertj.core.api.Assertions.assertThat;

public final class ProjectDirectoryResolverTest {

    private static final Logger logger = LoggerFactory.getLogger(ProjectDirectoryResolverTest.class);

    @Test
    public void test_getCodeSourceAsPath() {
        Path p = new ProjectDirectoryResolver().getCodeSourceAsPath(this.getClass());
        logger.info("[test_getCodeSourceAsPath] p = " + p);
        assertThat(p).isNotNull().exists();
    }

    @Test
    public void test_getProjectDirViaClasspath() {
        Path p = new ProjectDirectoryResolver().getProjectDirViaClasspath(this.getClass());
        logger.debug("[test_getProjectDirViaClasspath] p=" + p.toString());
        if (isWindows()) {
            assertThat(p.toString()).startsWith("C:\\");
        }
        assertThat(p).isNotNull().exists();
        assertThat(p.getFileName().toString())
                .isEqualTo("lib");
    }
    private boolean isWindows() {
        String OS = System.getProperty("os.name");
        return OS.startsWith("Windows");
    }

    @Test
    public void test_getSublistPatterns() {
        List<List<String>> sublistPatterns =
                new ProjectDirectoryResolver().getPathElementsAsClasspathComponentList();
        assertThat(sublistPatterns).isNotNull();
        assertThat(sublistPatterns.size()).isGreaterThanOrEqualTo(2);
        for (List<String> p : sublistPatterns) {
            logger.info("sublistPattern : " + p);
        }
    }

    @Test
    public void test_URL_PATTERN_file_scheme() {
        Matcher m = ProjectDirectoryResolver.URL_PATTERN.matcher("file:/Users/foo/bar");
        assertThat(m.matches()).isTrue();
        assertThat(m.group(1)).isEqualTo("file:");
        assertThat(m.group(2)).isEqualTo("/Users/foo/bar");
    }

    @Test
    public void test_URL_PATTERN_no_scheme() {
        Matcher m = ProjectDirectoryResolver.URL_PATTERN.matcher("/Users/foo/bar");
        assertThat(m.matches()).isTrue();
        assertThat(m.group(1)).isNull();
        assertThat(m.group(2)).isEqualTo("/Users/foo/bar");
    }

    @Test
    public void test_URL_PATTERN_s3_scheme() {
        Matcher m = ProjectDirectoryResolver.URL_PATTERN.matcher("s3:/Users/foo/bar");
        assertThat(m.matches()).isTrue();
        assertThat(m.group(1)).isEqualTo("s3:");
        assertThat(m.group(2)).isEqualTo("/Users/foo/bar");
    }
}
