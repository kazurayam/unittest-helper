-   [Unit Test Helper](#unit-test-helper)
    -   [Problems to solve](#problems-to-solve)
        -   [Is "Current Working Directory" reliable for unit-tests? --- Not always](#is-current-working-directory-reliable-for-unit-tests-not-always)
        -   [A single dedicated output directory, not under the current working directory](#a-single-dedicated-output-directory-not-under-the-current-working-directory)
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
        -   [Translating a Path to a Home Relative string](#translating-a-path-to-a-home-relative-string)

# Unit Test Helper

-   author: kazurayam

-   date: Nov, 2023

-   source project: <https://github.com/kazurayam/unittest-helper>

-   javadoc: <https://kazurayam.github.io/unittest-helper/api>

## Problems to solve

### Is "Current Working Directory" reliable for unit-tests? --- Not always

I encountered some difficulties in a TestNG test case in a Gradle Multi-project. I expected that a call to `System.getProperty("user.dir")` would return the Path of subproject’s directory. In most cases, yes. It works fine. But sometimes it failed. See the following post for detail:

-   <https://github.com/kazurayam/selenium-webdriver-java/issues/22>

I couldn’t find out the reason why the current working directory got different from the project directory.

### A single dedicated output directory, not under the current working directory

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

Temporary files located in the project directory make the project dirty. The files scattered in the project directory are difficult to manage. If you want to remove them, you have to choose each files and delete them one by one.

I want to create a dedicated directory where all test classes should write their output into.

## Solution

This project provides a Java class `com.kazurayam.unittest.TestOutputOrganizer`.

The `TestOutputOrganizer` helps your unit tests to save files into a dedicated directory in the Maven/Gradle project. Using this class, you can easily prepare a directory into which your unit tests can write files. The location of the output directory is resolved via the classpath of the unit-test class. The `TestOutputOrganizer` does ot depend on the value returned by `System.getProperty("user.dir")`.

The `TestOutputOrgainzer` class is independent on the type of unit-testing frameworks you choose: JUnit4, JUnit5 and TestNG.

The `TestOutputOrgainzer` class is compiled by Java8.

### Background

The following post in the Gradle forum gave me a clue:

-   <https://discuss.gradlecd.org/t/how-do-i-set-the-working-directory-for-testng-in-a-multi-project-gradle-build/7379>

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

### Translating a Path to a Home Relative string

A Path object can be turned into a string, which is an absolute path string like:

    /Users/kazurayam/github/unittest-helper/lib/

In this string you can find my personal name `kazurayam`. I do not like exposing my name in the blog posts and the documentations. I would prefer Home Relative path expression starting with tilde character, like:

    ~/github/unittest-helper/lib/

The `TestHelper` class implements a method `String toHomeRelativeString(Path p)`. This method does the translation.

                    new TestOutputOrganizer.Builder(this.getClass())
                            .outputDirPath(Paths.get("build/tmp/testOutput"))
                            .build();
            Path p = too.resolveOutput("hello.txt");
            Files.write(p, "Hello, world!".getBytes(StandardCharsets.UTF_8));
            assertThat(p.getParent()                   // expecting testOutput
                    .getFileName().toString())

This test prints the following output in the console:

    [test_toHomeRelativeString_simple] s = ~/github/unittest-helper/lib
