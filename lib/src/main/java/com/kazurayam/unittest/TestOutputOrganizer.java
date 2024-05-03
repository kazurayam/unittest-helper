package com.kazurayam.unittest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;


/**
 * Provides utility methods that helps JUnit4/JUnit5/TestNG tests with
 * the getProjectDirViaClasspath() method and the resolveOutput().
 * These methods are useful for Gradle Multi-projects where
 * the Current Working Directory is NOT equal to the sub-projects' root directory.
 */
public final class TestOutputOrganizer {

    private static final Logger logger = LoggerFactory.getLogger(TestOutputOrganizer.class);
    private final FileSystem fileSystem;
    private final Path projectDirectory;
    private final String outputDirectoryPathRelativeToProject;
    private final Optional<String> outputSubDirectoryPath;
    private final Boolean isByFullyQualifiedClassName;

    /**
     * @param builder TestOutputOrganizer.Builder instance
     */
    private TestOutputOrganizer(Builder builder) {
        this.fileSystem = builder.fileSystem;
        this.projectDirectory = builder.projectDirectory;
        this.outputDirectoryPathRelativeToProject = builder.outputDirectoryPathRelativeToProject;
        this.outputSubDirectoryPath = builder.subOutputDirectoryPath;
        this.isByFullyQualifiedClassName = builder.isByFullyQualifiedClassName;
    }

    /**
     * @return the project directory where the clazz is hosted
     */
    public Path getProjectDirectory() {
        return projectDirectory;
    }

    /**
     *
     * @return a String like "build/tmp/testOutput" which was given to the Builder.
     */
    public String getOutputDirectoryPathRelativeToProject() {
        return outputDirectoryPathRelativeToProject;
    }

    /**
     * @return returns the java.nio.file.Path object of the output directory.
     * The default is "(projectDir)/test-output".
     * You can customize the name of the output directory by calling setOutputDir(Path.get("dirName")),
     * which is relative to the project directory.
     */
    public Path resolveOutputDirectory() {
        return getProjectDirectory()
                .resolve(outputDirectoryPathRelativeToProject)
                .toAbsolutePath()
                .normalize();
    }

    public Path createOutputDirectory() throws IOException {
        Path d = resolveOutputDirectory();
        Files.createDirectories(d);
        return d;
    }

    /**
     * remove the output directory recursively if it is already present.
     * will re-create the output directory, which will be empty
     *
     * @return the reference to this TestOutputOrganizer instance
     * @throws IOException during removing files/directories
     */
    public Path cleanOutputDirectory() throws IOException {
        Path outputDir = this.createOutputDirectory();
        DeleteDir.deleteDirectoryRecursively(outputDir);
        Files.createDirectories(outputDir);
        return outputDir;
    }

    //--------------------------------------------------------------------------
    public Optional<String> getOutputSubDirectoryPath() {
        return outputSubDirectoryPath;
    }

    /**
     * if the subDirPath is set by setSubDir(Path d), then will return
     *     getOutputDir().resolve(subDirPath)
     * if the subDirpath is not set, then will return the same as getOutputDir()
     * @return Path of output sub directory
     */
    public Path resolveSubOutputDirectory() {
        return this.outputSubDirectoryPath.map(s -> resolveOutputDirectory().resolve(s))
                    .orElseGet(this::resolveOutputDirectory);
    }

    public Path createSubOutputDirectory() throws IOException {
        Path d = resolveOutputDirectory();
        Files.createDirectories(d);
        return d;
    }

    /**
     * remove the directory, which is identifiable by outputDir.resolve(subDirPath), recursively
     * if it is already present. Will re-create the dir which will be empty.
     *
     * @return the reference to this TestOutputOrganizer instance
     * @throws IOException during removing files/directories
     */
    public Path cleanSubOutputDirectory() throws IOException {
        Path outputSubDir = this.createSubOutputDirectory();
        DeleteDir.deleteDirectoryRecursively(outputSubDir);
        Files.createDirectories(outputSubDir);
        return outputSubDir;
    }

