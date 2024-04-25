package io.github.someone.examples;

import com.kazurayam.unittest.TestOutputOrganizer;

/**
 * A Factory class that creates an instance of com.kazurayam.unittest.TestHelper
 * initialized with custom values of "outputDirPath" and "subDirPath"
 */
public class TestOutputOrganizerFactory {

    public static TestOutputOrganizer create(Class<?> clazz) {
        return new TestOutputOrganizer.Builder(clazz)
                .outputDirectoryPathRelativeToProject("build/tmp/testOutput")
                .subPathUnderOutputDirectory(clazz.getName())
                    // e.g, "io.github.somebody.somestuff.SampleTest"
                .build();
    }
}
