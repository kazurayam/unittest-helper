package com.kazurayam.unittest;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

public class CodeSourcePathElementsUnderProjectDirectoryTest {

    @Test
    public void testConstructor_single_string() {
        CodeSourcePathElementsUnderProjectDirectory cspe =
                new CodeSourcePathElementsUnderProjectDirectory("build/classes/java/test");
        assertThat(cspe.toString()).isEqualTo("build/classes/java/test/");
    }

    @Test
    public void testConstructor_multiple_strings() {
        CodeSourcePathElementsUnderProjectDirectory cspe =
                new CodeSourcePathElementsUnderProjectDirectory(
                        "build", "classes", "java", "test");
        assertThat(cspe.toString()).isEqualTo("build/classes/java/test/");
    }

    @Test
    public void testConstructor_ListOfStrings() {
        CodeSourcePathElementsUnderProjectDirectory cspe =
                new CodeSourcePathElementsUnderProjectDirectory(
                        Arrays.asList("build", "classes", "java", "test"));
        assertThat(cspe.toString()).isEqualTo("build/classes/java/test/");
    }
}
