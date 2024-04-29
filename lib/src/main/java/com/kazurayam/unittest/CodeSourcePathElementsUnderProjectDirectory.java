package com.kazurayam.unittest;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * <PRE>
 * import java.net.URL;
 * import java.security.CodeSource;
 * import java.security.ProjectionDomain;
 * import org.testng.annotation.Test;
 *
 * public class S3FindingCodeSource() {
 *     @Test
 *     public void getLocationWhereTisClassIsFound() {
 *         ProtectionDomain pd = this.getClass().getProtectionDomain();
 *         CodeSource cs = pd.getCodeSource();
 *         URL url = cs.getLocation();
 *         System.out.println("codeSource URL=" + url.toString());
 *     }
 * }
 * </PRE>
 * This will print something like this:
 * <PRE>
 * codeSource URL=file:/Users/kazurayam/github/unittest-helper/preliminary-study/build/classes/java/test/
 * </PRE>
 *
 * I call "file:/Users/kazurayam/github/unittest-helper/preliminary-study/" as the "Project Directory",
 * "build/classes/java/test/" as the "CodeSource Path Elements Under Project Directory".
 *
 * This class gives a categorical name to the part "build/classes/java/test/" here.
 *
 */
public final class CodeSourcePathElementsUnderProjectDirectory {

    private List<String> cspe = new ArrayList<>();

    public CodeSourcePathElementsUnderProjectDirectory(List<String> cspe) {
        this.cspe = cspe;
    }

    public CodeSourcePathElementsUnderProjectDirectory(String pathElement, String... more) {
        this.cspe = new ArrayList<>();
        Path p = Paths.get(pathElement);
        if (p.getNameCount() > 0) {
            for (int i = 0; i < p.getNameCount(); i++) {
                this.cspe.add(p.getName(i).toString());
            }
        }
        if (more.length > 0) {
            this.cspe.addAll(Arrays.asList(more));
        }
    }

    /**
     * copy constructor
     */
    public CodeSourcePathElementsUnderProjectDirectory(CodeSourcePathElementsUnderProjectDirectory source) {
        this(source.toString());
    }

    public List<String> asList() {
        return new ArrayList<>(this.cspe);
    }

    public boolean isEmpty() {
        return this.cspe.isEmpty();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (String s : cspe) {
            sb.append(s);
            sb.append(File.separator);
        }
        return sb.toString();
    }
}
