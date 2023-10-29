# unittest-helper

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
2. under the `<projectDir>/test-output` directory
3. under the `<projectDir>/build/tmp/testOutput` directory

Using the `com.kazurayam.unittest.TestHelper` class, you can prepare the output location easily. You can use this helper class with JUnit5 and TestNG as well.

#### Ex1 write a file immediately under the project dir

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

#### Ex2 write a file under the default test-output directory

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

#### Ex3 write a file under a custom directory

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
