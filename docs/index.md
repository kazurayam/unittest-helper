# Unit Test Helper

-   author: kazurayam

-   date: Nov, 2034

-   source project: <https://github.com/kazurayam/unittest-helper>

-   javadoc: <https://kazurayam.github.io/unittest-helper/api>

## Problems to solve

### Is "Current Working Directory" reliable for unit-tests? --- Not always

I encountered some difficulties in a TestNG test case in a Gradle Multi-project. I expected that a call to `System.getProperty("user.dir")` would return the Path of sub-project’s directory. In most cases, yes. It works fine. But sometimes it failed. See the following post for detail:

-   <https://github.com/kazurayam/selenium-webdriver-java/issues/22>

I couldn’t find out the reason why the current working directory got different from the project directory.

### A single dedicated output directory, not under the current working directory

In the book ["Selenium WebDriver in Java" by Boni Garcia](https://github.com/bonigarcia/selenium-webdriver-java), which is very good book, the sample test classes write a lot of files into the subproject’s root directory.

-   <https://github.com/kazurayam/selenium-webdriver-java/issues/8>

Temporary files located in the project directory make the project dirty and difficult to manage. I want to create a dedicated directory where all test classes write their output into.

## Solution

This project provides a Java class `com.kazurayam.unittest.TestHelper`.

The `TestHelper` helps your unit tests to save files into a dedicated directory in the Maven/Gradle project. Using this class, you can easily prepare a directory into which your unit tests can write files. The location of the output directory is resolved via the classpath of the unit-test class. The `TestHelper` does ot depend on the value returned by `System.getProperty("user.dir")`.

The `TestHelper` class works with any unit-testing frameworks: JUnit4, JUnit5 and TestNG.

The `TestHelper` class is compiled by Java8.

### Background

The following post in the Gradle forum gave me a clue:

-   <https://discuss.gradle.org/t/how-do-i-set-the-working-directory-for-testng-in-a-multi-project-gradle-build/7379>

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

    import com.kazurayam.unittest.TestHelper;
    import org.junit.jupiter.api.Test;

    import java.nio.file.Files;
    import java.nio.file.Path;
    import java.nio.file.Paths;

    public class HelperlessTest {

        /*
         * will create a file `<projectDir>/sample1.txt`
         */
        @Test
        public void test_write_under_current_working_directory() throws Exception {
            Path p = Paths.get("sample1.txt");
            Files.writeString(p, "Hello, world!");
            System.out.println("[test_write_under_current_working_directory] p = " +
                    TestHelper.toHomeRelativeString(p));
        }

    }

This code calls `Paths.get("sample1_txt")` to resolve the path of output file. Many developers would do the same in their own codes. This code prints the following message:

    > Task :app:testClasses
    [test_write_under_current_working_directory] p = ~/github/unittest-helper/app/sample1.txt

The call to `Paths.get(p)` interpretes a relative path to the runtime **Current Working Directory** of the process. In the above case, the current working directory WILL be set `~/github/unittest-helper/app/`. And the path is equal to the project directory.

Is the current working directory equal to the project directory always? --- Usually yes. But sometimes not. When the current working directory is different from the project directory, we will be really confused.

So I do not like my unit-tests to depend on the current working directory. Any other way?

### Example 2 : resolve the project dir via classpath

    package com.kazurayam.unittesthelperdemo;

    import com.kazurayam.unittest.TestHelper;
    import org.junit.jupiter.api.Test;

    import java.nio.file.Files;
    import java.nio.file.Path;
    import java.nio.file.Paths;

    public class WithHelperTest {

        @Test
        public void test_getProjectDir() {
            Path projectDir = new TestHelper(this.getClass()).getProjectDir();
            System.out.println("[test_getProjectDir] projectDir = " +
                    TestHelper.toHomeRelativeString(projectDir));
        }

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

        @Test
        public void test_getOutputDir_as_default() {
            Path outputDir = new TestHelper(this.getClass()).getOutputDir();
            System.out.println("[test_getOutputDir_as_default] outputDir = " +
                    TestHelper.toHomeRelativeString(outputDir));
        }

### Example 3 : create a custom output directory

        @Test
        public void test_getOutputDir_custom() {
            Path outputDir = new TestHelper(this.getClass())
                    .setOutputDirPath(Paths.get("test-output-another"))
                    .getOutputDir();
            System.out.println("[test_getOutputDir_as_default] outputDir = " +
                    TestHelper.toHomeRelativeString(outputDir));
        }

### Example 4 : write a file into the default output directory

        @Test
        public void test_write_into_the_default_dir() throws Exception {
            Path p = new TestHelper(this.getClass())
                    .resolveOutput("sample4.txt");
            Files.writeString(p, "Hello, world!");
            System.out.println("[test_write_into_the_default_dir] p = " +
                    TestHelper.toHomeRelativeString(p));
        }

### Example 5 : write a file into a subdirectory under the test-output

        @Test
        public void test_write_into_subdir_under_the_default_dir() throws Exception {
            Path p = new TestHelper(this.getClass())
                    .resolveOutput("sub/sample5.txt");
            Files.writeString(p, "Hello, world!");
            System.out.println("[test_write_into_subdir_under_the_default_dir] p = " + TestHelper.toHomeRelativeString(p));
        }

### Example 6 : write a file into a custom output directory

        @Test
        public void test_write_into_custom_dir() throws Exception {
            Path p = new TestHelper(this.getClass())
                    .setOutputDirPath(Paths.get("build/tmp/testOutput"))
                    .resolveOutput("sample6.txt");
            Files.writeString(p, "Hello, world!");
            System.out.println("[test_write_into_custom_dir] p = " + TestHelper.toHomeRelativeString(p));
        }

### Translating a Path to a Home Relative string

A Path object can be stringified to an absolute path string like

    /Users/kazurayam/github/unittest-helper/lib/

In this string you can find my personal name `kazurayam`. I do not like exposing my name in the blog posts and the documentations. I would prefer Home Relative path expression starting with tilde character, like:

    ~/github/unittest-helper/lib/

The `TestHelper` class implements a method `String toHomeRelativeString(Path p)`. This method does the translation.

        @Test
        public void test_toHomeRelativeString_simple() {
            Path p = new TestHelper(this.getClass()).getProjectDir();
            String s = TestHelper.toHomeRelativeString(p);
            System.out.println("[test_toHomeRelativeString_simple] s = " + s);
            assertThat(s).isEqualTo("~/github/unittest-helper/lib");
        }

This test prints the following output in the console:

    [test_toHomeRelativeString_simple] s = ~/github/unittest-helper/lib
