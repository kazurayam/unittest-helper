- Table of contents
{:toc}

# Unit Test Helper

-   author: kazurayam

-   date: Nov, 2023

-   version: 0.2.4

-   source project: <https://github.com/kazurayam/unittest-helper>

-   javadoc: <https://kazurayam.github.io/unittest-helper/api>

## Problems to solve

### Is Current Working Directory reliable for unit-tests? --- Not always

I encountered some difficulties in a TestNG test case in a Gradle Multi-project. I expected that a call to `System.getProperty("user.dir")` would return the Path of subproject’s directory. In most cases, yes. It works fine. But sometimes it failed. See the following post for detail:

-   <https://github.com/kazurayam/selenium-webdriver-java/issues/22>

I couldn’t find out the reason why the "current working directory" became the parent project’s directory, not the subproject directory.

The following post in the Gradle forum gave me a clue:

-   [Gradle Forums, How do I set the working dreictory for TestNG in a multi-project Gradle build?](https://discuss.gradle.org/t/how-do-i-set-the-working-directory-for-testng-in-a-multi-project-gradle-build/7379/7)

> luke\_daley
> Gradle Employee
> Nov '13
>
> Loading from the filesystem using relative paths during unit tests is problematic because different environments will set a different working directory for the test process. For example, Gradle uses the projects directory while IntelliJ uses the directory of the root project.
>
> The only really safe way to solve this problem is to load via the classpath. Is this a possibility for your scenario?

I realized that I should not write unit tests which depend on the "current working directory".

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

Using the `TestOutputOrganizer` class, you can well-organize the files created by test classes, as follows:

![well organized test outputs](https://kazurayam.github.io/unittest-helper/images/well-organized-test-outputs.png)

## How does the `TestOutputOrganizer` resolves the project root directory ?

The `getProject()` method of `TestOutputOrganizer` class internally works as follows.

1.  The constructor call `new TestOutputOrganizer.Builder(this.getClass())` tells it should look at the code source of `this` object, which is `/Users/kazurayam/github/unittest-helper/app/build/classes/java/test/com/kazurayam/unittestshelperdemo/OrganizerPresentTest.class`.

2.  The `TestOutputOrganizer` internally tries to find out which build tool you used: Maven or Gradle?
    If you used Maven, it expects that the project directory to have a subdirectory `target/test-classes`. If the `TestOutputOrganizer` found `target/test-classes` in the code source path, then the parent directory of the `target` directory is presumed to be the project directory.
    If you use Gradle, the `TestOutputOrganizer` expects that the project directory would have a subdirectory `build/classes/java/test`. So `TestOutputOrganizer` tries to find `build/classes/java/test` in the code source path. When the subdirectory pattern is found in the code source path, then the parent directory of the `build` directory is presumed to be the project dir.

The `com.kazurayam.unittest.ProjectDirectoryResovler` class has a list of the patterns to match against the code source given. You can che check the content of the list. Let me assume you have the following test code:

    package com.kazurayam.unittesthelperdemo;

    import com.kazurayam.unittest.ProjectDirectoryResolver;
    import org.junit.jupiter.api.Test;

    import java.util.List;

    import static org.assertj.core.api.Assertions.assertThat;

    public final class ProjectDirectoryResolverTest {

        @Test
        public void test_getSublistPatterns() {
            List<List<String>> sublistPatterns =
                    new ProjectDirectoryResolver().getSublistPatterns();
            assertThat(sublistPatterns).isNotNull();
            assertThat(sublistPatterns.size()).isGreaterThanOrEqualTo(2);
            for (List<String> p : sublistPatterns) {
                System.out.println("sublistPattern : " + p);
            }
        }

    }

This test prints the following result in the console:

    sublistPattern : [target, test-classes]
    sublistPattern : [build, classes, java, test]
    sublistPattern : [build, classes, groovy, test]
    sublistPattern : [build, classes, kotlin, test]

The 1st sublistPattern is for Maven. the 2nd, sublistPattern is for Java codes built in Gradle. The 3rd is for Groovy codes built in Gradle. The 4th is for Kotlin codes built in Gradle.

Do you need a unique sublistPattern other than those built-in ones?

OK. You can add more sublistPatterns for your own needs by calling the `TestOutputOrganizer.Builder.sublistPattern(List<String>)` method.

## Description by examples

### Example1 Locating a file path via Current Working Directory

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

### Example2 Resolving the project directory resolved via classpath

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
        }

[source](https://github.com/kazurayam/unittest-helper/blob/develop/app/src/test/java/com/kazurayam/unittesthelperdemo/OrganizerPresentTest.java)

This will print the following in the console:

    [test_getProjectDir] projectDir = ~/github/unittest-helper/app

How the `TestOutputOrganizer` find the path of project directory via classpath? --- I will describe it later.

### Example3 Locating the default output directory

Quickly find the `test-output` directory by calling `getOutputDir()`.

        @Test
        public void test_getOutputDir_as_default() {
            TestOutputOrganizer too = new TestOutputOrganizer.Builder(this.getClass()).build();
            Path outputDir = too.getOutputDirectory();
            System.out.println("[test_getOutputDir_as_default] outputDir = " +
                    TestOutputOrganizer.toHomeRelativeString(outputDir));
        }

[source](https://github.com/kazurayam/unittest-helper/blob/develop/app/src/test/java/com/kazurayam/unittesthelperdemo/OrganizerPresentTest.java)

This will print the following in the console:

    [test_getOutputDir_as_default] outputDir = ~/github/unittest-helper/app/test-output

### Example4 Creating a custom output directory

        @Test
        public void test_getOutputDir_custom() {
            TestOutputOrganizer too = new TestOutputOrganizer.Builder(this.getClass())
                    .outputDirPath("test-output-another")
                    .build();
            Path outputDir = too.getOutputDirectory();
            System.out.println("[test_getOutputDir_custom] outputDir = " +
                    TestOutputOrganizer.toHomeRelativeString(outputDir));
        }

[source](https://github.com/kazurayam/unittest-helper/blob/develop/app/src/test/java/com/kazurayam/unittesthelperdemo/OrganizerPresentTest.java)

This will print the following in the console:

    [test_getOutputDir_custom] outputDir = ~/github/unittest-helper/app/test-output-another

### Example5 Writing a file into the default output directory

        @Test
        public void test_write_into_the_default_output_directory() throws Exception {
            TestOutputOrganizer too = new TestOutputOrganizer.Builder(this.getClass()).build();
            Path p = too.resolveOutput("sample.txt");
            Files.write(p, "Hello, world!".getBytes(StandardCharsets.UTF_8));
            System.out.println("[test_write_into_the_default_output_directory] p = " +
                    TestOutputOrganizer.toHomeRelativeString(p));
        }

[source](https://github.com/kazurayam/unittest-helper/blob/develop/app/src/test/java/com/kazurayam/unittesthelperdemo/OrganizerPresentTest.java)

This will print the following in the console:

    [test_write_into_the_default_output_directory] p = ~/github/unittest-helper/app/test-output/sample.txt

### Example6 Writing a file into a subdirectory under the default output directory

        @Test
        public void test_write_into_subdir_under_the_default_output_directory() throws Exception {
            TestOutputOrganizer too = new TestOutputOrganizer.Builder(this.getClass()).build();
            Path p = too.resolveOutput("sub/sample.txt");
            Files.write(p, "Hello, world!".getBytes(StandardCharsets.UTF_8));
            System.out.println("[test_write_into_subdir_under_the_default_output_directory] p = " + TestOutputOrganizer.toHomeRelativeString(p));
        }

[source](https://github.com/kazurayam/unittest-helper/blob/develop/app/src/test/java/com/kazurayam/unittesthelperdemo/OrganizerPresentTest.java)

This will print the following in the console:

    [test_write_into_subdir_under_the_default_output_directory] p = ~/github/unittest-helper/app/test-output/sub/sample.txt

### Example7 Writing a file into a custom output directory

        @Test
        public void test_write_into_custom_directory() throws Exception {
            TestOutputOrganizer too =
                    new TestOutputOrganizer.Builder(this.getClass())
                            .outputDirPath("build/tmp/testOutput")
                            .build();
            Path p = too.resolveOutput("sample.txt");
            Files.write(p, "Hello, world!".getBytes(StandardCharsets.UTF_8));
            System.out.println("[test_write_into_custom_directory] p = " + TestOutputOrganizer.toHomeRelativeString(p));
        }

[source](https://github.com/kazurayam/unittest-helper/blob/develop/app/src/test/java/com/kazurayam/unittesthelperdemo/OrganizerPresentTest.java)

This will print the following in the console:

    [test_write_into_custom_directory] p = ~/github/unittest-helper/app/build/tmp/testOutput/sample.txt

### Example8 Removing the output directory recursively

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

            // remove the "test-output/io.github.someone.somestuff.SampleTest" directory recursively
            too.cleanOutputSubDirectory();

            // or too.cleanOutputDirectory() to remove the "test-output" directory
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

[source](https://github.com/kazurayam/unittest-helper/blob/develop/app/src/test/java/io/github/someone/somestuff/SampleTest.java)

This will print the following in the console:

    [test_write_file] output is found at ~/github/unittest-helper/app/build/tmp/testOutput/io.github.someone.somestuff.SampleTest/test_write_file/sample_20231104_081132.txt
    [test_write_file_once_more] output is found at ~/github/unittest-helper/app/build/tmp/testOutput/io.github.someone.somestuff.SampleTest/test_write_file_once_more/sample_20231104_081132.txt

The `@BeforeClass`-annotated method is invoked once as soon as this test class started only once. By calling `too.cleanOutputDirectory()`, the `test-output` directory will be removed.

This method is useful when the test class writes files with timestamp in its name. If you do NOT clean the dir, you will accumulate a lot of files with different timestamps in the file name. For example:

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

There is `cleanOutputSubDirectory()` method as well. This will choose a specific sub directory specified by `setSubDir(Path subDir)` method of the `TestOutputOrganizer.Builder` class. The `cleanOutputSubDirectory()` will leave other subdirectories in the output directory untouched.

### Example9 Removing anonymous directory recursively

The `TestOutputOrganizer` class implements a static method `cleanDirectoryRecursively()` method which removes any directory recursively. See the following sample test class.

        @Test
        public void test_cleanDirectoryRecursively() throws IOException {
            // given
            Path dir = Paths.get("build/work");
            Files.createDirectories(dir);
            Path file = dir.resolve("foo.txt");
            Files.write(file, "Hello, world!".getBytes(StandardCharsets.UTF_8));
            // when
            TestOutputOrganizer.cleanDirectoryRecursively(dir);
            // then
            assertThat(file).doesNotExist();
            assertThat(dir).doesNotExist();
        }

[source](https://github.com/kazurayam/unittest-helper/blob/develop/lib/src/test/java/com/kazurayam/unittes/TestOutputOrganizerTest.java)

### Example9 You should make a Factory class that creates your customized TestOutputOrganizer

It is a good practice for you to create a factory class that creates an instance of `TestOutputOrganizer` for your own unit tests instantiated with custom parameters. See the following example.

    package io.github.someone.somestuff;

    import com.kazurayam.unittest.TestOutputOrganizer;

    import java.nio.file.Paths;

    /**
     * A Factory class that creates an instance of com.kazurayam.unittest.TestHelper
     * initialized with custom values of "outputDirPath" and "subDirPath"
     */
    public class TestOutputOrganizerFactory {

        public static TestOutputOrganizer create(Class<?> clazz) {
            return new TestOutputOrganizer.Builder(clazz)
                    .outputDirPath("build/tmp/testOutput")
                    .subDirPath(clazz.getName())
                        // e.g, "io.github.somebody.somestuff.SampleTest"
                    .build();
        }
    }

This `TestOutputFactory` class implements `create(Class)` method, which will instantiate a `com.kazurayam.unittest.TestOutputOrganizer` class with customized parameter values:

1.  the output directory will be located at `<projectDir>/build/tmp/testOutput`

2.  in the output directory, it will create subdirectory of which name is equal to the "Fully Qualified Class Name" of the given test class.

The following code is using the Factory.

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

            // remove the "test-output/io.github.someone.somestuff.SampleTest" directory recursively
            too.cleanOutputSubDirectory();

            // or too.cleanOutputDirectory() to remove the "test-output" directory
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

When you ran the test, the output directory will look like this:

    app/build/tmp/testOutput
    └── io.github.someone.somestuff.SampleTest
        └── test_write_file
            └── sample_20231103_094817.txt

Please note that the file tree has 2 layers of directories in between the output directory `app/build/tmp/testOutput` and the file `sample_yyyyMMdd_HHmmss.txt`.

The 1st layer is the FQCN of the test class.

The 2nd layer is the method name which actually wrote the file.

I find this tree format is useful for organizing a lot of output files created by multiple test cases.

### Example10 More layers of directory under the output sub-directory

The [io.github.someone.somestuff.SampleTest](https://github.com/kazurayam/unittest-helper/blob/develop/app/src/test/java/io/github/someone/somestuff/SampleTest.java) class has one more test method:

        @Test
        public void test_write_file_once_more() throws IOException {
            LocalDateTime ldt = LocalDateTime.now();
            Path p = too.resolveOutput(
                    String.format("test_write_file_once_more/sample_%s.txt", dtf.format(ldt)));
            Files.write(p, "Hello, world!".getBytes(StandardCharsets.UTF_8));
            assertThat(p).isNotNull().exists();
            assertThat(p.toFile().length()).isGreaterThan(0);
            System.out.println("[test_write_file_once_more] output is found at " +
                    TestOutputOrganizer.toHomeRelativeString(p));
        }

When you ran this test, you would get the output directory as follows:

    $ tree app/build/tmp/testOutput
    app/build/tmp/testOutput
    └── io.github.someone.somestuff.SampleTest
        ├── test_write_file
        │   └── sample_20231103_124015.txt
        └── test_write_file_once_more
            └── sample_20231103_124015.txt

Please find that one more directory layer is inserted between the output dir and the files.

-   `test_write_file`

-   `test_write_file_once_more`

How these directories were created? If you read the source of the test class, you would find that it effectively executed the following calls

        Path p = too.resolveOutut("test_write_file/sample_20231103_124015.txt");

and

        Path p = too.resolveOutut("test_write_file_once_more/sample_20231103_124015.txt");

Note that the parameter string to the `resolveOutput(String)` method can contain `/`, which represents one or more directories under the output sub-directory. For example, you can insert a directory of which name stands for the test method name. This technique makes it easy to organize output files created by multiple methods in a single test class.

### Example12 A helper method that translates a Path to a Home Relative string

A Path object can be turned into an absolute path string like:

    /Users/kazurayam/github/unittest-helper/lib/

In this string you can find my personal name "kazurayam". When I write some document, I feel like to copy the output message and paste it into page. However, I do not like exposing my personal name public. I would prefer "Home Relative Path" which starts with a tilde character, like:

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

### Example13 Copying a source directory to a target directory recursively

        @Test
        void test_copyDir() throws IOException {
            TestOutputOrganizer too =
                    new TestOutputOrganizer.Builder(this.getClass())
                            .subDirPath(this.getClass().getName())
                            .build();
            String methodName = "test_copyDir";
            // given
            Path sourceDir = too.resolveOutput(methodName + "/source");
            Path sourceFile = too.resolveOutput(methodName + "/source/foo/hello.txt");
            Files.write(sourceFile, "Hello, world!".getBytes(StandardCharsets.UTF_8));
            Path targetDir = too.resolveOutput(methodName + "/target");
            Path targetFile = too.resolveOutput(methodName + "/target/foo/hello.txt");
            // when
            too.copyDir(sourceDir, targetDir);
            // then
            assertThat(targetFile).exists();
        }

### Example14 Deleting a directory recursively

        @Test
        void test_deleteDir() throws IOException {
            TestOutputOrganizer too =
                    new TestOutputOrganizer.Builder(this.getClass())
                            .subDirPath(this.getClass().getName())
                            .build();
            String methodName = "test_deleteDir";
            // given
            Path sourceDir = too.resolveOutput(methodName + "/source");
            Path sourceFile = too.resolveOutput(methodName + "/source/foo/hello.txt");
            Files.write(sourceFile, "Hello, world!".getBytes(StandardCharsets.UTF_8));
            Path targetDir = too.resolveOutput(methodName + "/target");
            Path targetFile = too.resolveOutput(methodName + "/target/foo/hello.txt");
            too.copyDir(sourceDir, targetDir);
            assertThat(targetFile).exists();
            // when
            too.deleteDir(targetDir);
            // then
            assertThat(targetFile).doesNotExist();
        }

(FIN)
