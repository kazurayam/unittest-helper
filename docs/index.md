# Unit Test Helper

-   author: kazurayam

-   date: Nov, 2034

-   source project: <https://github.com/kazurayam/unittest-helper>

-   javadoc: <https://kazurayam.github.io/unittest-helper/api>\]

## Problems to solve

### Is "Current Working Directory" reliable for unit-tests? --- Not always

I encountered some difficulties in a TestNG test case in a Gradle Multiproject. I expected that a call to `System.getProperty("user.dir")` would return the Path of sub-project’s directory. In most cases, it works fine but sometimes it mal-functioned. See the following post for detail:

-   <https://github.com/kazurayam/selenium-webdriver-java/issues/22>

I haven’t managed to find out the reason why the current working directory got different from the project directory. I have given up trying to find.

The following post in the Gradle forum gave me a clue:

-   <https://discuss.gradle.org/t/how-do-i-set-the-working-directory-for-testng-in-a-multi-project-gradle-build/7379>

> luke\_daley
> Gradle Employee
> Nov '13
>
> Loading from the filesystem using relative paths during unit tests is problematic because different environments will set a different working directory for the test process. For example, Gradle uses the projects directory while IntelliJ uses the directory of the root project.
>
> The only really safe way to solve this problem is to load via the classpath. Is this a possibility for your scenario?

### I want my test classes to write files into a single dedicated directory, not under the current working directory

In the book ["Selenium WebDriver in Java" by Boni Garcia](https://github.com/bonigarcia/selenium-webdriver-java), which is very good book, the sample test classes write a lot of files into the sub-project’s root directory. In my opinion, it is a bad practice. I want to create a dedicated directory where all test classes write their output into. See the following issue for more detail.

-   <https://github.com/kazurayam/selenium-webdriver-java/issues/8>

## Solution

This project provides a Java class `com.kazurayam.unittest.TestHelper`.

The `TestHelper` helps your unit tests to save files into a dedicated directory in the Maven/Gradle project. Using this class, you can easily prepare a directory into which your unit tests can write files. The location of the output directory is resolved via the classpath of the unit-test class. The `TestHelper` does ot depend on the value returned by `System.getProperty("user.dir")`.

The `TestHelper` class works with any unit-testing frameworks: JUnit4, JUnit5 and TestNG.

The `TestHelper` class is compiled by Java8.

## Description by examples

### Resolving a file path by Current Working Directory

    package com.kazurayam.unittest.demo;

    import com.kazurayam.unittest.TestHelper;
    import org.junit.jupiter.api.Test;

    import java.nio.file.Files;
    import java.nio.file.Path;
    import java.nio.file.Paths;

    public class HelperlessDemo {

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

The call to `Paths.get(p)` interpretes a relative path to the runtime **Current Working Directory** of the process. In the above case, `~/github/unittest-helper/app/` is the current working directory, and it happened to be equal to the project directory.

Is the current working directory equal to the project directory? --- Usually yes. But sometimes not. When the current working directory has got different from the project directory, it will confuse us very much. So I do not like to depend on the current working directory in my unit-tests.
