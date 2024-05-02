package study;

import org.testng.annotations.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

public class S2WritingSystemPropertyValueIntoFileInTheOutputDirectoryTest {

    @Test
    public void testIt() throws IOException {
        // receive a System property
        Optional<String> text =
                Optional.ofNullable(System.getProperty("browserType"));
        System.out.println("System.property(\"browserType\")=" + text.orElse("null"));

        // write the "browserType" value into a file
        // in the "test-output" directory under the "user.dir"
        Path currentWorkingDirectory = Paths.get(System.getProperty("user.dir"));

        Path outputDir = currentWorkingDirectory.resolve("test-output");
        Path file = outputDir.resolve("browserType.txt");
        System.out.println("file path=" + TestUtils.shortenPath(file));
        if (text.isPresent()) {
            Files.createDirectories(file.getParent());
            Files.write(file, text.get().getBytes());
            System.out.println("file content=" +
                    String.join("", Files.readAllLines(file)));
        }
    }
}
