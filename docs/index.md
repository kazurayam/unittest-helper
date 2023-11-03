-   [Unit Test Helper](#unit-test-helper)
    -   [Problems to solve](#problems-to-solve)
        -   [Is "Current Working Directory" reliable for unit-tests? --- Not always](#is-current-working-directory-reliable-for-unit-tests-not-always)
        -   [Temporary output files shouldn’t be located into the project’s directory](#temporary-output-files-shouldnt-be-located-into-the-projects-directory)
    -   [Solution](#solution)
        -   [Background](#background)
    -   [Description by examples](#description-by-examples)
        -   [Example 1: Resolving a file path by Current Working Directory](#example-1-resolving-a-file-path-by-current-working-directory)
        -   [Example 2 : resolve the project dir via classpath](#example-2-resolve-the-project-dir-via-classpath)
        -   [Example 3 : locate the default output directory `test-output`](#example-3-locate-the-default-output-directory-test-output)
        -   [Example 3 : create a custom output directory](#example-3-create-a-custom-output-directory)
        -   [Example 4 : write a file into the default output directory](#example-4-write-a-file-into-the-default-output-directory)
        -   [Example 5 : write a file into a subdirectory under the test-output](#example-5-write-a-file-into-a-subdirectory-under-the-test-output)
        -   [Example 6 : write a file into a custom output directory](#example-6-write-a-file-into-a-custom-output-directory)
        -   [Removing the output directory recursively](#removing-the-output-directory-recursively)
        -   [Translating a Path to a Home Relative string](#translating-a-path-to-a-home-relative-string)
        -   [You should create a Factory class for your customized TestOutputOrganizer](#you-should-create-a-factory-class-for-your-customized-testoutputorganizer)

# Unit Test Helper

-   author: kazurayam

-   date: Nov, 2023

-   source project: <https://github.com/kazurayam/unittest-helper>

-   javadoc: <https://kazurayam.github.io/unittest-helper/api>

## Problems to solve

### Is "Current Working Directory" reliable for unit-tests? --- Not always

I encountered some difficulties in a TestNG test case in a Gradle Multi-project. I expected that a call to `System.getProperty("user.dir")` would return the Path of subproject’s directory. In most cases, yes. It works fine. But sometimes it failed. See the following post for detail:

-   <https://github.com/kazurayam/selenium-webdriver-java/issues/22>

I couldn’t find out the reason why the "current working directory" became the parent project’s directory, not the subproject directory.

### Temporary output files shouldn’t be located into the project’s directory

The easiest way to locate an output file from a unit-test is to call `java.io.File("some-file.txt")` or `java.nio.Paths("some-file.txt")`. Then the `some-file.txt` will be located under the current working directory = `System.getProperty("user.dir")`. Using Maven and Gradle, the current working directory will usually be equal to the project’s directory. By calling `java.io.File(relative path)` often, you will get a lot of temporary files located in the project directory, like this.

    .
    ├── 2023.10.24_22.07.27.742-7440524241d0dbd63ca5eec377b6455c.png    --- x
    ├── 2023.10.24_22.07.29.333-7440524241d0dbd63ca5eec377b6455c.png    --- x
    ├── build
    │   ├── allure-results
    │   ├── classes
    │   ├── downloads
    │   ├── generated
    │   ├── reports
    │   ├── resources
    │   ├── test-results
    │   └── tmp
    ├── build.gradle
    ├── extentReport.html    --- x
    ├── fullpage-screenshot-chrome.png    --- x
    ├── gradle
    │   └── wrapper
    ├── gradlew
    ├── gradlew.bat
    ├── login.har    --- x
    ├── my-pdf.pdf    --- x
    ├── pom.xml
    ├── screenshot.png    --- x
    ├── src
    │   ├── main
    │   └── test
    ├── target
    │   ├── classes
    │   ├── generated-sources
    │   ├── generated-test-sources
    │   ├── maven-status
    │   └── test-classes
    ├── testAccessibility.json    --- x
    ├── webdrivermanager.pdf    --- x
    ├── webdrivermanager.png    --- x
    └── webelement-screenshot.png    --- x

    20 directories, 15 files

Here the files labeled with "--- x" are the temporary output files created by the unit-tests.

Temporary files located in the project directory make the project tree dirty. The files scattered in the project directory are difficult to manage. If you want to remove them, you have to choose each files and delete them one by one manually.

Rather, I want to create a dedicated directory where all test classes should write their output into. I would list it in the `.gitignore` file to exclude the temporary files out of the git repository.

## Solution

This project provides a Java class [`com.kazurayam.unittest.TestOutputOrganizer`](https://github.com/kazurayam/unittest-helper/blob/develop/lib/src/main/java/com/kazurayam/unittest/TestOutputOrganizer.java).

The `TestOutputOrganizer` helps your unit tests to save files into a dedicated directory in the Maven/Gradle project. Using this class, you can easily prepare a directory into which your unit tests can write files. The location of the output directory is resolved via the classpath of the unit-test class. The `TestOutputOrganizer` does ot depend on the value returned by `System.getProperty("user.dir")`.

The `TestOutputOrgainzer` class is independent on the type of unit-testing frameworks you choose: JUnit4, JUnit5 and TestNG.

The `TestOutputOrgainzer` class is compiled by Java8.

### Background

The following post in the Gradle forum gave me a clue:

-   [Gradle Forums, How do I set the working dreictory for TestNG in a multi-project Gradle build?](https://discuss.gradle.org/t/how-do-i-set-the-working-directory-for-testng-in-a-multi-project-gradle-build/7379/7)

> luke\_daley
> Gradle Employee
> Nov '13
>
> Loading from the filesystem using relative paths during unit tests is problematic because different environments will set a different working directory for the test process. For example, Gradle uses the projects directory while IntelliJ uses the directory of the root project.
>
> The only really safe way to solve this problem is to load via the classpath. Is this a possibility for your scenario?

## Description by examples

### Example 1: Resolving a file path by Current Working Directory

    package com.kazurayam.unittesthelperdemo;

    import com.kazurayam.unittest.TestOutputOrganizer;
    import org.junit.jupiter.api.Test;

    import java.nio.charset.StandardCharsets;
    import java.nio.file.Files;
    import java.nio.file.Path;
    import java.nio.file.Paths;

    public class OrganizerAbsentTest {

        /*
         * will create a file `<projectDir>/sample1.txt`
         */
        @Test
        public void test_write_under_current_working_directory() throws Exception {
            Path p = Paths.get("sample1.txt");
            Files.write(p, "Hello, world!".getBytes(StandardCharsets.UTF_8));
            System.out.println("[test_write_under_current_working_directory] p = " +
                    TestOutputOrganizer.toHomeRelativeString(p));
        }

    }

[source](https://github.com/kazurayam/unittest-helper/blob/develop/app/src/test/java/com/kazurayam/unittesthelperdemo/OrganizerAbsentTest.java)

This code calls `Paths.get("sample1_txt")` to resolve the path of output file. Many developers would do the same in their own codes. This code prints the following message:

    > Task :app:testClasses
    [test_write_under_current_working_directory] p = ~/github/unittest-helper/app/sample1.txt

The call to `Paths.get("sample1.txt")` regards the parameter `sample1.txt` as relative to the runtime **Current Working Directory**. In the above case, the current working directory WILL be set `~/github/unittest-helper/app/`. And the path is equal to the project directory. So the `Paths.get("sample1.txt")` will return a Path object of `~/github.unittest-helper/app/sample1.txt`.

Is the **current working directory** equal to the **project directory** ? --- Usually yes. But sometimes not. It depends on the runtime environment. When the current working directory is different from the project directory, we will be really confused.

So I do not like my unit-tests to depend on the current working directory. Any other way?

### Example 2 : resolve the project dir via classpath

    package com.kazurayam.unittesthelperdemo;

    import com.kazurayam.unittest.TestOutputOrganizer;
    import org.junit.jupiter.api.Test;

    import java.nio.charset.StandardCharsets;
    import java.nio.file.Files;
    import java.nio.file.Path;
    import java.nio.file.Paths;

    public class OrganizerPresentTest {

        @Test
        public void test_getProjectDir() {
            TestOutputOrganizer too = new TestOutputOrganizer.Builder(this.getClass()).build();
            Path projectDir = too.getProjectDir();
            System.out.println("[test_getProjectDir] projectDir = " +
                    TestOutputOrganizer.toHomeRelativeString(projectDir));

[source](https://github.com/kazurayam/unittest-helper/blob/develop/app/src/test/java/com/kazurayam/unittesthelperdemo/OrganizerPresentTest.java)

`new TestHelper(this.getClass()).getProjectDir()` returns the `java.nio.file.Path` of the project directory.

This test prints the following result in the console:

    sublistPattern [build, classes, java, test] is found in the code source path [Users, kazurayam, github, unittest-helper, app, build, classes, java, test] at the index 5
    [test_getProjectDir] projectDir = ~/github/unittest-helper/app

This message tells how `TestHelper.getProject()` internally works.

1.  The constructor call `new TestHelper(this.getClass())` tells the `TestHelper` instance that the class was loaded from the directory `/Users/kazurayam/github/unittest-helper/app/build/classes/java/test`.

2.  The `TestHelper` internally tries to find out which build tool you used: Maven or Gradle? If you used Maven, the project directory would have a subdirectory `target/test-classes`. So `TestHelper` tries to find `target/test-classes` in the code source path. If the pattern is found, then the parent directory of the `target` directory is presumed to be the project directory. If you used Gradle, the project directory would have a subdirectory `build/classes/java/test`. So `TestHelper` tries to find `build/classes/java/test` in the code source path. When the subdirectory pattern is found in the code source path, then the parent directory of the `build` directory is presumed to be the project dir.

The following patterns are implemented in the `com.kazurayam.unittest.ProjectRepositoryResolver` class:

-   `[target, test-classes]`

-   `[build, classes, java, test]`

-   `[build, classes, groovy, test]`

-   `[build, classes, kotlin, test]`

You can add more sublist patterns for your own needs by calling the `TestHelper.addSublistPattern(List<String>)` method.

### Example 3 : locate the default output directory `test-output`

Quickly find the `test-output` directory by calling `getOutputDir()`.

        }

        @Test
        public void test_getOutputDir_as_default() {
            TestOutputOrganizer too = new TestOutputOrganizer.Builder(this.getClass()).build();
            Path outputDir = too.getOutputDir();

[source](https://github.com/kazurayam/unittest-helper/blob/develop/app/src/test/java/com/kazurayam/unittesthelperdemo/OrganizerPresentTest.java)

### Example 3 : create a custom output directory

                    TestOutputOrganizer.toHomeRelativeString(outputDir));
        }

        @Test
        public void test_getOutputDir_custom() {
            TestOutputOrganizer too = new TestOutputOrganizer.Builder(this.getClass())
                    .outputDirPath(Paths.get("test-output-another"))
                    .build();

[source](https://github.com/kazurayam/unittest-helper/blob/develop/app/src/test/java/com/kazurayam/unittesthelperdemo/OrganizerPresentTest.java)

### Example 4 : write a file into the default output directory

        /*
         * will create a file `<projectDir>/test-output/sample2.txt`
         */
        @Test
        public void test_write_into_the_default_dir() throws Exception {
            TestOutputOrganizer too = new TestOutputOrganizer.Builder(this.getClass()).build();
            Path p = too.resolveOutput("sample4.txt");

[source](https://github.com/kazurayam/unittest-helper/blob/develop/app/src/test/java/com/kazurayam/unittesthelperdemo/OrganizerPresentTest.java)

### Example 5 : write a file into a subdirectory under the test-output

            System.out.println("[test_write_into_the_default_dir] p = " +
                    TestOutputOrganizer.toHomeRelativeString(p));
        }

        @Test
        public void test_write_into_subdir_under_the_default_dir() throws Exception {
            TestOutputOrganizer too = new TestOutputOrganizer.Builder(this.getClass()).build();

[source](https://github.com/kazurayam/unittest-helper/blob/develop/app/src/test/java/com/kazurayam/unittesthelperdemo/OrganizerPresentTest.java)

### Example 6 : write a file into a custom output directory

        /*
         * will create a file `<projectDir>/build/tmp/testOutput/sample3.txt`
         */
        @Test
        public void test_write_into_custom_dir() throws Exception {
            TestOutputOrganizer too =
                    new TestOutputOrganizer.Builder(this.getClass())

[source](https://github.com/kazurayam/unittest-helper/blob/develop/app/src/test/java/com/kazurayam/unittesthelperdemo/OrganizerPresentTest.java)

### Removing the output directory recursively

The `TestOutputOrganizer` class implements `cleanOutputDirectory()` method which removes the output directory recursively. See the following sample test class.

    package io.github.someone.somestuff;

    import com.kazurayam.unittest.TestOutputOrganizer;
    import org.junit.jupiter.api.BeforeAll;
    import org.junit.jupiter.api.BeforeEach;
    import org.junit.jupiter.api.Test;

    import java.io.IOException;
    import java.nio.charset.StandardCharsets;
    import java.nio.file.Files;
    import java.nio.file.Path;
    import java.time.LocalDateTime;
    import java.time.format.DateTimeFormatter;

    import static org.assertj.core.api.Assertions.assertThat;

    public class SampleTest {

        private static TestOutputOrganizer too;

        private DateTimeFormatter dtf;

        @BeforeAll
        public static void beforeAll() throws IOException {
            too = TestOutputOrganizerFactory.create(SampleTest.class);
            too.cleanOutputDirectory();   // remove the test-output dir recursively
        }

        @BeforeEach
        public void setup() {
            dtf = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
        }

        @Test
        public void test_write_file() throws IOException {
            LocalDateTime ldt = LocalDateTime.now();
            Path p = too.resolveOutput(
                    String.format("test_write_file/sample_%s.txt", dtf.format(ldt)));
            Files.write(p, "Hello, world!".getBytes(StandardCharsets.UTF_8));
            assertThat(p).isNotNull().exists();
            assertThat(p.toFile().length()).isGreaterThan(0);
            System.out.println("[test_write_file] output is found at " +
                    TestOutputOrganizer.toHomeRelativeString(p));
        }
    }

[source](https://github.com/kazurayam/unittest-helper/blob/develop/app/src/test/java/io/github/someone/somestuff/SampleTest.java)

The `@BeforeClass`-annotated method is invoked once as soon as this test class started only once. By calling `too.cleanOutputDirectory()`, the `test-output` directory is removed. This method is useful when the test class writes files with timestamp in its name. If you do not clean the dir, you will accumulate a lot of files with different timestamps in the file name. For example:

    app/build/tmp/testOutput
    └── io.github.someone.somestuff.SampleTest
        └── test_write_file
            ├── sample_20231103_090143.txt
            ├── sample_20231103_094723.txt
            ├── sample_20231103_094759.txt
            ├── sample_20231103_094810.txt
            └── sample_20231103_094817.txt

By `cleanOutputDirectory`, you would have a cleaner result, like:

    app/build/tmp/testOutput
    └── io.github.someone.somestuff.SampleTest
        └── test_write_file
            └── sample_20231103_094817.txt

### Translating a Path to a Home Relative string

A Path object can be turned into a string, which is an absolute path string like:

    /Users/kazurayam/github/unittest-helper/lib/

In this string you can find my personal name `kazurayam`.

However, quite often, I do not like exposing my personal name in the forum posts and in the documentations. I would prefer Home Relative path expression starting with tilde character, like:

    ~/github/unittest-helper/lib/

The `TestOutputOrganizer` class implements a method `String toHomeRelativeString(Path p)`. This method does the translation.

        @Test
        public void test_toHomeRelativeString_simple() {
            TestOutputOrganizer too = new TestOutputOrganizer.Builder(this.getClass()).build();
            Path projectDir = too.getProjectDir();
            String homeRelative = TestOutputOrganizer.toHomeRelativeString(projectDir);
            System.out.println("[test_toHomeRelativeString_simple] " + homeRelative);
            assertThat(homeRelative).isEqualTo("~/github/unittest-helper/lib");
        }

This test prints the following output in the console:

    [test_toHomeRelativeString_simple] ~/github/unittest-helper/lib

### You should create a Factory class for your customized TestOutputOrganizer

It is a good practice for you to create a factory class that creates an instance of `TestOutputOrganizer` with customized parameter values. See the following example.

    package io.github.someone.somestuff;

    import com.kazurayam.unittest.TestOutputOrganizer;

    import java.nio.file.Paths;

    /**
     * A Factory class that creates an instance of com.kazurayam.unittest.TestHelper
     * initialized with custom values of "outputDirPath" and "subDirPath"
     */
    public class TestOutputOrganizerFactory {

        public static TestOutputOrganizer create(Class clazz) {
            return new TestOutputOrganizer.Builder(clazz)
                    .outputDirPath(Paths.get("build/tmp/testOutput"))
                    .subDirPath(Paths.get(clazz.getName()))
                        // e.g, "io.github.somebody.somestuff.SampleTest"
                    .build();
        }
    }

The `create(Class)` method will instanciate a `com.kazurayam.unittest.TestOutputOrganizer` class with customized parameter values:

1.  the output directory will be located at `<projectDir>/build/tmp/testOutput`

2.  in the output directory, it will create subdirectory of which name is equal to the Fully Qualified Class Name of the test class.

The following test class uses the Factory.

    package io.github.someone.somestuff;

    import com.kazurayam.unittest.TestOutputOrganizer;
    import org.junit.jupiter.api.BeforeAll;
    import org.junit.jupiter.api.BeforeEach;
    import org.junit.jupiter.api.Test;

    import java.io.IOException;
    import java.nio.charset.StandardCharsets;
    import java.nio.file.Files;
    import java.nio.file.Path;
    import java.time.LocalDateTime;
    import java.time.format.DateTimeFormatter;

    import static org.assertj.core.api.Assertions.assertThat;

    public class SampleTest {

        private static TestOutputOrganizer too;

        private DateTimeFormatter dtf;

        @BeforeAll
        public static void beforeAll() throws IOException {
            too = TestOutputOrganizerFactory.create(SampleTest.class);
            too.cleanOutputDirectory();   // remove the test-output dir recursively
        }

        @BeforeEach
        public void setup() {
            dtf = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
        }

        @Test
        public void test_write_file() throws IOException {
            LocalDateTime ldt = LocalDateTime.now();
            Path p = too.resolveOutput(
                    String.format("test_write_file/sample_%s.txt", dtf.format(ldt)));
            Files.write(p, "Hello, world!".getBytes(StandardCharsets.UTF_8));
            assertThat(p).isNotNull().exists();
            assertThat(p.toFile().length()).isGreaterThan(0);
            System.out.println("[test_write_file] output is found at " +
                    TestOutputOrganizer.toHomeRelativeString(p));
        }
    }

When you ran the test, the output directory will look like this:

    app/build/tmp/testOutput
    └── io.github.someone.somestuff.SampleTest
        └── test_write_file
            └── sample_20231103_094817.txt

Which test class, which method created this file? --- It’s obvious to see in this file tree.

Please note that here 2 layers of directories are inserted amongst the output directory `app/build/tmp/testOutput` and the file `sample_yyyyMMdd_HHmmss.txt`. The first layer is the FQCN of the test class, the second layer is the method name which actually wrote the file. This tree helps you well organize the output files created by your test cases.
