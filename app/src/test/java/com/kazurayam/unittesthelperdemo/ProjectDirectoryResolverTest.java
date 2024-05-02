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
        List<CodeSourcePathElementsUnderProjectDirectory> listOfCSPEUPD =
                new ProjectDirectoryResolver().getRegisteredListOfCodeSourcePathElementsUnderProjectDirectory();
        assertThat(listOfCSPEUPD).isNotNull();
        assertThat(listOfCSPEUPD.size()).isGreaterThanOrEqualTo(2);
        String methodName = new Object(){}.getClass().getEnclosingMethod().getName();
        log.info("[" + methodName + "]");
        for (CodeSourcePathElementsUnderProjectDirectory cspeupd : listOfCSPEUPD) {
            log.info("CodeSourcePathElementsUnderProjectDirectory: " + cspeupd);
        }
    }

    @Test
    public void test_addCodeSourcePathElementsUnderProjectDirectory() {
        ProjectDirectoryResolver pdr = new ProjectDirectoryResolver();
        pdr.addCodeSourcePathElementsUnderProjectDirectory(
                new CodeSourcePathElementsUnderProjectDirectory("out", "bin"));
        String methodName = new Object(){}.getClass().getEnclosingMethod().getName();
        log.info("[" + methodName + "]");
        List<CodeSourcePathElementsUnderProjectDirectory> listOfCSPEUPD =
                pdr.getRegisteredListOfCodeSourcePathElementsUnderProjectDirectory();
        for (CodeSourcePathElementsUnderProjectDirectory cspeupd : listOfCSPEUPD) {
            log.info("CodeSourcePathElementsUnderProjectDirectory: " + cspeupd);
        }
    }
}
