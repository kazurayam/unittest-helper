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
import java.util.List;
import java.util.Objects;

/**
 * Provides utility methods that helps JUnit4/JUnit5/TestNG tests with
 * the getProjectDirViaClasspath() method and the resolveOutput().
 * These methods are useful for Gradle Multi-projects where
 * the Current Working Directory is NOT equal to the sub-projects' root directory.
 */
public final class TestOutputOrganizer {

    private static final Logger log = LoggerFactory.getLogger(TestOutputOrganizer.class);

    private final Path projectDir;
    private final String outputDirPath;
    private final String subDirPath;

    /**
     * @param builder TestOutputOrganizer.Builder instance
     */
    private TestOutputOrganizer(Builder builder) {
        this.projectDir = builder.projectDir;
        this.outputDirPath = builder.outputDirPath;
        this.subDirPath = builder.subDirPath;
    }

    /**
     * @return the project directory where the clazz is hosted
     */
    public Path getProjectDir() {
        return projectDir;
    }

    /**
     *
     * @return a String like "build/tmp/testOutput" which was given to the Builder.
     */
    public String getOutputDirPath() {
        return outputDirPath;
    }

    /**
     * @return returns the java.nio.file.Path object of the output directory.
     * The default is "(projectDir)/test-output".
     * You can customize the name of the output directory by calling setOutputDir(Path.get("dirName")),
     * which is relative to the project directory.
     */
    public Path getOutputDirectory() {
        return projectDir.resolve(outputDirPath);
    }

    /**
     *
     * @return subDirPath may be null if not set
     */
    public String getSubDirPath() {
        return subDirPath;
    }

    /**
     * if the subDirPath is set by setSubDir(Path d), then will return
     *     getOutputDir().resolve(subDirPath)
     * if the subDirpath is not set, then will return the same as getOutputDir()
     * @return Path of output sub directory
     */
    public Path getOutputSubDirectory() {
        if (subDirPath != null) {
            return this.getOutputDirectory().resolve(subDirPath);
        } else {
            return this.getOutputDirectory();
        }
    }

    /**
     * remove the output directory recursively if it is already present.
     * will re-create the output directory, which will be empty
     *
     * @return the reference to this TestOutputOrganizer instance
     * @throws IOException during removing files/directories
     */
    public TestOutputOrganizer cleanOutputDirectory() throws IOException {
        Path outputDir = this.getOutputDirectory();
        cleanDirectoryRecursively(outputDir);
        Files.createDirectories(outputDir);
        return this;
    }

    /**
     * remove the directory, which is identifiable by outputDir.resolve(subDirPath), recursively
     * if it is already present. Will re-create the dir which will be empty.
     *
     * @return the reference to this TestOutputOrganizer instance
     * @throws IOException during removing files/directories
     */
    public TestOutputOrganizer cleanOutputSubDirectory() throws IOException {
        Path outputSubDir = this.getOutputSubDirectory();
        cleanDirectoryRecursively(outputSubDir);
        Files.createDirectories(outputSubDir);
        return this;
    }

    /**
     * remove the directory recursively.
     * If the dir is not present, does nothing.
     *
     * @param dir directory to remove
     * @throws IOException when any failure occured while removing files/dirs
     */
    public static void cleanDirectoryRecursively(Path dir) throws IOException {
        if (Files.exists(dir)) {
            Files.walk(dir)
                    .sorted(Comparator.reverseOrder())
                    .map(Path::toFile)
                    .forEach(File::delete);
        }
    }

    /**
     * Create the output directory if it is not yet there.
     * Returns the Path of a file that a test class write its output into.
     * As default, the output file will be located under the "test-output" directory.
     * You can change the directory location by calling setOutputDirPath(Path).
     *
     * @param fileName the file name
     * @return Path of a file as the output written by a test class
     */
    public Path resolveOutput(String fileName) {
        Path outFile =
                (subDirPath != null) ?
                        getOutputDirectory().resolve(subDirPath).resolve(fileName) :
                        getOutputDirectory().resolve(fileName);
        // make sure the parent directory to be present
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



    //---------- static section -----------------------------------------------

    static final String TILDE = "~";

    /**
     * This method is meant to be used in messages and documentations.
     * You do not want to show your own personal name in the console messages, right?
     * So you want to hide the username part of paths.
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
     * Joshua Bloch's "Builder" for the TestOutputOrganizer class
     */
    public static class Builder {
        private final Class<?> clazz;
        private Path projectDir;
        private List<String> sublistPattern;
        private String outputDirPath;
        private String subDirPath;

        /**
         * The name of the directory created by TestOutputOrganizer as default
         * when you do not call setOutputDirPath(Path)
         */
        private static final String DEFAULT_OUTPUT_DIR_PATH = "test-output";

        /**
         * Sole constructor
         *
         * @param clazz the Class object of a test class
         */
        public Builder(Class<?> clazz) {
            this.clazz = clazz;
            this.projectDir = null;
            this.sublistPattern = null;
            this.outputDirPath = DEFAULT_OUTPUT_DIR_PATH;
            this.subDirPath = null;
        }

        /**
         * add a sublistPattern
         * @param sublistPattern like ["bin", "classes"]
         * @return the reference to this Builder instance
         */
        public Builder sublistPattern(List<String> sublistPattern) {
            Objects.requireNonNull(sublistPattern);
            if (sublistPattern.isEmpty()) {
                throw new IllegalArgumentException("sublistPattern is empty");
            }
            this.sublistPattern = sublistPattern;
            return this;
        }

        /**
         * e.g., you can pass Paths.get("build/tmp/testOutput")
         * to specify the output dir location
         *
         * @param outputDirPath e.g, Paths.get("build/tmp/testOutput").
         *                      This could be relative to the project directory.
         *
         * @return the reference to this TestOutputOrganizer.Builder instance
         */
        public Builder outputDirPath(String outputDirPath) {
            Objects.requireNonNull(outputDirPath);
            Path odp = Paths.get(outputDirPath);
            if (odp.isAbsolute()) {
                throw new IllegalArgumentException(
                        "outputDirPath should not be absolute: " + outputDirPath);
            }
            this.outputDirPath = outputDirPath;
            return this;
        }

        /**
         * optional.
         * set a sub-directory path under the output directory.
         *
         * @param subDirPath e.g., Paths.get(this.getClass().getName()) or
         *               Paths.get("com.kazurayam.unittesthelperdemo.WithHelperTest")
         * @return the reference to this Builder.Builder instance
         */
        public Builder subDirPath(String subDirPath) {
            Objects.requireNonNull(subDirPath);
            Path sdp = Paths.get(subDirPath);
            if (sdp.isAbsolute()) {
                throw new IllegalArgumentException(
                        "subDirPath must not be absolute: " + subDirPath);
            }
            this.subDirPath = subDirPath;
            return this;
        }

        /**
         * @return TestOutputOrganizer object
         */
        public TestOutputOrganizer build() {
            ProjectDirectoryResolver pdr = new ProjectDirectoryResolver();
            if (sublistPattern != null) {
                pdr.addSublistPattern(sublistPattern);
            }
            this.projectDir = pdr.getProjectDirViaClasspath(clazz);
            return new TestOutputOrganizer(this);
        }

    }
}
