package study;

import java.nio.file.Path;
import java.nio.file.Paths;

public class TestUtils {

    protected static String shortenPath(Path path) {
        Path p = path.toAbsolutePath().normalize();
        return shortenPath(p.toString());
    }
    protected static String shortenPath(String pathStr) {
        String userDir = Paths.get(System.getProperty("user.home")).toString();
        if (pathStr.startsWith(userDir)) {
            return pathStr.replace(userDir, "~");
        } else {
            return pathStr;
        }
    }
}
