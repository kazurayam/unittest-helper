package com.kazurayam.unittesthelperdemo;

import com.kazurayam.unittest.ProjectDirectoryResolver;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public final class ProjectDirectoryResolverTest {

    Logger log = LoggerFactory.getLogger(ProjectDirectoryResolverTest.class);

    @Test
    public void test_getProjectDirViaClasspath() {
        ProjectDirectoryResolver resolver = new ProjectDirectoryResolver();
        Path projectDir = resolver.getProjectDirViaClasspath(ProjectDirectoryResolverTest.class);
        log.info("projectDir: " + projectDir);
    }

    @Test
    public void test_getSublistPatterns() {
        List<List<String>> sublistPatterns =
                new ProjectDirectoryResolver().getSublistPatterns();
        assertThat(sublistPatterns).isNotNull();
        assertThat(sublistPatterns.size()).isGreaterThanOrEqualTo(2);
        for (List<String> p : sublistPatterns) {
            log.info("sublistPattern : " + p);
        }
    }
}
