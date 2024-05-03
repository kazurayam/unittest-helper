package study;

import org.testng.annotations.Test;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class S1FindingProjectDirByCWDTest {

    @Test
    public void resolvingDirBasedOnCWD() throws IOException {
        Path currentWorkingDirectory = Paths.get(System.getProperty("user.dir"));
        Path dir = currentWorkingDirectory.resolve("test-output");
        System.out.println("dir = " + TestUtils.shortenPath(dir));
    }

}