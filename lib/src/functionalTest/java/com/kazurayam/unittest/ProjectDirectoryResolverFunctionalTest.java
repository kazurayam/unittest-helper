package com.kazurayam.unittest;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;

public final class ProjectDirectoryResolverFunctionalTest {

    private static final Logger logger = LoggerFactory.getLogger(ProjectDirectoryResolverFunctionalTest.class);

    @Test
    public void test_getProjectDirViaClasspath() {
        Path p = new ProjectDirectoryResolver().resolveProjectDirectoryViaClasspath(this.getClass());
        assertThat(p).isNotNull().exists();
        assertThat(p.getFileName().toString())
                .isEqualTo("lib");
    }

}