    //--------------------------------------------------------------------------
    public Path resolveClassOutputDirectory() {
        if (this.outputSubDirectoryPath.isPresent() && this.isByFullyQualifiedClassName) {
            return resolveSubOutputDirectory();
        } else {
            throw new IllegalStateException("getClassOutputDirectory will be operational " +
                    "only when you specify the subDirPath to the Builder " +
                    "and the specified subDirPath string is in the format of " +
                    "Fully-Qualified-Class-Name");
        }
    }

    public Path createClassOutputDirectory() throws IOException {
        Path d = resolveClassOutputDirectory();
        Files.createDirectories(d);
        return d;
    }

    public Path cleanClassOutputDirectory() throws IOException {
        Path classOutputDir = this.createClassOutputDirectory();
        DeleteDir.deleteDirectoryRecursively(classOutputDir);
        Files.createDirectories(classOutputDir);
        return classOutputDir;
    }

    //--------------------------------------------------------------------------
    public Path resolveMethodOutputDirectory(String testMethodName) {
        Objects.requireNonNull(testMethodName);
        assert !testMethodName.isEmpty();
        return resolveClassOutputDirectory().resolve(testMethodName);
    }

    public Path resolveMethodOutputDirectory(Method testMethod) {
        Objects.requireNonNull(testMethod);
        return resolveClassOutputDirectory().resolve(testMethod.getName());
    }

    public Path createMethodOutputDirectory(String testMethodName) throws IOException {
        Path d = resolveMethodOutputDirectory(testMethodName);
        Files.createDirectories(d);
        return d;
    }

    public Path createMethodOutputDirectory(Method testMethod) throws IOException {
        Path d = resolveMethodOutputDirectory(testMethod.getName());
        Files.createDirectories(d);
        return d;
    }

    public Path cleanMethodOutputDirectory(String testMethodName) throws IOException {
        Path methodOutputDir = this.createMethodOutputDirectory(testMethodName);
        DeleteDir.deleteDirectoryRecursively(methodOutputDir);
        Files.createDirectories(methodOutputDir);
        return methodOutputDir;
    }

    public Path cleanMethodOutputDirectory(Method testMethod) throws IOException {
        Path methodOutputDir = this.createMethodOutputDirectory(testMethod);
        DeleteDir.deleteDirectoryRecursively(methodOutputDir);
        Files.createDirectories(methodOutputDir);
        return methodOutputDir;
    }

    //--------------------------------------------------------------------------

    private static final String TILDE = "~";

