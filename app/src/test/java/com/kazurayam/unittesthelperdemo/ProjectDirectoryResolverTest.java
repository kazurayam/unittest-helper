package com.kazurayam.unittesthelperdemo;

import com.kazurayam.unittest.CodeSourcePathElementsUnderProjectDirectory;
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
        Path projectDir = resolver.resolveProjectDirectoryViaClasspath(ProjectDirectoryResolverTest.class);
        log.info("projectDir: " + projectDir);
    }

    @Test
    public void test_getRegisteredListOfCodeSourcePathElementsUnderProjectDirectory() {
        List<CodeSourcePathElementsUnderProjectDirectory> listOfCSPE =
                new ProjectDirectoryResolver().getRegisteredListOfCodeSourcePathElementsUnderProjectDirectory();
        assertThat(listOfCSPE).isNotNull();
        assertThat(listOfCSPE.size()).isGreaterThanOrEqualTo(2);
        for (CodeSourcePathElementsUnderProjectDirectory cspe : listOfCSPE) {
            log.info("CodeSourcePathElementsUnderProjectDirectory: " + cspe);
        }
    }
}
