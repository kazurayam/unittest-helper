# unittest-helper

## What this project provides

This project publishes 2 utility classes in Java:

1. ['com.kazurayam.unittest.ProjectDirectoryResolver'](https://github.com/kazurayam/unittest-helper/blob/develop/lib/src/main/java/com/kazurayam/unittest/ProjectDirectoryResolver.java) : This class enables your unit-test class to find the location of "project directory" based on the classpath for the test class rather than depending on the value of `System.property("user.dir")"`. This is useful because the System Property `user.dir` sometimes moves, therefore is unreliable especially in a Gradle Multi-project build.

2. [`com.kazurayam.unittest.TestOutputOrganizer`](https://github.com/kazurayam/unittest-helper/blob/develop/lib/src/main/java/com/kazurayam/unittest/TestOutuputOrganizer.java) : This class finds the location of project directory by calling `com.kazurayam.unittest.ProjectDirectoryResolver` class. Under the project directory, the `TestOutputOrganizer` enables your unit-test class to create a directory where your test classes can output arbitrary files. You can ask `TestOutputOrganizer` to create a directory with name of *Fully Qualified Class Name* of your unit-test class. Also you can ask it to create a director with name of individual test Method name. The `TestOutputOrganizer` enables your unit-test class to clean the output directory recursively and recreate it.

## How to use

You can download the jar from the Maven Central repository:

- https://mvnrepository.com/artifact/com.kazurayam/unittest-helper

Of course, your `build.gradle` file can use it as a dependency, as follows:

```
repositories {
    mavenCentral()
}

dependencies {
    testImplementation("com.kazurayam:unittest-helper:0.4.0")
}
```

## Long explanation

- [English](https://kazurayam.github.io/unittest-helper)

## Javadoc

- [Javadoc](https://kazurayam.github.io/unittest-helper/api/)

