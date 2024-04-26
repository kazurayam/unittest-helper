package demo;

import org.testng.annotations.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

public class OnProjectDirectoryTest {

    @Test
    public void test_write_text() throws IOException {
        Path dir = Paths.get("test-output");
        Path file = dir.resolve("browserType.txt");
        System.out.println("file=" + shortenPath(file));
        //
        Optional<String> text = Optional.ofNullable(System.getProperty("browserType"));
        System.out.println("browserType=" + text.orElse("null"));
        if (text.isPresent()) {
            Files.createDirectories(file.getParent());
            Files.write(file, text.get().getBytes());
            System.out.println("file:" + Files.readAllLines(file));
        }
    }

    private String shortenPath(Path p) {
        String userDir = Paths.get(System.getProperty("user.home")).toString();
        return p.toAbsolutePath().toString().replace(userDir, "~");
    }
}