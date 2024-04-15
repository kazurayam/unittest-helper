# unittest-helper

When testing, we often need access to a temporary file. However, managing, creating and deleting these files ourselves can be cumbersome. JUnit5 provides [TempDirectory Extension](https://www.baeldung.com/junit-5-temporary-directory) to create and clean up a temporary directory for an individual test or all tests in a test class. When the TempDirectory extension is used, the temporary files under the `@TempDir` will be automatically removed once the tests are done. But quite often I would rather like to see the contents of the temporary files. I need an alternative approach. I want to keep the temporary files when my tests are done. So I have developed `com.kazurayam.unittest.TestOutputOrganizer` class. This class helps

1. resolving the path of "project directory" based on the classpath of the test class rather than naively assuming that the project directory is equal to the value of `System.getProperty("user.home")` which is highly dependent on the runtime environment.
2. creating a "test-output" directory where tests can write temporary files.
3. cleaning and recreating the test-output directory
4. creating a directory for each individual test classes under the test-output directory.
5. creating a directory for each individual methods of a test class under the test-output directory.

The `TestOutputOrganizer` class was tested with Gradle. I haven't tested it enough with Maven. I have tested it with JUnit5. Hopefully it will work with JUnit4 and TestNG. I have tested it mainly on Mac, and tested it on Windows as well.

## Download from

- https://mvnrepository.com/artifact/com.kazurayam/unittest-helper

## Short explanation

You want to write the build.gradle file as follows:

```
repositories {
    mavenCentral()
}

dependencies {
    testImplementation("com.kazurayam:unittest-helper:0.3.3")
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

You should note that the value of the system property `user.dir` is dependent on the runtime environment. It is variable by the config of IDE and build tools' settings. Though rarely, the `user.dir` is not very much reliable. See [this issue](https://github.com/kazurayam/selenium-webdriver-java/issues/21) for example. 

TestOutputOrganizer provides an alternative way.

```
package my;

import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class SampleTest {

    private static final TestOutputOrganizer too = 
        new TestOutputOrganizer.Builder(SampleTest.class).build();


    @Test
    public void test_write_under_the_project_dir() throws Exception {
        Path projectDir = too.getProjectDir();
        Path p = projectDir.resolve("sample1.txt");
        Files.writeString(p, "Hello, world!");
    }
```

The `getProject()` method of `TestOutputOrganizer` class will return an instance of `java.nio.Path` which is the project's root directory. The `getProject` method is independent of the *current working directory*. How does the method resolves the project's directory? --- I will explain it later.

#### Ex2: Write a file under the default test-output directory

```
package my:

import com.kazurayam.unittest.TestOutputOrganizer;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class SampleTest {

    private static final TestOutputOrganizer too = new TestOutputOrganizer.Builder(SampleTest.class).build();

    @BeforeAll
    public static void beforeAll() {
        too.cleanOutputDirectory();
    }
    
    @Test
    public void test_write_into_the_default_dir() throws Exception {
        Path p = too.getOutputDirectory().resolve("sample2.txt");
        Files.writeString(p, "Hello, world!");
    }
}
```

This will create a file at `<projectDir>/test-output/sample2.txt`

If the `<projectDir>/test-output` directory is not there, it will be silently created.

If the `<projectDir>/test-output` directory is already there, the call to `cleanOutputDirectory()` will remove the directory recursively and recreate it.

If the `<projectDir>/test-output` directory is already there and if you do not call `too.cleanOutputDirectory()`, then the directory will be left as is and just reused.

#### Ex3: Write a file under a directory dedicated for the test class

```
package my:

import com.kazurayam.unittest.TestOutputOrganizer;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class SampleTest {

    private static final TestOutputOrganizer too = 
        new TestOutputOrganizer.Builder(SampleTest.class)
            .subDirPath(SampleTest.class).build();

    @BeforeAll
    public static void beforeAll() {
        too.cleanClassOutputDirectory();
    }
    
    @Test
    public void test_write_into_the_custom_dir() throws Exception {
        Path p = too.getClassOutputDirectory().resolve("sample3.txt");
        Files.writeString(p, "Hello, world!");
    }
}
```

This will create a file at `<projectDir>/test-output/my.SampleTest/sample3.txt`

If the `<projectDir>/test-output/my.SampleTest` directory is not yet there, it will be silently created.

If the `<projectDir>/test-output/my.SampleTest` directory is already there, the call to `cleanClassOutputDirectory()` will remove the directory recursively and recreate it.

If the `<projectDir>/test-output/my.SampleTest` directory is already there and if you do not call `cleanClassOutputDirectory()`, then the directory will stay as is and will be reused.

#### Ex4: Insert a subdirectory which has the Fully Qualified Class Name of the test class

```
package my:

import com.kazurayam.unittest.TestOutputOrganizer;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class SampleTest {

    private static final TestOutputOrganizer too = 
        new TestOutputOrganizer.Builder(SampleTest.class)
            .subDirPath(SampleTest.class).build();

    @BeforeAll
    public static void beforeAll() {
        too.cleanClassOutputDirectory();
    }
    
    @Test
    public void test_write_into_the_classOutputDirectory() throws Exception {
        Path p = too.getClassOutputDirectory().resolve("sample4.txt");
        Files.writeString(p, "Hello, world!");
    }
}
```

This will create a file at `<projectDir>/test-output/my.SampleTest/sample4.txt`

This path structure clearly tells you that the `sample4.txt` file was written by the `my.SampleTest` class.

If the `<projectDir>/test-output/my.SampleTest` directory is not there, it will be silently created.

If the `<projectDir>/test-output/my.SampleTest` directory is already there, the call to `cleanClassOutputDirectory()` will remove the directory recursively and recreate lit.

#### Ex:5 Insert a subdirectory which has the test method name

```
package my:

import com.kazurayam.unittest.TestOutputOrganizer;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class SampleTest {

    private static final TestOutputOrganizer too = 
        new TestOutputOrganizer.Builder(SampleTest.class)
            .subDirPath(SampleTest.class).build();

    @BeforeAll
    public static void beforeAll() {
        too.cleanClassOutputDirectory();
    }
    
    @Test
    public void test_write_into_the_methodOutputDirectory() throws Exception {
        String methodName = "test_write_into_the_methodOutputDirectory";
        too.cleanMethodOutputDirectory(methodName);
        Path p = too.getMethodOutputDirectory(methodName).resolve("sample5.txt");
        Files.writeString(p, "Hello, world!");
    }
}
```

This will create a file at `<projectDir>/test-output/my.SampleTest/test_write_into_the_methodOutputDirectory/sample5.txt`

This path structure clearly tells you that the `sample5.txt` file was written by the `test_write_into_the_methodOutputDirectory` method of the `my.SampleTest` class.

If the "method" directory is not there, it will be silently created.

If the "method" directory is already there, it will be just reused.

#### TestOutputOrganizer resolves the project directory via classpath

Here I wrote "TestOutputOrganizer resolves a path via classpath"? What do I mean?

If you use Gradle to build the project, then most probably you have the class file under the `build/classes/java/test/` directory with sub-path `my/SampleTest.class`. This case, the parent directory of the `build` is presumed to be the project directory. 

If you use Maven to build the project, then most probably you have the class file under the `target/test-classes/` directory with sub-path `my/SampleTest.class`. This case, the parent directory of the `target` is presumed to be the project directory.

Are you using other build tools so that the project file tree is different from Maven & Gradle? --- This case you can tell your own tree structure to the `TestOutputOrganizer` instance. See the long explanation for detail.

Resolving the project directory via classpath means that the `TestOutputOrgainzer` class works independent of the current working directory (= `System.getProperty('user.dir')`) of the runtime process.


## Long explanation

- [English](https://kazurayam.github.io/unittest-helper/)

## Javadoc

- [Javadoc](https://kazurayam.github.io/unittest-helper/api/)
