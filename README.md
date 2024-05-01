# unittest-helper

## What this project provides

This project provides 2 utility Java classes

1. ['com.kazurayam.unittest.ProjectDirectoryResolver'](https://github.com/kazurayam/unittest-helper/blob/develop/lib/src/main/java/com/kazurayam/unittest/ProjectDirectoryResolver.java) : It enables your unit-test class to find the location of "Project Directory" based on the classpath, rather than depending on the value of `System.property("user.dir")"` runtime.

2. [`com.kazurayam.unittest.TestOutputOrganizer`](https://github.com/kazurayam/unittest-helper/blob/develop/lib/src/main/java/com/kazurayam/unittest/TestOutuputOrganizer.java) : It resolves the location of "Project Directory" by calling the "ProjectDirectoryResolver" class. Based on it, the `TestOutputOrganizer` enables your unit-test class to resolve the path of a directory where your test class can write arbitrary files. The `TestOutputOrganizer` can decide the path based on the Fully-Qualified-Class-Name of your unit-test class and Method name. The `TestOutputOrganizer` provides helper methods to resolve the output directory, create it, and clean it recursively and recreate it.

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

- [English](https://kazurayam.github.io/unittest-helper/)

## Javadoc

- [Javadoc](https://kazurayam.github.io/unittest-helper/api/)

