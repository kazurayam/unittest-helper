package com.kazurayam.unittest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.CodeSource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

/**
 * ProjectDirectoryResolver resolves the project's directory based on the classpath
 * of a JVM class you are developing.
 *
 * The following quote from
 * https://discuss.gradle.org/t/how-do-i-set-the-working-directory-for-testng-in-a-multi-project-gradle-build/7379/7
 * explains what I wanted to do.
 *
 * <blockquote>
 *     <p>luke_daley Gradle Employee Nov '13</p>
 *
 *     <p>Loading from the filesystem using relative paths during unit tests is problematic
 *     because different environments will set a different working directory for the test process.
 *     For example, Gradle uses the projects directory while IntelliJ uses the directory of the root project.</p>
 *
 *     <p>The only really safe way to solve this problem is to load via the classpath.
 *     Is this a possibility for your scenario?</p>
 * </blockquote>
 */
public final class ProjectDirectoryResolver {

    private static final Logger log = LoggerFactory.getLogger(ProjectDirectoryResolver.class);

    private final List<List<String>> sublistPatterns;

    /**
     * Sole constructor.
     */
    public ProjectDirectoryResolver() {
        this.sublistPatterns = new ArrayList<>();
        sublistPatterns.add(Arrays.asList("target", "test-classes"));   // Maven
        sublistPatterns.add(Arrays.asList("build", "classes", "java", "test"));  // Gradle, Java
        sublistPatterns.add(Arrays.asList("build", "classes", "groovy", "test"));  // Gradle, Groovy
        sublistPatterns.add(Arrays.asList("build", "classes", "kotlin", "test"));  // Gradle, Kotlin
    }

    /**
     *
     * @param sublistPattern e.g, ["bin", "groovy"], ["bin", "keyword"], ["bin", "lib"], ["bin", "listener"], ["bin", "testcase"]
     */
    public void addSublistPattern(List<String> sublistPattern) {
        Objects.requireNonNull(sublistPattern);
        if (sublistPattern.isEmpty()) {
            throw new IllegalArgumentException("sublistPattern must not be null");
        }
        this.sublistPatterns.add(sublistPattern);
    }

    /**
     *
     * @return the list of Sublist Patterns including both builtin and ones you added
     */
    public List<List<String>> getSublistPatterns() {
        List<List<String>> clone = new ArrayList<>();
        for (List<String> l : sublistPatterns) {
            List<String> e = new ArrayList<>();
            for (String le : l) {
                e.add(le);
            }
            clone.add(e);
        }
        return clone;
    }

    /**
     * returns the Path in which the class binary of
     * `com.kazurayam.unittest.ProjectDirectoryResolver` is found on disk.
     *
     * e.g, "/Users/somebody/selenium-webdriver-java/selenium-webdriver-junit4/build/classes/java/test/" when built by Gradle
     * e.g, "/Users/somebody/selenium-webdriver-java/selenium-webdriver-junit4/target/test-classes/" when built by Maven
     *
     * @param clazz the Class object based on which the project dir is resolved
     * @return a java.nio.file.Path instance which is derived by clazz.getProtectionDomain().getCodeSource().getLocation()
     */
    public static final Path getCodeSourceAsPath(Class clazz) {
        CodeSource codeSource = clazz.getProtectionDomain().getCodeSource();
        URL url = codeSource.getLocation();
        try {
            Path path = Paths.get(url.toURI());
            log.trace("The code source : " + path);
            return path;
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * returns the project directory in which the class binary of
     * `com.kazurayam.webdriver.TestOutputOrganizer` is found on disk.
     * The same value will be returned in either case where the class
     * was built by Gradle and Maven.
     *
     * e.g, "/Users/somebody/selenium-webdriver-java/selenium-webdriver-junit4/"
     *
     * The point is, the System property "user.dir" will return
     * "/Users/somebody/selenium-webdriver-java".
     * This does not include the subproject directory
     * "selenium-webdriver-junit4", therefore it is not what we want.
     *
     * When the TestOutputOrganizer class is built using Gradle, the class will be stored
     * in the directory "selenium-webdriver-java/selenium-webdriver-junit4/build".
     * When the TestOutputOrganizer class is built using Maven, the class will be stored
     * in the directory "selenium-webdriver-java/selenium-webdriver-junit5/target".
     * So this method look up the file name "build" or "target" in the code source
     * of the TestOutputOrganizer class.
     * Then we can get the Path value of the project directory properly.
     *
     * @param clazz the Class object based on which the project dir is resolved
     * @return a java.nio.file.Path instance that represents the project directory.
     *         if the project is a Gradle Multiproject, then this method returns the
     *         path of the subproject's root dir.
     */
    public Path getProjectDirViaClasspath(Class clazz) {
        Path codeSourcePath = ProjectDirectoryResolver.getCodeSourceAsPath(clazz);
        // e.g. "/Users/myname/oreilly/selenium-webdriver-java/selenium-webdriver-junit4/build/classes/java/test/com/kazurayam/webdriver/TestOutputOrganizer.class"
        List<String> nameElements = toNameElements(codeSourcePath);
        StringSequence ss = new StringSequence(nameElements);
        int boundary = -1;
        for (List<String> sublistPattern : this.sublistPatterns) {
            int indexOfBuildDir = ss.indexOfSubsequence(sublistPattern);
            if (indexOfBuildDir > 0) {
                boundary = indexOfBuildDir;
                log.trace(String.format("sublistPattern %s is found in the code source path %s at the index %d",
                        sublistPattern, ss, boundary));
                break;
            } else {
                log.trace(String.format("sublistPattern %s is NOT found in the code source path %s",
                        sublistPattern, ss));
            }
        }
        if (boundary == -1) {
            throw new IllegalStateException("unable to resolve the project directory via classpath");
        }
        // build the project dir to return as the result
        Path w = Paths.get("/");
        for (int i = 0; i < boundary; i++) {
            w = w.resolve(nameElements.get(i));
        }
        return w;   // e.g, /Users/myname/oreilly/selenium-webdriver-java/selenium-webdriver-junit4
    }

    private static List<String> toNameElements(Path codeSourcePath) {
        List<String> nameElements = new ArrayList<>();
        Iterator<Path> iter = codeSourcePath.iterator();
        while (iter.hasNext()) {
            Path p = iter.next();
            nameElements.add(p.getFileName().toString());
        }
        return nameElements;
    }

}
