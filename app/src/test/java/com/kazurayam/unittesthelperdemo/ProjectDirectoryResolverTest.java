package com.kazurayam.unittesthelperdemo;

import com.kazurayam.unittest.ProjectDirectoryResolver;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public final class ProjectDirectoryResolverTest {

    @Test
    public void test_getSublistPatterns() {
        List<List<String>> sublistPatterns =
                new ProjectDirectoryResolver().getSublistPatterns();
        assertThat(sublistPatterns).isNotNull();
        assertThat(sublistPatterns.size()).isGreaterThanOrEqualTo(2);
        for (List<String> p : sublistPatterns) {
            System.out.println("sublistPattern : " + p);
        }
    }

}
