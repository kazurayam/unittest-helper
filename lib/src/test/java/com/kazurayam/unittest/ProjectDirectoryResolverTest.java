package com.kazurayam.unittest;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;

public class ProjectDirectoryResolverTest {

    private static final Logger log = LoggerFactory.getLogger(ProjectDirectoryResolverTest.class);

    @Test
    public void test_getCodeSourceAsPath() {
        Path p = ProjectDirectoryResolver.getCodeSourceAsPath(this.getClass());
        log.info("[testGetCodeSourceAsPath] p = " + p);
        assertThat(p).isNotNull().exists();
    }

    @Test
    public void test_getProjectDirViaClasspath() {
        Path p = new ProjectDirectoryResolver().getProjectDirViaClasspath(this.getClass());
        assertThat(p).isNotNull().exists();
        assertThat(p.getFileName().toString())
                .isEqualTo("lib");
    }

}
