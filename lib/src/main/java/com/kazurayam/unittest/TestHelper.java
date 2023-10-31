package com.kazurayam.unittest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

/**
 * Provides utility methods that helps JUnit4/JUnit5/TestNG tests with
 * the getProjectDirViaClasspath() method and the resolveOutput().
 * These methods are useful for Gradle Multiprojects where
 * the Current Working Directory is NOT equal to the sub-projects' root directory.
 */
public class TestHelper {

    private static final Logger log = LoggerFactory.getLogger(TestHelper.class);

    /**
     * The name of the directory created by TestHelper as default
     * when you do not call setOutputDirPath(Path)
     */
    public static final Path DEFAULT_OUTPUT_DIR_PATH = Paths.get("test-output");

    private final Path projectDir;

    private Path outputDirPath;

    /**
     * The clazz parameter is required.
     * The ProjectDirectoryResolver resolves the project directory
     * for the clazz specified via the runtime classpath.
     * @param clazz the Class object based on which the project dir is resolved
     */
    public TestHelper(Class clazz) {
        projectDir = new ProjectDirectoryResolver().getProjectDirViaClasspath(clazz);
        outputDirPath = DEFAULT_OUTPUT_DIR_PATH;
    }

    /**
     * e.g., you can pass Paths.get("build/tmp/testOutput") to specify the output dir location
     * @param outputDirPath e.g, Paths.get("build/tmp/testOutput"). This could be relative to the project directory.
     *
     * @return the reference to this instance
     */
    public TestHelper setOutputDirPath(Path outputDirPath) {
        Objects.requireNonNull(outputDirPath);
        this.outputDirPath = outputDirPath;
        return this;
    }

    /**
     * @return returns the java.nio.file.Path object of the output directory.
     * The default is "(projectDir)/test-output".
     * You can customize the name of the output directory by calling setOutputDir(Path.get("dirName")),
     * which is relative to the project directory.
     */
    public Path getOutputDir() {
        return projectDir.resolve(outputDirPath);
    }

    /**
     * @return the project directory where the clazz is hosted
     */
    public Path getProjectDirViaClasspath() {
        return projectDir;
    }

    /**
     * returns the Path of a file that a test class write its output into.
     * As default, the output file will be located under the "test-output" directory.
     * You can change the directory location by calling setOutputDirPath(Path).
     * The parent directory the output will be silently created.
     *
     * @param fileName the file name
     * @return Path of a file as the output written by a test class
     */
    public Path resolveOutput(String fileName) {
        Path outFile = getOutputDir().resolve(fileName);
        Path parentDir = outFile.getParent();
        if (!Files.exists(parentDir)) {
            try {
                Files.createDirectories(parentDir);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return outFile;
    }
}
