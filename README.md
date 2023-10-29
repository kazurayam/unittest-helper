# unittest-helper

Using the `com.kazurayam.unittest.TestHelper` class, you can easily prepare a directory where your unit-test can write files. The class is independent on the unit-test frameworks. You can use this helper class with JUnit4, JUnit5 and TestNG.

## Short explanation

You want to write the build.gradle file as follows:

```
repositories {
    mavenCentral()
}

dependencies {
    testImplementation("com.kazurayam:unittest-helper:0.1.0")
}
```

### Test code examples

Here I assume you have a Gradle project with a JUnit5 test class which is to write an output file into the local directory. You want to choose the output directory amongst the following 3:

1. immediately under the `<projectDir>`
2. under the default directory: `<projectDir>/test-output`
3. under a custom directory: `<projectDir>/build/tmp/testOutput`


#### Ex1: Write a file immediately under the project dir

```
package my;

import com.kazurayam.unittest.TestHelper;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class SampleTest {

    @Test
    public void test_write_under_the_project_dir() throws Exception {
        Path p = Paths.get("sample1.txt");
        Files.write(p, "Hello, world!".getBytes(StandardCharsets.UTF_8));
    }

```

This will create a file at `<projectDir>/sample1.txt`

In this case we used `Paths.get("sample1.txt")`. This expression will locate the directory which `System.getProperty("user.dir")` expression stands for.

However, you should note that the value of the system property `user.dir` is dependent on the runtime environment. It is variable by the config of IDE and build tools so that `user.dir` is not reliable sometimes.

#### Ex2: Write a file under the default test-output directory

```
    @Test
    public void test_write_into_the_default_dir() throws Exception {
        Path p = new TestHelper(SampleTest.class)
                .resolveOutput("sample2.txt");
        Files.write(p, "Hello, world!".getBytes(StandardCharsets.UTF_8));
    }
```

This will create a file at `<projectDir>/test-output/sample2.txt`

The `<projectDir>/test-output` directory will be silently created if not there.

The `com.kazurayam.unittest.TestHelper` class resolves the project directory via classpath. If you use Gradle to build the project, then most probably you have the class file at `<projectDir>/build/classes/java/test/my/SampleTest.class`. This case the project directory is presumed as the parent of the `build` directory. If you use Maven to build the project, then most probably you have the class file at `<projectDir>/target/test-classes/my/SampleTest.class`. This case the project directory is presumed as the parent of the `target` directory.

#### Ex3: Write a file under a custom directory

```
    @Test
    public void test_write_into_custom_dir() throws Exception {
        Path p = new TestHelper(SampleTest.class)
                .setOutputDirPath(Paths.get("build/tmp/testOutput"))
                .resolveOutput("sample3.txt");
        Files.write(p, "Hello, world!".getBytes(StandardCharsets.UTF_8));
    }
```

This will create a file at `<projectDir>/build/tmp/testOutput/sample3.txt`.

The `<projectDir>/build/tmp/testOutput` directory will be silently created if not there.


## Long explanation

- [English](https://kazurayam.github.io/unittest-helper/index.md)
- [日本語](https://kazurayam.github.io/unittest-helper/index_ja.md)
