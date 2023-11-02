package io.github.someone.somestuff;

import com.kazurayam.unittest.TestHelper;

import java.nio.file.Paths;

/**
 * A Factory class that creates an instance of com.kazurayam.unittest.TestHelper
 * initialized with custom values of "outputDirPath" and "subDirPath"
 */
public class TestHelperFactory {

    public static TestHelper create(Class clazz) {
        return new TestHelper.Builder(clazz)
                .outputDirPath(Paths.get("build/tmp/testOutput"))
                .subDirPath(Paths.get(clazz.getName()))
                    // e.g, "io.github.somebody.somestuff.SampleTest"
                .build();
    }
}