    /**
     * This method is meant to be used in messages and documentations.
     * You do not want to show your own personal name in the console messages, right?
     * So you will surely want to hide the username part of paths.
     * This method translate a Path of "/User/kazurayam/github/unittest-helper/app/foo.txt"
     * to "~/github/unittest-helper/app/foo.txt" which is relative to the $HOME
     * of the user if the path is located under the $HOME. If the path is NOT
     * located under the $HOME, just stringify the absolute path.
     *
     * This method returns a strin in the UNIX style path representation separated
     * with '/' charactor even on Windows platform.
     *
     * @param path a Path object to be translated into a Home Relative path string
     * @return a path string prepended by tilde `~` if the path starts with "user.home"
     */
    public String toHomeRelativeString(Path path) {
        Objects.requireNonNull(path);
        Path p = path.toAbsolutePath();
        String userHomeString = System.getProperty("user.home");
        if (userHomeString == null) {
            throw new IllegalStateException("System property user.home is null");
        }
        Path userHome = fileSystem.getPath(userHomeString);
        try {
            if (p.toUri().toURL().toString().startsWith(
                    userHome.toAbsolutePath().toUri().toURL().toString())) {
                // the other path is under the user.dir
                Path relativePath = userHome.relativize(p).normalize();
                return TILDE + File.separator + relativePath.toString();
            } else {
                return p.toString();
            }
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * copy the source directory and its content files/directories to the target directory
     * recursively.
     * If the targetDir is not existing, will create it.
     *
     * @param sourceDir directory to copy from
     * @param targetDir directory to copy into
     * @throws IOException any error while i/o
     */
    public void copyDir(Path sourceDir, Path targetDir) throws IOException {
        CopyDir.copyDir(sourceDir, targetDir);
    }


    /**
     * delete the target directory and its content files/directories recursively.
     *
     * @param targetDir directory to delete
     * @throws IOException any error while deletion
     */
    public void deleteDir(Path targetDir) throws IOException {
        DeleteDir.deleteDirectoryRecursively(targetDir);
    }

    /**
     * Joshua Bloch's "Builder" for the TestOutputOrganizer class
     */
    public static class Builder {
        private final FileSystem fileSystem;
        private final Class<?> clazz;
        private Path projectDirectory;
        private List<CodeSourcePathElementsUnderProjectDirectory> listOfCSPEUPD;
        private String outputDirectoryPathRelativeToProject;
        private Optional<String> subOutputDirectoryPath = Optional.empty();
        private Boolean isByFullyQualifiedClassName;

        /**
         * The name of the directory created by TestOutputOrganizer as default
         * when you do not call setOutputDirPath(Path)
         */
        private static final String DEFAULT_OUTPUT_DIRECTORY_RELATIVE_TO_PROJECT = "test-output";

        public Builder(Class<?> clazz) {
            this(FileSystems.getDefault(), clazz);
        }

        /**
         * Constructor
         * @param fileSystem the instance of java.nio.file.FileSystem
         * @param clazz the Class object of a test class
         */
        public Builder(FileSystem fileSystem, Class<?> clazz) {
            this.fileSystem = fileSystem;
            this.clazz = clazz;
            this.projectDirectory = null;
            this.listOfCSPEUPD = new ArrayList<>();
            this.outputDirectoryPathRelativeToProject = DEFAULT_OUTPUT_DIRECTORY_RELATIVE_TO_PROJECT;
            this.subOutputDirectoryPath = Optional.empty();
            this.isByFullyQualifiedClassName = false;
        }

        /*
         * add a codeSourcePathElementsUnderProjectDirectory into the registration list
         * @param CodeSourcePathElementsUnderProjectDirectory like ["bin", "classes"]
         * @return the reference to this Builder instance
         */
        public Builder addCodeSourcePathElementsUnderProjectDirectory(
                CodeSourcePathElementsUnderProjectDirectory cspeupd) {
            Objects.requireNonNull(cspeupd);
            if (cspeupd.isEmpty()) {
                throw new IllegalArgumentException("cspeupd is empty");
            }
            this.listOfCSPEUPD.add(cspeupd);
            return this;
        }

        /**
         * optional. default to "./test-output".
         * set a relative path of under the output directory.
         *
         * @param subPath e.g., Paths.get(this.getClass().getName()) or
         *               Paths.get("com.kazurayam.unittesthelperdemo.WithHelperTest")
         * @return the reference to this Builder.Builder instance
         */
        public Builder outputDirectoryRelativeToProject(String subPath, String ... more) {
            Objects.requireNonNull(subPath);
            Path sdp = fileSystem.getPath(subPath, more);
            if (sdp.isAbsolute()) {
                throw new IllegalArgumentException(
                        "outputDirectoryRelativeToProject must NOT be absolute: " + subPath);
            }
            this.outputDirectoryPathRelativeToProject = sdp.toString();
            return this;
        }

        public Builder subOutputDirectory(String subDirectory) {
            Objects.requireNonNull(subDirectory);
            this.subOutputDirectoryPath = Optional.of(subDirectory);
            return this;
        }

        public Builder subOutputDirectory(Class<?> clazz) {
            Objects.requireNonNull(clazz);
            this.subOutputDirectoryPath = Optional.of(clazz.getName());
            this.isByFullyQualifiedClassName = true;
            return this;
        }

        /**
         * @return TestOutputOrganizer object
         */
        public TestOutputOrganizer build() {
            ProjectDirectoryResolver pdr = new ProjectDirectoryResolver(fileSystem);
            for (CodeSourcePathElementsUnderProjectDirectory cspeupd : listOfCSPEUPD) {
                pdr.addCodeSourcePathElementsUnderProjectDirectory(cspeupd);
            }
            this.projectDirectory = pdr.resolveProjectDirectoryViaClasspath(clazz);
            return new TestOutputOrganizer(this);
        }
    }
}
