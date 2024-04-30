# unittest-helper

## What this project provides

This project provides 2 utility Java classes

1. ['com.kazurayam.unittest.ProjectDirectoryResolver'](https://github.com/kazurayam/unittest-helper/blob/develop/lib/src/main/java/com/kazurayam/unittest/ProjectDirectoryResolver.java) which enables your unit-test class to find the location of "Project Directory" by the runtime classpath, rather than depending on the `System.property("user.dir")"`.
2. [`com.kazurayam.unittest.TestOutputOrganizer`](https://github.com/kazurayam/unittest-helper/blob/develop/lib/src/main/java/com/kazurayam/unittest/TestOutuputOrganizer.java) which enables your unit-test class to resolve/create/clean a directory to store files under the "Project Directory" resolved by the "ProjectDirectoryResolver" class. You can create the output directories classified by Fully-Qualified-Class-Name and Method name.

## Problem to solve

How can a Java class as a unit-test find the location of the "project directory" 
in a Gradle project? A usual answer is that you can assume that 
the Current Working Directory is equal to the project directory. 
Therefore, the following test method will create a directory `test-output` 
immediately under the project directory.
```
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FooTest {

    @Test
    public void testFoo() {
        Path d = Paths.get("./test-output)
        Files.create
    }
}
```
But I have found that in some situation the CWD value is not equal to the project directory that I want.


I have developed `com.kazurayam.unittest.TestOutputOrganizer` class. What does this class do?

1. resolving the path of "project directory" based on the classpath of the test class rather than naively assuming that the project directory is equal to the value of `System.getProperty("user.home")` which is highly dependent on the runtime environment.
2. creating a "test-output" directory where tests can write any temporary files.
3. cleaning and recreating the test-output directory
4. creating a directory for each individual test classes under the test-output directory.
5. creating a directory for each individual methods of a test class under the test-output directory.

The `com.kazurayam.unittest.TestOutputOrganizer` class was tested using Gradle. I haven't tested it with Maven. I have tested it mainly with JUnit5, plus with TestNG a bit. I have tested it mainly on Mac. I tested it on Windows and found fine.

## Download from

- https://mvnrepository.com/artifact/com.kazurayam/unittest-helper

## How to import the jar

You want to write the build.gradle file as follows:

```
repositories {
    mavenCentral()
}

dependencies {
    testImplementation("com.kazurayam:unittest-helper:0.4.0")
}
```

## Long explanation

- [English](https://kazurayam.github.io/unittest-helper/index2.md)

## Javadoc

- [Javadoc](https://kazurayam.github.io/unittest-helper/api/)

