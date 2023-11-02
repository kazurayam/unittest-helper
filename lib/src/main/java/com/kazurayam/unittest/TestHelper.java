package com.kazurayam.unittest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.Objects;

/**
 * Provides utility methods that helps JUnit4/JUnit5/TestNG tests with
 * the getProjectDirViaClasspath() method and the resolveOutput().
 * These methods are useful for Gradle Multiprojects where
 * the Current Working Directory is NOT equal to the sub-projects' root directory.
 */
public final class TestHelper {

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
     * e.g., you can pass Paths.get("build/tmp/testOutput")
     * to specify the output dir location
     *
     * @param outputDirPath e.g, Paths.get("build/tmp/testOutput").
     *                      This could be relative to the project directory.
     *
     * @return the reference to this TestHelper instance
     */
    public TestHelper setOutputDirPath(Path outputDirPath) {
        Objects.requireNonNull(outputDirPath);
        if (outputDirPath.isAbsolute()) {
            throw new IllegalArgumentException(
                    "outputDirPath should not be absolute: " + outputDirPath);
        }
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
    public Path getProjectDir() {
        return projectDir;
    }

    /**
     * Create the output directory if it is not yet there.
     *
     * Returns the Path of a file that a test class write its output into.
     * As default, the output file will be located under the "test-output" directory.
     * You can change the directory location by calling setOutputDirPath(Path).
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

    static final String TILDE = "~";

    /**
     * This method is meant to be used in messages and documentations.
     * You do not want to show your own personal name in the console messages, right?
     * So you want to hide the username part of paths.
     *
     * Translate a Path of "/User/kazurayam/github/unittest-helper/app/foo.txt"
     * to "~/github/unittest-helper/app/foo.txt" which is relative to the $HOME
     * of the user if the path is located under the $HOME. If the path is NOT
     * located under the $HOME, just stringify the absolute path.
     *
     * @param path a Path object to be translated into a Home Relative path string
     * @return a path string prepended by tilde `~` if the path starts with "user.home"
     */
    public static String toHomeRelativeString(Path path) {
        Objects.requireNonNull(path);
        Path p = path.toAbsolutePath();
        String userHomeString = System.getProperty("user.home");
        if (userHomeString == null) {
            throw new IllegalStateException("System property user.home is null");
        }
        Path userHome = Paths.get(userHomeString);
        try {
            if (p.toUri().toURL().toString().startsWith(
                    userHome.toAbsolutePath().toUri().toURL().toString())) {
                // the other path is under the user.dir
                Path relativePath = userHome.relativize(p).normalize();
                return TILDE + "/" + relativePath.toString();
            } else {
                return p.toString();
            }
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * removed the output directory recursively if it is already present
     *
     * @return the reference to this TestHelper instance
     * @throws IOException
     */
    public TestHelper cleanOutputDirectory() throws IOException {
        Path outputDir = this.getOutputDir();
        if (Files.exists(outputDir)) {
            Files.walk(outputDir)
                    .sorted(Comparator.reverseOrder())
                    .map(Path::toFile)
                    .forEach(File::delete);
        }
        return this;
    }
}
