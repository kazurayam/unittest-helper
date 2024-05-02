# unittest-helper

## What this project provides

This project publishes 2 utility classes in Java:

1. ['com.kazurayam.unittest.ProjectDirectoryResolver'](https://github.com/kazurayam/unittest-helper/blob/develop/lib/src/main/java/com/kazurayam/unittest/ProjectDirectoryResolver.java) : This class enables your unit-test class to find the location of "project directory" based on the classpath, without depending on the value of `System.property("user.dir")"`. The System Property `user.dir` is unreliable sometimes. Resolving the project's directory is especially useful in a Gradle Multi-projects build.

2. [`com.kazurayam.unittest.TestOutputOrganizer`](https://github.com/kazurayam/unittest-helper/blob/develop/lib/src/main/java/com/kazurayam/unittest/TestOutuputOrganizer.java) : This class finds the location of project directory by calling `com.kazurayam.unittest.ProjectDirectoryResolver` class. Based on it, the `TestOutputOrganizer` enables your unit-test class to resolve the path of a directory under the project directory where your test class can output arbitrary files. You can ask `TestOutputOrganizer` to create a directory with name of *Fully Qualified Class Name* of your unit-test class and Method name. The `TestOutputOrganizer` provides helper methods to resolve the output directory, create it, and clean it recursively and recreate it.

## Download the jar from

- https://mvnrepository.com/artifact/com.kazurayam/unittest-helper

## How to use the jar in a Gradle build

You want to write the `build.gradle` file as follows:

```
repositories {
    mavenCentral()
}

dependencies {
    testImplementation("com.kazurayam:unittest-helper:0.4.0")
}
```

## Long explanation

- [English](https://kazurayam.github.io/unittest-helper/)

## Javadoc

- [Javadoc](https://kazurayam.github.io/unittest-helper/api/)

