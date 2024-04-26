# unittest-helper

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

- [English](https://kazurayam.github.io/unittest-helper/)
- 
## Javadoc

- [Javadoc](https://kazurayam.github.io/unittest-helper/api/)

