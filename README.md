# unittest-helper

Using the `com.kazurayam.unittest.TestOutputOrganizer` class, you can easily prepare a directory into which your unit-test can write files. The helper class works with any unit-testing frameworks: JUnit4, JUnit5 and TestNG.

## Short explanation

You want to write the build.gradle file as follows:

```
repositories {
    mavenCentral()
}

dependencies {
    testImplementation("com.kazurayam:unittest-helper:0.2.2")
}
```

### Test code examples

Here I assume you have a Gradle project with a JUnit5 test class. The test wants to write a file into a local directory. You have a few options into which directory the test to write a file:

1. immediately under the project directory
2. under a directory `<projectDir>/test-output`. This is the default location which you can use with minimum effort.
3. under a custom directory: `<projectDir>/build/tmp/testOutput`

#### Ex1: Write a file immediately under the project dir

Let me start with a problematic code:

```
package my;

import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class SampleTest {

    @Test
    public void test_write_under_the_project_dir() throws Exception {
        Path p = Paths.get("sample1.txt");
        Files.writeString(p, "Hello, world!");
    }

```

This will create a file at `<projectDir>/sample1.txt`

In this case we used `Paths.get("sample1.txt")`. This expression will locate the file `sample1.txt` in the directory which `System.getProperty("user.dir")` expression stands for.

You should note that the value of the system property `user.dir` is dependent on the runtime environment. It is variable by the config of IDE and build tools' settings. Though rarely, the `user.dir` is not very much reliable. See [this isseue](https://github.com/kazurayam/selenium-webdriver-java/issues/21) for example. 

#### Ex2: Write a file under the default test-output directory

```
package my:

import com.kazurayam.unittest.TestOutputOrganizer;

import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class SampleTest {

    @Test
    public void test_write_into_the_default_dir() throws Exception {
        TestOutputOrganizer too = new TestOutputOrganizer.Builder(this.getClass()).build(); 
        Path p = too.resolveOutput("sample2.txt");
        Files.writeString(p, "Hello, world!");
    }
}
```

This will create a file at `<projectDir>/test-output/sample2.txt`

The `<projectDir>/test-output` directory will be silently created if it is not yet there.

#### Ex3: Write a file under a custom directory

```
package my:

import com.kazurayam.unittest.TestOutputOrganizer;

import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class SampleTest {

    @Test
    public void test_write_into_the_default_dir() throws Exception {
        TestOutputOrganizer too = new TestOutputOrganizer.Builder(this.getClass())
            .outputDirPath("build/tmp/testOutput")
            .build(); 
        Path p = too.resolveOutput("sample3.txt");
        Files.writeString(p, "Hello, world!");
    }
}
```

This will create a file at `<projectDir>/build/tmp/testOutput/sample3.txt`

The `<projectDir>/build/tmp/testOutput` directory will be silently created if it is not yet there.

#### Ex4: Insert a subdirectory which has the Fully Qualified Class Name of the test class

```
package my:

import com.kazurayam.unittest.TestOutputOrganizer;

import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class SampleTest {

    @Test
    public void test_write_into_the_default_dir() throws Exception {
        TestOutputOrganizer too = new TestOutputOrganizer.Builder(this.getClass())
            .subdirPath(this.getClass.getName())
            .build(); 
        Path p = too.resolveOutput("sample4.txt");
        Files.writeString(p, "Hello, world!");
    }
}
```

This will create a file at `<projectDir>/testOutput/my.SampleTest/sample4.txt`

By this path structure, you can easily see that the `sample4.txt` file was written by the `my.SamplTest` class.


#### TestOutputOrganizer resolves the project directory via classpath

Here I wrote "TestOutputOrganizer resolves a path via classpath"? What do I mean here?

If you use Gradle to build the project, then most probably you have the class file under the `build/classes/java/test/` directory with sub-path `my/SampleTest.class`. This case, the parent directory of the `build` is presumed to be the project directory. 

If you use Maven to build the project, then most probably you have the class file under the `target/test-classes/` directory with sub-path `my/SampleTest.class`. This case, the parent directory of the `target` is presumed to be the project directory.

Are you using other build tools so that the project file tree is different from Maven & Gradle? --- This case you can tell your own tree structure to the `TestOutputOrganizer` instance. See the long explanation for detail.

Resolving the project directory via classpath means that the `TestOutputOrgainzer` class works independent on the current working directory (= `System.getProperty('user.dir')`) of the runtime process.


## Long explanation

- [English](https://kazurayam.github.io/unittest-helper/)

## Javadoc

- [Javadoc](https://kazurayam.github.io/unittest-helper/api/)