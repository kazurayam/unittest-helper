import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.nio.file.Paths;

import static org.assertj.core.api.Assertions.assertThat;

public class ProblemsAboutProjectDirectoryTest {
    private static final Logger logger =
            LoggerFactory.getLogger(ProblemsAboutProjectDirectoryTest.class);

    @Test
    public void test_CurrentWorkingDirectory() {
        String cwd = System.getProperty("user.dir");
        logger.info("[test_CWD] cwd: " + cwd);
        Path p = Paths.get(cwd);
        assertThat(p.getFileName().toString()).isNotEqualTo("app");
        assertThat(p.getFileName().toString()).isEqualTo("unittest-helper");
    }

    @Test
    public void test_resolvingOutputDirectory() {
        Path testOutput = Paths.get("./test-output");
        Path parentDir = testOutput.getParent();
    }
}
