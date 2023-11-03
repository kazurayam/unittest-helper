package io.github.someone.somestuff;

import com.kazurayam.unittest.TestOutputOrganizer;

import java.nio.file.Paths;

/**
 * A Factory class that creates an instance of com.kazurayam.unittest.TestHelper
 * initialized with custom values of "outputDirPath" and "subDirPath"
 */
public class TestOutputOrganizerFactory {

    public static TestOutputOrganizer create(Class clazz) {
        return new TestOutputOrganizer.Builder(clazz)
                .outputDirPath(Paths.get("build/tmp/testOutput"))
                .subDirPath(Paths.get(clazz.getName()))
                    // e.g, "io.github.somebody.somestuff.SampleTest"
                .build();
    }
}
